import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView, Dimensions } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { Search, SlidersHorizontal, Star, Users, Clock, BookOpen, Filter } from 'lucide-react-native';
import { useNavigationStore } from '@/lib/store';
import { courseApi, type Course, formatDuration, formatPrice } from '@/lib/api-client';
import { LightColors } from '@/lib/theme';
import { SectionHeader, EmptyState } from '../shared';

const { width } = Dimensions.get('window');
const CARD_WIDTH = (width - 48) / 2;

const LEVELS = ['All', 'Beginner', 'Intermediate', 'Advanced'];
const CATEGORIES = ['All', 'CSE', 'EEE', 'ME', 'CE', 'ETE'];
const THUMBNAIL_COLORS = [['#0ea5e9','#2563eb'],['#10b981','#059669'],['#8b5cf6','#7c3aed'],['#f59e0b','#d97706'],['#f43f5e','#e11d48']];

export function ExplorePage() {
  const navigate = useNavigationStore((s) => s.navigate);
  const [search, setSearch] = useState('');
  const [selectedLevel, setSelectedLevel] = useState('All');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [courses, setCourses] = useState<Course[]>([]);
  const colors = LightColors;

  useEffect(() => {
    courseApi.list({ limit: 50, search: search || undefined, level: selectedLevel !== 'All' ? selectedLevel.toLowerCase() : undefined }).then(res => setCourses(res.courses)).catch(() => {});
  }, [search, selectedLevel]);

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      {/* Search Bar */}
      <View style={styles.searchContainer}>
        <View style={[styles.searchBar, { backgroundColor: colors.card, borderColor: colors.border }]}>
          <Search size={18} color={colors.mutedForeground} />
          <TextInput
            style={[styles.searchInput, { color: colors.foreground }]}
            placeholder="Search courses, topics, instructors..."
            placeholderTextColor={colors.mutedForeground}
            value={search}
            onChangeText={setSearch}
          />
        </View>
      </View>

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
        {/* Level Filters */}
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ paddingHorizontal: 16, gap: 8, marginBottom: 12 }}>
          {LEVELS.map(level => (
            <TouchableOpacity
              key={level}
              style={[styles.filterPill, { backgroundColor: selectedLevel === level ? colors.primary : `${colors.primary}15`, borderColor: selectedLevel === level ? colors.primary : `${colors.primary}30` }]}
              onPress={() => setSelectedLevel(level)}
            >
              <Text style={[styles.filterPillText, { color: selectedLevel === level ? '#fff' : colors.primary }]}>{level}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        {/* Category Filters */}
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ paddingHorizontal: 16, gap: 8, marginBottom: 16 }}>
          {CATEGORIES.map(cat => (
            <TouchableOpacity
              key={cat}
              style={[styles.categoryPill, { backgroundColor: selectedCategory === cat ? colors.primary : colors.muted }]}
              onPress={() => setSelectedCategory(cat)}
            >
              <Text style={[styles.categoryPillText, { color: selectedCategory === cat ? '#fff' : colors.mutedForeground }]}>{cat}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        {/* Course Grid */}
        {courses.length > 0 ? (
          <View style={styles.courseGrid}>
            {courses.map((course, i) => (
              <MotiView key={course.id} from={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 40 }}>
                <TouchableOpacity
                  style={styles.courseCard}
                  onPress={() => navigate('course-detail', { courseId: course.id })}
                  activeOpacity={0.8}
                >
                  <LinearGradient colors={THUMBNAIL_COLORS[i % 5] as [string,string]} style={styles.thumbnail}>
                    <BookOpen size={24} color="rgba(255,255,255,0.4)" />
                    <View style={styles.levelBadge}>
                      <Text style={styles.levelBadgeText}>{course.level}</Text>
                    </View>
                  </LinearGradient>
                  <View style={styles.cardBody}>
                    <Text style={styles.cardTitle} numberOfLines={2}>{course.title}</Text>
                    <Text style={styles.cardInstructor}>{course.instructorName || 'Instructor'}</Text>
                    <View style={styles.cardMeta}>
                      <View style={styles.cardRating}>
                        <Star size={11} color="#f59e0b" fill="#f59e0b" />
                        <Text style={styles.cardRatingText}>{course.rating}</Text>
                      </View>
                      <Text style={styles.cardPrice}>{formatPrice(course.price)}</Text>
                    </View>
                  </View>
                </TouchableOpacity>
              </MotiView>
            ))}
          </View>
        ) : (
          <EmptyState
            icon={<Search size={48} color={colors.mutedForeground} />}
            title="No courses found"
            description="Try adjusting your search or filters"
          />
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  searchContainer: { paddingHorizontal: 16, paddingVertical: 8 },
  searchBar: { flexDirection: 'row', alignItems: 'center', gap: 8, paddingHorizontal: 16, height: 48, borderRadius: 16, borderWidth: 1 },
  searchInput: { flex: 1, fontSize: 14, paddingVertical: 0 },
  filterPill: { paddingHorizontal: 14, paddingVertical: 8, borderRadius: 20, borderWidth: 1 },
  filterPillText: { fontSize: 13, fontWeight: '600' },
  categoryPill: { paddingHorizontal: 14, paddingVertical: 6, borderRadius: 12 },
  categoryPillText: { fontSize: 12, fontWeight: '600' },
  courseGrid: { flexDirection: 'row', flexWrap: 'wrap', paddingHorizontal: 16, gap: 12 },
  courseCard: { width: CARD_WIDTH, borderRadius: 16, overflow: 'hidden', backgroundColor: '#fff', elevation: 3, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.08, shadowRadius: 4 },
  thumbnail: { aspectRatio: 16/10, alignItems: 'center', justifyContent: 'center' },
  levelBadge: { position: 'absolute', top: 8, right: 8, backgroundColor: 'rgba(0,0,0,0.4)', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 6 },
  levelBadgeText: { fontSize: 9, fontWeight: '700', color: '#fff', textTransform: 'uppercase' },
  cardBody: { padding: 10, gap: 4 },
  cardTitle: { fontSize: 13, fontWeight: '700', color: '#0f172a', lineHeight: 18 },
  cardInstructor: { fontSize: 11, color: '#94a3b8' },
  cardMeta: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: 2 },
  cardRating: { flexDirection: 'row', alignItems: 'center', gap: 3 },
  cardRatingText: { fontSize: 11, fontWeight: '600', color: '#0f172a' },
  cardPrice: { fontSize: 12, fontWeight: '800', color: '#0ea5e9' },
});
