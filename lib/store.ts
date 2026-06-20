import { create } from 'zustand';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as SecureStore from 'expo-secure-store';

// ── Page Types (111 pages — exact match from web) ──
export type Page =
  // Auth
  | 'login' | 'signup' | 'forgot-password'
  // Main
  | 'home' | 'explore' | 'search' | 'notifications' | 'notification-detail' | 'profile'
  | 'category' | 'about' | 'watch-history' | 'assignment'
  // Course
  | 'course-detail' | 'video-player'
  | 'course-curriculum' | 'course-reviews' | 'course-qa' | 'course-announcements'
  | 'course-resources' | 'course-notes' | 'course-quizzes' | 'course-progress'
  // Instructor
  | 'instructors' | 'instructor-profile'
  | 'instructor-courses' | 'instructor-reviews' | 'instructor-schedule' | 'instructor-contact'
  // User pages
  | 'my-courses' | 'bookmarks' | 'settings' | 'help'
  | 'downloads' | 'certificates' | 'live-sessions'
  | 'achievements' | 'discussion'
  // Department pages
  | 'dept-cse' | 'dept-ete' | 'dept-eee' | 'dept-me' | 'dept-ce'
  | 'dept-architecture' | 'dept-textile' | 'dept-chemical' | 'dept-automobile' | 'dept-rac'
  | 'dept-glass-ceramic' | 'dept-printing' | 'dept-surveying' | 'dept-mechatronics' | 'dept-mining'
  | 'dept-metallurgical' | 'dept-power' | 'dept-instrumentation' | 'dept-food' | 'dept-leather'
  // Semester pages
  | 'semester-1' | 'semester-2' | 'semester-3' | 'semester-4'
  | 'semester-5' | 'semester-6' | 'semester-7' | 'semester-8'
  // Profile sub-pages
  | 'edit-profile' | 'change-password' | 'learning-stats' | 'subscription' | 'referral' | 'delete-account' | 'verify-email'
  // Settings sub-pages
  | 'settings-account' | 'settings-notifications' | 'settings-privacy'
  | 'settings-language' | 'settings-theme' | 'settings-downloads' | 'settings-content-protection' | 'settings-sessions'
  | 'settings-video-quality' | 'settings-download-settings' | 'settings-network-data'
  // Help sub-pages
  | 'faq' | 'contact-support' | 'ticket-detail' | 'report-issue'
  | 'terms-of-service' | 'privacy-policy' | 'refund-policy'
  // Exam pages
  | 'exam-prep' | 'exam-schedule' | 'exam-results' | 'exam-practice' | 'exam-tips'
  // Social/Community pages
  | 'leaderboard' | 'study-groups' | 'peer-connections' | 'community' | 'feedback' | 'roadmap'
  // Misc pages
  | 'pricing' | 'changelog' | 'maintenance' | 'terms' | 'privacy'
  // Error pages
  | 'error-404' | 'error-500'
  // Payment status pages
  | 'payment-success' | 'payment-failed' | 'payment-cancel';

export interface PageParams {
  courseId?: string;
  videoId?: string;
  instructorId?: string;
  notificationId?: string;
  query?: string;
  categoryId?: string;
  departmentId?: string;
  semesterId?: string;
  ticketId?: string;
  [key: string]: any;
}

// ── User Type ──
export interface User {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  avatar?: string;
  role: 'student' | 'instructor' | 'admin';
  instituteId?: string;
  instituteName?: string;
  technologyId?: string;
  technologyName?: string;
  enrolledCourseIds?: string[];
  createdAt?: string;
}

// ── Navigation Store ──
interface NavigationState {
  currentPage: Page;
  pageParams: PageParams;
  previousPages: Page[];
  sidebarOpen: boolean;
  navigate: (page: Page, params?: PageParams) => void;
  goBack: () => void;
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
}

export const useNavigationStore = create<NavigationState>((set, get) => ({
  currentPage: 'home',
  pageParams: {},
  previousPages: [],
  sidebarOpen: false,

  navigate: (page, params = {}) => {
    const { currentPage, previousPages } = get();
    set({
      currentPage: page,
      pageParams: params,
      previousPages: [...previousPages.slice(-20), currentPage],
    });
  },

  goBack: () => {
    const { previousPages } = get();
    if (previousPages.length > 0) {
      const prev = previousPages[previousPages.length - 1];
      set({ currentPage: prev, previousPages: previousPages.slice(0, -1), pageParams: {} });
    }
  },

  toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),
  setSidebarOpen: (open) => set({ sidebarOpen: open }),
}));

