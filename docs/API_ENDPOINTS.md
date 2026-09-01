# CEOMS REST API Endpoints

Base URL: `/api/v1`

## Authentication (`/auth`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/auth/register` | Student registration | Public |
| POST | `/auth/login` | JWT login | Public |
| POST | `/auth/refresh` | Refresh access token | Public |
| POST | `/auth/logout` | Invalidate refresh token | Authenticated |
| POST | `/auth/forgot-password` | Send reset email | Public |
| POST | `/auth/reset-password` | Reset password with token | Public |
| POST | `/auth/verify-email` | Verify email | Public |
| POST | `/auth/change-password` | Change password | Authenticated |
| GET | `/auth/me` | Current user profile | Authenticated |

## Users (`/users`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/users` | List users (paginated, filtered) | SUPER_ADMIN |
| GET | `/users/{id}` | Get user by ID | SUPER_ADMIN |
| POST | `/users/operators` | Create operator | SUPER_ADMIN |
| PUT | `/users/{id}` | Update user | SUPER_ADMIN |
| DELETE | `/users/{id}` | Deactivate user | SUPER_ADMIN |
| GET | `/users/profile` | Get own profile | All |
| PUT | `/users/profile` | Update own profile | All |

## Departments (`/departments`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/departments` | List all | All |
| GET | `/departments/{id}` | Get by ID | All |
| POST | `/departments` | Create | SUPER_ADMIN |
| PUT | `/departments/{id}` | Update | SUPER_ADMIN |
| DELETE | `/departments/{id}` | Delete | SUPER_ADMIN |

## Event Categories (`/categories`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/categories` | List all | All |
| POST | `/categories` | Create | SUPER_ADMIN |
| PUT | `/categories/{id}` | Update | SUPER_ADMIN |
| DELETE | `/categories/{id}` | Delete | SUPER_ADMIN |

## Venues (`/venues`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/venues` | List all | All |
| POST | `/venues` | Create | SUPER_ADMIN |
| PUT | `/venues/{id}` | Update | SUPER_ADMIN |
| DELETE | `/venues/{id}` | Delete | SUPER_ADMIN |

## Events (`/events`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/events` | List (paginated, filtered, sorted) | All |
| GET | `/events/{id}` | Get event details | All |
| POST | `/events` | Create event proposal | OPERATOR, SUPER_ADMIN |
| PUT | `/events/{id}` | Update event | OPERATOR (own), SUPER_ADMIN |
| DELETE | `/events/{id}` | Soft delete | SUPER_ADMIN |
| POST | `/events/{id}/submit` | Submit for review | OPERATOR |
| POST | `/events/{id}/approve` | Approve event | SUPER_ADMIN |
| POST | `/events/{id}/reject` | Reject event | SUPER_ADMIN |
| POST | `/events/{id}/start` | Mark ongoing | OPERATOR, SUPER_ADMIN |
| POST | `/events/{id}/complete` | Mark completed | OPERATOR, SUPER_ADMIN |
| POST | `/events/{id}/archive` | Archive event | SUPER_ADMIN |
| POST | `/events/{id}/restore` | Restore archived | SUPER_ADMIN |
| GET | `/events/search` | Global search | All |
| GET | `/events/calendar` | Calendar view data | All |
| POST | `/events/{id}/banner` | Upload banner | OPERATOR |

## Budgets (`/budgets`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/budgets/event/{eventId}` | Get event budget | OPERATOR, SUPER_ADMIN |
| PUT | `/budgets/event/{eventId}` | Update budget | OPERATOR, SUPER_ADMIN |
| POST | `/budgets/event/{eventId}/approve` | Approve budget | SUPER_ADMIN |

## Expenses & Bills (`/expenses`, `/bills`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/expenses/event/{eventId}` | List expenses | OPERATOR, SUPER_ADMIN |
| POST | `/expenses` | Create expense | OPERATOR |
| PUT | `/expenses/{id}` | Update expense | OPERATOR |
| GET | `/bills` | List bills (filtered) | OPERATOR, SUPER_ADMIN |
| POST | `/bills` | Upload bill | OPERATOR |
| POST | `/bills/{id}/approve` | Approve bill | SUPER_ADMIN |
| POST | `/bills/{id}/reject` | Reject bill | SUPER_ADMIN |

