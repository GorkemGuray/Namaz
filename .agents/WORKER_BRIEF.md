# Worker Brifingi — Göreve Başlamadan Önce Oku

> Bu dosyayı okumadan kod yazmaya başlama. Coordinator sana bir `T-XXX` görev kartı verecek; kart + bu brifing + referans doküman, ihtiyacın olan her şeydir.

## 1. Proje Özeti (30 saniyede)

Konum bazlı Namaz Vakitleri uygulaması: `app/` (mobil, `co.gorkem.namaz`, minSdk 24) + `wear/` (saat, minSdk 30). Clean Architecture + MVVM + Hilt. Detay: `docs/00-genel-bakis.md`, `docs/01-mimari-ve-moduller.md`.

## 2. Mutlaka Uyulacaklar

1. **Kapsam:** Görev kartındaki `Dosyalar` listesi dışına çıkma. Başka dosya gerekiyorsa dur, coordinator'a sor.
2. **TDD:** İş mantığı yazmadan önce testi yaz/güncelle. Zaman bağımlı kodda clock'u parametre al (`now: LocalTime = ...`). Testler: `app/src/test/`, `wear/src/test/`.
3. **Commit yapma, push yapma.** Değişiklikleri working tree'de bırak, rapor yaz.
4. **Yorum yazma** (repo konvansiyonu). Kod kendi kendini anlatmalı.
5. **`dynamicColor = false`** — tema kodunda Monet/dinamik renk kullanma.
6. Türkçe vakit isimleri: `imsak, gunes, ogle, ikindi, aksam, yatsi`.

## 3. Komutlar

```bash
./gradlew :app:assembleDebug     # mobil derleme
./gradlew :wear:assembleDebug    # wear derleme
./gradlew test                   # tüm unit testler (görev sonunda zorunlu)
```

Emülatörde konum mock'lamak gerekirse: `docs/05-test-ve-kalite.md` Bölüm 4.

## 4. Referanslar (ihtiyaca göre oku)

| Konu | Dosya |
|------|-------|
| Veri kaynakları, API, cache, fallback | `docs/02-veri-kaynaklari-ve-akisi.md` |
| Konum, widget, bildirim | `docs/03-arka-plan-konum-widget-bildirim.md` |
| Wear sync, Tile, Complication | `docs/04-wear-os.md` |
| Protokol (roller, rapor formatı) | `.agents/COORDINATION.md` |

## 5. Bitince Ne Yapılacak

`.agents/COORDINATION.md` Bölüm 5'teki **Rapor formatını** aynen kullan, coordinator'a tek mesajda döndür. Karar verme, merge etme, yeni görev açma — hepsi coordinator'ın işi.
