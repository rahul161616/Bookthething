# Contributing to Bookthething

Thank you for your interest in contributing to Bookthething! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Making Changes](#making-changes)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Architecture Guidelines](#architecture-guidelines)

## Code of Conduct

This project adheres to a code of conduct that we expect all contributors to follow:

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on constructive feedback
- Respect different opinions and approaches

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- Java 21 or higher
- Maven 3.9+
- PostgreSQL 12+
- Git
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Development Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/your-username/bookthething.git
   cd bookthething
   ```

2. **Run Setup Script**
   ```bash
   ./setup.sh
   ```

3. **Configure Environment**
   ```bash
   cp .env.example .env
   # Update .env with your local settings
   ```

4. **Start Services**
   ```bash
   ./start-services.sh
   ```

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and concise (max 30 lines)
- Add JavaDoc comments for public methods and classes
- Use Spring Boot best practices

### Example Code Style

```java
/**
 * Service for managing booking operations.
 */
@Service
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    
    /**
     * Creates a new booking.
     * 
     * @param booking the booking to create
     * @return the created booking with generated ID
     * @throws BookingException if booking validation fails
     */
    public Booking createBooking(Booking booking) {
        validateBooking(booking);
        return bookingRepository.save(booking);
    }
}
```

### Package Structure

Follow this package structure for each service:

```
com.jugger.[service-name]
├── controller/     # REST controllers
├── service/        # Business logic
├── repository/     # Data access layer
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── config/         # Configuration classes
├── exception/      # Custom exceptions
└── util/           # Utility classes
```

## Making Changes

### Branch Naming

Use descriptive branch names:

- `feature/user-authentication`
- `bugfix/jwt-validation-issue`
- `hotfix/security-vulnerability`
- `docs/api-documentation`

### Commit Messages

Write clear, descriptive commit messages:

```
feat: add user authentication endpoint

- Implement JWT-based authentication
- Add login and logout functionality
- Include input validation and error handling

Closes #123
```

Use these prefixes:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks

## Testing

### Writing Tests

All new features must include tests:

1. **Unit Tests**
   ```java
   @ExtendWith(MockitoExtension.class)
   class BookingServiceTest {
       
       @Mock
       private BookingRepository bookingRepository;
       
       @InjectMocks
       private BookingService bookingService;
       
       @Test
       void shouldCreateBooking() {
           // Given
           Booking booking = new Booking();
           when(bookingRepository.save(any())).thenReturn(booking);
           
           // When
           Booking result = bookingService.createBooking(booking);
           
           // Then
           assertThat(result).isNotNull();
           verify(bookingRepository).save(booking);
       }
   }
   ```

2. **Integration Tests**
   ```java
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   @TestPropertySource(properties = {
       "spring.datasource.url=jdbc:h2:mem:testdb"
   })
   class BookingControllerIntegrationTest {
       
       @Autowired
       private TestRestTemplate restTemplate;
       
       @Test
       void shouldCreateBookingSuccessfully() {
           // Test implementation
       }
   }
   ```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests for specific service
cd api-gateway
./mvnw test

# Run with coverage
./mvnw clean test jacoco:report
```

## Submitting Changes

### Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes and Test**
   ```bash
   # Make your changes
   ./mvnw clean test  # Ensure tests pass
   ```

3. **Commit Changes**
   ```bash
   git add .
   git commit -m "feat: your descriptive message"
   ```

4. **Push and Create PR**
   ```bash
   git push origin feature/your-feature-name
   ```
   Then create a pull request on GitHub.

### Pull Request Checklist

Before submitting, ensure:

- [ ] Code follows project coding standards
- [ ] All tests pass
- [ ] New features include appropriate tests
- [ ] Documentation is updated if needed
- [ ] Commit messages are clear and descriptive
- [ ] Branch is up to date with main
- [ ] No sensitive information is committed

## Architecture Guidelines

### Microservices Principles

1. **Single Responsibility**: Each service should have one business responsibility
2. **Loose Coupling**: Services should be independently deployable
3. **High Cohesion**: Related functionality should be in the same service
4. **Database per Service**: Each service should have its own database

### API Design

Follow RESTful principles:

```java
@RestController
@RequestMapping("/api/v1/bookings")
@Validated
public class BookingController {
    
    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookings() {
        // Implementation
    }
    
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        // Implementation
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        // Implementation
    }
}
```

### Error Handling

Use consistent error responses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(BookingNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "BOOKING_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### Security Guidelines

1. **Input Validation**: Validate all inputs
2. **Authentication**: Use JWT for authentication
3. **Authorization**: Implement proper access controls
4. **Secrets Management**: Use environment variables for secrets

## Documentation

### API Documentation

Use OpenAPI/Swagger annotations:

```java
@Operation(summary = "Create a new booking")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Booking created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
@PostMapping
public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest request) {
    // Implementation
}
```

### README Updates

When adding new features:
- Update the main README.md
- Add configuration instructions
- Update API documentation
- Include examples if needed

## Getting Help

If you need help:

1. Check existing issues and discussions
2. Ask questions in issue comments
3. Join our development chat (if available)
4. Tag maintainers in your PR for review

## Recognition

Contributors will be recognized in:
- CONTRIBUTORS.md file
- Release notes
- Project documentation

Thank you for contributing to Bookthething! 🎉