# 01 — Mimari ve Modüller

## 1. Mimari Diyagram

```
┌─────────────────────────────────────┐
│  UI Layer (Compose Screens)         │  ← ui/screens/, ui/components/
│  ViewModel + UiState                │  ← ui/state/
├─────────────────────────────────────┤
│  Domain Layer                       │
│  UseCase → Repository Interface     │  ← domain/usecase/, domain/repository/
│  Domain Models + Calculator         │  ← domain/model/, domain/calculator/
├─────────────────────────────────────┤
│  Data Layer                         │
│  RepositoryImpl → API + Local       │  ← data/repository/
│  Remote (Retrofit 3 API) + DTO      │  ← data/remote/
│  Local (Room + DataStore)           │  ← data/local/
├─────────────────────────────────────┤
│  DI (Dagger Hilt)                   │  ← di/
│  Services (Location, Notification,  │  ← service/
│   WearSync, Sensor, Widget)         │
│  Util                               │  ← util/
└─────────────────────────────────────┘
```

Kurallar: Türkçe vakit isimleri (`imsak, gunes, ogle, ikindi, aksam, yatsi`), yorum yok (AGENTS.md konvansiyonu), `dynamicColor = false` zorunlu (Monet bypass).

## 2. Kök Dosyalar (app)

| Dosya | Sorumluluk | Ana imza |
|-------|------------|----------|
| `NamazApp.kt` | Root composable, 5 tab BottomNav (Times/Qibla/Zikirmatik/Calendar/Settings), AnimatedContent geçiş, CitySelection BottomSheet, `rememberAppTheme()` | `fun NamazApp()`, `fun rememberAppTheme(): AppThemeSelection` |
| `NamazApplication.kt` | `@HiltAndroidApp`, `Configuration.Provider(HiltWorkerFactory)`, kanal + periyodik worker + pasif konum + reboot widget tick | `class NamazApplication : Application(), Configuration.Provider` |
| `MainActivity.kt` | `@AndroidEntryPoint`, edge-to-edge, `LocaleManager` + `NamazTheme { NamazApp() }` | `class MainActivity : ComponentActivity()` |
| `LocaleManager.kt` | SharedPrefs (`locale_prefs/language`, default `tr`) tabanlı i18n | `object LocaleManager { fun setLanguage/getLanguage/applyLocale }` |

## 3. Domain Katmanı (app, ~17 dosya)

### 3.1 `domain/model/`
- `PrayerTime.kt`: `data class PrayerTime(date, gregorianDate, hijriDay/MonthNumber/Year, imsak, gunes, ogle, ikindi, aksam, yatsi, timezone)` + `getTimeForName()` + `getAllPrayers()`.
- `PrayerName.kt`: enum IMSAK/GUNES/OGLE/IKINDI/AKSAM/YATSI (`@StringRes`), `fromName()` Aladhan isimlerini eşler (fajr/sunrise/dhuhr/asr/maghrib/isha).
- `LocationInfo.kt`: `data class LocationInfo(latitude, longitude, timezone, cityName, districtName, districtId)` + `formattedName` + `DEFAULT` (İstanbul 41.0082,28.9784, Europe/Istanbul, districtId 9541).
- `QiblaDirection.kt`: `data class QiblaDirection(bearing, cardinalDirection)` (K/KD/D/GD/G/GB/B/KB).

### 3.2 `domain/repository/` (interface)
- `PrayerTimeRepository.kt`: `getPrayerTimes(location, month, year, method=13, school=1): Flow<NetworkResult<List<PrayerTime>>>`, `refreshPrayerTimes()`, `getActiveDataSource()`, `findDistrictId()`, `cleanupOldData()`.
- `ZikirRepository.kt`: `getAllZikirs()`, `getNamazTesbihati()`, `insert/update/delete`, `updateCountAndPhase()`.

### 3.3 `domain/usecase/`
- `GetPrayerTimesUseCase`: repository delegasyonu.
- `GetNextPrayerUseCase`: `NextPrayerInfo(prayerName, prayerTime, remainingMinutes, targetPrayerTimeInMillis)`; `invoke(today, now=now(), today=now())`, yarına sarar.
- `GetPrayerDashboardUseCase`: `PeriodType(SABAH/KUSLUK/OGLE/IKINDI/AKSAM/YATSI)`, `PrayerDashboardData`, `KerahatInfo`; `execute(today, tomorrow?, now=now())`. 3 kerahat penceresini hesaplar.
- `QiblaUseCase`: `invoke(lat, lon): QiblaDirection` (Kabe 21.4225,39.8262).
- `GetZikirsUseCase`, `GetNamazTesbihatiUseCase`, `ManageZikirUseCase` (`addZikir/deleteZikir/updateCount/resetZikir`).

