import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { User, Lock, BarChart3, CreditCard, Users2, Trash2, Mail, ChevronRight, LogOut, BookOpen, Award, Clock } from 'lucide-react-native';
import { useNavigationStore, useAuthStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const MENU_ITEMS = [
  { section: 'Account', items: [
    { icon: User, label: 'Edit Profile', page: 'edit-profile' as const },
    { icon: Lock, label: 'Change Password', page: 'change-password' as const },
    { icon: Mail, label: 'Verify Email', page: 'verify-email' as const },
  ]},
  { section: 'Learning', items: [
    { icon: BarChart3, label: 'Learning Stats', page: 'learning-stats' as const },
    { icon: CreditCard, label: 'Subscription', page: 'subscription' as const },
    { icon: Users2, label: 'Referral Program', page: 'referral' as const },
  ]},
  { section: 'Danger', items: [
    { icon: Trash2, label: 'Delete Account', page: 'delete-account' as const, danger: true },
  ]},
];

export function ProfilePage() {
  const navigate = useNavigationStore((s) => s.navigate);
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);
  const colors = LightColors;

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
        {/* Profile Header */}
        <LinearGradient colors={['#0c1222', '#1e293b']} style={styles.profileHeader}>
          <View style={styles.avatarContainer}>
            <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.avatar}>
              <Text style={styles.avatarText}>{user?.fullName?.charAt(0) || 'U'}</Text>
            </LinearGradient>
          </View>
          <Text style={styles.userName}>{user?.fullName || 'Student'}</Text>
          <Text style={styles.userEmail}>{user?.email || 'student@dakkho.com'}</Text>
          {user?.instituteName && <Text style={styles.userInstitute}>{user.instituteName}</Text>}
          {user?.technologyName && <Text style={styles.userTech}>{user.technologyName}</Text>}
        </LinearGradient>

        {/* Stats */}
        <View style={styles.statsRow}>
          {[
            { icon: BookOpen, value: '12', label: 'Courses' },
            { icon: Clock, value: '48h', label: 'Learned' },
            { icon: Award, value: '3', label: 'Certificates' },
          ].map((stat, i) => (
            <View key={i} style={[styles.statCard, { backgroundColor: colors.card }]}>
              <stat.icon size={20} color={colors.primary} />
              <Text style={[styles.statValue, { color: colors.forefront }]}>{stat.value}</Text>
              <Text style={[styles.statLabel, { color: colors.mutedForeground }]}>{stat.label}</Text>
            </View>
          ))}
        </View>

        {/* Menu Sections */}
        {MENU_ITEMS.map(section => (
          <View key={section.section} style={styles.menuSection}>
            <Text style={[styles.menuSectionTitle, { color: colors.mutedForeground }]}>{section.section}</Text>
            <View style={[styles.menuCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
              {section.items.map((item, i) => {
                const Icon = item.icon;
                return (
                  <TouchableOpacity
                    key={item.label}
                    style={[styles.menuItem, i < section.items.length - 1 && { borderBottomWidth: 1, borderBottomColor: colors.border }]}
                    onPress={() => navigate(item.page)}
                    activeOpacity={0.7}
                  >
                    <View style={[styles.menuIconCircle, { backgroundColor: item.danger ? '#fef2f2' : `${colors.primary}15` }]}>
                      <Icon size={18} color={item.danger ? '#ef4444' : colors.primary} />
                    </View>
                    <Text style={[styles.menuLabel, { color: item.danger ? '#ef4444' : colors.foreground }]}>{item.label}</Text>
                    <ChevronRight size={18} color={colors.mutedForeground} />
                  </TouchableOpacity>
                );
              })}
            </View>
          </View>
        ))}

        {/* Logout */}
        <TouchableOpacity style={styles.logoutBtn} onPress={logout} activeOpacity={0.7}>
          <LogOut size={18} color="#ef4444" />
          <Text style={styles.logoutText}>Log Out</Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  profileHeader: { alignItems: 'center', paddingVertical: 32, paddingHorizontal: 16, borderBottomLeftRadius: 32, borderBottomRightRadius: 32 },
  avatarContainer: { marginBottom: 12 },
  avatar: { width: 80, height: 80, borderRadius: 40, alignItems: 'center', justifyContent: 'center' },
  avatarText: { fontSize: 32, fontWeight: '800', color: '#fff' },
  userName: { fontSize: 22, fontWeight: '800', color: '#fff', marginBottom: 4 },
  userEmail: { fontSize: 14, color: '#94a3b8', marginBottom: 2 },
  userInstitute: { fontSize: 13, color: '#0ea5e9', fontWeight: '600' },
  userTech: { fontSize: 12, color: '#94a3b8' },
  statsRow: { flexDirection: 'row', gap: 12, paddingHorizontal: 16, marginTop: -24, marginBottom: 16 },
  statCard: { flex: 1, borderRadius: 16, padding: 16, alignItems: 'center', gap: 4, elevation: 3, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.08, shadowRadius: 4 },
  statValue: { fontSize: 20, fontWeight: '800' },
  statLabel: { fontSize: 11 },
  menuSection: { marginBottom: 16, paddingHorizontal: 16 },
  menuSectionTitle: { fontSize: 11, fontWeight: '700', letterSpacing: 1, marginBottom: 8, paddingHorizontal: 4 },
  menuCard: { borderRadius: 16, borderWidth: 1, overflow: 'hidden' },
  menuItem: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingHorizontal: 16, paddingVertical: 14 },
  menuIconCircle: { width: 36, height: 36, borderRadius: 10, alignItems: 'center', justifyContent: 'center' },
  menuLabel: { flex: 1, fontSize: 15, fontWeight: '500' },
  logoutBtn: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, paddingVertical: 16, marginTop: 8 },
  logoutText: { fontSize: 16, fontWeight: '600', color: '#ef4444' },
});
