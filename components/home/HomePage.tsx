import React, { useState, useEffect, useRef } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView, Dimensions, FlatList } from 'react-native';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { Play, Radio, Trophy, Flame, Clock, Star, Users, ChevronLeft, ChevronRight, BookOpen, Compass } from 'lucide-react-native';
import { useNavigationStore, useAuthStore, useServerConfigStore } from '@/lib/store';
import { courseApi, type Course, formatDuration, formatPrice } from '@/lib/api-client';
import { LightColors, Gradients } from '@/lib/theme';
import { SectionHeader, ProgressBar, AnimatedCounter } from '../shared';

const { width: SCREEN_WIDTH } = Dimensions.get('window');

// ── Hero Section (for non-enrolled users) ──
function HeroSection() {
  const navigate = useNavigationStore((s) => s.navigate);
  const colors = LightColors;

  return (
    <MotiView from={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 500 }}>
      <LinearGradient colors={['#0c1222', '#1e293b']} style={styles.hero}>
        <View style={styles.heroContent}>
          <Text style={styles.heroTitle}>শেখো. সফল হও. {'\n'}নেতৃত্ব দাও.</Text>
          <Text style={styles.heroSubtext}>
            বাংলাদেশের #১ পলিটেকনিক লার্নিং প্ল্যাটফর্ম
          </Text>
          <View style={styles.heroCtas}>
            <TouchableOpacity style={styles.heroPrimaryBtn} onPress={() => navigate('explore')}>
              <LinearGradient colors={['#0ea5e9', '#2563eb']} style={styles.heroPrimaryGradient}>
                <Play size={16} color="#fff" fill="#fff" />
                <Text style={styles.heroPrimaryText}>Explore Courses</Text>
              </LinearGradient>
            </TouchableOpacity>
            <TouchableOpacity style={styles.heroSecondaryBtn} onPress={() => navigate('my-courses')}>
              <BookOpen size={16} color="#fff" />
              <Text style={styles.heroSecondaryText}>My Courses</Text>
            </TouchableOpacity>
          </View>
          {/* Stats bar */}
          <View style={styles.heroStats}>
            {[
              { value: '50K+', label: 'Students' },
              { value: '1.2K+', label: 'Courses' },
              { value: '200+', label: 'Instructors' },
            ].map((s, i) => (
              <View key={i} style={styles.heroStat}>
                <Text style={styles.heroStatValue}>{s.value}</Text>
                <Text style={styles.heroStatLabel}>{s.label}</Text>
              </View>
            ))}
          </View>
        </View>
      </LinearGradient>
    </MotiView>
  );
}

// ── Enrolled Hero (for enrolled users) ──
function EnrolledHero() {
  const colors = LightColors;
  return (
    <LinearGradient colors={['#0c1222', '#1e293b']} style={styles.enrolledHero}>
      <Text style={styles.enrolledGreeting}>Welcome back! 👋</Text>
      <Text style={styles.enrolledTitle}>Continue your learning journey</Text>
      <ProgressBar progress={45} height={8} color="#0ea5e9" />
      <Text style={styles.enrolledProgress}>45% completed this week</Text>
    </LinearGradient>
  );
}

// ── Category Pills ──
const CATEGORIES = [
  { id: 'cse', label: '💻 CSE', color: '#0ea5e9' },
  { id: 'eee', label: '⚡ EEE', color: '#f59e0b' },
  { id: 'me', label: '🔧 ME', color: '#ef4444' },
  { id: 'ce', label: '🏗️ CE', color: '#10b981' },
  { id: 'ete', label: '📡 ETE', color: '#8b5cf6' },
  { id: 'all', label: '📚 All', color: '#0ea5e9' },
];

