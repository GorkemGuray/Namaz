# Namaz — CI/CD Sürekli Entegrasyon ve Dağıtım Planı

## 1. Genel Bakış

Bu pipeline, **Git tag** bazlı çalışan bir GitHub Actions workflow'udur. Amaç, her yeni sürüm etiketi (`v1.2.3`) push'landığında otomatik olarak:

- **`:app`** (telefon) ve **`:wear`** (Wear OS) modüllerinin **debug APK**'larını derlemek
- APK'ları sürüm bilgisiyle adlandırmak (`Namaz-1.2.3-app-debug.apk`)
- GitHub Release sayfası oluşturmak ve APK'ları bu release'e assets olarak eklemek

### Pipeline sonucu oluşan çıktılar

| Dosya | Açıklama |
|-------|----------|
| `Namaz-{versionName}-app-debug.apk` | Telefon modülü debug APK |
| `Namaz-{versionName}-wear-debug.apk` | Wear OS modülü debug APK |

---

## 2. Ön Koşullar

### 2.1 GitHub Repo Ayarları

- Repository'de **Actions** sekmesi açık olmalıdır (varsayılan olarak açıktır)
- Repository **public** veya **private** olabilir
- Gerekli izinler: `contents: write` (release oluşturmak için)

### 2.2 Yerel Geliştirme Ortamı

Pipeline **GitHub Actions üzerinde** çalışır. Yerel ortamda herhangi bir ek kurulum gerekmez. Ancak tag oluşturmak için git gerekir:

```bash
git tag v1.0.0
git push origin v1.0.0
```

### 2.3 İmzalama (Gelecek)

