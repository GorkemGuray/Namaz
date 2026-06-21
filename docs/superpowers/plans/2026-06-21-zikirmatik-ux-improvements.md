# Zikirmatik UI/UX İyileştirmeleri Uygulama Planı

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Zikirmatik ve Namaz Tesbihatı ekranlarında HorizontalPager sekme geçişi, ModalBottomSheet zikir seçici ve adım geçiş animasyonlarının eklenmesi.

**Architecture:** 
- ZikirmatikScreen'deki `TabRow` seçimi `pagerState` ile ilişkilendirilir ve alt içerik `HorizontalPager` içine taşınır.
- Zikir seçimi dropdown menüsü yerine şık bir `ModalBottomSheet` bileşeni yerleştirilir. Özel Zikir Ekleme aksiyonu da bu bottom sheet içerisine gömülür.
- `NamazTesbihatiFlow` içerisindeki adımlar arası geçişler `AnimatedContent` ile yatay kaydırma animasyonlu hale getirilir.

**Tech Stack:** Kotlin, Jetpack Compose, Material Design 3, Android SDK.

## Global Constraints
- Material Design 3 UI bileşenleri kullanılacaktır (`ModalBottomSheet`, `rememberModalBottomSheetState`).
- Kod kalitesi: Clean Architecture ve MVVM prensiplerine sadık kalınacaktır.

---

### Task 1: Tab Row ve HorizontalPager Entegrasyonu

**Files:**
- Modify: [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt)

**Interfaces:**
- Consumes: Yok.
- Produces: `HorizontalPager` tabanlı Zikirmatik ve Namaz Tesbihatı sekmeleri.

- [ ] **Step 1: Gerekli pager ve coroutine import'larını ekle**
  [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt) dosyasına şu import'ları ekleyin:
  ```kotlin
  import androidx.compose.foundation.pager.HorizontalPager
  import androidx.compose.foundation.pager.rememberPagerState
  import androidx.compose.runtime.rememberCoroutineScope
  import kotlinx.coroutines.launch
  ```

- [ ] **Step 2: ZikirmatikScreen içinde pagerState ve coroutineScope tanımla**
  `ZikirmatikScreen` metodu başında `pagerState` ve `coroutineScope` tanımlayın ve eski `selectedTab` durumunu kaldırın:
  ```kotlin
  val pagerState = rememberPagerState(pageCount = { 2 })
  val coroutineScope = rememberCoroutineScope()
  ```

- [ ] **Step 3: TabRow'u pagerState ile bağla**
  `TabRow` bileşeninin `selectedTabIndex` değerini `pagerState.currentPage` yapın ve `Tab` tıklama olaylarında `pagerState.animateScrollToPage` çağrısını coroutine ile koşturun:
  ```kotlin
  TabRow(
      selectedTabIndex = pagerState.currentPage,
      containerColor = Color.Transparent,
      contentColor = MaterialTheme.colorScheme.primary,
      modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
  ) {
      Tab(
          selected = pagerState.currentPage == 0,
          onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
          text = { Text("Zikirmatik", fontWeight = FontWeight.Bold) }
      )
      Tab(
          selected = pagerState.currentPage == 1,
          onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
          text = { Text("Namaz Tesbihatı", fontWeight = FontWeight.Bold) }
      )
  }
  ```

- [ ] **Step 4: HorizontalPager'ı ekle ve sekmeleri içine yerleştir**
  `TabRow`'un hemen altına `HorizontalPager` yerleştirerek sayfa içeriklerini (Zikirmatik ve Namaz Tesbihatı akışları) bu pager'a taşıyın:
  ```kotlin
  HorizontalPager(
      state = pagerState,
      modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
  ) { page ->
      if (page == 0) {
          // Zikirmatik Tab Content (DropDown, Add button, Tap Area ve Reset butonu)
      } else {
          // Namaz Tesbihatı Flow (NamazTesbihatiFlow(viewModel = viewModel, context = context))
      }
  }
  ```

- [ ] **Step 5: Commit**
  ```bash
  git add app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt
  git commit -m "feat(ux): implement HorizontalPager for Zikirmatik tab transitions"
  ```

---