// ── Auth Store ──
interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  needsVerification: boolean;
  isHydrated: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (data: SignupData) => Promise<void>;
  logout: () => Promise<void>;
  hydrateAuth: () => void;
  refreshUser: () => Promise<void>;
  setUser: (user: User | null) => void;
}

export interface SignupData {
  fullName: string;
  email: string;
  phone: string;
  password: string;
  instituteId?: string;
  technologyId?: string;
}

const AUTH_TOKEN_KEY = 'dakkho_student_token';
const PENDING_TOKEN_KEY = 'dakkho_pending_verification_token';
const USER_STORAGE_KEY = 'dakkho_user';

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  needsVerification: false,
  isHydrated: false,

  login: async (email, password) => {
    set({ isLoading: true });
    try {
      const token = await SecureStore.getItemAsync(AUTH_TOKEN_KEY);
      // API call would go here
      // For now, hydrate from storage
      const userJson = await AsyncStorage.getItem(USER_STORAGE_KEY);
      if (userJson) {
        const user = JSON.parse(userJson);
        set({ user, isAuthenticated: true, isLoading: false, needsVerification: false });
      } else {
        set({ isLoading: false });
      }
    } catch {
      set({ isLoading: false });
      throw new Error('Login failed');
    }
  },

  signup: async (data) => {
    set({ isLoading: true });
    try {
      // API call would go here
      set({ isLoading: false, needsVerification: true });
    } catch {
      set({ isLoading: false });
      throw new Error('Signup failed');
    }
  },

  logout: async () => {
    await SecureStore.deleteItemAsync(AUTH_TOKEN_KEY);
    await SecureStore.deleteItemAsync(PENDING_TOKEN_KEY);
    await AsyncStorage.removeItem(USER_STORAGE_KEY);
    set({ user: null, isAuthenticated: false, needsVerification: false });
  },

  hydrateAuth: () => {
    // AsyncStorage is async, so we do a fire-and-forget hydrate
    AsyncStorage.getItem(USER_STORAGE_KEY).then((userJson) => {
      if (userJson) {
        const user = JSON.parse(userJson);
        set({ user, isAuthenticated: true, isHydrated: true, needsVerification: false });
      } else {
        set({ isHydrated: true });
      }
    }).catch(() => {
      set({ isHydrated: true });
    });
  },

  refreshUser: async () => {
    // Would fetch from API — for now just keep current state
  },

  setUser: (user) => set({ user, isAuthenticated: !!user }),
}));

// ── Notification Store ──
export interface AppNotification {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error' | 'course' | 'system';
  isRead: boolean;
  createdAt: string;
  actionUrl?: string;
}

interface NotificationState {
  notifications: AppNotification[];
  addNotification: (notification: Omit<AppNotification, 'id' | 'isRead' | 'createdAt'>) => void;
  markAsRead: (id: string) => void;
  markAllAsRead: () => void;
  hydrateFromStorage: () => void;
}

const NOTIF_STORAGE_KEY = 'dakkho_notifications';

export const useNotificationStore = create<NotificationState>((set, get) => ({
  notifications: [],

  addNotification: (notif) => {
    const newNotif: AppNotification = {
      ...notif,
      id: Date.now().toString(),
      isRead: false,
      createdAt: new Date().toISOString(),
    };
    const updated = [newNotif, ...get().notifications].slice(0, 50);
    set({ notifications: updated });
    AsyncStorage.setItem(NOTIF_STORAGE_KEY, JSON.stringify(updated)).catch(() => {});
  },

  markAsRead: (id) => {
    const updated = get().notifications.map((n) =>
      n.id === id ? { ...n, isRead: true } : n
    );
    set({ notifications: updated });
    AsyncStorage.setItem(NOTIF_STORAGE_KEY, JSON.stringify(updated)).catch(() => {});
  },

  markAllAsRead: () => {
    const updated = get().notifications.map((n) => ({ ...n, isRead: true }));
    set({ notifications: updated });
    AsyncStorage.setItem(NOTIF_STORAGE_KEY, JSON.stringify(updated)).catch(() => {});
  },

  hydrateFromStorage: () => {
    AsyncStorage.getItem(NOTIF_STORAGE_KEY).then((data) => {
      if (data) {
        set({ notifications: JSON.parse(data) });
      }
    }).catch(() => {});
  },
}));

