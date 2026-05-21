# Namaz Vakitleri - Android & Wear OS Uygulaması

Sen uzman bir Android ve Wear OS uygulama geliştiricisisin. Bu proje konum bazlı çalışan, hem mobil hem de Wear OS desteği olan bir "Namaz Vakitleri" uygulamasıdır. Kodları yazarken Clean Architecture ve MVVM tasarım desenlerini kullan.

## Projenin Mevcut Durumu

Uygulama build alıp başarıyla derlenmektedir. Temel özellikler tamamlanmıştır.

### Build & Run

```bash
# Projeyi derle (app + wear)
./gradlew assembleDebug

# Sadece mobil
./gradlew :app:assembleDebug

# Sadece wear
./gradlew :wear:assembleDebug

# Test
./gradlew test
```

### Konfigürasyon

```
Gradle:      9.4.1
AGP:         9.2.0
Kotlin:      2.2.10
KSP:         2.2.10-2.0.2
Compose BOM: 2026.02.01
compileSdk:  36
targetSdk:   36
app minSdk:  24
wear minSdk: 30
JDK:         11 (desugar enabled)
Java target: 11
```

### Dependencies (gradle/libs.versions.toml)

```
DI:          Hilt 2.59.2, hilt-navigation-compose 1.3.0, hilt-work 1.3.0
Network:     Retrofit 2.11.0, OkHttp 4.12.0, Gson 2.11.0
Coroutines:  1.10.1
Navigation:  2.9.8
Location:    play-services-location 21.3.0
DataStore:   1.1.1 (Preferences)
Room:        2.8.4
WorkManager: 2.11.2
Wear:        compose-material3 1.6.1, tiles-material 1.6.0, complications 1.3.0
Wearable:    play-services-wearable 20.0.1
Horologist:  0.7.15
Desugar:     2.1.5
```

---

## API'lar

### İmsakiyem API (Namaz Vakitleri) — Diyanet Resmi Verisi
```
GET https://ezanvakti.imsakiyem.com/api/prayer-times/{districtId}/monthly
GET https://ezanvakti.imsakiyem.com/api/locations/search/districts?q={query}&countryId=2
```
- Diyanet İşleri Başkanlığı'nın resmi namaz vakitleri verisini kullanır.
- District ID bazlıdır. Türkiye için Diyanet'in ilçe ID'leri kullanılır (örn. Silivri=9548, Fatih=9549).
- Aylık endpoint ilgili ayın tüm günlerini döndürür.
- Hicri tarih bilgisi (`hijri_date`) response içinde gelir.
- Rate limit: 100 istek / 5 dakika.
- **İlçe arama**: `/api/locations/search/districts?q={ilçe adı}` ile ilçe ID'si bulunur.

### Nominatim / OpenStreetMap (Şehir Arama) — Yedek
```
GET https://nominatim.openstreetmap.org/search?q={query}&format=json&addressdetails=1&limit=20&accept-language=tr
```
- CitySelectionScreen'te yedek olarak tutulur (Türkiye dışı aramalar için).
- `@Named("nominatim")` ile ayrı OkHttpClient + Retrofit instance (User-Agent header gerekli).

---

## Clean Architecture Katmanları

```
┌─────────────────────────────────────┐
│  UI Layer (Compose Screens)         │  <- ui/screens/, ui/components/
│  ViewModel + UiState                │  <- ui/state/HomeUiState.kt
├─────────────────────────────────────┤
│  Domain Layer                       │
│  UseCase → Repository Interface     │  <- domain/usecase/, domain/repository/
│  Domain Models                      │  <- domain/model/
├─────────────────────────────────────┤
│  Data Layer                         │
│  RepositoryImpl → API + Local       │  <- data/repository/
│  Remote (Retrofit) + DTO            │  <- data/remote/
│  Local (DataStore, Room)            │  <- data/local/
├─────────────────────────────────────┤
│  DI (Dagger Hilt)                   │  <- di/
│  Services (Location, Notification)  │  <- service/
└─────────────────────────────────────┘
```

---

## Proje Yapısı

