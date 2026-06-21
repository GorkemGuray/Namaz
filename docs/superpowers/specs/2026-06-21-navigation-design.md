# Namaz Uygulaması Navigasyon ve UX İyileştirmesi Tasarım Belgesi

**Tarih:** 2026-06-21  
**Yazar:** Antigravity  
**Konu:** 5 Sekmeli Alt Navigasyon (Bottom Navigation) Yapısına Geçiş

---

## 1. Amaç ve Gerekçeler

Uygulamanın mevcut navigasyon yapısı Vakitler, Takvim, Kıble ve Ayarlar olmak üzere 4 ana sekmeden oluşmaktadır. Ancak, önemli bir ana özellik olan Zikirmatik, Vakitler sekmesinin içinde bir `HorizontalPager` (sağa/sola kaydırılabilir sayfa) içerisine gizlenmiştir. 

Bu yapı aşağıdaki UX (Kullanıcı Deneyimi) problemlerine yol açmaktadır:
1. **Keşfedilebilirlik Sorunu:** Zikirmatik'in Vakitler sekmesi altında sola kaydırılarak açıldığı kullanıcılar tarafından kolayca keşfedilemeyebilir.
2. **Kafa Karıştırıcı Kontroller:** Ana ekranda sol üst köşede yer alan `FormatListBulleted` (Liste) ikonunun Zikirmatik'i açması sezgisel değildir.
3. **Yanlış Tetiklenmeler:** Kullanıcılar vakit listesini incelerken veya kaydırırken yanlışlıkla sola kaydırarak Zikirmatik ekranına geçebilirler.
4. **Tutarsız Bar Yapısı:** Zikirmatik ekranına geçildiğinde alt bar görünür kalmakta, ancak üst bar geri dön butonu (`ArrowBack`) barındırmaktadır. Bu durum, sekmeli navigasyon prensiplerine aykırıdır.

Bu tasarım belgesi, Zikirmatik özelliğini bağımsız bir 5. sekme olarak alt navigasyona taşıyarak navigasyon yapısını sadeleştirmeyi ve UX standartlarına uyumlu hale getirmeyi amaçlamaktadır.

---

## 2. Tasarım Detayları

### 2.1. Yeni Alt Navigasyon (Bottom Navigation) Yapısı
Alt bar sekmeleri ve sıralaması şu şekilde güncellenecektir:
1. **Vakitler** (Schedule) - Ana sayfa, namaz vakitleri gösterilir.
2. **Kıble** (Explore) - Kıble pusulası.
3. **Zikirmatik** (FilterVintage) - Zikirmatik & Tesbihat ekranı.
4. **Takvim** (CalendarMonth) - Aylık takvim tablosu.
5. **Ayarlar** (Settings) - Uygulama ayarları.

Zikirmatik sekmesinin ikonu olarak tesbih boncuklarını andıran `FilterVintage` seçilmiştir.

### 2.2. Ekran ve Akış Değişiklikleri
- **HorizontalPager Kaldırılması:** `NamazApp.kt` içerisindeki `HorizontalPager` kaldırılacak. `HomeScreen` ve `ZikirmatikScreen` doğrudan alt sekmeler üzerinden yüklenecek.
- **HomeScreen Üst Bar Güncellemesi:** Ana ekrandaki Zikirmatik kısayolu (sol üstteki liste ikonu) kaldırılacak, "Namaz Vakitleri" başlığı sola yaslanacak.
- **ZikirmatikScreen Üst Bar Güncellemesi:** Zikirmatik üst barındaki geri ok tuşu (`ArrowBack`) kaldırılacak. Zikirmatik artık bağımsız bir sekme olarak tam ekran görüntülenecek.
- **Dil Desteği:** `strings.xml` dosyalarına alt bar başlığı için gerekli çeviriler eklenecek:
  - Türkçe: `Zikirmatik`
  - İngilizce: `Tasbih`

---

## 3. Etkilenecek Dosyalar

1. [NamazApp.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/NamazApp.kt) (Sınıf güncellenecek, sekme listesine Zikirmatik eklenecek, Pager kaldırılacak).
2. [HomeScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/home/HomeScreen.kt) (TopAppBar'daki navigasyon ikonu ve Zikirmatik geçiş callback'i kaldırılacak).
3. [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt) (TopAppBar'daki geri dön butonu ve `onBackClick` callback parametresi kaldırılacak/temizlenecek).
4. [strings.xml](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/res/values/strings.xml) (`nav_zikirmatik` eklenecek).
5. [strings.xml (en)](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/res/values-en/strings.xml) (`nav_zikirmatik` eklenecek).

---

## 4. Doğrulama Planı

### 4.1. Derleme ve Statik Analiz
- Projenin başarıyla derlendiği doğrulanacak: `./gradlew assembleDebug`
- Kod düzeni ve import'lar kontrol edilecek.

### 4.2. Manuel Test Senaryoları
1. Uygulama açıldığında alt barda 5 adet sekme (Vakitler, Kıble, Zikirmatik, Takvim, Ayarlar) göründüğü doğrulanacak.
2. Zikirmatik sekmesine tıklandığında Zikirmatik ekranının açıldığı ve üst barında geri okunun olmadığı doğrulanacak.
3. Vakitler sekmesinde üst barın solunda herhangi bir liste/geri butonu olmadığı, başlığın sola yaslı durduğu doğrulanacak.
4. Sekmeler arası geçişlerin akıcı ve doğru ekranları yüklediği doğrulanacak.
5. Cihaz dili İngilizce yapıldığında Zikirmatik sekmesinin adı "Tasbih" olarak güncellendiği doğrulanacak.
