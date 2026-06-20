import React from 'react';
import { useLocalSearchParams } from 'expo-router';
import { SemesterPageTemplate } from '@/components/semester/SemesterPageTemplate';

export default function SemesterScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const semNum = parseInt(id || '1', 10);

  return (
    <SemesterPageTemplate
      semester={semNum}
      departmentName="Your Department"
    />
  );
}
