# Online Voting System

A Spring Boot application for managing online voting processes.

## Features

- Voter management and authentication
- Candidate management
- Election management
- Secure voting process
- Admin results endpoint

## API Endpoints

### Admin Endpoints

#### GET /admin/results
Returns the total number of votes per candidate, sorted by vote count in descending order.

**Response:**
```json
[
  {
    "candidateName": "Candidate A",
    "totalVotes": 150
  },
  {
    "candidateName": "Candidate B", 
    "totalVotes": 120
  },
  {
    "candidateName": "Candidate C",
    "totalVotes": 80
  }
]
```

**Features:**
- Results are automatically sorted by vote count (highest to lowest)
- Efficient database query using JPA GROUP BY
- Comprehensive error handling and logging
- Production-ready code with full test coverage

## Architecture

The application follows a layered architecture:
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access and database operations
- **Domain Layer**: Entity models
- **DTO Layer**: Data transfer objects for API responses

## Technologies

- Spring Boot 3.x
- Spring Data JPA
- H2/MySQL Database
- Maven
- JUnit 5 for testing
- Mockito for mocking