## Vendors (`/vendors`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/vendors` | List vendors | OPERATOR, SUPER_ADMIN |
| POST | `/vendors` | Create vendor | OPERATOR, SUPER_ADMIN |
| PUT | `/vendors/{id}` | Update vendor | SUPER_ADMIN |

## Registrations (`/registrations`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/registrations/event/{eventId}` | Event registrations | OPERATOR, SUPER_ADMIN |
| GET | `/registrations/my` | My registrations | STUDENT |
| POST | `/registrations` | Register for event | STUDENT |
| DELETE | `/registrations/{id}` | Cancel registration | STUDENT |

## Attendance (`/attendance`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/attendance/event/{eventId}` | Attendance list | OPERATOR, SUPER_ADMIN |
| POST | `/attendance/check-in` | Manual check-in | OPERATOR |
| POST | `/attendance/qr-check-in` | QR check-in | OPERATOR, STUDENT |
| GET | `/attendance/event/{eventId}/qr` | Generate QR code | OPERATOR |
| GET | `/attendance/my` | My attendance | STUDENT |

## Volunteers (`/volunteers`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/volunteers/event/{eventId}` | List volunteers | OPERATOR |
| POST | `/volunteers` | Add volunteer | OPERATOR |
| PUT | `/volunteers/{id}` | Update volunteer | OPERATOR |
| DELETE | `/volunteers/{id}` | Remove volunteer | OPERATOR |

## Media (`/media`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/media/event/{eventId}/gallery` | Photo gallery | All |
| POST | `/media/event/{eventId}/gallery` | Upload photos | OPERATOR |
| GET | `/media/event/{eventId}/videos` | Videos | All |
| POST | `/media/event/{eventId}/videos` | Upload video | OPERATOR |
| GET | `/media/event/{eventId}/documents` | Documents | All |
| POST | `/media/event/{eventId}/documents` | Upload document | OPERATOR |

## Notifications (`/notifications`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/notifications` | My notifications | Authenticated |
| PUT | `/notifications/{id}/read` | Mark as read | Authenticated |
| PUT | `/notifications/read-all` | Mark all read | Authenticated |

## Feedback (`/feedback`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/feedback` | Submit feedback | STUDENT |
| GET | `/feedback/event/{eventId}` | Event feedback | OPERATOR, SUPER_ADMIN |

## Reports (`/reports`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/reports/event/{eventId}` | Event report | OPERATOR, SUPER_ADMIN |
| GET | `/reports/budget` | Budget report | SUPER_ADMIN |
| GET | `/reports/expense` | Expense report | SUPER_ADMIN |
| GET | `/reports/attendance` | Attendance report | SUPER_ADMIN |
| GET | `/reports/department` | Department report | SUPER_ADMIN |
| GET | `/reports/yearly` | Yearly report | SUPER_ADMIN |
| GET | `/reports/export/{type}` | Export PDF/Excel | SUPER_ADMIN |

## Dashboard (`/dashboard`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/dashboard/admin` | Admin dashboard stats | SUPER_ADMIN |
| GET | `/dashboard/operator` | Operator dashboard | OPERATOR |
| GET | `/dashboard/student` | Student dashboard | STUDENT |

## Audit (`/audit`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/audit` | Audit logs (paginated) | SUPER_ADMIN |
| GET | `/audit/search` | Search audit logs | SUPER_ADMIN |

## Announcements (`/announcements`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/announcements` | Active announcements | All |
| POST | `/announcements` | Create | SUPER_ADMIN |
| PUT | `/announcements/{id}` | Update | SUPER_ADMIN |
| DELETE | `/announcements/{id}` | Delete | SUPER_ADMIN |

## Bookmarks (`/bookmarks`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/bookmarks` | My bookmarks | STUDENT |
| POST | `/bookmarks/{eventId}` | Add bookmark | STUDENT |
| DELETE | `/bookmarks/{eventId}` | Remove bookmark | STUDENT |

## Event Tasks (`/tasks`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/tasks/event/{eventId}` | Event checklist | OPERATOR |
| POST | `/tasks` | Add task | OPERATOR |
| PUT | `/tasks/{id}` | Update task | OPERATOR |
| DELETE | `/tasks/{id}` | Delete task | OPERATOR |

## Certificates (`/certificates`)
| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/certificates/event/{eventId}` | Generate certificate | STUDENT |