function CategoryPills() {
  const navigate = useNavigationStore((s) => s.navigate);
  return (
    <View style={styles.section}>
      <SectionHeader title="Categories" actionLabel="See All" onAction={() => navigate('explore')} />
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ gap: 8 }}>
        {CATEGORIES.map((cat, i) => (
          <MotiView key={cat.id} from={{ opacity: 0, scale: 0.8 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: i * 50 }}>
            <TouchableOpacity
              style={[styles.categoryPill, { backgroundColor: `${cat.color}15`, borderColor: `${cat.color}30` }]}
              onPress={() => navigate('category', { categoryId: cat.id })}
            >
              <Text style={[styles.categoryPillText, { color: cat.color }]}>{cat.label}</Text>
            </TouchableOpacity>
          </MotiView>
        ))}
      </ScrollView>
    </View>
  );
}

// ── Live Now ──
const LIVE_SESSIONS = [
  { id: '1', title: 'Power Systems Q&A', instructor: 'Dr. Shahid', viewers: 142, subject: 'EEE' },
  { id: '2', title: 'React Hooks Deep Dive', instructor: 'Taslima K.', viewers: 89, subject: 'CSE' },
  { id: '3', title: 'Arduino Project Build', instructor: 'Fatema B.', viewers: 64, subject: 'ETE' },
  { id: '4', title: 'Thermodynamics Live', instructor: 'Prof. Kamal', viewers: 53, subject: 'ME' },
  { id: '5', title: 'Structural Analysis', instructor: 'Engr. Rahim', viewers: 41, subject: 'CE' },
];
const LIVE_COLORS = [['#ef4444', '#dc2626'], ['#0ea5e9', '#2563eb'], ['#10b981', '#059669'], ['#f59e0b', '#d97706'], ['#8b5cf6', '#7c3aed']];

function LiveNow() {
  const navigate = useNavigationStore((s) => s.navigate);
  return (
    <View style={styles.section}>
      <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 12 }}>
        <MotiView animate={{ scale: [1, 1.3, 1] }} transition={{ duration: 1500, loop: true }}>
          <Radio size={20} color="#ef4444" />
        </MotiView>
        <Text style={styles.sectionTitle}>Live Now</Text>
        <View style={styles.liveBadge}>
          <Text style={styles.liveBadgeText}>{LIVE_SESSIONS.length} LIVE</Text>
        </View>
      </View>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ gap: 12 }}>
        {LIVE_SESSIONS.map((session, i) => (
          <MotiView key={session.id} from={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 60 }}>
            <TouchableOpacity style={styles.liveCard} onPress={() => navigate('live-sessions')} activeOpacity={0.8}>
              <LinearGradient colors={LIVE_COLORS[i] as [string, string]} style={styles.liveThumbnail}>
                <View style={styles.liveIndicator}>
                  <View style={styles.liveDot} />
                  <Text style={styles.liveText}>LIVE</Text>
                </View>
                <View style={styles.liveSubjectBadge}>
                  <Text style={styles.liveSubjectText}>{session.subject}</Text>
                </View>
                <View style={styles.liveBottomOverlay}>
                  <Text style={styles.liveTitle}>{session.title}</Text>
                  <View style={styles.liveMetaRow}>
                    <View style={styles.liveMeta}>
                      <Users size={10} color="rgba(255,255,255,0.7)" />
                      <Text style={styles.liveMetaText}>{session.viewers}</Text>
                    </View>
                  </View>
                </View>
              </LinearGradient>
              <View style={styles.liveInstructorBar}>
                <Text style={styles.liveInstructorName}>{session.instructor}</Text>
              </View>
            </TouchableOpacity>
          </MotiView>
        ))}
      </ScrollView>
    </View>
  );
}

// ── New Releases ──
const NEW_COLORS = [['#0ea5e9', '#2563eb'], ['#10b981', '#059669'], ['#8b5cf6', '#7c3aed'], ['#f59e0b', '#d97706'], ['#f43f5e', '#e11d48']];