// ── Search Store ──
interface SearchState {
  query: string;
  recentSearches: string[];
  setQuery: (q: string) => void;
  addRecentSearch: (q: string) => void;
  clearRecentSearches: () => void;
}

const SEARCH_STORAGE_KEY = 'dakkho_recent_searches';

export const useSearchStore = create<SearchState>((set, get) => ({
  query: '',
  recentSearches: [],

  setQuery: (q) => set({ query: q }),

  addRecentSearch: (q) => {
    if (!q.trim()) return;
    const existing = get().recentSearches.filter((s) => s !== q);
    const updated = [q, ...existing].slice(0, 10);
    set({ recentSearches: updated });
    AsyncStorage.setItem(SEARCH_STORAGE_KEY, JSON.stringify(updated)).catch(() => {});
  },

  clearRecentSearches: () => {
    set({ recentSearches: [] });
    AsyncStorage.removeItem(SEARCH_STORAGE_KEY).catch(() => {});
  },
}));

// ── Theme Store ──
export type ThemeMode = 'light' | 'dark' | 'system';

interface ThemeState {
  themeMode: ThemeMode;
  setThemeMode: (mode: ThemeMode) => void;
  loadFromPreferences: (mode: ThemeMode) => void;
}

const THEME_STORAGE_KEY = 'dakkho_theme_mode';

export const useThemeStore = create<ThemeState>((set) => ({
  themeMode: 'system',

  setThemeMode: (mode) => {
    set({ themeMode: mode });
    AsyncStorage.setItem(THEME_STORAGE_KEY, mode).catch(() => {});
  },

  loadFromPreferences: (mode) => {
    set({ themeMode: mode });
  },
}));

// ── Server Config Store ──
interface ServerConfig {
  homeSections: string[];
  bottomNavTabs: string[];
  topBarElements: string[];
}

interface ServerConfigState {
  config: ServerConfig | null;
  isHomeSectionVisible: (section: string) => boolean;
  isBottomNavTabVisible: (tab: string) => boolean;
  isTopBarElementVisible: (element: string) => boolean;
  fetchConfig: () => Promise<void>;
}

const DEFAULT_CONFIG: ServerConfig = {
  homeSections: ['hero', 'continue-watching', 'categories', 'new-releases', 'live', 'trending', 'instructors', 'leaderboard', 'recommended'],
  bottomNavTabs: ['home', 'explore', 'my-courses', 'watch-history', 'profile'],
  topBarElements: ['search', 'notifications', 'avatar', 'hamburger'],
};

export const useServerConfigStore = create<ServerConfigState>((set, get) => ({
  config: null,

  isHomeSectionVisible: (section) => {
    const { config } = get();
    return config ? config.homeSections.includes(section) : DEFAULT_CONFIG.homeSections.includes(section);
  },

  isBottomNavTabVisible: (tab) => {
    const { config } = get();
    return config ? config.bottomNavTabs.includes(tab) : DEFAULT_CONFIG.bottomNavTabs.includes(tab);
  },

  isTopBarElementVisible: (element) => {
    const { config } = get();
    return config ? config.topBarElements.includes(element) : DEFAULT_CONFIG.topBarElements.includes(element);
  },

  fetchConfig: async () => {
    try {
      // Would fetch from API — for now use defaults
      set({ config: DEFAULT_CONFIG });
    } catch {
      set({ config: DEFAULT_CONFIG });
    }
  },
}));

