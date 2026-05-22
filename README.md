# Namaz Vakitleri

Android ve Wear OS için konum bazlı namaz vakitleri uygulaması.

## Özellikler

- **Üçlü Veri Kaynağı Seçeneği**:
  - **Aladhan API (Varsayılan)**: Dünya genelinde konum koordinatları ile çevrimiçi namaz vakitleri
  - **İmsakiyem API (Diyanet Resmi)**: Türkiye genelinde resmi ilçe bazlı namaz vakitleri
  - **Lokal Hesaplama (Çevrimdışı mod)**: İnternet bağlantısı yokken veya tercih edildiğinde cihazda matematiksel formüllerle Diyanet uyumlu hesaplama
- **Harf/Karakter Duyarsız Konum Arama**: Türkçe karakterler (kırklareli, üsküdar vb.) ve büyük/küçük harf hassasiyeti giderilmiş ilçe arama desteği
- **Sadeleştirilmiş Konum ve Responsive Arayüz**: Çok uzun konum adlarında dahi kaymayan HeroCard tasarımı ve sadeleştirilmiş konum bilgisi
- Kerahat vakitleri gösterimi (aktif/yaklaşıyor)
- Bildirimler (her vakit için ayrı WorkManager Worker'ı)
- Wear OS desteği (aktif senkronizasyon + Aladhan API ile standalone çalışabilme)
- Türkçe/İngilizce dil desteği
- Çevrimdışı önbellek (Room veri tabanı v4 ile veri kaynağı uyumlu cache)

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
