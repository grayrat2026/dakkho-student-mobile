import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, StatusBar, Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { Search, Bell, Menu, X } from 'lucide-react-native';
import { useNavigationStore, useNotificationStore, useSearchStore, useAuthStore, useServerConfigStore } from '@/lib/store';
import { LightColors, DarkColors } from '@/lib/theme';

export function TopBar() {
  const navigate = useNavigationStore((s) => s.navigate);
  const user = useAuthStore((s) => s.user);
  const toggleSidebar = useNavigationStore((s) => s.toggleSidebar);
  const storeSearchQuery = useSearchStore((s) => s.query);
  const storeSetQuery = useSearchStore((s) => s.setQuery);
  const unreadCount = useNotificationStore((s) => s.notifications.filter(n => !n.isRead).length);
  const isTopBarElementVisible = useServerConfigStore((s) => s.isTopBarElementVisible);
  const [searchFocused, setSearchFocused] = React.useState(false);

  // Default to light — in production use useColorScheme()
  const colors = LightColors;

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]} edges={['top']}>
      <MotiView
        from={{ opacity: 0, translateY: -64 }}
        animate={{ opacity: 1, translateY: 0 }}
        transition={{ type: 'spring', stiffness: 300, damping: 30 }}
        style={[styles.container, { backgroundColor: `${colors.background}CC`, borderBottomColor: colors.border }]}
      >
        {/* LEFT: Logo */}
        <TouchableOpacity onPress={() => navigate('home')} style={styles.logoBtn}>
          <View style={[styles.logoIcon, { backgroundColor: colors.primary }]}>
            <Text style={styles.logoText}>D</Text>
          </View>
        </TouchableOpacity>

        {/* CENTER: Search bar */}
        {isTopBarElementVisible('search') && (
          <TouchableOpacity
            style={[styles.searchBar, { backgroundColor: colors.muted, borderColor: searchFocused ? colors.primary : 'transparent' }]}
            onPress={() => navigate('search')}
            activeOpacity={0.7}
          >
            <Search size={16} color={colors.mutedForeground} />
            <Text style={[styles.searchPlaceholder, { color: colors.mutedForeground }]}>
              Search courses, instructors...
            </Text>
          </TouchableOpacity>
        )}

        {/* RIGHT: Notification + Avatar + Menu */}
        <View style={styles.rightActions}>
          {isTopBarElementVisible('notifications') && (
            <TouchableOpacity
              style={[styles.iconBtn, { backgroundColor: colors.muted }]}
              onPress={() => navigate('notifications')}
            >
              <Bell size={20} color={colors.mutedForeground} />
              {unreadCount > 0 && (
                <View style={styles.badge}>
                  <MotiView
                    from={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: 'spring', stiffness: 500 }}
                    style={styles.badgeDot}
                  />
                </View>
              )}
            </TouchableOpacity>
          )}

          {isTopBarElementVisible('avatar') && (
            <TouchableOpacity
              style={[styles.avatarBtn, { backgroundColor: colors.primary }]}
              onPress={() => navigate('profile')}
            >
              <Text style={styles.avatarText}>
                {user?.fullName?.charAt(0) || 'U'}
              </Text>
            </TouchableOpacity>
          )}

          {isTopBarElementVisible('hamburger') && (
            <TouchableOpacity
              style={[styles.iconBtn, { backgroundColor: colors.muted }]}
              onPress={toggleSidebar}
            >
              <Menu size={20} color={colors.mutedForeground} />
            </TouchableOpacity>
          )}
        </View>
      </MotiView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { zIndex: 50 },
  container: {
    height: 64,
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    gap: 12,
    borderBottomWidth: 1,
  },
  logoBtn: { padding: 4 },
  logoIcon: {
    width: 32, height: 32, borderRadius: 8,
    alignItems: 'center', justifyContent: 'center',
  },
  logoText: { color: '#fff', fontSize: 16, fontWeight: '800' },
  searchBar: {
    flex: 1, maxWidth: 400, height: 40, borderRadius: 12,
    flexDirection: 'row', alignItems: 'center', paddingHorizontal: 12, gap: 8,
    borderWidth: 2,
  },
  searchPlaceholder: { fontSize: 14 },
  rightActions: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  iconBtn: {
    width: 40, height: 40, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
  },
  avatarBtn: {
    width: 40, height: 40, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
  },
  avatarText: { color: '#fff', fontSize: 14, fontWeight: '700' },
  badge: { position: 'absolute', top: 8, right: 8 },
  badgeDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: '#ef4444' },
});
