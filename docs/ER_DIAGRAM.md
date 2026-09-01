erDiagram
    ROLE ||--o{ USER_ROLE : has
    USER ||--o{ USER_ROLE : has
    USER }o--|| DEPARTMENT : belongs_to
    DEPARTMENT ||--o{ EVENT : hosts
    EVENT_CATEGORY ||--o{ EVENT : categorizes
    VENUE ||--o{ EVENT : hosts_at
    USER ||--o{ EVENT : organizes
    EVENT ||--|| BUDGET : has
    EVENT ||--o{ EXPENSE : tracks
    BUDGET ||--o{ EXPENSE : contains
    EXPENSE ||--o| BILL : has
    VENDOR ||--o{ BILL : issues
    EVENT ||--o{ REGISTRATION : accepts
    USER ||--o{ REGISTRATION : makes
    EVENT ||--o{ ATTENDANCE : records
    USER ||--o{ ATTENDANCE : checks_in
    EVENT ||--o{ VOLUNTEER : manages
    EVENT ||--o{ EVENT_DOCUMENT : stores
    EVENT ||--o{ EVENT_GALLERY : archives
    EVENT ||--o{ EVENT_VIDEO : archives
    EVENT ||--o{ EVENT_TASK : tracks
    EVENT ||--o{ FEEDBACK : receives
    USER ||--o{ FEEDBACK : gives
    USER ||--o{ NOTIFICATION : receives
    USER ||--o{ AUDIT_LOG : performs
    USER ||--o{ BOOKMARK : saves
    EVENT ||--o{ BOOKMARK : bookmarked

    ROLE {
        bigint id PK
        string name UK
        string description
    }

    USER {
        bigint id PK
        string email UK
        string password
        string first_name
        string last_name
        bigint department_id FK
        boolean active
        boolean email_verified
    }

    DEPARTMENT {
        bigint id PK
        string name UK
        string code UK
        string description
    }

    EVENT {
        bigint id PK
        string event_code UK
        string title
        string status
        date start_date
        bigint department_id FK
        bigint category_id FK
        bigint venue_id FK
        bigint organizer_id FK
    }

    BUDGET {
        bigint id PK
        bigint event_id FK UK
        decimal estimated_amount
        decimal approved_amount
        decimal actual_spending
    }

    EXPENSE {
        bigint id PK
        bigint event_id FK
        string category
        decimal amount
    }

    BILL {
        bigint id PK
        bigint expense_id FK
        bigint vendor_id FK
        string approval_status
        string file_path
    }

    REGISTRATION {
        bigint id PK
        bigint event_id FK
        bigint user_id FK
        string status
    }

    ATTENDANCE {
        bigint id PK
        bigint event_id FK
        bigint user_id FK
        datetime check_in_time
        string check_in_method
    }

    AUDIT_LOG {
        bigint id PK
        bigint user_id FK
        string action
        string entity_type
        bigint entity_id
        datetime timestamp
    }
