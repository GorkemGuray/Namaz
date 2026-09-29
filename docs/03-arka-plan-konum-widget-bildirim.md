# 03 — Arka Plan: Konum, Widget, Bildirim

## 1. Üç Yedekli Konum Mekanizması

| Yol | Mekanizma | Tetikleyici | Pil | "Sadece kullanırken" izni |
|-----|-----------|:---:|:---:|:---:|
| 1. `WidgetMinuteReceiver` | `lastLocation` (önbellek), dakikada bir | 10km aşılınca `LocationSyncWorker` | ~0 | ✅ |
| 2. `LocationSyncWorker` | Periyodik 12 saat, `BATTERY_NOT_LOW` | Zamanlanmış | 2-3sn CPU + 1 HTTP | ✅ |
| 3. `LocationPassiveReceiver` | PendingIntent 10km / 15dk | Displacement | Düşük (pasif) | ❌ (BACKGROUND gerekli) |

### Yol 1 — `WidgetMinuteReceiver` (en güvenilir)
`AlarmManager.setExactAndAllowWhileIdle(RTC, ...)` her dakika başı `:00`'da tetiklenir. Her tick:
1. `NamazWidget().updateAll(context)` — geri sayımı günceller.
2. `checkLocationChange()`: `lastLocation` (bedava önbellek) vs kayıtlı konum.
   - `lastLocation` null → `getCurrentLocation()` fallback (25sn timeout).
   - Başarısız → 5 dakika backoff (GPS tekrar uyandırılmaz).
   - 10km aşıldı → `LocationSyncWorker.executeNow(lat, lon)`.
- `SCHEDULE_EXACT_ALARM` manifest'te; API 33+ `canScheduleExactAlarms()` + SecurityException fallback.
- Reboot sonrası `NamazApplication.onCreate()` aktif widget varsa `scheduleNextMinute()` ile yeniden kurar.
- Manuel test: `adb shell am broadcast -a co.gorkem.namaz.WIDGET_MINUTE_TICK -n co.gorkem.namaz/.service.WidgetMinuteReceiver -f 0x10`.

### Yol 2 — `LocationSyncWorker`
`schedulePeriodic()`: 12 saat, `BATTERY_NOT_LOW`. `executeNow()`: `setExpedited(RUN_AS_NON_EXPEDITED_WORK_REQUEST)` — Doze'da bile hemen çalışır.
`doWork()`: konum → 10km kontrol → reverse geocode + districtId → eksik ay kontrolü → `refreshPrayerTimes()` → bildirim reschedule + wear sync (sadece success) + **widget update her zaman** (isSuccess dışı) → `cleanupOldData(30)`.

### Yol 3 — `LocationPassiveReceiver`
`LocationTracker.registerPassiveLocationListener()`: 10km displacement, 15dk interval, PendingIntent → `LocationPassiveReceiver.onReceive()` → `executeNow()`. Sadece `ACCESS_BACKGROUND_LOCATION` varsa çalışır.

## 2. Widget Güncelleme Zinciri

```
Konum 10km değişti
  → setLastAutoLocation()       [widget GÜNCELLENMEZ]
  → refreshPrayerTimes()         [API veya lokal]
  → NamazWidgetUpdater.update()  [her zaman, hata dahil]
  → NamazWidget.provideGlance()  [DB'den okur, render eder]
  → schedulePrayerTransitionAlarm() [sonraki vakit/kerahat için alarm]
  → WearSyncManager.syncData()   [sadece success]
```

- **Native Chronometer**: `AndroidRemoteViews` + `Chronometer`, `elapsedRealtime()` tabanlı countdown. Launcher/SystemUI işletir — %0 CPU/wake lock, Doze geçirmez.
- **Kerahat sync**: `NamazWidgetState(isKerahat/kerahatPrayerName/kerahatRemainingMinutes)`; `GetPrayerDashboardUseCase.execute()` widget state'e taşınır. Kerahatta kart `errorContainer` + `error` badge, Chronometer hedefi kerahat bitişi.
- **Glance anlık güncelleme**: `NamazWidgetUpdater.update()` tüm glanceId'leri günceller; hatalar `Log.e` ile logcat'e (eski sessiz `printStackTrace` kaldırıldı).
- Batarya özeti: `lastLocation`/dakika ~0, `getCurrentLocation` nadiren düşük, worker ~2-3sn CPU + 1 HTTP, `provideGlance` ~50ms.

## 3. Bildirimler

- `PrayerNotificationScheduler.schedulePrayerNotifications(times, minutesBefore, city)`: 6 vakit için ayrı `OneTimeWorkRequest` (unique `prayer_{NAME}_{date}`, tag `prayer_notification`), delay = `vakit − minutesBefore`.
- `PrayerNotificationWorker.doWork()`: `NotificationHelper.showPrayerNotification()` + widget update.
- `cancelAllNotifications()`: tag ile iptal.
- `NotificationHelper`: kanal oluşturma + tek/hatırlatma bildirimi + renk/ikon.

## 4. Logcat ve Test

```bash
adb logcat -d | grep -E "WidgetMinuteReceiver|NamazWidgetUpdater|LocationSyncWorker"
```
API 36 emülatörde `adb emu geo fix` çalışmaz — mock provider kullanılır (bkz. `05-test-ve-kalite.md`).
