import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Dimensions, ScrollView, Animated } from 'react-native';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import {
  Home, Compass, BookOpen, Clock, User, Search, Bell,
  Bookmark, Download, Award, Radio, Trophy, Settings,
  HelpCircle, FileText, BarChart3, Users, MessageCircle,
  ChevronRight, X, LogOut, Star, Flame, Target,
  GraduationCap, School, Calendar, Shield
} from 'lucide-react-native';
import { useNavigationStore, useAuthStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const SIDEBAR_WIDTH = 280;

interface SidebarItem {
  icon: React.ElementType;
  label: string;
  page: string;
  color?: string;
}

const mainItems: SidebarItem[] = [
  { icon: Home, label: 'Home', page: 'home' },
  { icon: Compass, label: 'Explore', page: 'explore' },
  { icon: BookOpen, label: 'My Courses', page: 'my-courses' },
  { icon: Search, label: 'Search', page: 'search' },
  { icon: Clock, label: 'Watch History', page: 'watch-history' },
  { icon: Bookmark, label: 'Bookmarks', page: 'bookmarks' },
  { icon: Download, label: 'Downloads', page: 'downloads' },
];

const learnItems: SidebarItem[] = [
  { icon: Award, label: 'Certificates', page: 'certificates' },
  { icon: Radio, label: 'Live Sessions', page: 'live-sessions' },
  { icon: Trophy, label: 'Achievements', page: 'achievements' },
  { icon: BarChart3, label: 'Leaderboard', page: 'leaderboard' },
  { icon: Users, label: 'Community', page: 'community' },
  { icon: GraduationCap, label: 'Instructors', page: 'instructors' },
  { icon: FileText, label: 'Assignments', page: 'assignment' },
  { icon: MessageCircle, label: 'Discussion', page: 'discussion' },
];

const examItems: SidebarItem[] = [
  { icon: Target, label: 'Exam Prep', page: 'exam-prep' },
  { icon: Calendar, label: 'Exam Schedule', page: 'exam-schedule' },
  { icon: BarChart3, label: 'Exam Results', page: 'exam-results' },
  { icon: Flame, label: 'Practice Tests', page: 'exam-practice' },
  { icon: Star, label: 'Exam Tips', page: 'exam-tips' },
];

const accountItems: SidebarItem[] = [
  { icon: Settings, label: 'Settings', page: 'settings' },
  { icon: HelpCircle, label: 'Help & Support', page: 'help' },
  { icon: Shield, label: 'About DAKKHO', page: 'about' },
];

export function Sidebar() {
  const { currentPage, navigate, sidebarOpen, setSidebarOpen } = useNavigationStore();
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);
  const colors = LightColors;

  if (!sidebarOpen) return null;

  const renderSection = (title: string, items: SidebarItem[]) => (
    <View style={styles.section}>
      <Text style={[styles.sectionTitle, { color: colors.mutedForeground }]}>{title}</Text>
      {items.map((item, i) => {
        const Icon = item.icon;
        const isActive = currentPage === item.page;
        return (
          <TouchableOpacity
            key={item.page}
            style={[
              styles.menuItem,
              { backgroundColor: isActive ? `${colors.primary}15` : 'transparent' },
            ]}
            onPress={() => { navigate(item.page as any); setSidebarOpen(false); }}
            activeOpacity={0.7}
          >
            <Icon size={18} color={isActive ? colors.primary : colors.mutedForeground} />
            <Text style={[
              styles.menuLabel,
              { color: isActive ? colors.primary : colors.foreground, fontWeight: isActive ? '700' : '500' },
            ]}>
              {item.label}
            </Text>
            {isActive && <View style={[styles.activeDot, { backgroundColor: colors.primary }]} />}
          </TouchableOpacity>
        );
      })}
    </View>
  );

  return (
    <View style={styles.overlay}>
      {/* Backdrop */}
      <TouchableOpacity
        style={styles.backdrop}
        activeOpacity={1}
        onPress={() => setSidebarOpen(false)}
      />

      {/* Sidebar Panel */}
      <MotiView
        from={{ translateX: -SIDEBAR_WIDTH }}
        animate={{ translateX: 0 }}
        exit={{ translateX: -SIDEBAR_WIDTH }}
        transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        style={[styles.panel, { backgroundColor: colors.background }]}
      >
        <ScrollView showsVerticalScrollIndicator={false} style={styles.scrollView}>
          {/* Header */}
          <View style={[styles.header, { borderBottomColor: colors.border }]}>
            <View style={[styles.logoIcon, { backgroundColor: colors.primary }]}>
              <Text style={styles.logoText}>D</Text>
            </View>
            <View style={styles.headerInfo}>
              <Text style={[styles.userName, { color: colors.foreground }]}>{user?.fullName || 'Student'}</Text>
              <Text style={[styles.userEmail, { color: colors.mutedForeground }]}>{user?.email || 'student@dakkho.com'}</Text>
            </View>
            <TouchableOpacity onPress={() => setSidebarOpen(false)}>
              <X size={20} color={colors.mutedForeground} />
            </TouchableOpacity>
          </View>

          {renderSection('MAIN', mainItems)}
          {renderSection('LEARNING', learnItems)}
          {renderSection('EXAMS', examItems)}
          {renderSection('ACCOUNT', accountItems)}

          {/* Logout */}
          <TouchableOpacity
            style={[styles.logoutBtn, { borderTopColor: colors.border }]}
            onPress={logout}
          >
            <LogOut size={18} color="#ef4444" />
            <Text style={styles.logoutText}>Log Out</Text>
          </TouchableOpacity>
        </ScrollView>
      </MotiView>
    </View>
  );
}

const styles = StyleSheet.create({
  overlay: {
    position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
    zIndex: 100, flexDirection: 'row',
  },
  backdrop: {
    flex: 1, backgroundColor: 'rgba(0,0,0,0.4)',
  },
  panel: {
    width: SIDEBAR_WIDTH, height: '100%',
    borderRightWidth: 1, borderRightColor: 'rgba(255,255,255,0.1)',
  },
  scrollView: { flex: 1 },
  header: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    padding: 16, borderBottomWidth: 1,
  },
  logoIcon: {
    width: 40, height: 40, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
  },
  logoText: { color: '#fff', fontSize: 18, fontWeight: '800' },
  headerInfo: { flex: 1 },
  userName: { fontSize: 14, fontWeight: '700' },
  userEmail: { fontSize: 11 },
  section: { paddingVertical: 8 },
  sectionTitle: {
    fontSize: 10, fontWeight: '700', letterSpacing: 1.2,
    paddingHorizontal: 16, paddingVertical: 8,
  },
  menuItem: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    paddingHorizontal: 16, paddingVertical: 10, borderRadius: 8,
    marginHorizontal: 8,
  },
  menuLabel: { fontSize: 14, flex: 1 },
  activeDot: { width: 6, height: 6, borderRadius: 3 },
  logoutBtn: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    paddingHorizontal: 16, paddingVertical: 16, borderTopWidth: 1,
    marginTop: 8,
  },
  logoutText: { fontSize: 14, fontWeight: '600', color: '#ef4444' },
});
