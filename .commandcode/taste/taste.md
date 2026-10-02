# Taste (Continuously Learned by [CommandCode][cmd])

[cmd]: https://commandcode.ai/

# ui
- Widget settings sayfalarında safe area/safety zone'lara dikkat edilmeli, tab bar veya status bar ile çakışma olmamalı. Confidence: 0.65
- Üretim/gerçek cihaz UI yolunda asla mock ya da uydurma veri (sahte vakit, `20:33`, `--:--` gibi) render edilmemeli; veri yoksa dürüst bir yükleniyor/boş durum tercih ediliyor, "düzgün görünen" sahte veri lehine değil. Testlerle de sabitlenmeli (örn. resolved data asla uydurma vakit içermemeli). Confidence: 0.8
- Sensör tabanlı uyarı durumlarında iki farklı hâl tek bir hata bandında birleştirilmemeli: kullanıcının eylemiyle kendiliğinden düzelen kalibrasyon rehberi collapsible olup doğruluk toparlanınca kompozisyondan tamamen kalkmalı, kullanıcı hemen müdahale etmeden geçmeyecek parazit/bozulma uyarısı ise kapanmaz (kapatma butonu olmadan) görünür kalmalı. İkisi farklı container rengi, ikon ve "tone" ile görsel olarak ayrışmalı. Confidence: 0.75
- Açılır/kapanır UI'da ikon butonları için aç/kapa `contentDescription` metinlerini ayrı string anahtarlarıyla tanımlamayı ve zorunlu kılmayı bekliyor (erişilebilirlik). Aynı zamanda canlı doğruluk göstergesi isteniyorsa (LinearProgressIndicator vb.) doğruluk seviyesini görsel ilerlemeye çevirmek kabul; ana ölçütlü kural "kullanıcı iyileşmeyi ekranda görebilmeli". Confidence: 0.7

# yerellestirme
- Kullanıcıya görünen her yeni metin `res/values/strings.xml` ve `res/values-en/strings.xml` dosyalarına birebir aynı anahtarla eklenmeli; TR/EN senkronluğu gözle değil, iki dosyanın `<string name="...">` anahtar kümelerini karşılaştıran programatik bir kontrol ile kanıtlanmalı (yeni anahtarların ikisinde de bulunduğu ayrıca teyit edilmeli). Confidence: 0.85
- Yeni string'ler mevcut kaynaklara komşu/ilgili bir anahtarın hemen altına ekleniyor (ilgisiz bir yere yapıştırmak yerine). Confidence: 0.7
- Mevcut string'ler yeni ihtiyacı tam karşılamıyorsa (örn. net bir "Son veri / Last update" etiketi) dosyalar kapsam dışıysa yeni anahtar uydurmak yerine var olan en yakın string'i dürüstçe kullanmayı ve daha iyi etiket için koordinatöre soru sormayı tercih ediyor. Confidence: 0.7
- Bir değişiklik eski bir string'i kullanılmaz hale getiriyorsa (orphan) o anahtarı kendiliğinden silmiyor, başka yerde kullanılıp kullanılmadığını sorup koordinatöre bırakıyor. Confidence: 0.7

# git
- .commandcode/taste/ öğrenilen tercihler dosyası git'e commit edilmeli, projeye özel kuralların kalıcı olması sağlanmalı. Confidence: 0.65
- Worker/agent görevlerinde commit ve push yapılmamalı; değişiklikler working tree'de bırakılmalı, karar koordinatöre bırakılmalı. Confidence: 0.8
- Çalışma ağacında kendi yazmadığı değişiklikler (ör. otomatik araçların güncellediği dosyalar) görülürse dokunulmamalı, kapsam kanıtında ayrıştırılıp raporda belirtilmeli. Confidence: 0.7

# kod-yazimi
See [kod-yazimi/taste.md](kod-yazimi/taste.md)
# dokumantasyon
- Dokümandaki her sürüm numarası, applicationId, yol ve komutu gerçek dosyadan (build.gradle.kts, libs.versions.toml, .github/workflows/*.yml gibi tek doğruluk kaynakları) doğrulamayı bekliyor; referans dokümanı (ör. docs/08) doğru kabul edilmiyor ve kendisi de kaymış olabilir. Confidence: 0.85
- Referans dokümanın hatalı olduğu tespit edilirse hata yayınlamak yerine dokümana düzeltilmiş/kaçınılmış şekilde yansıtılıp koordinatöre "Questions" altında bildirilmeli. Confidence: 0.8
- Yalnızca istenen kalemleri düzeltmekle kalmayıp, dokümanın kod tabanıyla çelişen diğer iddiaları da bulup düzeltmeyi ve her birini raporda tek tek listelemeyi istiyor. Confidence: 0.8
- Uzun kod snippet'larını kopyalamak yerine "gerçek dosyaya bakınız" referansını tercih ediyor; snippet yalnızca artı değer katıyorsa tutulmalı, dosyanın tek doğruluk kaynağı olduğu notuyla birlikte. Kullanılmayan alternatif bir yaklaşımı silmek yerine "KULLANILMIYOR/geçmiş yöntem" diye işaretlemeyi ve neden öyle olduğunu açıklamayı (örn. neden applicationId'ler aynı olmalı) tercih ediyor. Confidence: 0.8
- Markdown tabloları, başlık hiyerarşisi ve emoji/uyarı işaretleme stili mevcut dokümanla tutarlı tutulmalı. Confidence: 0.75

# teknik-karar
- Üretilen çıktıları (ör. Room şema JSON'ları) `.gitignore`'a atmak yerine versiyona commit edilmesini tercih ediyor; migration yazarken şema farkını görmek için referans gerekiyor ve bu dosyalar "build artifact" sayılmıyor. Confidence: 0.7
- Sürüm/şema gibi sayısal sabitleri tek bir yerden (`const val VERSION = 5`) referanslayıp hem annotation'da hem testlerde kullanmayı tercih ediyor; böylece registry ile annotation'ın birbirinden kayması testlerle yakalanabiliyor. Confidence: 0.7
- Savunmasız veri kaybını kabul etmeyi reddedip, ileride gerekli olacak deseni bugünden testlenebilir ve saf hâlde hazırlamayı (boş migration registry + yol çözümleyici + dokümana yazılı kural) tercih ediyor; bugün için gereksiz görünse bile kalıcı çözümü önceliklendiriyor. Confidence: 0.7

# is-akisi
See [is-akisi/taste.md](is-akisi/taste.md)

# dogrulama
See [dogrulama/taste.md](dogrulama/taste.md)
