Sen uzman bir Android ve Wear OS uygulama geliştiricisisin. Senden, modern Android geliştirme standartlarına (Modern Android Development - MAD) uygun, konum bazlı çalışan ve hem mobil hem de akıllı saat (Wear OS) desteği olan bir "Namaz Vakitleri" uygulaması geliştirmeni istiyorum.

Lütfen kodları yazarken Clean Architecture ve MVVM (Model-View-ViewModel) tasarım desenlerini kullan. Projeyi modüler bir yapıda, okunabilir ve sürdürülebilir bir şekilde inşa et.

### 1. Teknoloji Yığını (Tech Stack)
*   **Dil:** Kotlin
*   **Kullanıcı Arayüzü:** Jetpack Compose (Mobil için) ve Compose for Wear OS (Saat için)
*   **Mimari:** MVVM + Clean Architecture
*   **Ağ İşlemleri (Network):** Retrofit + OkHttp
*   **Asenkron İşlemler:** Kotlin Coroutines & Flow
*   **Bağımlılık Enjeksiyonu (DI):** Dagger Hilt
*   **Veri Saklama (Cache):** Preferences DataStore veya Room Database (Çevrimdışı kullanım için)
*   **Konum Servisleri:** Fused Location Provider Client (Google Play Services)
*   **Arka Plan İşlemleri:** WorkManager (Günlük vakit güncellemeleri ve bildirimleri ayarlamak için)
*   **API:** Aladhan API (veya eşdeğer ücretsiz bir namaz vakti API'si)

### 2. Mobil Uygulama Özellikleri (App Module)
*   **Konum İzinleri:** Uygulama ilk açıldığında kullanıcıdan konum izni (Location Permission) istemelisin. İzin verilmezse manuel şehir seçme ekranı sunulmalı veya varsayılan bir şehir atanmalıdır.
*   **Ana Ekran (Dashboard):** 
    *   Kullanıcının bulunduğu konumu (İl/İlçe) göstermeli.
    *   Bugünün namaz vakitlerini (İmsak, Güneş, Öğle, İkindi, Akşam, Yatsı) liste şeklinde göstermeli.
    *   Bir sonraki vakte kalan süreyi gösteren dinamik bir "Geri Sayım (Countdown)" aracı içermeli.
*   **Bildirimler (Notifications):** Vakit girdiğinde veya vakitten 15/30/45 dakika önce (kullanıcı ayarlarına bağlı olarak) alarm veya bildirim göndermeli.
*   **Çevrimdışı Desteği:** Alınan 1 aylık vakit verisi lokalde saklanmalı, böylece internet olmadığında da uygulama çalışabilmeli.

### 3. Wear OS Özellikleri (Wear Module)
*   **Mobil ile Senkronizasyon (Data Layer API):** Wear OS uygulaması, telefondaki konum ve vakit verilerini Wearable Data Layer API veya MessageClient aracılığıyla alabilmeli. Telefon yoksa kendi üzerinden konum alıp API'ye istek atabilecek bağımsız (standalone) bir yapı da desteklenmelidir.
*   **Ana Ekran:** Siyah arka plan üzerine (OLED dostu), büyük ve okunabilir fontlarla güncel vakti, sıradaki vakti ve kalan süreyi gösteren minimalist bir arayüz.
*   **Tile Desteği:** Kullanıcının saat kadranından sağa kaydırarak hızlıca bir sonraki vakti ve kalan süreyi görebileceği bir "Wear OS Tile" eklenmeli.
*   **Complications Desteği:** Kullanıcının mevcut saat kadranına (Watch Face) sıradaki vakti ekleyebilmesi için bir "Short Text Complication" oluşturulmalı.

### 4. Geliştirme Adımları (Bu sırayla ilerle)
1.  **Adım 1:** Proje iskeletini oluştur. `app` (Mobil) ve `wear` (Wear OS) modüllerini ayarla. Gerekli kütüphane bağımlılıklarını (dependencies) `build.gradle.kts` dosyalarına ekle.
2.  **Adım 2:** API katmanını (Retrofit) ve Veri Modellerini (Data Models) oluştur. Aladhan API kullanarak enlem/boylam üzerinden aylık vakitleri çeken repository katmanını yaz.
3.  **Adım 3:** Konum alma (Location Services) servisini ve izin yönetimini entegre et.
4.  **Adım 4:** Mobil için Jetpack Compose ile MVVM mimarisinde arayüzleri (ViewModel, State, UI) geliştir.
5.  **Adım 5:** WorkManager ve AlarmManager kullanarak bildirim altyapısını kur.
6.  **Adım 6:** Wear OS entegrasyonuna başla. Mobil uygulamadan saate veri aktarımını sağla.
7.  **Adım 7:** Wear OS için Compose kullanarak UI, Tile ve Complication bileşenlerini yaz.
