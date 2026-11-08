# 05 — Test ve Kalite

## 1. TDD Kuralı (AGENTS.md, zorunlu)

1. İş mantığı (UseCase/ViewModel/Repository) yazmadan **önce** unit test yazılır/güncellenir.
2. Her değişiklikten sonra `./gradlew test` — geçmeden sonraki adıma geçilmez.
3. **Her görev commit ile biter** — `git status` boş olmadan yeni göreve başlanmaz (T-213 sonrası kural).
4. Zaman bağımlı mantıkta clock dışarıdan opsiyonel parametre alınır (`now: LocalTime = now()`), testte deterministik girdi verilir.
5. Konum: `app/src/test/`, saat: `wear/src/test/`, paylaşım: `core/src/test/`.

## 2. Mevcut Test Envanteri (Kas 2026 — 30 dosya)

### app/src/test (23 dosya)
| Dosya | Kapsam |
|-------|--------|
| `ExampleUnitTest.kt` | Şablon |
| `domain/calculator/PrayerTimeCalculatorTest.kt` | İstanbul Mayıs/Ocak/Aralık/Haziran + Ankara, Diyanet'e ±5/10dk tolerans |
| `domain/model/PrayerNameTest.kt` | TR-locale `I/İ/ı/i` + `fromName` (T-201) |
| `domain/usecase/QiblaUseCaseTest.kt` | core'a taşındı — bkz. core bölümü (eski dosya silindi) |
| `domain/usecase/GetNextPrayerUseCaseTest.kt` | Sıradaki vakit + kalan dakika geçişleri |
| `domain/usecase/GetPrayerDashboardUseCaseTest.kt` | Periyot + kerahat pencereleri |
| `domain/usecase/GetRamadanCountersUseCaseTest.kt` | 6 test: ay 9 aktifliği, sahur/iftar kalan süre, ay-dışı null (T-232) |
| `domain/usecase/GetSpecialDayUseCaseTest.kt` | Cuma + 2026/2027 kandiller + negatif gün (T-233) |
| `domain/usecase/QadaUseCasesTest.kt` | Borç artır/azalt, 6 vakit sayımı, toggle (T-231) |
| `data/local/room/QadaPrayerDaoTest.kt` | CRUD, vakit sayımı, done filtresi (T-231) |
| `data/local/room/NamazDatabaseMigrationTest.kt` | Migration yolu + registry tutarlılığı |
| `ui/screens/cityselection/CitySelectionViewModelTest.kt` | 6 test: debounce, Diyanet/Nominatim arama, hata, kaydetme (T-212) |
| `ui/screens/zikirmatik/TesbihatStepInitializationTest.kt` | Tesbihat adım init |
| `ui/screens/zikirmatik/GestureTriggerResolverTest.kt` | Jest tetikleyici çözümleme |
| `ui/screens/zikirmatik/SacredWordAnnotationTest.kt` | Kutsal kelime anotasyonu |
| `ui/components/CompassGuidanceTest.kt` | Pusula yönlendirme metni |
| `ui/theme/ThemeIdStabilityTest.kt` | 11 tema ID kararlılığı |
| `util/LocationMathTest.kt` | 10km displacement hesabı |
| `service/PrayerNotificationSchedulingTest.kt` | 6 vakit zamanlama hesabı |
| `service/PrayerNotificationChannelTest.kt` | Voice/normal kanal seçimi (T-230) |
| `service/SpecialDayNotificationSchedulingTest.kt` | 09:00 hedef + `special_day_` adlandırma (T-233) |
| `service/WidgetLayoutResolutionTest.kt` | Widget layout çözümü |
| `service/AlarmStrategyResolutionTest.kt` | Alarm stratejisi (exact/inexact fallback) |

### wear/src/test (6 dosya)
| Dosya | Kapsam |
|-------|--------|
| `PrayerNextPrayerCalculationTest.kt` | 3 test (`now/today` parametreli): 02:00→İmsak 90dk, 14:00→İkindi 185dk, 23:00→yarın İmsak 270dk |
| `PrayerNameMappingTest.kt` | İsim → stringRes eşleşmesi |
| `WearDataFreshnessTest.kt` | Data tazeliği |
| `data/WearPrayerRepositoryTest.kt` | 9 test: dataSource 0/1 dallanma + hata (T-234) |
| `data/WearDataLayerParseTest.kt` | `parsePrayerData()` (T-221) |
| `ui/StandaloneLocationResolutionTest.kt` | GPS → DataStore → DEFAULT fallback (T-234) |

### core/src/test (1 dosya)
| Dosya | Kapsam |
|-------|--------|
| `QiblaUseCaseTest.kt` | 6 test: İstanbul ~151.6°, NYC ~58.5°, Sydney ~277.5°, Kabe 0°, kardinal GD/KD (app'ten taşındı, T-211) |

## 3. Komutlar

```bash
./gradlew test            # tümü (app + wear + core)
./gradlew :app:test       # mobil
./gradlew :wear:test      # wear
./gradlew :core:test      # paylaşım
./gradlew :app:testDebugUnitTest --rerun-tasks  # gerçek koşum (UP-TO-DATE'e güvenme)
./gradlew connectedAndroidTest  # cihaz testi (gerekirse)
```

Kontrol notu: `UP-TO-DATE` çıktısı testin koştuğunu göstermez; revizyon doğrulamasında `--rerun-tasks` kullanılır.

## 4. Manuel Test — Emülatör Konumu

API 36 emülatörde `adb emu geo fix` çalışmaz (OK döner, konum güncellenmez). Mock provider:

```bash
adb shell appops set com.android.shell android:mock_location allow
adb shell cmd location providers add-test-provider gps --requiresSatellite
adb shell cmd location providers set-test-provider-enabled gps true
adb shell cmd location providers set-test-provider-location gps --location 41.0082,28.9784

# Widget tick manuel tetikleme
adb shell am broadcast -a co.gorkem.namaz.WIDGET_MINUTE_TICK \
  -n co.gorkem.namaz/.service.WidgetMinuteReceiver -f 0x10

# Logcat
adb logcat -d | grep -E "WidgetMinuteReceiver|NamazWidgetUpdater|LocationSyncWorker"
```

Android CLI: `android layout --pretty` (UI hataları), `android screen capture` (PNG), `android docs search <terim>` (resmi doküman).
