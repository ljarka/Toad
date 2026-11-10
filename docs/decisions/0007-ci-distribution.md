# 0007 — CI and distribution: GitHub Actions + Obtainium

Status: Accepted · Date: 2026-11-10

## Decision

**CI**: GitHub Actions — build + lint + unit tests on every push/PR to `main`.
**Distribution**: signed APK published as GitHub Release; user installs updates via Obtainium.

No Google Play in v1 (overkill for a personal/hobby app; Play Store review adds friction).

## CI workflow (`.github/workflows/ci.yml`)

- Trigger: push to `main`, pull requests.
- Steps: checkout → JDK 21 → `./gradlew lintDebug testDebugUnitTest assembleDebug`.
- Upload debug APK as artifact.
- Paparazzi screenshot tests if any screenshot tests exist.

## Release workflow (`.github/workflows/release.yml`)

- Trigger: push of tag `v*` (e.g., `v1.0.0`).
- Steps: build signed APK → `gh release create` with APK attached.
- Signing: keystore from GitHub Secrets (`TOAD_KEYSTORE_BASE64`, `TOAD_KEYSTORE_PASSWORD`,
  `TOAD_KEY_ALIAS`, `TOAD_KEY_PASSWORD`).

## Obtainium

User installs Obtainium, adds source `https://github.com/ljarka/Toad`.
On new release: Obtainium notifies → one tap → installed.

## Supabase credentials in CI

Do NOT put Supabase `service_role` key in CI. Use `anon` key only for integration tests.
Unit tests mock `SupabaseClient` via interface; no network calls in CI.
