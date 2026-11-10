# 0005 — Photo storage: Supabase Storage

Status: Accepted · Date: 2026-11-10

## Decision

User photos stored in **Supabase Storage** bucket `mushroom-photos`.
Path: `{user_id}/{photo_id}.jpg`.

## Why

- Integrated with Supabase Auth for Row Level Security (users can only delete their own files).
- Free tier: 1 GB storage, 2 GB bandwidth/month — enough for a hobby project.
- CDN URLs for fast loading in the community map and feed.

## Photo handling

- App resizes photo to max 1920×1920 before upload (keep file size < 2 MB).
- Thumbnail: 400×400 crop, stored as `{user_id}/{photo_id}_thumb.jpg`.
- EXIF stripped before upload (privacy — location stored separately in DB, not in EXIF).
- Format: JPEG, quality 85%.

## RLS

```sql
-- Only owner can upload/delete; public photos readable by signed URL
create policy "owner upload" on storage.objects for insert with check (
  bucket_id = 'mushroom-photos' and auth.uid()::text = (storage.foldername(name))[1]
);
create policy "owner delete" on storage.objects for delete using (
  bucket_id = 'mushroom-photos' and auth.uid()::text = (storage.foldername(name))[1]
);
create policy "public read" on storage.objects for select using (
  bucket_id = 'mushroom-photos'
);
```

## Android

Upload with `supabase.storage.from("mushroom-photos").upload(path, byteArray)`.
Display with Coil: `AsyncImage(model = supabase.storage.from("mushroom-photos").publicUrl(path))`.