// ── Helper: URL to Page mapping ──
export function urlToPage(url: string): { page: Page; params: PageParams } {
  const parts = url.split('/').filter(Boolean);
  if (parts.length === 0) return { page: 'home', params: {} };

  const pageMap: Record<string, Page> = {
    '': 'home',
    home: 'home',
    explore: 'explore',
    search: 'search',
    login: 'login',
    signup: 'signup',
    'forgot-password': 'forgot-password',
    notifications: 'notifications',
    profile: 'profile',
    'my-courses': 'my-courses',
    bookmarks: 'bookmarks',
    settings: 'settings',
    help: 'help',
    downloads: 'downloads',
    certificates: 'certificates',
    'live-sessions': 'live-sessions',
    achievements: 'achievements',
    discussion: 'discussion',
    about: 'about',
    'watch-history': 'watch-history',
    assignment: 'assignment',
    instructors: 'instructors',
    category: 'category',
    pricing: 'pricing',
    changelog: 'changelog',
    maintenance: 'maintenance',
    terms: 'terms',
    privacy: 'privacy',
    faq: 'faq',
    'contact-support': 'contact-support',
    'report-issue': 'report-issue',
    'terms-of-service': 'terms-of-service',
    'privacy-policy': 'privacy-policy',
    'refund-policy': 'refund-policy',
    'exam-prep': 'exam-prep',
    'exam-schedule': 'exam-schedule',
    'exam-results': 'exam-results',
    'exam-practice': 'exam-practice',
    'exam-tips': 'exam-tips',
    leaderboard: 'leaderboard',
    'study-groups': 'study-groups',
    'peer-connections': 'peer-connections',
    community: 'community',
    feedback: 'feedback',
    roadmap: 'roadmap',
  };

  // Dynamic routes
  if (parts[0] === 'course' && parts[1]) {
    if (parts[2]) {
      const subMap: Record<string, Page> = {
        curriculum: 'course-curriculum',
        reviews: 'course-reviews',
        qa: 'course-qa',
        announcements: 'course-announcements',
        resources: 'course-resources',
        notes: 'course-notes',
        quizzes: 'course-quizzes',
        progress: 'course-progress',
        video: 'video-player',
      };
      return { page: subMap[parts[2]] || 'course-detail', params: { courseId: parts[1] } };
    }
    return { page: 'course-detail', params: { courseId: parts[1] } };
  }

  if (parts[0] === 'instructor' && parts[1]) {
    if (parts[2]) {
      const subMap: Record<string, Page> = {
        courses: 'instructor-courses',
        reviews: 'instructor-reviews',
        schedule: 'instructor-schedule',
        contact: 'instructor-contact',
      };
      return { page: subMap[parts[2]] || 'instructor-profile', params: { instructorId: parts[1] } };
    }
    return { page: 'instructor-profile', params: { instructorId: parts[1] } };
  }

  // Department pages
  if (parts[0] === 'department' && parts[1]) {
    const deptKey = `dept-${parts[1]}` as Page;
    return { page: deptKey, params: { departmentId: parts[1] } };
  }

  // Semester pages
  if (parts[0] === 'semester' && parts[1]) {
    const semKey = `semester-${parts[1]}` as Page;
    return { page: semKey, params: { semesterId: parts[1] } };
  }

  // Settings sub-pages
  if (parts[0] === 'settings' && parts[1]) {
    const settingsMap: Record<string, Page> = {
      account: 'settings-account',
      notifications: 'settings-notifications',
      privacy: 'settings-privacy',
      language: 'settings-language',
      theme: 'settings-theme',
      downloads: 'settings-downloads',
      'video-quality': 'settings-video-quality',
      'download-settings': 'settings-download-settings',
      'network-data': 'settings-network-data',
      'content-protection': 'settings-content-protection',
      sessions: 'settings-sessions',
    };
    return { page: settingsMap[parts[1]] || 'settings', params: {} };
  }

  // Profile sub-pages
  if (parts[0] === 'profile' && parts[1]) {
    const profileMap: Record<string, Page> = {
      edit: 'edit-profile',
      'change-password': 'change-password',
      'learning-stats': 'learning-stats',
      subscription: 'subscription',
      referral: 'referral',
      'delete-account': 'delete-account',
      'verify-email': 'verify-email',
    };
    return { page: profileMap[parts[1]] || 'profile', params: {} };
  }

  // Payment pages
  if (parts[0] === 'payment' && parts[1]) {
    const payMap: Record<string, Page> = {
      success: 'payment-success',
      failed: 'payment-failed',
      cancel: 'payment-cancel',
    };
    return { page: payMap[parts[1]] || 'home', params: {} };
  }

  const page = pageMap[parts[0]] || 'home';
  return { page, params: {} };
}
