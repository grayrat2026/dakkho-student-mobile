import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { BookOpen, Users, GraduationCap, ChevronRight } from 'lucide-react-native';
import { LightColors } from '@/lib/theme';
import { SectionHeader } from '../shared';

interface DepartmentPageTemplateProps {
  name: string;
  shortCode: string;
  description: string;
  color: string;
  courseCount?: number;
  studentCount?: number;
  instructorCount?: number;
}

export function DepartmentPageTemplate({
  name, shortCode, description, color, courseCount = 24, studentCount = 1200, instructorCount = 8,
}: DepartmentPageTemplateProps) {
  const colors = LightColors;

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
        {/* Header */}
        <LinearGradient colors={[color, `${color}CC`]} style={styles.header}>
          <Text style={styles.shortCode}>{shortCode}</Text>
          <Text style={styles.deptName}>{name}</Text>
          <Text style={styles.deptDesc}>{description}</Text>
        </LinearGradient>

        {/* Stats */}
        <View style={styles.statsRow}>
          {[
            { icon: BookOpen, value: `${courseCount}`, label: 'Courses' },
            { icon: Users, value: `${studentCount}`, label: 'Students' },
            { icon: GraduationCap, value: `${instructorCount}`, label: 'Instructors' },
          ].map((stat, i) => (
            <View key={i} style={[styles.statCard, { backgroundColor: colors.card }]}>
              <stat.icon size={20} color={color} />
              <Text style={[styles.statValue, { color: colors.foreground }]}>{stat.value}</Text>
              <Text style={[styles.statLabel, { color: colors.mutedForeground }]}>{stat.label}</Text>
            </View>
          ))}
        </View>

        {/* Semesters */}
        <View style={{ paddingHorizontal: 16 }}>
          <SectionHeader title="Semesters" />
          {[1, 2, 3, 4, 5, 6, 7, 8].map((sem, i) => (
            <MotiView key={sem} from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 50 }}>
              <View style={[styles.semesterCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                <View style={[styles.semesterBadge, { backgroundColor: `${color}15` }]}>
                  <Text style={[styles.semesterBadgeText, { color }]}>{sem}</Text>
                </View>
                <View style={{ flex: 1 }}>
                  <Text style={[styles.semesterTitle, { color: colors.foreground }]}>Semester {sem}</Text>
                  <Text style={[styles.semesterInfo, { color: colors.mutedForeground }]}>{5 + sem} subjects</Text>
                </View>
                <ChevronRight size={18} color={colors.mutedForeground} />
              </View>
            </MotiView>
          ))}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  header: { padding: 24, paddingTop: 32, borderBottomLeftRadius: 32, borderBottomRightRadius: 32 },
  shortCode: { fontSize: 14, fontWeight: '700', color: 'rgba(255,255,255,0.7)', letterSpacing: 2, marginBottom: 4 },
  deptName: { fontSize: 26, fontWeight: '800', color: '#fff', marginBottom: 8 },
  deptDesc: { fontSize: 14, color: 'rgba(255,255,255,0.8)', lineHeight: 20 },
  statsRow: { flexDirection: 'row', gap: 12, paddingHorizontal: 16, marginTop: -24, marginBottom: 16 },
  statCard: { flex: 1, borderRadius: 16, padding: 14, alignItems: 'center', gap: 4, elevation: 3, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.08, shadowRadius: 4 },
  statValue: { fontSize: 20, fontWeight: '800' },
  statLabel: { fontSize: 11 },
  semesterCard: { flexDirection: 'row', alignItems: 'center', gap: 12, padding: 14, borderRadius: 12, borderWidth: 1, marginBottom: 8 },
  semesterBadge: { width: 40, height: 40, borderRadius: 12, alignItems: 'center', justifyContent: 'center' },
  semesterBadgeText: { fontSize: 18, fontWeight: '800' },
  semesterTitle: { fontSize: 15, fontWeight: '600' },
  semesterInfo: { fontSize: 12 },
});
