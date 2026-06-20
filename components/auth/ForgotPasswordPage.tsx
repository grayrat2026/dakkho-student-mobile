import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { Mail, ArrowLeft, KeyRound } from 'lucide-react-native';
import { useNavigationStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

export function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const navigate = useNavigationStore((s) => s.navigate);
  const colors = LightColors;

  const handleSend = () => {
    if (!email.includes('@')) return;
    setSent(true);
  };

  return (
    <SafeAreaView style={styles.safeArea}>
      <LinearGradient colors={['#f0f9ff', '#ffffff', '#eff6ff']} style={styles.gradientBg}>
        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.container}>
          <ScrollView contentContainerStyle={styles.scrollContent}>
            <MotiView from={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} style={styles.card}>
              <TouchableOpacity style={styles.backBtn} onPress={() => navigate('login')}>
                <ArrowLeft size={20} color={colors.mutedForeground} />
              </TouchableOpacity>

              <View style={styles.iconContainer}>
                <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.iconCircle}>
                  <KeyRound size={28} color="#fff" />
                </LinearGradient>
              </View>

              <Text style={styles.title}>Forgot Password?</Text>
              <Text style={[styles.subtitle, { color: colors.mutedForeground }]}>
                {sent ? "We've sent a reset link to your email" : "Enter your email and we'll send you a reset link"}
              </Text>

              {!sent ? (
                <>
                  <Text style={[styles.label, { color: colors.foreground }]}>Email</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <Mail size={16} color={colors.mutedForeground} />
                    <TextInput
                      style={[styles.input, { color: colors.foreground }]}
                      placeholder="your@email.com"
                      placeholderTextColor={colors.mutedForeground}
                      value={email}
                      onChangeText={setEmail}
                      keyboardType="email-address"
                      autoCapitalize="none"
                    />
                  </View>
                  <TouchableOpacity style={styles.sendBtn} onPress={handleSend} activeOpacity={0.8}>
                    <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.sendGradient}>
                      <Text style={styles.sendText}>Send Reset Link</Text>
                    </LinearGradient>
                  </TouchableOpacity>
                </>
              ) : (
                <View style={styles.successBox}>
                  <Text style={styles.successText}>Check your inbox at {email}</Text>
                  <TouchableOpacity onPress={() => navigate('login')} style={styles.backToLogin}>
                    <Text style={{ color: colors.primary, fontWeight: '700', fontSize: 14 }}>Back to Sign In</Text>
                  </TouchableOpacity>
                </View>
              )}
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
  card: {
    backgroundColor: 'rgba(255,255,255,0.7)', borderRadius: 24, padding: 24,
    shadowColor: '#0ea5e9', shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.1, shadowRadius: 15, elevation: 5,
    borderWidth: 1, borderColor: 'rgba(255,255,255,0.5)',
  },
  backBtn: { marginBottom: 16 },
  iconContainer: { alignItems: 'center', marginBottom: 16 },
  iconCircle: { width: 64, height: 64, borderRadius: 32, alignItems: 'center', justifyContent: 'center' },
  title: { fontSize: 22, fontWeight: '800', color: '#0f172a', textAlign: 'center', marginBottom: 8 },
  subtitle: { fontSize: 14, textAlign: 'center', marginBottom: 24, lineHeight: 20 },
  label: { fontSize: 12, fontWeight: '600', marginBottom: 6 },
  inputRow: {
    flexDirection: 'row', alignItems: 'center', gap: 8,
    paddingHorizontal: 12, height: 48, borderRadius: 12, borderWidth: 2,
    borderColor: 'transparent', marginBottom: 20,
  },
  input: { flex: 1, fontSize: 14, paddingVertical: 0 },
  sendBtn: { borderRadius: 12, overflow: 'hidden' },
  sendGradient: { paddingVertical: 16, alignItems: 'center', justifyContent: 'center' },
  sendText: { color: '#fff', fontSize: 16, fontWeight: '700' },
  successBox: { alignItems: 'center', gap: 16 },
  successText: { fontSize: 14, color: '#10b981', fontWeight: '600', textAlign: 'center' },
  backToLogin: { paddingVertical: 12 },
});
