# Taste (Continuously Learned by [CommandCode][cmd])

[cmd]: https://commandcode.ai/

# ui
- Widget settings sayfalarında safe area/safety zone'lara dikkat edilmeli, tab bar veya status bar ile çakışma olmamalı. Confidence: 0.65

# git
- .commandcode/taste/ öğrenilen tercihler dosyası git'e commit edilmeli, projeye özel kuralların kalıcı olması sağlanmalı. Confidence: 0.65
- Worker/agent görevlerinde commit ve push yapılmamalı; değişiklikler working tree'de bırakılmalı, karar koordinatöre bırakılmalı. Confidence: 0.8
- Çalışma ağacında kendi yazmadığı değişiklikler (ör. otomatik araçların güncellediği dosyalar) görülürse dokunulmamalı, kapsam kanıtında ayrıştırılıp raporda belirtilmeli. Confidence: 0.7

# kod-yazimi
- Koda yorum satırı (comment) eklememeyi tercih ediyor; niyet isimlendirmeden anlaşılmalı. Confidence: 0.85
- Kod yazmadan önce repo konvansiyonlarını anlatan brief/dokümanı (ör. .agents/WORKER_BRIEF.md, docs/ altındaki mimari notlar) okumayı bekliyor. Confidence: 0.8
- Proje veri/yerelleştirme tutarlılığına özen gösteriyor: domain isimleri (namaz vakitleri) Türkçe ve tüm modüllerde aynı tutulmalı, dynamicColor gibi tema ayarlarına dokunulmamalı. Confidence: 0.6
- Test edilebilirlik için iş mantığını saf bir çekirdek fonksiyona ayırmayı tercih ediyor; Android/Compose'a bağlı katmanlar (Context.getString, stringResource, GPS çağrıları) ince sarmalayıcı olarak kalmalı, eşleme/karar mantığı tek ve saf noktada toplanmalı. Confidence: 0.8
- Sabit kodlanmış yer/kent yedeklerinden kaçınıyor; nötr bir kullanıcı etiketi (ör. "Konumunuz") ve zaten var olan sabitleri (repository'deki default koordinatlar) tercih ediyor. Confidence: 0.7

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