### 3.4 `domain/calculator/` (offline motor)
- `CalculationMethod.kt`: `fajrAngle=-18°, ishaAngle=-17°, asrShadowFactor=1.0, sunriseOffset=-7, dhuhrOffset=+5, asrOffset=+4, maghribOffset=+7`; `DIYANET` preset.
- `SolarCalculations.kt`: `julianDay()`, `sunPosition()` (declination + EoT), `hourAngle()`, `solarNoonUtc()`, `calculatedTimeToLocalTime()`.
- `PrayerTimeCalculator.kt` (`@Singleton`): `calculate(date, lat, lon, timezone, method=DIYANET): CalculatedPrayerTimes`.

## 4. Data Katmanı (app, ~16 dosya)

### 4.1 `data/remote/api/`
- `AladhanApiService`: `GET calendar/{year}/{month}?latitude&longitude&method&school`.
- `ImsakiyemApiService`: `GET api/locations/search/districts?q=&countryId=2` + `GET api/prayer-times/{districtId}/monthly?startDate?`.
- `NominatimApiService`: `GET search?q=&format=json&addressdetails=1&limit=20&accept-language=tr`.

### 4.2 DTO + `NetworkResult`
- `NetworkResult<T>`: `Success(data)` / `Error(message, code?)`.
- `AladhanResponse`: `code/status/data: List<AladhanDayData(timings/date/meta)>`; timings `fajr/sunrise/dhuhr/asr/maghrib/isha` ("HH:mm ..." suffixli).
- `ImsakiyemResponse`: `DistrictSearchResponse`, `PrayerTimesResponse`, `DistrictDto(id/name/stateId/countryId)`, `DayPrayerDto(date/times/hijriDate)`, `PrayerTimesDto(imsak/gunes/ogle/ikindi/aksam/yatsi)`, `HijriDateDto`.
- `NominatimResponse`: `NominatimPlace(placeId/display_name/lat/lon/address)` + türetilmiş `cityName/districtName`.

### 4.3 `data/repository/`
- `PrayerTimeRepositoryImpl` (`@Singleton`): cache-first + hibrit. `getPrayerTimes()`, `findDistrictId()` (TR uppercase normalize), `refreshPrayerTimes()`, `cleanupOldData(30)`, `shouldUseLocalCalculation()` (ağ yoksa otomatik lokal), `calculateLocally/fetchFromImsakiyem/fetchFromAladhan/cleanTime/minutesToTimeString/toEntity/toDomain`. Cache key: `cityName+month+year+method+school+dataSource`. Her yazımda `NamazWidgetUpdater.update()`.
- `ZikirRepositoryImpl`: DAO delegasyonu.

### 4.4 `data/local/room/`
- `NamazDatabase` v5 (`NamazDatabase.VERSION`), `exportSchema=true`, entities `[PrayerTimeEntity, ZikirEntity]`. Şemalar `app/schemas/` altına KSP ile dışa aktarılır (`room.schemaLocation`).
- **Migration kuralı:** v1–v4 arası sürümlerde `zikirs` tablosu yoktu, bu nedenle v5'e geçişte kullanıcı verisi kaybedilecek bir şey bulunmuyor. **v5'ten sonraki her şema değişikliğinde `MigrationSpec(from, to, statements)` eklenmeli** ve `NamazDatabaseMigrations.registry`'ye kaydedilmelidir; registry boş kaldığı sürece `RoomModule`'daki `fallbackToDestructiveMigration(true)` devreye girer. Migration yazılmadan sürüm artırılmayacaktır.
- Zikir verisi (kullanıcıya ait sayaçlar) API'den yeniden çekilemez; vakit cache'i ise `cleanupOldData(30)` ile zaten temizlenen, yeniden alınabilir veridir.
- `PrayerTimeEntity` (PK: date+cityName+cacheMethod+cacheSchool+cacheDataSource): 6 vakit + hicri + timezone + cacheMonth/Year/Method/School/DataSource + cachedAt.
- `PrayerTimeDao`: `getPrayerTimesForMonth()`, `getMonthRecordCount()`, `getPrayerTimeForDate()`, `insertAll(REPLACE)`, `deleteOlderThan()`, `deleteMonthForCity()`.
- `ZikirEntity` (auto id): `title/targetCount/currentCount/isNamazTesbihati/currentPhase`.
- `ZikirDao`: `getAllZikirs(): Flow`, `getNamazTesbihati(): Flow`, `insert/update/delete`, `updateCountAndPhase()`.
- `NamazDatabaseMigrationTest`: migration yolu çözümleme (tek adım, zincir, kopuk zincir, downgrade) ve registry tutarlılığı testleri.

