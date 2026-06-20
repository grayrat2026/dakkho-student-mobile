import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, FlatList } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MotiView } from 'moti';
import { Bell, CheckCheck, Info, AlertTriangle, BookOpen, Settings } from 'lucide-react-native';
import { useNavigationStore, useNotificationStore, type AppNotification } from '@/lib/store';
import { LightColors } from '@/lib/theme';

const NOTIF_ICONS: Record<string, React.ElementType> = {
  info: Info,
  success: CheckCheck,
  warning: AlertTriangle,
  error: AlertTriangle,
  course: BookOpen,
  system: Settings,
};

const NOTIF_COLORS: Record<string, string> = {
  info: '#0ea5e9', success: '#10b981', warning: '#f59e0b', error: '#ef4444',
  course: '#8b5cf6', system: '#64748b',
};

function timeAgo(dateStr: string): string {
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
}

export function NotificationsPage() {
  const { notifications, markAsRead, markAllAsRead } = useNotificationStore();
  const navigate = useNavigationStore((s) => s.navigate);
  const colors = LightColors;
  const unread = notifications.filter(n => !n.isRead);

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={[styles.title, { color: colors.foreground }]}>Notifications</Text>
        {unread.length > 0 && (
          <TouchableOpacity onPress={markAllAsRead} style={styles.markAllBtn}>
            <CheckCheck size={16} color={colors.primary} />
            <Text style={{ color: colors.primary, fontSize: 13, fontWeight: '600' }}>Mark all read</Text>
          </TouchableOpacity>
        )}
      </View>

      {notifications.length > 0 ? (
        <FlatList
          data={notifications}
          keyExtractor={(item) => item.id}
          contentContainerStyle={{ paddingBottom: 100 }}
          renderItem={({ item, index }) => {
            const Icon = NOTIF_ICONS[item.type] || Bell;
            const notifColor = NOTIF_COLORS[item.type] || colors.primary;
            return (
              <MotiView from={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: index * 40 }}>
                <TouchableOpacity
                  style={[styles.notifCard, { backgroundColor: item.isRead ? colors.muted : colors.card, borderLeftColor: notifColor }]}
                  onPress={() => { markAsRead(item.id); if (item.actionUrl) navigate('home'); }}
                  activeOpacity={0.7}
                >
                  <View style={[styles.notifIcon, { backgroundColor: `${notifColor}15` }]}>
                    <Icon size={18} color={notifColor} />
                  </View>
                  <View style={{ flex: 1 }}>
                    <Text style={[styles.notifTitle, { color: colors.foreground }]}>{item.title}</Text>
                    <Text style={[styles.notifMessage, { color: colors.mutedForeground }]} numberOfLines={2}>{item.message}</Text>
                    <Text style={[styles.notifTime, { color: colors.mutedForeground }]}>{timeAgo(item.createdAt)}</Text>
                  </View>
                  {!item.isRead && <View style={[styles.unreadDot, { backgroundColor: notifColor }]} />}
                </TouchableOpacity>
              </MotiView>
            );
          }}
        />
      ) : (
        <View style={styles.emptyState}>
          <Bell size={48} color={colors.mutedForeground} />
          <Text style={[styles.emptyTitle, { color: colors.foreground }]}>No notifications</Text>
          <Text style={[styles.emptyText, { color: colors.mutedForeground }]}>You're all caught up!</Text>
        </View>
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 16, paddingVertical: 12 },
  title: { fontSize: 24, fontWeight: '800' },
  markAllBtn: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  notifCard: { flexDirection: 'row', alignItems: 'flex-start', gap: 12, paddingHorizontal: 16, paddingVertical: 14, borderLeftWidth: 3 },
  notifIcon: { width: 36, height: 36, borderRadius: 10, alignItems: 'center', justifyContent: 'center' },
  notifTitle: { fontSize: 14, fontWeight: '600', marginBottom: 2 },
  notifMessage: { fontSize: 13, lineHeight: 18 },
  notifTime: { fontSize: 11, marginTop: 4 },
  unreadDot: { width: 8, height: 8, borderRadius: 4, marginTop: 4 },
  emptyState: { flex: 1, alignItems: 'center', justifyContent: 'center', paddingHorizontal: 24 },
  emptyTitle: { fontSize: 18, fontWeight: '700', marginTop: 16 },
  emptyText: { fontSize: 14, marginTop: 4 },
});
