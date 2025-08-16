# Online Voting System 🗳️

A secure, role-based online voting system built with Spring Boot, featuring JWT authentication, role-based access control, and comprehensive voting management.

## 🏗️ Project Overview

The Online Voting System is a robust web application that enables secure electronic voting with the following key features:

- **Role-based Access Control**: Separate interfaces for ADMIN and VOTER roles
- **JWT Authentication**: Secure token-based authentication with configurable expiration
- **Election Management**: Create and manage elections with start/end dates
- **Candidate Management**: Add candidates to specific elections
- **Secure Voting**: Cast votes with validation and restrictions
- **Real-time Results**: View election results with vote tallies
- **City-based Voter Filtering**: Filter voters by geographical location
- **Bean Validation**: Comprehensive input validation on all DTOs and entities
- **Global Exception Handling**: Centralized error handling with proper HTTP status codes

## ✅ Requirements Implementation Status

### Core Requirements ✅
- [x] **Spring Boot Project**: Initialized with all required dependencies
- [x] **Bean Validation**: Applied on DTOs with @Valid, @NotNull, @NotBlank, @Email, @Size, @Future
- [x] **Component Scanning**: @Component, @Service, @Repository with @ComponentScan filters
- [x] **Entity Classes**: Voter, Candidate, Election, Vote with proper JPA annotations
- [x] **Repository Pattern**: CrudRepository implementation with custom finder methods
- [x] **Custom Queries**: @Query for custom results, @Modifying for update/delete operations
- [x] **REST Endpoints**: @RestController with proper HTTP methods and status codes
- [x] **Global Exception Handling**: @ControllerAdvice with comprehensive error handling
- [x] **Spring Security**: JWT-based authentication with role-based authorization
- [x] **Role-based Access Control**: ADMIN and VOTER roles with proper endpoint protection

### User Story Requirements ✅

#### Admin User Stories
- [x] **Register Candidates**: Admin can submit forms to register new candidates
- [x] **Assign Voters**: Admin can assign eligible voters based on city
- [x] **Election Management**: Create and manage elections with time windows
- [x] **Results Display**: Count and display election results in real-time
- [x] **Security**: All admin endpoints secured with ADMIN role requirement

#### Voter User Stories
- [x] **Secure Login**: Voter login returns JWT token on success
- [x] **Vote Casting**: Voters can cast one vote per election with validation
- [x] **Time Restrictions**: Voting only allowed during election time window
- [x] **Duplicate Prevention**: System prevents multiple votes by same voter
- [x] **City Assignment**: Voters must be assigned to a city before voting

### Technical Requirements ✅
- [x] **Bean Validation**: @Valid annotations on all controller endpoints
- [x] **Custom Queries**: @Modifying @Query for update/delete operations
- [x] **Exception Handling**: Custom exceptions with proper HTTP status codes
- [x] **JWT Security**: Token generation, validation, and role extraction
- [x] **Data Integrity**: Proper relationships and constraints between entities
- [x] **Input Validation**: Comprehensive validation on all input DTOs

## 🏛️ Architecture

The system follows a layered architecture pattern:

```
┌─────────────────┐
│   Controllers   │ ← REST API endpoints with validation
├─────────────────┤
│    Services     │ ← Business logic and validation
├─────────────────┤
│  Repositories   │ ← Data access with custom queries
├─────────────────┤
│     Domain      │ ← Entity models with validation
└─────────────────┘
```

### Core Components

- **Security Layer**: JWT-based authentication with role-based authorization
- **Validation Layer**: Bean validation on all DTOs and entities
- **Business Logic**: Service layer handling voting rules and validation
- **Data Persistence**: JPA/Hibernate with custom repository methods
- **Exception Handling**: Global exception handler with proper HTTP status codes

## 🔐 Authentication & Authorization

### JWT Token Structure
```json
{
  "sub": "voter@example.com",
  "role": "VOTER",
  "iat": 1640995200,
  "exp": 1640998800
}
```

### Role-based Access Control

| Endpoint | Role Required | Description |
|----------|---------------|-------------|
| `/auth/**` | None | Public authentication endpoints |
| `/admin/**` | ADMIN | Admin management endpoints |
| `/api/voters/**` | VOTER | Voter-specific endpoints |

### Public Endpoints
- `POST /auth/admin/login` - Admin login
- `POST /auth/voter/login` - Voter login  
- `POST /auth/voter/register` - Voter registration

### Admin Endpoints
- `POST /admin/elections` - Create election
- `GET /admin/elections` - List all elections
- `POST /admin/candidates` - Register candidate
- `GET /admin/candidates` - List all candidates
- `POST /admin/voters` - Register voter
- `PUT /admin/voters/{id}/assign` - Assign voter to city
- `GET /admin/voters/city/{city}` - List voters by city
- `GET /admin/results` - View election results

