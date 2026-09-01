# CEOMS Database Schema

## Entity Relationship Overview

```
Role ←──M:N──→ User ──M:1──→ Department
                  │
                  ├── creates/manages Events
                  └── receives Notifications

Department ──1:M──→ Event ──M:1──→ EventCategory
                      │
                      ├──M:1──→ Venue
                      ├──M:1──→ User (organizer/operator)
                      ├──1:1──→ Budget
                      ├──1:M──→ Expense ──M:1──→ Vendor
                      │              └──1:1──→ Bill
                      ├──1:M──→ Registration
                      ├──1:M──→ Attendance
                      ├──1:M──→ Volunteer
                      ├──1:M──→ EventDocument
                      ├──1:M──→ EventGallery
                      ├──1:M──→ EventVideo
                      ├──1:M──→ Feedback
                      └──1:M──→ EventTask (checklist)

AuditLog ──M:1──→ User
Report ──M:1──→ User (generatedBy)
```

## Tables & Key Columns

| Table | Primary Key | Key Fields | Indexes |
|-------|-------------|------------|---------|
| roles | id | name (SUPER_ADMIN, OPERATOR, STUDENT) | name UNIQUE |
| users | id | email, password, first_name, last_name, phone, department_id, active, email_verified | email UNIQUE |
| user_roles | user_id, role_id | composite PK | |
| departments | id | name, code, description, head_name | code UNIQUE |
| event_categories | id | name, description | name UNIQUE |
| venues | id | name, capacity, location, facilities | |
| events | id | event_code, title, description, status, start_date, end_date, capacity, department_id, category_id, venue_id, organizer_id | event_code UNIQUE, status, start_date |
| budgets | id | event_id, estimated_amount, approved_amount, actual_spending | event_id UNIQUE |
| vendors | id | name, contact_person, email, phone, gst_number | |
| expenses | id | event_id, budget_id, category, amount, description, expense_date | event_id, category |
| bills | id | expense_id, vendor_id, bill_number, amount, bill_date, file_path, approval_status | bill_number, approval_status |
| registrations | id | event_id, user_id, status, registered_at | event_id, user_id UNIQUE |
| attendances | id | event_id, user_id, check_in_time, check_in_method, attendance_type | event_id, user_id |
| volunteers | id | event_id, name, email, phone, assigned_work, shift, performance_rating | event_id |
| event_documents | id | event_id, file_name, file_path, document_type | event_id |
| event_gallery | id | event_id, file_name, file_path, caption | event_id |
| event_videos | id | event_id, file_name, file_path, title, duration | event_id |
| event_tasks | id | event_id, title, completed, due_date, assigned_to | event_id |
| notifications | id | user_id, title, message, type, read, created_at | user_id, read |
| feedbacks | id | event_id, user_id, rating, comment | event_id |
| audit_logs | id | user_id, action, entity_type, entity_id, old_value, new_value, ip_address, timestamp | entity_type, timestamp |
| reports | id | report_type, file_path, generated_by, parameters | report_type |
| bookmarks | id | user_id, event_id | user_id, event_id UNIQUE |
| refresh_tokens | id | user_id, token, expiry_date | token UNIQUE |
| password_reset_tokens | id | user_id, token, expiry_date | token UNIQUE |
| announcements | id | title, content, created_by, active, publish_date | active |

## Event Status Enum
`DRAFT → SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED → ONGOING → COMPLETED → ARCHIVED`

## Expense Categories Enum
`FOOD, DECORATION, SOUND_SYSTEM, TRANSPORTATION, ACCOMMODATION, PRINTING, PHOTOGRAPHY, VIDEOGRAPHY, GIFTS, CERTIFICATES, MISCELLANEOUS`

## Bill Approval Status
`PENDING, APPROVED, REJECTED`

## Soft Delete
Events use `archived` status + `deleted_at` timestamp for soft delete with restore capability.
