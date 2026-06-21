# Alt Navigasyon ve UX İyileştirmesi Uygulama Planı

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Zikirmatik özelliğini HorizontalPager yapısından çıkartıp, 5 sekmeli alt navigasyon barında bağımsız bir sekme haline getirmek ve buna uygun UX düzenlemelerini yapmak.

**Architecture:** 
Alt navigasyondaki `BottomNavItem` mülkleri güncellenerek Zikirmatik 3. sıraya (ortaya) eklenir. `NamazApp.kt` içerisindeki `HorizontalPager` ve sayfa kaydırma mantığı tamamen kaldırılarak sekmeler arası doğrudan geçiş yapısı kurulur. `HomeScreen` ve `ZikirmatikScreen` üst barlarındaki sekmeler arası yönlendirme/geri butonları ve callback'leri temizlenir.

**Tech Stack:** Kotlin, Jetpack Compose, Material Design 3, Android SDK.

## Global Constraints
- Diller: Türkçe (varsayılan) ve İngilizce.
- Material Icons: `FilterVintage` (Zikirmatik ikonu olarak).
- Kod kalitesi: Clean Architecture ve MVVM prensiplerine sadık kalınacaktır.

---

### Task 1: Dil Kaynaklarının Eklenmesi (Localization)

**Files:**
- Modify: [strings.xml](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/res/values/strings.xml)
- Modify: [strings.xml (en)](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/res/values-en/strings.xml)

**Interfaces:**
- Consumes: Yok.
- Produces: `R.string.nav_zikirmatik` (Türkçe: "Zikirmatik", İngilizce: "Tasbih")

- [ ] **Step 1: Türkçe dil dosyasına yeni anahtarı ekle**
  [strings.xml](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/res/values/strings.xml) dosyasına şu satırı ekleyin:
  ```xml
  <string name="nav_zikirmatik">Zikirmatik</string>
  ```

- [ ] **Step 2: İngilizce dil dosyasına yeni anahtarı ekle**
  [strings.xml (en)](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/res/values-en/strings.xml) dosyasına şu satırı ekleyin:
  ```xml
  <string name="nav_zikirmatik">Tasbih</string>
  ```

- [ ] **Step 3: Commit**
  ```bash
  git add app/src/main/res/values/strings.xml app/src/main/res/values-en/strings.xml
  git commit -m "feat(i18n): add nav_zikirmatik string resource"
  ```

---

### Task 2: Alt Navigasyon ve Ekran Geçiş Mantığının Güncellenmesi

**Files:**
- Modify: [NamazApp.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/NamazApp.kt)

**Interfaces:**
- Consumes: `R.string.nav_zikirmatik`
- Produces: 5 sekmeli alt bar arayüzü ve ekran durum yönlendirmeleri.

- [ ] **Step 1: Gerekli ikon kütüphanelerini import et**
  [NamazApp.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/NamazApp.kt) başına şu satırları ekleyin:
  ```kotlin
  import androidx.compose.material.icons.filled.FilterVintage
  import androidx.compose.material.icons.rounded.FilterVintage
  ```

- [ ] **Step 2: BottomNavItem sealed class'ına Zikirmatik'i ekle**
  `BottomNavItem` tanımına Zikirmatik nesnesini ekleyin:
  ```kotlin
  data object Zikirmatik : BottomNavItem(
      titleRes = R.string.nav_zikirmatik,
      selectedIcon = Icons.Filled.FilterVintage,
      unselectedIcon = Icons.Rounded.FilterVintage
  )
  ```

- [ ] **Step 3: Sekme listesini güncelle**
  `NamazApp` fonksiyonu içindeki `items` listesini sıraya uygun olarak 5 sekmeye çıkartın:
  ```kotlin
  val items = listOf(
      BottomNavItem.Times,
      BottomNavItem.Qibla,
      BottomNavItem.Zikirmatik,
      BottomNavItem.Calendar,
      BottomNavItem.Settings
  )
  ```

