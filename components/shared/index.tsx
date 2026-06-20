import React from 'react';
import { View, Text, StyleSheet, type ViewStyle, type TextStyle } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { MotiView } from 'moti';
import { useThemeStore } from '@/lib/store';
import { LightColors, DarkColors, type DakkhoColors } from './theme';

// ── Theme Hook ──
export function useDakkhoTheme() {
  const themeMode = useThemeStore((s) => s.themeMode);
  // Simple detection — in real app use useColorScheme()
  const isDark = themeMode === 'dark';
  return {
    colors: isDark ? DarkColors : LightColors,
    isDark,
  };
}

// ── GlassCard — same as web's GlassCard ──
interface GlassCardProps {
  children: React.ReactNode;
  style?: ViewStyle;
  hover?: boolean;
  onPress?: () => void;
  className?: string;
}

export function GlassCard({ children, style, onPress }: GlassCardProps) {
  const { colors, isDark } = useDakkhoTheme();

  return (
    <MotiView
      from={{ opacity: 0, translateY: 8 }}
      animate={{ opacity: 1, translateY: 0 }}
      transition={{ type: 'timing', duration: 400 }}
      style={[
        styles.glassCard,
        {
          backgroundColor: colors.card,
          borderColor: isDark ? 'rgba(255,255,255,0.1)' : 'rgba(255,255,255,0.5)',
          shadowColor: isDark ? '#0ea5e9' : '#0ea5e9',
        },
        style,
      ]}
    >
      {onPress ? (
        <Text onPress={onPress} style={{ color: 'transparent' }}>
          {children}
        </Text>
      ) : (
        children
      )}
    </MotiView>
  );
}

const styles = StyleSheet.create({
  glassCard: {
    borderRadius: 16,
    borderWidth: 1,
    padding: 16,
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.1,
    shadowRadius: 15,
    elevation: 5,
  },
});

// ── GradientButton — same as web's GradientButton ──
interface GradientButtonProps {
  children: React.ReactNode;
  onPress?: () => void;
  loading?: boolean;
  variant?: 'primary' | 'secondary' | 'outline' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  style?: ViewStyle;
  textStyle?: TextStyle;
  colors?: readonly [string, string];
  disabled?: boolean;
}

export function GradientButton({
  children,
  onPress,
  loading = false,
  variant = 'primary',
  size = 'md',
  style,
  textStyle,
  colors: customColors,
  disabled = false,
}: GradientButtonProps) {
  const gradientColors = customColors ||
    (variant === 'danger'
      ? ['#ef4444', '#dc2626'] as const
      : ['#0ea5e9', '#2563eb'] as const);

  const sizeStyles = {
    sm: { paddingVertical: 8, paddingHorizontal: 16, fontSize: 12 },
    md: { paddingVertical: 12, paddingHorizontal: 24, fontSize: 14 },
    lg: { paddingVertical: 16, paddingHorizontal: 32, fontSize: 16 },
  }[size];

  if (variant === 'outline') {
    return (
      <MotiView
        from={{ scale: 1 }}
        animate={{ scale: 1 }}
        whileHover={{ scale: 1.02 }}
      >
        <View
          style={[
            {
              paddingVertical: sizeStyles.paddingVertical,
              paddingHorizontal: sizeStyles.paddingHorizontal,
              borderRadius: 12,
              borderWidth: 2,
              borderColor: '#0ea5e9',
              alignItems: 'center',
              justifyContent: 'center',
              flexDirection: 'row',
              gap: 8,
            },
            disabled && { opacity: 0.5 },
            style,
          ]}
        >
          <Text
            style={[
              { fontSize: sizeStyles.fontSize, fontWeight: '700', color: '#0ea5e9' },
              textStyle,
            ]}
            onPress={disabled ? undefined : onPress}
          >
            {loading ? 'Loading...' : children}
          </Text>
        </View>
      </MotiView>
    );
  }

  return (
    <MotiView
      from={{ scale: 1 }}
      animate={{ scale: 1 }}
      whileHover={{ scale: 1.02 }}
    >
      <LinearGradient
        colors={gradientColors as [string, string]}
        start={{ x: 0, y: 0 }}
        end={{ x: 1, y: 1 }}
        style={[
          {
            paddingVertical: sizeStyles.paddingVertical,
            paddingHorizontal: sizeStyles.paddingHorizontal,
            borderRadius: 12,
            alignItems: 'center',
            justifyContent: 'center',
            flexDirection: 'row',
            gap: 8,
            shadowColor: gradientColors[0],
            shadowOffset: { width: 0, height: 4 },
            shadowOpacity: 0.3,
            shadowRadius: 8,
            elevation: 4,
          },
          disabled && { opacity: 0.5 },
          style,
        ]}
      >
        <Text
          style={[
            { fontSize: sizeStyles.fontSize, fontWeight: '700', color: '#ffffff' },
            textStyle,
          ]}
          onPress={disabled || loading ? undefined : onPress}
        >
          {loading ? 'Loading...' : children}
        </Text>
      </LinearGradient>
    </MotiView>
  );
}

