# DAKKHO Student App — Native Android Rewrite

> Bangladesh's premier polytechnic student streaming platform, rebuilt natively with Kotlin + Jetpack Compose.

[![Android CI/CD](https://github.com/grayrat2026/dakkho-student-mobile/actions/workflows/android-ci.yml/badge.svg?branch=native-android-rewrite)](https://github.com/grayrat2026/dakkho-student-mobile/actions/workflows/android-ci.yml)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.12-9C27B0.svg)](https://developer.android.com/jetpack/compose)

---

## Overview

DAKKHO serves students across **20 polytechnic technologies** in Bangladesh, offering video courses, live classes, quizzes, certificates, and social features. This is a **complete native Android rewrite** of the existing web application at [dakkho-student.pages.dev](https://dakkho-student.pages.dev), matching the website's UI/UX while adding significant security enhancements, offline capabilities, and native device integrations.

### Key Highlights

- **111 screens** organized across **29 implementation phases**
- **Pixel-perfect UI** matching the website — glassmorphism cards, gradient buttons, exact color system
- **Multi-format video playback** with DRM protection (ExoPlayer + WebView)
- **Comprehensive security** — root detection, certificate pinning, screenshot blocking, encrypted storage
- **Offline-first architecture** with Room database caching
- **Seamless Cloudflare backend sync** — D1, KV, Workers, R2
- **Dual push notifications** — FCM + OneSignal
- **PipraPay payment integration** via Chrome Custom Tabs

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin 2.1.0 |
| UI Framework | Jetpack Compose + Material3 |
| Architecture | MVVM + Clean Architecture + Repository Pattern |
| DI | Hilt (Dagger) |
| Network | Retrofit 2 + OkHttp 4 + Certificate Pinning |
| Database | Room (SQLite) + EncryptedSharedPreferences |
| Serialization | Moshi + Kotlin Serialization |
| Video Player | Media3 ExoPlayer (MP4/MKV/HLS/DASH) + WebView (YouTube) |
| Image Loading | Coil |
| Navigation | Jetpack Navigation Compose (type-safe) |
| Push Notifications | FCM + OneSignal (dual channel) |
| Payment | PipraPay via Chrome Custom Tabs |
| Offline | Room + DataStore + WorkManager |
| Analytics | Firebase Analytics |
| Security | FLAG_SECURE, Root Detection, R8, Anti-Tampering |
| CI/CD | GitHub Actions |

---

## Project Structure

```
app/src/main/java/com/dakkho/android/
├── DakkhoApplication.kt          # @HiltAndroidApp entry point
├── MainActivity.kt                # Splash + Navigation host
│
├── data/
│   ├── api/                       # Retrofit API services
│   │   ├── AuthApiService.kt
│   │   ├── CourseApiService.kt
│   │   ├── EnrollmentApiService.kt
│   │   ├── InstructorApiService.kt
│   │   ├── NotificationApiService.kt
│   │   ├── AuthInterceptor.kt     # Bearer token injection
│   │   └── ApiResponse.kt         # Generic response wrapper
│   ├── db/
│   │   ├── DakkhoDatabase.kt      # Room database
│   │   ├── Converters.kt          # Type converters
│   │   ├── EncryptedPrefsHelper.kt # Secure token storage
│   │   ├── dao/                   # 9 DAOs
│   │   └── entity/               # 9 Room entities
│   ├── notification/             # Android notification channels
│   │   └── NotificationChannelManager.kt
│   ├── work/                     # WorkManager workers
│   │   └── NotificationSyncWorker.kt
│   └── repository/               # Repository implementations
│       ├── AuthRepositoryImpl.kt
│       ├── CourseRepositoryImpl.kt
│       ├── EnrollmentRepositoryImpl.kt
│       └── NotificationRepositoryImpl.kt
│
├── paging/                       # Paging 3 components
│   ├── CoursePagingSource.kt     # API-based PagingSource
│   ├── CourseRemoteMediator.kt   # Room + API sync mediator
│   └── NotificationPagingSource.kt # Notification API PagingSource
│
├── domain/
│   ├── model/                     # Domain models + API DTOs
│   └── repository/               # Repository interfaces
│
├── presentation/
│   ├── components/               # 14 shared UI components
│   │   ├── TopBar.kt
│   │   ├── BottomNav.kt
│   │   ├── Sidebar.kt
│   │   ├── GlassCard.kt
│   │   ├── GradientButton.kt
│   │   ├── DakkhoProgressBar.kt
│   │   ├── ShimmerEffect.kt
│   │   ├── EmptyState.kt
│   │   ├── AnimatedPage.kt
│   │   ├── AnimatedCounter.kt
│   │   ├── CourseCardGrid.kt
│   │   ├── SensitiveActionPrompt.kt
│   │   ├── ContentProtection.kt
│   │   ├── CustomContextMenu.kt
│   │   ├── home/              # Home screen components
│   │   │   ├── HeroSection.kt
│   │   │   ├── CategoryPills.kt
│   │   │   ├── ContinueWatching.kt
│   │   │   ├── TrendingCourses.kt
│   │   │   ├── FeaturedInstructors.kt
│   │   │   └── SectionHeader.kt
│   │   ├── explore/           # Explore screen components
│   │   │   ├── ExploreCourseCard.kt
│   │   │   ├── FilterChipsRow.kt
│   │   │   ├── SortDropdown.kt
│   │   │   └── ExploreSearchBar.kt
│   │   ├── search/           # Search screen components
│   │   │   ├── DakkhoSearchBar.kt
│   │   │   ├── RecentSearchesRow.kt
│   │   │   ├── SuggestionItem.kt
│   │   │   └── SearchResultItem.kt
│   │   └── notifications/     # Notification screen components
│   │       ├── NotificationItemCard.kt
│   │       └── NotificationEmptyState.kt
│   ├── navigation/
│   │   ├── Route.kt              # @Serializable routes
│   │   ├── DakkhoNavHost.kt      # NavHost + transitions
│   │   └── DakkhoNavigation.kt   # Scaffold + drawer + bottom bar
│   ├── screens/
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt     # Gradient + GlassCard login
│   │   │   ├── LoginViewModel.kt
│   │   │   ├── SignupScreen.kt    # 4-step wizard
│   │   │   ├── SignupViewModel.kt
│   │   │   ├── ForgotPasswordScreen.kt
│   │   │   └── ForgotPasswordViewModel.kt
│   │   ├── home/
│   │   │   ├── HomeScreen.kt      # PullToRefresh + LazyColumn
│   │   │   └── HomeViewModel.kt
│   │   ├── explore/
│   │   │   ├── ExploreScreen.kt   # LazyVerticalGrid + Paging 3
│   │   │   └── ExploreViewModel.kt
│   │   ├── search/
│   │   │   ├── SearchScreen.kt    # FTS + debounced search + history
│   │   │   └── SearchViewModel.kt
│   │   └── notifications/
│   │       ├── NotificationsScreen.kt  # SwipeToDismiss + PullToRefresh
│   │       ├── NotificationsViewModel.kt
│   │       └── NotificationDetailScreen.kt
│   └── theme/                    # Design system
│       ├── Color.kt              # DAKKHO palette (SkyBlue/DeepBlue/Green)
│       ├── Theme.kt              # Light/Dark + Material You
│       ├── Type.kt               # Typography scale
│       ├── Glassmorphism.kt      # Glass card effects
│       ├── Gradients.kt          # 7 gradient brushes
│       ├── Spacing.kt            # Spacing constants
│       ├── Shapes.kt             # Shape definitions
│       ├── Animation.kt          # Animation constants
│       └── DesignToken.kt        # Centralized design tokens
│
└── di/                           # Hilt modules
    ├── NetworkModule.kt          # Retrofit + OkHttp
    ├── DatabaseModule.kt         # Room + DAOs
    └── RepositoryModule.kt       # Repository bindings
```

---

## Implementation Progress

### ✅ Completed (Phase 1–10)

| Phase | Title | Status | Files |
|-------|-------|--------|-------|
| 1 | Project Foundation & Infrastructure | ✅ Complete | 45 files |
| 2 | Theme System, Typography & Design Tokens | ✅ Complete | 9 files |
| 3 | Shared Components (14) & Navigation Scaffold | ✅ Complete | 17 files |
| 4 | Login Page #1 | ✅ Complete | 2 files |
| 5 | Signup Page #2 (4-Step Wizard) | ✅ Complete | 2 files |
| 6 | Forgot Password Page #3 | ✅ Complete | 2 files |
| 7 | Home Screen #4 (6 Components) | ✅ Complete | 8 files |
| 8 | Explore Screen #5 (Paging 3) | ✅ Complete | 11 files |
| 9 | Search Screen #6 (FTS + History) | ✅ Complete | 8 files |
| 10 | Notifications #7-8 (Paging + Swipe + Worker) | ✅ Complete | 12 files |

### 🚧 Upcoming (Phase 11–29)

| Phase | Title | Est. Duration |
|-------|-------|---------------|
| 11 | My Learning #8 & Stats #9 | 3–4 days |
| 12 | Watch History #12 & Assignment #13 | 2–3 days |
| 13 | Course Detail #14 | 4–5 days |
| 14 | Secure Video Player #15 | 6–8 days |
| 15 | Course Curriculum #16 & Reviews #17 | 3–4 days |
| 16 | Course Q&A #18, Announcements #19, Resources #20 | 3 days |
| 17 | Course Notes #21, Quizzes #22, Progress #23 | 3–4 days |
| 18 | Instructor List #24 & Profile #25 | 3 days |
| 19 | Instructor Sub-pages #26–29 | 2–3 days |
| 20 | My Courses #30 & Bookmarks #31 | 2–3 days |
| 21 | Downloads #32 & Certificates #33 | 3–4 days |
| 22 | Live Sessions #34, Achievements #35, Discussion #36 | 3–4 days |
| 23 | Department Pages Template #37–56 | 3–4 days |
| 24 | Semester Pages Template #57–64 | 2–3 days |
| 25 | Profile Sub-pages #65–71 | 4–5 days |
| 26 | Settings Part 1 #72–76 | 3 days |
| 27 | Settings Part 2 #77–82 | 3–4 days |
| 28 | Help & Support #83–90 + Exam #91–95 | 5–6 days |
| 29 | Social #96–101 + Payment #102–104 + Misc + Security | 6–8 days |

---

## Design System

### Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| Sky Blue | `#0EA5E9` | Primary actions, links, highlights |
| Deep Blue | `#1E3A8A` | Secondary, gradients, headings |
| Green | `#22C55E` | Success, enrollment, active states |
| Light Background | `#F8FAFC` | Light mode background |
| Dark Background | `#0F172A` | Dark mode background |
| Error Red | `#EF4444` | Error states, destructive actions |

### Glassmorphism Cards

- Semi-transparent background: `rgba(255,255,255,0.7)` light / `rgba(15,23,42,0.7)` dark
- Backdrop blur: 24dp
- Subtle 1px border
- 16dp rounded corners

### Typography Scale

| Style | Size | Usage |
|-------|------|-------|
| Display Large | 57sp | Hero sections |
| Headline Large | 32sp | Page titles |
| Title Large | 22sp | Section headers |
| Body Large | 16sp | Primary body text |
| Body Medium | 14sp | Secondary text |
| Label Small | 11sp | Captions, badges |

---

## Backend Architecture

The app connects to a **Cloudflare Workers** backend (Hono framework) with the following infrastructure:

- **D1** — Relational database (users, courses, enrollments, etc.)
- **KV** — Key-value cache (config, feature flags, cached responses)
- **Workers** — Serverless API (auth, CRUD, file management, business logic)
- **R2** — Object storage (videos, thumbnails, resources)

### API Base URL
- Production: `https://dakkho-api.pages.dev`

### Key API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new student (4-step) |
| POST | `/api/auth/login` | Login with email + password |
| POST | `/api/auth/verify-otp` | Verify OTP for signup/reset |
| GET | `/api/auth/me` | Get current user profile |
| GET | `/api/courses` | List courses (paginated, filterable) |
| GET | `/api/courses/:id` | Get course detail |
| GET | `/api/courses/:id/curriculum` | Get course curriculum tree |
| GET | `/api/notifications` | List notifications (paginated) |
| PATCH | `/api/notifications/:id/read` | Mark notification as read |
| PATCH | `/api/notifications/read-all` | Mark all notifications as read |
| GET | `/api/enrollments/check` | Check enrollment status |
| GET | `/api/instructors` | List instructors |
| GET | `/api/institutes` | List polytechnic institutes |
| GET | `/api/technologies` | List technologies/departments |
| POST | `/api/payments/create` | Create PipraPay payment |
| POST | `/api/video/stream/session/:id` | Get DRM streaming session |

---

## Security Features

- **FLAG_SECURE** on video and payment screens (screenshot prevention)
- **Certificate Pinning** for Cloudflare Worker hostname
- **Root Detection** via Play Integrity API
- **R8 Obfuscation** with string encryption for API keys
- **Anti-Tampering** via APK signature verification
- **EncryptedSharedPreferences** for all sensitive data
- **Overlay Detection** to prevent clickjacking
- **Network Security Config** — no cleartext traffic in release
- **Content Protection** — disable copy/paste on protected content

---

## Deep Links

| Scheme | Route | Description |
|--------|-------|-------------|
| `dakkho://payment/status` | PaymentStatus | PipraPay redirect |
| `dakkho://verify/otp` | OtpVerification | OTP callback |
| `dakkho://course/:id` | CourseDetail | Push notification action |
| `dakkho://notification/:id` | Notifications | Push notification tap |
| `dakkho://certificate/:id` | Certificate | Certificate verification |
| `dakkho://referral?code=X` | Home | Referral link |

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35
- Google Play Services (for FCM)

### Build & Run

```bash
# Clone the repository
git clone https://github.com/grayrat2026/dakkho-student-mobile.git
cd dakkho-student-mobile
git checkout native-android-rewrite

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew testDebugUnitTest
```

### Configuration

1. Add your `google-services.json` to the `app/` directory (Firebase)
2. Update certificate pin hashes in `NetworkModule.kt` with production values
3. Set up signing keystore for release builds

---

## Related Repositories

- [DAKKHO Admin Panel](https://github.com/grayrat2026/dakkho-admin-web) — Admin dashboard
- [DAKKHO Worker API](https://github.com/grayrat2026/dakkho-worker) — Cloudflare Workers backend
- [DAKKHO Student Web](https://dakkho-student.pages.dev) — Web application (current)

---

## License

This project is proprietary. All rights reserved.

---

<div align="center">
  <strong>DAKKHO</strong> — Learn Without Limits 🎓
</div>
