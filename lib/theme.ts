// ── DAKKHO Theme — exact match from web app CSS variables ──

// React Navigation DefaultTheme colors (inlined to avoid extra dep)
const RN_DEFAULT_COLORS = {
  primary: 'rgb(0, 122, 255)',
  background: 'rgb(242, 242, 242)',
  card: 'rgb(255, 255, 255)',
  text: 'rgb(28, 28, 30)',
  border: 'rgb(216, 216, 216)',
  notification: 'rgb(255, 59, 48)',
};

export const LightColors = {
  background: '#f0f9ff',
  foreground: '#0f172a',
  card: 'rgba(255, 255, 255, 0.7)',
  cardForeground: '#0f172a',
  primary: '#0ea5e9',
  primaryForeground: '#ffffff',
  secondary: '#f1f5f9',
  secondaryForeground: '#0f172a',
  muted: '#f1f5f9',
  mutedForeground: '#64748b',
  accent: '#10b981',
  accentForeground: '#ffffff',
  destructive: '#ef4444',
  border: 'rgba(255, 255, 255, 0.5)',
  input: '#e2e8f0',
  ring: '#0ea5e9',
  sky: '#0ea5e9',
  skyDeep: '#2563eb',
  emeraldAccent: '#10b981',
  amberAccent: '#f59e0b',
  danger: '#ef4444',
  // Dark mode navy
  dakkhoNavy: '#0c1222',
  dakkhoDark: '#0f172a',
};

export const DarkColors = {
  background: '#0c1222',
  foreground: '#f0f9ff',
  card: 'rgba(15, 23, 42, 0.7)',
  cardForeground: '#f0f9ff',
  primary: '#0ea5e9',
  primaryForeground: '#ffffff',
  secondary: '#1e293b',
  secondaryForeground: '#f0f9ff',
  muted: '#1e293b',
  mutedForeground: '#94a3b8',
  accent: '#10b981',
  accentForeground: '#ffffff',
  destructive: '#ef4444',
  border: 'rgba(255, 255, 255, 0.1)',
  input: '#1e293b',
  ring: '#0ea5e9',
  sky: '#0ea5e9',
  skyDeep: '#2563eb',
  emeraldAccent: '#10b981',
  amberAccent: '#f59e0b',
  danger: '#ef4444',
  dakkhoNavy: '#0c1222',
  dakkhoDark: '#0f172a',
};

export type DakkhoColors = typeof LightColors;

// Gradient configs — same as web
export const Gradients = {
  primary: ['#0ea5e9', '#2563eb'] as const,
  sky: ['#0ea5e9', '#2563eb'] as const,
  emerald: ['#10b981', '#059669'] as const,
  amber: ['#f59e0b', '#d97706'] as const,
  danger: ['#ef4444', '#dc2626'] as const,
  purple: ['#8b5cf6', '#7c3aed'] as const,
  rose: ['#f43f5e', '#e11d48'] as const,
  hero: ['#0c1222', '#1e293b'] as const,
};

// App constants — same as web
export const APP_NAME = 'DAKKHO';
export const APP_DESCRIPTION = "Bangladesh's Premier Polytechnic Student Streaming Platform";
export const API_BASE = 'https://dakkho-admin-api.dakkho-admin.workers.dev';
export const OTP_LENGTH = 6;
export const OTP_RESEND_COOLDOWN = 60;
export const TOPBAR_HEIGHT = 64;
export const BOTTOM_NAV_HEIGHT = 64;
export const SIDEBAR_WIDTH = 260;

// Department → technology mapping (same as web)
export const DEPT_TO_TECHNOLOGY: Record<string, string> = {
  'dept-cse': 'CST',
  'dept-eee': 'ELECTRICAL',
  'dept-me': 'MECH',
  'dept-ce': 'CIVIL',
  'dept-ete': 'ELEX',
  'dept-power': 'POWER',
  'dept-architecture': 'CIVIL',
  'dept-textile': 'CIVIL',
  'dept-chemical': 'CIVIL',
  'dept-automobile': 'MECH',
  'dept-rac': 'MECH',
  'dept-glass-ceramic': 'CIVIL',
  'dept-printing': 'CST',
  'dept-surveying': 'CIVIL',
  'dept-mechatronics': 'ELEX',
  'dept-mining': 'CIVIL',
  'dept-metallurgical': 'MECH',
  'dept-instrumentation': 'ELEX',
  'dept-food': 'CIVIL',
  'dept-leather': 'CIVIL',
};

export const TECHNOLOGY_SHORT_NAMES: Record<string, string> = {
  CIVIL: 'Civil Technology',
  CST: 'Computer Science & Technology',
  ELECTRICAL: 'Electrical Technology',
  EMED: 'Electromedical Technology',
  ELEX: 'Electronics Technology',
  MECH: 'Mechanical Technology',
  POWER: 'Power Technology',
};

// Nav theme for React Navigation
export const LightNavTheme = {
  dark: false,
  colors: {
    ...RN_DEFAULT_COLORS,
    primary: '#0ea5e9',
    background: '#f0f9ff',
    card: 'rgba(255, 255, 255, 0.7)',
    text: '#0f172a',
    border: 'rgba(255, 255, 255, 0.5)',
    notification: '#ef4444',
  },
};

export const DarkNavTheme = {
  dark: true,
  colors: {
    ...RN_DEFAULT_COLORS,
    primary: '#0ea5e9',
    background: '#0c1222',
    card: 'rgba(15, 23, 42, 0.7)',
    text: '#f0f9ff',
    border: 'rgba(255, 255, 255, 0.1)',
    notification: '#ef4444',
  },
};