function NewReleases() {
  const navigate = useNavigationStore((s) => s.navigate);
  const [courses, setCourses] = useState<Course[]>([]);

  useEffect(() => {
    courseApi.list({ limit: 8 }).then((res) => setCourses(res.courses)).catch(() => {});
  }, []);

  return (
    <View style={styles.section}>
      <SectionHeader title="New Releases" />
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ gap: 12 }}>
        {courses.length > 0 ? courses.map((course, i) => (
          <MotiView key={course.id} from={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 50 }}>
            <TouchableOpacity
              style={styles.courseCard}
              onPress={() => navigate('course-detail', { courseId: course.id })}
              activeOpacity={0.8}
            >
              <LinearGradient colors={NEW_COLORS[i % 5] as [string, string]} style={styles.courseThumbnail}>
                <Play size={32} color="rgba(255,255,255,0.3)" />
                <View style={styles.newBadge}><Text style={styles.newBadgeText}>NEW</Text></View>
                <View style={styles.durationBadge}>
                  <Clock size={10} color="#fff" />
                  <Text style={styles.durationText}>{formatDuration(course.duration)}</Text>
                </View>
              </LinearGradient>
              <View style={styles.courseInfo}>
                <Text style={styles.courseTitle} numberOfLines={1}>{course.title}</Text>
                <Text style={styles.courseInstructor}>{course.instructorName || 'Instructor'}</Text>
                <View style={styles.courseMeta}>
                  <View style={styles.courseRating}>
                    <Star size={12} color="#f59e0b" fill="#f59e0b" />
                    <Text style={styles.courseRatingText}>{course.rating}</Text>
                  </View>
                  <View style={styles.courseStudents}>
                    <Users size={12} color="#94a3b8" />
                    <Text style={styles.courseStudentsText}>{course.totalStudents}</Text>
                  </View>
                </View>
              </View>
            </TouchableOpacity>
          </MotiView>
        )) : (
          // Placeholder courses
          [1, 2, 3, 4].map((_, i) => (
            <View key={i} style={styles.courseCard}>
              <LinearGradient colors={NEW_COLORS[i % 5] as [string, string]} style={styles.courseThumbnail}>
                <Play size={32} color="rgba(255,255,255,0.3)" />
              </LinearGradient>
              <View style={styles.courseInfo}>
                <Text style={styles.courseTitle}>Course {i + 1}</Text>
                <Text style={styles.courseInstructor}>Instructor</Text>
              </View>
            </View>
          ))
        )}
      </ScrollView>
    </View>
  );
}

// ── Weekly Leaderboard ──
const LEADERBOARD = [
  { rank: 1, name: 'Rahim Ahmed', xp: 12450, initials: 'RA' },
  { rank: 2, name: 'Fatima Khan', xp: 11200, initials: 'FK' },
  { rank: 3, name: 'Kamal Hossain', xp: 9870, initials: 'KH' },
  { rank: 4, name: 'Nusrat Jahan', xp: 8540, initials: 'NJ' },
  { rank: 5, name: 'Tanvir Islam', xp: 7320, initials: 'TI' },
];
const RANK_COLORS = ['#f59e0b', '#94a3b8', '#f97316'];

function WeeklyLeaderboard() {
  const navigate = useNavigationStore((s) => s.navigate);
  const colors = LightColors;

  return (
    <View style={styles.section}>
      <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 12 }}>
        <Trophy size={20} color="#f59e0b" />
        <Text style={styles.sectionTitle}>Weekly Leaderboard</Text>
      </View>
      <View style={[styles.leaderboardCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
        {LEADERBOARD.map((student, i) => (
          <MotiView key={student.rank} from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 60 }}>
            <View style={styles.leaderboardRow}>
              <View style={[styles.rankBadge, i < 3 ? { backgroundColor: RANK_COLORS[i] } : { backgroundColor: colors.muted }]}>
                <Text style={styles.rankText}>{student.rank}</Text>
              </View>
              <View style={[styles.avatarCircle, { backgroundColor: colors.primary }]}>
                <Text style={styles.avatarInitials}>{student.initials}</Text>
              </View>
              <View style={styles.leaderboardInfo}>
                <Text style={[styles.leaderboardName, { color: colors.foreground }]}>{student.name}</Text>
                <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}>
                  <Flame size={12} color="#f97316" />
                  <Text style={{ fontSize: 10, color: colors.mutedForeground }}>{student.rank <= 3 ? 'Top Performer' : 'Rising Star'}</Text>
                </View>
              </View>
              <View style={styles.xpContainer}>
                <AnimatedCounter target={student.xp} style={{ fontSize: 14, fontWeight: '800', color: colors.primary }} />
                <Text style={{ fontSize: 10, color: colors.mutedForeground }}>XP</Text>
              </View>
            </View>
          </MotiView>
        ))}
      </View>
    </View>
  );
}

