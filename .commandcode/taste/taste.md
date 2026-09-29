# Taste (Continuously Learned by [CommandCode][cmd])

[cmd]: https://commandcode.ai/

# ui
- Widget settings sayfalarında safe area/safety zone'lara dikkat edilmeli, tab bar veya status bar ile çakışma olmamalı. Confidence: 0.65

# git
- .commandcode/taste/ öğrenilen tercihler dosyası git'e commit edilmeli, projeye özel kuralların kalıcı olması sağlanmalı. Confidence: 0.65
- Worker/agent görevlerinde commit ve push yapılmamalı; değişiklikler working tree'de bırakılmalı, karar koordinatöre bırakılmalı. Confidence: 0.8

# kod-yazimi
- Koda yorum satırı (comment) eklememeyi tercih ediyor; niyet isimlendirmeden anlaşılmalı. Confidence: 0.85
- Kod yazmadan önce repo konvansiyonlarını anlatan brief/dokümanı (ör. .agents/WORKER_BRIEF.md, docs/ altındaki mimari notlar) okumayı bekliyor. Confidence: 0.8
- Proje veri/yerelleştirme tutarlılığına özen gösteriyor: domain isimleri (namaz vakitleri) Türkçe ve tüm modüllerde aynı tutulmalı, dynamicColor gibi tema ayarlarına dokunulmamalı. Confidence: 0.6

# is-akisi
- Görevlere kesin scope listesi veriyor; scope dışına çıkmak gerekirse devam etmek yerine durulup sorulmasını istiyor. Confidence: 0.85
- TDD tercih ediyor: implementasyondan önce test yazılmalı/uyarlanmalı; testler deterministik ve salt-fonksiyon tarzında olmalı. Confidence: 0.8
- Tekrar eden/aynı işi yapan iki dosya (duplicate helper) bulununca tek noktada birleştirmeyi (merge), mevcut çağıranları kırmadan her iki kullanım stilini de destekleyecek şekilde tercih ediyor. Confidence: 0.7
- İş bitince tek mesajda sabit formatta kısa rapor bekliyor: Status (DONE/PARTIAL/BLOCKED), Changed files, Test sonucu, Build sonucu, Out-of-scope notes, Questions for coordinator. Confidence: 0.8
- Kabul kriteri olarak gerçek build/test komut çıktılarının (ör. ./gradlew :wear:assembleDebug, :wear:test) raporlanması bekleniyor. Confidence: 0.75

