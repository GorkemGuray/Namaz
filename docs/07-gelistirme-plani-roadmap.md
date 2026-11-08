# 07 — Geliştirme Planı (Roadmap)

> İlke: her faz TDD ile (önce test), her görev commit ile biter, her faz sonunda `./gradlew assembleDebug + test` yeşil. Mimari: Clean + MVVM + Hilt + `:core` korunur.

## Tamamlanan Fazlar (Kas 2026 — T-200 → T-234 ✅)

| Faz | Görevler | Durum |
|-----|----------|-------|
| **A — Risksiz temizlik** | T-200 string'ler, T-201 TR-locale, T-202 build borcu | ✅ 3 commit |
| **B — Mimari** | T-210 `:core` modül, T-211 taşıma, T-211b wear dedup, T-212 CitySelection repo, T-213 domain/data ayrışması, T-214 dosya parçalama | ✅ 5 commit |
| **C — Performans** | T-220 wear polling kaldırma, T-221 parse tekleştirme, T-222 widget sorgu+log, T-222b cache invalidation | ✅ 4 commit |
| **D — Yeni özellikler** | T-230 ezan sesi toggle, T-231/231b kaza takibi, T-232 ramazan, T-233 cuma/kandil, T-234 wear Diyanet | ✅ 5 commit |

Detay + kontrol modeli: `09-icra-plani-ve-kontrol.md` (tümü DONE).

## Faz E — Yeni İşler (öneri, öncelik sırasız)

### E1. Gerçek ezan sesi (T-230 devamı) — ✅ ÇÖZÜLDÜ
- `res/raw/ezan.mp3` altına CC0 lisanslı 30.5 saniyelik ezan kaydı eklendi (+256 KB). `PrayerAudioPlayer` ile ilk 10 saniye kademeli ses artışı sağlandı. Voice kanalına `setSound()` ve `AudioAttributes(USAGE_NOTIFICATION)` bağlandı. Ayarlar'a "Sesi dene" butonu eklendi.

### E2. R8/ProGuard küçültme
- `r8-analyzer` skill ile keep kuralları, `isMinifyEnabled=true` denemesi, release APK boyut ölçümü. Glance + Hilt + Room keep kurallarına dikkat.

### E3. Navigation-3 geçişi
- `navigation-3` skill: `NavHost` → Navigation 3 sahne modeli. Qada ekranını ayrı route yapma fırsatı (T-231b'de silindi, ihtiyaç olursa geri gelir).

### E4. Kandil takvimi otomasyonu — ✅ ÇÖZÜLDÜ
- `SpecialDays.kt` 2028 resmi Diyanet takvimi eklendi. `SpecialDaysTest` guard testi ile içinde bulunulan + sonraki yıl denetimi bağlandı (tablo biterse test kırmızı olur). Umm al-Qura spike'ında ±1 gün Diyanet sapması görüldüğü için tablo + guard yaklaşımı benimsendi.

### E5. Edge-to-edge + Wear Tile kerahat
- `edge-to-edge` skill ile insets denetimi; Wear Tile'a kerahat rozeti (08/P1-7'nin devamı).

### E6. EN/TR string mirror otomasyonu — ✅ ÇÖZÜLDÜ
- `.github/workflows/string-mirror-check.yml` + `scripts/check_string_mirror.py` ile PR ve push'larda XML string anahtar uyumu denetimi eklendi.

### E7. `generate_static.py` CI adımı — ✅ ÇÖZÜLDÜ
- `deploy-website.yml` ve `release.yml` adımlarında site dosyaları kopyalanmadan önce `generate_static.py` çalıştırılarak sitemap ve SEO sayfaları güncellenir.

## Öncelik Önerisi

| İş | Etki | Efor | Öneri |
|----|------|------|-------|
| E4 kandil otomasyonu | Yüksek (2028'e kadar zorunlu) | Düşük | P0 |
| E2 R8 küçültme | Orta | Orta | P1 |
| E1 gerçek ezan sesi | Yüksek | Orta | P1 |
| E6 string mirror CI | Düşük | Düşük | P1 |
| E7 generate_static CI | Düşük | Düşük | P1 |
| E5 edge-to-edge + tile | Orta | Orta | P2 |
| E3 navigation-3 | Orta | Yüksek | P2 |
