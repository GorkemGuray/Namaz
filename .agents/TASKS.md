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
| T-301 | Kıble kalibrasyon + manyetik bozulma yönlendirmeli dialog | P1 | ASSIGNED | worker-1 | `app/.../ui/screens/qibla/*`, `app/.../ui/components/QiblaDegreeDisplay.kt` | Bozulma durumunda uyarı + yönlendirme |

## Faz 4 — Release Hazırlığı

| ID | Başlık | Öncelik | Durum | Sahip | Kapsam (dosya) | Kabul |
|----|--------|---------|-------|-------|----------------|-------|
| T-401 | İmzalı release altyapısı (keystore docs + workflow eki) | P1 | PENDING | — | `.github/workflows/release.yml`, `*/build.gradle.kts` | Dry-run release yeşil |
| T-402 | `generate_static.py` adımını `deploy-website.yml`'e ekleme | P1 | PENDING | — | `.github/workflows/deploy-website.yml` | Site deploy'da statik sayfalar üretiliyor |
| T-403 | `release.yml` RELEASE_REPO_TOKEN guard denetimi (`if: env...` job-seviyesi env'siz) | P2 | PENDING | — | `.github/workflows/release.yml` | Guard gerçekten çalışıyor veya düzeltilmiş |

---
*Yeni görev ekleme: coordinator tabloya satır ekler, ID sıradaki numaradan verilir. Worker kendi görev açamaz.*