### app/ (Mobil Modül) — `co.gorkem.namaz` — 51 Kotlin dosyası

```
app/
├── build.gradle.kts
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    │   Permissions: ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, POST_NOTIFICATIONS
    │   @HiltAndroidApp NamazApplication, @AndroidEntryPoint MainActivity
    ├── java/co/gorkem/namaz/
    │   ├── NamazApp.kt                    # Root composable, 3-tab bottom nav (Vakitler/Kıble/Ayarlar)
    │   ├── NamazApplication.kt            # @HiltAndroidApp, notification channel + worker schedule
    │   ├── MainActivity.kt                # @AndroidEntryPoint, edge-to-edge, LocaleManager.attachBaseContext
    │   ├── LocaleManager.kt               # i18n: SharedPreferences-based locale, activity recreate
    │   │
    │   ├── domain/
    │   │   ├── calculator/
    │   │   │   ├── SolarCalculations.kt      # Julian Day, solar declination, EoT, hour angle, UTC→local
    │   │   │   ├── PrayerTimeCalculator.kt   # @Singleton, Diyanet parametreleriyle 6 vakit hesaplama
    │   │   │   └── CalculationMethod.kt      # Fajr=-18°, Isha=-17°, Asr=1x shadow, offset'ler
    │   │   ├── model/
    │   │   │   ├── PrayerTime.kt          # data class: imsak, gunes, ogle, ikindi, aksam, yatsi
    │   │   │   ├── PrayerName.kt          # enum: IMSAK, GUNES, OGLE, IKINDI, AKSAM, YATSI (@StringRes)
    │   │   │   ├── LocationInfo.kt        # data class: lat, lon, city, district, timezone. DEFAULT=Istanbul
    │   │   │   └── QiblaDirection.kt      # data class: bearing (Double), cardinalDirection (TR: K/KD/D/GD/G/GB/B/KB)
    │   │   ├── repository/
    │   │   │   └── PrayerTimeRepository.kt # interface: getPrayerTimes() → Flow<NetworkResult<List<PrayerTime>>>
    │   │   └── usecase/
    │   │       ├── GetNextPrayerUseCase.kt    # next prayer + remaining minutes
    │   │       ├── GetPrayerTimesUseCase.kt   # repository delegasyonu
    │   │       ├── GetPrayerDashboardUseCase.kt # PeriodType + kerahat windows + current period logic
    │   │       └── QiblaUseCase.kt            # bearing calculation: lat/lon → Kaaba (21.4225, 39.8262)
    │   │
    │   ├── data/
    │   │   ├── remote/
    │   │   │   ├── NetworkResult.kt        # sealed: Success, Error, Loading
    │   │   │   ├── api/
    │   │   │   │   ├── ImsakiyemApiService.kt # Retrofit: GET monthly, search districts, states
    │   │   │   │   └── NominatimApiService.kt # Retrofit: GET search (OpenStreetMap geocoding)
    │   │   │   └── dto/
    │   │   │       ├── ImsakiyemResponse.kt  # BaseResponse, DayPrayerDto, DistrictDto, HijriDateDto
    │   │   │       └── NominatimResponse.kt   # NominatimPlace, NominatimAddress
    │   │   ├── repository/
    │   │   │   └── PrayerTimeRepositoryImpl.kt # @Singleton, API + Room cache, method+school-aware cache key
    │   │   └── local/
    │   │       ├── room/
    │   │       │   ├── NamazDatabase.kt        # Room v3, PrayerTimeEntity, fallbackToDestructiveMigration(true)
    │   │       │   ├── PrayerTimeDao.kt        # Queries by city+month+year+method+school, deleteOlderThan
    │   │       │   └── PrayerTimeEntity.kt     # date (PK), cityName, 6 prayer times, hijri fields, cacheSchool
    │   │       └── datastore/
    │   │           ├── UserPreferencesRepository.kt # DataStore: notifications, city, method, school, language, recentCities
    │   │           ├── UserPreferencesSerializer.kt # NO-OP stub (not actively used; standard Preferences keys used)
    │   │           └── UserPreferencesDataStore.kt  # preferencesDataStore extension
    │   │
    │   ├── di/
    │   │   ├── NetworkModule.kt       # @Module: OkHttp (logging, 30s), Retrofit, AladhanApiService
    │   │   ├── RepositoryModule.kt    # @Module: binds PrayerTimeRepository → Impl
    │   │   ├── LocationModule.kt      # @Module: FusedLocationProviderClient
    │   │   └── RoomModule.kt          # @Module: NamazDatabase + PrayerTimeDao singleton
    │   │
    │   ├── service/
    │   │   ├── LocationTracker.kt           # FusedLocationProviderClient, hasLocationPermission()
    │   │   ├── GeocoderHelper.kt            # Reverse geocode: lat/lon → il/ilçe (Turkish locale)
    │   │   ├── NotificationHelper.kt        # Notification channel + bildirim gösterme (POST_NOTIFICATIONS check)
    │   │   ├── PrayerNotificationWorker.kt  # @HiltWorker, tek vakit bildirimi
    │   │   ├── PrayerNotificationScheduler.kt # 6 vakit bildirimi zamanlaması (WorkManager OneTimeWorkRequest)
    │   │   ├── PrayerRefreshWorker.kt         # @HiltWorker, every 6h periodic refresh + notification reschedule
    │   │   ├── WearSyncManager.kt             # Data Layer API ile Wear'a JSON sync (/namaz_prayer_data)
    │   │   ├── WearPrayerData.kt              # WearPrayerData, WearPrayerItem, WearNextPrayer, WearKerahatInfo + Gson helpers
    │   │   └── QiblaSensorManager.kt          # SensorManager wrapper, magnetometer detection, Flow<QiblaOrientation>
    │   │
    │   └── ui/
    │   ├── state/
    │   │   ├── HomeUiState.kt  # sealed: Loading, Success, Error, PermissionRequired
    │   │   └── QiblaUiState.kt # sealed: Loading, CompassActive, StaticDirection, Error, LocationRequired
    │       ├── theme/
    │       │   ├── Theme.kt       # NamazTheme (dynamic color, prayer-tinted scheme)
    │       │   ├── Color.kt       # M3 expressive light/dark colors + PrayerTints
    │       │   ├── PrayerColors.kt # Atmospheric gradient brushes per prayer time
    │       │   ├── Shape.kt       # ExpressiveShapes, HeroCardShape, PillShape
    │       │   └── Type.kt        # ExpressiveTypography scale
    │       ├── components/
    │       │   ├── PrayerHeroCard.kt       # Hero card: countdown + location + period info + hijri date
    │       │   ├── PrayerTimesList.kt      # 6 prayer rows: next prayer badge, current prayer highlight
    │       │   ├── PrayerIcon.kt           # PrayerName → ImageVector + tint mapping
    │       │   ├── CountdownTimer.kt       # Standalone countdown composable
    │       │   ├── QiblaCompass.kt         # Canvas compass: ring, tick marks, rotating Kaaba icon on circumference, cardinal labels at edge
    │       │   └── QiblaDegreeDisplay.kt   # Bearing + cardinal direction + alignment delta display
    │       └── screens/
    │           ├── home/
    │           │   ├── HomeScreen.kt      # Dashboard: permission → hero card → kerahat banner → prayer list
    │           │   └── HomeViewModel.kt   # prayerTimesJob cancellation (race fix), prefs reactive reload
    │           ├── cityselection/
    │           │   ├── CitySelectionScreen.kt    # Search (Nominatim), recent cities, use my location
    │           │   └── CitySelectionViewModel.kt # Debounced search (400ms), save/clear preferences
    │           ├── settings/
    │           │   ├── SettingsScreen.kt    # Notifications, language (recreate), method, mezhep
    │           │   └── SettingsViewModel.kt # UserPreferences via DataStore
    │           └── qibla/
    │               ├── QiblaScreen.kt      # Functional compass: sensor-driven + static fallback, all UI states
    │               └── QiblaViewModel.kt   # Lifecycle-aware, Flow-based sensor + UseCase orchestration
    │
    └── res/
        ├── values/strings.xml, values-en/strings.xml, themes.xml, colors.xml
        ├── drawable/ic_my_location.xml, ic_launcher_*.xml
        ├── mipmap-*/ic_launcher.webp, ic_launcher_round.webp
        └── xml/backup_rules.xml, data_extraction_rules.xml
```

