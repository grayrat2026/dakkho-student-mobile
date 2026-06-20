import axios, { type AxiosInstance, type AxiosError } from 'axios';
import * as SecureStore from 'expo-secure-store';
import { API_BASE } from './theme';

// ── Token Management (SecureStore for RN) ──
const AUTH_TOKEN_KEY = 'dakkho_student_token';
const PENDING_TOKEN_KEY = 'dakkho_pending_verification_token';

export async function getAuthToken(): Promise<string | null> {
  try {
    return await SecureStore.getItemAsync(AUTH_TOKEN_KEY);
  } catch {
    return null;
  }
}

export async function setAuthToken(token: string): Promise<void> {
  await SecureStore.setItemAsync(AUTH_TOKEN_KEY, token);
}

export async function clearAuthToken(): Promise<void> {
  await SecureStore.deleteItemAsync(AUTH_TOKEN_KEY);
}

export async function getPendingVerificationToken(): Promise<string | null> {
  try {
    return await SecureStore.getItemAsync(PENDING_TOKEN_KEY);
  } catch {
    return null;
  }
}

export async function setPendingVerificationToken(token: string): Promise<void> {
  await SecureStore.setItemAsync(PENDING_TOKEN_KEY, token);
}

export async function clearPendingVerificationToken(): Promise<void> {
  await SecureStore.deleteItemAsync(PENDING_TOKEN_KEY);
}

