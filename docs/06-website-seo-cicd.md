# 06 — Website, SEO, CI/CD

## 1. Website Yapısı (`website/`)

Canlı adres: `gorkemguray.github.io/Namaz`. Hibrit prerendering: her şehir için statik HTML (title/meta/JSON-LD/OG gömülü), vakit değerleri JS ile canlı doldurulur.

| Dosya/Dizin | Açıklama |
|-------------|----------|
| `index.html` / `en/index.html` | TR/EN ana sayfa (tanıtım + canlı widget) |
| `app.js` (764 satır) | SPA motoru: tema, version.json → APK linkleri, `?sehir=X` → `/sehir/X/` redirect, Aladhan+Nominatim+ipapi canlı vakit |
| `style.css` | Responsive (9 breakpoint) |
| `cities.json` | TR 976 + EN 983 kayıt (`param+priority`) |
| `generate_static.py` (539 satır) | Statik sayfa üretici |
| `sehir/{city}/index.html` | ~976 TR prerender sayfa |
| `en/city/{city}/index.html` | ~983 EN prerender sayfa |
| `sehirler/index.html`, `en/cities/index.html` | Alfabetik site haritası |
| `sitemap.xml` | Birleşik sitemap (`xhtml:link` alternates) |
| `robots.txt` | `Sitemap: .../sitemap.xml` |
| `version.json` | `1.0.4/4` + TR/EN notes |

Akış: `Googlebot → /sehir/istanbul/ → statik HTML (title/meta/h1/JSON-LD + window.PRERENDERED_CITY) → app.js API'den canlı vakitleri doldurur (--:-- → 04:30)`.

### Kritik adım — `generate_static.py`
Sitemap/şehir/şablon değişikliğinden sonra **mutlaka**:
```bash
python3 website/generate_static.py
```
Sitemap'lerden şehir listesini okur, her şehir için benzersiz HTML üretir. `deploy-website.yml` bunu otomatik çalıştırır (T-402); yerelde şablon test ederken manuel koşulabilir. Not: `release.yml` de `website/*` kopyalar — orada da üretim adımı gerekir (T-404).

URL: `/sehir/{city}/`, `/en/city/{city}/` (eski `?sehir=` redirect ile taşınır). SEO: benzersiz title/description/h1/canonical, OG/Twitter Card, JSON-LD (WebApplication+Place, sahte aggregateRating kaldırıldı), hreflang, GA4, lazy img, birleşik sitemap (`sitemap.xml`, `xhtml:link` alternates).

## 2. CI/CD (`.github/workflows/`)

### 2.1 `deploy-website.yml` (site)
Tetikleyici: `main` + `website/**` push. Süreç: `GorkemGuray/Namaz@gh-pages` klonla (yoksa orphan) → `version.json` yedekle/geri yükle → `website/*` kopyala → commit+push. Secret: `RELEASE_REPO_TOKEN`.

### 2.2 `release.yml` (uygulama)
Tetikleyici: `v*` tag push. Süreç: JDK 17 + Gradle cache → `versionName=tag-v`, `versionCode=tag sayısı` → `:app:assembleDebug :wear:assembleDebug -PciVersion*` → `Namaz-{v}-app/wear-debug.apk` rename → `softprops/action-gh-release` ile release → `version.json` üretip siteyle `gh-pages`'e push.

Release komutları:
```bash
git add -A
git commit -m "feat: yeni özellikler"
git push origin main
git tag -a v1.0.5 -m "Sürüm notları"
git push origin v1.0.5
```

### 2.3 CI-CD-PLAN.md Fark Notu
`CI-CD-PLAN.md` plan dokümanıdır, güncel gerçekle 3 farkı vardır: (1) wear `applicationId` planda `co.gorkem.namaz.wear`, gerçekte `co.gorkem.namaz`; (2) planda JDK 17 + `signingConfigs` taslağı, yerel derleme JDK 11; (3) versionCode stratejisi planda tag-sayısı/date alternatifli, gerçek workflow tag sayısı kullanır. Release öncesi bu dosyanın güncellenmesi önerilir (bkz. `08`).
