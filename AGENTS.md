# Namaz Vakitleri - Android & Wear OS Uygulaması

Sen uzman bir Android ve Wear OS uygulama geliştiricisisin. Bu proje konum bazlı çalışan, hem mobil hem de Wear OS desteği olan bir "Namaz Vakitleri" uygulamasıdır. Kodları yazarken Clean Architecture ve MVVM tasarım desenlerini kullan.

## Projenin Mevcut Durumu

Tüm 7 geliştirme adımı tamamlanmıştır. Uygulama build alıp başarıyla derlenmektedir.

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

# Lint (henüz tanımlı değil - script eklenmeli)
./gradlew lint
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

## API

Aladhan Calendar API kullanılıyor:
```
GET https://api.aladhan.com/v1/calendar?latitude={lat}&longitude={lon}&method=13&school=1&month={month}&year={year}
```
- `method=13`: Diyanet İşleri Başkanlığı hesaplama yöntemi.
- `school=1`: Hanafi mezhebi (Türkiye'de varsayılan). `school=0`: Şafii.
- Eksik `school` parametresi API'nin Şafii hesaplamasına düşmesine ve Fajr/İmsak gibi vakitlerde ~10 dk sapmaya neden olur.

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

### app/ (Mobil Modül) — `co.gorkem.namaz`

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
    │   ├── NamazApplication.kt            # @HiltAndroidApp
    │   ├── MainActivity.kt                # @AndroidEntryPoint, edge-to-edge
    │   │
    │   ├── domain/
    │   │   ├── model/
    │   │   │   ├── PrayerTime.kt          # data class: imsak, gunes, ogle, ikindi, aksam, yatsi
    │   │   │   ├── PrayerName.kt          # enum: IMSAK, GUNES, OGLE, IKINDI, AKSAM, YATSI
    │   │   │   └── LocationInfo.kt        # data class: lat, lon, city, district, timezone. DEFAULT=Istanbul
    │   │   ├── repository/
    │   │   │   └── PrayerTimeRepository.kt # interface: getPrayerTimes() → Flow<NetworkResult<List<PrayerTime>>>
    │   │   └── usecase/
    │   │       ├── GetNextPrayerUseCase.kt  # next prayer + remaining minutes hesabı
    │   │       └── GetPrayerTimesUseCase.kt # repository delegasyonu
    │   │
    │   ├── data/
    │   │   ├── remote/
    │   │   │   ├── NetworkResult.kt        # sealed: Success, Error, Loading
    │   │   │   ├── api/
    │   │   │   │   └── AladhanApiService.kt # Retrofit: GET calendar
    │   │   │   └── dto/
    │   │   │       └── PrayerTimeResponse.kt # CalendarResponse, DayData, Timings, DateInfo, Meta
    │   │   ├── repository/
    │   │   │   └── PrayerTimeRepositoryImpl.kt # @Singleton, API + Room cache
    │   │   └── local/datastore/
    │   │       ├── UserPreferencesRepository.kt # DataStore: notifications, city, method, school, recentCities
    │   │       ├── UserPreferencesSerializer.kt
    │   │       └── UserPreferencesDataStore.kt  # preferencesDataStore extension
    │   │
    │   ├── di/
    │   │   ├── NetworkModule.kt       # @Module: OkHttpClient, Retrofit, AladhanApiService
    │   │   ├── RepositoryModule.kt    # @Module: binds PrayerTimeRepository → Impl
    │   │   └── LocationModule.kt      # @Module: FusedLocationProviderClient
    │   │
    │   ├── service/
    │   │   ├── LocationTracker.kt           # FusedLocationProviderClient ile konum
    │   │   ├── GeocoderHelper.kt            # Reverse geocode: lat/lon → il/ilçe
    │   │   ├── NotificationHelper.kt        # Notification channel + bildirim gösterme
    │   │   ├── PrayerNotificationWorker.kt  # @HiltWorker, tek vakit bildirimi
    │   │   ├── PrayerNotificationScheduler.kt # 6 vakit bildirimi zamanlaması (WorkManager)
    │   │   ├── WearSyncManager.kt           # Data Layer API ile Wear'a veri gönderimi
    │   │   └── WearPrayerData.kt            # WearPrayerData, WearPrayerItem, WearNextPrayer
    │   │
    │   └── ui/
    │       ├── state/
    │       │   └── HomeUiState.kt  # sealed: Loading, Success(prayerTimes, nextPrayer, location), Error, PermissionRequired
    │       ├── theme/
    │       │   ├── Theme.kt       # NamazTheme (dynamic color, prayer-tinted scheme)
    │       │   ├── Color.kt       # M3 expressive light/dark colors + PrayerTints
    │       │   ├── PrayerColors.kt # Atmospheric gradient brushes per prayer time
    │       │   ├── Shape.kt       # ExpressiveShapes, HeroCardShape, PillShape
    │       │   └── Type.kt        # ExpressiveTypography scale
    │       ├── components/
    │       │   ├── PrayerHeroCard.kt   # Hero card: countdown + location + prayer name/time
    │       │   ├── PrayerTimeCard.kt   # Individual prayer time row
    │       │   └── CountdownTimer.kt   # Standalone countdown composable
    │       └── screens/
    │           ├── home/
    │           │   ├── HomeScreen.kt      # Dashboard: permission → hero card → prayer list → location
    │           │   └── HomeViewModel.kt   # Loads prayer times, cancels previous job on new load, handles permissions, syncs to wear, schedules notifications
    │           ├── cityselection/
    │           │   ├── CitySelectionScreen.kt    # Search, province/district browser, recent cities, location button
    │           │   ├── CitySelectionViewModel.kt # Search logic, save/clear preferences
    │           │   └── TurkishCities.kt          # 81 il + tüm ilçeler + koordinatlar
    │           ├── settings/
    │           │   ├── SettingsScreen.kt    # Notification toggle, minutes-before radio, calculation method, mezhep (school)
    │           │   └── SettingsViewModel.kt # UserPreferences via DataStore
    │           └── qibla/
    │               └── QiblaScreen.kt      # Placeholder "Yakında" (Coming Soon)
    │
    └── res/
        ├── values/strings.xml, themes.xml, colors.xml
        ├── drawable/ic_my_location.xml, ic_launcher_foreground.xml, ic_launcher_background.xml
        ├── mipmap-anydpi-v26/ic_launcher.xml, ic_launcher_round.xml
        └── xml/backup_rules.xml, data_extraction_rules.xml
```
**43 Kotlin dosyası**

### wear/ (Wear OS Modül) — `co.gorkem.namaz.wear`

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
    │   │
    │   ├── di/
    │   │   ├── WearNetworkModule.kt    # @Module: OkHttp, Retrofit, AladhanApiService
    │   │   └── WearDataEntryPoint.kt   # @EntryPoint: DataLayerService, PrayerRepository
    │   │
    │   ├── data/
    │   │   ├── WearDataLayerService.kt    # Wearable DataClient listener (phone → watch)
    │   │   ├── WearPrayerRepository.kt    # Standalone Aladhan API call (phone yoksa)
    │   │   ├── WearPrayerModel.kt         # WearPrayerData, WearPrayerItem, WearNextPrayer
    │   │   └── remote/
    │   │       ├── api/AladhanApiService.kt      # Retrofit interface
    │   │       └── dto/PrayerTimeResponse.kt     # DTOs (simplified)
    │   │
    │   ├── ui/
    │   │   ├── WearViewModel.kt       # DataLayer listener + standalone API fallback
    │   │   ├── screens/
    │   │   │   └── WearHomeScreen.kt  # ScalingLazyColumn: countdown hero, city, prayer rows, source
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
        ├── values/strings.xml
        ├── drawable/ic_launcher_foreground.xml, ic_launcher_background.xml
        ├── mipmap-anydpi-v26/ic_launcher.xml, ic_launcher_round.xml
        └── xml/backup_rules.xml, data_extraction_rules.xml
```
**17 Kotlin dosyası**

---

## Kullanıcı Akışı

1. **İlk açılış** → Konum izni istenir
   - İzin verilirse: FusedLocationProviderClient ile lat/lon alınır → Geocoder ile il/ilçe çözülür → Aladhan API'den vakitler çekilir
   - İzin verilmezse: CitySelectionScreen gösterilir, varsayılan İstanbul
2. **Ana ekran**: Hero card (geri sayım + sıradaki vakit) + 6 vakit listesi + konum chip'i
3. **Ayarlar**: Bildirim toggle, dakika seçimi (15/30/45), hesaplama yöntemi, mezhep (school)
4. **Şehir seçimi**: Arama + 81 il tarayıcısı + son seçilenler
5. **Kıble**: "Yakında" placeholder
6. **Wear senkronizasyonu**: Telefon Data Layer API ile wear'a vakit + konum bilgisi gönderir
7. **Wear standalone**: Telefon yoksa saat kendi konumundan API'ye sorar

## Geliştirme Notları

- **Konvansiyonlar**: Dilde Türkçe değişken isimleri kullanılmıştır (imsak, gunes, ogle, ikindi, aksam, yatsi)
- **Tema**: Material 3 dynamic color + namaz vakitlerine özel gradient/renk paleti (PrayerColors)
- **Çevrimdışı**: Room DB ile 1 aylık vakit verisi cache'lenir
- **Bildirimler**: WorkManager OneTimeWorkRequest ile her vakit için ayrı worker schedule edilir
- **Wear**: Hem Data Layer (telefon bağlıyken) hem standalone API (telefon yokken) çalışır
- **Complication**: 600 saniyede bir güncellenir, SHORT_TEXT tipinde
- **Tile**: TileService ile next prayer bilgisi gösterilir
- **Navigate**: `./gradlew :app:assembleDebug` (mobil), `./gradlew :wear:assembleDebug` (wear)
- **Code style**: Yorum yok (AGENTS.md convention)
- **Race condition fix**: HomeViewModel'de prayerTimesJob takip edilir, yeni yükleme önceki job'ı iptal eder. Konum değişince eski API çağrısı yeni veriyi ezemez.
- **School param**: Aladhan API'ye `school` parametresi eklendi (1=Hanafi, 0=Shafi). Eksik parametre ~10 dk sapmaya neden oluyordu.
