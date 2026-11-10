# T-018 — Release setup

**Status:** ready  
**Phase:** Etap 9  
**Depends on:** T-004

## Goal

Sign the release APK and publish it via GitHub Releases so Obtainium can auto-update.

## Design

### Keystore generation
```bash
keytool -genkeypair -v \
  -keystore toad-release.jks \
  -alias toad \
  -keyalg RSA -keysize 2048 \
  -validity 10000
```
Store `toad-release.jks` **outside** the repo. Add to `build.gradle.kts` via env variables.

### `build.gradle.kts` signing config
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("TOAD_KEYSTORE_PATH") ?: "keystore.jks")
            storePassword = System.getenv("TOAD_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("TOAD_KEY_ALIAS")
            keyPassword = System.getenv("TOAD_KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

### GitHub Actions release workflow
`.github/workflows/release.yml`:
```yaml
name: Release
on:
  push:
    tags: ['v*']

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: temurin }
      - name: Decode keystore
        run: echo "${{ secrets.TOAD_KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
      - name: Build release APK
        env:
          TOAD_KEYSTORE_PATH: keystore.jks
          TOAD_KEYSTORE_PASSWORD: ${{ secrets.TOAD_KEYSTORE_PASSWORD }}
          TOAD_KEY_ALIAS: ${{ secrets.TOAD_KEY_ALIAS }}
          TOAD_KEY_PASSWORD: ${{ secrets.TOAD_KEY_PASSWORD }}
        run: ./gradlew assembleRelease
      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          files: app/build/outputs/apk/release/app-release.apk
          generate_release_notes: true
```

### GitHub Secrets to configure
| Secret | Value |
|---|---|
| `TOAD_KEYSTORE_BASE64` | `base64 -w 0 toad-release.jks` |
| `TOAD_KEYSTORE_PASSWORD` | keystore password |
| `TOAD_KEY_ALIAS` | `toad` |
| `TOAD_KEY_PASSWORD` | key password |

### Obtainium setup (user's device)
1. Install Obtainium from F-Droid or GitHub
2. Add app → GitHub URL `https://github.com/<owner>/toad`
3. Leave "APK filter" blank (single APK)
4. Enable auto-update

### Release process (agent-triggered)
1. Merge PR to `main`
2. `git tag v1.x.x && git push --tags`
3. GitHub Actions builds and publishes
4. Obtainium detects new release and notifies user

## Acceptance criteria

- [ ] `assembleRelease` builds a signed APK locally with env vars set
- [ ] Pushing a `v*` tag triggers the release workflow
- [ ] Release APK appears as an asset on the GitHub Release
- [ ] Obtainium successfully detects and installs the new version
