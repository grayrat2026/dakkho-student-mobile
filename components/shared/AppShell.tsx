import React from 'react';
import { View, Text, ScrollView, StyleSheet, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { useNavigationStore } from '@/lib/store';
import { LightColors, DEPT_TO_TECHNOLOGY, TECHNOLOGY_SHORT_NAMES } from '@/lib/theme';
import { TopBar } from './TopBar';
import { BottomNav } from './BottomNav';
import { Sidebar } from './Sidebar';

// Page components
import { HomePage } from '@/components/home/HomePage';
import { ExplorePage } from '@/components/explore/ExplorePage';
import { MyCoursesPage } from '@/components/courses/MyCoursesPage';
import { ProfilePage } from '@/components/profile/ProfilePage';
import { SearchPage } from '@/components/search/SearchPage';
import { NotificationsPage } from '@/components/notifications/NotificationsPage';
import { CourseDetailPage } from '@/components/course/CourseDetailPage';
import { VideoPlayerPage } from '@/components/video/VideoPlayerPage';
import { SettingsPage } from '@/components/settings/SettingsPage';
import { DepartmentPageTemplate } from '@/components/department/DepartmentPageTemplate';
import { SemesterPageTemplate } from '@/components/semester/SemesterPageTemplate';

// Department color map
const DEPT_COLORS: Record<string, string> = {
  'dept-cse': '#0ea5e9',
  'dept-ete': '#8b5cf6',
  'dept-eee': '#f59e0b',
  'dept-me': '#ef4444',
  'dept-ce': '#10b981',
  'dept-architecture': '#f97316',
  'dept-textile': '#ec4899',
  'dept-chemical': '#14b8a6',
  'dept-automobile': '#6366f1',
  'dept-rac': '#84cc16',
  'dept-glass-ceramic': '#06b6d4',
  'dept-printing': '#a855f7',
  'dept-surveying': '#eab308',
  'dept-mechatronics': '#f43f5e',
  'dept-mining': '#78716c',
  'dept-metallurgical': '#fb923c',
  'dept-power': '#22d3ee',
  'dept-instrumentation': '#c084fc',
  'dept-food': '#4ade80',
  'dept-leather': '#a3a3a3',
};

const DEPT_NAMES: Record<string, string> = {
  'dept-cse': 'Computer Science & Technology',
  'dept-ete': 'Electronics & Telecommunication Engineering',
  'dept-eee': 'Electrical Engineering',
  'dept-me': 'Mechanical Engineering',
  'dept-ce': 'Civil Engineering',
  'dept-architecture': 'Architecture',
  'dept-textile': 'Textile Technology',
  'dept-chemical': 'Chemical Technology',
  'dept-automobile': 'Automobile Technology',
  'dept-rac': 'Refrigeration & Air Conditioning',
  'dept-glass-ceramic': 'Glass & Ceramic Technology',
  'dept-printing': 'Printing Technology',
  'dept-surveying': 'Surveying Technology',
  'dept-mechatronics': 'Mechatronics Technology',
  'dept-mining': 'Mining Technology',
  'dept-metallurgical': 'Metallurgical Technology',
  'dept-power': 'Power Technology',
  'dept-instrumentation': 'Instrumentation Technology',
  'dept-food': 'Food Technology',
  'dept-leather': 'Leather Technology',
};

// Placeholder page for unbuilt pages
function PlaceholderPage({ name, onBack }: { name: string; onBack: () => void }) {
  const colors = LightColors;
  const displayName = name.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());

  return (
    <View style={styles.placeholder}>
      <MotiView
        from={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ type: 'spring', stiffness: 300, damping: 25 }}
        style={styles.placeholderCard}
      >
        <View style={styles.placeholderIcon}>
          <Text style={styles.placeholderIconText}>
            {displayName.charAt(0)}
          </Text>
        </View>
        <Text style={[styles.placeholderTitle, { color: colors.foreground }]}>
          {displayName}
        </Text>
        <Text style={[styles.placeholderDesc, { color: colors.mutedForeground }]}>
          This page is coming soon. We're building it to match the exact same UI as the web app.
        </Text>
        <TouchableOpacity style={styles.placeholderBtn} onPress={onBack}>
          <Text style={styles.placeholderBtnText}>Go Back</Text>
        </TouchableOpacity>
      </MotiView>
    </View>
  );
}

