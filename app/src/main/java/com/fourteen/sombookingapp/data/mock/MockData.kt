package com.fourteen.sombookingapp.data.mock

import com.fourteen.sombookingapp.data.model.Service

/**
 * Hardcoded fake data for the mock API service.
 */
val mockServices = listOf(
    Service(
        id = "svc-1",
        name = "Standard Home Cleaning",
        category = "Cleaning",
        provider = "SparkleWorks",
        price = 2500.0,
        currency = "NPR",
        durationMinutes = 90,
        rating = 4.7,
        description = "A thorough clean of kitchen, bathrooms, and living areas by a vetted professional."
    ),
    Service(
        id = "svc-2",
        name = "Emergency Plumbing Repair",
        category = "Plumbing",
        provider = "FlowFix Co.",
        price = 4500.0,
        currency = "NPR",
        durationMinutes = 60,
        rating = 4.5,
        description = "Fast-response repair for leaks, clogs, and fixture installation."
    ),
    Service(
        id = "svc-3",
        name = "AC Maintenance & Tune-up",
        category = "HVAC",
        provider = "CoolBreeze Techs",
        price = 3500.0,
        currency = "NPR",
        durationMinutes = 75,
        rating = 4.8,
        description = "Filter replacement, coil cleaning, and performance check for home AC units."
    ),
    Service(
        id = "svc-4",
        name = "1-on-1 Math Tutoring",
        category = "Education",
        provider = "BrightPath Tutors",
        price = 2000.0,
        currency = "NPR",
        durationMinutes = 60,
        rating = 4.9,
        description = "Personalized tutoring session for middle and high school math."
    ),
    Service(
        id = "svc-5",
        name = "At-Home Hair Styling",
        category = "Beauty",
        provider = "Glow Studio",
        price = 3000.0,
        currency = "NPR",
        durationMinutes = 50,
        rating = 4.6,
        description = "Wash, cut, and style in the comfort of your home."
    )
)