### 4.5 `data/local/datastore/`
- `UserPreferencesDataStore.kt`: `preferencesDataStore(name="namaz_preferences")`.
- `UserPreferencesRepository.kt`: `UserPreferences` + `RecentCity` + `getLocationInfo()`; Flow `preferences`; setter'lar aşağıda. `setLastAutoLocation()` widget tetiklemez (erken güncelleme önlemi).

DataStore anahtarları:

| Key | Tip | Default | Not |
|-----|-----|---------|-----|
| `notifications_enabled` | Bool | true | master |
| `notification_permission_requested` | Bool | false | |
| `notification_minutes_before` | Int | 15 | 0/15/30/45 |
| `calculation_method` | Int | 13 | Diyanet=13 |
| `school` | Int | 0 | 0 Şafii / 1 Hanefi |
| `use_local_calculation` | Bool | false | true → dataSource=2 |
| `data_source` | Int | 0 | 0 Aladhan / 1 İmsakiyem / 2 Lokal |
| `language` | String | tr | + LocaleManager mirror |
| `app_theme` | Int | 0 | 0-3 Dinamik (Emerald/Sapphire/Amber/Mono), 4-10 Sabit Görünümlü (Yumuşak Aydınlık..Amber Gece) |
| `manual_city/district/district_id/lat/lon/timezone` | String | ""/Europe/Istanbul | manuel seçim |
| `last_auto_*` | String | "" | GPS (lat/lon String) |
| `recent_cities` | String JSON | [] | Gson max 5 dedup |
| `last_update_check_ms` | Long | 0 | haftalık throttle |

Her `set*` (hariç `setLastAutoLocation`) → `NamazWidgetUpdater.update()`.

## 5. DI (Hilt, 4 modül)

- `NetworkModule`: `provideOkHttpClient` (30sn, logging), imsakiyem Retrofit, Aladhan Retrofit (ayrı builder), `@Named("nominatim")` OkHttp (User-Agent header) + Retrofit + service.
- `RepositoryModule`: `bindPrayerTimeRepository`, `bindZikirRepository` (`@Binds @Singleton`).
- `RoomModule`: `provideNamazDatabase/providePrayerTimeDao/provideZikirDao`.
- `LocationModule`: `provideFusedLocationClient`.

## 6. Service Katmanı (app, 16 dosya)

| Sınıf | Tür | Görev |
|-------|-----|-------|
| `LocationSyncWorker` | `@HiltWorker CoroutineWorker` | 10km kontrol + reverse geocode + refresh + bildirim + wear sync + her halükarda widget update; `schedulePeriodic` (12sa, BATTERY_NOT_LOW), `executeNow` (expedited) |
| `PrayerNotificationWorker` | `@HiltWorker` | Tek vakit alarmı (`prayer_name/time/city_name/notification_id`) + widget update |
| `PrayerNotificationScheduler` | `@Singleton` | 6 vakit için OneTime worker (`prayer_{NAME}_{date}` tag), `cancelAllNotifications()` |
| `LocationTracker` | `@Singleton` | `getCurrentLocation/getLastKnownLocation/hasLocationPermission/hasBackgroundLocationPermission/registerPassiveLocationListener` (15dk, 10km PendingIntent) |
| `LocationPassiveReceiver` | BroadcastReceiver | → `LocationSyncWorker.executeNow(lat, lon)` |
| `WidgetMinuteReceiver` | BroadcastReceiver | Dakikalık tick: `updateAll + checkLocationChange + scheduleNextMinute` (AlarmManager RTC exact) |
| `NamazWidget` | GlanceAppWidget | `provideGlance`, Compact/Expanded/Error/Loading layout, `schedulePrayerTransitionAlarm`, `RefreshActionCallback` |
| `NamazWidgetUpdater` | object | Tüm glanceId update (`Log.d/e`) |
| `NamazWidgetReceiver` | GlanceAppWidgetReceiver | `ACTION_PRAYER_TIME_TRANSITION` handling |
| `NamazWidgetPrefs/ConfigureActivity` | object/Activity | Layout seçimi |
| `WearSyncManager` | `@Singleton` | `syncPrayerData/syncData` (DataClient `/namaz_prayer_data`, urgent, lokalize) |
| `WearPrayerData.kt` | DTO | `WearPrayerData(cityName/prayers/nextPrayer/kerahat/themeId/lat/lon/timestamp)` + `toJson/fromJson` |
| `QiblaSensorManager` | `@Inject` | `hasMagnetometer()`, `getOrientationEvents(): Flow<QiblaOrientation>` (sin/cos smoothing) |
| `GeocoderHelper` | `@Singleton` | `getLocationInfo(lat, lon)` (Geocoder tr, fallback DEFAULT) |
| `NotificationHelper` | object | Kanal + `showPrayerNotification()` + renk/ikon helper'ları |

