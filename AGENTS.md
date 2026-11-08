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
Gradle:      9.6.0
AGP:         9.4.0
Kotlin:      2.2.10
KSP:         2.2.10-2.0.2
DB:          v5
Compose BOM: 2026.05.00
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
Network:     Retrofit 2.11.0, OkHttp 4.12.0, Gson 2.14.0
Coroutines:  1.11.0
Navigation:  2.9.8
Location:    play-services-location 21.3.0
DataStore:   1.2.1 (Preferences)
Room:        2.8.4
WorkManager: 2.11.2
Wear:        compose-material3 1.6.1, tiles-material 1.6.0, complications 1.3.0
Wearable:    play-services-wearable 20.0.1
Horologist:  0.7.15
Desugar:     2.1.5
Glance:      1.1.1 (Widget)
MapLibre:    0.12.1 (MapLibre Compose)
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

### app/ (Mobil Modül) — `co.gorkem.namaz` — 89 Kotlin dosyası

```
app/
├── build.gradle.kts
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    │   Permissions: ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION, INTERNET, POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM, USE_EXACT_ALARM
    │   @HiltAndroidApp NamazApplication, @AndroidEntryPoint MainActivity
    │   Widget: APPWIDGET_UPDATE receiver + configure activity + WorkManager init provider
    ├── java/co/gorkem/namaz/
    │   ├── NamazApp.kt                    # Root composable, 4-tab bottom nav (Vakitler/Takvim/Kıble/Ayarlar) + AppThemeEntryPoint + rememberAppTheme() + HorizontalPager (Zikirmatik/Home)
    │   ├── NamazApplication.kt            # @HiltAndroidApp, Configuration.Provider (HiltWorkerFactory), notification channel + worker schedule + passive location listener + WidgetMinuteReceiver reboot reschedule
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
    │   │   │   ├── PrayerTimeRepository.kt # interface: getPrayerTimes() → Flow<NetworkResult<List<PrayerTime>>>, refreshPrayerTimes(), getActiveDataSource(), findDistrictId(), cleanupOldData()
    │   │   │   └── ZikirRepository.kt      # interface: getZikirs(), addZikir(), updateZikir(), deleteZikir(), resetZikirCount(), getZikirById()
    │   │   └── usecase/
    │   │       ├── GetNamazTesbihatiUseCase.kt # returns Namaz Tesbihati preset zikirs
    │   │       ├── GetNextPrayerUseCase.kt    # next prayer + remaining minutes
    │   │       ├── GetPrayerTimesUseCase.kt   # repository wrapper
    │   │       ├── GetPrayerDashboardUseCase.kt # PeriodType + kerahat windows + current period logic
    │   │       ├── GetZikirsUseCase.kt        # gets zikirs from repository
    │   │       ├── ManageZikirUseCase.kt      # CRUD & count/reset operations for custom/preset zikirs
    │   │       └── QiblaUseCase.kt            # bearing calculation: lat/lon → Kaaba (21.4225, 39.8262)
    │   │
    │   ├── data/
    │   │   ├── remote/
    │   │   │   ├── NetworkResult.kt        # sealed: Success, Error, Loading
    │   │   │   ├── api/
    │   │   │   │   ├── AladhanApiService.kt   # Retrofit: GET prayer times calendar by coordinates (with method/school)
    │   │   │   │   ├── ImsakiyemApiService.kt # Retrofit: GET monthly, search districts, states
    │   │   │   │   └── NominatimApiService.kt # Retrofit: GET search (OpenStreetMap geocoding)
    │   │   │   └── dto/
    │   │   │       ├── AladhanResponse.kt     # Aladhan prayer calendar DTOs
    │   │   │       ├── ImsakiyemResponse.kt   # BaseResponse, DayPrayerDto, DistrictDto, HijriDateDto
    │   │   │       └── NominatimResponse.kt   # NominatimPlace, NominatimAddress
    │   │   ├── repository/
    │   │   │   ├── PrayerTimeRepositoryImpl.kt # @Singleton, API + Room cache, method+school-aware cache key, widget updater
    │   │   │   └── ZikirRepositoryImpl.kt      # @Singleton, Local Room DB implementation of ZikirRepository
    │   │   └── local/
    │   │       ├── room/
    │   │       │   ├── NamazDatabase.kt        # Room v5, PrayerTimeEntity, ZikirEntity, fallbackToDestructiveMigration(true)
    │   │       │   ├── PrayerTimeDao.kt        # Queries by city+month+year+method+school, deleteOlderThan
    │   │       │   ├── PrayerTimeEntity.kt     # date (PK), cityName, 6 prayer times, hijri fields, cacheSchool, cacheDataSource
    │   │       │   ├── ZikirDao.kt             # Queries for Zikir CRUD operations
    │   │       │   └── ZikirEntity.kt          # Room Entity for Zikirs: id, title, arabicText, targetCount, currentCount, isNamazTesbihati, currentPhase
    │   │       └── datastore/
    │   │           ├── UserPreferencesRepository.kt # DataStore: notifications, city, method, school, language, recentCities, appTheme → widget updater. setLastAutoLocation() does NOT update widget (waits for data load).
    │   │           └── UserPreferencesDataStore.kt  # preferencesDataStore extension
    │   │
    │   ├── di/
    │   │   ├── NetworkModule.kt       # @Module: OkHttp (logging, 30s), Retrofit instances for Aladhan, Imsakiyem, and Nominatim
    │   │   ├── RepositoryModule.kt    # @Module: binds repository interfaces to implementation singletons
    │   │   ├── LocationModule.kt      # @Module: FusedLocationProviderClient
    │   │   └── RoomModule.kt          # @Module: NamazDatabase, PrayerTimeDao, and ZikirDao singletons
    │   │
    │   ├── service/
    │   │   ├── LocationTracker.kt           # FusedLocationProviderClient, hasLocationPermission(), registerPassiveLocationListener()
    │   │   ├── LocationPassiveReceiver.kt   # BroadcastReceiver: PendingIntent-based displacement listener (10km), triggers LocationSyncWorker
    │   │   ├── LocationSyncWorker.kt        # @HiltWorker, periodic (12h) & on-demand (expedited) location sync, 10km significance check, API+widget+wear chain. Widget update always called (even on API failure).
    │   │   ├── GeocoderHelper.kt            # Reverse geocode: lat/lon → city/district name using Android Geocoder
    │   │   ├── NotificationHelper.kt        # Notification channel + displaying single/reminder notifications
    │   │   ├── PrayerNotificationWorker.kt  # @HiltWorker, displays single prayer time alert + updates widget
    │   │   ├── PrayerNotificationScheduler.kt # Schedules WorkManager workers for the 6 prayer times
    │   │   ├── WearSyncManager.kt             # Sends serialized prayer, location, theme & kerahat data to Wear OS via Data Client API
    │   │   ├── WearPrayerData.kt              # DTO representation of synchronized wear data
    │   │   ├── QiblaSensorManager.kt          # SensorManager wrapper, accelerometer & magnetometer listener, smooth azimuth flow (Sin/Cos filtering)
    │   │   ├── NamazWidget.kt                 # GlanceAppWidget: compact/expanded layout, theme-aware, AndroidRemoteViews Chronometer countdown, prayer transition alarm
    │   │   ├── NamazWidgetReceiver.kt         # GlanceAppWidgetReceiver: ACTION_PRAYER_TIME_TRANSITION handling, WidgetMinuteReceiver lifecycle
    │   │   ├── NamazWidgetConfigureActivity.kt # Widget layout selection
    │   │   ├── NamazWidgetPrefs.kt            # Widget-specific SharedPreferences (layout selection)
    │   │   ├── NamazWidgetUpdater.kt          # Updates all active Glance widgets on demand with Log.d/e error reporting
    │   │   └── WidgetMinuteReceiver.kt        # AlarmManager exact-minute tick: widget update + location change detection (lastLocation → getCurrentLocation with 5min backoff) → LocationSyncWorker trigger
    │   │
    │   ├── ui/
    │   │   ├── state/
    │   │   │   ├── HomeUiState.kt  # sealed: Loading, Success, Error, PermissionRequired
    │   │   │   └── QiblaUiState.kt # sealed: Loading, CompassActive, StaticDirection, Error, LocationRequired
    │   │   ├── theme/
    │   │   │   ├── Theme.kt       # NamazTheme(currentTheme), custom schemes, bypasses dynamic color
    │   │   │   ├── Color.kt       # AppThemeSelection enum + Color Palettes + Glance constants + PrayerTints
    │   │   │   ├── PrayerColors.kt # Atmospheric gradient brushes per prayer time
    │   │   │   ├── Shape.kt       # ExpressiveShapes, HeroCardShape, PillShape
    │   │   │   └── Type.kt        # ExpressiveTypography scale
    │   │   ├── theme/AppThemeUtils.kt # Helper for mapping theme selection to colors (e.g. for map view)
    │   │   ├── components/
    │   │   │   ├── PrayerHeroCard.kt       # Hero card: countdown + location + period info + hijri date
    │   │   │   ├── PrayerTimesList.kt      # 6 prayer rows: period-aware coloring and highlights
    │   │   │   ├── PrayerIcon.kt           # PrayerName → ImageVector mapping
    │   │   │   ├── CountdownTimer.kt       # Realtime countdown calculations with System.currentTimeMillis()
    │   │   │   ├── QiblaCompass.kt         # Canvas compass: rotating inner dial, shortest path rotation, glow Ring, rotating Kaaba
    │   │   │   └── QiblaDegreeDisplay.kt   # Degree, cardinal indicators, calibration dots, alignment gradient overlay animations
    │   │   └── screens/
    │   │       ├── home/
    │   │       │   ├── HomeScreen.kt      # Dashboard: hero card -> kerahat banner -> zikir button -> prayer list
    │   │       │   └── HomeViewModel.kt   # prayerTimesJob cancellation, settings reactive reload
    │   │       ├── calendar/
    │   │       │   ├── CalendarScreen.kt  # Monthly table calendar view, current day highlighted
    │   │       │   └── CalendarViewModel.kt # Observers preferences & loads monthly calendar times
    │   │       ├── cityselection/
    │   │       │   ├── CitySelectionScreen.kt    # Search (Nominatim / Imsakiyem), recent cities, GPS select
    │   │       │   └── CitySelectionViewModel.kt # Debounced search (400ms), large-case Turkish convert, save/clear prefs
    │   │       ├── settings/
    │   │       │   ├── SettingsScreen.kt    # Notifications toggles, language recreate, method, theme select
    │   │       │   └── SettingsViewModel.kt # UserPreferences repository interaction
    │   │       ├── qibla/
    │   │       │   ├── QiblaScreen.kt      # Functional compass: sensor or static fallback, map switch
    │   │       │   ├── QiblaViewModel.kt   # Lifecycle-aware sensor/Compass flow controller
    │   │       │   └── QiblaMap.kt         # MapLibre offline geocoded Kaaba route drawer (emerald great-circle line)
    │   │       └── zikirmatik/
    │   │           ├── ZikirmatikScreen.kt # Realtime Zikir tracking UI, progress arc, Namaz Tesbihati stages
    │   │           ├── ZikirmatikViewModel.kt # Manages current zikir, presets & custom zikirs, vibration triggers
    │   │           └── NamazTesbihatiStep.kt # Namaz Tesbihatı data class and list of steps
    │   │
    │   └── util/
    │       ├── ContextExtensions.kt            # Context extension properties (e.g. check permissions)
    │       ├── DateTimeUtils.kt               # Time conversion helpers
    │       ├── GreatCircleUtil.kt              # Computes path points on the globe along the shortest spherical distance (Great Circle)
    │       ├── LocationMath.kt                # Geolocation maths, distance checks between coordinates
    │       └── NotificationPermissionHelper.kt # Safe request & checker helpers for notifications
    │
    └── res/
        ├── values/strings.xml, values-en/strings.xml, themes.xml, colors.xml
        ├── drawable/ic_prayer_*.xml, ic_widget_settings.xml, ic_my_location.xml, ic_launcher_*.xml
        ├── layout/widget_chronometer.xml
        ├── mipmap-*/ic_launcher.webp, ic_launcher_round.webp
        └── xml/backup_rules.xml, data_extraction_rules.xml, namaz_widget_info.xml
```

