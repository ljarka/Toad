# app/ui/auth — AGENTS.md

Authentication and user profile screens ([ADR 0001](../../../../../../../../docs/decisions/0001-backend-supabase.md), T-005).

## Files

- `LoginScreen.kt` — Email + password sign-in form with inline validation and error snackbar.
- `RegisterScreen.kt` — Username + email + password registration form with inline validation and error snackbar.
- `ProfileScreen.kt` — User avatar initial badge, username, email, trip/photo counters, and sign-out action.
- `AuthViewModel.kt` — Manages login/register form state, validation (`AuthValidator`), and reactive `sessionState`.
- `ProfileViewModel.kt` — Loads `UserProfile` (`profiles` + `trips` / `mushroom_photos` counts) and triggers sign-out.
