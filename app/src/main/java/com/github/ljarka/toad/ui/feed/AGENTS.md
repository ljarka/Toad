# app/ui/feed — AGENTS.md

Home feed screen (T-008): mushrooms in season + community photo feed.
Probability display on home screen (T-012): gauge card between season section and feed.

## Files

- `FeedScreen.kt` — main composable; uses `PullToRefreshBox`, infinite scroll, realtime prepend.
- `FeedViewModel.kt` — `FeedState` + pagination (`loadMore`) + pull-to-refresh + realtime via `SupabaseClient.channel`; loads probability via `ProbabilityRepository` for Warsaw (52.2, 21.0) on init.
- `SeasonCard.kt` — `SeasonSection` header with current-month species in a `LazyRow`.
- `FeedPhotoCard.kt` — community photo card: avatar initial, photo placeholder, species tag, relative time.
- `ProbabilityCard.kt` — probability card with `ProbabilityGauge`, label, score, and expandable `FactorBreakdownTable`. Shows loading state when `ProbabilityResult` is null.
- `ProbabilityGauge.kt` — animated arc gauge (Canvas, `animateFloatAsState`); score 0–100; color driven by caller.
- `HomeScreen.kt` — legacy placeholder kept for `HomeScreenScreenshotTest`; not used in navigation.

## Notes

- `FeedScreen` is wired into `MainActivity`'s `AppNavGraph` at route `"home"`.
- Photo thumbnails are placeholder (🍄 emoji) until T-009 adds Supabase Storage URL loading.
- Realtime subscription silently fails if Supabase URL is a placeholder; feed still shows data from HTTP.
- Probability uses a fixed default location (Warsaw) until a location service is added.
