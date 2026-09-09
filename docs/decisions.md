# Technical Decisions

This document outlines the key technical and architectural decisions made during the development of the SOM Booking Module, focusing on why specific approaches were chosen over alternatives.

## 1. Using a Dedicated Mock API Service Layer
**Decision:** We implemented a `MockBookingApiService` class that strictly implements the `BookingApiService` interface, rather than hardcoding JSON or mock data directly into the ViewModels or UI screens.

**Why it was selected:**
- **Replaceability:** By depending only on the `BookingApiService` interface, the UI and ViewModels are completely decoupled from the data source. When the real backend is ready, we simply swap the dependency injection to provide a Retrofit implementation. Zero UI or ViewModel code will need to change.
- **Realistic Behavior:** The Mock API can simulate latency (`delay()`), HTTP error codes, and dynamic state (like saving a booking in memory so it appears in the "My Bookings" list). Hardcoded JSON cannot do this.

**Alternatives considered:** 
- Hardcoding list data inside `ServiceListViewModel`.
- **Why rejected:** It violates the separation of concerns, makes the app impossible to test cleanly, and requires major refactoring when real APIs are introduced.

## 2. Managing UI State with `StateFlow` and UDF
**Decision:** Every screen is backed by a ViewModel that exposes a single `UiState` data class via a `StateFlow`. We use Unidirectional Data Flow (UDF) where the UI only observes state and sends events.

**Why it was selected:**
- **Predictability:** The UI becomes a pure function of the state. It is impossible for the UI to enter an invalid or conflicting visual state.
- **Lifecycle Safety:** By collecting state using Compose's `collectAsStateWithLifecycle()`, we ensure that background processing and state emission pause when the app is in the background, preventing resource leaks.

**Alternatives considered:** 
- Exposing multiple `LiveData` objects (e.g., `isLoading`, `errorMessage`, `serviceList`).
- **Why rejected:** Multiple observable fields can lead to conflicting states (e.g., `isLoading = true` but `errorMessage` is also populated). A single `UiState` data class guarantees mutually exclusive states.

## 3. Extracting Mock Data into a Separate File (`MockData.kt`)
**Decision:** We extracted the large list of hardcoded mock services out of the `MockBookingApiService` class and into a dedicated `MockData.kt` file.

**Why it was selected:**
- **Readability & Maintenance:** The `MockBookingApiService` is strictly responsible for *behavior* (latency, validation, error throwing). Keeping a 60-line block of data inside it distracted from the business logic. Moving the data out keeps the service class small, focused, and easy to read.

**Alternatives considered:**
- Keeping the data in a `companion object` inside the service.
- **Why rejected:** It bloated the file size and mixed data definition with business logic execution.

## 4. Universal `!contains()` vs Kotlin `!in` operator
**Decision:** We consciously chose to use `!bookedSlotIds.contains(slotId)` instead of the Kotlin-specific `slotId !in bookedSlotIds` operator when checking for slot availability.

**Why it was selected:**
- **Clarity:** While `!in` is idiomatic syntactic sugar in Kotlin, `.contains()` is universally understood across all major programming languages (Java, Python, C#, etc.). It makes the code instantly readable and explicable in a cross-functional team environment.

**Alternatives considered:**
- Using the idiomatic `!in` operator.
- **Why rejected:** It was slightly less explicit when explaining the code logic step-by-step.
