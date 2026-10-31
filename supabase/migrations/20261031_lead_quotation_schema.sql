-- ====================================================================
-- HAZE SERVICES - LEAD & QUOTATION SYSTEM ENHANCEMENT
-- Run this script in the Supabase SQL Editor: https://aqksqqjoioztotizzvad.supabase.co
-- ====================================================================

-- 1. Ensure uuid-ossp extension is enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Relax foreign key on profiles so partners can register in Demo / Admin mode
ALTER TABLE public.profiles DROP CONSTRAINT IF EXISTS profiles_id_fkey;

-- 3. Allow provider_id to be NULL initially when customer requests a service
--    (So it can be broadcast to providers in that city until accepted/quoted)
ALTER TABLE public.leads ALTER COLUMN provider_id DROP NOT NULL;

-- 4. Add Quotation and Location columns to public.leads table
ALTER TABLE public.leads 
    ADD COLUMN IF NOT EXISTS city TEXT NOT NULL DEFAULT 'Bhopal',
    ADD COLUMN IF NOT EXISTS customer_id TEXT,
    ADD COLUMN IF NOT EXISTS customer_address TEXT,
    ADD COLUMN IF NOT EXISTS visiting_charges NUMERIC(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS quoted_amount NUMERIC(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS estimated_arrival TEXT,
    ADD COLUMN IF NOT EXISTS provider_notes TEXT;

-- 5. Update status constraint to include 'quoted' and 'cancelled'
ALTER TABLE public.leads DROP CONSTRAINT IF EXISTS leads_status_check;
ALTER TABLE public.leads ADD CONSTRAINT leads_status_check 
    CHECK (status IN ('new', 'quoted', 'accepted', 'rejected', 'completed', 'cancelled'));

-- 6. Performance Indexes for City & Category matching
CREATE INDEX IF NOT EXISTS idx_leads_city_category ON public.leads(city, service_category);
CREATE INDEX IF NOT EXISTS idx_leads_customer_phone ON public.leads(customer_phone);
CREATE INDEX IF NOT EXISTS idx_leads_provider_id ON public.leads(provider_id);
CREATE INDEX IF NOT EXISTS idx_leads_status ON public.leads(status);

-- 7. Configure Row Level Security (RLS) Policies for profiles
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow all operations for profiles" ON public.profiles;
CREATE POLICY "Allow all operations for profiles"
    ON public.profiles FOR ALL
    USING (true)
    WITH CHECK (true);

-- 8. Configure Row Level Security (RLS) Policies for leads
ALTER TABLE public.leads ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Allow insert of service leads" ON public.leads;
CREATE POLICY "Allow insert of service leads"
    ON public.leads FOR INSERT
    WITH CHECK (true);

DROP POLICY IF EXISTS "Allow select of service leads" ON public.leads;
CREATE POLICY "Allow select of service leads"
    ON public.leads FOR SELECT
    USING (true);

DROP POLICY IF EXISTS "Allow update of service leads" ON public.leads;
CREATE POLICY "Allow update of service leads"
    ON public.leads FOR UPDATE
    USING (true)
    WITH CHECK (true);
