import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { BookOpen, Clock, CheckCircle, Heart, Star, Play } from 'lucide-react-native';
import { useNavigationStore, useAuthStore } from '@/lib/store';
import { enrollmentApi, type Enrollment, type Course, formatDuration } from '@/lib/api-client';
import { LightColors } from '@/lib/theme';
import { ProgressBar, EmptyState } from '../shared';

const TABS = ['Ongoing', 'Completed', 'Wishlist'] as const;
const THUMBNAIL_COLORS = [['#0ea5e9','#2563eb'],['#10b981','#059669'],['#8b5cf6','#7c3aed'],['#f59e0b','#d97706'],['#f43f5e','#e11d48']];

export function MyCoursesPage() {
  const [activeTab, setActiveTab] = useState<typeof TABS[number]>('Ongoing');
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const navigate = useNavigationStore((s) => s.navigate);
  const colors = LightColors;

  useEffect(() => {
    enrollmentApi.getMyEnrollments().then(setEnrollments).catch(() => {});
  }, []);

  const ongoing = enrollments.filter(e => e.progress < 100);
  const completed = enrollments.filter(e => e.progress >= 100);

  const currentList = activeTab === 'Ongoing' ? ongoing : activeTab === 'Completed' ? completed : [];

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      {/* Tabs */}
      <View style={styles.tabRow}>
        {TABS.map(tab => (
          <TouchableOpacity
            key={tab}
            style={[styles.tab, activeTab === tab && { backgroundColor: colors.primary }]}
            onPress={() => setActiveTab(tab)}
          >
            <Text style={[styles.tabText, activeTab === tab && { color: '#fff' }]}>{tab}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100, paddingHorizontal: 16 }}>
        {currentList.length > 0 ? currentList.map((enrollment, i) => (
          <MotiView key={enrollment.id} from={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 50 }}>
            <TouchableOpacity
              style={[styles.courseCard, { backgroundColor: colors.card, borderColor: colors.border }]}
              onPress={() => navigate('course-detail', { courseId: enrollment.courseId })}
              activeOpacity={0.8}
            >
              <LinearGradient colors={THUMBNAIL_COLORS[i % 5] as [string,string]} style={styles.thumbnail}>
                <Play size={20} color="rgba(255,255,255,0.5)" fill="rgba(255,255,255,0.5)" />
              </LinearGradient>
              <View style={styles.cardContent}>
                <Text style={[styles.cardTitle, { color: colors.foreground }]} numberOfLines={2}>
                  {enrollment.course?.title || 'Course'}
                </Text>
                <Text style={[styles.cardInstructor, { color: colors.mutedForeground }]}>Instructor</Text>
                <View style={styles.progressRow}>
                  <ProgressBar progress={enrollment.progress} height={6} />
                  <Text style={[styles.progressText, { color: colors.primary }]}>{enrollment.progress}%</Text>
                </View>
                {enrollment.progress >= 100 && (
                  <View style={styles.completedBadge}>
                    <CheckCircle size={14} color="#10b981" />
                    <Text style={styles.completedText}>Completed</Text>
                  </View>
                )}
              </View>
            </TouchableOpacity>
          </MotiView>
        )) : (
          <EmptyState
            icon={<BookOpen size={48} color={colors.mutedForeground} />}
            title={`No ${activeTab.toLowerCase()} courses`}
            description={activeTab === 'Ongoing' ? 'Enroll in courses to start learning' : 'Complete courses to see them here'}
            actionLabel={activeTab === 'Ongoing' ? 'Explore Courses' : undefined}
            onAction={activeTab === 'Ongoing' ? () => navigate('explore') : undefined}
          />
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  tabRow: { flexDirection: 'row', paddingHorizontal: 16, paddingVertical: 12, gap: 8 },
  tab: { paddingHorizontal: 20, paddingVertical: 8, borderRadius: 20, backgroundColor: '#f1f5f9' },
  tabText: { fontSize: 13, fontWeight: '600', color: '#64748b' },
  courseCard: { flexDirection: 'row', borderRadius: 16, borderWidth: 1, overflow: 'hidden', marginBottom: 12, elevation: 2, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.06, shadowRadius: 4 },
  thumbnail: { width: 100, aspectRatio: 1, alignItems: 'center', justifyContent: 'center' },
  cardContent: { flex: 1, padding: 12, gap: 4 },
  cardTitle: { fontSize: 14, fontWeight: '700', lineHeight: 18 },
  cardInstructor: { fontSize: 12 },
  progressRow: { flexDirection: 'row', alignItems: 'center', gap: 8, marginTop: 4 },
  progressText: { fontSize: 12, fontWeight: '700', minWidth: 36 },
  completedBadge: { flexDirection: 'row', alignItems: 'center', gap: 4, marginTop: 2 },
  completedText: { fontSize: 11, fontWeight: '600', color: '#10b981' },
});
