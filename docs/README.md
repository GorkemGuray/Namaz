# Namaz Vakitleri — Dokümantasyon İndeksi

> Son güncelleme: Kasım 2026 (T-200 → T-234 sonrası kapsamlı revizyon)
> Proje kökü: `/Users/gorkem/Desktop/Android_Kurs/Namaz`
> Mevcut durum: `assembleDebug` (app + wear + core) başarılı, 30 test dosyası yeşil.

Bu klasör, daha önce dağınık halde bulunan bilgileri (README.md, AGENTS.md, CI-CD-PLAN.md, `docs/superpowers/`) tek bir tutarlı dokümantasyon setinde toplar ve eksik alanları tamamlar.

## Mevcut Dokümantasyon Durumu (Gap Analizi)

| Alan | Önceki durum | Bu set |
|------|--------------|--------|
| Genel bakış, kurulum, derleme | README.md'de iyi | `00-genel-bakis.md` içinde konsolide + doğrulandı |
| Mimari (Clean + MVVM + Hilt) | README + AGENTS'de özet | `01-mimari-ve-moduller.md` — paket bazında sınıf/fonksiyon envanteri |
| Veri kaynakları ve API'lar | AGENTS'de dağınık | `02-veri-kaynaklari-ve-akisi.md` — endpoint, DTO, cache-key, fallback zinciri |
| Arka plan, konum, widget, bildirim | README + AGENTS'de parçalı | `03-arka-plan-konum-widget-bildirim.md` — 3 yedekli yol + pil tablosu + zincir diyagramı |
| Wear OS | README'de özet | `04-wear-os.md` — Data Layer protokolü, standalone, Tile/Complication |
| Kıble, Zikirmatik, Takvim, Tema, i18n | Sadece AGENTS notları | `01` içinde ilgili bölümler |
| Kaza takibi, Ramazan, Özel gün, Ezan sesi | Yok (T-230→T-234 yeni) | `01` + `02` + `03` + `00` §2.10-2.13 |
| Paylaşılan `:core` modülü | Yok (T-210/211 yeni) | `01` §1 + AGENTS.md |
| Test stratejisi / TDD | AGENTS'de 1 paragraf | `05-test-ve-kalite.md` — 30 dosya envanteri, TDD + commit kuralı |
| Website + SEO | README'de iyi | `06-website-seo.md` — hibrit prerendering + kritik `generate_static.py` adımı |
| CI/CD | CI-CD-PLAN.md güncel değil (applicationId, JDK, versionCode farkları) | `06` içinde güncel workflow özeti + CI-CD-PLAN ile fark notu |
| Geliştirme planı / roadmap | Yok (sadece 2 superpowers planı) | `07-gelistirme-plani-roadmap.md` — fazlı plan |
| Bilinen sorunlar / tech debt | AGENTS'de 2 satır (duplicate, hardcoded) | `08-bilinen-sorunlar-tech-debt.md` — önceliklendirilmiş liste |

## Dosya Listesi

```
docs/
├── README.md                              ← bu dosya (indeks + gap analizi)
├── 00-genel-bakis.md                      ← ürün tanımı, özellikler, kurulum, konfigürasyon
├── 01-mimari-ve-moduller.md               ← Clean Architecture, app/wear/core envanteri, DI, DataStore, Room v6
├── 02-veri-kaynaklari-ve-akisi.md         ← 3 veri kaynağı, API referansı, repository fallback, cache
├── 03-arka-plan-konum-widget-bildirim.md  ← 3 yedekli konum yolu, widget tick, bildirim scheduler
├── 04-wear-os.md                          ← sync protokolü, standalone, Tile, Complication, tema
├── 05-test-ve-kalite.md                   ← 30 test envanteri, TDD + commit kuralı, manuel test
├── 06-website-seo-cicd.md                 ← website yapısı, prerendering, SEO, 2 workflow
├── 07-gelistirme-plani-roadmap.md         ← tamamlanan fazlar + Faz E yeni iş önerileri
├── 08-bilinen-sorunlar-tech-debt.md       ← kapatılanlar + güncel P1/P2
└── 09-icra-plani-ve-kontrol.md             ← ARŞİV: T-200→T-234 görev detayları (tamamlandı)
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
