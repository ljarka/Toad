# 0008 — Guest browsing, login required to create

Status: Accepted

## Decision
Read-only features (home feed, photos, community map, species guide) are available without an
account. Creating or modifying anything (photos, trips, profile) requires a signed-in user.

## Consequences
- One navigation graph (`MainActivity.kt`) starting at `home`; login/register are regular routes
  opened from the home top bar ("Zaloguj się"). Successful sign-in pops back to `home`.
- Every "add" entry point must use `requireLogin(...)`, which sends guests to `login`.
- Backend already matches: RLS allows public `select` (`is_public = true`), writes need `auth.uid()`
  ([0001](0001-backend-supabase.md)). New tables must keep this split; reads must not need a session.
