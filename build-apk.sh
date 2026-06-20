#!/bin/bash
# ═══════════════════════════════════════════════════════════════════
# DAKKHO Student Mobile — One-Click APK Build Script
# Run on VPS: bash build-apk.sh
# ═══════════════════════════════════════════════════════════════════
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
echo -e "${CYAN}   DAKKHO Student Mobile — APK Build Script${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"

# ── Step 1: Install Android SDK if missing ──
echo -e "\n${YELLOW}[1/7] Checking Android SDK...${NC}"

if [ -z "$ANDROID_HOME" ] || [ ! -d "$ANDROID_HOME/platforms" ]; then
  echo -e "${YELLOW}Android SDK not found. Installing...${NC}"
  
  export ANDROID_HOME="$HOME/android-sdk"
  export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
  
  # Download command-line tools
  mkdir -p "$ANDROID_HOME/cmdline-tools"
  cd /tmp
  wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip
  unzip -q cmdline-tools.zip -d "$ANDROID_HOME/cmdline-tools"
  mv "$ANDROID_HOME/cmdline-tools/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest" 2>/dev/null || true
  rm cmdline-tools.zip
  
  # Accept licenses and install required packages
  yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null 2>&1 || true
  "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0" \
    "ndk;27.1.12297006" > /dev/null 2>&1
  
  echo -e "${GREEN}Android SDK installed at $ANDROID_HOME${NC}"
else
  echo -e "${GREEN}Android SDK found at $ANDROID_HOME${NC}"
fi

export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

# ── Step 2: Clone/Update project ──
echo -e "\n${YELLOW}[2/7] Updating project...${NC}"

PROJECT_DIR="$HOME/dakkho-student-mobile"
if [ -d "$PROJECT_DIR" ]; then
  cd "$PROJECT_DIR"
  git pull origin main
else
  git clone https://github.com/grayrat2026/dakkho-student-mobile.git "$PROJECT_DIR"
  cd "$PROJECT_DIR"
fi

# ── Step 3: Install Node.js 20 if missing ──
echo -e "\n${YELLOW}[3/7] Checking Node.js...${NC}"

NODE_VERSION=$(node -v 2>/dev/null || echo "none")
if [[ ! "$NODE_VERSION" =~ ^v2[0-9] ]]; then
  echo -e "${YELLOW}Installing Node.js 20...${NC}"
  curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash - > /dev/null 2>&1
  sudo apt-get install -y nodejs > /dev/null 2>&1
fi
echo -e "${GREEN}Node.js: $(node -v)${NC}"

# ── Step 4: Install dependencies ──
echo -e "\n${YELLOW}[4/7] Installing dependencies...${NC}"
cd "$PROJECT_DIR"
rm -rf node_modules
npm install

# ── Step 5: Expo Prebuild (generates android/ folder) ──
echo -e "\n${YELLOW}[5/7] Running Expo prebuild...${NC}"
cd "$PROJECT_DIR"
npx expo prebuild --platform android --clean

# ── Step 6: Build APK ──
echo -e "\n${YELLOW}[6/7] Building APK (this takes 5-15 minutes)...${NC}"
cd "$PROJECT_DIR/android"
chmod +x gradlew
./gradlew assembleRelease 2>&1 | tail -5

# ── Step 7: Copy APK to accessible location ──
echo -e "\n${YELLOW}[7/7] Copying APK...${NC}"
APK_PATH="$PROJECT_DIR/android/app/build/outputs/apk/release/app-release.apk"
OUTPUT_DIR="$HOME/dakkho-builds"
mkdir -p "$OUTPUT_DIR"

if [ -f "$APK_PATH" ]; then
  TIMESTAMP=$(date +%Y%m%d_%H%M%S)
  OUTPUT_APK="$OUTPUT_DIR/dakkho-student-$TIMESTAMP.apk"
  cp "$APK_PATH" "$OUTPUT_APK"
  
  APK_SIZE=$(du -h "$OUTPUT_APK" | cut -f1)
  
  echo -e "\n${GREEN}═══════════════════════════════════════════════════${NC}"
  echo -e "${GREEN}   ✅ APK BUILD SUCCESSFUL!${NC}"
  echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
  echo -e "${CYAN}   📦 APK: $OUTPUT_APK${NC}"
  echo -e "${CYAN}   📏 Size: $APK_SIZE${NC}"
  echo -e "${CYAN}   📱 Install: adb install $OUTPUT_APK${NC}"
  echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
else
  echo -e "\n${RED}❌ APK build failed! Check errors above.${NC}"
  echo -e "${YELLOW}Try debug build: cd android && ./gradlew assembleDebug${NC}"
  exit 1
fi
