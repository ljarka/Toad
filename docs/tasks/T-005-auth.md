# T-005 — Auth: login, register, profile

**Status:** done (`71170f2`)  
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

### Access model

**Public (no login required):**
- Home feed — viewing community photos
- Community photo map — browsing photos on the map
- Species guide — reading species descriptions
- Mushroom probability screen — viewing forecasts

**Requires login:**
- Uploading a photo (T-009)
- Recording a trip (T-013, T-014, T-015, T-016)
- Viewing own profile

The app opens directly to the feed/map. The login screen is reachable via a “Sign in” button in the top bar or triggered automatically when the user taps an action that requires auth.

### Navigation
`AuthGraph` → `LoginScreen` ↔ `RegisterScreen`
`AppGraph` → `FeedScreen`, `MapScreen`, ..., `ProfileScreen` (accessible without login)

`MainActivity.kt`: always navigate to `AppGraph` on launch. Auth state is observed via
`supabase.auth.sessionStatus` Flow — UI elements that require login are conditionally
enabled or hidden based on the current session.

When an unauthenticated user triggers an auth-gated action (e.g. upload photo), navigate
to `LoginScreen` with a `returnRoute` argument so the app returns to the right place
after successful login.

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

### Browse-first (new requirement)
- [ ] App opens to home feed without requiring login
- [ ] Community map and species guide are accessible without login
- [ ] “Sign in” entry point visible in the top bar for unauthenticated users
- [ ] Tapping an auth-gated action (e.g. upload photo) navigates to `LoginScreen` with `returnRoute`
- [ ] After successful login, app navigates back to `returnRoute`
- [ ] “Sign out” navigates back to feed (not to login screen)

### Auth basics (done)
- [x] User can register with email + password + username
- [x] User can log in and is redirected to home feed
- [x] Session persists across app kill/restart (via `supabase.auth.sessionStatus`)
- [x] "Sign out" clears session and navigates to login
- [x] Profile screen shows username, email, trip count, and photo count
- [x] Invalid email / password / username → inline validation error
- [x] Wrong password → "Invalid credentials" snackbar
- [x] Network error → "No connection" snackbar

## Notes for implementing agent

- Use `supabase.auth.sessionStatus` Flow to react to auth state changes (e.g., token expired).
- Do NOT store the JWT token manually; supabase-kt handles it in DataStore.
- Username uniqueness: enforce with `UNIQUE` constraint in DB (from migration); handle `409` from
  Supabase with "Username already taken" message.
