# 07 — Geliştirme Planı (Roadmap)

> İlke: her faz TDD ile (önce test), her faz sonunda `./gradlew assembleDebug + test` yeşil. Mimari: Clean + MVVM + Hilt korunur.

## Faz 0 — Dokümantasyon Temeli (bu set) ✅
- `docs/00–08` oluşturuldu, gap analizi kapatıldı.
- Sonraki adım: `CI-CD-PLAN.md` güncelleme + README versiyon düzeltmeleri (AGP 9.4.0, KSP 2.3.6) — Faz 1'e alındı.

## Faz 1 — Teknik Borç Temizliği (1–2 hafta)
1. **Duplicate `PrayerNameHelper` birleştir**: `wear/PrayerNameHelper.kt` (Context) + `wear/ui/PrayerNameHelper.kt` (@Composable) → tek dosya. Test: derleme + Wear smoke.
2. **Wear hardcoded fallback kaldır**: `WearViewModel.loadFromStandalone()` İstanbul/Silivri 9548 → önce saat GPS (timeout'lu), yoksa son DataStore konumu, en son DEFAULT. Test: GPS kapalı senaryo.
3. **CI-CD-PLAN senkronu**: applicationId, JDK, versionCode gerçekle eşitle; README rozetleri düzelt.
4. **Room exportSchema kararı**: `exportSchema=false` + `fallbackToDestructiveMigration` → ya schema export aç ya da migration stratejisi dokümante et (veri kaybı riski).
5. **Kabul**: `./gradlew test` yeşil, lint temiz.

## Faz 2 — Güvenilirlik ve Pil (2–3 hafta)
1. `WidgetMinuteReceiver` 5dk backoff + exact-alarm izni yoksa inexact fallback matriksi (API 31/33/36) — test: izin kapalı emülatör.
2. `LocationSyncWorker` eksik-ay kontrolü genişlet (takvim önceden yükleme), `cleanupOldData` periyodu ayarlara bağla.
3. Bildirim scheduler: `minutesBefore` değişiminde reschedule doğrulama + Doze testi.
4. Complication/Tile null-state UX: `NoData` yerine son cache + "güncelleniyor" gösterimi.
5. Kabul: 24 saat pil ölçümü (mevcut tabloyla karşılaştır), logcat hatasız.

## Faz 3 — UX Parlatma (2–4 hafta)
1. Kıble kalibrasyon akışı: `needsCalibration` + manyetik bozulma (±20–70 μT dışı) için yönlendirmeli dialog.
2. Zikirmatik: `08`'deki tamamlanmamış superpowers planlarını kapat (HorizontalPager/BottomSheet/AnimatedContent doğrulama).
3. Takvim: yıl geçişi + hızlı bugün butonu.
4. Tema: `themeToColorProviders()` widget eşlemesini 4 tema için görsel test.
5. Kabul: `android layout --pretty` + screenshot incelemesi.

## Faz 4 — Release Hazırlığı (1–2 hafta)
1. Keystore + GitHub Secrets (`KEYSTORE_BASE64/PASSWORD/ALIAS/KEY_PASSWORD`) → imzalı release APK (CI-CD-PLAN Bölüm 6).
2. `release.yml`'e lint+test gate ekle (`:app:lintDebug`, `test`).
3. `version.json` şeması dokümante et + `UpdateDownloader` install-permission akışını Android 14+ ile test et.
4. Play Store yolu kararı: Gradle Play Publisher vs GitHub Action (CI-CD-PLAN 8.2).
5. Kabul: `v*` tag dry-run ile GitHub Release + site güncellemesi.

## Faz 5 — Uzun Vade (backlog)
- R8/ProGuard küçültme (`r8-analyzer` skill) + `isMinifyEnabled` açma.
- `navigation-3` geçişi, `edge-to-edge` insets denetimi, `appfunctions` kısayolları.
- Website `generate_static.py` adımını `deploy-website.yml`'e ekleme (manuel unutma riskini kaldırır).
- EN/TR string mirror denetimi otomasyonu.
- Wear Tile'da kerahat rozeti.

## Öncelik Matrisi

| İş | Etki | Efor | Öncelik |
|----|------|------|---------|
| Duplicate helper birleştirme | Orta | Düşük | P0 |
| Wear GPS fallback | Yüksek | Orta | P0 |
| CI-CD-PLAN senkronu | Düşük | Düşük | P0 |
| İmzalı release | Yüksek | Orta | P1 |
| Kalibrasyon UX | Orta | Orta | P1 |
| generate_static CI adımı | Orta | Düşük | P1 |
| R8 küçültme | Orta | Yüksek | P2 |
