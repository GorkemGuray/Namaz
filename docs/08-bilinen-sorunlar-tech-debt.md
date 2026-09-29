# 08 — Bilinen Sorunlar ve Teknik Borç

## P0 — Hemen Ele Alınmalı

### 1. Duplicate `PrayerNameHelper` (Wear)
- `wear/PrayerNameHelper.kt` (Context-tabanlı) + `wear/ui/PrayerNameHelper.kt` (@Composable) aynı işi yapar.
- Risk: çeviri tutarsızlığı, çift bakım. Çözüm: tek dosyaya birleştir (Faz 1).

### 2. Wear Hardcoded Fallback
- `WearViewModel.loadFromStandalone()` GPS yoksa İstanbul/Silivri districtId=9548'e düşer.
- Risk: İstanbul dışındaki kullanıcıya yanlış vakit. Çözüm: saat GPS → son DataStore konumu → DEFAULT sıralaması.

### 3. CI-CD-PLAN.md Güncel Değil — ✅ ÇÖZÜLDÜ (T-103)
- Wear `applicationId`, JDK (CI 17 / yerel 11+, bytecode Java 11), `versionCode` (yalnızca tag sayısı), README rozetleri (AGP 9.4.0, KSP 2.3.6, Gradle 9.6.0) ve workflow snippet'leri gerçek dosyalarla eşitlendi.
- Not: JDK 11 bir taban gereksinimdir (bytecode seviyesi); yerel derleme JDK 25 ile de çalışır.

## P1 — Kısa Vadede

### 4. Room Destructive Migration
- `NamazDatabase` v5, `exportSchema=false`, `fallbackToDestructiveMigration(true)`.
- Risk: schema değişiminde kullanıcı cache/zikir verisi sessizce silinir. Çözüm: export aç + migration yaz veya bilinçli tercih olarak dokümante et.

### 5. `UserPreferencesSerializer` Ölü Kod — ✅ ÇÖZÜLDÜ (T-105)
- Dosya 23 Mayıs 2026'da `42d5fd93` ile silinmiş (NO-OP stub, hiç kullanılmadı). Kodda referans yok (`grep` ile doğrulandı). AGENTS.md'deki bayat referanslar temizlendi.

### 6. `generate_static.py` Manuel Adım
- `deploy-website.yml` bu adımı çalıştırmaz; unutulursa site/sitemap eskir. Çözüm: CI'ya ekle veya pre-push hook.

### 7. Complication/Tile Null-State
- Veri yoksa `NoData`/mock (`AKSAM:20:33`) gösterilir. Çözüm: son cache + yükleniyor durumu.

## P2 — İzlenmeli

- `NamazDatabase` version AGENTS'de 4, kodda 5 yazıyor — doküman senkronu (bu set kodu esas aldı).
- `NetworkResult.Loading` AGENTS şemasında var, kodda sadece Success/Error — kullanılmıyorsa şemayı sadeleştir.
- School default'u DataStore'da 0 (Şafii) ama repo lokalde Hanefi notu düşmüş — varsayılan mezhep kararı netleştirilmeli.
- API 36 mock-konum zorunluluğu (`adb emu geo fix` çalışmaz) yeni geliştiricilere her seferinde anlatılıyor — bu dosya + `05` artık referans.
