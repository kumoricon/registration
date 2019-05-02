# Object/Class Types

By convention, different object types are used. This guide should help you 
understand them.

- _____ (a specific object)
  - Examples: Attendee, Badge, Order, Role
  - Internal representation of a business object. This usually closely mirrors the database 
    table structure.

- _____ DTO
  - Examples: SessionInfoDTO, PaymentFormDTO, RoleDTO, AttendeeListDTO
  - Data Transfer Objects represent a combination of business objects needed by the user interface.
    For example, AttendeeListDTO contains only a small portion of the full Attendee object. They
    may also include information from different database tables, such as the Payemnt information
    from the `payments` table combined with username from the `users` table.  

- _____ Repository
  - Examples: AttendeeRepository, OrderRepository, BadgeRepository
  - Handles loading a specific kind of object from the database. Contains the SQL commands and
    an internal class that will map SQL results to objects.

- _____ Service
  - Examples: AttendeeService, OrderService
  - Handles loading or processing more complex objects. For example, an Order has associated
    attendees and payments. Or, AttendeeService will handle adding an entry to the AttendeeHistory 
    table when an attendee checks in.

- _____ Controller
  - Examples: RolesController, AttendeeController, TillReportController
  - Middle layer between the user interface (HTML templates) and business logic services/data
    repositories. Also contians some business logic where that's simpler. (In a more strict architecture,
    *all* business logic would be in Service classes)

- _____ ControllerAdvice
  - Examples: CookieControllerAdvice, MessageControllerAdvice
  - Runs for all Controllers, such as reading values from Cookies and inserting them in to the Model objects
    before each controller runs, or parsing the `msg` and `err` query parameters and adding their values to 
    the Model. (Model objects are used to pass data to front end HTML templates)
  
- _____ Application
  - Examples: RegistrationApplication
  - Base class responsible for starting the service.

- _____ Config
  - Examples: SpringWebMvcConfig, WebSecurityConfig
  - Classes used to configure some aspect of Spring, such as which pages are password protected.

- _____ Task
  - Examples: LoginTrackerTask
  - A scheduled task that runs at a certain time interval. For example, LoginTrackerTask calls LoginRepository to
  checks all the currently open login sessions and write their metadata to the loginsessions table, which is
  used to track which users were logged in at a given time.