### wear/ (Wear OS Modül) — `co.gorkem.namaz.wear` — 19 Kotlin dosyası

```
wear/
├── build.gradle.kts
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    │   standalone=true, tile + complication service declarations
    │   Permissions: FINE_LOCATION, COARSE_LOCATION, INTERNET, WAKE_LOCK
    │   uses-feature: android.hardware.type.watch
    ├── java/co/gorkem/namaz/wear/
    │   ├── WearApp.kt              # Root composable (WearTheme + WearHomeScreen)
    │   ├── WearApplication.kt      # @HiltAndroidApp
    │   ├── MainActivity.kt         # @AndroidEntryPoint ComponentActivity
    │   ├── PrayerNameHelper.kt     # Context-based prayer name mapper (⚠️ duplicate with ui/PrayerNameHelper.kt)
    │   │
    │   ├── di/
    │   │   ├── WearNetworkModule.kt    # @Module: OkHttp, Retrofit, AladhanApiService
    │   │   └── WearDataEntryPoint.kt   # @EntryPoint: DataLayerService, PrayerRepository (for Tile/Complication)
    │   │
    │   ├── data/
    │   │   ├── WearDataLayerService.kt    # DataClient listener (/namaz_prayer_data)
    │   │   ├── WearPrayerRepository.kt    # Standalone Aladhan API call (phone yoksa)
    │   │   ├── WearPrayerModel.kt         # WearPrayerData, WearPrayerItem, WearNextPrayer, WearKerahatInfo
    │   │   └── remote/
    │   │       ├── api/AladhanApiService.kt      # Retrofit interface (method, school params)
    │   │       └── dto/PrayerTimeResponse.kt     # DTOs (simplified)
    │   │
    │   ├── ui/
    │   │   ├── WearViewModel.kt       # DataLayer listener + standalone API fallback (hardcoded Istanbul)
    │   │   ├── PrayerNameHelper.kt    # @Composable prayer name mapper (⚠️ duplicate with root PrayerNameHelper.kt)
    │   │   ├── screens/
    │   │   │   ── WearHomeScreen.kt  # ScalingLazyColumn: countdown hero, city, kerahat banner, prayer rows, source chip
    │   │   └── theme/
    │   │       ├── Theme.kt           # WearTheme (OLED-black color scheme)
    │   │       ├── Color.kt           # High-contrast wear palette (cyan/blue/amber on black)
    │   │       └── Type.kt            # WearTypography scale
    │   │
    │   ├── tile/
    │   │   └── PrayerTileService.kt    # TileService: next prayer name, time, remaining minutes
    │   │
    │   └── complication/
    │       └── PrayerComplicationService.kt  # ShortTextComplication: next prayer on watch face
    │
    └── res/
        ├── values/strings.xml, values-en/strings.xml
        ├── drawable/ic_launcher_*.xml
        ├── mipmap-*/ic_launcher.webp, ic_launcher_round.webp
        └── xml/backup_rules.xml, data_extraction_rules.xml
```