### Task 2: Zikir Seçimi için ModalBottomSheet Entegrasyonu

**Files:**
- Modify: [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt)

**Interfaces:**
- Consumes: Yok.
- Produces: Zikir seçimi için ekranın altından açılan modern bottom sheet arayüzü.

- [ ] **Step 1: Gerekli material3 bottom sheet ve list import'larını ekle**
  [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt) dosyasına şu import'ları ekleyin:
  ```kotlin
  import androidx.compose.material3.ModalBottomSheet
  import androidx.compose.material3.rememberModalBottomSheetState
  import androidx.compose.foundation.lazy.LazyColumn
  import androidx.compose.foundation.lazy.items
  ```

- [ ] **Step 2: Zikir seçim DropdownMenu'sünü kaldır ve ModalBottomSheet ekle**
  Mevcut `DropdownMenu` bileşenini tamamen silip yerine `expanded` (seçici açık mı) durumuna göre açılan bir `ModalBottomSheet` ekleyin:
  ```kotlin
  if (expanded) {
      ModalBottomSheet(
          onDismissRequest = { expanded = false },
          sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
          containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
          contentColor = MaterialTheme.colorScheme.onSurface
      ) {
          ZikirSelectionBottomSheetContent(
              zikirs = zikirs,
              currentZikir = currentZikir,
              onZikirSelect = { zikir ->
                  viewModel.selectZikir(zikir)
                  expanded = false
              },
              onDeleteClick = { zikir ->
                  viewModel.deleteCustomZikir(zikir)
              },
              onAddClick = {
                  showAddDialog = true
                  expanded = false
              },
              onDismiss = { expanded = false }
          )
      }
  }
  ```

- [ ] **Step 3: ZikirSelectionBottomSheetContent bileşenini tanımla**
  Dosyanın sonuna `ZikirSelectionBottomSheetContent` Composable fonksiyonunu ekleyin:
  ```kotlin
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun ZikirSelectionBottomSheetContent(
      zikirs: List<co.gorkem.namaz.data.local.room.ZikirEntity>,
      currentZikir: co.gorkem.namaz.data.local.room.ZikirEntity?,
      onZikirSelect: (co.gorkem.namaz.data.local.room.ZikirEntity) -> Unit,
      onDeleteClick: (co.gorkem.namaz.data.local.room.ZikirEntity) -> Unit,
      onAddClick: () -> Unit,
      onDismiss: () -> Unit
  ) {
      Column(
          modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
              .navigationBarsPadding(),
          horizontalAlignment = Alignment.CenterHorizontally
      ) {
          Text(
              text = "Zikir Seçin",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 16.dp)
          )
          
          LazyColumn(
              modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f, fill = false),
              verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
              items(zikirs) { zikir ->
                  val isSelected = currentZikir?.id == zikir.id
                  Card(
                      onClick = { onZikirSelect(zikir) },
                      colors = CardDefaults.cardColors(
                          containerColor = if (isSelected) {
                              MaterialTheme.colorScheme.primaryContainer
                          } else {
                              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                          }
                      ),
                      modifier = Modifier.fillMaxWidth()
                  ) {
                      Row(
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 16.dp, vertical = 12.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                          Column(modifier = Modifier.weight(1f)) {
                              Text(
                                  text = zikir.title,
                                  style = MaterialTheme.typography.bodyLarge,
                                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                              )
                              Text(
                                  text = "Sayaç: ${zikir.currentCount}" + if (zikir.targetCount > 0) " / ${zikir.targetCount}" else " (Sınırsız)",
                                  style = MaterialTheme.typography.bodySmall,
                                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                              )
                          }
                          if (!zikir.isNamazTesbihati) {
                              IconButton(
                                  onClick = { onDeleteClick(zikir) }
                              ) {
                                  Icon(
                                      imageVector = Icons.Default.Delete,
                                      contentDescription = "Sil",
                                      tint = MaterialTheme.colorScheme.error
                                  )
                              }
                          }
                      }
                  }
              }
          }
          
          Spacer(modifier = Modifier.height(16.dp))
          
          Button(
              onClick = onAddClick,
              modifier = Modifier.fillMaxWidth().height(48.dp),
              shape = CircleShape
          ) {
              Icon(Icons.Default.Add, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Yeni Özel Zikir Ekle")
          }
      }
  }
  ```

