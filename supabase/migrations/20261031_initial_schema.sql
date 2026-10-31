-- ====================================================================
-- HAZE SERVICES PROVIDER - SUPABASE DATABASE SCHEMA & RLS POLICIES
-- ====================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. SERVICE CATEGORIES TABLE
CREATE TABLE IF NOT EXISTS public.service_categories (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    icon_name TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- Index on is_active and display_order
CREATE INDEX IF NOT EXISTS idx_service_categories_active ON public.service_categories(is_active, display_order);

-- 3. PROFILES TABLE (Linked with Supabase Auth auth.users)
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

-- Trigger to automatically update updated_at on profiles
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = timezone('utc'::text, now());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_profiles_updated_at ON public.profiles;
CREATE TRIGGER set_profiles_updated_at
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

-- Indexes for profiles
CREATE INDEX IF NOT EXISTS idx_profiles_phone ON public.profiles(phone_number);
CREATE INDEX IF NOT EXISTS idx_profiles_city ON public.profiles(city);
CREATE INDEX IF NOT EXISTS idx_profiles_category ON public.profiles(service_category);

-- 4. LEADS TABLE (Customer Enquiries Architecture)
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

DROP TRIGGER IF EXISTS set_leads_updated_at ON public.leads;
CREATE TRIGGER set_leads_updated_at
    BEFORE UPDATE ON public.leads
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

-- Indexes for leads
CREATE INDEX IF NOT EXISTS idx_leads_provider ON public.leads(provider_id);
CREATE INDEX IF NOT EXISTS idx_leads_status ON public.leads(status);
CREATE INDEX IF NOT EXISTS idx_leads_created ON public.leads(created_at DESC);

-- 5. ROW LEVEL SECURITY (RLS) POLICIES

-- Enable RLS on all tables
ALTER TABLE public.service_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.leads ENABLE ROW LEVEL SECURITY;

-- 5.1 Service Categories Policies
-- Everyone (authenticated and anonymous) can read active categories
CREATE POLICY "Allow public read access to active service categories"
    ON public.service_categories
    FOR SELECT
    USING (is_active = true);

-- 5.2 Profiles Policies
-- Providers can read ONLY their own profile
CREATE POLICY "Providers can view own profile"
    ON public.profiles
    FOR SELECT
    TO authenticated
    USING (auth.uid() = id);

-- Providers can insert their own profile
CREATE POLICY "Providers can insert own profile"
    ON public.profiles
    FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = id);

-- Providers can update only their own profile
CREATE POLICY "Providers can update own profile"
    ON public.profiles
    FOR UPDATE
    TO authenticated
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);

-- Providers can delete their own profile
CREATE POLICY "Providers can delete own profile"
    ON public.profiles
    FOR DELETE
    TO authenticated
    USING (auth.uid() = id);

-- 5.3 Leads Policies
-- Providers can view leads assigned to them
CREATE POLICY "Providers can view own leads"
    ON public.leads
    FOR SELECT
    TO authenticated
    USING (auth.uid() = provider_id);

-- Providers can update the status of leads assigned to them
CREATE POLICY "Providers can update own leads"
    ON public.leads
    FOR UPDATE
    TO authenticated
    USING (auth.uid() = provider_id)
    WITH CHECK (auth.uid() = provider_id);

-- 6. DEFAULT SERVICE CATEGORIES SEED DATA
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

-- 7. STORAGE BUCKET CONFIGURATION FOR PROFILE PHOTOS
-- Note: Create bucket 'profile-photos' in Supabase Storage dashboard
-- Or run via Supabase SQL:
INSERT INTO storage.buckets (id, name, public)
VALUES ('profile-photos', 'profile-photos', true)
ON CONFLICT (id) DO NOTHING;

-- Storage RLS:
-- Anyone can view profile photos
CREATE POLICY "Public profile photos access"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'profile-photos');

-- Authenticated providers can upload photos into their own folder (userId/filename)
CREATE POLICY "Providers can upload their own profile photo"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'profile-photos' 
        AND (storage.foldername(name))[1] = auth.uid()::text
    );

-- Authenticated providers can update/overwrite their own photo
CREATE POLICY "Providers can update their own profile photo"
    ON storage.objects FOR UPDATE
    TO authenticated
    USING (
        bucket_id = 'profile-photos' 
        AND (storage.foldername(name))[1] = auth.uid()::text
    );

-- Authenticated providers can delete their own photo
CREATE POLICY "Providers can delete their own profile photo"
    ON storage.objects FOR DELETE
    TO authenticated
    USING (
        bucket_id = 'profile-photos' 
        AND (storage.foldername(name))[1] = auth.uid()::text
    );
