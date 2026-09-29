# 00 — Genel Bakış

## 1. Ürün Tanımı

**Namaz Vakitleri**, Jetpack Compose + Clean Architecture + MVVM ile geliştirilmiş, konum bazlı çalışan premium namaz vakti uygulamasıdır. İki modülden oluşur:

| Modül | Package | minSdk / target | Sorumluluk |
|-------|---------|-----------------|------------|
| `app/` (mobil) | `co.gorkem.namaz` | 24 / 36 | Vakitler, kıble + harita, takvim, zikirmatik, Glance widget, bildirim, Wear sync kaynağı |
| `wear/` (saat) | `co.gorkem.namaz` | 30 / 36 | Grid vakitler, kıble, zikirmatik, Tile, 2 Complication; Data Layer + standalone |

> Kritik: Data Layer senkronizasyonu için iki modülün `applicationId` değeri eşitlenmiştir (`co.gorkem.namaz`). CI-CD-PLAN.md içindeki eski `co.gorkem.namaz.wear` değeri geçersizdir.

Ek bileşen: `website/` — GitHub Pages tanıtım + canlı vakit sitesi (`gorkemguray.github.io/Namaz-App`).

## 2. Özellik Özeti

### 2.1 Ana ekran (Times)
Hero card (geri sayım + sıradaki vakit + periyot + hicri tarih) + kerahat banner + zikir butonu + 6 vakit listesi. Uzun konum isimleri sadeleştirilir, taşma düzeltilmiştir. Geri sayım `System.currentTimeMillis()` ile her tick'te yeniden hesaplanır (lokal decrement yok — arka plandan dönüşte donma olmaz).

### 2.2 Zikirmatik (gelişmiş)
- **Kısa Namaz Tesbihatı**: Diyanet rehberine uygun, Arapça + Türkçe okunuş + anlam, 33'lük adım adım rehberli akış.
- **Serbest Sayaç**: hedefli (33/99/100/1000) veya sınırsız, dairesel ilerleme halkası, haptik geri bildirim (tıklamada kısa, hedefte uzun titreşim).
- Mobil: `TabRow + HorizontalPager`, `ModalBottomSheet` zikir seçici, `AnimatedContent` adım geçişi.
- Wear: dairesel ekrana optimize, TimeText çakışmasız tasarım.

### 2.3 Aylık Takvim
5 vakit + güneş sütunlu responsive tablo, bugünü vurgulama, sol/sağ okla ay geçişi.

### 2.4 Ayarlar
Bildirim master toggle + dakika seçimi (0/15/30/45), veri kaynağı (0 Aladhan / 1 İmsakiyem / 2 Lokal), hesaplama yöntemi, mezhep (school), tema (4 seçenek), dil (TR/EN). Alt kısımda `BuildConfig.VERSION_NAME` ile sürüm gösterimi + manuel güncelleme kontrol butonu.

### 2.5 Şehir seçimi
- `dataSource == 1` (İmsakiyem) → İmsakiyem ilçe arama (TR büyük harf normalizasyonlu).
- Diğer (Aladhan/Lokal) → Nominatim dünya araması.
- Son seçilenler: max 5, Gson JSON olarak DataStore'da, deduplicated.
- GPS seçimi: `clearManualCity()` → init block reload'u tetikler.

### 2.6 Kıble
Sensör varsa tam pusula (magnetometer + accelerometer, sin/cos smoothing α=0.90), yoksa statik yön. ±5° hizalanmada titreşim + glow + partikül. Histerezis: ≤4° aligned, ≥6.5° çıkış. MapLibre ile zümrüt Great Circle hattı, "Konumuma Git" (zoom 18).

### 2.7 Kerahat
3 pencere: Güneş→+45dk (kılınmaz), Öğle−45dk→Öğle (kılınmaz), Akşam−45dk→Akşam (sadece farz-ı ikindi). Banner "Kerahat Vakti" yazar (vakit ismi yok). Aktif = errorContainer, yaklaşan ≤60dk = tertiaryContainer. Widget ve Wear'a sync edilir.

### 2.8 Widget (Glance)
Native `Chronometer` (OS işletir — %0 CPU/wake lock), kerahat modunda kırmızı + hedef=kerahat bitişi, dakikalık `AlarmManager` tick, vakit geçiş alarmı, reboot sonrası yeniden zamanlama. Detay: `03` numaralı dosya.

### 2.9 Uygulama içi güncelleme
`UpdateChecker` → `version.json` endpoint'i, semantik karşılaştırma, haftada 1 otomatik throttle (`lastUpdateCheckMs`), ana ekranda `UpdateBanner` + ayarlarda `UpdateCheckButton`. "İndir" GitHub Releases'i açar.

## 3. Konfigürasyon

```
Gradle:      9.4.1
AGP:         9.4.0 (libs.versions.toml) — README'deki 9.2.1 eski değerdir
Kotlin:      2.2.10
KSP:         2.3.6 (libs.versions.toml) — AGENTS'deki 2.3.2 eski değerdir
Compose BOM: 2026.05.00
compileSdk / targetSdk: 36
app minSdk:  24, wear minSdk: 30
Java target: 11, desugar enabled
```

Ana bağımlılıklar (`gradle/libs.versions.toml`): Hilt 2.59.2, Retrofit 2.11.0, OkHttp 4.12.0, Gson 2.14.0, Coroutines 1.11.0, Navigation 2.9.8, play-services-location 21.3.0, DataStore 1.2.1, Room 2.8.4, WorkManager 2.11.2, Wear compose-material3 1.6.1 / tiles 1.6.0 / complications 1.3.0, play-services-wearable 20.0.1, Horologist 0.7.15, Glance 1.1.1, MapLibre Compose 0.12.1.

## 4. Kurulum ve Derleme

```bash
# Gereksinim: JDK 11+, Android SDK 36
git clone https://github.com/gorkem/Namaz.git
cd Namaz

./gradlew assembleDebug          # app + wear
./gradlew :app:assembleDebug     # sadece mobil
./gradlew :wear:assembleDebug    # sadece wear
./gradlew test                   # tüm unit testler
./gradlew :app:test              # sadece mobil
./gradlew :wear:test              # sadece wear
```

Android CLI tercih edilir (AGENTS.md kuralı): `android run --debug`, `android describe`, `android layout --pretty`, `android screen capture`, `android emulator list/start/stop`, `android sdk list/install`.

## 5. Kullanıcı Akışı (uçtan uca)

1. İlk açılış → konum izni istenir. İzin varsa FusedLocation → Geocoder → koordinat → aktif veri kaynağı (Aladhan varsayılan / İmsakiyem / Lokal). Yoksa CitySelection, varsayılan İstanbul/Silivri.
2. Ana ekran hero + kerahat + zikir + liste.
3. Zikirmatik: sağa kaydırma veya zikir butonu.
4. Takvim: ay okları.
5. Ayarlar: bildirim/metod/mezhep/tema/dil/kaynak.
6. Şehir seçimi kaynağa göre arama.
7. Kıble: pusula veya harita.
8. Telefon → Wear Data Layer sync (vakit + konum + next + kerahat + tema).
9. Wear standalone: telefon yoksa saat kendi GPS + API ile çalışır (GPS yoksa İstanbul fallback — bkz. `08` tech debt).
