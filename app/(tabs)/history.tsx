import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { Clock, Play, CheckCircle } from 'lucide-react-native';
import { LightColors } from '@/lib/theme';
import { SectionHeader, ProgressBar, EmptyState } from '@/components/shared';

const WATCH_HISTORY = [
  { id: '1', title: 'Intro to Python - Ch.3', course: 'Computer Science 101', watchedAt: '2 hours ago', progress: 75, duration: '45:00' },
  { id: '2', title: 'Circuit Analysis - Ch.5', course: 'Electrical Engineering', watchedAt: '5 hours ago', progress: 40, duration: '1:20:00' },
  { id: '3', title: 'Thermodynamics Basics', course: 'Mechanical Engineering', watchedAt: 'Yesterday', progress: 100, duration: '35:00' },
  { id: '4', title: 'Data Structures - Trees', course: 'CS 201', watchedAt: '2 days ago', progress: 20, duration: '55:00' },
];

export default function HistoryScreen() {
  const colors = LightColors;

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 20 }}>
        <Text style={[styles.pageTitle, { color: colors.foreground }]}>Watch History</Text>

        {WATCH_HISTORY.length > 0 ? WATCH_HISTORY.map((item, i) => (
          <MotiView key={item.id} from={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 50 }}>
            <View style={[styles.historyCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
              <View style={[styles.thumbnailPlaceholder, { backgroundColor: `${colors.primary}20` }]}>
                {item.progress >= 100 ? <CheckCircle size={24} color="#10b981" /> : <Play size={24} color={colors.primary} fill={colors.primary} />}
              </View>
              <View style={styles.cardContent}>
                <Text style={[styles.cardTitle, { color: colors.foreground }]} numberOfLines={1}>{item.title}</Text>
                <Text style={[styles.cardCourse, { color: colors.mutedForeground }]} numberOfLines={1}>{item.course}</Text>
                <View style={styles.metaRow}>
                  <Text style={[styles.metaText, { color: colors.mutedForeground }]}>{item.watchedAt}</Text>
                  <Text style={[styles.metaText, { color: colors.mutedForeground }]}>{item.duration}</Text>
                </View>
                <ProgressBar progress={item.progress} height={4} />
              </View>
            </View>
          </MotiView>
        )) : (
          <EmptyState
            icon={<Clock size={48} color={colors.mutedForeground} />}
            title="No watch history"
            description="Start watching courses to see your history here"
          />
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  pageTitle: { fontSize: 24, fontWeight: '800', paddingHorizontal: 16, paddingTop: 16, paddingBottom: 8 },
  historyCard: { flexDirection: 'row', marginHorizontal: 16, marginBottom: 12, borderRadius: 16, borderWidth: 1, overflow: 'hidden', padding: 12, gap: 12 },
  thumbnailPlaceholder: { width: 56, height: 56, borderRadius: 12, alignItems: 'center', justifyContent: 'center' },
  cardContent: { flex: 1, gap: 2 },
  cardTitle: { fontSize: 14, fontWeight: '700' },
  cardCourse: { fontSize: 12 },
  metaRow: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 2, marginBottom: 4 },
  metaText: { fontSize: 10 },
});