// ── LoadingSkeleton ──
export function LoadingSkeleton({ width, height, style }: { width: number; height: number; style?: ViewStyle }) {
  return (
    <MotiView
      from={{ opacity: 0.3 }}
      animate={{ opacity: 0.7 }}
      transition={{ type: 'timing', duration: 800, loop: true }}
      style={[
        {
          width,
          height,
          borderRadius: 8,
          backgroundColor: '#e2e8f0',
        },
        style,
      ]}
    />
  );
}

// ── EmptyState ──
export function EmptyState({ icon, title, description, actionLabel, onAction }: {
  icon: React.ReactNode;
  title: string;
  description: string;
  actionLabel?: string;
  onAction?: () => void;
}) {
  const { colors } = useDakkhoTheme();

  return (
    <View style={{ alignItems: 'center', justifyContent: 'center', paddingVertical: 48, paddingHorizontal: 24 }}>
      <View style={{ marginBottom: 16 }}>{icon}</View>
      <Text style={{ fontSize: 18, fontWeight: '700', color: colors.foreground, marginBottom: 8, textAlign: 'center' }}>
        {title}
      </Text>
      <Text style={{ fontSize: 14, color: colors.mutedForeground, textAlign: 'center', lineHeight: 20 }}>
        {description}
      </Text>
      {actionLabel && onAction && (
        <GradientButton onPress={onAction} style={{ marginTop: 16 }} size="sm">
          {actionLabel}
        </GradientButton>
      )}
    </View>
  );
}

// ── ProgressBar ──
export function ProgressBar({ progress, height = 6, color }: { progress: number; height?: number; color?: string }) {
  const { colors } = useDakkhoTheme();

  return (
    <View style={{ height, borderRadius: height / 2, backgroundColor: colors.muted, overflow: 'hidden' }}>
      <MotiView
        from={{ width: '0%' }}
        animate={{ width: `${Math.min(progress, 100)}%` }}
        transition={{ type: 'timing', duration: 1000 }}
        style={{
          height: '100%',
          borderRadius: height / 2,
          backgroundColor: color || colors.primary,
        }}
      />
    </View>
  );
}

// ── AnimatedCounter ──
export function AnimatedCounter({ target, style }: { target: number; style?: TextStyle }) {
  const { colors } = useDakkhoTheme();

  return (
    <Text style={[{ fontSize: 14, fontWeight: '800', color: colors.primary }, style]}>
      {target.toLocaleString()}
    </Text>
  );
}

// ── SectionHeader ──
export function SectionHeader({ title, actionLabel, onAction }: {
  title: string;
  actionLabel?: string;
  onAction?: () => void;
}) {
  const { colors } = useDakkhoTheme();

  return (
    <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
      <Text style={{ fontSize: 18, fontWeight: '800', color: colors.foreground }}>{title}</Text>
      {actionLabel && onAction && (
        <Text onPress={onAction} style={{ fontSize: 13, fontWeight: '600', color: colors.primary }}>
          {actionLabel}
        </Text>
      )}
    </View>
  );
}

// ── SensitiveActionPrompt ──
export function SensitiveActionPrompt({ visible, title, message, onConfirm, onCancel }: {
  visible: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}) {
  if (!visible) return null;

  return (
    <View style={{ position: 'absolute', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', alignItems: 'center', justifyContent: 'center', zIndex: 100 }}>
      <GlassCard style={{ width: '80%', maxWidth: 320 }}>
        <Text style={{ fontSize: 18, fontWeight: '700', marginBottom: 8, color: '#0f172a' }}>{title}</Text>
        <Text style={{ fontSize: 14, color: '#64748b', marginBottom: 16, lineHeight: 20 }}>{message}</Text>
        <View style={{ flexDirection: 'row', gap: 12 }}>
          <GradientButton variant="outline" onPress={onCancel} size="sm" style={{ flex: 1 }}>
            Cancel
          </GradientButton>
          <GradientButton variant="danger" onPress={onConfirm} size="sm" style={{ flex: 1 }}>
            Confirm
          </GradientButton>
        </View>
      </GlassCard>
    </View>
  );
}
