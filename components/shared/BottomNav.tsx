import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { Home, Compass, BookOpen, Clock, User } from 'lucide-react-native';
import { useNavigationStore, useServerConfigStore } from '@/lib/store';
import type { Page } from '@/lib/store';
import { LightColors } from '@/lib/theme';

interface NavTab {
  icon: React.ElementType;
  label: string;
  page: Page;
}

const tabs: NavTab[] = [
  { icon: Home, label: 'Home', page: 'home' },
  { icon: Compass, label: 'Explore', page: 'explore' },
  { icon: BookOpen, label: 'Courses', page: 'my-courses' },
  { icon: Clock, label: 'History', page: 'watch-history' },
  { icon: User, label: 'Profile', page: 'profile' },
];

export function BottomNav() {
  const { currentPage, navigate } = useNavigationStore();
  const isBottomNavTabVisible = useServerConfigStore((s) => s.isBottomNavTabVisible);
  const colors = LightColors;

  const visibleTabs = tabs.filter((tab) => isBottomNavTabVisible(tab.page));

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: `${colors.background}DD` }]} edges={['bottom']}>
      <MotiView
        from={{ opacity: 0, translateY: 64 }}
        animate={{ opacity: 1, translateY: 0 }}
        transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        style={[styles.container, { borderTopColor: colors.border }]}
      >
        {visibleTabs.map((tab) => {
          const isActive = currentPage === tab.page;
          const Icon = tab.icon;
          return (
            <TouchableOpacity
              key={tab.page}
              style={styles.tabBtn}
              onPress={() => navigate(tab.page)}
              activeOpacity={0.7}
            >
              <MotiView
                animate={{ translateY: isActive ? -2 : 0 }}
                transition={{ type: 'spring', stiffness: 400, damping: 20 }}
              >
                <Icon
                  size={22}
                  color={isActive ? colors.primary : colors.mutedForeground}
                  fill={isActive ? colors.primary : 'none'}
                />
              </MotiView>
              {isActive && (
                <MotiView
                  from={{ opacity: 0, translateY: 4 }}
                  animate={{ opacity: 1, translateY: 0 }}
                  exit={{ opacity: 0, translateY: 4 }}
                  transition={{ duration: 200 }}
                >
                  <Text style={[styles.tabLabel, { color: colors.primary }]}>{tab.label}</Text>
                </MotiView>
              )}
              {isActive && (
                <MotiView
                  from={{ scaleX: 0 }}
                  animate={{ scaleX: 1 }}
                  transition={{ type: 'spring', stiffness: 400, damping: 30 }}
                  style={styles.indicator}
                />
              )}
            </TouchableOpacity>
          );
        })}
      </MotiView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { zIndex: 40 },
  container: {
    height: 64,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
    borderTopWidth: 1,
  },
  tabBtn: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 2,
    height: '100%',
  },
  tabLabel: {
    fontSize: 10,
    fontWeight: '700',
  },
  indicator: {
    position: 'absolute',
    bottom: 0,
    width: 24,
    height: 4,
    borderRadius: 2,
    backgroundColor: '#0ea5e9',
  },
});
