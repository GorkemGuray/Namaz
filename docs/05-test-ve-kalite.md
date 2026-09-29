# 05 — Test ve Kalite

## 1. TDD Kuralı (AGENTS.md, zorunlu)

1. İş mantığı (UseCase/ViewModel/Repository) yazmadan **önce** unit test yazılır/güncellenir.
2. Her değişiklikten sonra `./gradlew test` — geçmeden sonraki adıma geçilmez.
3. Zaman bağımlı mantıkta clock dışarıdan opsiyonel parametre alınır (`now: LocalTime = now()`), testte deterministik girdi verilir.
4. Konum: `app/src/test/`, saat: `wear/src/test/`.

## 2. Mevcut Test Envanteri

### app/src/test (6 dosya)
| Dosya | Kapsam |
|-------|--------|
| `ExampleUnitTest.kt` | Şablon |
| `domain/usecase/QiblaUseCaseTest.kt` | 6 test: İstanbul ~151.6°, NYC ~58.5°, Sydney ~277.5°, Kabe 0°, kardinal GD/KD |
| `domain/calculator/PrayerTimeCalculatorTest.kt` | İstanbul Mayıs/Ocak/Aralık/Haziran + Ankara, Diyanet'e ±5/10dk tolerans |
| `domain/usecase/GetNextPrayerUseCaseTest.kt` | Sıradaki vakit + kalan dakika geçişleri |
| `domain/usecase/GetPrayerDashboardUseCaseTest.kt` | Periyot + kerahat pencereleri |
| `util/LocationMathTest.kt` | 10km displacement hesabı |

### wear/src/test (1 dosya)
| Dosya | Kapsam |
|-------|--------|
| `PrayerNextPrayerCalculationTest.kt` | 3 test (`now/today` parametreli): 02:00→İmsak 90dk, 14:00→İkindi 185dk, 23:00→yarın İmsak 270dk |

## 3. Komutlar

```bash
./gradlew test            # tümü
./gradlew :app:test       # mobil
./gradlew :wear:test      # wear
./gradlew connectedAndroidTest  # cihaz testi (gerekirse)
```

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
