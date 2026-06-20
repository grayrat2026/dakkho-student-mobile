import React from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { Eye, EyeOff, Mail, Lock, LogIn, Check, AlertCircle, KeyRound } from 'lucide-react-native';
import { router } from 'expo-router';
import { useAuthStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const isValidEmail = (email: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

const getPasswordStrength = (password: string): { label: string; color: string } => {
  if (!password) return { label: '', color: '' };
  let score = 0;
  if (password.length >= 6) score++;
  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;
  if (score <= 1) return { label: 'Weak', color: '#ef4444' };
  if (score <= 2) return { label: 'Fair', color: '#f59e0b' };
  if (score <= 3) return { label: 'Good', color: '#eab308' };
  if (score <= 4) return { label: 'Strong', color: '#10b981' };
  return { label: 'Very Strong', color: '#059669' };
};

export default function LoginScreen() {
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [showPassword, setShowPassword] = React.useState(false);
  const [rememberMe, setRememberMe] = React.useState(false);
  const [error, setError] = React.useState('');
  const { login, isLoading } = useAuthStore();
  const colors = LightColors;

  const handleSubmit = async () => {
    setError('');
    if (!isValidEmail(email)) { setError('Please enter a valid email address'); return; }
    if (password.length < 6) { setError('Password must be at least 6 characters'); return; }
    try {
      await login(email, password);
      router.replace('/(tabs)');
    } catch {
      setError('Invalid email or password. Please try again.');
    }
  };

  const emailValid = email && isValidEmail(email);
  const passwordStrength = getPasswordStrength(password);

  return (
    <SafeAreaView style={styles.safeArea}>
      <LinearGradient colors={['#f0f9ff', '#ffffff', '#eff6ff']} style={styles.gradientBg} start={{ x: 0, y: 0 }} end={{ x: 1, y: 1 }}>
        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.container}>
          <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
            <MotiView from={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 600 }} style={styles.card}>
              {/* Logo */}
              <MotiView from={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: 200, type: 'spring', stiffness: 200 }} style={styles.logoContainer}>
                <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.logoIcon}>
                  <Text style={styles.logoText}>D</Text>
                </LinearGradient>
                <Text style={styles.appName}>DAKKHO</Text>
                <Text style={[styles.subtitle, { color: colors.mutedForeground }]}>Sign in to continue learning</Text>
              </MotiView>

              {error ? (
                <MotiView from={{ opacity: 0, translateY: -10 }} animate={{ opacity: 1, translateY: 0 }} style={styles.errorBox}>
                  <AlertCircle size={16} color="#ef4444" />
                  <Text style={styles.errorText}>{error}</Text>
                </MotiView>
              ) : null}

              {/* Email */}
              <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 300 }}>
                <Text style={[styles.label, { color: colors.foreground }]}>Email</Text>
                <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50`, borderColor: 'transparent' }]}>
                  <Mail size={16} color={colors.mutedForeground} />
                  <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="your@email.com" placeholderTextColor={colors.mutedForeground} value={email} onChangeText={(t) => { setEmail(t); setError(''); }} keyboardType="email-address" autoCapitalize="none" />
                  {emailValid && <Check size={16} color="#10b981" />}
                </View>
              </MotiView>

              {/* Password */}
              <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 400 }}>
                <Text style={[styles.label, { color: colors.foreground }]}>Password</Text>
                <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50`, borderColor: 'transparent' }]}>
                  <Lock size={16} color={colors.mutedForeground} />
                  <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="Enter your password" placeholderTextColor={colors.mutedForeground} value={password} onChangeText={(t) => { setPassword(t); setError(''); }} secureTextEntry={!showPassword} />
                  <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
                    {showPassword ? <EyeOff size={16} color={colors.mutedForeground} /> : <Eye size={16} color={colors.mutedForeground} />}
                  </TouchableOpacity>
                </View>
                {password ? <Text style={{ fontSize: 12, color: passwordStrength.color, marginTop: 4 }}>Password strength: {passwordStrength.label}</Text> : null}
              </MotiView>

              {/* Remember + Forgot */}
              <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 500 }} style={styles.row}>
                <TouchableOpacity style={styles.rememberRow} onPress={() => setRememberMe(!rememberMe)}>
                  <View style={[styles.checkbox, rememberMe && { backgroundColor: colors.primary, borderColor: colors.primary }]}>
                    {rememberMe && <Check size={12} color="#fff" strokeWidth={3} />}
                  </View>
                  <Text style={[styles.rememberText, { color: colors.mutedForeground }]}>Remember me</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => router.push('/(auth)/forgot-password')}>
                  <Text style={[styles.forgotText, { color: colors.primary }]}>Forgot Password?</Text>
                </TouchableOpacity>
              </MotiView>

              {/* Submit */}
              <MotiView from={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 600 }}>
                <TouchableOpacity style={styles.submitBtn} onPress={handleSubmit} disabled={isLoading} activeOpacity={0.8}>
                  <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.submitGradient}>
                    {isLoading ? <Text style={styles.submitText}>Signing in...</Text> : <><LogIn size={16} color="#fff" /><Text style={styles.submitText}>Sign In</Text></>}
                  </LinearGradient>
                </TouchableOpacity>
              </MotiView>

              {/* OTP Login */}
              <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 650 }}>
                <TouchableOpacity style={styles.otpBtn} activeOpacity={0.7}>
                  <KeyRound size={16} color={colors.mutedForeground} />
                  <Text style={[styles.otpText, { color: colors.mutedForeground }]}>Sign in with OTP</Text>
                </TouchableOpacity>
              </MotiView>

              {/* Divider */}
              <View style={styles.divider}>
                <View style={styles.dividerLine} />
                <Text style={[styles.dividerText, { color: colors.mutedForeground }]}>or continue with</Text>
                <View style={styles.dividerLine} />
              </View>

              {/* Social Login */}
              <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 750 }} style={styles.socialRow}>
                <TouchableOpacity style={[styles.socialBtn, { backgroundColor: `${colors.muted}50` }]}>
                  <Text style={styles.googleText}>G</Text>
                  <Text style={[styles.socialLabel, { color: colors.foreground }]}>Google</Text>
                </TouchableOpacity>
                <TouchableOpacity style={[styles.socialBtn, { backgroundColor: `${colors.muted}50` }]}>
                  <Text style={styles.githubIcon}>⚙</Text>
                  <Text style={[styles.socialLabel, { color: colors.foreground }]}>GitHub</Text>
                </TouchableOpacity>
              </MotiView>

              {/* Sign Up */}
              <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 800 }} style={styles.signupRow}>
                <Text style={{ color: colors.mutedForeground, fontSize: 14 }}>Don't have an account? </Text>
                <TouchableOpacity onPress={() => router.push('/(auth)/signup')}>
                  <Text style={{ color: colors.primary, fontSize: 14, fontWeight: '700' }}>Sign Up</Text>
                </TouchableOpacity>
              </MotiView>
            </MotiView>
          </ScrollView>
        </KeyboardAvoidingView>
      </LinearGradient>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  gradientBg: { flex: 1 },
  container: { flex: 1 },
  scrollContent: { flexGrow: 1, justifyContent: 'center', padding: 16 },
  card: { backgroundColor: 'rgba(255,255,255,0.7)', borderRadius: 24, padding: 24, shadowColor: '#0ea5e9', shadowOffset: { width: 0, height: 10 }, shadowOpacity: 0.1, shadowRadius: 15, elevation: 5, borderWidth: 1, borderColor: 'rgba(255,255,255,0.5)' },
  logoContainer: { alignItems: 'center', marginBottom: 24 },
  logoIcon: { width: 64, height: 64, borderRadius: 16, alignItems: 'center', justifyContent: 'center', marginBottom: 8 },
  logoText: { color: '#fff', fontSize: 28, fontWeight: '800' },
  appName: { fontSize: 24, fontWeight: '800', color: '#0ea5e9' },
  subtitle: { fontSize: 14, marginTop: 4 },
  errorBox: { flexDirection: 'row', alignItems: 'center', gap: 8, padding: 12, borderRadius: 12, backgroundColor: '#fef2f2', marginBottom: 16 },
  errorText: { fontSize: 12, fontWeight: '500', color: '#dc2626', flex: 1 },
  label: { fontSize: 12, fontWeight: '600', marginBottom: 6 },
  inputRow: { flexDirection: 'row', alignItems: 'center', gap: 8, paddingHorizontal: 12, height: 48, borderRadius: 12, borderWidth: 2, marginBottom: 16 },
  input: { flex: 1, fontSize: 14, paddingVertical: 0 },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  rememberRow: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  checkbox: { width: 16, height: 16, borderRadius: 4, borderWidth: 2, borderColor: '#94a3b8', alignItems: 'center', justifyContent: 'center' },
  rememberText: { fontSize: 12 },
  forgotText: { fontSize: 12, fontWeight: '600' },
  submitBtn: { borderRadius: 12, overflow: 'hidden', marginBottom: 12 },
  submitGradient: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, paddingVertical: 16, paddingHorizontal: 24 },
  submitText: { color: '#fff', fontSize: 16, fontWeight: '700' },
  otpBtn: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, paddingVertical: 12, borderRadius: 12, borderWidth: 2, borderStyle: 'dashed', borderColor: 'rgba(148,163,184,0.3)', marginBottom: 16 },
  otpText: { fontSize: 14 },
  divider: { flexDirection: 'row', alignItems: 'center', marginVertical: 16, gap: 8 },
  dividerLine: { flex: 1, height: 1, backgroundColor: 'rgba(148,163,184,0.2)' },
  dividerText: { fontSize: 12, fontWeight: '500' },
  socialRow: { flexDirection: 'row', gap: 12 },
  socialBtn: { flex: 1, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, paddingVertical: 12, borderRadius: 12 },
  googleText: { fontSize: 18, fontWeight: '800', color: '#4285F4' },
  githubIcon: { fontSize: 16, fontWeight: '800' },
  socialLabel: { fontSize: 14, fontWeight: '600' },
  signupRow: { flexDirection: 'row', justifyContent: 'center', marginTop: 20 },
});