// ── Main HomePage ──
export function HomePage() {
  const isHomeSectionVisible = useServerConfigStore((s) => s.isHomeSectionVisible);
  const user = useAuthStore((s) => s.user);
  const hasEnrolled = !!(user?.enrolledCourseIds && user.enrolledCourseIds.length > 0);

  return (
    <ScrollView style={styles.container} showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
      {isHomeSectionVisible('hero') && (hasEnrolled ? <EnrolledHero /> : <HeroSection />)}
      {isHomeSectionVisible('categories') && <CategoryPills />}
      {isHomeSectionVisible('new-releases') && <NewReleases />}
      {isHomeSectionVisible('live') && <LiveNow />}
      {isHomeSectionVisible('leaderboard') && <WeeklyLeaderboard />}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f9ff' },
  section: { marginBottom: 24, paddingHorizontal: 16 },
  sectionTitle: { fontSize: 18, fontWeight: '800', color: '#0f172a' },

  // Hero
  hero: { margin: 16, borderRadius: 24, overflow: 'hidden' },
  heroContent: { padding: 24 },
  heroTitle: { fontSize: 32, fontWeight: '800', color: '#fff', lineHeight: 38, marginBottom: 8 },
  heroSubtext: { fontSize: 14, color: '#94a3b8', marginBottom: 20 },
  heroCtas: { flexDirection: 'row', gap: 12, marginBottom: 20 },
  heroPrimaryBtn: { borderRadius: 12, overflow: 'hidden' },
  heroPrimaryGradient: { flexDirection: 'row', alignItems: 'center', gap: 8, paddingVertical: 14, paddingHorizontal: 24 },
  heroPrimaryText: { color: '#fff', fontSize: 15, fontWeight: '700' },
  heroSecondaryBtn: {
    flexDirection: 'row', alignItems: 'center', gap: 8, paddingVertical: 14, paddingHorizontal: 24,
    borderRadius: 12, borderWidth: 1, borderColor: 'rgba(255,255,255,0.3)',
  },
  heroSecondaryText: { color: '#fff', fontSize: 15, fontWeight: '600' },
  heroStats: { flexDirection: 'row', gap: 24 },
  heroStat: {},
  heroStatValue: { fontSize: 16, fontWeight: '800', color: '#0ea5e9' },
  heroStatLabel: { fontSize: 11, color: '#94a3b8' },

  // Enrolled hero
  enrolledHero: { margin: 16, borderRadius: 24, padding: 24 },
  enrolledGreeting: { fontSize: 14, color: '#94a3b8', marginBottom: 4 },
  enrolledTitle: { fontSize: 20, fontWeight: '800', color: '#fff', marginBottom: 16 },
  enrolledProgress: { fontSize: 12, color: '#94a3b8', marginTop: 8 },

  // Category pills
  categoryPill: { paddingHorizontal: 16, paddingVertical: 10, borderRadius: 20, borderWidth: 1 },
  categoryPillText: { fontSize: 13, fontWeight: '600' },

  // Live
  liveBadge: { backgroundColor: 'rgba(239,68,68,0.1)', paddingHorizontal: 8, paddingVertical: 2, borderRadius: 10 },
  liveBadgeText: { fontSize: 10, fontWeight: '700', color: '#ef4444' },
  liveCard: { width: 150, borderRadius: 16, overflow: 'hidden', backgroundColor: '#fff', elevation: 3, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4 },
  liveThumbnail: { aspectRatio: 3 / 4, justifyContent: 'space-between', padding: 10 },
  liveIndicator: { flexDirection: 'row', alignItems: 'center', gap: 4, alignSelf: 'flex-end' },
  liveDot: { width: 6, height: 6, borderRadius: 3, backgroundColor: '#ef4444' },
  liveText: { fontSize: 9, fontWeight: '700', color: '#fff', letterSpacing: 1 },
  liveSubjectBadge: { position: 'absolute', top: 10, left: 10, backgroundColor: 'rgba(0,0,0,0.4)', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 4 },
  liveSubjectText: { fontSize: 10, fontWeight: '700', color: '#fff' },
  liveBottomOverlay: { position: 'absolute', bottom: 0, left: 0, right: 0, padding: 10, backgroundColor: 'linear-gradient(transparent, rgba(0,0,0,0.8))' },
  liveTitle: { fontSize: 13, fontWeight: '700', color: '#fff', marginBottom: 4 },
  liveMetaRow: { flexDirection: 'row', gap: 8 },
  liveMeta: { flexDirection: 'row', alignItems: 'center', gap: 3 },
  liveMetaText: { fontSize: 10, color: 'rgba(255,255,255,0.7)' },
  liveInstructorBar: { paddingHorizontal: 10, paddingVertical: 8, borderTopWidth: 1, borderTopColor: 'rgba(255,255,255,0.2)' },
  liveInstructorName: { fontSize: 11, fontWeight: '600', color: '#0f172a' },

  // Course cards
  courseCard: { width: 240, borderRadius: 16, overflow: 'hidden', backgroundColor: '#fff', elevation: 3, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, shadowRadius: 4 },
  courseThumbnail: { aspectRatio: 16 / 9, alignItems: 'center', justifyContent: 'center' },
  newBadge: { position: 'absolute', top: 8, left: 8, backgroundColor: 'rgba(16,185,129,0.8)', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 10 },
  newBadgeText: { fontSize: 9, fontWeight: '700', color: '#fff' },
  durationBadge: { position: 'absolute', bottom: 8, right: 8, flexDirection: 'row', alignItems: 'center', gap: 3, backgroundColor: 'rgba(0,0,0,0.5)', paddingHorizontal: 5, paddingVertical: 2, borderRadius: 4 },
  durationText: { fontSize: 10, fontWeight: '700', color: '#fff' },
  courseInfo: { padding: 12, gap: 4 },
  courseTitle: { fontSize: 14, fontWeight: '700', color: '#0f172a' },
  courseInstructor: { fontSize: 12, color: '#94a3b8' },
  courseMeta: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  courseRating: { flexDirection: 'row', alignItems: 'center', gap: 3 },
  courseRatingText: { fontSize: 12, color: '#0f172a', fontWeight: '600' },
  courseStudents: { flexDirection: 'row', alignItems: 'center', gap: 3 },
  courseStudentsText: { fontSize: 12, color: '#94a3b8' },

  // Leaderboard
  leaderboardCard: { borderRadius: 16, padding: 16, borderWidth: 1, gap: 8 },
  leaderboardRow: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingVertical: 10 },
  rankBadge: { width: 32, height: 32, borderRadius: 8, alignItems: 'center', justifyContent: 'center' },
  rankText: { color: '#fff', fontSize: 14, fontWeight: '800' },
  avatarCircle: { width: 36, height: 36, borderRadius: 18, alignItems: 'center', justifyContent: 'center' },
  avatarInitials: { color: '#fff', fontSize: 12, fontWeight: '700' },
  leaderboardInfo: { flex: 1 },
  leaderboardName: { fontSize: 14, fontWeight: '600' },
  xpContainer: { alignItems: 'flex-end' },
});
