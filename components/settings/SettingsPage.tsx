import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { User, Bell, Shield, Globe, Moon, Download, Video, Wifi, Lock, Monitor, HelpCircle, ChevronRight } from 'lucide-react-native';
import { useNavigationStore } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const SECTIONS = [
  { title: 'Account', items: [
    { icon: User, label: 'Account Settings', page: 'settings-account' },
    { icon: Bell, label: 'Notifications', page: 'settings-notifications' },
    { icon: Shield, label: 'Privacy', page: 'settings-privacy' },
  ]},
  { title: 'Preferences', items: [
    { icon: Globe, label: 'Language', page: 'settings-language' },
    { icon: Moon, label: 'Theme', page: 'settings-theme' },
  ]},
  { title: 'Content & Data', items: [
    { icon: Download, label: 'Downloads', page: 'settings-downloads' },
    { icon: Video, label: 'Video Quality', page: 'settings-video-quality' },
    { icon: Wifi, label: 'Network & Data', page: 'settings-network-data' },
    { icon: Lock, label: 'Content Protection', page: 'settings-content-protection' },
    { icon: Monitor, label: 'Active Sessions', page: 'settings-sessions' },
  ]},
  { title: 'Support', items: [
    { icon: HelpCircle, label: 'Help & Support', page: 'help' },
  ]},
];

export function SettingsPage() {
  const navigate = useNavigationStore((s) => s.navigate);
  const colors = LightColors;

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ paddingBottom: 100 }}>
        <Text style={[styles.pageTitle, { color: colors.foreground }]}>Settings</Text>

        {SECTIONS.map(section => (
          <View key={section.title} style={styles.section}>
            <Text style={[styles.sectionTitle, { color: colors.mutedForeground }]}>{section.title}</Text>
            <View style={[styles.sectionCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
              {section.items.map((item, i) => {
                const Icon = item.icon;
                return (
                  <TouchableOpacity
                    key={item.label}
                    style={[styles.item, i < section.items.length - 1 && { borderBottomWidth: 1, borderBottomColor: colors.border }]}
                    onPress={() => navigate(item.page as any)}
                    activeOpacity={0.7}
                  >
                    <View style={[styles.iconCircle, { backgroundColor: `${colors.primary}15` }]}>
                      <Icon size={18} color={colors.primary} />
                    </View>
                    <Text style={[styles.itemLabel, { color: colors.foreground }]}>{item.label}</Text>
                    <ChevronRight size={18} color={colors.mutedForeground} />
                  </TouchableOpacity>
                );
              })}
            </View>
          </View>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  pageTitle: { fontSize: 24, fontWeight: '800', paddingHorizontal: 16, paddingTop: 16, paddingBottom: 8 },
  section: { marginBottom: 16, paddingHorizontal: 16 },
  sectionTitle: { fontSize: 11, fontWeight: '700', letterSpacing: 1, marginBottom: 8, paddingHorizontal: 4 },
  sectionCard: { borderRadius: 16, borderWidth: 1, overflow: 'hidden' },
  item: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingHorizontal: 16, paddingVertical: 14 },
  iconCircle: { width: 36, height: 36, borderRadius: 10, alignItems: 'center', justifyContent: 'center' },
  itemLabel: { flex: 1, fontSize: 15, fontWeight: '500' },
});
