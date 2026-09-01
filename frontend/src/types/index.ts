export type RoleType = 'SUPER_ADMIN' | 'OPERATOR' | 'STUDENT';

export type EventStatus =
  | 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED'
  | 'REJECTED' | 'ONGOING' | 'COMPLETED' | 'ARCHIVED';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  phone?: string;
  departmentId?: number;
  departmentName?: string;
  roles: RoleType[];
  active: boolean;
  emailVerified: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface Event {
  id: number;
  eventCode: string;
  title: string;
  description?: string;
  categoryId?: number;
  categoryName?: string;
  departmentId?: number;
  departmentName?: string;
  organizerId: number;
  organizerName: string;
  facultyCoordinator?: string;
  studentCoordinator?: string;
  venueId?: number;
  venueName?: string;
  startDate: string;
  endDate?: string;
  startTime?: string;
  endTime?: string;
  durationHours?: number;
  capacity: number;
  objectives?: string;
  requirements?: string;
  status: EventStatus;
  bannerPath?: string;
  estimatedBudget?: number;
  approvedBudget?: number;
  actualSpending?: number;
  remainingBudget?: number;
  budgetUtilization?: number;
  registrationCount: number;
  createdAt: string;
}

export interface DashboardData {
  totalEvents?: number;
  activeEvents?: number;
  upcomingEvents?: number;
  completedEvents?: number;
  operators?: number;
  departments?: number;
  registrations?: number;
  totalBudget?: number;
  totalExpenses?: number;
  pendingBills?: number;
  pendingApprovals?: number;
  assignedEvents?: number;
  todaysEvents?: number;
  pendingTasks?: number;
  uploadedBills?: number;
  registeredEvents?: number;
  certificates?: number;
  notifications?: number;
  monthlyEvents?: { month: number; count: number }[];
  expenseTrends?: { category: string; amount: number }[];
  budgetUtilization?: { event: string; approved: number; spent: number; utilization: number }[];
  departmentPerformance?: { department: string; events: number }[];
  attendanceAnalysis?: { month: number; count: number }[];
}

export interface Department {
  id: number;
  name: string;
  code: string;
  description?: string;
  headName?: string;
}

export interface EventCategory {
  id: number;
  name: string;
  description?: string;
}

export interface Venue {
  id: number;
  name: string;
  capacity: number;
  location?: string;
  facilities?: string;
}