- [ ] **Step 4: Ana ekrandaki eski Özel Zikir Ekle (Add) butonunu kaldır**
  Zikirmatik ana sayfasındaki `Spacer` ve `FilledTonalIconButton` (Add butonu) satırlarını silerek bu işlevi tamamen Bottom Sheet içine taşıyın.

- [ ] **Step 5: Commit**
  ```bash
  git add app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt
  git commit -m "feat(ux): replace dropdown menu with ModalBottomSheet for zikir selection"
  ```

---

### Task 3: NamazTesbihatiFlow Adım Geçiş Animasyonlarının Eklenmesi

**Files:**
- Modify: [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt)

**Interfaces:**
- Consumes: Yok.
- Produces: Animasyonlu adım geçişleri.

- [ ] **Step 1: Gerekli animation ve transition import'larını ekle**
  [ZikirmatikScreen.kt](file:///Users/gorkem/Desktop/Android_Kurs/Namaz/app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt) dosyasına şu import'ları ekleyin:
  ```kotlin
  import androidx.compose.animation.AnimatedContent
  import androidx.compose.animation.fadeIn
  import androidx.compose.animation.fadeOut
  import androidx.compose.animation.slideInHorizontally
  import androidx.compose.animation.slideOutHorizontally
  import androidx.compose.animation.togetherWith
  import androidx.compose.animation.core.EaseInOut
  ```

- [ ] **Step 2: NamazTesbihatiFlow içindeki static kart içeriğini AnimatedContent içine al**
  `NamazTesbihatiFlow` metodunda adımları çizen Card ve Completion bileşenlerini, `currentStepIndex` durumunu dinleyen bir `AnimatedContent` bloğuna sarın:
  ```kotlin
  AnimatedContent(
      targetState = currentStepIndex,
      transitionSpec = {
          val direction = if (targetState > initialState) 1 else -1
          slideInHorizontally(
              animationSpec = tween(300, easing = EaseInOut),
              initialOffsetX = { fullWidth -> fullWidth / 2 * direction }
          ) + fadeIn(tween(300)) togetherWith
          slideOutHorizontally(
              animationSpec = tween(300, easing = EaseInOut),
              targetOffsetX = { fullWidth -> fullWidth / 2 * -direction }
          ) + fadeOut(tween(300))
      },
      label = "tesbihat_step_transition",
      modifier = Modifier.weight(1f).fillMaxWidth()
  ) { stepIdx ->
      val currentStep = NamazTesbihatiSteps.steps.getOrNull(stepIdx) ?: return@AnimatedContent
      if (currentStep.type != StepType.COMPLETED) {
          // Kart içeriğini buraya taşı (Card(modifier = Modifier.fillMaxSize().padding(...)) { ... })
      } else {
          // Completion Layout içeriğini buraya taşı (Column(modifier = Modifier.fillMaxSize().padding(...)) { ... })
      }
  }
  ```

- [ ] **Step 3: Kart ve Completion Layout modifier'larını güncelle**
  AnimatedContent içindeki Card ve Completion Layout Column bileşenlerinin `modifier = Modifier.fillMaxWidth().weight(1f)` olan değerlerini, AnimatedContent'in kendisi weight alacağı için `modifier = Modifier.fillMaxSize()` olarak güncelleyin.

- [ ] **Step 4: Commit**
  ```bash
  git add app/src/main/java/co/gorkem/namaz/ui/screens/zikirmatik/ZikirmatikScreen.kt
  git commit -m "feat(ux): add animated slide transitions for Namaz Tesbihati steps"
  ```

---

### Task 4: Derleme ve Doğrulama

**Files:**
- Test: Tüm proje derleme ve testleri.

- [ ] **Step 1: Projeyi derle**
  Run: `./gradlew assembleDebug`
  Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Unit testleri çalıştır**
  Run: `./gradlew test`
  Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**
  ```bash
  git commit --allow-empty -m "test(verify): build and test passed after Zikirmatik UI/UX update"
  ```
