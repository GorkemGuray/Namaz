# Namaz Vakitleri

Android ve Wear OS için konum bazlı namaz vakitleri uygulaması.

## Özellikler

- **Dört Premium Tema**:
  - **Emerald Oasis** (Varsayılan): Modern İslami adaçayı yeşili estetiği
  - **Sapphire Night**: Derin ruhani gece mavisi paleti
  - **Amber Sands**: Sıcak miras ve Kabe ışıltıları tonları
  - **Monochrome Platinum**: Ultra-minimalist yönetici teması
  - Tüm temalar sistem dinamik renklerini (Monet) bypass eder, DataStore üzerinden kalıcıdır
  - Jetpack Compose Glance Widget ile tam renk senkronizasyonu
- **Ana Ekran Widget (Glance)**: Detaylı (6 vakit + geri sayım) veya sade (sadece sıradaki vakit) layout seçenekli
- **Üçlü Veri Kaynağı Seçeneği**:
  - **Aladhan API (Varsayılan)**: Dünya genelinde konum koordinatları ile çevrimiçi namaz vakitleri
  - **İmsakiyem API (Diyanet Resmi)**: Türkiye genelinde resmi ilçe bazlı namaz vakitleri
  - **Lokal Hesaplama (Çevrimdışı mod)**: İnternet bağlantısı yokken veya tercih edildiğinde cihazda matematiksel formüllerle Diyanet uyumlu hesaplama
- **Aylık Namaz Takvimi**: Alt bar üzerinden erişilebilen, 5 günlük vakit + güneş sütunlarını tüm ay boyunca listeleyen, ay bazında ileri/geri geçiş ve bulunulan günü belirginleştirme (highlight) özelliğine sahip responsive tablo ekranı.
- **Harf/Karakter Duyarsız Konum Arama**: Türkçe karakterler (kırklareli, üsküdar vb.) ve büyük/küçük harf hassasiyeti giderilmiş ilçe arama desteği
- **Sadeleştirilmiş Konum ve Responsive Arayüz**: Çok uzun konum adlarında dahi kaymayan HeroCard tasarımı ve sadeleştirilmiş konum bilgisi
- **Gelişmiş Kıble Pusulası**: Sin/cos tabanlı yumuşatılmış azimut takibi (360° sarmalama sorunsuz), en kısa yol rotasyonu, histerezisli hizalama algılama (±4°/±6.5°), animasyonlu hizalama glowsweep efektleri, sensör doğruluk seviyesi göstergesi
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
- Glance 1.1.1 (Widget)
- Wear OS Compose Material3 1.6.1