### wear/ (Wear OS Modül) — `co.gorkem.namaz.wear` — 33 Kotlin dosyası

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
    │   ├── WearApp.kt              # Root composable (WearTheme + SwipeDismissableNavHost: Home / Qibla / Zikirmatik)
    │   ├── WearApplication.kt      # @HiltAndroidApp
    │   ├── MainActivity.kt         # @AndroidEntryPoint ComponentActivity
    │   ├── PrayerNameHelper.kt     # Context-based prayer name mapper (⚠️ duplicate with ui/PrayerNameHelper.kt)
    │   │
    │   ├── complication/
    │   │   ├── ComplicationUpdateReceiver.kt # BroadcastReceiver triggering watch-face complication updates
    │   │   ├── PrayerComplicationService.kt # ShortTextComplication displaying next prayer time & name
    │   │   └── PrayerCountdownComplicationService.kt # Dynamic ShortTextComplication displaying watch-face countdown
    │   │
    │   ├── di/
    │   │   ├── WearNetworkModule.kt    # @Module: OkHttp, Retrofit for Aladhan and Imsakiyem APIs
    │   │   └── WearDataEntryPoint.kt   # @EntryPoint: DataLayerService, PrayerRepository (for Tile/Complications)
    │   │
    │   ├── data/
    │   │   ├── WearDataLayerService.kt    # DataClient listener (/namaz_prayer_data) for background sync from phone
    │   │   ├── WearPrayerRepository.kt    # Standalone Aladhan API fetch (if phone is disconnected)
    │   │   ├── WearPrayerModel.kt         # WearPrayerData, WearPrayerItem, WearNextPrayer, WearKerahatInfo
    │   │   ├── QiblaDirection.kt          # QiblaDirection model copy
    │   │   ├── QiblaUseCase.kt            # Qibla bearing calculation use case copy
    │   │   ├── QiblaSensorManager.kt      # Magnetometer & Accelerometer flow copy
    │   │   └── remote/
    │   │       ├── api/
    │   │       │   ├── AladhanApiService.kt  # Retrofit interface (method, school params)
    │   │       │   └── ImsakiyemApiService.kt # Retrofit interface for standalone Diyanet fetch on watch
    │   │       └── dto/
    │   │           ├── AladhanResponse.kt    # DTOs simplified
    │   │           └── PrayerTimesResponse.kt # DTO representation of Diyanet monthly times
    │   │
    │   ├── ui/
    │   │   ├── WearViewModel.kt       # DataLayer listener + standalone API fallback
    │   │   ├── WearQiblaViewModel.kt  # Qibla calculation, sensor, and location management ViewModel
    │   │   ├── WearZikirmatikViewModel.kt # ViewModel managing Watch Zikirmatik tracking & resets
    │   │   ├── PrayerNameHelper.kt    # @Composable prayer name mapper (⚠️ duplicate with root PrayerNameHelper.kt)
    │   │   ├── state/
    │   │   │   └── QiblaUiState.kt    # Compass state enum (CompassActive, StaticDirection, etc.)
    │   │   │
    │   │   ├── screens/
    │   │   │   ├── WearHomeScreen.kt  # TransformingLazyColumn grid (2x3 cells), status header, countdown & action buttons
    │   │   │   ├── WearQiblaScreen.kt # Canvas compass layout, glow Ring, rotating Kaaba, haptic feedback when aligned
    │   │   │   ├── WearZikirmatikScreen.kt # Circular screen tapped tracking interface with progress circle & Namaz Tesbihati phases
    │   │   │   └── WearNamazTesbihatiStep.kt # Wear Namaz Tesbihatı data models and steps
    │   │   └── theme/
    │   │       ├── Theme.kt           # WearTheme (OLED-black color schemes synchronized with mobile theme ID)
    │   │       ├── Color.kt           # OLED-optimized high-contrast wear palettes
    │   │       └── Type.kt            # WearTypography scale
    │   │
    │   ├── tile/
    │   │   └── PrayerTileService.kt    # TileService: next prayer name, time, and remaining minutes
    │   │
    │   └── complication/
    │       └── (Services listed under complication/ package)
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
2. **Ana ekran**: Hero card (geri sayım + sıradaki vakit + periyot) + kerahat banner + zikir butonu + 6 vakit listesi (uzun konum isimleri sadeleştirilmiş ve taşma sorunları giderilmiştir)
3. **Zikirmatik (Gelişmiş)**: Ana ekranda (Times) sağa kaydırarak (veya HomeScreen'deki zikir butonuna tıklayarak) ulaşılan; (a) **Kısa Namaz Tesbihatı** (sırasıyla duaları, Türkçe anlamlarını ve Arapçalarını gösteren, 33'lük tesbih çekimlerini adım adım rehberlikle yaptıran interaktif akış) ve (b) **Serbest Sayaç** (sınırsız veya 33, 99, 100, 1000 gibi hedefleri olan, ilerleme halkası ve titreşimli geri bildirimli sayaç) seçeneklerini barındıran gelişmiş zikir takip ekranı.
4. **Aylık Takvim**: Sol/sağ oklar ile ay bazlı ileri/geri geçiş yapılabilen, 5 günlük vakit + güneş sütunlarını barındıran, bulunulan günü vurgulayan responsive tablo ekranı
5. **Ayarlar**: Bildirim toggle, dakika seçimi (15/30/45), veri kaynağı seçimi (Aladhan API / İmsakiyem API / Lokal Hesaplama), hesaplama yöntemi, mezhep (school), tema (4 seçenek: Emerald Oasis / Sapphire Night / Amber Sands / Monochrome Platinum), dil
6. **Şehir seçimi**: Seçilen veri kaynağına göre arama yapılır:
   - Aladhan API / Lokal Hesaplama için: Nominatim API ile dünya genelinde koordinat arama
   - İmsakiyem API için: İmsakiyem API ile ilçe arama (büyük/küçük harf ve Türkçe karakter desteğiyle)
   - Son seçilenler (max 5, JSON olarak DataStore'da)
7. **Kıble**: Sensör tabanlı pusula (magnetometer + accelerometer), gerçek zamanlı iğne, Kıble yayı, ±5° hizalanma titreşimi. Manyetometre yoksa statik yön + derece gösterimi. Ayrıca MapLibre entegrasyonu ile Kabe'ye giden çizgiyi haritada (zümrüt yeşili Great Circle hattıyla bina/sokak detayında) görselleştiren harita desteği.
8. **Wear senkronizasyonu**: Telefon Data Layer API ile wear'a vakit + konum koordinatları (enlem/boylam) + next prayer + kerahat + tema bilgisi gönderir.
9. **Wear Kıble**: Wear OS ana ekranındaki "Kıble Bulucu" butonuna basarak ulaşılan, dairesel ekranlara optimize edilmiş, hizalandığında haptik titreşim ve visual glow parlaması veren canvas pusula. Sensör yoksa statik kıble yönü ve açısını gösterir.
10. **Wear Zikirmatik (Gelişmiş)**: Wear OS ana ekranındaki "Zikirmatik" butonundan ulaşılan; dairesel ekran kavislerine tam uyumlu, üst ve alt çakışmaları (TimeText ve sistem butonları) önleyecek şekilde optimize edilmiş; hem **Serbest Sayaç** (hedefli/sınırsız) hem de **Kısa Namaz Tesbihatı** (adım adım rehberli) akışlarını barındıran, dairesel ilerleme halkalı ve haptik titreşim geri bildirimli saat zikirmatik ekranı.
11. **Wear standalone**: Telefon yoksa saat standalone olarak Aladhan API / Imsakiyem API ile koordinat bazlı çalışır (GPS konumu alınamadığında İstanbul koordinatlarına düşer)

---

## Geliştirme Notları

### Konvansiyonlar
- Dilde Türkçe değişken isimleri kullanılmıştır (imsak, gunes, ogle, ikindi, aksam, yatsi).
- Code style: Yorum yok (AGENTS.md convention).

### Test Kuralları ve TDD (Test-Driven Development) Yaklaşımı
- **TDD İlkesi**: Herhangi bir iş mantığı (business logic), UseCase, ViewModel veya Repository geliştirmesine başlamadan önce **MUTLAKA** ilgili birim testleri (unit test) yazılmalı veya güncellenmelidir.
- **Her Değişiklikten Sonra Test**: Yapılan her kod değişikliği veya yeni özellik ekleme adımından sonra `./gradlew test` komutu çalıştırılarak tüm birim testlerinin başarıyla geçtiği doğrulanmalıdır. Testlerin geçmediği durumlarda hata düzeltilmeden bir sonraki adıma geçilemez.
- **Zamana Bağımlı Testler**: Vakit hesaplama, countdown veya zaman hassasiyetli iş mantığı içeren UseCase veya Helper fonksiyonlarında, test edilebilirliği sağlamak için clock bağımlılığı (System time/LocalDate/LocalTime) dışarıdan opsiyonel parametre olarak alınmalı ve testlerde deterministik girdilerle doğrulanmalıdır.
- **Test Lokasyonları**: 
  - Mobil modül iş mantığı testleri [app/src/test/](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/test/) altında olmalıdır.
  - Wear OS modülü iş mantığı testleri [wear/src/test/](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/wear/src/test/) altında olmalıdır.

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
- `PrayerTimeEntity` ve `ZikirEntity` cache'lenir. PrayerTime cache key: `cityName + month + year + method + school + cacheDataSource`.
- `NamazDatabase` version = 5. `fallbackToDestructiveMigration(true)` kullanılır (schema export disabled).
- `cleanupOldData(30)` 30 günden eski veriyi siler.

### Bildirimler
- WorkManager `OneTimeWorkRequest` ile her vakit için ayrı worker.
- `LocationSyncWorker`: 12 saatte bir periyodik ve konum değiştiğinde on-demand çalışır.
  - **On-demand işler `setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)` ile önceliklendirilir** — Doze modunda bile hemen çalışır.
  - 10km displacement limitiyle gereksiz ağ isteklerini keserek pil tasarrufu sağlar.
  - **Widget güncellemesi her zaman çağrılır** — API başarılı veya başarısız fark etmez, `NamazWidgetUpdater.update()` `isSuccess` bloğu dışındadır.
  - Wear senkronizasyonu sadece başarılı API sonucunda yapılır (`isSuccess` içinde).

### i18n
- `LocaleManager`: SharedPreferences tabanlı, `attachBaseContext` ile locale uygular.
- Dil değişikliği `activity.recreate()` ile yapılır (CompositionLocalProvider yaklaşımı çöktü).
- `values/strings.xml` (TR) ve `values-en/strings.xml` (EN) tam mirror.
- `PrayerName` enum'u `@StringRes` tutar, `stringResource()` ile çözülür.

### DataStore
- `UserPreferencesRepository`: standard Preferences DataStore keys kullanır.
- `recentCities`: Gson ile JSON string olarak saklanır (max 5, deduplicated).
- `dataSource`: Int. Hangi veri kaynağının aktif olduğunu belirtir (0: Aladhan API, 1: Imsakiyem API, 2: Lokal Hesaplama).
- `useLocalCalculation`: Boolean. Geriye uyumluluk için tutulur. Açıksa API kullanılmaz, lokal hesaplama yapılır.
- `appTheme`: Int. Aktif temayı belirtir (0: Emerald Oasis, 1: Sapphire Night, 2: Amber Sands, 3: Monochrome Platinum). Her preference değişikliğinde widget otomatik güncellenir.
- **`setLastAutoLocation()` widget güncellemez** — konum değişikliğinde widget'ın yeni veri yüklenmeden boş/hatalı state'e geçmesini engellemek için `updateWidget()` çağrısı kaldırılmıştır. Widget güncellemesi sadece `refreshPrayerTimes()` başarıyla tamamlandıktan sonra `LocationSyncWorker` tarafından tetiklenir.

### HomeViewModel Race Condition
- `prayerTimesJob` takip edilir, yeni yükleme önceki job'ı iptal eder.
- Yeni load başladığında `_prayerTimes`, `_error`, `_nextPrayer`, `_dashboardData` sıfırlanır.
- `init` block `preferencesRepository.preferences` dinler; city/method/school değişince reload eder.
- `useMyLocation()`: sadece `clearManualCity()` çağırır, init block reload'u tetikler.

### Wear
- Hem Data Layer (telefon bağlıyken) hem standalone API (telefon yokken) çalışır.
- **Application ID Eşleşmesi**: Veri senkronizasyonu (Data Layer API) için mobil ve wear modüllerinin `applicationId` değeri `"co.gorkem.namaz"` olarak eşitlendi.
- **Senkronizasyon İyileştirmesi**: `WearDataLayerService` başlangıçta `dataItems` sorgulayarak telefondan gelen son verileri anında çeker. Standalone yüklemesi başlamadan önce 2 saniyelik DataLayer bekleme süresi eklendi ve standalone verisinin senkronize veriyi ezmesi engellendi.
- **Dinamik Tema (Premium OLED):** Telefon uygulamasında seçilen tema (Emerald, Sapphire, Amber, Monochrome), `themeId` aracılığıyla Wear OS uygulamasına anlık senkronize edilir. Wear OS tarafında siyah (OLED uyumlu) arka planlı özel `ColorScheme` paletleri bulunur.
- **Kompakt Premium Arayüz (Grid Layout):** Wear OS ana ekranında vakitler alt alta sıralanmak yerine 2x3'lük bir ızgara (Grid) şeklinde sunulur. `TransformingLazyColumn` ve `SurfaceTransformation` ile satır satır 3 boyutlu kavis efektleri korunmuştur. Sistem TimeText çakışmaları (overlap) engellenmiştir.
- **Standart Navigasyon (SwipeDismissableNavHost):** Eski `HorizontalPager` ve yan Chevron butonları kaldırılmıştır. Ekranlar arası geçişler Wear OS resmi standartlarına uygun olarak `SwipeDismissableNavHost` ile yönetilir; sola kaydırarak (swipe-to-dismiss) bir önceki ekrana dönülür.
- **Premium Wear OS Kıble Pusulası (Qibla Finder):** Mobil uygulamadaki 3 boyutlu, aydınlatmalı (glow), animasyonlu canvas tabanlı tasarım Wear OS'e entegre edilmiştir. Kabe'ye hizalandığında görsel ışık efektleri ve haptik titreşim devreye girer. Manyetometre yoksa statik modda çalışır.
- **Dil Desteği**: Telefondan (`HomeViewModel`) saate gönderilen veriler `LocaleManager` kullanılarak kullanıcının seçtiği dilde gönderilir.
- **Complication (Saat Formatı)**: `PrayerComplicationService` ShortText formatındadır. Vakit adı `title` alanına, saati ise `text` alanına ayrıldı.
- **Complication (Geri Sayım)**: Yeni eklenen `PrayerCountdownComplicationService` ile işletim sistemi düzeyinde (Native) çalışan, pil dostu dinamik geri sayım komplikasyonu sunulur (Örn: "İkindi", "1sa 30dk").
- **Complication Tetikleme**: Telefondan saate veya WorkManager/Alarm altyapısından tetiklenen durumlarda `ComplicationUpdateReceiver` (`ComplicationDataSourceUpdateRequester` aracılığıyla) komplikasyonun kadran üzerinde anında güncellenmesini sağlar.
- **Tile**: `PrayerTileService`, next prayer + remaining minutes.
- **Kerahat**: Telefon'dan Wear'a kerahat bilgisi sync edilir. `WearKerahatBanner` aktif/yaklaşıyor durumlarını kompakt bir tasarımla gösterir.
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
- **Harita Görünümü**: MapLibre altyapısıyla (Raster tile) çevrimdışı Kabe haritası. `GreatCircleUtil` ile hesaplanan gerçek doğrusal hat `LineLayer` olarak zümrüt yeşili renkle çizilir. Kullanıcı "Konumuma Git" (18.0 zoom) butonuyla bina/sokak seviyesinde kıble yönünü fiziksel referanslarla eşleştirebilir.
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
- **Widget Dakikalık Tick**: `WidgetMinuteReceiver` BroadcastReceiver, `AlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, ...)` ile her dakika başı `:00`'da tetiklenir. Her tick'te iki iş yapar:
  1. `NamazWidget().updateAll(context)` — widget geri sayımını günceller.
  2. `checkLocationChange(context)` — `FusedLocationClient.lastLocation` ile kaydedilen son konumu karşılaştırır.
     - `lastLocation` null ise `getCurrentLocation()` fallback (25sn timeout).
     - `getCurrentLocation()` başarısız olursa **5 dakika backoff** — pil koruması için GPS tekrar tekrar uyandırılmaz.
     - 10 km aşıldıysa `LocationSyncWorker.executeNow()` çağrılır.
  - `SCHEDULE_EXACT_ALARM` izni manifest'te tanımlı. API 33+ `canScheduleExactAlarms()` kontrolü, SecurityException fallback.
  - **Reboot sonrası**: `NamazApplication.onCreate()` aktif widget varsa `scheduleNextMinute()` ile yeniden zamanlar.
  - **Wear etkilenmez**: Tüm bu mekanizma sadece mobil modüldedir, wear Data Layer sync ile güncel veriyi alır.
- **Widget Kerahat Senkronizasyonu**: `NamazWidgetState` içinde `isKerahat`, `kerahatPrayerName`, `kerahatRemainingMinutes` alanları. `GetPrayerDashboardUseCase.execute()` ile mevcut domain mantığı widget state'e taşınır. Kerahat aktifken widget kartı `errorContainer` arka plan, `error` badge ile uyarı moduna geçer. Başlık "Kerahat Vakti • Şehir", kart içi "(Kerahat)" etiketi, Chronometer hedefi kerahat bitiş zamanına ayarlanır.
- **Glance Anlık Güncelleme**: DataStore veya veri değişikliklerinde `NamazWidgetUpdater` tüm aktif widget'ları `NamazWidget().update(context, glanceId)` ile anında günceller. Hatalar `Log.e(TAG, ...)` ile logcat'e yazılır (önceden sessiz `e.printStackTrace()` idi).

### Arka Plan Konum ve Widget Güncelleme Mimarisi

Konum değişikliği → veri yenileme → widget güncelleme zinciri üç bağımsız yedekli yolla çalışır:

#### Yol 1: `LocationPassiveReceiver` (PendingIntent tabanlı)
- `LocationTracker.registerPassiveLocationListener()`: 10km displacement, 15dk interval.
- `LocationPassiveReceiver.onReceive()` → `LocationSyncWorker.executeNow(lat, lon)`.
- **Kısıt**: `ACCESS_BACKGROUND_LOCATION` izni gerektirir. "Yalnızca uygulamayı kullanırken" seçildiğinde çalışmaz.

#### Yol 2: `LocationSyncWorker` (12 saatte bir periyodik)
- `NamazApplication.onCreate()` → `schedulePeriodic()`.
- `doWork()`: `getCurrentLocation()` → 10km kontrolü → `refreshPrayerTimes()` → widget + wear sync.
- **Pil**: `BATTERY_NOT_LOW` kısıtı. Sadece eksik veri varsa API çağrısı yapar.

#### Yol 3: `WidgetMinuteReceiver` (dakikada bir — en güvenilir)
- Her dakika `lastLocation` okur (bedava, önbellekten).
- 10 km aşılınca `LocationSyncWorker.executeNow(lat, lon)`.
- `lastLocation` null ise `getCurrentLocation()` fallback, 5dk backoff korumalı.
- **"Yalnızca uygulamayı kullanırken" izniyle de çalışır** — hiçbir özel arka plan izni gerektirmez.

#### Widget güncelleme zinciri (Worker sonrası)
```
setLastAutoLocation()           ← widget GÜNCELLEMEZ (erken güncelleme önlendi)
         ↓
refreshPrayerTimes() → API/local
         ↓
NamazWidgetUpdater.update()     ← her zaman çağrılır (isSuccess içinde değil)
         ↓
NamazWidget.provideGlance()     ← DB'den okur, widget'ı render eder
         ↓
schedulePrayerTransitionAlarm() ← sonraki vakit için alarm kurar
```

#### Batarya etki özeti
| Bileşen | Sıklık | Pil maliyeti |
|---------|--------|-------------|
| `lastLocation` okuma | Her dakika | ~0 (önbellek) |
| `getCurrentLocation()` | Sadece null + 5dk backoff | Düşük (nadiren) |
| `LocationSyncWorker` | Sadece 10km aşılınca | ~2-3 sn CPU + 1 HTTP |
| Dakikalık `AlarmManager` | Her dakika | İhmal edilebilir (RTC) |
| Widget `provideGlance()` | Dakikada bir + vakit geçişi | ~50ms CPU |

### Navigate
- `./gradlew :app:assembleDebug` (mobil), `./gradlew :wear:assembleDebug` (wear)

### Emülatörde Konum Testi
- **API 36 emülatörde `adb emu geo fix` çalışmaz** (OK döner ama konum güncellenmez).
- Alternatif — Mock Location Provider:
  ```bash
  adb shell appops set com.android.shell android:mock_location allow
  adb shell cmd location providers add-test-provider gps --requiresSatellite
  adb shell cmd location providers set-test-provider-enabled gps true
  adb shell cmd location providers set-test-provider-location gps --location 41.0082,28.9784
  ```
- Widget dakikalık tick'i manuel tetikleme (test için):
  ```bash
  adb shell am broadcast -a co.gorkem.namaz.WIDGET_MINUTE_TICK -n co.gorkem.namaz/.service.WidgetMinuteReceiver -f 0x10
  ```
- Logcat filtreleme: `adb logcat -d | grep -E "WidgetMinuteReceiver|NamazWidgetUpdater|LocationSyncWorker"`

### CI/CD Pipeline (GitHub Actions) & Deployment
Projede iki adet otomatik iş akışı (workflow) tanımlıdır:
- **1. Sadece Web Sitesi Dağıtımı (`deploy-website.yml`)**:
  - `website/**` yollarındaki dosyalar güncellenip `main` branch'ine `push` yapıldığında tetiklenir.
  - Sitenin barındırıldığı `GorkemGuray/Namaz-App` reposunun `gh-pages` branch'ini klonlar, mevcut `version.json` dosyasını koruyarak (yedekleyip geri yükleyerek) web sitesini günceller.
  - Bu sayede uygulamayı derlemeden site güncellenebilir.
- **2. Uygulama Sürümü & Otomatik Release (`release.yml`)**:
  - `v*` formatındaki tag'ler pushlandığında tetiklenir.
  - **Dynamic Versioning**: Tag adından `versionName` (`v1.2.3` -> `1.2.3`) ve tag sayısından `versionCode` türetilip Gradle derlemesine aktarılır.
  - **Artifacts & Releases**: Derlenen `Namaz-{versionName}-app-debug.apk` ve `Namaz-{versionName}-wear-debug.apk` dosyaları oluşturulan GitHub Release'e yüklenir.
  - **version.json Dağıtımı**: Otomatik olarak yeni sürümü, sürüm kodunu ve tag açıklamalarını içeren `version.json` dosyasını üretip, web sitesi ile birlikte `gh-pages` branch'ine pushlar.
- **Secrets**: Her iki workflow da repo bazındaki `RELEASE_REPO_TOKEN` secret'ını kullanır.

### Uygulama İçi Güncelleme Kontrolü
- **UpdateChecker** (`util/UpdateChecker.kt`): `https://gorkemguray.github.io/Namaz-App/version.json` endpoint'inden JSON verisini okur, semantik versiyon karşılaştırması yapar (`isNewerThan`).
- **UpdateBanner** (`ui/components/UpdateBanner.kt`): Ana ekranda LazyColumn'ın ilk item'i olarak render edilir. İki mod: otomatik (haftada 1) ve manuel. Banner'da "İndir" butonu GitHub Releases sayfasını Intent ile açar.
- **UpdateCheckButton** (`ui/components/UpdateCheckButton.kt`): Ayarlar sayfasında manuel kontrol butonu.
- **Throttle**: `UserPreferencesRepository.lastUpdateCheckMs` ile haftada 1'den fazla otomatik kontrol yapılmaz.
- **Sürüm gösterimi**: `SettingsScreen` altında `BuildConfig.VERSION_NAME` ile `Namaz Vakitleri vX.Y.Z` yazar. `buildFeatures.buildConfig = true` etkin.

### Web Sitesi ve SEO (`website/`)

Tanıtım sitesi `gorkemguray.github.io/Namaz-App` adresinde GitHub Pages üzerinden yayınlanır. **Hibrit prerendering** yaklaşımı kullanır: her şehir için statik HTML (title, meta, JSON-LD, OG etiketleri gömülü), vakit değerleri JS ile canlı doldurulur.

**Önemli dosyalar:**
- `website/index.html` + `website/en/index.html` — ana sayfalar
- `website/app.js` — SPA motoru (dinamik SEO, tema, API)
- `website/generate_static.py` — statik şehir sayfası üretici
- `website/sehir/{city}/index.html` — 976 TR şehir (generate_static.py ile üretilir)
- `website/en/city/{city}/index.html` — 983 EN şehir
- `website/sitemap_index.xml` → `sitemap-tr.xml` + `sitemap-en.xml`
- `website/robots.txt` — `sitemap_index.xml` referansı

**⚠️ Kritik adım — `generate_static.py`:**
Sitemap değişikliği, yeni şehir ekleme veya sayfa şablonu güncellemelerinden sonra **mutlaka** çalıştırılmalıdır:
```bash
python3 website/generate_static.py
```
Bu script sitemap'lerden şehir listesini okur, her şehir için benzersiz statik HTML üretir. CI/CD (`deploy-website.yml`) bu adımı otomatik yapmaz — manuel çalıştırılmalı veya CI/CD'ye eklenmelidir.

**URL yapısı (clean URL):** `/sehir/{city}/` ve `/en/city/{city}/` formatı. Eski `?sehir=X` URL'leri `app.js` içinde redirect ile yeni formata yönlendirilir.

**SEO özellikleri:** Her sayfada benzersiz title/description/h1, OG/Twitter Card etiketleri, JSON-LD (WebApplication + Place), hreflang, GA4, lazy loading resimler, sitemap index yapısı. Sahte `aggregateRating` JSON-LD'den kaldırılmıştır (Google manuel ceza riski).