- [ ] **Step 4: HorizontalPager'ı kaldır ve AnimatedContent when bloğunu güncelle**
  `AnimatedContent` içerisindeki `when(tab)` koşullarını şu şekilde güncelleyin:
  ```kotlin
  when (tab) {
      0 -> HomeScreen(
          modifier = Modifier.padding(paddingValues),
          onLocationClick = { showCitySheet = true }
      )
      1 -> QiblaScreen(
          modifier = Modifier.padding(paddingValues),
          onSetLocationClick = { showCitySheet = true }
      )
      2 -> co.gorkem.namaz.ui.screens.zikirmatik.ZikirmatikScreen(
          modifier = Modifier.padding(paddingValues)
      )
      3 -> CalendarScreen(
          modifier = Modifier.padding(paddingValues)
      )
      4 -> SettingsScreen(modifier = Modifier.padding(paddingValues))
  }
  ```

- [ ] **Step 5: Commit**
  ```bash
  git add app/src/main/java/co/gorkem/namaz/NamazApp.kt
  git commit -m "refactor(nav): integrate Zikirmatik tab and remove HorizontalPager"
  ```

---

### Task 3: HomeScreen Üst Barının UX Uyumlu Hale Getirilmesi

**Files:**
- Modify: [HomeScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/home/HomeScreen.kt)

**Interfaces:**
- Consumes: Yok.
- Produces: Güncellenmiş `HomeScreen` imzası ve üst bar arayüzü.

- [ ] **Step 1: HomeScreen parametrelerinden onZikirClick callback'ini kaldır**
  `HomeScreen` fonksiyon imzasını ve varsayılan değerini güncelleyin:
  ```kotlin
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun HomeScreen(
      viewModel: HomeViewModel = hiltViewModel(),
      modifier: Modifier = Modifier,
      onLocationClick: () -> Unit
  )
  ```

- [ ] **Step 2: TopAppBar içindeki navigationIcon'ı (Zikirmatik Butonu) kaldır**
  `TopAppBar` bileşenindeki `navigationIcon` parametresini tamamen silin (liste ikonu olan buton kaldırılacak).

- [ ] **Step 3: Commit**
  ```bash
  git add app/src/main/java/co/gorkem/namaz/ui/screens/home/HomeScreen.kt
  git commit -m "refactor(ux): remove zikirmatik shortcut icon from HomeScreen TopAppBar"
  ```

---

### Task 4: ZikirmatikScreen Üst Barının Güncellenmesi

**Files:**
- Modify: [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt)

**Interfaces:**
- Consumes: Yok.
- Produces: Güncellenmiş `ZikirmatikScreen` imzası ve geri ok tuşu olmayan bağımsız üst bar.

- [ ] **Step 1: ZikirmatikScreen parametrelerinden onBackClick callback'ini kaldır**
  `ZikirmatikScreen` fonksiyon imzasını güncelleyin:
  ```kotlin
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun ZikirmatikScreen(
      modifier: Modifier = Modifier,
      viewModel: ZikirmatikViewModel = hiltViewModel()
  )
  ```

- [ ] **Step 2: TopAppBar içindeki navigationIcon'ı (Geri Butonu) kaldır**
  `TopAppBar` bileşenindeki `navigationIcon` parametresini tamamen silin (sol üstteki geri dön ok butonu kaldırılacak).

- [ ] **Step 3: Commit**
  ```bash
  git add app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt
  git commit -m "refactor(ux): remove back button from ZikirmatikScreen TopAppBar"
  ```

---

### Task 5: Derleme ve Doğrulama

**Files:**
- Test: Tüm proje derleme ve testleri.

- [ ] **Step 1: Projeyi derle**
  Run: `./gradlew assembleDebug`
  Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Unit testleri çalıştır**
  Run: `./gradlew test`
  Expected: BUILD SUCCESSFUL, tüm testler pass

- [ ] **Step 3: Commit**
  ```bash
  git commit --allow-empty -m "test(verify): build and run unit tests successfully"
  ```
