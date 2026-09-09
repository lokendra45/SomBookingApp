# Architecture

## Overview

```
Compose UI → ViewModel → Repository → API Service → Mock API
```

I split the app into these layers:

- **UI** — renders state, sends user actions to the ViewModel
- **ViewModel** — holds screen state, calls the Repository
- **Repository** — hides where the data comes from
- **API Service** — interface that defines what operations are available
- **Mock API** — implements the interface in memory, simulates real network responses

## Folder Structure

```
app/
  ui/
    screens/          one folder per screen, screen + ViewModel together
    components/       reusable Compose components
  data/
    api/              API interface and result types
    repository/       Repository interface and implementation
    mock/             Mock API and hardcoded data
    model/            data classes
  di/                 ViewModelFactory
  navigation/         nav graph
docs/
```

## State Management

Each screen has a sealed `UiState` (e.g. `Loading`, `Success`, `Error`). I expose it from the ViewModel as a `StateFlow` and collect it in Compose using `collectAsStateWithLifecycle`.

## How to swap in a real backend

I designed the data layer so `BookingRepository` only depends on the `BookingApiService` interface — it never references `MockBookingApiService` directly. To replace the mock:

1. Create a Retrofit implementation of `BookingApiService`.
2. In `ViewModelFactory`, pass the Retrofit instance instead of `MockBookingApiService`.

Nothing in the UI or ViewModels needs to change.
