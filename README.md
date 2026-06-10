<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="128" height="128" alt="Namaz Vakitleri Logo"/>
  <h1>Namaz Vakitleri</h1>
  <p><b>Android &amp; Wear OS için Konum Bazlı Premium Namaz Vakitleri Uygulaması</b></p>

  [![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-%237F52FF?logo=kotlin)](https://kotlinlang.org)
  [![Compose](https://img.shields.io/badge/Compose%20BOM-2026.05-%234285F4?logo=jetpackcompose)](https://developer.android.com/compose)
  [![AGP](https://img.shields.io/badge/AGP-9.2.1-%233DDC84?logo=android)](https://developer.android.com/build)
  [![Gradle](https://img.shields.io/badge/Gradle-9.4.1-%2302303A?logo=gradle)](https://gradle.org)
  [![API](https://img.shields.io/badge/minSdk-24%20|%20targetSdk-36-brightgreen)](https://developer.android.com/tools/releases/platforms)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
</div>

---

**Namaz Vakitleri**, Jetpack Compose ve Clean Architecture ile geliştirilmiş, konum bazlı çalışan şık bir namaz vakti takip uygulamasıdır. Telefonunuzda ve Wear OS akıllı saatinizde anlık senkronizasyon ile kusursuz bir deneyim sunar. Pil dostu arka plan konum takibi, widget geri sayımı ve çoklu veri kaynağı desteği ile öne çıkar.

---

## ✨ Öne Çıkan Özellikler

### 🎨 Dört Premium Tema — Her Ekranda Senkronize
Telefondan seçtiğiniz tema anında akıllı saatinize ve ana ekran widget'ınıza yansır.

| Tema | Stil |
|------|------|
| 🌿 **Emerald Oasis** *(Varsayılan)* | Modern İslami adaçayı yeşili |
| 🌌 **Sapphire Night** | Derin ruhani gece mavisi |
| 🏜️ **Amber Sands** | Sıcak altın sarısı ve miras tonları |
| ⚪ **Monochrome Platinum** | Ultra-minimalist executive tasarım |

### ⌚ Wear OS — Telefon Olmadan da Çalışır
- **Gerçek Zamanlı Senkronizasyon:** Telefondaki tema, dil ve vakit bilgileri Data Layer API ile saate anında iletilir.
- **OLED Uyumlu Temalar:** Tüm temaların siyah arka planlı, yüksek kontrastlı Wear OS varyantları.
- **2×3 Izgara Arayüz:** `TransformingLazyColumn` ile altı vakti tek ekranda gösteren kavisli grid tasarımı.
- **SwipeDismissableNavHost:** Wear OS standartlarına uygun sola kaydırarak geri dönüş.
- **Kıble Pusulası:** Mobildeki 3D animasyonlu, ışımalı (glow) ve titreşimli pusula deneyimi saatte birebir.
- **Komplikasyon (Complication):** Saat kadranında işletim sistemi seviyesinde (native) geri sayım — `"İkindi 1sa 30dk"`.
- **Bağımsız (Standalone) Mod:** Telefon yanınızda olmasa bile saat kendi GPS'i üzerinden API'den vakitleri çeker.

### 🧭 Gelişmiş Kıble Pusulası ve Harita
- **Sin/Cos Tabanlı Yumuşatma:** Manyetik parazitleri filtreleyen, 360°→0° sarmalamasında titremeyi (glitch) engelleyen pürüzsüz pusula takibi.
- **Hizalama Efektleri:** Kabe'ye ±5° yaklaştığınızda cihaz titreşir, pusula halkası parlar (glow), parçacık efektleri devreye girer.
- **Histerezisli Algılama:** ≤4° içinde aligned, ≤6.5° üstünde aligned'dan çıkar — titremeyi önler.
- **MapLibre Harita:** Zümrüt yeşili Great Circle hattı ile Kabe yönünü sokak/bina seviyesinde haritada görselleştirme.
- **Manyetometre Yoksa:** Statik yön + derece gösterimi ile çalışmaya devam eder.

### 📿 Gelişmiş Zikirmatik ve Namaz Tesbihatı
Hem mobilde hem Wear OS'te kullanılabilir:

- **İnteraktif Namaz Tesbihatı:** Diyanet rehberine uygun, duaların Türkçe okunuşu, Arapçası ve anlamları ile 33'lük adım adım rehberli tesbih akışı.
- **Serbest Sayaç:** Hedefli (33/99/100/1000) veya sınırsız zikir çekme. Dairesel ilerleme halkası.
- **Haptik Geri Bildirim:** Her tıklamada kısa, hedefe ulaşıldığında uzun titreşim — ekrana bakmadan zikir çekme imkanı.
- **Dairesel Ekran Uyumlu (Wear OS):** Çakışmaları önleyen, butonları orta hatta toplayan optimize tasarım.

### 📡 Üç Bağımsız Veri Kaynağı
| # | Kaynak | Kapsam | İnternet |
|---|--------|--------|:---:|
| 1 | **Diyanet (İmsakiyem API)** | Türkiye — resmi ilçe bazlı | ✓ |
| 2 | **Aladhan API** *(Varsayılan)* | Dünya geneli — koordinat bazlı | ✓ |
| 3 | **Lokal Hesaplama** | Cihaz içi matematiksel | ✗ |

Türkçe karakter ve büyük/küçük harf sorunları çözülmüştür (`kırklareli`, `üsküdar`, `kadıköy` yazsanız dahi bulur). Nominatim (OpenStreetMap) entegrasyonu ile dünya geneli konum arama desteği.

### 📱 Glance Widget — Pil Dostu Geri Sayım
- **Native Chronometer:** Android `Chronometer` widget'ı ile işletim sistemi seviyesinde geri sayım. Launcher/SystemUI tarafından işletilir — uygulama uyurken **%0 CPU, %0 wake lock**. Doze/AppStandby geçirmez.
- **Kerahat Uyarısı:** Kerahat vaktinde widget kırmızıya döner, chronometer hedefi kerahat bitişine ayarlanır.
- **Dakikalık Güncelleme:** `AlarmManager.setExactAndAllowWhileIdle()` ile her dakika başı `:00`'da yenilenir. Reboot sonrası otomatik yeniden zamanlanır.
- **Vakit Geçiş Alarmı:** Widget, bir sonraki namaz vaktinde veya kerahat başlangıcında kendini otomatik günceller.

### 📅 Aylık Takvim
Beş günlük vakit ve güneş sütunlarıyla donatılmış, içinde bulunulan günü belirgin şekilde vurgulayan responsive aylık tablo. Sol/sağ oklar ile aylar arası geçiş.

---

## 🔋 Arka Plan Konum ve Pil Optimizasyonu

Uygulama, konum değişikliğini algılamak için **üç yedekli mekanizma** kullanır. Hiçbiri pil ömründen ödün vermez:

| Yol | Mekanizma | Tetikleyici | Pil Maliyeti | "Sadece kullanırken" izniyle |
|-----|-----------|-------------|:---:|:---:|
| 1. `WidgetMinuteReceiver` | `lastLocation` (önbellek) → dakikada bir | 10 km aşılınca `LocationSyncWorker` | ~0 | ✅ |
| 2. `LocationSyncWorker` | Periyodik (12 saat) | Zamanlanmış + `BATTERY_NOT_LOW` kısıtı | 2-3 sn CPU + 1 HTTP | ✅ |
| 3. `LocationPassiveReceiver` | PendingIntent (10 km) | 10 km displacement + 15 dk interval | Düşük (pasif) | ❌ * |

\* *Sadece `ACCESS_BACKGROUND_LOCATION` izni verilmişse çalışır. Diğer iki yol her durumda aktiftir.*

**getCurrentLocation fallback ve 5 dakika backoff:** `FusedLocationClient.lastLocation` sistem tarafından önbelleklenir ve neredeyse her zaman anında döner. Nadiren null olduğu durumlarda `getCurrentLocation()` denenir; başarısız olursa bir sonraki deneme için 5 dakika beklenir. Böylece GPS tekrar tekrar uyandırılmaz.

**Widget güncelleme zinciri:**
```
Konum 10km değişti
    → setLastAutoLocation()       [widget GÜNCELLENMEZ — erken güncelleme önlendi]
    → refreshPrayerTimes()         [API veya lokal hesaplama]
    → NamazWidgetUpdater.update()  [her zaman çağrılır, API hatasında bile]
    → NamazWidget.provideGlance()  [DB'den okur, widget'ı render eder]
    → WearSyncManager.syncData()   [sadece API başarılıysa Wear'a gönderilir]
```

---

## 🧪 Test Kapsamı

Uygulamanın kritik iş mantığı kapsamlı birim testleri ile güvence altındadır:

| Test Paketi | Kapsam |
|-------------|--------|
| `PrayerTimeCalculatorTest` | Lokal hesaplama motoru — İstanbul, Ankara, Mayıs, Ocak, Aralık, Haziran için Diyanet saatleriyle karşılaştırmalı (tolerans ±10dk) |
| `QiblaUseCaseTest` | Kıble yön açısı — İstanbul (~151.6°), NYC (~58.5°), Sydney (~277.5°), Kabe (0°), kardinal yönler |
| `GetNextPrayerUseCase` | Vakit geçişleri ve kalan dakika hesaplamaları |
| `GetPrayerDashboardUseCase` | Periyot tespiti, kerahat pencereleri, aktif/yaklaşan kerahat mantığı |
| Wear modül testleri | Komplikasyon hesaplamaları, saat ana ekranı vakit geçişleri |

```bash
./gradlew test                 # Tüm testler
./gradlew :app:test            # Sadece mobil
./gradlew :wear:test           # Sadece Wear OS
```

---

## 🛠️ Kurulum ve Derleme

```bash
# Gereksinimler: JDK 11+, Android SDK 36
git clone https://github.com/gorkem/Namaz.git
cd Namaz

# Tüm projeyi derle (Mobil + Wear OS)
./gradlew assembleDebug

# Sadece mobil
./gradlew :app:assembleDebug

# Sadece Wear OS
./gradlew :wear:assembleDebug
```

### Emülatörde Konum Testi

> ⚠️ **API 36 emülatörde `adb emu geo fix` çalışmaz.** OK döner ama konumu güncellemez. Yerine mock location provider kullanın:

```bash
# Mock provider kurulumu (bir kere)
adb shell appops set com.android.shell android:mock_location allow
adb shell cmd location providers add-test-provider gps --requiresSatellite
adb shell cmd location providers set-test-provider-enabled gps true

# Konum ayarlama
adb shell cmd location providers set-test-provider-location gps --location 41.0082,28.9784

# Widget dakikalık tick'ini manuel tetikleme
adb shell am broadcast \
  -a co.gorkem.namaz.WIDGET_MINUTE_TICK \
  -n co.gorkem.namaz/.service.WidgetMinuteReceiver \
  -f 0x10

# Logcat izleme
adb logcat -d | grep -E "WidgetMinuteReceiver|NamazWidgetUpdater|LocationSyncWorker"
```

---

## 📐 Mimari

```
┌─────────────────────────────────────┐
│  UI Layer (Compose Screens)         │  ← ui/screens/, ui/components/
│  ViewModel + UiState                │
├─────────────────────────────────────┤
│  Domain Layer                       │
│  UseCase → Repository Interface     │  ← domain/usecase/, domain/repository/
│  Domain Models                      │  ← domain/model/
├─────────────────────────────────────┤
│  Data Layer                         │
│  RepositoryImpl → API + Local       │  ← data/repository/
│  Remote (Retrofit 3 API) + DTO      │  ← data/remote/
│  Local (Room + DataStore)           │  ← data/local/
├─────────────────────────────────────┤
│  DI (Dagger Hilt)                   │  ← di/
│  Services (Location, Notification)  │  ← service/
└─────────────────────────────────────┘
```

| Bileşen | Teknoloji |
|---------|-----------|
| UI | Jetpack Compose, Wear Compose Material 3, Glance (Widget) |
| DI | Dagger Hilt 2.59.2 |
| Network | Retrofit 2.11, OkHttp 4.12, Gson 2.14 |
| Async | Kotlin Coroutines 1.11, Flow |
| Database | Room 2.8.4 (çevrimdışı cache) |
| Storage | DataStore Preferences 1.2.1 |
| Background | WorkManager 2.11.2, AlarmManager |
| Location | FusedLocationProviderClient 21.3.0 |
| Map | MapLibre Compose 0.12.1 |
| Wear | Compose Material 3, Tiles, Complications, Horologist |
| CI/CD | GitHub Actions (tag bazlı otomatik build + release) |

---

## 🚀 CI/CD (GitHub Actions)

`v*` formatındaki git tag'leri push'landığında otomatik olarak:
1. Tag adından `versionName` ve `versionCode` türetilir
2. Mobil (`:app`) ve Wear OS (`:wear`) debug APK'ları derlenir
3. APK'lar `Namaz-{version}-app-debug.apk` / `Namaz-{version}-wear-debug.apk` olarak adlandırılır
4. GitHub Release sayfasına otomatik yüklenir

---

## 📄 Lisans

MIT © [Görkem](https://github.com/gorkem)
