# 02 — Veri Kaynakları ve Veri Akışı

## 1. Üç Veri Kaynağı

| # | Kaynak | Kapsam | İnternet | `data_source` |
|---|--------|--------|:---:|:---:|
| 1 | **Aladhan API** (varsayılan) | Dünya geneli, koordinat bazlı | Gerekli | 0 |
| 2 | **İmsakiyem API (Diyanet)** | Türkiye, ilçe ID bazlı resmi veri | Gerekli | 1 |
| 3 | **Lokal Hesaplama** | Cihaz içi astronomik | Gereksiz | 2 |

Hibrit mod: `useLocalCalculation=true` → hep lokal. `false` → internet varsa API, yoksa otomatik lokal (`shouldUseLocalCalculation()`).

## 2. API Referansı

### 2.1 Aladhan (dünya)
```
GET https://api.aladhan.com/v1/calendar/{year}/{month}
  ?latitude={lat}&longitude={lon}&method={method}&school={school}
```
- `method=13` Diyanet yöntemi (varsayılan `calculation_method`).
- `school`: 0 Şafii / 1 Hanefi.
- Response: `AladhanResponse(code/status/data: List<AladhanDayData>)`; timings `fajr/sunrise/dhuhr/asr/maghrib/isha` ("HH:mm (EET)" suffixli → `cleanTime()` ile temizlenir).

### 2.2 İmsakiyem (Diyanet resmi)
```
GET https://ezanvakti.imsakiyem.com/api/prayer-times/{districtId}/monthly?startDate?
GET https://ezanvakti.imsakiyem.com/api/locations/search/districts?q={query}&countryId=2
```
- Aylık endpoint ilgili ayın tüm günlerini + `hijri_date` döndürür.
- `method/school` parametresi yok — veri doğrudan Diyanet'tir.
- Rate limit: 100 istek / 5 dakika.
- Örnek: Silivri=9548, Fatih=9549 (varsayılan DEFAULT districtId=9541).

### 2.3 Nominatim (şehir arama, yedek)
```
GET https://nominatim.openstreetmap.org/search
  ?q={query}&format=json&addressdetails=1&limit=20&accept-language=tr
```
- `@Named("nominatim")` ayrı OkHttp (User-Agent header zorunlu) + Retrofit.
- Aladhan/Lokal modunda dünya araması için; İmsakiyem modunda kullanılmaz.
- Uzun adresler sadeleştirilir ("Şehir, İlçe" / "Şehir, Ülke").

### 2.4 Lokal motor
`domain/calculator/`: Fajr=-18°, Isha=-17°, Asr=1x shadow, offset'ler (sunrise −7, dhuhr +5, asr +4, maghrib +7). `PrayerTimeCalculatorTest` Diyanet saatleriyle ±10dk toleransla doğrular.

### 2.5 Sürüm endpoint'i (uygulama içi güncelleme)
```
GET https://gorkemguray.github.io/Namaz-App/version.json
```
`UpdateChecker.check(currentVersion)` + `isNewerThan` semantik karşılaştırma.

## 3. Repository Akışı (`PrayerTimeRepositoryImpl`)

```
getPrayerTimes(location, month, year, method, school)
  → Room cache var mı? (getPrayerTimesForMonth + getMonthRecordCount)
  → varsa Success(cache), yoksa refreshPrayerTimes()
        ↓
refreshPrayerTimes()
  → getActiveDataSource() (data_source + useLocalCalculation)
  → dataSource==2 veya !isNetworkAvailable() → calculateLocally()
  → dataSource==1 → fetchFromImsakiyem(districtId via findDistrictId)
  → dataSource==0 → fetchFromAladhan(lat/lon/method/school)
  → API hatası → otomatik lokal fallback
  → insertAll(REPLACE) + NamazWidgetUpdater.update() + cleanupOldData(30)
```

- Cache key: `cityName + month + year + method + school + cacheDataSource`.
- `cleanupOldData(30)`: 30 günden eski veri silinir.
- Her DB yazımı widget'ı tetikler.
- Wear sync sadece `isSuccess` içinde (`LocationSyncWorker`).

## 4. Türkçe Arama Çözümü

İmsakiyem ilçe arama büyük/küçük harf + Türkçe karakter hassastır. Çözüm:
- İstek öncesi: `query.uppercase(Locale.forLanguageTag("tr"))` (örn. `kırklareli` → `KIRKLARELİ`).
- Eşleştirme: `name.lowercase(Locale.forLanguageTag("tr"))` karşılaştırma.
- Uygulanan yerler: `CitySelectionViewModel` + `PrayerTimeRepositoryImpl.findDistrictId()`.

## 5. Konum → Veri Zinciri

```
İzin verildi → FusedLocationProviderClient → GeocoderHelper (tr locale)
  → LocationInfo(lat/lon/city/district/timezone)
  → refreshPrayerTimes → Room → UI (HomeUiState.Success)
  → WearSyncManager.syncData → saat
İzin yok → CitySelectionScreen → manuel şehir → aynı zincir (DEFAULT İstanbul/Silivri)
```

`setLastAutoLocation()` widget güncellemez — widget sadece `refreshPrayerTimes()` başarısından sonra `LocationSyncWorker` üzerinden güncellenir (boş/hatalı state önlemi).
