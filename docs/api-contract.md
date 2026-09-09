# API Contract

I defined these endpoints to match what a real SOM backend would provide. The mock API implements the same behavior in memory.

Base URL (production, not built): `https://api.som.com/api/v1`

---

## GET /services

Returns a list of services. I can filter by passing an optional `query` param.

**200 OK**
```json
[
  {
    "id": "svc-1",
    "name": "Home Cleaning",
    "category": "Cleaning",
    "provider": "SparkleWorks",
    "price": 1500.0,
    "currency": "NPR",
    "durationMinutes": 90,
    "rating": 4.7,
    "description": "Full home cleaning service."
  }
]
```

---

## GET /services/{id}

Returns details of a single service.

**404** if the service doesn't exist.

---

## GET /services/{id}/availability?date=YYYY-MM-DD

Returns the time slots for a service on a given date. Already-booked slots are marked as `available: false`.

**200 OK**
```json
[
  { "id": "svc-1-2026-10-25-09:00", "date": "2026-10-25", "time": "09:00", "available": true },
  { "id": "svc-1-2026-10-25-10:30", "date": "2026-10-25", "time": "10:30", "available": false }
]
```

---

## POST /bookings

Creates a new booking.

**Request body:**
```json
{
  "serviceId": "svc-1",
  "slotId": "svc-1-2026-10-25-09:00",
  "date": "2026-10-25",
  "time": "09:00",
  "customerName": "Ram Bahadur",
  "contact": "9800000000"
}
```

**201 Created**
```json
{
  "id": "b-1",
  "bookingNumber": "SOM-4829",
  "serviceName": "Home Cleaning",
  "provider": "SparkleWorks",
  "date": "2026-10-25",
  "time": "09:00",
  "status": "CONFIRMED",
  "customerName": "Ram Bahadur",
  "contact": "9800000000"
}
```

**400** — validation error (e.g. missing name)  
**409** — slot was already booked

---

## GET /bookings

Returns my booking history.

**200 OK** — list of bookings, or `[]` if I have none.
