<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="128" height="128" alt="Namaz Vakitleri Logo"/>
  <h1>Namaz Vakitleri</h1>
  <p><b>Android ve Wear OS için Konum Bazlı Premium Namaz Vakitleri Uygulaması</b></p>
</div>

---

Namaz Vakitleri, modern Android (Jetpack Compose) mimarisiyle geliştirilmiş, son derece şık, şarj dostu ve kapsamlı bir namaz ve ezan vakti takip uygulamasıdır. Hem telefonunuzda hem de akıllı saatinizde (Wear OS) kusursuz bir senkronizasyon ile çalışır.

## ✨ Öne Çıkan Özellikler

### 🎨 4 Farklı Premium Tema
Telefonunuzdan seçtiğiniz tema, anında akıllı saatinize ve ana ekran widget'ınıza senkronize edilir:
* 🌿 **Emerald Oasis (Varsayılan):** Modern İslami adaçayı yeşili estetiği.
* 🌌 **Sapphire Night:** Derin ruhani gece mavisi paleti.
* 🏜️ **Amber Sands:** Sıcak miras ve altın sarısı parıltılar.
* ⚪ **Monochrome Platinum:** Ultra-minimalist yönetici (executive) tasarımı.

### ⌚ Wear OS (Akıllı Saat) Desteği
* **Senkronize Tasarım:** Telefondan seçtiğiniz temanın OLED (tam siyah) uyumlu harika bir versiyonu saatinizde anında belirir.
* **Kompakt Izgara Arayüzü:** 6 vakti alt alta kaydırmak yerine 2x3'lük grid (ızgara) yapısıyla tek ekranda görebilme. Wear OS'in yerleşik ekran kavislerine tam uyumlu (`TransformingLazyColumn`).
* **Akıcı Navigasyon (SwipeDismissableNavHost):** Sayfalar arasında ekranın solundan sağa kaydırarak sezgisel ve standartlara uygun geri dönüş imkanı. "Kıble Bulucu" ve "Zikirmatik" ekranlarına liste sonundaki butonlarla erişim.
* **Premium Kıble Bulucu (Qibla Finder):** Mobil uygulamadaki 3D hissiyatlı, animasyonlu, ışımalı (glow) ve titreşimli üst düzey pusula deneyimi birebir akıllı saate taşındı. Manyetometre yoksa statik modda çalışır.
* **Saat Kadranı (Complication):** Saatinizin ana ekranında sıradaki vakti veya işletim sistemi tabanlı (Native) **geri sayımı** (Örn: `İkindi -01:30`) saniye saniye görebilirsiniz.
* **Bağımsız (Standalone) Mod:** Telefonunuz yanınızda olmasa bile saat kendi GPS'i üzerinden vakitleri çekmeye devam eder.

### 🧭 Gelişmiş 3D Kıble Pusulası ve Harita
* **Manyetik Hassasiyet:** Cihaz sensörlerindeki parazitleri filtreleyen sin/cos tabanlı yumuşatılmış pürüzsüz azimut takibi. (Manyetik sapmalara ve kılıflara karşı uyarı sistemi)
* **Premium Animasyonlar:** Kabe'ye doğru hizalandığınızda cihazınız titrer, pusula yayı parlar (glow) ve özel parçacık efektleri devreye girer.
* **MapLibre Offline Harita:** Manyetometrenin zayıf olduğu yerlerde veya emin olmak istediğinizde, sokak ve bina seviyesindeki harita üzerinden doğrudan Kabe'ye çekilen çizgiyi (Great Circle) görebilirsiniz.

### 📿 Gelişmiş Zikirmatik ve İnteraktif Namaz Tesbihatı
Hem mobilde hem de Wear OS (akıllı saat) tarafında çalışan yeni zikirmatik deneyimi:
* **İnteraktif Kısa Namaz Tesbihatı:** Diyanet rehberine uygun olarak, duaların Türkçe okunuşunu, Arapçasını ve anlamlarını sırayla gösteren, tesbih kısımlarında otomatik 33'lük sayaç çektiren ve bittiğinde bitirme ekranı sunan interaktif akış.
* **Serbest Sayaç:** Sınırsız veya hedefe göre (33, 99, 100, 1000) zikir çekebilmeyi sağlayan serbest mod.
* **Hizalanmış ve Dairesel Ekran Uyumlu Tasarım (Wear OS):** Yuvarlak saat kadranında çakışmaları ve taşmaları önleyen, butonları orta hatta toplayan ve dial boyutunu dairesel yapıya göre ölçeklendiren kullanıcı dostu Wear OS tasarımı.
* **Titreşimli Geri Bildirim:** Her tıklamada kısa, hedefe ulaşıldığında ise uzun haptik geri bildirim (titreşim) ile ekrana bakmadan zikir çekebilme imkanı.