---

## Kullanıcı Akışı

1. **İlk açılış** → Konum izni istenir
   - İzin verilirse: FusedLocationProviderClient → Geocoder → İmsakiyem API (district ID lookup)
   - İzin verilmezse: CitySelectionScreen (imsakiyem arama), varsayılan İstanbul/Silivri
2. **Ana ekran**: Hero card (geri sayım + sıradaki vakit + periyot) + kerahat banner + 6 vakit listesi
3. **Ayarlar**: Bildirim toggle, dakika seçimi (15/30/45), veri kaynağı (API / Lokal Hesaplama), hesaplama yöntemi, mezhep (school), dil
4. **Şehir seçimi**: İmsakiyem API ile ilçe arama + son seçilenler (max 5, JSON olarak DataStore'da)
5. **Kıble**: Sensör tabanlı pusula (magnetometer + accelerometer), gerçek zamanlı iğne, Kıble yayı, ±5° hizalanma titreşimi. Manyetometre yoksa statik yön + derece gösterimi
6. **Wear senkronizasyonu**: Telefon Data Layer API ile wear'a vakit + konum + next prayer + kerahat bilgisi gönderir
7. **Wear standalone**: Telefon yoksa saat kendi konumundan API'ye sorar (hardcoded İstanbul koordinatları)

---

## Geliştirme Notları

### Konvansiyonlar
- Dilde Türkçe değişken isimleri kullanılmıştır (imsak, gunes, ogle, ikindi, aksam, yatsi).
- Code style: Yorum yok (AGENTS.md convention).

### Tema
- Material 3 dynamic color + namaz vakitlerine özel gradient/renk paleti (PrayerColors.kt).
- Expressive shapes, typography scale.

### Çevrimdışı (Room)
- `PrayerTimeEntity` cache'lenir. Cache key: `cityName + month + year + method + school`.
- `NamazDatabase` version = 3. `fallbackToDestructiveMigration(true)` kullanılır (schema export disabled).
- `cleanupOldData(30)` 30 günden eski veriyi siler.

### Bildirimler
- WorkManager `OneTimeWorkRequest` ile her vakit için ayrı worker.
- `PrayerRefreshWorker`: 6 saatte bir periyodik refresh (ağ kısıtı yok, lokal hesaplama çalışır).

### i18n
- `LocaleManager`: SharedPreferences tabanlı, `attachBaseContext` ile locale uygular.
- Dil değişikliği `activity.recreate()` ile yapılır (CompositionLocalProvider yaklaşımı çöktü).
- `values/strings.xml` (TR) ve `values-en/strings.xml` (EN) tam mirror.
- `PrayerName` enum'u `@StringRes` tutar, `stringResource()` ile çözülür.

### DataStore
- `UserPreferencesRepository`: standard Preferences DataStore keys kullanır.
- `UserPreferencesSerializer`: NO-OP stub. Aktif olarak kullanılmaz.
- `recentCities`: Gson ile JSON string olarak saklanır (max 5, deduplicated).
- `useLocalCalculation`: Boolean. Açıksa API kullanılmaz, lokal hesaplama yapılır.

### HomeViewModel Race Condition
- `prayerTimesJob` takip edilir, yeni yükleme önceki job'ı iptal eder.
- Yeni load başladığında `_prayerTimes`, `_error`, `_nextPrayer`, `_dashboardData` sıfırlanır.
- `init` block `preferencesRepository.preferences` dinler; city/method/school değişince reload eder.
- `useMyLocation()`: sadece `clearManualCity()` çağırır, init block reload'u tetikler.

### Wear
- Hem Data Layer (telefon bağlıyken) hem standalone API (telefon yokken) çalışır.
- **Complication**: `PrayerComplicationService`, ShortText tipinde.
- **Tile**: `PrayerTileService`, next prayer + remaining minutes.
- **Kerahat**: Telefon'dan Wear'a kerahat bilgisi sync edilir. `WearKerahatBanner` aktif/yaklaşıyor durumlarını gösterir.
- **⚠️ Duplicate**: `PrayerNameHelper.kt` hem root package'ta (`Context`-based) hem `ui/`'da (`@Composable`). Birini kaldırmak gerekir.
- **⚠️ Hardcoded fallback**: `WearViewModel.loadFromStandalone()` İstanbul/Silivri districtId=9548 kullanır. GPS konum alınmalı.

### Kerahat Vakitleri
- 3 periyot: Güneş→Güneş+45dk (namaz kılınmaz), Öğle-45dk→Öğle (namaz kılınmaz), Akşam-45dk→Akşam (sadece farz-ı ikindi kılınabilir)
- `GetPrayerDashboardUseCase` aktif ve yaklaşan kerahat'ı hesaplar
- Banner: sadece "Kerahat Vakti" yazar (vakit ismi gösterilmez). Aktif (errorContainer) veya yaklaşan ≤60dk (tertiaryContainer)
- Fallback path'te de kerahat banner gösterilir
- Wear OS'a kerahat bilgisi sync edilir (`WearKerahatInfo`)

### API Parametreleri
- İmsakiyem API Diyanet'in resmi verisini kullanır. `method` ve `school` parametreleri gerekmez.
- Vakitler doğrudan Diyanet verisidir, lokal hesaplamadan farklı olarak ofset vb. ayarlara ihtiyaç duymaz.

### Lokal Hesaplama
- `domain/calculator/` paketi: Güneş konumu hesaplamaları + Diyanet'e uygun parametrelerle namaz vakitleri.
- **Parametreler**: Fajr=-18°, Isha=-17°, Asr=1x shadow (Standard, Hanefi değil), offset'ler.
- **Hibrit mod**: `UserPreferencesRepository.useLocalCalculation` ile kontrol. Açık → hep lokal. Kapalı → internet varsa API, yoksa otomatik lokal.
- **Test**: `PrayerTimeCalculatorTest` — İstanbul Mayıs, Ocak, Aralık, Haziran, Ankara için Diyanet resmi saatleriyle karşılaştırma (tolerans ±10dk).
- `PrayerTimeRepositoryImpl`: `shouldUseLocalCalculation()` ile ağ durumuna göre veri kaynağı seçimi. API yoksa/hatalıysa otomatik lokal'e fallback.

### Kıble Pusulası
- **QiblaUseCase**: Bearing formülü ile lat/lon → Kaabe yönü (21.4225°N, 39.8262°E). İstanbul→~151.6°, NYC→~58.5°, Sydney→~277.5°.
- **QiblaSensorManager**: SensorManager wrapper. TYPE_ACCELEROMETER + TYPE_MAGNETIC_FIELD. Exponential smoothing (α=0.2). `hasMagnetometer()` kontrolü. Sensör doğruluğu (`onAccuracyChanged`) + manyetik alan şiddeti takibi. ±20–70 μT aralığı dışı → bozulma uyarısı.
- **QiblaViewModel**: `collectAsStateWithLifecycle()` ile Flow dinleme. `onCleared()` ile otomatik sensor temizliği. Sensör doğruluk/kalibrasyon/manyetik verileri UI state'e aktarır.
- **QiblaCompass**: Referans pusula tasarımı. İç yüz (derece çizgileri, K/D/G/B etiketleri) cihaz yönüne göre döner, dış yüz sabit kalır. Kaaba simgesi `qiblaBearing - deviceAzimuth` açısında halka çevresinde döner. Sabit üst gösterge (12 o'clock) yardımcı olur. Kullanıcı telefonu çevirerek Kaaba simgesini üst göstergeye hizalar. Hizalandığında Kıble yönündesiniz. Merkezde crosshair + seviye göstergesi: hareketli baloncuk nokta telefon eğimine göre kayar (roll sola/sağa, pitch yukarı/aşağı). Baloncuk crosshair merkezine geldiğinde telefon düzdür ve rengi değişir. Canvas-only implementasyon.
- **QiblaDegreeDisplay**: Bearing + kardinal yön + sapma. Sensör doğruluk seviyesi ve manyetik alan μT değeri kaldırıldı (minimalist).
- **QiblaScreen**: Tam ekran, TopAppBar yok. 5 UI state: Loading/CompassActive/StaticDirection/Error/LocationRequired. ±5° hizalanma titreşimi. Kalibrasyon/manyetik bozulma uyarıları banner yerine inline mikro-metin olarak gösterilir.
- **Hybrid yaklaşım**: Manyetometre varsa tam pusula, yoksa statik yön + derece gösterimi.
- **Test**: `QiblaUseCaseTest` - 6 birim testi passing (Istanbul, NYC, Sydney, Kaabe, kardinal yönler).

### Navigate
- `./gradlew :app:assembleDebug` (mobil), `./gradlew :wear:assembleDebug` (wear)
