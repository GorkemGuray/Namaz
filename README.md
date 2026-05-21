# Namaz Vakitleri

Android ve Wear OS için konum bazlı namaz vakitleri uygulaması.

## Özellikler

- Konum bazlı namaz vakitleri (İmsakiyem API — Diyanet resmi verisi)
- **Lokal hesaplama**: İnternetsiz, cihazda matematiksel formüllerle Diyanet uyumlu vakitler
- **Hibrit mod**: Kullanıcı seçimine göre API veya lokal hesaplama; internetsiz otomatik lokal'e geçiş
- Kerahat vakitleri gösterimi (aktif/yaklaşıyor)
- İlçe arama (İmsakiyem API ile Diyanet ilçe ID'leri)
- Bildirimler (her vakit için ayrı Worker)
- Wear OS desteği (senkronize + standalone)
- Türkçe/İngilizce dil desteği
- Çevrimdışı cache (Room)

## Build

```bash
./gradlew assembleDebug          # app + wear
./gradlew :app:assembleDebug     # sadece mobil
./gradlew :wear:assembleDebug    # sadece wear
```

## Kerahat Vakitleri

| Periyot | Aralık |
|---------|--------|
| Güneş | Güneş → Güneş +45dk |
| Öğle | Öğle -45dk → Öğle |
| İkindi-Akşam | İkindi → Akşam -45dk |

Detay: `kerahat.jpg`

## Mimari

Clean Architecture + MVVM + Hilt DI

## Teknolojiler

- Kotlin 2.2.10, Compose BOM 2026.02.01
- Hilt 2.59.2, Retrofit 2.11.0, Room 2.8.4
- WorkManager 2.11.2, DataStore 1.1.1
- Wear OS Compose Material3 1.6.1
