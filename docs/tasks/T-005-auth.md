# T-005 — Auth: login, register, profile

**Status:** ready  
**Phase:** Etap 2  
**Depends on:** T-004

## Goal

Working login/register flow with Supabase Auth. After login, user has a profile (`profiles` table).
Session persists across app restarts.

## Design

### Screens
- `LoginScreen` — email + password + "Sign in" button + "Create account" link
- `RegisterScreen` — email + password + username + "Create account" button
- `ProfileScreen` — avatar, username, trip count, photo count; "Sign out" button

### Navigation
`AuthGraph` → `LoginScreen` ↔ `RegisterScreen`
`AppGraph` (authenticated) → `FeedScreen`, ..., `ProfileScreen`

`MainActivity.kt`: on launch, check `supabase.auth.currentUserOrNull()`:
- null → navigate to `AuthGraph`
- not null → navigate to `AppGraph`

### New files
- `ui/auth/LoginScreen.kt`
- `ui/auth/RegisterScreen.kt`
- `ui/auth/AuthViewModel.kt`
- `ui/profile/ProfileScreen.kt`
- `ui/profile/ProfileViewModel.kt`
- `data/supabase/AuthRepository.kt`
- `data/supabase/ProfileRepository.kt`

### Auth flow
```kotlin
// Register
supabase.auth.signUpWith(Email) { email = ...; password = ... }
// Insert profile row (triggered automatically via Supabase DB function, or done from app)
supabase.from("profiles").insert(mapOf("id" to userId, "username" to username))

// Login
supabase.auth.signInWith(Email) { email = ...; password = ... }

// Session persistence: supabase-kt handles token refresh automatically
// On cold start: supabase.auth.currentSession → non-null = already logged in
```

### Profile creation trigger (SQL in migration)
```sql
create or replace function handle_new_user()
returns trigger language plpgsql security definer as $$
begin
  insert into public.profiles (id, username)
  values (new.id, new.raw_user_meta_data->>'username');
  return new;
end;
$$;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure handle_new_user();
```
Add this SQL to `supabase/migrations/0002_auth_trigger.sql`.

## Acceptance criteria

- [ ] User can register with email + password + username
- [ ] User can log in and is redirected to home feed
- [ ] Session persists across app kill/restart (no re-login required)
- [ ] "Sign out" clears session and navigates to login
- [ ] Profile screen shows username and email
- [ ] Invalid email → inline validation error
- [ ] Wrong password → "Invalid credentials" snackbar
- [ ] Network error → "No connection" snackbar

## Notes for implementing agent

- Use `supabase.auth.sessionStatus` Flow to react to auth state changes (e.g., token expired).
- Do NOT store the JWT token manually; supabase-kt handles it in DataStore.
- Username uniqueness: enforce with `UNIQUE` constraint in DB (from migration); handle `409` from
  Supabase with "Username already taken" message.