// Page Router — renders the current page based on navigation state
function PageRouter() {
  const { currentPage, pageParams, goBack } = useNavigationStore();

  // Auth pages (redirected by App.tsx, but just in case)
  if (currentPage === 'login' || currentPage === 'signup' || currentPage === 'forgot-password') {
    return <PlaceholderPage name={currentPage} onBack={goBack} />;
  }

  // Main pages
  switch (currentPage) {
    case 'home':
      return <HomePage />;
    case 'explore':
      return <ExplorePage />;
    case 'my-courses':
      return <MyCoursesPage />;
    case 'profile':
      return <ProfilePage />;
    case 'search':
      return <SearchPage />;
    case 'notifications':
    case 'notification-detail':
      return <NotificationsPage />;
    case 'course-detail':
      return <CourseDetailPage />;
    case 'video-player':
      return <VideoPlayerPage />;
    case 'settings':
      return <SettingsPage />;
    default:
      break;
  }

  // Department pages
  if (currentPage.startsWith('dept-')) {
    const techCode = DEPT_TO_TECHNOLOGY[currentPage] || 'CST';
    const deptColor = DEPT_COLORS[currentPage] || '#0ea5e9';
    const deptName = DEPT_NAMES[currentPage] || currentPage.replace('dept-', '').toUpperCase();
    const shortName = TECHNOLOGY_SHORT_NAMES[techCode] || techCode;

    return (
      <DepartmentPageTemplate
        name={deptName}
        shortCode={techCode}
        description={`Explore courses and resources for ${deptName} at DAKKHO Academy.`}
        color={deptColor}
      />
    );
  }

  // Semester pages
  if (currentPage.startsWith('semester-')) {
    const semNum = parseInt(currentPage.replace('semester-', ''), 10);
    return (
      <SemesterPageTemplate
        semester={semNum}
        departmentName="Your Department"
      />
    );
  }

  // All other pages — placeholder
  return <PlaceholderPage name={currentPage} onBack={goBack} />;
}

export function AppShell() {
  return (
    <SafeAreaView style={styles.safeArea} edges={['top', 'bottom']}>
      <View style={styles.shell}>
        <TopBar />
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 80 }}>
          <PageRouter />
        </ScrollView>
        <BottomNav />
        <Sidebar />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: '#f0f9ff' },
  shell: { flex: 1 },
  content: { flex: 1 },
  placeholder: { flex: 1, alignItems: 'center', justifyContent: 'center', paddingVertical: 60, paddingHorizontal: 24 },
  placeholderCard: { alignItems: 'center', padding: 32, borderRadius: 24, backgroundColor: 'rgba(255,255,255,0.7)', borderWidth: 1, borderColor: 'rgba(255,255,255,0.5)', shadowColor: '#0ea5e9', shadowOffset: { width: 0, height: 10 }, shadowOpacity: 0.1, shadowRadius: 15, elevation: 5, width: '100%', maxWidth: 360 },
  placeholderIcon: { width: 64, height: 64, borderRadius: 32, backgroundColor: '#0c1222', alignItems: 'center', justifyContent: 'center', marginBottom: 16 },
  placeholderIconText: { fontSize: 28, fontWeight: '800', color: '#0ea5e9' },
  placeholderTitle: { fontSize: 20, fontWeight: '800', marginBottom: 8, textAlign: 'center', textTransform: 'capitalize' },
  placeholderDesc: { fontSize: 14, textAlign: 'center', lineHeight: 20, marginBottom: 20 },
  placeholderBtn: { backgroundColor: '#0ea5e9', paddingHorizontal: 24, paddingVertical: 12, borderRadius: 12 },
  placeholderBtnText: { color: '#fff', fontSize: 14, fontWeight: '700' },
});
