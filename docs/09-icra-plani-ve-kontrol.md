# 09 — İcra Planı ve Kontrol (Başka Agent Uygulayacak)

> Sahip: plan + kontrol bu oturumda. Uygulama başka agent tarafından yapılır.
> İlke: her görev TDD, her görev sonu `./gradlew test` yeşil. Mimari: Clean + MVVM + Hilt korunur.

## Kontrol Modeli

- Her görev ID'lidir (T-200+). Agent görevi bitince `Durum: TAMAM + test çıktısı + değişen dosyalar` raporlar.
- Kontrol (ben) her görevde şunları doğrular:
  1. `git diff --stat` — sadece ilgili dosyalar değişmiş mi?
  2. `./gradlew :app:test :wear:test` — yeşil mi?
  3. Kabul kriteri — dosyada `grep` ile doğrulanır (her görevde komut verildi).
- Gate: Faz N+1'e geçmek için Faz N'in tüm kabul kriterleri + test yeşil şart.

## Faz A — Risksiz Temizlik (önce bunu yap)

### T-200 Hardcoded string temizliği
- Hedef: `strings.xml` dışı kullanıcı metni kalmasın.
- Dosyalar: `data/repository/PrayerTimeRepositoryImpl.kt` ("Lokal hesaplama yapılamadı", "Vakitler alınamadı"), `service/NamazWidget.kt` ("Bugünün vakitleri bulunamadı", "Yenile", "İstanbul"), `CitySelectionViewModel.kt` ("Türkiye"), `ZikirmatikViewModel.kt` (zikir adları), `util/DateTimeUtils.kt` ("Şev/Shaw"), `service/NotificationHelper.kt` (channel desc).
- Adım: her string → `res/values/strings.xml` + `values-en` mirror + `context.getString()`.
- Kabul: `grep -rn '"[A-ZÇĞİÖŞÜ].*"' app/src/main/java/co/gorkem/namaz/data app/src/main/java/co/gorkem/namaz/service --include="*.kt" | grep -v getString` boş döner.
- Test: `./gradlew :app:test`

