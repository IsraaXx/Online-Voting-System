# Global Exception Handling Mechanism

This document describes the comprehensive global exception handling mechanism implemented in the Spring Boot application using `@ControllerAdvice` and `@ExceptionHandler`.

## Overview

The global exception handling mechanism provides:
- **Consistent error responses** across all API endpoints
- **Proper HTTP status codes** for different types of errors
- **Structured error information** with timestamps, error types, and messages
- **Centralized logging** for all exceptions
- **Production-ready error handling** with security considerations

## Components

### 1. Custom Exception Classes

#### ResourceNotFoundException
```java
// Thrown when a requested resource is not found
throw new ResourceNotFoundException("Voter", "id", 123);
// Results in: "Voter not found with id : '123'"
```

#### BadRequestException
```java
// Thrown for invalid input or business rule violations
throw new BadRequestException("City name cannot be null or empty");
```

### 2. ErrorResponse DTO

Standardized error response structure:
```json
{
  "timestamp": "2025-08-15 19:48:45",
  "status": 404,
  "error": "Not Found",
  "message": "Voter not found with id : '123'",
  "path": "/api/voters/123",
  "validationErrors": {
    "email": "Email is required",
    "name": "Name cannot be empty"
  }
}
```

### 3. GlobalExceptionHandler

The main exception handler class that catches and processes all exceptions:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // Handles all types of exceptions and returns consistent responses
}
```

## Exception Types and HTTP Status Codes

| Exception Type | HTTP Status | Use Case |
|----------------|-------------|----------|
| `ResourceNotFoundException` | 404 | Resource not found |
| `BadRequestException` | 400 | Invalid input data |
| `MethodArgumentNotValidException` | 400 | Validation failures |
| `ConstraintViolationException` | 400 | Bean validation errors |
| `MissingServletRequestParameterException` | 400 | Missing required parameters |
| `MethodArgumentTypeMismatchException` | 400 | Parameter type mismatches |
| `HttpMessageNotReadableException` | 400 | Invalid request body |
| `DataIntegrityViolationException` | 400 | Database constraint violations |
| `SQLException` | 500 | Database errors |
| `NoHandlerFoundException` | 404 | Endpoint not found |
| Generic `Exception` | 500 | Unexpected errors |

## Usage Examples

### In Service Layer

```java
@Service
public class VoterService {
    
    public Voter getVoterById(Long id) {
        return voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", "id", id));
    }
    
    public List<Voter> getVotersByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new BadRequestException("City name cannot be null or empty");
        }
        return voterRepository.findByCity(city);
    }
}
```

### In Controller Layer

```java
@RestController
@RequestMapping("/api/voters")
public class VoterController {
    
    @GetMapping("/{id}")
    public ResponseEntity<Voter> getVoterById(@PathVariable Long id) {
        // If voter not found, ResourceNotFoundException will be thrown
        // and automatically handled by GlobalExceptionHandler
        Voter voter = voterService.getVoterById(id);
        return ResponseEntity.ok(voter);
    }
}
```

### Validation with @Valid

```java
@PostMapping
public ResponseEntity<Voter> createVoter(@Valid @RequestBody Voter voter) {
    // If validation fails, MethodArgumentNotValidException will be thrown
    // and automatically handled with detailed validation errors
    Voter savedVoter = voterService.saveVoter(voter);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedVoter);
}
```

## Response Examples

### Resource Not Found (404)
```json
{
  "timestamp": "2025-08-15 19:48:45",
  "status": 404,
  "error": "Not Found",
  "message": "Voter not found with id : '999'",
  "path": "/api/voters/999"
}
```

### Bad Request (400)
```json
{
  "timestamp": "2025-08-15 19:48:45",
  "status": 400,
  "error": "Bad Request",
  "message": "City name cannot be null or empty",
  "path": "/api/voters/city/"
}
```

### Validation Error (400)
```json
{
  "timestamp": "2025-08-15 19:48:45",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid input parameters",
  "path": "/api/voters",
  "validationErrors": {
    "email": "Email is required",
    "name": "Name cannot be empty"
  }
}
```

### Internal Server Error (500)
```json
{
  "timestamp": "2025-08-15 19:48:45",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/voters"
}
```

## Best Practices

### 1. Exception Naming
- Use descriptive exception names that clearly indicate the problem
- Follow the pattern: `[Action][Entity][Problem]Exception`

### 2. Error Messages
- Provide clear, user-friendly error messages
- Include relevant context (e.g., field names, values)
- Avoid exposing sensitive information

### 3. Logging
- Use appropriate log levels (WARN for client errors, ERROR for server errors)
- Include relevant context in log messages
- Avoid logging sensitive data

### 4. HTTP Status Codes
- Use standard HTTP status codes consistently
- 4xx for client errors (bad requests, validation failures)
- 5xx for server errors (database issues, unexpected exceptions)

### 5. Security
- Don't expose internal error details to clients
- Sanitize error messages in production
- Log security-related exceptions appropriately

## Testing

The exception handling mechanism includes comprehensive test coverage:

- **GlobalExceptionHandlerTest**: Tests all exception handlers
- **ResourceNotFoundExceptionTest**: Tests custom exception behavior
- **BadRequestExceptionTest**: Tests custom exception behavior
- **ErrorResponseTest**: Tests error response DTO
- **VoterServiceTest**: Tests service layer exception handling
- **VoterControllerTest**: Tests controller layer exception handling

## Configuration

The exception handling is automatically configured when the application starts. No additional configuration is required.

## Production Considerations

1. **Error Message Sanitization**: Ensure error messages don't expose sensitive information
2. **Logging**: Configure appropriate log levels for production
3. **Monitoring**: Set up monitoring for exception patterns
4. **Rate Limiting**: Consider rate limiting for error endpoints
5. **Security Headers**: Ensure proper security headers are set

## Troubleshooting

### Common Issues

1. **Exception not being caught**: Ensure the exception extends `RuntimeException` or is properly configured
2. **Wrong HTTP status code**: Check the exception handler method for the correct status
3. **Missing error details**: Verify the `ErrorResponse` object is properly populated

### Debug Mode

Enable debug logging to see detailed exception handling:
```properties
logging.level.com.sprints.onlineVotingSystem.exception=DEBUG
```

## Future Enhancements

Potential improvements for the exception handling mechanism:

1. **Internationalization**: Support for multiple languages
2. **Error Codes**: Standardized error codes for client handling
3. **Retry Logic**: Automatic retry for transient failures
4. **Circuit Breaker**: Circuit breaker pattern for external dependencies
5. **Metrics**: Exception metrics and monitoring