// ── Axios Instance ──
const api: AxiosInstance = axios.create({
  baseURL: API_BASE,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

// Request interceptor — attach auth token
api.interceptors.request.use(async (config) => {
  const token = await getAuthToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor — handle 401
api.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      await clearAuthToken();
    }
    return Promise.reject(error);
  }
);

// ── Types ──
export interface Course {
  id: string;
  title: string;
  description?: string;
  thumbnail?: string;
  instructorId: string;
  instructorName?: string;
  price: number;
  originalPrice?: number;
  rating: number;
  totalStudents: number;
  totalReviews: number;
  duration: number;
  level: 'beginner' | 'intermediate' | 'advanced' | 'expert';
  language: string;
  category?: string;
  tags?: string[];
  isFeatured: boolean;
  isFree: boolean;
  chapters?: Chapter[];
  createdAt: string;
  updatedAt: string;
}

export interface Instructor {
  id: string;
  name: string;
  avatar?: string;
  bio?: string;
  qualification?: string;
  rating: number;
  totalStudents: number;
  totalCourses: number;
  specialties?: string[];
}

export interface Enrollment {
  id: string;
  courseId: string;
  studentId: string;
  progress: number;
  completedAt?: string;
  enrolledAt: string;
  course?: Course;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  icon?: string;
  courseCount: number;
}

export interface Chapter {
  id: string;
  courseId: string;
  title: string;
  order: number;
  lessons: Lesson[];
}

export interface Lesson {
  id: string;
  chapterId: string;
  title: string;
  type: 'video' | 'quiz' | 'assignment' | 'resource';
  duration: number;
  videoUrl?: string;
  isFree: boolean;
  isCompleted: boolean;
}

export interface Technology {
  id: string;
  name: string;
  shortCode: string;
  description?: string;
  icon?: string;
  courseCount?: number;
}

export interface Institute {
  id: string;
  name: string;
  code: string;
  city?: string;
}

// ── Auth API ──
export const authApi = {
  login: async (email: string, password: string) => {
    const res = await api.post('/api/auth/login', { email, password });
    return res.data;
  },

  signup: async (data: {
    fullName: string;
    email: string;
    phone: string;
    password: string;
    instituteId?: string;
    technologyId?: string;
  }) => {
    const res = await api.post('/api/auth/register', data);
    return res.data;
  },

  verifyOTP: async (email: string, otp: string) => {
    const res = await api.post('/api/auth/verify-otp', { email, otp });
    return res.data;
  },

  forgotPassword: async (email: string) => {
    const res = await api.post('/api/auth/forgot-password', { email });
    return res.data;
  },

  resetPassword: async (token: string, password: string) => {
    const res = await api.post('/api/auth/reset-password', { token, password });
    return res.data;
  },

  refreshToken: async () => {
    const res = await api.post('/api/auth/refresh');
    return res.data;
  },
};

// ── Course API ──
export const courseApi = {
  list: async (params?: { limit?: number; offset?: number; category?: string; search?: string; level?: string }) => {
    const res = await api.get('/api/courses', { params });
    return res.data as { courses: Course[]; total: number };
  },

  getById: async (id: string) => {
    const res = await api.get(`/api/courses/${id}`);
    return res.data as Course;
  },

  getChapters: async (id: string) => {
    const res = await api.get(`/api/courses/${id}/chapters`);
    return res.data as Chapter[];
  },

  getReviews: async (id: string) => {
    const res = await api.get(`/api/courses/${id}/reviews`);
    return res.data;
  },

  search: async (query: string, filters?: Record<string, any>) => {
    const res = await api.get('/api/courses', { params: { search: query, ...filters } });
    return res.data as { courses: Course[]; total: number };
  },
};

// ── Enrollment API ──
export const enrollmentApi = {
  enroll: async (courseId: string) => {
    const res = await api.post('/api/enrollments', { courseId });
    return res.data;
  },

  getMyEnrollments: async () => {
    const res = await api.get('/api/enrollments/my');
    return res.data as Enrollment[];
  },

  getProgress: async (courseId: string) => {
    const res = await api.get(`/api/enrollments/${courseId}/progress`);
    return res.data;
  },

  updateProgress: async (courseId: string, lessonId: string) => {
    const res = await api.post(`/api/enrollments/${courseId}/progress`, { lessonId });
    return res.data;
  },
};

// ── Instructor API ──
export const instructorApi = {
  list: async (params?: { limit?: number; offset?: number }) => {
    const res = await api.get('/api/instructors', { params });
    return res.data as { instructors: Instructor[]; total: number };
  },

  getById: async (id: string) => {
    const res = await api.get(`/api/instructors/${id}`);
    return res.data as Instructor;
  },

  getCourses: async (id: string) => {
    const res = await api.get(`/api/instructors/${id}/courses`);
    return res.data as Course[];
  },

  getReviews: async (id: string) => {
    const res = await api.get(`/api/instructors/${id}/reviews`);
    return res.data;
  },
};

// ── Category API ──
export const categoryApi = {
  list: async () => {
    const res = await api.get('/api/categories');
    return res.data as Category[];
  },

  getById: async (id: string) => {
    const res = await api.get(`/api/categories/${id}`);
    return res.data as Category;
  },
};

// ── Technology API ──
export const technologyApi = {
  list: async () => {
    const res = await api.get('/api/technologies');
    return res.data as Technology[];
  },
};

// ── Institute API ──
export const instituteApi = {
  list: async () => {
    const res = await api.get('/api/institutes');
    return res.data as Institute[];
  },
};

// ── Notification API ──
export const notificationApi = {
  list: async () => {
    const res = await api.get('/api/notifications');
    return res.data;
  },

  markAsRead: async (id: string) => {
    const res = await api.patch(`/api/notifications/${id}/read`);
    return res.data;
  },

  markAllAsRead: async () => {
    const res = await api.patch('/api/notifications/read-all');
    return res.data;
  },
};

// ── Profile API ──
export const profileApi = {
  getProfile: async () => {
    const res = await api.get('/api/student/profile');
    return res.data;
  },

  updateProfile: async (data: Record<string, any>) => {
    const res = await api.patch('/api/student/profile', data);
    return res.data;
  },

  changePassword: async (data: { currentPassword: string; newPassword: string }) => {
    const res = await api.post('/api/student/change-password', data);
    return res.data;
  },

  deleteAccount: async () => {
    const res = await api.delete('/api/student/account');
    return res.data;
  },
};

// ── Payment API ──
export const paymentApi = {
  createOrder: async (courseId: string) => {
    const res = await api.post('/api/payments/create', { courseId });
    return res.data;
  },

  verifyPayment: async (orderId: string) => {
    const res = await api.post('/api/payments/verify', { orderId });
    return res.data;
  },

  getPaymentHistory: async () => {
    const res = await api.get('/api/payments/history');
    return res.data;
  },
};

// ── Utility ──
export function formatDuration(minutes: number): string {
  if (!minutes) return '0m';
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours === 0) return `${mins}m`;
  if (mins === 0) return `${hours}h`;
  return `${hours}h ${mins}m`;
}

export function formatPrice(price: number): string {
  if (price === 0) return 'Free';
  return `৳${price.toLocaleString()}`;
}
