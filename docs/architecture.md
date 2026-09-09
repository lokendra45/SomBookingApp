# Architecture Overview

The SOM Booking App uses a modern Android architecture based on Google's recommended app architecture, ensuring a strict separation of concerns, unidirectional data flow (UDF), and highly testable, predictable behavior.

## Core Layers & Responsibilities

The application maintains a clear separation structured as:
`Compose UI → ViewModel → Repository → API Service → Mock API`

### 1. UI Layer (Jetpack Compose)
- **Role:** Displays application state and captures user input.
- **Rules:** 
  - Cannot contain business logic.
  - Cannot directly instantiate Repositories or APIs.
  - Must observe state via `lifecycle-aware state collection` (e.g., `collectAsStateWithLifecycle()`).
- **Examples:** `ServiceListScreen`, `BookingScreen`.

### 2. Presentation Layer (ViewModel)
- **Role:** Owns and manages the screen's state (`UiState`) and coordinates user actions with the data layer.
- **Rules:**
  - Maintains state in a `StateFlow` to ensure UDF.
  - Exposes actions/intents that the UI can call (e.g., `onSearchQueryChanged()`, `submitBooking()`).
  - Converts data layer results (like `ApiResult`) into UI-friendly state (Loading, Success, Error).
- **Examples:** `ServiceListViewModel`, `BookingViewModel`.

### 3. Data Layer (Repository)
- **Role:** Provides an application-facing data boundary and abstracts the origin of the data.
- **Rules:**
  - Translates network responses into domain models.
  - Catches raw exceptions (like network timeouts or serialization errors) and converts them into safe `ApiResult.Error` wrappers, preventing crashes.
- **Examples:** `BookingRepository`, `BookingRepositoryImpl`.

### 4. Network Layer (API Service & Mock API)
- **Role:** Defines the API contracts and handles remote data fetching.
- **Rules:**
  - `BookingApiService` defines the pure interface contracts.
  - `MockBookingApiService` implements this interface completely in-memory, simulating network latency, API success, and exact HTTP-style error structures (like 404 Not Found or 409 Conflict).

---

## State Management
State is managed using the **Unidirectional Data Flow (UDF)** pattern:
1. **State flows down:** The `ViewModel` exposes a single `UiState` data class via a `StateFlow`. The Compose UI observes this state and recomposes when it changes.
2. **Events flow up:** When a user interacts with the UI (e.g., clicks "Book"), the UI calls a method on the `ViewModel`. The `ViewModel` then updates its internal state (e.g., `isLoading = true`) and interacts with the Repository.

---

## How to Replace the Mock API
The architecture is designed to be completely decoupled from the mock implementation. 

To replace the `MockBookingApiService` with a real backend:
1. Create a real `Retrofit` service that implements the `BookingApiService` interface (or a similar interface).
2. Update the `ViewModelFactory` (or your Dependency Injection module, like Hilt/Dagger) to provide the real `Retrofit` implementation to the `BookingRepositoryImpl` instead of instantiating `MockBookingApiService`.

Because the UI and ViewModels only depend on the `BookingRepository` interface, **zero changes** are required in the presentation or UI layers to switch from Mock to Production.
