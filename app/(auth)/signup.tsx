import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { User, Mail, Phone, Lock, Eye, EyeOff, School, ChevronRight, Check, ArrowLeft } from 'lucide-react-native';
import { router } from 'expo-router';
import { useAuthStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const STEPS = ['Name', 'Email & Phone', 'Institute', 'Verify OTP'] as const;

export default function SignupScreen() {
  const [step, setStep] = useState(0);
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [instituteId, setInstituteId] = useState('');
  const [technologyId, setTechnologyId] = useState('');
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [error, setError] = useState('');
  const { signup, isLoading, needsVerification } = useAuthStore();
  const colors = LightColors;

  React.useEffect(() => {
    if (needsVerification && step !== 3) setStep(3);
  }, [needsVerification]);

  const handleNext = async () => {
    setError('');
    if (step === 0) {
      if (!fullName.trim()) { setError('Please enter your full name'); return; }
      setStep(1);
    } else if (step === 1) {
      if (!email.includes('@')) { setError('Please enter a valid email'); return; }
      if (phone.length < 10) { setError('Please enter a valid phone number'); return; }
      if (password.length < 6) { setError('Password must be at least 6 characters'); return; }
      setStep(2);
    } else if (step === 2) {
      try {
        await signup({ fullName, email, phone, password, instituteId, technologyId });
        setStep(3);
      } catch {
        setError('Signup failed. Please try again.');
      }
    }
  };

  const handleVerifyOTP = () => {
    const code = otp.join('');
    if (code.length === 6) {
      Alert.alert('Success', 'Account verified! You can now sign in.');
      router.replace('/(auth)/login');
    } else {
      setError('Please enter all 6 digits');
    }
  };

  const renderStepIndicator = () => (
    <View style={styles.stepRow}>
      {STEPS.map((s, i) => (
        <View key={s} style={styles.stepItem}>
          <View style={[styles.stepCircle, i <= step ? { backgroundColor: colors.primary } : { backgroundColor: colors.muted }]}>
            {i < step ? <Check size={14} color="#fff" /> : <Text style={styles.stepNum}>{i + 1}</Text>}
          </View>
          <Text style={[styles.stepLabel, { color: i <= step ? colors.primary : colors.mutedForeground }]}>{s}</Text>
          {i < STEPS.length - 1 && <View style={[styles.stepLine, { backgroundColor: i < step ? colors.primary : colors.muted }]} />}
        </View>
      ))}
    </View>
  );

  return (
    <SafeAreaView style={styles.safeArea}>
      <LinearGradient colors={['#f0f9ff', '#ffffff', '#eff6ff']} style={styles.gradientBg}>
        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.container}>
          <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
            <MotiView from={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} style={styles.card}>
              {step > 0 && (
                <TouchableOpacity style={styles.backBtn} onPress={() => setStep(step - 1)}>
                  <ArrowLeft size={20} color={colors.mutedForeground} />
                </TouchableOpacity>
              )}
              <View style={styles.logoContainer}>
                <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.logoIcon}>
                  <Text style={styles.logoText}>D</Text>
                </LinearGradient>
                <Text style={styles.title}>Create Account</Text>
                <Text style={[styles.subtitle, { color: colors.mutedForeground }]}>Step {step + 1}: {STEPS[step]}</Text>
              </View>
              {renderStepIndicator()}
              {error ? <Text style={styles.errorText}>{error}</Text> : null}
              {step === 0 && (
                <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}>
                  <Text style={[styles.label, { color: colors.foreground }]}>Full Name</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <User size={16} color={colors.mutedForeground} />
                    <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="Enter your full name" placeholderTextColor={colors.mutedForeground} value={fullName} onChangeText={setFullName} />
                  </View>
                </MotiView>
              )}
              {step === 1 && (
                <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}>
                  <Text style={[styles.label, { color: colors.foreground }]}>Email</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <Mail size={16} color={colors.mutedForeground} />
                    <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="your@email.com" placeholderTextColor={colors.mutedForeground} value={email} onChangeText={setEmail} keyboardType="email-address" autoCapitalize="none" />
                  </View>
                  <Text style={[styles.label, { color: colors.foreground }]}>Phone</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <Phone size={16} color={colors.mutedForeground} />
                    <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="+880 1XXX-XXXXXX" placeholderTextColor={colors.mutedForeground} value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
                  </View>
                  <Text style={[styles.label, { color: colors.foreground }]}>Password</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <Lock size={16} color={colors.mutedForeground} />
                    <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="Min 6 characters" placeholderTextColor={colors.mutedForeground} value={password} onChangeText={setPassword} secureTextEntry={!showPassword} />
                    <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
                      {showPassword ? <EyeOff size={16} color={colors.mutedForeground} /> : <Eye size={16} color={colors.mutedForeground} />}
                    </TouchableOpacity>
                  </View>
                </MotiView>
              )}
              {step === 2 && (
                <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}>
                  <Text style={[styles.label, { color: colors.foreground }]}>Institute</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <School size={16} color={colors.mutedForeground} />
                    <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="Select your institute" placeholderTextColor={colors.mutedForeground} value={instituteId} onChangeText={setInstituteId} />
                    <ChevronRight size={16} color={colors.mutedForeground} />
                  </View>
                  <Text style={[styles.label, { color: colors.foreground }]}>Technology/Department</Text>
                  <View style={[styles.inputRow, { backgroundColor: `${colors.muted}50` }]}>
                    <School size={16} color={colors.mutedForeground} />
                    <TextInput style={[styles.input, { color: colors.foreground }]} placeholder="Select your department" placeholderTextColor={colors.mutedForeground} value={technologyId} onChangeText={setTechnologyId} />
                    <ChevronRight size={16} color={colors.mutedForeground} />
                  </View>
                </MotiView>
              )}
              {step === 3 && (
                <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}>
                  <Text style={[styles.otpSubtitle, { color: colors.mutedForeground }]}>We've sent a 6-digit code to {email}</Text>
                  <View style={styles.otpRow}>
                    {otp.map((digit, i) => (
                      <TextInput key={i} style={[styles.otpInput, { borderColor: digit ? colors.primary : colors.border, color: colors.foreground }]} maxLength={1} keyboardType="number-pad" value={digit} onChangeText={(t) => { const newOtp = [...otp]; newOtp[i] = t; setOtp(newOtp); }} />
                    ))}
                  </View>
                  <TouchableOpacity onPress={() => {}}><Text style={[styles.resendText, { color: colors.primary }]}>Resend OTP</Text></TouchableOpacity>
                </MotiView>
              )}
              <TouchableOpacity style={styles.actionBtn} onPress={step === 3 ? handleVerifyOTP : handleNext} disabled={isLoading} activeOpacity={0.8}>
                <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.actionGradient}>
                  <Text style={styles.actionText}>{step === 3 ? 'Verify & Create Account' : isLoading ? 'Please wait...' : 'Continue'}</Text>
                </LinearGradient>
              </TouchableOpacity>
              <View style={styles.signInRow}>
                <Text style={{ color: colors.mutedForeground, fontSize: 14 }}>Already have an account? </Text>
                <TouchableOpacity onPress={() => router.push('/(auth)/login')}>
                  <Text style={{ color: colors.primary, fontSize: 14, fontWeight: '700' }}>Sign In</Text>
                </TouchableOpacity>
              </View>
            </MotiView>
          </ScrollView>
        </KeyboardAvoidingView>
      </LinearGradient>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 }, gradientBg: { flex: 1 }, container: { flex: 1 },
  scrollContent: { flexGrow: 1, justifyContent: 'center', padding: 16 },
  card: { backgroundColor: 'rgba(255,255,255,0.7)', borderRadius: 24, padding: 24, shadowColor: '#0ea5e9', shadowOffset: { width: 0, height: 10 }, shadowOpacity: 0.1, shadowRadius: 15, elevation: 5, borderWidth: 1, borderColor: 'rgba(255,255,255,0.5)' },
  backBtn: { marginBottom: 12 }, logoContainer: { alignItems: 'center', marginBottom: 16 },
  logoIcon: { width: 56, height: 56, borderRadius: 16, alignItems: 'center', justifyContent: 'center', marginBottom: 8 },
  logoText: { color: '#fff', fontSize: 24, fontWeight: '800' }, title: { fontSize: 20, fontWeight: '800', color: '#0f172a' },
  subtitle: { fontSize: 14, marginTop: 4 }, stepRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', marginBottom: 24, gap: 4 },
  stepItem: { alignItems: 'center' }, stepCircle: { width: 28, height: 28, borderRadius: 14, alignItems: 'center', justifyContent: 'center' },
  stepNum: { color: '#fff', fontSize: 12, fontWeight: '700' }, stepLabel: { fontSize: 9, fontWeight: '600', marginTop: 4 },
  stepLine: { width: 20, height: 2 }, errorText: { color: '#ef4444', fontSize: 12, fontWeight: '500', marginBottom: 12, textAlign: 'center' },
  label: { fontSize: 12, fontWeight: '600', marginBottom: 6 }, inputRow: { flexDirection: 'row', alignItems: 'center', gap: 8, paddingHorizontal: 12, height: 48, borderRadius: 12, borderWidth: 2, borderColor: 'transparent', marginBottom: 12 },
  input: { flex: 1, fontSize: 14, paddingVertical: 0 }, otpSubtitle: { fontSize: 14, textAlign: 'center', marginBottom: 16 },
  otpRow: { flexDirection: 'row', justifyContent: 'center', gap: 8, marginBottom: 16 },
  otpInput: { width: 44, height: 52, borderRadius: 12, borderWidth: 2, textAlign: 'center', fontSize: 20, fontWeight: '700' },
  resendText: { fontSize: 13, fontWeight: '600', textAlign: 'center', marginBottom: 16 },
  actionBtn: { borderRadius: 12, overflow: 'hidden', marginTop: 8 }, actionGradient: { paddingVertical: 16, alignItems: 'center', justifyContent: 'center' },
  actionText: { color: '#fff', fontSize: 16, fontWeight: '700' }, signInRow: { flexDirection: 'row', justifyContent: 'center', marginTop: 20 },
});
