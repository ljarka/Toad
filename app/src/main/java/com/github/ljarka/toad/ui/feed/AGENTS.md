# app/ui/feed — AGENTS.md

Home feed screen (T-008): mushrooms in season + community photo feed.

## Files

- `FeedScreen.kt` — main composable; uses `PullToRefreshBox`, infinite scroll, realtime prepend.
- `FeedViewModel.kt` — `FeedState` + pagination (`loadMore`) + pull-to-refresh + realtime via `SupabaseClient.channel`.
- `SeasonCard.kt` — `SeasonSection` header with current-month species in a `LazyRow`.
- `FeedPhotoCard.kt` — community photo card: avatar initial, photo placeholder, species tag, relative time.
- `ProbabilityCard.kt` — placeholder for probability engine (T-011/T-012).
- `HomeScreen.kt` — legacy placeholder kept for `HomeScreenScreenshotTest`; not used in navigation.

## Notes

- `FeedScreen` is wired into `MainActivity`'s `AppNavGraph` at route `"home"`.
- Photo thumbnails are placeholder (🍄 emoji) until T-009 adds Supabase Storage URL loading.
- Realtime subscription silently fails if Supabase URL is a placeholder; feed still shows data from HTTP.