### T-201 Türkçe locale fix
- Dosyalar: `domain/model/PrayerName.kt:16`, `ui/screens/zikirmatik/ZikirmatikScreen.kt:889` içindeki locale'siz `lowercase()`.
- Adım: `lowercase(Locale.forLanguageTag("tr"))` yap (CitySelection'daki çözümle aynı).
- Kabul: `grep -rn "lowercase()" app/src/main --include="*.kt"` boş döner.
- Test: `I/İ` içeren birim test ekle (`PrayerNameTest`).

### T-202 Build borcu
- Adımlar:
  1. `gradle/libs.versions.toml`: KSP `2.3.6` → Kotlin 2.2.x ile uyumlu `2.2.x-y` serisine indir.
  2. `wear/build.gradle.kts`: versiyonsuz `material-icons-core/extended` → katalogdan (`libs.*`) al.
  3. `app/src/main/AndroidManifest.xml`: `WRITE_EXTERNAL_STORAGE (maxSdk 28)` ölü izni kaldır; `REQUEST_INSTALL_PACKAGES` için gerekçe yorumu ekle veya kaldır.
  4. `AGENTS.md` konfig tablosu: Gradle 9.6.0, AGP 9.4.0, KSP (yeni), DB v5 yaz.
- Kabul: `./gradlew :app:assembleDebug :wear:assembleDebug` + `./gradlew test` yeşil.
- Kontrol: `grep -n "ksp\|agp\|gradle" gradle/libs.versions.toml` + manifest diff.

## Faz B — Mimari (:core modülü + ihlal düzeltme)

### T-210 `:core` modülü aç
- Adım: `settings.gradle.kts`'e `include(":core")` ekle; `core/build.gradle.kts` oluştur (Kotlin-only, `minSdk` yok).
- Kabul: `./gradlew :core:build` çalışır.
- Kontrol: `read settings.gradle.kts` içinde `:core` var.

### T-211 Paylaşılan kodu `:core`'a taşı (kopyala-sil değil, taşı)
- Kaynaklar (app tarafı esas): `domain/model/QiblaDirection.kt`, `domain/usecase/QiblaUseCase.kt`, `service/QiblaSensorManager.kt` (kullanılmayan `import kotlin.math.PI` silinerek), `data/remote/dto/AladhanResponse.kt`, `data/remote/api/AladhanApiService.kt`, `util/GreatCircleUtil.kt`, `util/LocationMath.kt`, `util/DateTimeUtils.kt`.
- Wear karşılıklarını sil: `wear/data/QiblaUseCase.kt`, `wear/data/QiblaDirection.kt`, `wear/data/QiblaSensorManager.kt`, `wear/data/remote/dto/AladhanResponse.kt`, `wear/data/remote/api/AladhanApiService.kt`.
- Tesbihat içeriği: `NamazTesbihatiStep.kt` + `LongTesbihatSteps.kt` + `WearNamazTesbihatiStep.kt` → tek `core` listesi, tek enum (`StepType`).
- Kabul: `diff <(app dosyası) <(wear dosyası)` çakışması kalmaz; `grep -rn "package co.gorkem.namaz.wear.data" wear/src/main --include="*.kt" | grep -i qibla` boş.
- Test: `QiblaUseCaseTest` `:core`'a taşınır, app+wear ikisi de kullanır.

### T-212 CitySelection mimari ihlali
- Dosya: `ui/screens/cityselection/CitySelectionViewModel.kt` — `ImsakiyemApiService` + `NominatimApiService` import'larını sil, `PrayerTimeRepository.findDistrictId()` + yeni `SearchLocationUseCase` üzerinden yap.
- Kabul: `grep -n "ApiService" app/src/main/java/co/gorkem/namaz/ui/screens/cityselection/CitySelectionViewModel.kt` boş.
- Test: ViewModel için fake-repository testi ekle.

### T-213 Domain/Data ters bağımlılık
- `NetworkResult.kt` → `domain/model/` altına taşı (package güncelle, import'ları düzelt).
- `ManageZikirUseCase`, `GetZikirsUseCase`, `GetNamazTesbihatiUseCase` içindeki `data.local.room.ZikirEntity` import'unu kaldır → domain `Zikir` modeline map'le.
- `UserPreferencesRepository` içindeki `service.NamazWidgetUpdater` çağrısını kaldır → `NamazApplication` veya UI katmanında collect edip tetikle.
- Kabul: `grep -rn "data\." app/src/main/java/co/gorkem/namaz/domain --include="*.kt"` boş.
- Test: `./gradlew :app:test`

### T-214 Büyük dosya parçalama
- `ZikirmatikScreen.kt` (1106) → `ZikirCounter.kt` + `TesbihatFlow.kt` + `ZikirProgressRing.kt` çıkar.
- `NamazWidget.kt` (988) → `NamazWidgetState.kt` (zaten varsa sadeleştir) + `WidgetLayout.kt` + `WidgetAlarm.kt` çıkar.
- `SettingsScreen.kt` (938) → `NotificationSettingsGroup.kt` + `ThemeSettingsGroup.kt` + `LanguageSettingsGroup.kt` çıkar.
- `WearQiblaScreen.kt` (700) → app'teki gibi `WearQiblaCompass.kt` + `WearQiblaDegreeDisplay.kt` + screen çıkar.
- Kabul: hiçbir screen dosyası 400 satırı geçmez (`wc -l` ile kontrol).
- Test: derleme + `android layout --pretty` smoke.

## Faz C — Performans + Pil

### T-220 Wear countdown polling kaldır
- Dosya: `wear/ui/WearViewModel.kt` — `while(true)+delay(10sn)` countdown'u sil, app'teki `CountdownTimer.kt` desenini uygula (`System.currentTimeMillis()` ile her tick'te yeniden hesap).
- Kabul: `grep -n "while(true)" wear/src/main --include="*.kt"` boş.
- Test: `DataFreshness` testi yeşil.

### T-221 Wear DataLayer parse tekleştir
- Dosya: `wear/data/WearDataLayerService.kt` — 3x tekrar eden JSON-parse bloğu → `parsePrayerData(json: String): WearPrayerData?` tek fonksiyon.
- Kabul: dosyada `fromJson` 1 kez geçer.
- Test: derleme + unit test.

### T-222 Widget tick ölçümü
- `NamazWidget.provideGlance()` DB okumayı tek sorguya indir, gereksiz `Log.d`'leri `BuildConfig.DEBUG` guard'ına al.
- Kabul: release log taramasında `Log.d` yok (`grep -rn "Log\.d" app/src/main --include="*.kt"` boş veya guard'lı).
- Test: `./gradlew :app:test` + manuel widget tick broadcast testi.

## Faz D — Yeni Özellikler (sıralı, her biri ayrı issue)

- T-230 Ezan sesi + sesli bildirim (`PrayerNotificationWorker` genişletme).
- T-231 Kaza namazı takibi (Room 1 tablo + migration, `exportSchema` kararı 08/§4 ile birlikte).
- T-232 Ramazan modu (`GetPrayerDashboardUseCase` üzerine sahur sayacı).
- T-233 Cuma/kandil hatırlatıcıları.
- T-234 Wear standalone Diyanet: `WearPrayerRepository`'ye `ImsakiyemApiService.searchDistricts` ekle (şu an ölü kod) + GPS→DataStore→DEFAULT fallback (08/P0-2).

## Uygulayıcı Agent'e Talimat

1. Görevleri sırayla al (T-200 → T-234). Paralel dal açma, tek branch tek görev.
2. Her göreve TDD ile başla: önce/beraber test yaz veya güncelle.
3. Her görev sonu rapor formatı: `ID + değişen dosyalar + test komutu çıktısı + kabul komutu çıktısı`.
4. 400 satır üstü yeni dosya yazma. Yorum satırı yazma (proje konvansiyonu).
5. Takıldığın yerde dur, varsayım üretme — kontrol oturumuna sor.
