-- ====================================================================
-- HAZE SERVICES PROVIDER - SUPABASE DATABASE SETUP (COMPLETE)
-- ====================================================================

-- 1. Enable Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Service Categories Table
CREATE TABLE IF NOT EXISTS public.service_categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    icon_name TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 3. Profiles Table (Linked with Supabase Auth users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    phone_number TEXT NOT NULL,
    secondary_phone_number TEXT,
    city TEXT NOT NULL,
    full_address TEXT NOT NULL,
    service_category TEXT NOT NULL,
    custom_service_category TEXT,
    profile_photo_url TEXT,
    profile_completed BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 4. Leads Table
CREATE TABLE IF NOT EXISTS public.leads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    customer_name TEXT NOT NULL,
    customer_phone TEXT NOT NULL,
    service_category TEXT NOT NULL,
    description TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'new' CHECK (status IN ('new', 'accepted', 'rejected', 'completed')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- 5. Enable Row Level Security (RLS)
ALTER TABLE public.service_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.leads ENABLE ROW LEVEL SECURITY;

-- 6. Policies (Drop existing first to avoid duplicate policy error on rerun)
DROP POLICY IF EXISTS "Allow public read access to active service categories" ON public.service_categories;
CREATE POLICY "Allow public read access to active service categories"
    ON public.service_categories FOR SELECT
    USING (is_active = true);

DROP POLICY IF EXISTS "Providers can view own profile" ON public.profiles;
CREATE POLICY "Providers can view own profile"
    ON public.profiles FOR SELECT TO authenticated
    USING (auth.uid() = id);

DROP POLICY IF EXISTS "Providers can insert own profile" ON public.profiles;
CREATE POLICY "Providers can insert own profile"
    ON public.profiles FOR INSERT TO authenticated
    WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "Providers can update own profile" ON public.profiles;
CREATE POLICY "Providers can update own profile"
    ON public.profiles FOR UPDATE TO authenticated
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "Providers can delete own profile" ON public.profiles;
CREATE POLICY "Providers can delete own profile"
    ON public.profiles FOR DELETE TO authenticated
    USING (auth.uid() = id);

DROP POLICY IF EXISTS "Providers can view own leads" ON public.leads;
CREATE POLICY "Providers can view own leads"
    ON public.leads FOR SELECT TO authenticated
    USING (auth.uid() = provider_id);

DROP POLICY IF EXISTS "Providers can update own leads" ON public.leads;
CREATE POLICY "Providers can update own leads"
    ON public.leads FOR UPDATE TO authenticated
    USING (auth.uid() = provider_id)
    WITH CHECK (auth.uid() = provider_id);

-- 7. Seed Service Categories
INSERT INTO public.service_categories (id, name, icon_name, display_order) VALUES
    ('plumber', 'Plumber', 'Plumbing', 1),
    ('electrician', 'Electrician', 'ElectricBolt', 2),
    ('painter', 'Painter', 'FormatPaint', 3),
    ('carpenter', 'Carpenter', 'Carpenter', 4),
    ('ac_technician', 'AC Technician', 'AcUnit', 5),
    ('tv_repair', 'TV Repair', 'Tv', 6),
    ('refrigerator_repair', 'Refrigerator Repair', 'Kitchen', 7),
    ('washing_machine_repair', 'Washing Machine Repair', 'LocalLaundryService', 8),
    ('ro_repair', 'RO Water Purifier Repair', 'WaterDrop', 9),
    ('mobile_repair', 'Mobile Repair', 'Smartphone', 10),
    ('computer_repair', 'Computer Repair', 'Computer', 11),
    ('mechanic', 'Mechanic', 'Build', 12),
    ('cleaning_services', 'Cleaning Services', 'CleaningServices', 13),
    ('pest_control', 'Pest Control', 'PestControl', 14),
    ('salon_services', 'Salon Services', 'ContentCut', 15),
    ('appliance_repair', 'Appliance Repair', 'HomeRepairService', 16),
    ('other', 'Other', 'Category', 99)
ON CONFLICT (id) DO UPDATE 
SET name = EXCLUDED.name,
    icon_name = EXCLUDED.icon_name,
    display_order = EXCLUDED.display_order;

-- 8. Storage Bucket for Profile Photos
INSERT INTO storage.buckets (id, name, public)
VALUES ('profile-photos', 'profile-photos', true)
ON CONFLICT (id) DO NOTHING;

DROP POLICY IF EXISTS "Public profile photos access" ON storage.objects;
CREATE POLICY "Public profile photos access"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'profile-photos');

DROP POLICY IF EXISTS "Providers can upload their own profile photo" ON storage.objects;
CREATE POLICY "Providers can upload their own profile photo"
    ON storage.objects FOR INSERT TO authenticated
    WITH CHECK (
        bucket_id = 'profile-photos' 
        AND (storage.foldername(name))[1] = auth.uid()::text
    );