## 7. UI Katmanı (app, ~35 dosya)

- `ui/state/`: `HomeUiState(Loading/Success/Error/PermissionRequired)`, `QiblaUiState(Loading/CompassActive/StaticDirection/Error/LocationRequired)`.
- `ui/theme/`: `AppThemeSelection` enum (11 tema: 0-3 sistem `darkTheme` takip eden dinamik çiftler; 4-10 sistem karanlık modundan bağımsız tek ve sabit palet render eden fixed-look temalar — Yumuşak Aydınlık, Tam Aydınlık, Saman Kâğıdı, Karanlık, Galaksi, Gün Batımı, Amber Gece) + `ThemeTokens` + light/dark & sabit `ColorScheme`'ler (`Color.kt`); `NamazTheme(dynamicColor=false, fixed-look desteği)` (`Theme.kt`); `prayerHeroGradient/prayerAmbientColor/countdownAccentColor` (`PrayerColors.kt`); `ExpressiveShapes/Type/AppThemeUtils`.
- `ui/components/`: `PrayerHeroCard` (+`CountdownDisplay`), `PrayerTimesList`, `PrayerIcon`, `CountdownTimer` (epoch recompute), `QiblaCompass` (shortest-path + glow), `QiblaDegreeDisplay` (doğruluk + kalibrasyon banner), `UpdateBanner/UpdateCheckButton`.
- `ui/screens/`: home (dashboard + `prayerTimesJob` race fix + prefs collect reload), calendar (ay navigasyonu), cityselection (400ms debounce + TR-uppercase), settings (bildirim/metod/mezhep/tema/dil), qibla (histeresis + titreşim + `QiblaMap` MapLibre GreatCircle), zikirmatik (preset + custom + tesbihat fazları).

`HomeViewModel` race fix: `prayerTimesJob` takip edilir, yeni load eskisini iptal eder; `_prayerTimes/_error/_nextPrayer/_dashboardData` sıfırlanır; `init` prefs dinler; `useMyLocation()` sadece `clearManualCity()` çağırır.

## 8. Util (7 dosya)

`DateTimeUtils` (formatter + hicri ay), `LocationMath` (mesafe + 10km eşik), `GreatCircleUtil` (küresel hat noktaları), `NotificationPermissionHelper`, `ContextExtensions` (alarmManager + izin), `UpdateChecker` (version.json + `isNewerThan`), `UpdateDownloader` (FileProvider + REQUEST_INSTALL_PACKAGES).

## 9. Manifest

İzinler (11): FINE/COARSE/BACKGROUND_LOCATION, INTERNET, POST_NOTIFICATIONS, VIBRATE, SCHEDULE_EXACT_ALARM + USE_EXACT_ALARM, REQUEST_INSTALL_PACKAGES, WRITE_EXTERNAL_STORAGE (maxSdk 28).

Bileşenler: `NamazApplication`, `MainActivity` (MAIN/LAUNCHER), `NamazWidgetReceiver` (APPWIDGET_UPDATE + TRANSITION + widget_info), `WidgetMinuteReceiver` (WIDGET_MINUTE_TICK, exported false), `LocationPassiveReceiver` (exported false), `NamazWidgetConfigureActivity` (APPWIDGET_CONFIGURE), WorkManagerInitializer remove (Hilt), FileProvider.
