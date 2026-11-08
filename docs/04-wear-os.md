# 04 — Wear OS

## 1. Genel

- Kök: `wear/src/main/java/co/gorkem/namaz/wear/` (29 Kotlin dosyası).
- Navigasyon: `WearApp.kt` → `WearTheme(themeId)` + `AppScaffold` + `SwipeDismissableNavHost(home/qibla/zikirmatik)`. Eski HorizontalPager/chevron kaldırıldı.
- `implementation(project(":core"))` — duplicate kopyalar (QiblaDirection/QiblaUseCase/Aladhan*) T-211b’de silindi, core’dan import edilir. `QiblaSensorManager` platform impl wear’da durur.
- `applicationId` mobil ile eşit (`co.gorkem.namaz`) — Data Layer zorunluluğu.
- Manifest: `standalone=true`, Tile + Complication service, izinler FINE/COARSE_LOCATION + INTERNET + WAKE_LOCK, `android.hardware.type.watch`.

## 2. Senkronizasyon Protokolü

- Telefon `WearSyncManager.syncData(prayerTimes, location, themeId, getNext, getDashboard)` → `Wearable.DataClient` path `/namaz_prayer_data`, key `prayer_json`, urgent.
- İçerik: vakit listesi + konum (lat/lon) + `districtId` + `dataSource` (T-234) + next prayer + kerahat (`WearKerahatInfo`) + `themeId` + timestamp. Dil `LocaleManager` ile kullanıcının seçtiği dilde lokalize edilir.
- Saat `WearDataLayerService` (`OnDataChangedListener` + başlangıçta `dataItems` sorgusu): JSON'u `wear_prayer_prefs` DataStore'a cache'ler, complication update tetikler. Parse tek fonksiyonda: `parsePrayerData()` (T-221). `getLatestPrayerData()` Tile/Complication için suspend helper.
- İyileştirme: saat açılışta 2sn DataLayer bekler, sonra standalone'a düşer; standalone verisi sync verisini ezmez.
- DTO: `WearPrayerData(cityName/prayers/nextPrayer(targetTimeMillis)/kerahat/themeId/lat/lon/timestamp)` + `WearPrayerItem/WearNextPrayer/WearKerahatInfo` (Gson `fromJson`).

## 3. Standalone Mod

`WearPrayerRepository`: telefon yoksa saat kendi GPS'i + `dataSource`'a göre Aladhan veya İmsakiyem API ile çalışır (T-234). `WearViewModel`: DataLayer collect → 2sn bekle → `loadFromStandalone()`. Konum zinciri: saat GPS → telefondan gelen son DataStore konumu (`districtId` dahil) → İstanbul DEFAULT (9548). Countdown ViewModel'de değil, `WearCountdownHero` composable'ında `System.currentTimeMillis()` tick'i ile hesaplanır (T-220, polling kaldırıldı).

`WearNetworkModule`: OkHttp (BASIC log, 30sn) + `Retrofit(ezanvakti.imsakiyem.com/)` + ayrı `Retrofit(api.aladhan.com/v1/)`. `WearDataEntryPoint` (`@EntryPoint`) Tile/Complication'a repo erişimi sağlar.

## 4. UI

- `WearHomeScreen`: `TransformingLazyColumn` 2×3 grid + status header + `WearCountdownHero` (`System.currentTimeMillis()` tick, T-220), TimeText overlap korumalı.
- `WearQiblaScreen` (T-214: ince iskelet) + `WearQiblaCompass` + `WearQiblaDegreeDisplay` + `WearQiblaViewModel`: öncelik telefon koordinatı → saat GPS → İstanbul. Magnetometer varsa `CompassActive` (canvas glow ring + rotating Kaaba + hizalanmada haptik), yoksa `StaticDirection`.
- `WearZikirmatikScreen` + `WearZikirmatikViewModel` + `WearNamazTesbihatiStep`: serbest sayaç + rehberli tesbihat, progress halka, `wear_zikir_prefs` DataStore (mode/step/count/target).
- Tema: OLED siyah zeminli 4 palet (`themeId` telefondan sync) — `Theme.kt/Color.kt/Type.kt`.
- Kerahat: `WearKerahatBanner` aktif/yaklaşan kompakt gösterim.

## 5. Tile ve Complication

- `PrayerTileService` (Horologist `SuspendingTileService`): next prayer adı:saat + kalan dakika; null ise mock `AKSAM:20:33` preview.
- `PrayerComplicationService` (ShortText): `title`=vakit adı, `text`=saat; veri yoksa `NoData` + 5dk alarm; vakit geçişinde `scheduleComplicationUpdateAlarm()`.
- `PrayerCountdownComplicationService` (SHORT_TEXT/LONG_TEXT): `TimeDifferenceComplicationText(STOPWATCH)` native geri sayım ("İkindi 1sa 30dk"), OS işletir (pil dostu).
- `ComplicationUpdateReceiver`: `ComplicationDataSourceUpdateRequester` ile kadranı anında yeniler.
