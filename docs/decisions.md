# Decisions

## 1. Mock API as a proper interface implementation

I made `MockBookingApiService` implement the `BookingApiService` interface, the same way a Retrofit service would. This means I can replace it with a real backend by changing one line in `ViewModelFactory` — nothing else needs to change.

I considered just hardcoding data inside the ViewModel, but that would mix data and presentation and make it impossible to test properly.

---

## 2. Single `UiState` sealed class per screen

Each screen has one sealed class (e.g. `ServiceListUiState`) covering all possible states: `Loading`, `Success`, `Error`. I expose it as one `StateFlow<UiState>` from the ViewModel.

This prevents conflicting states, like showing a loading spinner and an error message at the same time, which can happen when you have multiple separate state variables instead of one sealed class.

---

## 3. Mock data in a separate file

I moved the hardcoded services list into `MockData.kt` instead of keeping it inside `MockBookingApiService`.

This keeps the service class focused on behavior (simulating latency, errors, slot conflicts) and makes the data easy to find and update separately.

---

## 4. `StateFlow` with `WhileSubscribed`

I used `stateIn(WhileSubscribed(5_000))` so the upstream flow stops when no UI is collecting (e.g. the app goes to the background). This avoids doing unnecessary work and follows Android's recommended lifecycle practices.
