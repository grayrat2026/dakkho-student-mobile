import React, { useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { View, ActivityIndicator, StyleSheet } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { useAuthStore, useThemeStore, useNavigationStore, useNotificationStore, useServerConfigStore } from '@/lib/store';
import { LightNavTheme, DarkNavTheme } from '@/lib/theme';

// Auth screens
import { LoginPage } from '@/components/auth/LoginPage';
import { SignupPage } from '@/components/auth/SignupPage';
import { ForgotPasswordPage } from '@/components/auth/ForgotPasswordPage';

// Main app shell
import { AppShell } from '@/components/shared/AppShell';

const Stack = createNativeStackNavigator();

function AuthStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false, animation: 'slide_from_right' }}>
      <Stack.Screen name="login" component={LoginPage} />
      <Stack.Screen name="signup" component={SignupPage} />
      <Stack.Screen name="forgot-password" component={ForgotPasswordPage} />
    </Stack.Navigator>
  );
}

function MainStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false, animation: 'slide_from_right' }}>
      <Stack.Screen name="MainApp" component={AppShell} />
    </Stack.Navigator>
  );
}

export default function App() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const needsVerification = useAuthStore((s) => s.needsVerification);
  const isHydrated = useAuthStore((s) => s.isHydrated);
  const hydrateAuth = useAuthStore((s) => s.hydrateAuth);
  const refreshUser = useAuthStore((s) => s.refreshUser);
  const themeMode = useThemeStore((s) => s.themeMode);
  const loadFromPreferences = useThemeStore((s) => s.loadFromPreferences);
  const hydrateFromStorage = useNotificationStore((s) => s.hydrateFromStorage);
  const fetchConfig = useServerConfigStore((s) => s.fetchConfig);

  // Hydrate all stores on mount
  useEffect(() => {
    hydrateAuth();
    loadFromPreferences('system');
    hydrateFromStorage();
    fetchConfig();
    refreshUser();
  }, []);

  // Loading screen while hydrating
  if (!isHydrated) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#0ea5e9" />
      </View>
    );
  }

  const navTheme = themeMode === 'dark' ? DarkNavTheme : LightNavTheme;

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <NavigationContainer theme={navTheme}>
          <StatusBar style="auto" />
          {!isAuthenticated || needsVerification ? <AuthStack /> : <MainStack />}
        </NavigationContainer>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  loadingContainer: {
    flex: 1, backgroundColor: '#f0f9ff',
    alignItems: 'center', justifyContent: 'center',
  },
});
