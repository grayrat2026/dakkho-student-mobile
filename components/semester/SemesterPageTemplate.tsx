import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { BookOpen, FileText, Clock, ChevronRight } from 'lucide-react-native';
import { LightColors } from '@/lib/theme';
import { SectionHeader, ProgressBar } from '../shared';

interface SemesterPageTemplateProps {
  semester: number;
  departmentName: string;
  subjects?: { name: string; code: string; credits: number }[];
}

const DEFAULT_SUBJECTS: Record<number, { name: string; code: string; credits: number }[]> = {
  1: [
    { name: 'Mathematics I', code: 'MATH-101', credits: 4 },
    { name: 'Physics I', code: 'PHY-101', credits: 3 },
    { name: 'English', code: 'ENG-101', credits: 2 },
    { name: 'Computer Fundamentals', code: 'CSE-101', credits: 3 },
    { name: 'Engineering Drawing', code: 'ED-101', credits: 2 },
  ],
  2: [
    { name: 'Mathematics II', code: 'MATH-201', credits: 4 },
    { name: 'Physics II', code: 'PHY-201', credits: 3 },
    { name: 'Programming in C', code: 'CSE-201', credits: 3 },
    { name: 'Electrical Circuits', code: 'EEE-201', credits: 3 },
    { name: 'Workshop Practice', code: 'WS-201', credits: 2 },
  ],
};

export function SemesterPageTemplate({ semester, departmentName, subjects }: SemesterPageTemplateProps) {
  const colors = LightColors;
  const subjectList = subjects || DEFAULT_SUBJECTS[semester] || DEFAULT_SUBJECTS[1];

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
        <View style={styles.header}>
          <Text style={styles.semLabel}>SEMESTER</Text>
          <Text style={styles.semNumber}>{semester}</Text>
          <Text style={styles.deptName}>{departmentName}</Text>
        </View>

        <View style={{ paddingHorizontal: 16 }}>
          <SectionHeader title="Subjects" />
          {subjectList.map((subject, i) => (
            <MotiView key={subject.code} from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 50 }}>
              <View style={[styles.subjectCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                <View style={[styles.subjectIcon, { backgroundColor: `${colors.primary}15` }]}>
                  <BookOpen size={18} color={colors.primary} />
                </View>
                <View style={{ flex: 1 }}>
                  <Text style={[styles.subjectName, { color: colors.foreground }]}>{subject.name}</Text>
                  <View style={{ flexDirection: 'row', gap: 12 }}>
                    <Text style={[styles.subjectCode, { color: colors.mutedForeground }]}>{subject.code}</Text>
                    <Text style={[styles.subjectCredits, { color: colors.primary }]}>{subject.credits} credits</Text>
                  </View>
                </View>
                <ChevronRight size={18} color={colors.mutedForeground} />
              </View>
            </MotiView>
          ))}

          <SectionHeader title="Progress" />
          <View style={[styles.progressCard, { backgroundColor: colors.card }]}>
            <ProgressBar progress={35} height={8} />
            <Text style={[styles.progressText, { color: colors.mutedForeground }]}>35% completed</Text>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  header: { alignItems: 'center', paddingVertical: 24 },
  semLabel: { fontSize: 12, fontWeight: '700', color: '#94a3b8', letterSpacing: 2 },
  semNumber: { fontSize: 64, fontWeight: '800', color: '#0ea5e9' },
  deptName: { fontSize: 16, fontWeight: '600', color: '#0f172a' },
  subjectCard: { flexDirection: 'row', alignItems: 'center', gap: 12, padding: 14, borderRadius: 12, borderWidth: 1, marginBottom: 8 },
  subjectIcon: { width: 40, height: 40, borderRadius: 12, alignItems: 'center', justifyContent: 'center' },
  subjectName: { fontSize: 15, fontWeight: '600', marginBottom: 2 },
  subjectCode: { fontSize: 12 },
  subjectCredits: { fontSize: 12, fontWeight: '600' },
  progressCard: { borderRadius: 12, padding: 16, gap: 8 },
  progressText: { fontSize: 13 },
});
