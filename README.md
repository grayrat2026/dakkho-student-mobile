# DAKKHO Academy — Student Mobile App

**React Native + Expo** Android app for DAKKHO Academy — Bangladesh's premier polytechnic student streaming platform.

## 🎯 Same UI as Web

This mobile app replicates the exact same UI, design system, and features as the [DAKKHO Student Web App](https://dakkho-student.pages.dev), built with:

- **React Native** + **Expo** (SDK 52+)
- **TypeScript** — full type safety
- **NativeWind** — Tailwind CSS syntax for RN
- **Moti** — smooth animations (same feel as Framer Motion web)
- **Zustand** — state management (same stores as web)
- **React Navigation** — file-based routing
- **MMKV** — fast native key-value storage
- **Expo EAS** — CI/CD builds

## 📱 111 Pages (Same as Web)

| Category | Count |
|----------|-------|
| 🔐 Auth | 3 |
| 🏠 Main | 10 |
| 📚 Course | 10 |
| 👨‍🏫 Instructor | 6 |
| 🎓 User Activity | 7 |
| 🏛️ Department | 20 |
| 📅 Semester | 8 |
| 👤 Profile Sub | 7 |
| ⚙️ Settings Sub | 11 |
| ❓ Help & Support | 8 |
| 📝 Exam | 5 |
| 🌐 Social/Community | 6 |
| 💳 Payment | 3 |
| 📄 Misc/Legal | 5 |
| ⚠️ Error | 2 |
| **Total** | **111** |

## 🎨 Design System

- **Primary**: `#0ea5e9` (Sky Blue)
- **Background**: `#f0f9ff` (Light) / `#0c1222` (Dark)
- **Glass Cards**: Blur effect + transparency
- **Gradients**: Primary (sky→blue), Emerald, Amber, etc.
- **Font**: Nunito (same as web)
- **Dark Mode**: Full support

## 🚀 Getting Started

```bash
# Install dependencies
npm install

# Start development server
npx expo start

# Run on Android
npx expo run:android

# Build APK for testing
eas build --profile development --platform android

# Build AAB for Play Store
eas build --profile production --platform android
```

## 📦 Project Structure

```
dakkho-student-mobile/
├── App.tsx                    # Entry point
├── app.json                   # Expo config
├── eas.json                   # EAS Build config
├── lib/
│   ├── store.ts               # Zustand stores (111 pages)
│   ├── api-client.ts          # API client (same backend)
│   └── theme.ts               # Colors, gradients, constants
├── components/
│   ├── auth/                  # Login, Signup, ForgotPassword
│   ├── home/                  # HomePage, Hero, Live, Leaderboard
│   ├── explore/               # ExplorePage
│   ├── courses/               # MyCoursesPage
│   ├── course/                # CourseDetailPage
│   ├── profile/               # ProfilePage
│   ├── settings/              # SettingsPage
│   ├── search/                # SearchPage
│   ├── notifications/         # NotificationsPage
│   ├── video/                 # VideoPlayerPage (DRM)
│   ├── department/            # DepartmentPageTemplate (20 depts)
│   ├── semester/              # SemesterPageTemplate (8 sems)
│   └── shared/                # TopBar, BottomNav, Sidebar, GlassCard, etc.
├── .github/workflows/         # CI/CD
└── assets/                    # Fonts, images
```

## 🔄 CI/CD (GitHub Actions)

Push to `main` → triggers EAS Build → Android AAB → ready for Play Store.

## 📄 License

Proprietary — © 2026 DAKKHO Academy. Powered by GrayRAT.
