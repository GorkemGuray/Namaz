# 08 — Bilinen Sorunlar ve Teknik Borç

## ✅ Kapatılanlar

### 1. Duplicate `PrayerNameHelper` (Wear) — ✅ ÇÖZÜLDÜ
- Tek dosyada birleştirildi (`wear/PrayerNameHelper.kt`: mapper + calculateNextPrayer + complication alarm).

### 2. Wear Hardcoded Fallback — ✅ ÇÖZÜLDÜ (T-234)
- `loadFromStandalone()` artık GPS → son DataStore konumu (`districtId` dahil) → İstanbul DEFAULT (9548) zincirini kullanır. `WearPrayerRepository` dataSource'a göre Aladhan veya İmsakiyem çağırır.

### 3. CI-CD-PLAN.md Güncel Değil — ✅ ÇÖZÜLDÜ (T-103)
- Wear `applicationId`, JDK (CI 17 / yerel 11+, bytecode Java 11), `versionCode` (tag sayısı), README rozetleri gerçekle eşitlendi.

### 4. Wear ↔ App Duplicate Kod — ✅ ÇÖZÜLDÜ (T-211/211b)
- `QiblaDirection/QiblaUseCase/Aladhan*` `:core`'a taşındı, wear kopyaları silindi.

### 5. `UserPreferencesSerializer` Ölü Kod — ✅ ÇÖZÜLDÜ (T-105)
- 23 Mayıs 2026'da silindi, referans kalmadı.

### 6. CitySelection API İhlali — ✅ ÇÖZÜLDÜ (T-212)
- ViewModel artık `ApiService` görmüyor; arama `PrayerTimeRepository` üzerinden.

### 7. Domain/Data Ters Bağımlılık — ✅ ÇÖZÜLDÜ (T-213)
- `NetworkResult` domain'de, `ZikirEntity` domain'e sızmıyor, Data katmanı service çağırmıyor.

## P1 — Kısa Vadede

### 8. Room Migration Stratejisi (v6)
- `NamazDatabase` v6, `exportSchema=true`, şemalar `app/schemas/` altında. v5→v6 geçişinde migration yazılmadı (bilinçli tercih: qada tablosu yeni, kullanıcı verisi kaybı yok).
- **Kural:** v7+ şema değişikliğinde `MigrationSpec` + registry kaydı zorunlu. `NamazDatabaseMigrationTest` bunu doğrular.

### 9. `generate_static.py` Manuel Adım
- `deploy-website.yml` bu adımı çalıştırmaz; unutulursa site/sitemap eskir. Çözüm: CI'ya ekle (07/E7).

### 10. Complication/Tile Null-State
- Veri yoksa `NoData`/mock (`AKSAM:20:33`) gösterilir. Çözüm: son cache + yükleniyor durumu.

### 11. Kandil Tablosu Yıllık Bakım
- `SpecialDays.kt` 2026-2027 sabit tablo. 2028 öncesi otomasyon veya güncelleme gerekli (07/E4).

## P2 — İzlenmeli

- School default'u DataStore'da 0 (Şafii). Lokal motor doküman notu eskiden Hanefi varsayımı yazıyordu — tek kaynak DataStore'dur, çelişki giderildi (02 §2.1).
- API 36 mock-konum zorunluluğu (`adb emu geo fix` çalışmaz) — bkz. `05` §4.
- `QiblaSensorManager` core'da stub, platformda gerçek impl — bilinçli ayrım; saf smoothing matematiğini core'a taşıma fırsatı duruyor.
- `REQUEST_INSTALL_PACKAGES` Play politika incelemesi gerektirir — gerekçe manifest'te yazılı, release öncesi tekrar değerlendirilmeli.
- `SpecialDayNotificationWorker` kademeli ses yok; gerçek ezan mp3'ü için 07/E1.
