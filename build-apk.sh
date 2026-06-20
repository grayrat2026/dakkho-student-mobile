#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════════
#  DAKKHO Student Mobile — One-Command VPS APK Build
#  Usage:  bash <(curl -sL https://raw.githubusercontent.com/grayrat2026/dakkho-student-mobile/main/build-apk.sh)
#  Or:     git pull && bash build-apk.sh
# ═══════════════════════════════════════════════════════════════════════════
set -e

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'

echo -e "${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}   📱 DAKKHO Student Mobile — APK Build${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════════════════${NC}"

# ────────────────────────────────────────────────────────────
# Step 1: Environment setup
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[1/8] Setting up environment...${NC}"

# Android SDK
if [ -z "$ANDROID_HOME" ] || [ ! -d "$ANDROID_HOME/platforms" ]; then
  echo -e "${YELLOW}Installing Android SDK...${NC}"
  export ANDROID_HOME="$HOME/android-sdk"
  export ANDROID_SDK_ROOT="$ANDROID_HOME"
  mkdir -p "$ANDROID_HOME"

  cd /tmp
  wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdlinetools.zip
  unzip -qo cmdlinetools.zip -d "$ANDROID_HOME/"
  mkdir -p "$ANDROID_HOME/cmdline-tools/latest"
  # Handle both old and new zip structures
  if [ -d "$ANDROID_HOME/cmdline-tools/cmdline-tools" ]; then
    mv "$ANDROID_HOME/cmdline-tools/cmdline-tools/"* "$ANDROID_HOME/cmdline-tools/latest/" 2>/dev/null || true
  fi
  rm -f cmdlinetools.zip

  yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses 2>/dev/null || true
  "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0" 2>/dev/null

  echo -e "${GREEN}✓ Android SDK installed${NC}"
else
  echo -e "${GREEN}✓ Android SDK found: $ANDROID_HOME${NC}"
fi

export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

# Java
if [ -z "$JAVA_HOME" ]; then
  JAVA_PATH=$(dirname $(dirname $(readlink -f $(which java))))
  export JAVA_HOME="$JAVA_PATH"
fi
echo -e "${GREEN}✓ Java: $(java -version 2>&1 | head -1)${NC}"

# Node.js 20+
NODE_VER=$(node -v 2>/dev/null | sed 's/v//' | cut -d. -f1)
if [ -z "$NODE_VER" ] || [ "$NODE_VER" -lt 20 ]; then
  echo -e "${YELLOW}Installing Node.js 20...${NC}"
  curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash - > /dev/null 2>&1
  sudo apt-get install -y nodejs > /dev/null 2>&1
fi
echo -e "${GREEN}✓ Node.js: $(node -v)${NC}"

# ────────────────────────────────────────────────────────────
# Step 2: Clone / Update project
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[2/8] Getting latest code...${NC}"
PROJECT_DIR="$HOME/dakkho-student-mobile"
if [ -d "$PROJECT_DIR" ]; then
  cd "$PROJECT_DIR" && git fetch origin && git reset --hard origin/main
else
  git clone https://github.com/grayrat2026/dakkho-student-mobile.git "$PROJECT_DIR"
  cd "$PROJECT_DIR"
fi
echo -e "${GREEN}✓ Code updated${NC}"

# ────────────────────────────────────────────────────────────
# Step 3: Install npm dependencies
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[3/8] Installing npm dependencies...${NC}"
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps 2>&1 | tail -3
echo -e "${GREEN}✓ Dependencies installed${NC}"

# ────────────────────────────────────────────────────────────
# Step 4: Expo Doctor check
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[4/8] Running Expo Doctor...${NC}"
npx expo-doctor 2>&1 | tail -5
echo -e "${GREEN}✓ Health check done${NC}"

# ────────────────────────────────────────────────────────────
# Step 5: Expo Prebuild
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[5/8] Running Expo Prebuild (generating Android project)...${NC}"
npx expo prebuild --platform android --clean 2>&1 | tail -5
echo -e "${GREEN}✓ Prebuild complete${NC}"

# ────────────────────────────────────────────────────────────
# Step 6: Build Release APK
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[6/8] Building Release APK (5-15 min)...${NC}"
cd "$PROJECT_DIR/android"
chmod +x gradlew
./gradlew assembleRelease 2>&1 | tail -10

# ────────────────────────────────────────────────────────────
# Step 7: Verify & copy APK
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[7/8] Verifying APK...${NC}"
APK_PATH="$PROJECT_DIR/android/app/build/outputs/apk/release/app-release.apk"
if [ ! -f "$APK_PATH" ]; then
  APK_PATH="$PROJECT_DIR/android/app/build/outputs/apk/debug/app-debug.apk"
  if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ APK not found! Build may have failed.${NC}"
    echo -e "${YELLOW}Trying debug build...${NC}"
    cd "$PROJECT_DIR/android" && ./gradlew assembleDebug 2>&1 | tail -5
    APK_PATH="$PROJECT_DIR/android/app/build/outputs/apk/debug/app-debug.apk"
  fi
fi

OUTPUT_DIR="$HOME/dakkho-builds"
mkdir -p "$OUTPUT_DIR"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
OUTPUT_APK="$OUTPUT_DIR/dakkho-student-$TIMESTAMP.apk"
cp "$APK_PATH" "$OUTPUT_APK"
APK_SIZE=$(du -h "$OUTPUT_APK" | cut -f1)

# ────────────────────────────────────────────────────────────
# Step 8: Done!
# ────────────────────────────────────────────────────────────
echo -e "\n${YELLOW}[8/8] Cleaning up...${NC}"
echo -e "${GREEN}✓ Done${NC}"

echo -e "\n${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}   ✅ APK BUILD SUCCESSFUL!${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}   📦 File:  $OUTPUT_APK${NC}"
echo -e "${CYAN}   📏 Size:  $APK_SIZE${NC}"
echo -e "${CYAN}   📱 Install: adb install $OUTPUT_APK${NC}"
echo -e "${CYAN}   🌐 Serve:  cd $OUTPUT_DIR && python3 -m http.server 8080${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
