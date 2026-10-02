# Görev Kuyruğu — Tek Doğruluk Kaynağı

> Kaynak: `docs/07-gelistirme-plani-roadmap.md` + `docs/08-bilinen-sorunlar-tech-debt.md`.
> Durum geçişleri yalnızca **coordinator** tarafından yapılır. Worker atama için coordinator'a başvurur.

Durumlar: `PENDING` → `ASSIGNED` → `IN_PROGRESS` → `REVIEW` → `DONE` / `CANCELLED`

## Faz 0 — Dokümantasyon ✅ DONE

| ID | Başlık | Durum | Sahip | Not |
|----|--------|-------|-------|-----|
| T-000 | Ayrıntılı dokümantasyon seti (`docs/00–08`) + koordinasyon yapısı | DONE | coordinator | Bu commit |

## Faz 1 — Teknik Borç Temizliği

| ID | Başlık | Öncelik | Durum | Sahip | Kapsam (dosya) | Kabul |
|----|--------|---------|-------|-------|----------------|-------|
| T-101 | Duplicate `PrayerNameHelper` birleştir (wear) | P0 | DONE | worker-1 | `wear/.../PrayerNameHelper.kt`, `wear/.../ui/PrayerNameHelper.kt` | Tek dosya, derleme + Wear smoke yeşil |
| T-102 | Wear hardcoded fallback kaldır (GPS → DataStore → DEFAULT) | P0 | DONE | worker-1 | `wear/.../ui/WearViewModel.kt` (+ test) | GPS kapalı senaryoda doğru konum, test yeşil |
| T-103 | CI-CD-PLAN + README rozet senkronu (applicationId, JDK, AGP/KSP) | P0 | DONE | worker-1 | `CI-CD-PLAN.md`, `README.md` | Gerçek değerlerle birebir (bkz. `docs/08` Bölüm 3) |
| T-104 | Room migration stratejisi (exportSchema / migration kararı + doküman) | P0 | DONE | worker-1 | `app/.../data/local/room/NamazDatabase.kt`, `docs/01-*` ilgili bölüm | Karar dokümante, test yeşil |
| T-105 | Ölü `UserPreferencesSerializer` temizliği | P1 | DONE | worker-1 | `app/.../data/local/datastore/UserPreferencesSerializer.kt` | Dosya zaten silinmiş (42d5fd93), referans yok — no-op, doküman senkronu yapıldı |

## Faz 2 — Güvenilirlik ve Pil

| ID | Başlık | Öncelik | Durum | Sahip | Kapsam (dosya) | Kabul |
|----|--------|---------|-------|-------|----------------|-------|
| T-201 | `WidgetMinuteReceiver` exact-alarm fallback matriksi (API 31/33/36) | P1 | DONE | worker-1 | `app/.../service/WidgetMinuteReceiver.kt` (+ test) | İzin kapalı emülatörde tick çalışıyor |
| T-202 | Complication/Tile null-state UX (son cache + yükleniyor) | P1 | DONE | worker-1 | `wear/.../tile/PrayerTileService.kt`, `wear/.../complication/*.kt` | Mock veri yok, cache gösterimi |

## Faz 3 — UX Parlatma

| ID | Başlık | Öncelik | Durum | Sahip | Kapsam (dosya) | Kabul |
|----|--------|---------|-------|-------|----------------|-------|
| T-301 | Kıble kalibrasyon + manyetik bozulma yönlendirmeli dialog | P1 | DONE | worker-1 | `app/.../ui/components/QiblaDegreeDisplay.kt` (+ strings/test) | Bozulma durumunda uyarı + yönlendirme |

## Faz 4 — Release Hazırlığı

| ID | Başlık | Öncelik | Durum | Sahip | Kapsam (dosya) | Kabul |
|----|--------|---------|-------|-------|----------------|-------|
| T-401 | İmzalı release altyapısı (keystore docs + workflow eki) | P1 | CANCELLED | — | `.github/workflows/release.yml`, `*/build.gradle.kts` | Ertelendi (kullanıcı kararı 2026-10-02): debug APK akışı devam; ihtiyaç olursa yeni görevle açılır |
| T-402 | `generate_static.py` adımını `deploy-website.yml`'e ekleme | P1 | DONE | worker-1 | `.github/workflows/deploy-website.yml` | Site deploy'da statik sayfalar üretiliyor |
| T-403 | `release.yml` RELEASE_REPO_TOKEN guard denetimi (`if: env...` job-seviyesi env'siz) | P2 | DONE | worker-1 | `.github/workflows/release.yml` | Hüküm: guard çalışıyor, değişiklik yok (resmi docs ile doğrulandı) |
| T-404 | `release.yml`'e `generate_static.py` üretim adımı (tag'de bayat site riski) | P1 | DONE | worker-1 | `.github/workflows/release.yml` | Tag release'inde site taze üretiliyor |
| T-405 | Fork'ta guard'sız "Create Release" adımı (secret yoksa erken patlar) | P2 | PENDING | — | `.github/workflows/release.yml` | Fork tag push'unda temiz skip veya bilinçli fail |

## Faz 5 — Tesbihat Sadeleştirme (kullanıcı kararı 2026-10-02: tek akış, sekmesiz; öncelik Uzun → Kısa)

| ID | Başlık | Öncelik | Durum | Sahip | Kapsam (dosya) | Kabul |
|----|--------|---------|-------|-------|----------------|-------|
| T-501 | Tesbihat ekranı tek akışa indir (TabRow/HorizontalPager kaldır, varsayılan Uzun) | P0 | ASSIGNED | worker-1 | `app/.../ui/screens/zikirmatik/ZikirmatikScreen.kt` (+ ViewModel varsayılanı, TR/EN string) | Sekmesiz tek ekran, varsayılan Uzun, Serbest erişilebilir |

---
*Yeni görev ekleme: coordinator tabloya satır ekler, ID sıradaki numaradan verilir. Worker kendi görev açamaz.*
