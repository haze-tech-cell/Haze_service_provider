# Haze Services Provider - Supabase Setup & Configuration

This guide provides instructions for configuring your Supabase project for the **Haze Services Provider** Android application.

---

## 1. Supabase Project Setup

1. Create a project on [Supabase](https://supabase.com).
2. Go to **Project Settings** > **API**.
3. Note your:
   - **Project URL** (`https://<project-ref>.supabase.co`)
   - **anon / public key** (`eyJhbGciOi...`)
4. Never distribute or embed your `service_role` key in the mobile application.

---

## 2. Database Schema & RLS Setup

1. In your Supabase Dashboard, open **SQL Editor**.
2. Click **New query**.
3. Open `supabase/migrations/20261031_initial_schema.sql` from this repository.
4. Paste the entire SQL content and click **Run**.

This script sets up:
- `public.profiles`: Stores provider information linked to `auth.users(id)`.
- `public.service_categories`: Active service categories with default seeded entries.
- `public.leads`: Future-ready customer enquiries table.
- Row-Level Security (RLS) policies ensuring each provider can only view/edit their own profile and assigned leads.
- Automatic `updated_at` trigger.
- Seed data for 17 service categories (Plumber, Electrician, etc.).

---

## 3. Storage Bucket Configuration

1. In Supabase Dashboard, navigate to **Storage**.
2. Verify that the bucket `profile-photos` has been created (the SQL migration creates it automatically).
3. Ensure it is set to **Public** for avatar reads.
4. The migration script installs RLS policies allowing authenticated providers to upload, update, and delete files inside their own folder:
   `profile-photos/{user_id}/avatar.jpg`

---

## 4. Phone Authentication (SMS OTP) Configuration

1. In Supabase Dashboard, go to **Authentication** > **Providers** > **Phone**.
2. Toggle **Enable Phone Provider** ON.
3. Choose an SMS Provider:
   - **Twilio**: Requires Account SID, Auth Token, and Message Service SID or Twilio Phone Number.
   - **MessageBird / Vonage / Textlocal**: Configure according to Supabase docs.
4. For testing without SMS costs during development:
   - Under **Phone Auth**, expand **Test Phone Numbers**.
   - Add test phone numbers (e.g., `+919876543210` with OTP `123456`).
   - The app verifies real OTPs via Supabase Auth `verifyPhoneOtp(...)` or `signInWith(OTP)`.

---

## 5. Android Application Configuration

Set your Supabase credentials in `local.properties` or as environment variables during build:

```properties
# C:\Haze_Services_provider\local.properties
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=your-actual-anon-key-here
```

Or configure via `com.haze.servicesprovider.utils.AppConfig`.
