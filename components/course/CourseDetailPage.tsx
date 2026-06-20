import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { Star, Users, Clock, BarChart3, Play, BookOpen, FileText, MessageCircle, Bell, ChevronLeft, Heart, Share2 } from 'lucide-react-native';
import { useNavigationStore } from '@/lib/store';
import { type Course, formatDuration, formatPrice } from '@/lib/api-client';
import { LightColors } from '@/lib/theme';
import { GradientButton, ProgressBar, SectionHeader } from '../shared';

const TABS = ['Overview', 'Curriculum', 'Reviews', 'QA', 'Resources'];

export function CourseDetailPage() {
  const navigate = useNavigationStore((s) => s.navigate);
  const { pageParams } = useNavigationStore();
  const [activeTab, setActiveTab] = useState('Overview');
  const colors = LightColors;

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Back button */}
        <TouchableOpacity style={styles.backBtn} onPress={() => navigate('home')}>
          <ChevronLeft size={24} color="#fff" />
        </TouchableOpacity>

        {/* Course Header */}
        <LinearGradient colors={['#0c1222', '#1e293b']} style={styles.courseHeader}>
          <View style={styles.headerContent}>
            <View style={styles.badgeRow}>
              <View style={styles.levelBadge}><Text style={styles.levelBadgeText}>BEGINNER</Text></View>
              <View style={styles.featuredBadge}><Text style={styles.featuredBadgeText}>FEATURED</Text></View>
            </View>
            <Text style={styles.courseTitle}>Introduction to Computer Science</Text>
            <Text style={styles.courseInstructor}>by Dr. Shahid Hossain</Text>
            <View style={styles.headerStats}>
              <View style={styles.headerStat}><Star size={14} color="#f59e0b" fill="#f59e0b" /><Text style={styles.headerStatText}>4.8 (2.3k)</Text></View>
              <View style={styles.headerStat}><Users size={14} color="#94a3b8" /><Text style={styles.headerStatText}>5.2k students</Text></View>
              <View style={styles.headerStat}><Clock size={14} color="#94a3b8" /><Text style={styles.headerStatText}>24h</Text></View>
            </View>
          </View>
        </LinearGradient>

        {/* Price & Enroll */}
        <View style={[styles.enrollRow, { backgroundColor: colors.card }]}>
          <View>
            <Text style={styles.priceText}>৳499</Text>
            <Text style={styles.originalPrice}>৳999</Text>
          </View>
          <GradientButton size="lg" style={{ flex: 1, maxWidth: 200 }} onPress={() => {}}>
            <Play size={16} color="#fff" fill="#fff" />
            Enroll Now
          </GradientButton>
        </View>

        {/* Tabs */}
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ paddingHorizontal: 16, gap: 8, marginBottom: 16 }}>
          {TABS.map(tab => (
            <TouchableOpacity
              key={tab}
              style={[styles.tab, activeTab === tab ? { backgroundColor: colors.primary } : { backgroundColor: colors.muted }]}
              onPress={() => setActiveTab(tab)}
            >
              <Text style={[styles.tabText, activeTab === tab && { color: '#fff' }]}>{tab}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        {/* Tab Content */}
        <View style={{ paddingHorizontal: 16, paddingBottom: 100 }}>
          {activeTab === 'Overview' && (
            <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }}>
              <Text style={[styles.sectionTitle, { color: colors.foreground }]}>About This Course</Text>
              <Text style={[styles.bodyText, { color: colors.mutedForeground, lineHeight: 22 }]}>
                This comprehensive course covers the fundamentals of computer science including programming, data structures, algorithms, and software engineering principles. Designed for polytechnic students following the Bangladesh technical education curriculum.
              </Text>
              <Text style={[styles.sectionTitle, { color: colors.foreground, marginTop: 16 }]}>What You'll Learn</Text>
              {['Programming fundamentals with Python', 'Data structures and algorithms', 'Database management systems', 'Software engineering basics', 'Web development fundamentals'].map((item, i) => (
                <View key={i} style={styles.learnItem}>
                  <View style={styles.learnDot} />
                  <Text style={[styles.learnText, { color: colors.foreground }]}>{item}</Text>
                </View>
              ))}
            </MotiView>
          )}

          {activeTab === 'Curriculum' && (
            <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }}>
              {['Chapter 1: Introduction', 'Chapter 2: Variables & Data Types', 'Chapter 3: Control Flow', 'Chapter 4: Functions', 'Chapter 5: OOP Concepts'].map((ch, i) => (
                <View key={i} style={[styles.chapterCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                  <Text style={[styles.chapterTitle, { color: colors.forefront }]}>{ch}</Text>
                  <Text style={[styles.chapterInfo, { color: colors.mutedForeground }]}>{3 + i} lessons · {30 + i * 10} min</Text>
                </View>
              ))}
            </MotiView>
          )}

          {activeTab === 'Reviews' && (
            <MotiView from={{ opacity: 0 }} animate={{ opacity: 1 }}>
              <View style={styles.reviewSummary}>
                <Text style={styles.bigRating}>4.8</Text>
                <View>{[5,4,3,2,1].map(s => (
                  <View key={s} style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}>
                    <Star size={12} color="#f59e0b" fill={s <= 4 ? '#f59e0b' : 'none'} />
                    <ProgressBar progress={s === 5 ? 70 : s === 4 ? 20 : s === 3 ? 7 : s === 2 ? 2 : 1} height={4} width={100} />
                  </View>
                ))}</View>
              </View>
            </MotiView>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  backBtn: { position: 'absolute', top: 12, left: 16, zIndex: 10, width: 36, height: 36, borderRadius: 18, backgroundColor: 'rgba(0,0,0,0.3)', alignItems: 'center', justifyContent: 'center' },
  courseHeader: { padding: 24, paddingTop: 48, borderBottomLeftRadius: 32, borderBottomRightRadius: 32 },
  headerContent: {},
  badgeRow: { flexDirection: 'row', gap: 8, marginBottom: 12 },
  levelBadge: { backgroundColor: 'rgba(14,165,233,0.2)', paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  levelBadgeText: { fontSize: 10, fontWeight: '700', color: '#0ea5e9', letterSpacing: 1 },
  featuredBadge: { backgroundColor: 'rgba(245,158,11,0.2)', paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  featuredBadgeText: { fontSize: 10, fontWeight: '700', color: '#f59e0b', letterSpacing: 1 },
  courseTitle: { fontSize: 22, fontWeight: '800', color: '#fff', marginBottom: 6, lineHeight: 28 },
  courseInstructor: { fontSize: 14, color: '#94a3b8', marginBottom: 12 },
  headerStats: { flexDirection: 'row', gap: 16 },
  headerStat: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  headerStatText: { fontSize: 13, color: '#94a3b8', fontWeight: '500' },
  enrollRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', padding: 16, marginHorizontal: 16, marginTop: -20, borderRadius: 16, elevation: 5, shadowColor: '#000', shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.1, shadowRadius: 8 },
  priceText: { fontSize: 24, fontWeight: '800', color: '#0ea5e9' },
  originalPrice: { fontSize: 14, color: '#94a3b8', textDecorationLine: 'line-through' },
  tab: { paddingHorizontal: 16, paddingVertical: 8, borderRadius: 20 },
  tabText: { fontSize: 13, fontWeight: '600', color: '#64748b' },
  sectionTitle: { fontSize: 18, fontWeight: '700', marginBottom: 10 },
  bodyText: { fontSize: 14, marginBottom: 16 },
  learnItem: { flexDirection: 'row', alignItems: 'flex-start', gap: 8, marginBottom: 8 },
  learnDot: { width: 6, height: 6, borderRadius: 3, backgroundColor: '#0ea5e9', marginTop: 6 },
  learnText: { fontSize: 14, flex: 1 },
  chapterCard: { padding: 16, borderRadius: 12, borderWidth: 1, marginBottom: 8 },
  chapterTitle: { fontSize: 14, fontWeight: '600', marginBottom: 4 },
  chapterInfo: { fontSize: 12 },
  reviewSummary: { flexDirection: 'row', alignItems: 'center', gap: 20, marginBottom: 16 },
  bigRating: { fontSize: 48, fontWeight: '800', color: '#0ea5e9' },
});
