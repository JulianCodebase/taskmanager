taskmanager
│
├── .idea
├── .mvn
├── src
│   ├── main
│   │   ├── java
│   │   │   └── de.personal.taskmanager
│   │   │       ├── annotation
│   │   │       │   └── AuditLog
│   │   │       ├── aop
│   │   │       │   └── AuditLoggingAspect
│   │   │       ├── common
│   │   │       │   ├── CommentMapper
│   │   │       │   ├── CustomErrorResponse
│   │   │       │   ├── SecurityErrorWriter
│   │   │       │   └── TaskMapper
│   │   │       ├── config
│   │   │       │   ├── JpaAuditingConfig
│   │   │       │   └── SecurityConfig
│   │   │       ├── controller
│   │   │       │   ├── AuthController
│   │   │       │   ├── TaskCommentController
│   │   │       │   └── TaskController
│   │   │       ├── dto
│   │   │       │   ├── auth
│   │   │       │   │   ├── AuthRegisterRequest
│   │   │       │   │   ├── AuthRequest
│   │   │       │   │   └── AuthResponse
│   │   │       │   └── task
│   │   │       │       ├── TaskBase
│   │   │       │       ├── TaskCommentRequest
│   │   │       │       ├── TaskCommentResponse
│   │   │       │       ├── TaskRequest
│   │   │       │       └── TaskResponse
│   │   │       ├── exception
│   │   │       │   ├── CustomAccessDeniedHandler
│   │   │       │   ├── CustomAuthenticationEntryPoint
│   │   │       │   ├── GlobalExceptionHandler
│   │   │       │   ├── JwtExceptionHandler
│   │   │       │   └── TaskNotFoundException
│   │   │       ├── message
│   │   │       │   ├── TaskEventConsumer
│   │   │       │   └── TaskEventProducer
│   │   │       ├── model
│   │   │       │   ├── AppUser
│   │   │       │   ├── AuditLogRecord
│   │   │       │   ├── Task
│   │   │       │   ├── TaskComment
│   │   │       │   ├── TaskPriority
│   │   │       │   ├── TaskStatus
│   │   │       │   ├── UserRole
│   │   │       │   ├── UserStats
│   │   │       │   └── UserTitle
│   │   │       ├── respository
│   │   │       │   ├── AuditLogRepository
│   │   │       │   ├── TaskCommentRepository
│   │   │       │   ├── TaskRepository
│   │   │       │   ├── UserRepository
│   │   │       │   └── UserStatsRepository
│   │   │       ├── security
│   │   │       │   ├── JwtFilter
│   │   │       │   └── JwtUtil
│   │   │       ├── service
│   │   │       │   ├── impl
│   │   │       │   │   ├── AuthServiceImpl
│   │   │       │   │   ├── NotificationServiceImpl
│   │   │       │   │   ├── TaskCleanupServiceImpl
│   │   │       │   │   ├── TaskCommentServiceImpl
│   │   │       │   │   ├── TaskServiceImpl
│   │   │       │   │   ├── UserDetailServiceImpl
│   │   │       │   │   └── UserStatsServiceImpl
│   │   │       │   ├── AuthService
│   │   │       │   ├── NotificationService
│   │   │       │   ├── TaskCleanupService
│   │   │       │   ├── TaskCommentService
│   │   │       │   ├── TaskService
│   │   │       │   └── UserStatsService
│   │   │       └── TaskManagerApplication
│   │   └── resources
│   ├── test
│   └── target
├── .gitattributes
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── Dockerfile.jenkins
├── HELP.md
├── jenkins-docker-compose.yml
├── Jenkinsfile
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── taskmanager.iml