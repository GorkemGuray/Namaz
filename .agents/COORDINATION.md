# Çoklu Agent Geliştirme — Koordinasyon Protokolü

> Bu dosya, bu repoda çoklu agent ile çalışmanın **tek doğruluk kaynağıdır**.
> Coordinator: ana oturum (bu oturum). Worker: görev bazında çağrılan diğer agent'lar.

## 1. Roller

| Rol | Kim | Sorumluluk |
|-----|-----|------------|
| **Coordinator** | Ana oturum | Görevleri `TASKS.md`'de tanımlar, önceliklendirir, worker'a devreder, sonucu **doğrular** (derleme + test + diff incelemesi), merge/commit kararını verir |
| **Worker** | Çağrılan agent | Sadece kendisine atanan `T-XXX` görevini yapar, kapsam dışına çıkmaz, sonucu raporlar, **commit/push yapmaz** |

## 2. Değişmez Kurallar

1. **Tek görev, tek worker.** Aynı anda iki worker aynı dosyaya dokunamaz. Coordinator atama öncesi `TASKS.md` sahiplik alanını kontrol eder.
2. **Worker commit yapmaz.** Worker sadece working tree'de değişiklik bırakır + rapor yazar. Commit yalnızca coordinator tarafından yapılır.
3. **Kapsam disiplini.** Worker, görev kartındaki `Dosyalar` listesindeki dosyalar dışına çıkamaz. Ek dosya gerekiyorsa durur ve coordinator'a sorar.
4. **TDD zorunlu** (AGENTS.md): iş mantığı öncesi test, her görev sonunda `./gradlew test` yeşil.
5. **Doğrulama coordinator'da:** worker "bitti" dese bile coordinator `./gradlew assembleDebug` + `./gradlew test` + `git diff` incelemesi yapmadan görevi kapatmaz.
6. **Türkçe iletişim, İngilizce kod.** Commit mesajları Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`, `chore:`).
7. **Yorum yok** (repo konvansiyonu) — kod içine açıklama yorumu yazılmaz.

## 3. Görev Yaşam Döngüsü

```
PENDING → ASSIGNED → IN_PROGRESS → REVIEW → DONE
              ↓            ↓           ↓
           (coordinator atar) (worker çalışır) (coordinator doğrular)
CANCELLED (ihtiyaç kalkarsa, gerekçesiyle)
```

Durum geçişleri yalnızca coordinator tarafından `TASKS.md`'de işlenir.

## 4. Worker'a Devretme Formatı

Coordinator her görevde worker'a şunları verir:

```markdown
## Görev T-XXX: <başlık>
- Kapsam: <ne yapılacak, ne yapılmayacak>
- Dosyalar: <izinli dosya listesi, ötesi yasak>
- Referans doküman: docs/XX-....md#bolum
- Kabul kriteri: <test + derleme + davranış>
- Rapor formatı: değiştirilen dosyalar, test sonucu, riskler, açık sorular
```

## 5. Worker Rapor Formatı (zorunlu)

Worker işini bitirince coordinator'a **tek mesajda** şunu döndürür:

```markdown
## Rapor T-XXX
- Durum: TAMAMLANDI / KISMEN / BLOKELENDI
- Değiştirilen dosyalar: <liste>
- Test: `./gradlew test` → <sonuç>
- Derleme: `./gradlew assembleDebug` → <sonuç>
- Kapsam dışı notlar: <varsa>
- Coordinator'a sorular: <varsa>
```

## 6. Çakışma Önleme

- Atama öncesi: `git status --short` temiz mi + hedef dosyalar başka ASSIGNED/IN_PROGRESS görevde mi kontrol edilir.
- Worker çalışırken coordinator o dosyalara kendisi dokunmaz.
- REVIEW aşamasında çakışma çıkarsa coordinator çözer, worker'a geri göndermez (küçükse); büyükse yeni görev açılır.

## 7. Dosya Haritası

```
.agents/
├── COORDINATION.md   ← bu dosya (protokol)
├── TASKS.md          ← görev kuyruğu (tek doğruluk kaynağı)
└── WORKER_BRIEF.md   ← worker'ın göreve başlamadan önce okuduğu brifing
docs/
├── 07-gelistirme-plani-roadmap.md  ← faz planı (görevlerin kaynağı)
└── 08-bilinen-sorunlar-tech-debt.md ← teknik borç (görevlerin kaynağı)
```
