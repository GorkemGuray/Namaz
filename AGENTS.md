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

### Android CLI & Android Skills

Proje geliştirme sürecinde Android CLI (`android`) ve Android Skills (`https://github.com/android/skills`) kullanılmalıdır.

#### 1. Android CLI (`android` komutu)
Android CLI, Android araç zinciri ile çalışmayı kolaylaştıran resmi bir terminal arayüzüdür. Klasik adb veya emulator komutları yerine Android CLI komutları tercih edilmelidir:
- **Uygulamayı Çalıştırma / Yükleme:** `android run --debug` (derlenmiş APK'ları cihazda veya emülatörde başlatır).
- **Proje Analizi:** `android describe` (proje yapısını ve çıktı hedeflerini analiz eder).
- **Arayüz Analizi ve UI Hatalarını Giderme:** `android layout --pretty` (uygulamanın mevcut ekran düzenini JSON formatında verir. UI hatalarını görsel olarak incelemekten daha hızlıdır).
- **Ekran Görüntüsü Alma:** `android screen capture` (cihaz ekranını PNG olarak kaydeder).
- **Dokümantasyon Arama:** `android docs search <terimler>` (Diyanet API, konum servisleri veya diğer Android kütüphaneleri ile ilgili resmi dokümantasyonu arar).
- **Emülatör Yönetimi:** 
  - Listeleme: `android emulator list`
  - Başlatma: `android emulator start <cihaz-adi>`
  - Durdurma: `android emulator stop <cihaz-adi>`
- **SDK ve Paket Yönetimi:** `android sdk list --all` veya `android sdk install <paket>`
- **Skill Yönetimi:** `android skills list` veya `android skills add --skill=<skill-adi>`

Klasik yedek / doğrudan araçlar (gerekirse):
- `adb devices` (cihazları listeleme)
- `adb logcat` (logları izleme)
- `./gradlew connectedAndroidTest` (cihaz testlerini çalıştırma)

#### 2. Android Skills
Resmi Android en iyi pratiklerini takip etmek için Android Skills entegrasyonu kullanılır. Bir geliştirme görevi yaparken (özellikle yeni kütüphane eklerken, tema güncellerken veya mimari değişikliklerde) ilgili skill yönergeleri okunmalı ve birebir uygulanmalıdır.

**Mevcut / Kullanılabilir Önemli Skill'ler:**
- **`jetpack-compose-m3`:** Wear OS Compose Material3 bileşenleri ve temalama kuralları.
- **`styles`:** Compose Styles API entegrasyonu ve bileşen temalandırması.
- **`edge-to-edge`:** Durum çubuğu ve navigasyon çubuğu taşmalarını düzeltme, pencere içi boşluklar (insets) yönetimi.
- **`navigation-3`:** Jetpack Navigation 3 mimarisine geçiş ve sayfa geçiş yönetimi.
- **`r8-analyzer`:** R8/Proguard optimizasyon kuralları ve küçültme ayarları.
- **`testing-setup`:** Unit test ve UI test stratejisi oluşturma.
- **`migrate-xml-views-to-jetpack-compose`:** Eski görünüm yapılarını Jetpack Compose'a dönüştürme.
- **`appfunctions`:** Cihaz içi kısa yollar ve AI ajan entegrasyonları için AppFunctions tanımlama.
- **`verified-email`:** Credential Manager ile güvenli ve kolay kullanıcı doğrulama.

**Skill Kullanım Kuralı:** 
Eğer görev yukarıdaki konulardan biriyle ilgiliyse (örneğin; Wear OS'te Material 3 veya ekran kenarlarına taşma sorunları), kod yazmaya başlamadan önce kesinlikle ilgili `SKILL.md` dosyası (`file:///Users/gorkem/.gemini/config/skills/<skill-adı>/SKILL.md` veya plugin altındaki) `view_file` ile okunmalı ve oradaki kurallara sadık kalınmalıdır.


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
Glance:       1.1.1 (Widget)
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

### Aladhan API — Dünya Geneli Namaz Vakitleri (Varsayılan)
```
GET https://api.aladhan.com/v1/calendar/{year}/{month}?latitude={lat}&longitude={lon}&method={method}&school={school}
```
- Dünya genelinde koordinat bazlı namaz vakti hesaplamaları için kullanılır.
- Uygulama içinde varsayılan veri kaynağıdır.
- Diyanet hesaplama yöntemi için `method=13` parametresi gönderilir.

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

### app/ (Mobil Modül) — `co.gorkem.namaz` — 63 Kotlin dosyası

```
app/
├── build.gradle.kts
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    │   Permissions: ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, POST_NOTIFICATIONS
    │   @HiltAndroidApp NamazApplication, @AndroidEntryPoint MainActivity
    │   Widget: APPWIDGET_UPDATE receiver + configure activity + WorkManager init provider
    ├── java/co/gorkem/namaz/
    │   ├── NamazApp.kt                    # Root composable, 4-tab bottom nav (Vakitler/Takvim/Kıble/Ayarlar) + AppThemeEntryPoint + rememberAppTheme()
    │   ├── NamazApplication.kt            # @HiltAndroidApp, Configuration.Provider (HiltWorkerFactory), notification channel + worker schedule
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
    │   │   │   └── PrayerTimeRepositoryImpl.kt # @Singleton, API + Room cache, method+school-aware cache key, widget updater
    │   │   └── local/
    │   │       ├── room/
    │   │       │   ├── NamazDatabase.kt        # Room v3, PrayerTimeEntity, fallbackToDestructiveMigration(true)
    │   │       │   ├── PrayerTimeDao.kt        # Queries by city+month+year+method+school, deleteOlderThan
    │   │       │   └── PrayerTimeEntity.kt     # date (PK), cityName, 6 prayer times, hijri fields, cacheSchool
    │   │       └── datastore/
    │   │           ├── UserPreferencesRepository.kt # DataStore: notifications, city, method, school, language, recentCities, appTheme → widget updater
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
    │   │   ├── PrayerNotificationWorker.kt  # @HiltWorker, tek vakit bildirimi + widget update
    │   │   ├── PrayerNotificationScheduler.kt # 6 vakit bildirimi zamanlaması (WorkManager OneTimeWorkRequest)
    │   │   ├── PrayerRefreshWorker.kt         # @HiltWorker, every 6h periodic refresh + notification reschedule
    │   │   ├── WearSyncManager.kt             # Data Layer API ile Wear'a JSON sync (/namaz_prayer_data)
    │   │   ├── WearPrayerData.kt              # WearPrayerData, WearPrayerItem, WearNextPrayer, WearKerahatInfo + Gson helpers
    │   │   ├── QiblaSensorManager.kt          # SensorManager wrapper, magnetometer detection, Flow<QiblaOrientation>
    │   │   ├── NamazWidget.kt                 # GlanceAppWidget: compact/expanded layout, themeToColorProviders sync, kerahat-aware, AndroidRemoteViews Chronometer
    │   │   ├── NamazWidgetReceiver.kt         # AppWidgetProvider: APPWIDGET_UPDATE receiver, minute tick start/stop
    │   │   ├── NamazWidgetConfigureActivity.kt # Widget layout seçimi (detailed/compact)
    │   │   ├── NamazWidgetPrefs.kt            # Widget-specific SharedPreferences (layout)
    │   │   ├── NamazWidgetUpdater.kt          # Widget güncelleme yardımcısı
    │   │   ├── NamazWidgetWorker.kt           # @HiltWorker, periyodik widget refresh (15dk fallback)
    │   │   └── WidgetMinuteReceiver.kt        # AlarmManager ile dakikalık tick BroadcastReceiver
    │   │
    │   └── ui/
    │   ├── state/
    │   │   ├── HomeUiState.kt  # sealed: Loading, Success, Error, PermissionRequired
    │   │   └── QiblaUiState.kt # sealed: Loading, CompassActive, StaticDirection, Error, LocationRequired
    │       ├── theme/
    │       │   ├── Theme.kt       # NamazTheme(currentTheme: AppThemeSelection), prayer-tinted scheme, bypasses system dynamic colors
    │       │   ├── Color.kt       # AppThemeSelection enum (EMERALD/SAPPHIRE/AMBER/MONOCHROME) + 4 tam M3 palet + Glance hex sabitleri + PrayerTints
    │       │   ├── PrayerColors.kt # Atmospheric gradient brushes per prayer time
    │       │   ├── Shape.kt       # ExpressiveShapes, HeroCardShape, PillShape
    │       │   └── Type.kt        # ExpressiveTypography scale
    │       ├── components/
    │       │   ├── PrayerHeroCard.kt       # Hero card: countdown + location + period info + hijri date
    │       │   ├── PrayerTimesList.kt      # 6 prayer rows: PrayerRowState enum (Passed/Active/Upcoming/Normal), period-aware
    │       │   ├── PrayerIcon.kt           # PrayerName → ImageVector + tint mapping
    │       │   ├── CountdownTimer.kt       # Standalone countdown composable
    │       │   ├── QiblaCompass.kt         # Canvas compass: ring, tick marks, rotating Kaaba icon on circumference, cardinal labels at edge
    │       │   └── QiblaDegreeDisplay.kt   # Bearing + cardinal direction + alignment delta display
    │       └── screens/
    │           ├── home/
    │           │   ├── HomeScreen.kt      # Dashboard: permission → hero card → kerahat banner → prayer list
    │           │   └── HomeViewModel.kt   # prayerTimesJob cancellation (race fix), prefs reactive reload
    │           ├── calendar/
    │           │   ├── CalendarScreen.kt  # Monthly calendar table view (M3, current day highlighted)
    │           │   └── CalendarViewModel.kt # Observes current preferences & fetches monthly prayer times
    │           ├── cityselection/
    │           │   ├── CitySelectionScreen.kt    # Search (Nominatim), recent cities, use my location
    │           │   └── CitySelectionViewModel.kt # Debounced search (400ms), save/clear preferences
    │           ├── settings/
    │           │   ├── SettingsScreen.kt    # Notifications, language (recreate), method, mezhep, tema (4 seçenek)
    │           │   └── SettingsViewModel.kt # UserPreferences via DataStore + appTheme setter
    │           └── qibla/
    │               ├── QiblaScreen.kt      # Functional compass: sensor-driven + static fallback, all UI states
    │               └── QiblaViewModel.kt   # Lifecycle-aware, Flow-based sensor + UseCase orchestration
    │
    └── res/
        ├── values/strings.xml, values-en/strings.xml, themes.xml, colors.xml
        ├── drawable/ic_prayer_*.xml, ic_widget_settings.xml, ic_my_location.xml, ic_launcher_*.xml
        ├── layout/widget_chronometer.xml
        ├── mipmap-*/ic_launcher.webp, ic_launcher_round.webp
        └── xml/backup_rules.xml, data_extraction_rules.xml, namaz_widget_info.xml
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
   - İzin verilirse: FusedLocationProviderClient → Geocoder → Konum Koordinatları → Aladhan API (Varsayılan) / Imsakiyem API (Diyanet) / Lokal Hesaplama
   - İzin verilmezse: CitySelectionScreen, varsayılan İstanbul/Silivri
2. **Ana ekran**: Hero card (geri sayım + sıradaki vakit + periyot) + kerahat banner + 6 vakit listesi (uzun konum isimleri sadeleştirilmiş ve taşma sorunları giderilmiştir)
3. **Aylık Takvim**: Sol/sağ oklar ile ay bazlı ileri/geri geçiş yapılabilen, 5 günlük vakit + güneş sütunlarını barındıran, bulunulan günü vurgulayan responsive tablo ekranı
4. **Ayarlar**: Bildirim toggle, dakika seçimi (15/30/45), veri kaynağı seçimi (Aladhan API / İmsakiyem API / Lokal Hesaplama), hesaplama yöntemi, mezhep (school), tema (4 seçenek: Emerald Oasis / Sapphire Night / Amber Sands / Monochrome Platinum), dil
5. **Şehir seçimi**: Seçilen veri kaynağına göre arama yapılır:
   - Aladhan API / Lokal Hesaplama için: Nominatim API ile dünya genelinde koordinat arama
   - İmsakiyem API için: İmsakiyem API ile ilçe arama (büyük/küçük harf ve Türkçe karakter desteğiyle)
   - Son seçilenler (max 5, JSON olarak DataStore'da)
6. **Kıble**: Sensör tabanlı pusula (magnetometer + accelerometer), gerçek zamanlı iğne, Kıble yayı, ±5° hizalanma titreşimi. Manyetometre yoksa statik yön + derece gösterimi. Ayrıca MapLibre entegrasyonu ile Kabe'ye giden çizgiyi haritada (sokak seviyesi detayında) görselleştiren harita desteği.
7. **Wear senkronizasyonu**: Telefon Data Layer API ile wear'a vakit + konum + next prayer + kerahat bilgisi gönderir
8. **Wear standalone**: Telefon yoksa saat standalone olarak Aladhan API ile koordinat bazlı çalışır (GPS konumu alınamadığında İstanbul koordinatlarına düşer)

---

## Geliştirme Notları

### Konvansiyonlar
- Dilde Türkçe değişken isimleri kullanılmıştır (imsak, gunes, ogle, ikindi, aksam, yatsi).
- Code style: Yorum yok (AGENTS.md convention).

### Tema
- 4 özel premium tema: Emerald Oasis (varsayılan), Sapphire Night, Amber Sands, Monochrome Platinum.
- Sistem dinamik renkleri (Monet) tamamen bypass edilir — `dynamicColor = false` zorunlu.
- `AppThemeSelection` enum'u DataStore'da `appTheme` (Int, 0-3) olarak saklanır.
- `NamazTheme(currentTheme: AppThemeSelection)` — `when` ile seçili temanın tam M3 `ColorScheme`'ini yükler.
- `rememberAppTheme()`: `NamazApp` seviyesinde DataStore'dan tema okur, `NamazTheme`'e geçirir.
- Widget senkronizasyonu: `themeToColorProviders()` ile `AppThemeSelection` → Glance `ColorProviders` birebir hex eşleme.
- `PrayerTints`: Her temada namaz vakitlerine özel yüzey tonlaması (surface/primaryContainer blend) korunur.
- Expressive shapes, typography scale (Theme.kt'te `NamazTheme` içinde `MaterialTheme`'e verilir).

### Çevrimdışı (Room)
- `PrayerTimeEntity` cache'lenir. Cache key: `cityName + month + year + method + school + cacheDataSource`.
- `NamazDatabase` version = 4. `fallbackToDestructiveMigration(true)` kullanılır (schema export disabled).
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
- `dataSource`: Int. Hangi veri kaynağının aktif olduğunu belirtir (0: Aladhan API, 1: Imsakiyem API, 2: Lokal Hesaplama).
- `useLocalCalculation`: Boolean. Geriye uyumluluk için tutulur. Açıksa API kullanılmaz, lokal hesaplama yapılır.
- `appTheme`: Int. Aktif temayı belirtir (0: Emerald Oasis, 1: Sapphire Night, 2: Amber Sands, 3: Monochrome Platinum). Her preference değişikliğinde widget otomatik güncellenir.

### HomeViewModel Race Condition
- `prayerTimesJob` takip edilir, yeni yükleme önceki job'ı iptal eder.
- Yeni load başladığında `_prayerTimes`, `_error`, `_nextPrayer`, `_dashboardData` sıfırlanır.
- `init` block `preferencesRepository.preferences` dinler; city/method/school değişince reload eder.
- `useMyLocation()`: sadece `clearManualCity()` çağırır, init block reload'u tetikler.

### Wear
- Hem Data Layer (telefon bağlıyken) hem standalone API (telefon yokken) çalışır.
- **Application ID Eşleşmesi**: Veri senkronizasyonu (Data Layer API) için mobil ve wear modüllerinin `applicationId` değeri `"co.gorkem.namaz"` olarak eşitlendi.
- **Senkronizasyon İyileştirmesi**: `WearDataLayerService` başlangıçta `dataItems` sorgulayarak telefondan gelen son verileri anında çeker. Böylece telefon bağlıyken standalone moda düşme sorunu giderildi. Standalone yüklemesi başlamadan önce 2 saniyelik DataLayer bekleme süresi eklendi ve standalone verisinin senkronize veriyi ezmesi engellendi.
- **Dil Desteği**: Telefondan (`HomeViewModel`) saate gönderilen veriler `LocaleManager` kullanılarak kullanıcının seçtiği dilde gönderilir.
- **Complication**: `PrayerComplicationService` ShortText formatındadır. Taşıma ve kırpılma ("AKŞAM 2...") sorununu önlemek için vakit adı `title` alanına, saati ise `text` alanına ayrıldı.
- **Complication Tetikleme**: Telefondan saate yeni veri ulaştığında `ComplicationDataSourceUpdateRequester` aracılığıyla komplikasyonun kadran üzerinde anında güncellenmesi sağlandı.
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
- **QiblaSensorManager**: SensorManager wrapper. TYPE_ACCELEROMETER + TYPE_MAGNETIC_FIELD. **Sin/Cos tabanlı azimuth smoothing** — açılar doğrudan smoothing yerine sin/cos komponentlerine ayrılıp smooth'lanır, böylece 360°→0° sarmalamasında (angle wrap) glitch oluşmaz. Smoothing faktörü: α=0.90 (daha yumuşak geçiş, daha az gürültü). `hasMagnetometer()` kontrolü. Sensör doğruluğu (`onAccuracyChanged`) + manyetik alan şiddeti takibi. ±20–70 μT aralığı dışı → bozulma uyarısı.
- **QiblaViewModel**: `collectAsStateWithLifecycle()` ile Flow dinleme. `onCleared()` ile otomatik sensor temizliği. Sensör doğruluk/kalibrasyon/manyetik verileri UI state'e aktarır.
- **QiblaCompass**: Referans pusula tasarımı. İç yüz (derece çizgileri, K/D/G/B etiketleri) cihaz yönüne göre döner, dış yüz sabit kalır. Kaaba simgesi `qiblaBearing - deviceAzimuth` açısında halka çevresinde döner. **En kısa yol rotasyonu**: `accumulatedAzimuth`/`accumulatedKaaba` değişkenleri ile açı farkı >180° durumunda ters yönde dönüş (shortest path). Spring animasyon: low stiffness (azimut) / medium bouncy (Kaaba). **Hizalama glow**: `alignedAlpha` interpolasyonu ile sürekli glow ring + radial gradient parlaması. Kaaba simgesi hizalıyken `1.0 + 0.2 * alignedAlpha` ile scale olur. Merkezde crosshair + seviye göstergesi. Canvas-only implementasyon.
- **QiblaDegreeDisplay**: Bearing + kardinal yön + sapma kartı. **Hizalı durumda arka plan overlay animasyonları**: Canvas tabanlı radial gradient sweep (2 trail beam), dönen arc çizgileri, orbital partikül dot'lar, breathing glow efekti. `AnimatedContent` ile durum metni (hizalı/sapma) geçişleri. **Sensör doğruluk göstergesi**: renkli dot + metin (İyi/Orta/Düşük/Kötü). Kalibrasyon/manyetik bozulma uyarıları inline error banner.
- **QiblaScreen**: Tam ekran, TopAppBar yok. 5 UI state: Loading/CompassActive/StaticDirection/Error/LocationRequired. **Histerezisli hizalama algılama**: ≤4° içinde aligned olur, ≤6.5° üstünde aligned'dan çıkar (flicker önler). 200ms gecikme ile state değişimi. ±5° hizalıyken titreşim.
- **Harita Görünümü**: MapLibre altyapısıyla (Raster tile) API anahtarı gerektirmeyen çevrimdışı Kabe haritası. `GreatCircleUtil` ile hesaplanan gerçek doğrusal hat `LineLayer` olarak zümrüt yeşili renkle çizilir. Kullanıcı "Konumuma Git" (17.0 zoom) butonuyla bina/sokak seviyesinde kıble yönünü fiziksel referanslarla eşleştirebilir.
- **Hybrid yaklaşım**: Manyetometre varsa tam pusula, yoksa statik yön + derece gösterimi.
- **Test**: `QiblaUseCaseTest` - 6 birim testi passing (Istanbul, NYC, Sydney, Kaabe, kardinal yönler).

### Türkçe Karakter Konum Arama Çözümü
- İmsakiyem API ilçe arama endpoint'inin (`api/locations/search/districts`) büyük/küçük harf ve Türkçe karakter hassasiyetinden kaynaklanan arama sorunları (`kırklareli`, `üsküdar`, `kadıköy` vb. yazılarak arama yapılamaması) çözülmüştür.
- `CitySelectionViewModel` ve `PrayerTimeRepositoryImpl` içindeki API çağrılarından önce arama terimleri `Locale.forLanguageTag("tr")` ile Türkçe kurallarına uygun olarak **BÜYÜK HARFE** dönüştürülür (`KIRKLARELİ` vb.).
- Eşleştirme aşamasında da isimlerin karşılaştırılması `lowercase(Locale.forLanguageTag("tr"))` ile yapılarak Türkçe karakterlerin tam eşleşmesi sağlanmıştır.

### Konum İsimleri Sadeleştirmesi ve Arayüz Düzeltmeleri
- Konum isimlerinin çok uzun olması durumunda `PrayerHeroCard` arayüzünün kayması ve bozulması engellenmiş, metin boyutları ve padding değerleri duyarlı hale getirilmiştir.
- Nominatim API'sinden gelen karmaşık ve çok uzun adres bilgileri sadeleştirilmiştir (ülke, eyalet vb. fazla detaylar elenerek sadece "Şehir, İlçe" veya "Şehir, Ülke" formatı korunmuştur).

### Geri Sayım ve Widget Senkronizasyonu
- **Gerçek Zamanlı Sayım**: App composable'ları (`CountdownTimer`, `CountdownDisplay`, `WearCountdownHero`) lokal decrement yerine `System.currentTimeMillis()` ile her tick'te hedef epoch millis'ten kalan saniyeyi yeniden hesaplar. Arka plana alınıp geri dönüldüğünde sayaç donma sorunu tamamen çözüldü.
- **Widget Native Chronometer**: Geri sayım, Glance `Text` yerine Android `Chronometer` widget'ı ile `AndroidRemoteViews` üzerinden render edilir. `SystemClock.elapsedRealtime()` tabanlı countdown mode (`setChronometerCountDown(true)`). Launcher/SystemUI süreci tarafından işletilir — uygulama uyurken %0 CPU, %0 wake lock, Doze/AppStandby geçirmez.
- **Widget Dakikalık Tick**: `WidgetMinuteReceiver` BroadcastReceiver, `AlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, ...)` ile her dakika başı `:00`'da `NamazWidget().updateAll()` tetikler. `SCHEDULE_EXACT_ALARM` izni manifest'te tanımlı. API 33+ `canScheduleExactAlarms()` kontrolü, SecurityException fallback. Mevcut 15dk WorkManager `NamazWidgetWorker` yedek katman olarak korunur.
- **Widget Kerahat Senkronizasyonu**: `NamazWidgetState` içinde `isKerahat`, `kerahatPrayerName`, `kerahatRemainingMinutes` alanları. `GetPrayerDashboardUseCase.execute()` ile mevcut domain mantığı widget state'e taşınır. Kerahat aktifken widget kartı `errorContainer` arka plan, `error` badge ile uyarı moduna geçer. Başlık "Kerahat Vakti • Şehir", kart içi "(Kerahat)" etiketi, Chronometer hedefi kerahat bitiş zamanına ayarlanır.
- **Glance Anlık Güncelleme**: DataStore veya veri değişikliklerinde `NamazWidgetUpdater` tüm aktif widget'ları `NamazWidget().updateAll(context)` ile anında günceller.

### Navigate
- `./gradlew :app:assembleDebug` (mobil), `./gradlew :wear:assembleDebug` (wear)
