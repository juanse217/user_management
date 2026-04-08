# User Management Service - Architectural Best Practices

This document compiles the senior architectural decisions and feedback implemented during the code review phase of this project. The goal is to enforce rigid architectural boundaries, concurrent safety, and correct framework semantic utilization.

## 1. Atomic Database Constraints (Preventing the Check-Then-Act Race Condition)

**The Flaw:** Utilizing a sequential validation algorithm—checking `if (!repo.existsByEmail())` before executing `repo.save()`—creates a lethal Time-Of-Check to Time-Of-Use (TOCTOU) vulnerability. Under concurrent loads, identical requests pass the memory validation before either reaches the database, culminating in an unchecked `DuplicateKeyException` server crash.

**The Solution:** The database is the ultimate arbiter of truth. By defining `@Indexed(unique = true)` on entity fields (like `username` and `email`), the application can simply attempt a blind atomic write. If a collision occurs, the database immediately bounces the transaction, and the service catches the `org.springframework.dao.DuplicateKeyException`. Once caught, exact sequential existence queries determine the specific source of the collision to produce a highly accurate error message. This design asks for forgiveness instead of permission, providing 100% thread safety without manual locking arrays.

## 2. Decoupling Web Validation from Serialization Constraints

**The Flaw:** Utilizing JSR-380 input validation annotations (such as `@Null(groups = OnRead.class)`) solely to mask fields from outgoing JSON REST payloads improperly couples validation frameworks to Jackson serializations.

**The Solution:** Input validation ensures systemic data integrity on entry. Outgoing payload mutation is fundamentally the responsibility of the JSON Marshaller. Implementing the `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` mechanism surgically obscures fields like passwords from HTTP responses without abusing memory validation constraints.

## 3. Resolving Validation Group Assignments 

**The Flaw:** Annotating fields with specific grouping execution (e.g., `@NotBlank(groups = OnCreate.class)`) renders those constraints totally dormant if the controller is utilizing the standard Java EE `@Valid` boundary injection which defaults to baseline constraints. 

**The Solution:** Using the Spring context-specific `@Validated(OnCreate.class)` directly within the REST parameter boundary successfully activates explicit conditional constraints based on endpoint intention, ensuring proper payload validation.

## 4. DTO Coupling & Boundary Leakage (Context Decoupling)

**The Flaw:** A Data Transfer Object (DTO) directly utilizing an inner class nested within a persistent Document entity (e.g., `User.Configuration`) circumvents layered architectures. Sharding structures or schema modifications within the underlying database layer instantly shatter the external APIs using those exact mappings.

**The Solution:** Abstracting embedded data boundaries into an infrastructural `shared/` package insulates HTTP transmission contracts from database layer mutations. It ensures DTO boundaries only reflect required network payloads.

## 5. Elimination of Fat Controllers (Single Responsibility Principle)

**The Flaw:** Permitting web controllers to dictate process logic, such as inspecting parameters for nullity and manually routing branching deletion methodologies, firmly bolts business logic workflows onto identical network transport mechanics.

**The Solution:** The `@RestController` functions solely as a transmission layer. By establishing distinct discrete endpoints (`/deletion/username` and `/deletion/email`) the application transfers process ownership and logic entirely into the core `Service`, facilitating complete decoupling and re-usability (e.g., executing deletions from asynchronous message queues seamlessly).

## 6. Query Derivation Re-usability

**The Flaw:** Hand-crafting descriptive repository derivations (e.g., `Slice<User> findAllUsersOrderByUsername(Pageable)`) immediately conflicts with dynamic controller parameters manipulating the pagination object, forcing Spring to attempt mapping contradictory sorting directives.

**The Solution:** Capitalize on `PagingAndSortingRepository` abstracts. Invoking the inherited and intrinsically generic `findAll(Pageable)` directly utilizes dynamic memory parameters mapped flawlessly by external controllers without the need for bespoke method generation.
