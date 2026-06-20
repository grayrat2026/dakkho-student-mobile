import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { LinearGradient } from 'expo-linear-gradient';
import { LightColors, DEPT_TO_TECHNOLOGY, TECHNOLOGY_SHORT_NAMES } from '@/lib/theme';
import { DepartmentPageTemplate } from '@/components/department/DepartmentPageTemplate';
import { useLocalSearchParams } from 'expo-router';

const DEPT_COLORS: Record<string, string> = {
  'cse': '#0ea5e9', 'ete': '#8b5cf6', 'eee': '#f59e0b', 'me': '#ef4444', 'ce': '#10b981',
  'architecture': '#f97316', 'textile': '#ec4899', 'chemical': '#14b8a6', 'automobile': '#6366f1',
  'rac': '#84cc16', 'glass-ceramic': '#06b6d4', 'printing': '#a855f7', 'surveying': '#eab308',
  'mechatronics': '#f43f5e', 'mining': '#78716c', 'metallurgical': '#fb923c', 'power': '#22d3ee',
  'instrumentation': '#c084fc', 'food': '#4ade80', 'leather': '#a3a3a3',
};

const DEPT_NAMES: Record<string, string> = {
  'cse': 'Computer Science & Technology', 'ete': 'Electronics & Telecommunication',
  'eee': 'Electrical Engineering', 'me': 'Mechanical Engineering', 'ce': 'Civil Engineering',
  'architecture': 'Architecture', 'textile': 'Textile Technology', 'chemical': 'Chemical Technology',
  'automobile': 'Automobile Technology', 'rac': 'Refrigeration & Air Conditioning',
  'glass-ceramic': 'Glass & Ceramic Technology', 'printing': 'Printing Technology',
  'surveying': 'Surveying Technology', 'mechatronics': 'Mechatronics Technology',
  'mining': 'Mining Technology', 'metallurgical': 'Metallurgical Technology',
  'power': 'Power Technology', 'instrumentation': 'Instrumentation Technology',
  'food': 'Food Technology', 'leather': 'Leather Technology',
};

export default function DepartmentScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const deptKey = `dept-${id}` as keyof typeof DEPT_TO_TECHNOLOGY;
  const techCode = DEPT_TO_TECHNOLOGY[deptKey] || 'CST';
  const color = DEPT_COLORS[id || 'cse'] || '#0ea5e9';
  const name = DEPT_NAMES[id || 'cse'] || (id || 'Unknown');

  return (
    <DepartmentPageTemplate
      name={name}
      shortCode={techCode}
      description={`Explore courses and resources for ${name} at DAKKHO Academy.`}
      color={color}
    />
  );
}
