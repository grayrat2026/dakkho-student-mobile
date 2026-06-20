#!/bin/bash
# ═══════════════════════════════════════════════════════════════════
# Quick EAS Build — Run from ANY machine with Node.js + EAS CLI
# Usage: bash quick-build.sh
# ═══════════════════════════════════════════════════════════════════
set -e

echo "🔍 Checking EAS CLI..."
npx eas-cli --version

echo "🔐 Logging in to Expo..."
npx eas-cli login

echo "📦 Installing dependencies..."
npm install

echo "🩺 Running Expo Doctor..."
npx expo-doctor

echo "🏗️ Building APK with EAS (this takes 10-20 minutes)..."
npx eas-cli build --platform android --profile preview --non-interactive --wait

echo "✅ Build complete! Download the APK from the EAS dashboard."
echo "🌐 https://expo.dev/accounts/[your-account]/projects/dakkho-student-mobile/builds"