### Voter Endpoints
- `GET /api/voters/candidates` - View available candidates
- `POST /api/voters/vote` - Cast vote
- `GET /api/voters/city/{city}` - View voters by city
- `GET /api/voters/{id}` - Get voter details

## 📊 Data Models

### Voter Entity
```java
@Entity
@Table(name = "users")
public class Voter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank @Email @Column(unique = true)
    private String email;
    
    @NotBlank
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @NotBlank
    private String city;
}
```

### Candidate Entity
```java
@Entity
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank @Size(min = 2, max = 100)
    private String name;
    
    @NotNull
    @ManyToOne
    private Election election;
}
```

### Election Entity
```java
@Entity
@Table(name = "Election")
public class Election {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    @NotNull @Future
    private LocalDate startDate;
    
    @NotNull @Future
    private LocalDate endDate;
}
```

### Vote Entity
```java
@Entity
@Table(name = "vote")
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime voteTime;
    
    @ManyToOne @JoinColumn(nullable = false)
    private Candidate candidate;
    
    @ManyToOne @JoinColumn(nullable = false)
    private Election election;
    
    @ManyToOne @JoinColumn(nullable = false)
    private Voter voter;
}
```

## 🔍 Validation & Error Handling

### Bean Validation
All DTOs and entities include comprehensive validation:

- **@NotNull**: Required fields
- **@NotBlank**: Non-empty strings
- **@Email**: Valid email format
- **@Size**: String length constraints
- **@Future**: Date validation for elections

### Global Exception Handler
Centralized error handling with proper HTTP status codes:

- **400 Bad Request**: Validation errors, bad input
- **401 Unauthorized**: Invalid/missing JWT
- **403 Forbidden**: Insufficient role permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server-side errors

### Custom Exceptions
- `VotingClosedException`: Voting outside time window
- `UnassignedVoterException`: Voter not assigned to city
- `BadRequestException`: Invalid business logic
- `ResourceNotFoundException`: Entity not found

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd onlineVotingSystem
   ```

2. **Configure the application**
   
   Update `src/main/resources/application.properties`:
   ```properties
   # Database Configuration (for production, use PostgreSQL/MySQL)
   spring.datasource.url=jdbc:postgresql://localhost:5432/voting_system
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JWT Configuration
   jwt.secret=your-256-bit-secret-key-here-make-it-long-and-secure
   jwt.expiration=3600000
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or build and run:
   ```bash
   mvn clean package
   java -jar target/onlineVotingSystem-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080`
   - H2 Console (dev): `http://localhost:8080/h2-console`
   - Health Check: `http://localhost:8080/actuator/health`

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=GlobalExceptionHandlerTest

# Run with coverage
mvn jacoco:report
```

### Test Coverage
- **Unit Tests**: Service layer, exception handling
- **Integration Tests**: Repository layer, security
- **Exception Tests**: Global exception handler
- **Validation Tests**: DTO validation constraints

## 📝 API Usage Examples

### Admin Registration
```bash
curl -X POST http://localhost:8080/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

### Create Election
```bash
curl -X POST http://localhost:8080/admin/elections \
  -H "Authorization: Bearer <ADMIN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Presidential Election 2024",
    "startDate": "2024-11-05",
    "endDate": "2024-11-06"
  }'
```

### Register Candidate
```bash
curl -X POST http://localhost:8080/admin/candidates \
  -H "Authorization: Bearer <ADMIN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "party": "Democratic Party",
    "electionId": 1
  }'
```

### Voter Login
```bash
curl -X POST http://localhost:8080/auth/voter/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "voter@example.com",
    "password": "voter123"
  }'
```

### Cast Vote
```bash
curl -X POST http://localhost:8080/api/voters/vote \
  -H "Authorization: Bearer <VOTER_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "candidateId": 1,
    "electionId": 1
  }' \
  -G -d "voterEmail=voter@example.com"
```

## 🔧 Configuration

### JWT Configuration
```properties
jwt.secret=your-secret-key-here
jwt.expiration=3600000
```

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/voting_system
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Security Configuration
```properties
spring.security.user.name=admin
spring.security.user.password=admin
```

## 📚 Additional Documentation

- [Exception Handling Guide](EXCEPTION_HANDLING_README.md) - Comprehensive error handling documentation
- [Postman Collection](postman_collection.json) - API testing collection
- [Security Configuration](src/main/java/com/sprints/onlineVotingSystem/config/SecurityConfig.java) - Security setup details

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation and examples

---

**Note**: This system is designed for educational and demonstration purposes. For production use, additional security measures, logging, and monitoring should be implemented.


