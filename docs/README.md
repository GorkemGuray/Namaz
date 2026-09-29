# Namaz Vakitleri — Dokümantasyon İndeksi

> Son güncelleme: 29 Eylül 2026
> Proje kökü: `/Users/gorkem/Desktop/Android_Kurs/Namaz`
> Mevcut durum: `assembleDebug` (app + wear) başarılı, temel özellikler tamamlanmış durumda.

Bu klasör, daha önce dağınık halde bulunan bilgileri (README.md, AGENTS.md, CI-CD-PLAN.md, `docs/superpowers/`) tek bir tutarlı dokümantasyon setinde toplar ve eksik alanları tamamlar.

## Mevcut Dokümantasyon Durumu (Gap Analizi)

| Alan | Önceki durum | Bu set |
|------|--------------|--------|
| Genel bakış, kurulum, derleme | README.md'de iyi | `00-genel-bakis.md` içinde konsolide + doğrulandı |
| Mimari (Clean + MVVM + Hilt) | README + AGENTS'de özet | `01-mimari-ve-moduller.md` — paket bazında sınıf/fonksiyon envanteri |
| Veri kaynakları ve API'lar | AGENTS'de dağınık | `02-veri-kaynaklari-ve-akisi.md` — endpoint, DTO, cache-key, fallback zinciri |
| Arka plan, konum, widget, bildirim | README + AGENTS'de parçalı | `03-arka-plan-konum-widget-bildirim.md` — 3 yedekli yol + pil tablosu + zincir diyagramı |
| Wear OS | README'de özet | `04-wear-os.md` — Data Layer protokolü, standalone, Tile/Complication |
| Kıble, Zikirmatik, Takvim, Tema, i18n | Sadece AGENTS notları | `01` ve `02` içinde ilgili bölümler + `07` teknik borç |
| Test stratejisi / TDD | AGENTS'de 1 paragraf | `05-test-ve-kalite.md` — dosya envanteri, TDD kuralı, komutlar |
| Website + SEO | README'de iyi | `06-website-seo.md` — hibrit prerendering + kritik `generate_static.py` adımı |
| CI/CD | CI-CD-PLAN.md güncel değil (applicationId, JDK, versionCode farkları) | `06` içinde güncel workflow özeti + CI-CD-PLAN ile fark notu |
| Geliştirme planı / roadmap | Yok (sadece 2 superpowers planı) | `07-gelistirme-plani-roadmap.md` — fazlı plan |
| Bilinen sorunlar / tech debt | AGENTS'de 2 satır (duplicate, hardcoded) | `08-bilinen-sorunlar-tech-debt.md` — önceliklendirilmiş liste |

## Dosya Listesi

```
docs/
├── README.md                              ← bu dosya (indeks + gap analizi)
├── 00-genel-bakis.md                      ← ürün tanımı, özellikler, kurulum, konfigürasyon
├── 01-mimari-ve-moduller.md               ← Clean Architecture, app/wear paket envanteri, DI, DataStore, Room
├── 02-veri-kaynaklari-ve-akisi.md         ← 3 veri kaynağı, API referansı, repository fallback, cache
├── 03-arka-plan-konum-widget-bildirim.md  ← 3 yedekli konum yolu, widget tick, bildirim scheduler
├── 04-wear-os.md                          ← sync protokolü, standalone, Tile, Complication, tema
├── 05-test-ve-kalite.md                   ← unit test envanteri, TDD kuralları, manuel test (konum mock)
├── 06-website-seo-cicd.md                 ← website yapısı, prerendering, SEO, 2 workflow
├── 07-gelistirme-plani-roadmap.md         ← kısa/orta/uzun vadeli faz planı
└── 08-bilinen-sorunlar-tech-debt.md       ← duplicate, hardcoded fallback, CI-CD-PLAN farkları
```

## Nasıl Okunmalı

1. Projeye yeni başlayan: `00` → `01` → `02`.
2. Widget/konum/bildirim hatası çözen: `03`.
3. Saat tarafı çalışan: `04`.
4. Release alan / site güncelleyen: `06`.
5. Ne yapacağını planlayan: `07` + `08`.

## Kaynak Doğruluk Notu

- Tüm sınıf/fonksiyon adları Eylül 2026'daki gerçek kaynak kod taramasından alınmıştır.
- API base URL'leri `app/di/NetworkModule.kt` ve `wear/di/WearNetworkModule.kt` ile doğrulanmıştır.
- İzin listesi `app/src/main/AndroidManifest.xml` ile birebir eşleşir.
- CI-CD-PLAN.md içindeki eski bilgiler (wear applicationId, JDK 17, tag-sayısı versionCode) `08` içinde düzeltilmiştir — güncel gerçek `06` içindedir.
