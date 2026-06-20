import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { Search, X, Clock, TrendingUp, BookOpen, Users } from 'lucide-react-native';
import { useNavigationStore, useSearchStore } from '@/lib/store';
import { courseApi, instructorApi, type Course, type Instructor } from '@/lib/api-client';
import { LightColors } from '@/lib/theme';

export function SearchPage() {
  const { query, setQuery, recentSearches, addRecentSearch, clearRecentSearches } = useSearchStore();
  const navigate = useNavigationStore((s) => s.navigate);
  const [results, setResults] = useState<Course[]>([]);
  const [searching, setSearching] = useState(false);
  const colors = LightColors;

  useEffect(() => {
    if (query.trim().length >= 2) {
      setSearching(true);
      const timer = setTimeout(() => {
        courseApi.search(query).then(res => {
          setResults(res.courses);
          addRecentSearch(query);
          setSearching(false);
        }).catch(() => setSearching(false));
      }, 500);
      return () => clearTimeout(timer);
    } else {
      setResults([]);
    }
  }, [query]);

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      {/* Search Bar */}
      <View style={styles.searchContainer}>
        <View style={[styles.searchBar, { backgroundColor: colors.card, borderColor: colors.primary }]}>
          <Search size={20} color={colors.primary} />
          <TextInput
            style={[styles.searchInput, { color: colors.foreground }]}
            placeholder="Search courses, instructors..."
            placeholderTextColor={colors.mutedForeground}
            value={query}
            onChangeText={setQuery}
            autoFocus
          />
          {query ? (
            <TouchableOpacity onPress={() => setQuery('')}>
              <X size={18} color={colors.mutedForeground} />
            </TouchableOpacity>
          ) : null}
        </View>
      </View>

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
        {/* Recent Searches */}
        {!query && recentSearches.length > 0 && (
          <View style={styles.section}>
            <View style={styles.sectionHeader}>
              <Text style={[styles.sectionTitle, { color: colors.foreground }]}>Recent Searches</Text>
              <TouchableOpacity onPress={clearRecentSearches}>
                <Text style={{ color: colors.primary, fontSize: 13, fontWeight: '600' }}>Clear</Text>
              </TouchableOpacity>
            </View>
            {recentSearches.map((term, i) => (
              <TouchableOpacity key={i} style={styles.recentItem} onPress={() => setQuery(term)}>
                <Clock size={16} color={colors.mutedForeground} />
                <Text style={[styles.recentText, { color: colors.foreground }]}>{term}</Text>
              </TouchableOpacity>
            ))}
          </View>
        )}

        {/* Search Results */}
        {query && results.length > 0 && (
          <View style={styles.section}>
            <Text style={[styles.sectionTitle, { color: colors.foreground }]}>{results.length} results for "{query}"</Text>
            {results.map((course, i) => (
              <MotiView key={course.id} from={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 40 }}>
                <TouchableOpacity
                  style={[styles.resultCard, { backgroundColor: colors.card, borderColor: colors.border }]}
                  onPress={() => navigate('course-detail', { courseId: course.id })}
                  activeOpacity={0.7}
                >
                  <View style={[styles.resultIcon, { backgroundColor: `${colors.primary}15` }]}>
                    <BookOpen size={20} color={colors.primary} />
                  </View>
                  <View style={{ flex: 1 }}>
                    <Text style={[styles.resultTitle, { color: colors.foreground }]}>{course.title}</Text>
                    <Text style={[styles.resultMeta, { color: colors.mutedForeground }]}>{course.instructorName} · {course.level}</Text>
                  </View>
                </TouchableOpacity>
              </MotiView>
            ))}
          </View>
        )}

        {/* No results */}
        {query && !searching && results.length === 0 && (
          <View style={styles.noResults}>
            <Search size={48} color={colors.mutedForeground} />
            <Text style={[styles.noResultsTitle, { color: colors.foreground }]}>No results found</Text>
            <Text style={[styles.noResultsText, { color: colors.mutedForeground }]}>Try different keywords</Text>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  searchContainer: { paddingHorizontal: 16, paddingVertical: 8 },
  searchBar: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingHorizontal: 16, height: 52, borderRadius: 16, borderWidth: 2 },
  searchInput: { flex: 1, fontSize: 16, paddingVertical: 0 },
  section: { paddingHorizontal: 16, marginBottom: 16 },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  sectionTitle: { fontSize: 16, fontWeight: '700' },
  recentItem: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: 'rgba(148,163,184,0.1)' },
  recentText: { fontSize: 14 },
  resultCard: { flexDirection: 'row', alignItems: 'center', gap: 12, padding: 14, borderRadius: 12, borderWidth: 1, marginBottom: 8 },
  resultIcon: { width: 44, height: 44, borderRadius: 12, alignItems: 'center', justifyContent: 'center' },
  resultTitle: { fontSize: 15, fontWeight: '600', marginBottom: 2 },
  resultMeta: { fontSize: 12 },
  noResults: { alignItems: 'center', justifyContent: 'center', paddingVertical: 64 },
  noResultsTitle: { fontSize: 18, fontWeight: '700', marginTop: 16 },
  noResultsText: { fontSize: 14, marginTop: 4 },
});
