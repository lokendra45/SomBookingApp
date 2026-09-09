# SOM Customer Service Booking Module

A native Android application demonstrating a customer-side service discovery and booking flow for SOM. This project focuses on clean architecture, proper state management, and separation of concerns using modern Android development practices.

## Features
- **Service Discovery:** Browse a list of available services and search by name, category, or provider.
- **Service Details:** View rich details about a service, including price, duration, and ratings.
- **Availability Scheduling:** Select a date and view available time slots simulated realistically.
- **Booking Flow:** Validate required customer info and submit a booking, handling simulated API conflicts.
- **My Bookings:** View a list of successfully scheduled bookings.

## Technical Highlights
- **Language:** 100% Kotlin.
- **UI Toolkit:** Jetpack Compose (Modern declarative UI).
- **Architecture:** MVVM + Clean Architecture (`UI -> ViewModel -> Repository -> API`).
- **State Management:** Unidirectional Data Flow using `StateFlow` and single `UiState` classes.
- **Concurrency:** Kotlin Coroutines.
- **API Simulation:** A robust `MockBookingApiService` that realistically simulates network latency, validation failures, HTTP error states, and slot conflicts.

## Documentation
Please refer to the `docs/` folder for comprehensive documentation required by the assignment:
- [Architecture Overview](docs/architecture.md)
- [API Contract](docs/api-contract.md)
- [Technical Decisions](docs/decisions.md)
- [Setup & Build Instructions](docs/setup.md)

## Demo Video
*(Replace this placeholder with a link to your screen recording)*
[Link to Demo Video](#)

## Installation
See [Setup Instructions](docs/setup.md) for how to build the project from source or install the generated APK.