### 📡 3 Farklı Veri Kaynağı Seçeneği
Nasıl veri alacağınıza siz karar verin:
1. **Diyanet (İmsakiyem API):** Türkiye geneli resmi ilçe bazlı vakitler. Harf/Karakter duyarsız (Örn: *kırklareli*, *üsküdar* yazsanız dahi anlar) gelişmiş arama sistemi.
2. **Dünya Geneli (Aladhan API):** Varsayılan olarak tüm dünyada çalışır. Nominatim (OpenStreetMap) entegrasyonuyla sınırsız konum desteği.
3. **Çevrimdışı Mod (Lokal Hesaplama):** İnternet yoksa veya tercih ederseniz uygulama arka planda matematiksel formüllerle cihaz içinde çevrimdışı vakit hesabı yapar.

### 📱 Gelişmiş Glance Widget'ları
* Pilinizi asla tüketmeyen, doğrudan işletim sisteminin `Chronometer` yapısına entegre edilmiş yerel (Native) geri sayım sistemi (%0 CPU kullanımı).
* **Kerahat Uyarısı:** Kerahat vakitlerinde Widget kızarır ve sizi uyarır.

### 📅 Aylık Takvim Görünümü
* Alt menüden ulaşılabilen 5 günlük vakit ve güneş sütunlarıyla donatılmış akıllı tablo. Ay içinde gezinebilirsiniz ve içinde bulunulan gün otomatik olarak fosforlu (highlight) şekilde işaretlenir.

## 🛠️ Kurulum (Build & Run)

Uygulama tamamen güncel modern Android araçlarıyla yazılmıştır (Kotlin 2.2+, Gradle 9.4+, AGP 9.2+).

```bash
# Tüm projeyi (Mobil + Wear) derle
./gradlew assembleDebug

# Sadece Mobil Uygulamayı (Phone) derle
./gradlew :app:assembleDebug

# Sadece Saat Uygulamasını (Wear OS) derle
./gradlew :wear:assembleDebug
```

## 🧪 Testler (Birim Testleri)

Uygulamanın hem mobil hem de Wear OS tarafındaki kritik iş mantığı (business logic) kapsamlı birim testleri ile güvence altına alınmıştır:
* **Namaz Vakti Hesaplama Algoritması:** Çevrimdışı solar hesaplama motorunun Diyanet vakitleriyle (tolerans sınırları dahilinde) eşleştiği doğrulanır.
* **Kıble Yönü Hesaplamaları:** Farklı koordinat girdileri üzerinden Kabe'ye olan yön açılarının (bearing) doğruluğu test edilir.
* **Vakit Geçişleri ve Geri Sayım:** `GetNextPrayerUseCase` ve `GetPrayerDashboardUseCase` testleri ile günün her dilimindeki (imsak öncesi, gün ortası, yatsı sonrası) vakit geçişleri, kerahat süreleri ve kalan dakikalar doğrulanır.
* **Wear OS Dinamik Hesaplama:** Saat kadranındaki komplikasyonların ve saat ana ekranının vakit geçişlerinde otomatik olarak bir sonraki vakti hesaplamasını sağlayan yardımcı fonksiyonlar test edilir.

### Testleri Çalıştırma
```bash
# Tüm testleri (Mobil + Wear) çalıştır
./gradlew test

# Sadece mobil modül testlerini çalıştır
./gradlew :app:test

# Sadece wear modül testlerini çalıştır
./gradlew :wear:test
```

## 📐 Mimari ve Teknolojiler

Projede **Clean Architecture**, **MVVM** tasarım kalıpları ve Hilt ile Bağımlılık Enjeksiyonu (DI) kullanılmıştır. 

- **UI:** Jetpack Compose, Wear Compose Material 3, Glance (Widget)
- **Veritabanı ve Yerel Saklama:** Room (Offline Cache), DataStore
- **Ağ ve Concurrency:** Coroutines, Retrofit, OkHttp, Gson
- **Arka Plan İşlemleri:** WorkManager (Her vakit bildirimi için özelleştirilmiş arka plan görevleri)
- **Konum ve Harita:** FusedLocationProviderClient, MapLibre Compose
- **Kerahat Hesaplamaları:** 
  - *Güneş:* Güneş → Güneş +45dk (Namaz Kılınmaz)
  - *Öğle:* Öğle -45dk → Öğle (Namaz Kılınmaz)
  - *Akşam:* İkindi → Akşam -45dk (Sadece Farz-ı İkindi kılınabilir)

## 🚀 CI/CD Pipeline (GitHub Actions)

Uygulamanın sürekli entegrasyon ve dağıtım süreçleri GitHub Actions ile otomatikleştirilmiştir.

* **Otomatik Derleme:** Her yeni sürüm etiketi (`v*` tag) push'landığında mobil (`:app`) ve saat (`:wear`) modülleri için otomatik debug APK derlenir.
* **Dinamik Sürüm Yönetimi:** Sürüm numaraları ve kodları (`versionName` ve `versionCode`) push'lanan git tag'inden dinamik olarak çözümlenir.
* **Otomatik GitHub Release:** Derlenen APK'lar sürüm adlarıyla yeniden adlandırılarak otomatik olarak yeni bir GitHub Release sayfasına eklenir.

