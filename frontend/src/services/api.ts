import api from '../api/axios';
import type { ApiResponse, AuthResponse, DashboardData, Department, Event, EventCategory, PageResponse, User, Venue } from '../types';

export const authApi = {
  login: (email: string, password: string) =>
    api.post<ApiResponse<AuthResponse>>('/auth/login', { email, password }),
  register: (data: { email: string; password: string; firstName: string; lastName: string; phone?: string }) =>
    api.post<ApiResponse<AuthResponse>>('/auth/register', data),
  me: () => api.get<ApiResponse<User>>('/auth/me'),
  logout: () => api.post('/auth/logout'),
};

export const eventApi = {
  list: (params?: Record<string, unknown>) =>
    api.get<ApiResponse<PageResponse<Event>>>('/events', { params }),
  get: (id: number) => api.get<ApiResponse<Event>>(`/events/${id}`),
  create: (data: Record<string, unknown>) => api.post<ApiResponse<Event>>('/events', data),
  update: (id: number, data: Record<string, unknown>) => api.put<ApiResponse<Event>>(`/events/${id}`, data),
  submit: (id: number) => api.post<ApiResponse<Event>>(`/events/${id}/submit`),
  approve: (id: number, comment?: string) => api.post<ApiResponse<Event>>(`/events/${id}/approve`, { comment }),
  reject: (id: number, comment?: string) => api.post<ApiResponse<Event>>(`/events/${id}/reject`, { comment }),
  complete: (id: number) => api.post<ApiResponse<Event>>(`/events/${id}/complete`),
  search: (q: string) => api.get<ApiResponse<Event[]>>('/events/search', { params: { q } }),
};

export const dashboardApi = {
  admin: () => api.get<ApiResponse<DashboardData>>('/dashboard/admin'),
  operator: () => api.get<ApiResponse<DashboardData>>('/dashboard/operator'),
  student: () => api.get<ApiResponse<DashboardData>>('/dashboard/student'),
};

export const masterApi = {
  departments: () => api.get<ApiResponse<Department[]>>('/departments'),
  categories: () => api.get<ApiResponse<EventCategory[]>>('/categories'),
  venues: () => api.get<ApiResponse<Venue[]>>('/venues'),
};

export const registrationApi = {
  register: (eventId: number) => api.post('/registrations', { eventId }),
  my: () => api.get('/registrations/my'),
};
