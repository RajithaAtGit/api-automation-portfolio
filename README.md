# ✅ API Automation Framework with Java and Rest Assured

This framework provides a robust solution for API test automation with features for authentication, reporting, and test management.

![API Automation Framework Overview](Diagram/img.png)

## Package Structure

![Framework Architecture Diagram](Diagram/mermaid-diagram-2025-04-21-202856.png)
The framework is organized into the following packages:

- `com.gtrxone.auth`: Authentication-related classes (strategies, managers)
- `com.gtrxone.config`: Configuration-related classes (config manager, environment config)
- `com.gtrxone.core`: Core framework classes (base service, request builder, response handler)
- `com.gtrxone.exceptions`: Exception classes (API exceptions, configuration exceptions)
- `com.gtrxone.models`: Model classes (request/response models)
- `com.gtrxone.reporting`: Reporting-related classes (report manager, listeners)
- `com.gtrxone.services`: Service classes (API service implementations)
- `com.gtrxone.utils`: Utility classes (validation, logging)

## Features

### Authentication
The framework supports multiple authentication strategies:
- Basic Authentication
- Bearer Token Authentication
- API Key Authentication
- OAuth 2.0 Authentication

### Reporting
- Allure reporting integration
- Custom report listeners
- Test method execution logging
- Report folder management

### Test Management
- Base test class with lifecycle hooks
- Test method processors
- Test execution logging

### Utilities
- Validation utilities
- Logging utilities
- Configuration management

## Centralized Logging System

The framework uses a centralized logging system based on Log4j 2. This system provides:

- Console logging for immediate feedback
- File logging for persistent records
- Configurable log levels
- Automatic log file rotation and archiving
- Automatic logging of test method execution

Each test method execution is automatically logged to the log file, including:
- When a test method starts
- When a test method completes (with status: PASSED, FAILED, or SKIPPED)
- Any exceptions that occur during test execution

For more details, see the [logs/README.md](logs/README.md) file.

## Custom Reporting Configuration

The framework supports custom reporting folders and maintaining a history of test execution reports for analysis.

### Configuration Properties

The following properties can be configured in `config.properties`:

```properties
# Logging Configuration
logging.directory=.
logging.file.enabled=true

# Reporting Configuration
reporting.logDir=.
reporting.screenshotDir=.
reporting.customResultsDir=.
reporting.maxReportHistory=10
reporting.useTimestampedFolders=true
```

- `logging.directory`: The directory where log files will be stored (set to project root)
- `reporting.logDir`: The directory where report logs will be stored (set to project root)
- `reporting.screenshotDir`: The directory where screenshots will be stored (set to project root)
- `reporting.customResultsDir`: The base directory where custom Allure results will be stored (set to project root)
- `reporting.maxReportHistory`: The maximum number of report folders to keep (only applicable when using timestamped folders)
- `reporting.useTimestampedFolders`: Whether to use timestamped folders for reports (true/false)

## Running Tests

To run the tests, use the following Maven command:

```bash
mvn clean test
```

This will execute all tests defined in the TestNG suite and generate reports in both the default and custom locations.

## Programming Principles and Design Patterns

This framework follows several key programming principles and design patterns to ensure maintainability, extensibility, and robustness:

### Design Patterns

#### Creational Patterns
- **Singleton Pattern**: Used in ConfigManager and AuthenticationManager to ensure a single instance throughout the application lifecycle.
- **Factory Method Pattern**: Implemented in ConfigManager for creating environment configurations and in AuthenticationManager for creating authentication strategies.
- **Builder Pattern**: Used in request construction to create complex API requests with a fluent interface.

#### Structural Patterns
- **Adapter Pattern**: Used to adapt external APIs to the framework's internal interfaces.
- **Facade Pattern**: Provides simplified interfaces to complex subsystems like reporting and authentication.
- **Proxy Pattern**: Implemented for logging and caching mechanisms.

#### Behavioral Patterns
- **Strategy Pattern**: Core to the authentication system, allowing different authentication methods to be interchanged.
- **Observer Pattern**: Used in the reporting system to notify listeners of test events.
- **Template Method Pattern**: Implemented in base test classes to define the skeleton of test execution.
- **Chain of Responsibility**: Used in request/response handling to process requests through multiple handlers.

### Programming Principles

- **SOLID Principles**:
  - **Single Responsibility**: Each class has a single responsibility (e.g., ConfigManager manages only configuration).
  - **Open/Closed**: Classes are open for extension but closed for modification (e.g., authentication strategies).
  - **Liskov Substitution**: Subtypes can be substituted for their base types without altering program correctness.
  - **Interface Segregation**: Clients are not forced to depend on interfaces they don't use.
  - **Dependency Inversion**: High-level modules depend on abstractions, not concrete implementations.

- **DRY (Don't Repeat Yourself)**: Common functionality is extracted into reusable methods and classes.
- **KISS (Keep It Simple, Stupid)**: Complex operations are broken down into simpler, more manageable components.
- **Encapsulation**: Implementation details are hidden behind well-defined interfaces.
- **Composition over Inheritance**: Favoring object composition over class inheritance for more flexible designs.
- **Fail-Fast**: Errors are detected and reported as soon as possible in the development cycle.

These principles and patterns contribute to a framework that is maintainable, extensible, and robust, making it easier to add new features and adapt to changing requirements.

## License

This project is licensed under a Proprietary and Confidential license - see the [LICENSE](LICENSE) file for details.

## Author and Copyright

Author: R N W Gunawardana

Copyright © 2025 R N W Gunawardana. All rights reserved.

## AI Training Restriction Notice

Use of this codebase for training artificial intelligence and machine learning models is strictly prohibited without explicit written permission from R N W Gunawardana. Any unauthorized use for AI/ML training purposes will be considered a violation of intellectual property rights and may result in legal action.
