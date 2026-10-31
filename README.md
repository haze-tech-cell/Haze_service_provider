# Haze Services Provider - Android Application

<p align="center">
  <img src="app/src/main/res/drawable/ic_haze_logo.xml" width="100" height="100" alt="Haze Services Logo" />
</p>

A production-quality Android application built for local service providers (Plumbers, Electricians, Painters, Carpenters, Technicians, Mechanics, Cleaners, etc.) to register their business, manage their profile, and receive customer enquiries.

---

## 1. Project Overview & Architecture

- **Platform**: Android 8.0+ (Min SDK 26, Target SDK 36, Compile SDK 36)
- **Programming Language**: Kotlin (2.3)
- **UI Framework**: 100% Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Clean Architecture with Coroutines & StateFlow
- **Backend & Auth**: Supabase Phone OTP Authentication + PostgreSQL Database + Storage
- **Image Loading**: Coil Compose
- **Navigation**: Android Navigation Compose with deep linking & state preservation

### Package Structure

```
com.haze.servicesprovider/
├── HazeApplication.kt               # Application bootstrap & ServiceLocator initialization
├── MainActivity.kt                  # Single-activity container with Compose & Edge-to-Edge
├── data/
│   ├── local/
│   │   └── SessionManager.kt        # Encrypted/Cached provider session & profile state
│   ├── remote/
│   │   ├── SupabaseClientProvider.kt # Supabase Client (Auth, Postgrest, Storage, Realtime)
│   │   └── dto/                     # Kotlinx Serialization DTOs
│   │       ├── ProfileDto.kt
│   │       ├── ServiceCategoryDto.kt
│   │       └── LeadDto.kt
│   └── repository/                  # Repository implementations with Dispatchers.IO
│       ├── AuthRepositoryImpl.kt
│       ├── ProfileRepositoryImpl.kt
│       ├── CategoryRepositoryImpl.kt
│       └── LeadRepositoryImpl.kt
├── domain/
│   ├── model/                       # Domain Entities
│   │   ├── UserProfile.kt
│   │   ├── ServiceCategory.kt
│   │   ├── Lead.kt
│   │   ├── LeadStatus.kt
│   │   └── AuthState.kt
│   ├── repository/                  # Domain Repository Contracts
│   │   ├── AuthRepository.kt
│   │   ├── ProfileRepository.kt
│   │   ├── CategoryRepository.kt
│   │   └── LeadRepository.kt
│   └── usecase/                     # Business Logic Use Cases
│       ├── AuthUseCases.kt
│       ├── ProfileUseCases.kt
│       ├── CategoryUseCases.kt
│       └── LeadUseCases.kt
├── di/
│   └── ServiceLocator.kt            # Clean dependency injection container
├── presentation/
│   ├── theme/                       # Design System (#1769E0, typography, shapes)
│   ├── components/                  # Reusable Compose Widgets
│   ├── navigation/                  # Routes, Bottom Navigation, NavGraph
│   ├── splash/                      # Splash Screen & routing logic
│   ├── auth/                        # Mobile Number Registration & 6-Digit OTP Verification
│   ├── registration/                # Multi-section Business Profile Form & Category Picker
│   ├── home/                        # Business Dashboard & Popular Categories
│   ├── leads/                       # Leads management (New, Accepted, Rejected, Completed)
│   ├── profile/                     # Profile & Edit Profile with Photo Upload
│   └── settings/                    # Settings, Notifications, Help & Support, Legal
└── utils/
    ├── AppConfig.kt
    ├── ValidationUtils.kt           # Indian phone number and required field validations
    └── DateUtils.kt
```

---

## 2. Implemented Screens

1. **Splash Screen**: Authenticates session; automatically routes to Home if registered, or remaining onboarding steps if profile incomplete.
2. **Mobile Registration Screen**: Indian mobile number (+91) validation, country code selector, terms agreements.
3. **Six-Digit OTP Verification Screen**: 6 individual input boxes, auto-focus next, paste support, 30s resend timer, change number option.
4. **Business Registration Form**:
   - *Personal Info*: Full Name, Secondary Number
   - *Location Info*: City, Full Workshop Address
   - *Business Info*: Primary Service Category picker + "Other" custom category field
5. **Category Search & Selection Bottom Sheet**: Fast search for 17+ categories with custom category fallback.
6. **Registration Success Screen**: Welcome screen with summary.
7. **Home Screen**: Business dashboard, search bar, active partner status, quick actions, popular categories grid, recent enquiries.
8. **Leads Screen**: Filter tabs (All, New, Accepted, Rejected, Completed), customer enquiry cards, empty state view when no leads exist.
9. **Lead Detail Bottom Sheet**: Customer call action, service requirements, accept/reject/complete workflow.
10. **My Profile Screen**: Profile photo with change photo button, partner details, account creation date.
11. **Edit Profile Screen**: Update personal info, address, category, and upload profile photo.
12. **Settings Screen**: Profile preview, account navigation, logout confirmation dialog, delete account confirmation dialog.
13. **Notification Settings Screen**: Switch controls for enquiry alerts, SMS alerts, and business tips.
14. **Help & Support Screen**: Helpline dialer, email support, FAQs.
15. **About Screen**: App version, mission, architectural details.
16. **Privacy Policy & Terms Screens**: Complete legal policies.

---

## 3. Supabase Database & Security (RLS)

All database migrations, tables, indexes, and Row-Level Security policies are located in:
`supabase/migrations/20261031_initial_schema.sql`

### Tables Created:
- `public.profiles`: Stores partner profiles linked to `auth.users(id)` via ON DELETE CASCADE.
- `public.service_categories`: Active service categories with default seeded entries.
- `public.leads`: Customer enquiries with provider foreign key, status constraints, and timestamps.
- `storage.buckets`: Public `profile-photos` bucket.

### RLS Policies:
- Providers can only `SELECT`, `INSERT`, `UPDATE`, and `DELETE` their own profile (`auth.uid() = id`).
- Providers can only access leads assigned to them (`auth.uid() = provider_id`).
- Service categories are readable by all users (`is_active = true`).
- Profile photo storage allows upload only within `profile-photos/{user_id}/*`.

---

## 4. Setup & Configuration

1. Copy `local.properties.example` to `local.properties`:
   ```properties
   sdk.dir=C\:\\Users\\asus\\AppData\\Local\\Android\\Sdk
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your-anon-key
   ```
2. In Supabase Dashboard:
   - Run the SQL file in `supabase/migrations/20261031_initial_schema.sql`.
   - Enable Phone Authentication under **Authentication > Providers > Phone**.
   - Configure an SMS provider (Twilio, MessageBird, etc.) or set Test Phone Numbers for local testing.

---

## 5. Building & Testing

### Run Unit Tests
```powershell
.\gradlew.bat testDebugUnitTest
```

### Build Debug APK
```powershell
.\gradlew.bat assembleDebug
```
The compiled debug APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK / App Bundle
```powershell
.\gradlew.bat assembleRelease
# or for Google Play:
.\gradlew.bat bundleRelease
```
