import React, { useEffect } from 'react';
import { View, ActivityIndicator, StyleSheet } from 'react-native';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { ErrorBoundary } from 'react-native';
import { useAuthStore, useThemeStore, useNotificationStore, useServerConfigStore } from '@/lib/store';
import { LightColors, DarkColors } from '@/lib/theme';

// Global error handler for TurboModule issues
if (__DEV__) {
  const originalConsoleError = console.error;
  console.error = (...args) => {
    const message = args[0]?.toString?.() || '';
    // Suppress known TurboModule warnings that are harmless in Expo managed workflow
    if (message.includes('TurboModuleRegistry') && message.includes('PlatformConstants')) {
      return;
    }
    originalConsoleError.call(console, ...args);
  };
}

export function ErrorFallback({ error, resetErrorBoundary }: { error: Error; resetErrorBoundary: () => void }) {
  return (
    <View style={styles.errorContainer}>
      <ActivityIndicator size="large" color="#0ea5e9" />
    </View>
  );
}

export default function RootLayout() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const needsVerification = useAuthStore((s) => s.needsVerification);
  const isHydrated = useAuthStore((s) => s.isHydrated);
  const hydrateAuth = useAuthStore((s) => s.hydrateAuth);
  const themeMode = useThemeStore((s) => s.themeMode);
  const hydrateFromStorage = useNotificationStore((s) => s.hydrateFromStorage);
  const fetchConfig = useServerConfigStore((s) => s.fetchConfig);

  // Hydrate all stores on mount
  useEffect(() => {
    hydrateAuth();
    hydrateFromStorage();
    fetchConfig();
  }, []);

  // Loading screen while hydrating
  if (!isHydrated) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#0ea5e9" />
      </View>
    );
  }

  const isDark = themeMode === 'dark';
  const bgColor = isDark ? '#0c1222' : '#f0f9ff';

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <StatusBar style={isDark ? 'light' : 'dark'} />
        <Stack
          screenOptions={{
            headerShown: false,
            animation: 'slide_from_right',
            contentStyle: { backgroundColor: bgColor },
          }}
        >
          {/* Auth screens */}
          <Stack.Screen name="(auth)" redirect={isAuthenticated && !needsVerification} />
          {/* Main app screens */}
          <Stack.Screen name="(tabs)" redirect={!isAuthenticated || needsVerification} />
        </Stack>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1,
    backgroundColor: '#0c1222',
    alignItems: 'center',
    justifyContent: 'center',
  },
  errorContainer: {
    flex: 1,
    backgroundColor: '#0c1222',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
