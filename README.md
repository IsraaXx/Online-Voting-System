# Online Voting System

A Spring Boot application for managing online voting processes.

## Features

- Voter management and authentication
- Candidate management
- Election management
- Secure voting process with time restrictions
- Voter assignment validation
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

### Voter Endpoints

#### POST /api/voters/login
Authenticates a voter and returns a JWT token.

**Request:**
```json
{
  "email": "voter@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "jwt.token.here",
  "email": "voter@example.com",
  "role": "VOTER",
  "message": "Login successful"
}
```

#### GET /api/voters/candidates
Returns a list of all candidates available for voting (requires VOTER role).

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "party": "Independent",
    "electionName": "Presidential Election 2024"
  }
]
```

#### POST /api/voters/vote
Casts a vote for a candidate in an election (requires VOTER role).

**Request:**
```json
{
  "candidateId": 1,
  "electionId": 1
}
```

**Voting Restrictions:**
- **Time Window**: Voting is only allowed between election start and end dates
- **Voter Assignment**: Voter must be assigned to a city/constituency
- **Duplicate Prevention**: Voter can only vote once per election
- **Election Validation**: Candidate must belong to the specified election

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

