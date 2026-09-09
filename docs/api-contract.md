# API Contract

This document outlines the API contracts for the SOM Customer Service Booking Module. Although the application currently runs on an in-memory Mock API (`MockBookingApiService`), it adheres strictly to these defined contracts to ensure seamless replacement with a real backend later.

---

## 1. Get Services
Retrieves a list of available services, optionally filtered by a search query.

- **Method:** `GET`
- **Endpoint:** `/api/v1/services`
- **Query Parameters:**
  - `query` (optional, string): Search term to filter by name, category, or provider.
- **Success Response (200 OK):**
  ```json
  [
    {
      "id": "svc-1",
      "name": "Standard Home Cleaning",
      "category": "Cleaning",
      "provider": "SparkleWorks",
      "price": 2500.0,
      "currency": "NPR",
      "durationMinutes": 90,
      "rating": 4.7,
      "description": "A thorough clean..."
    }
  ]
  ```
- **Empty Response (200 OK):** `[]` (Returns an empty array if no matches are found).
- **Error Response (500 Internal Server Error):**
  ```json
  { "error": "Unable to load services right now. Please try again." }
  ```

---

## 2. Get Service Details
Retrieves detailed information for a specific service.

- **Method:** `GET`
- **Endpoint:** `/api/v1/services/{service_id}`
- **Path Parameters:**
  - `service_id` (required, string)
- **Success Response (200 OK):**
  ```json
  {
    "id": "svc-1",
    "name": "Standard Home Cleaning",
    "provider": "SparkleWorks",
    "price": 2500.0,
    ...
  }
  ```
- **Error Response (404 Not Found):**
  ```json
  { "error": "Service not found." }
  ```

---

## 3. Get Availability
Retrieves a list of available time slots for a specific service on a specific date.

- **Method:** `GET`
- **Endpoint:** `/api/v1/services/{service_id}/availability`
- **Query Parameters:**
  - `date` (required, string, YYYY-MM-DD): The date to check availability for.
- **Success Response (200 OK):**
  ```json
  [
    {
      "id": "svc-1-2026-10-25-09:00",
      "date": "2026-10-25",
      "time": "09:00",
      "available": true
    },
    {
      "id": "svc-1-2026-10-25-10:30",
      "date": "2026-10-25",
      "time": "10:30",
      "available": false
    }
  ]
  ```

---

## 4. Create Booking
Submits a new booking request.

- **Method:** `POST`
- **Endpoint:** `/api/v1/bookings`
- **Request Body:**
  ```json
  {
    "serviceId": "svc-1",
    "slotId": "svc-1-2026-10-25-09:00",
    "date": "2026-10-25",
    "time": "09:00",
    "customerName": "John Doe",
    "contact": "john@example.com"
  }
  ```
- **Success Response (201 Created):**
  ```json
  {
    "id": "uuid-1234",
    "bookingNumber": "SOM-4829",
    "serviceId": "svc-1",
    "serviceName": "Standard Home Cleaning",
    "provider": "SparkleWorks",
    "date": "2026-10-25",
    "time": "09:00",
    "status": "CONFIRMED",
    "customerName": "John Doe",
    "contact": "john@example.com"
  }
  ```
- **Validation Error (400 Bad Request):**
  ```json
  { "error": "Name must be at least 2 characters.", "type": "VALIDATION" }
  ```
- **Business Error - Slot Conflict (409 Conflict):**
  ```json
  { "error": "This time slot was just booked by someone else. Please choose another slot.", "type": "CONFLICT" }
  ```

---

## 5. Get My Bookings
Retrieves a list of all bookings made by the current user.

- **Method:** `GET`
- **Endpoint:** `/api/v1/bookings`
- **Success Response (200 OK):**
  ```json
  [
    {
      "id": "uuid-1234",
      "bookingNumber": "SOM-4829",
      "serviceName": "Standard Home Cleaning",
      "status": "CONFIRMED",
      ...
    }
  ]
  ```
- **Empty Response (200 OK):** `[]` (Returns an empty array if the user has no bookings).