Pipeline şu an **debug APK** üretir. İmzalı release APK için bir keystore dosyası ve GitHub Secrets hazırlığı gerekir (Bölüm 6'da detaylandırılmıştır).

### 2.4 JDK Sürümü (Yerel vs CI)

Yerel derleme ile CI derlemesi farklı JDK sürümleri kullanır; bu kasıtlıdır ve iki ortam birbirinin yerine geçmez:

| Ortam | JDK | Kaynak |
|-------|-----|--------|
| GitHub Actions (CI) | **17** (Temurin) | `.github/workflows/release.yml` → `actions/setup-java` |
| Yerel geliştirme | **11 veya üzeri** (JDK 25 ile de derlenir) | `java-version` toolchain kullanılmaz; `JAVA_HOME`/PATH'teki JDK ile çalışır |

> **Neden farklı?** `release.yml` sabit olarak JDK 17 kurar; bu değer CI için güvenli bir tabandır. Yerelde ise tek bir sürüm zorunlu değildir — Gradle derleme JDK'sını çalıştığı ortamdan alır. JDK 25 (LTS) ile de `./gradlew :app:assembleDebug :wear:assembleDebug` sorunsuz çalışmaktadır.

Kritik nokta: her iki modülde de `compileOptions` **Java 11** seviyesindedir (`sourceCompatibility`/`targetCompatibility = JavaVersion.VERSION_11`, mobilde ayrıca core library desugaring açık). Yani "JDK sürümü" derlemenin *çalıştığı* JVM'i, "Java 11" ise *üretilen bytecode seviyesini* tanımlar. JDK 17 veya 25 ile derlemek, Java 11 uyumlu çıktı üretmek anlamına gelir.

---

## 3. Gradle Değişiklikleri

### 3.1 `app/build.gradle.kts`

CI/CD için gereken değişiklikler **uygulanmış ve commit'lenmiş durumdadır**. Aşağıdaki blok, dosyanın CI ile ilgili kısımlarının güncel halidir; tam ve tek doğruluk kaynağı `app/build.gradle.kts` dosyasının kendisidir.

```kotlin
android {
    namespace = "co.gorkem.namaz"
    compileSdk = 36

    defaultConfig {
        applicationId = "co.gorkem.namaz"
        minSdk = 24
        targetSdk = 36
        // CI'dan -P ile override edilir; varsayılan değerler
        versionCode = getVersionCode()
        versionName = getVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// --- Versiyon Yardımcı Fonksiyonları ---

fun getVersionCode(): Int {
    return (project.findProperty("ciVersionCode") as? String)?.toIntOrNull() ?: 1
}

fun getVersionName(): String {
    return (project.findProperty("ciVersionName") as? String) ?: "1.0"
}
```

> **Not:** `signingConfigs` bloğu tanımlı olsa da `release` build type içinde `signingConfig = ...` atanmamıştır; yani imzalama pasiftir. Keystore hazır olduğunda ilgili atama eklenir (bkz. Bölüm 6).

### 3.2 `wear/build.gradle.kts`

Aynı pattern Wear OS modülünde de uygulanmıştır. **Önemli fark:** `namespace` `co.gorkem.namaz.wear` olsa da `applicationId` mobil modülle **aynıdır**:

```kotlin
android {
    namespace = "co.gorkem.namaz.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "co.gorkem.namaz"   // mobil modülle AYNI olmalı
        minSdk = 30
        targetSdk = 36
        versionCode = getVersionCode()
        versionName = getVersionName()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}
```

> #### ⚠️ Neden `applicationId` aynı olmalı?
>
> Mobil ve Wear OS modülleri **Data Layer API** (`com.google.android.gms.wearable`) üzerinden haberleşir. Data Layer, iki uygulamayı aynı uygulama kimliği altında eşleştirerek veri taşır; farklı `applicationId` değerleri iki modülü iki ayrı uygulama olarak görür ve **saat hiçbir zaman telefondan vakit verisi alamaz**. Ayrıca iki ayrı `applicationId`, aynı cihazda iki ayrı uygulama kaydı ve iki ayrı izin/veri deposu demektir; kullanıcı iki uygulamayı da ayrı ayrı yönetmek zorunda kalır ve senkronizasyon sessizce bozulur.
>
> `namespace` ise Kotlin paket yapısını ve `R` sınıfının üretildiği paketi belirlediği için farklı kalabilir ve farklı olması gerekir. Yani doğru kurgu şudur:
>
> | Alan | `app` | `wear` |
> |------|-------|--------|
> | `namespace` | `co.gorkem.namaz` | `co.gorkem.namaz.wear` |
> | `applicationId` | `co.gorkem.namaz` | `co.gorkem.namaz` (**aynı olmalı**) |

---

## 4. GitHub Actions Workflow: `.github/workflows/release.yml`

### 4.1 Dizin Yapısı

Proje kökünde aşağıdaki dizini ve dosyayı oluşturun:

```
Namaz/
└── .github/
    └── workflows/
        └── release.yml
```

### 4.2 `release.yml` — Tam İçerik

```yaml
# ============================================================
# Namaz CI/CD — Release Pipeline
# Tetikleyici: vX.Y.Z formatındaki git tag push'ları
# Çıktı: Debug APK'lar → GitHub Release
# ============================================================

name: Namaz Release Pipeline

on:
  push:
    tags:
      - 'v*'           # v1.0.0, v2.3.4-rc1 vb.

jobs:

  # -----------------------------------------------------------
  # Job 1: Derleme ve Release (Debug APK)
  # -----------------------------------------------------------
  build-and-release:
    name: Build & Release Debug APKs
    runs-on: ubuntu-latest

    permissions:
      contents: write        # Release oluşturmak için gerekli

    steps:

      # 1. Kaynak kodunu checkout et
      - name: Checkout repository
        uses: actions/checkout@v4
        with:
          fetch-depth: 0      # Tüm git geçmişi (tag sayımı için)

      # 2. JDK 17 kurulumu (Gradle 9.x + AGP 9.x gereksinimi)
      - name: Setup JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      # 3. Gradle wrapper'ı cache'le
      - name: Cache Gradle packages
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle.kts', '**/gradle/libs.versions.toml', '**/gradle/wrapper/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      # 4. (Wrapper doğrulama adımı planda önerilmişti, gerçek workflow'ta YOK)

      # 5. Versiyon bilgilerini git tag'den çıkar
      - name: Extract version from git tag
        id: version
        run: |
          TAG_NAME="${GITHUB_REF_NAME}"
          echo "Tag: ${TAG_NAME}"

          # "v" ön ekini kaldır → "1.2.3"
          VERSION_NAME="${TAG_NAME#v}"
          echo "Version name: ${VERSION_NAME}"
          echo "versionName=${VERSION_NAME}" >> "$GITHUB_OUTPUT"

          # versionCode: TAG SAYISI (kullanılan tek yöntem)
          TAG_COUNT=$(git tag --list | wc -l | xargs)
          echo "Tag count: ${TAG_COUNT}"
          echo "versionCode=${TAG_COUNT}" >> "$GITHUB_OUTPUT"

      # 6. Debug APK'ları derle
      - name: Build debug APKs
        run: |
          ./gradlew :app:assembleDebug :wear:assembleDebug \
            -PciVersionName=${{ steps.version.outputs.versionName }} \
            -PciVersionCode=${{ steps.version.outputs.versionCode }} \
            --no-daemon

      # 7. APK'ları sürüm adıyla yeniden adlandır
      - name: Rename APKs
        id: artifacts
        run: |
          VERSION="${{ steps.version.outputs.versionName }}"

          # Telefon APK
          APP_APK="app/build/outputs/apk/debug/app-debug.apk"
          APP_APK_RENAMED="Namaz-${VERSION}-app-debug.apk"
          if [ -f "$APP_APK" ]; then
            cp "$APP_APK" "$APP_APK_RENAMED"
            echo "appApk=${APP_APK_RENAMED}" >> "$GITHUB_OUTPUT"
            echo "App APK ready: ${APP_APK_RENAMED}"
          else
            echo "ERROR: App APK not found at ${APP_APK}"
            exit 1
          fi

          # Wear OS APK
          WEAR_APK="wear/build/outputs/apk/debug/wear-debug.apk"
          WEAR_APK_RENAMED="Namaz-${VERSION}-wear-debug.apk"
          if [ -f "$WEAR_APK" ]; then
            cp "$WEAR_APK" "$WEAR_APK_RENAMED"
            echo "wearApk=${WEAR_APK_RENAMED}" >> "$GITHUB_OUTPUT"
            echo "Wear APK ready: ${WEAR_APK_RENAMED}"
          else
            echo "ERROR: Wear APK not found at ${WEAR_APK}"
            exit 1
          fi

      # 8. Git tag mesajını changelog olarak kullan
      - name: Generate release notes
        id: release_notes
        run: |
          TAG_NAME="${GITHUB_REF_NAME}"
          NOTES=$(git tag -l --format='%(contents)' "${TAG_NAME}" 2>/dev/null || echo "")
          if [ -z "$NOTES" ]; then
            NOTES="Namaz sürümü ${TAG_NAME#v} yayınlandı."
          fi
          # Çok satırlı çıktıyı delimiter ile güvenli şekilde aktar
          DELIMITER="RELEASE_NOTES_EOF"
          echo "notes<<${DELIMITER}" >> "$GITHUB_OUTPUT"
          echo "$NOTES" >> "$GITHUB_OUTPUT"
          echo "${DELIMITER}" >> "$GITHUB_OUTPUT"

      # 9. GitHub Release oluştur ve APK'ları yükle
      #    NOT: Release, build edilen bu repo'da değil, herkesa açık
      #    GorkemGuray/Namaz-App reposunda oluşturulur (RELEASE_REPO_TOKEN).
      - name: Create Release and Upload Assets
        uses: softprops/action-gh-release@v2
        with:
          repository: GorkemGuray/Namaz-App
          token: ${{ secrets.RELEASE_REPO_TOKEN }}
          name: Namaz ${{ steps.version.outputs.versionName }}
          body: ${{ steps.release_notes.outputs.notes }}
          files: |
            ${{ steps.artifacts.outputs.appApk }}
            ${{ steps.artifacts.outputs.wearApk }}
          draft: false
          prerelease: false

      # 10. version.json üret ve web sitesiyle birlikte gh-pages'e pushla
      #     (yalnızca RELEASE_REPO_TOKEN tanımlıysa çalışır)
      - name: Push version.json to public release repo
        if: env.RELEASE_REPO_TOKEN != ''
        env:
          RELEASE_REPO_TOKEN: ${{ secrets.RELEASE_REPO_TOKEN }}
        run: |
          # version.json üretimi → website/* kopyalanır → gh-pages'e commit + push
          # (tam gövde için .github/workflows/release.yml dosyasına bakınız)

  # -----------------------------------------------------------
  # Job 2 (Gelecek): İmzalı Release APK
  # -----------------------------------------------------------
  # build-and-sign:
  #   name: Build & Sign Release APKs
  #   needs: build-and-release
  #   runs-on: ubuntu-latest
  #   if: false   # Şu an pasif
  #
  #   steps:
  #     ... (Keystore Secrets ile imzalama)
```

---

## 5. Versiyon Stratejisi

### 5.1 Genel Yaklaşım

Pipeline, versiyon bilgilerini **git tag**'den türetir:

| Bilgi | Kaynak | Örnek |
|-------|--------|-------|
| `versionName` | Git tag (`v` ön eki kaldırılır) | `v1.2.3` → `1.2.3` |
| `versionCode` | Mevcut tag sayısı | 10 tag → `10` |

### 5.2 Gradle'a Aktarım

Her iki değer `-P` parametresiyle Gradle build'ine iletilir:

```bash
./gradlew :app:assembleDebug :wear:assembleDebug \
  -PciVersionName="1.2.3" \
  -PciVersionCode="10"
```

Gradle tarafında `project.findProperty("ciVersionName")` ile okunur.

### 5.3 Tag Sayısı Yöntemi (tek aktif yöntem)

- `release.yml` **yalnızca** bu yöntemi kullanır: `TAG_COUNT=$(git tag --list | wc -l | xargs)`
- Her yeni sürümde `versionCode` bir artar
- Dezavantaj: Tag silinirse sayı değişir (nadir durum)

### 5.4 Tarih Tabanlı Yöntem (KULLANILMIYOR)

> ⚠️ **Bu yöntem artık kullanılmıyor.** Planın önceki hâlinde `release.yml` içinde yorum satırı olarak bir alternatif olarak geçiyordu; gerçek workflow'tan bu yorum satırları kaldırılmıştır. Aşağıdaki bilgi yalnızca geçmiş yöntemi belgelemek için tutulmaktadır — kod değişikliği gerektirmeden bu yönteme geçmek isterseniz `release.yml` içindeki "Extract version from git tag" adımını güncellemeniz gerekir.

- `versionCode = $(date -u +"%Y%m%d%H%M")` formatı
- Örnek: `202605261732` (26 Mayıs 2026, 17:32 UTC)
- Avantaj: Eşsiz ve monoton artan
- Dezavantaj: 32-bit integer sınırına 2147'de yaklaşılır (Android için sorun değil)

---

## 6. GitHub Secrets Hazırlığı (Gelecek)

### 6.1 Gerekli Secret'lar

İmzalı APK üretimi için aşağıdaki secret'lar GitHub'a eklenmelidir:

| Secret Adı | Açıklama |
|------------|----------|
| `KEYSTORE_BASE64` | Keystore dosyasının base64 kodlanmış hali |
| `KEYSTORE_PASSWORD` | Keystore şifresi |
| `KEY_ALIAS` | Anahtar alias adı |
| `KEY_PASSWORD` | Anahtar şifresi |

### 6.2 Keystore Oluşturma

```bash
# Yerel ortamda keystore oluştur
keytool -genkey -v -keystore namaz-keystore.jks \
  -alias namaz-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass Sifre123 \
  -keypass Sifre123

# Base64'e çevir (GitHub'a yüklemek için)
base64 -i namaz-keystore.jks -o namaz-keystore.base64
# macOS: base64 -i namaz-keystore.jks -o namaz-keystore.base64
# Linux:  base64 namaz-keystore.jks > namaz-keystore.base64
```

### 6.3 Secret'ları Ekleme

1. GitHub → Repo → Settings → Secrets and variables → Actions
2. "New repository secret" butonuna tıklayın
3. Aşağıdaki secret'ları tek tek ekleyin:
   - `KEYSTORE_BASE64` → `namaz-keystore.base64` dosyasının içeriği
   - `KEYSTORE_PASSWORD` → Keystore şifresi
   - `KEY_ALIAS` → `namaz-key`
   - `KEY_PASSWORD` → Anahtar şifresi

### 6.4 İmzalı Build Step (Gelecek Workflow Eki)

```yaml
- name: Decode keystore
  run: |
    echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks

- name: Build signed release APK
  run: |
    ./gradlew :app:assembleRelease :wear:assembleRelease \
      -PciVersionName=${{ steps.version.outputs.versionName }} \
      -PciVersionCode=${{ steps.version.outputs.versionCode }} \
      -DKEYSTORE_PATH=${{ github.workspace }}/keystore.jks \
      -DKEYSTORE_PASSWORD=${{ secrets.KEYSTORE_PASSWORD }} \
      -DKEY_ALIAS=${{ secrets.KEY_ALIAS }} \
      -DKEY_PASSWORD=${{ secrets.KEY_PASSWORD }} \
      --no-daemon
```

---

## 7. Release İş Akışı

### 7.1 Yeni Sürüm Yayınlama

Bir sürüm yayınlamak için aşağıdaki adımlar izlenir:

```bash
# 1. Değişiklikleri commit'leyin
git add -A
git commit -m "feat: yeni özellikler eklendi"

# 2. Versiyon tag'i oluşturun
git tag v1.0.0

# 3. Tag'i GitHub'a push'layın (CI'ı tetikler)
git push origin v1.0.0
```

### 7.2 Pipeline Sonrası

1. GitHub Actions sekmesinde **Namaz Release Pipeline** çalışmaya başlar
2. Build başarılı olursa:
   - **Releases** sayfasında yeni bir release görünür
   - Release adı: `Namaz 1.0.0`
   - Açıklama: Tag mesajı (veya varsayılan metin)
   - Assets: 2 adet APK dosyası
3. Kullanıcılar release sayfasından APK'ları indirebilir

### 7.3 Ekran Görüntüsü (Metin Temsili)

```
GitHub Repo → Releases
  ├── Namaz 1.0.0        ← Yeni oluşturuldu
  │   ├── Namaz-1.0.0-app-debug.apk
  │   ├── Namaz-1.0.0-wear-debug.apk
  │   └── Release notes...
  └── (Gelecek sürümler)
```

---

## 8. Gelecek İyileştirmeler

### 8.1 İmzalı Release APK

- `signingConfigs` içindeki placeholder yapı aktifleştirilir
- GitHub Secrets'den keystore okunup imzalı release APK üretilir
- İmzalı APK, debug APK'ya ek olarak release'e eklenir

### 8.2 Play Store'a Otomatik Yayınlama

**Seçenek A — Gradle Play Publisher:**

```kotlin
// build.gradle.kts (app)
plugins {
    id("com.github.triplet.play") version "3.11.0"
}

play {
    serviceAccountCredentials.set(file("play-store-key.json"))
}
```

```yaml
- name: Publish to Play Store
  run: ./gradlew :app:publishReleaseBundle
  env:
    PLAY_STORE_KEY: ${{ secrets.PLAY_STORE_KEY_JSON }}
```

**Seçenek B — GitHub Action:**

```yaml
- name: Upload to Play Store
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_KEY_JSON }}
    packageName: co.gorkem.namaz
    releaseFiles: app/build/outputs/bundle/release/app-release.aab
    track: production
```

### 8.3 Otomatik Değişiklik Günlüğü (Changelog)

```yaml
- name: Generate changelog
  uses: mikepenz/release-changelog-builder-action@v5
  with:
    commitMode: true
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### 8.4 Derleme Öncesi Kontroller

Release build'inden önce lint ve test çalıştırılabilir:

```yaml
- name: Run lint and tests
  run: |
    ./gradlew :app:lintDebug :wear:lintDebug
    ./gradlew test
```

### 8.5 Çoklu Dağıtım Kanalları

| Track | Açıklama |
|-------|----------|
| `internal` | İç test ekibi |
| `alpha` | Erken erişim |
| `beta` | Geniş test |
| `production` | Herkese açık |

---

## Ek A: Workflow Şeması

```
Git Tag Push (v1.2.3)
       │
       ▼
┌──────────────────────────────┐
│ Checkout + JDK 17 Setup      │
│ + Gradle Cache               │
└──────────┬───────────────────┘
           ▼
┌──────────────────────────────┐
│ Extract Version              │
│ versionName ← v1.2.3 → 1.2.3│
│ versionCode ← tag count     │
└──────────┬───────────────────┘
            ▼
┌──────────────────────────────┐
│ Build Debug APKs             │
│ ./gradlew :app:assembleDebug │
│ ./gradlew :wear:assembleDebug│
└──────────┬───────────────────┘
            ▼
┌──────────────────────────────┐
│ Rename APKs                  │
│ Namaz-1.2.3-app-debug.apk   │
│ Namaz-1.2.3-wear-debug.apk  │
└──────────┬───────────────────┘
            ▼
┌──────────────────────────────┐
│ Create GitHub Release        │
│ (GorkemGuray/Namaz-App)     │
│ + Upload Assets              │
└──────────┬───────────────────┘
            ▼
┌──────────────────────────────┐
│ version.json üret + gh-pages │
│ push (RELEASE_REPO_TOKEN)    │
└──────────────────────────────┘
```

---

## Ek B: Dosya ve Dizin Özeti

```
Namaz/
├── .github/
│   └── workflows/
│       ├── release.yml            # uygulama release pipeline'ı
│       └── deploy-website.yml     # web sitesi dağıtımı
├── app/
│   └── build.gradle.kts           # applicationId = co.gorkem.namaz
├── wear/
│   └── build.gradle.kts           # applicationId = co.gorkem.namaz (mobille aynı)
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/
    ├── libs.versions.toml
    └── wrapper/
        └── gradle-wrapper.properties   # Gradle 9.6.0
```

---

> **Hazırlayan:** Namaz Geliştirme Ekibi
> **Son Güncelleme:** 29 Eylül 2026 — gerçek `build.gradle.kts`, `libs.versions.toml` ve `.github/workflows/release.yml` dosyalarıyla senkronize edildi.
