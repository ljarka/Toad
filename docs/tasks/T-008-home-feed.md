# T-008 — Home screen: season info + community feed

**Status:** ready  
**Phase:** Etap 4  
**Depends on:** T-005, T-006

## Goal

The first screen the user sees after login: what mushrooms are findable right now + community
photos from other users, newest first.

## Design

### Layout (top to bottom)

**Season section** — fixed header card:
- Current month name + mushroom icon
- `LazyRow` of species cards for species in season this month (`getBySeason(currentMonth)`)
- Each card: thumbnail, name, edibility badge (green/orange/red)
- Tapping a species card → `SpeciesDetailScreen`

**Probability indicator** — horizontal card below season section:
- Icon + text: "Warunki grzybowe: Dobre 72%" (computed by T-011; show placeholder "–" until T-011 done)
- Tapping → navigates to map with probability overlay (T-012)

**Community feed** — `LazyColumn` below the fixed header:
- Each item: user avatar + username + "X dni temu", photo (full width), species tag, location name, caption
- Pull-to-refresh: `PullToRefreshBox`
- Pagination: load next 20 items when user scrolls to bottom (`LazyListState.isScrolledToEnd()`)
- Realtime: subscribe to `mushroom_photos` inserts → prepend new item with animation

### Data
```kotlin
// Supabase query — public photos, newest first, with profile join
supabase.from("mushroom_photos")
    .select("*, profiles(username, avatar_url), species(name_pl, edibility)")
    .eq("is_public", true)
    .order("created_at", ascending = false)
    .range(offset, offset + 19)
    .decodeList<FeedPhotoDto>()
```

### New files
- `ui/feed/FeedScreen.kt`
- `ui/feed/FeedViewModel.kt` — season species, feed photos, realtime subscription
- `ui/feed/SeasonCard.kt` — horizontal species row
- `ui/feed/FeedPhotoCard.kt` — community photo item
- `ui/feed/ProbabilityCard.kt` — placeholder for now, wired in T-012
- `data/supabase/FeedRepository.kt`

## Acceptance criteria

- [ ] Season section shows ≥ 3 species for current month
- [ ] Species cards tap to detail screen
- [ ] Community feed shows public photos in reverse chronological order
- [ ] Pull-to-refresh reloads the feed
- [ ] Pagination loads more photos on scroll to bottom
- [ ] New photo uploaded by another user appears at top within 5 s (Realtime)
- [ ] Logged-out users are redirected to login (not shown the feed)
- [ ] Empty state shown when feed has no photos yet
