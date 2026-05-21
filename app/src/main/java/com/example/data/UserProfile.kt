package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Only 1 logged-in user profile locally
    val name: String = "",
    val phone: String = "",
    val preferredCategory: String = "ALL", // "ALL" or specific category
    val experienceEn: String = "No Experience",
    val experienceHi: String = "कोई अनुभव नहीं",
    val cityEn: String = "Noida",
    val cityHi: String = "नोएडा",
    
    // New fields for supporting high fidelity seeker and giver registrations
    val userType: String = "SEEKER", // "SEEKER" or "GIVER"
    val seekerExperienceYears: String = "Fresher", // "Fresher", "1-2 Years", "3-5 Years", "5+ Years"
    val seekerHasVehicle: String = "None", // "None", "Cycle", "Bike"
    val seekerExpectedSalary: String = "₹10,000 - ₹15,000",
    val seekerEducation: String = "10th Pass", // "Not Studied", "8th Pass", "10th Pass", "12th Pass", "Graduate"
    val companyName: String = "", // For Job Givers / Employers
    val giverContactPerson: String = "", // For Job Givers
    val giverWhatsapp: String = "", // For Job Givers
    val giverIndustry: String = "Household" // "Household", "Shop / Retail", "Factory / Warehouse", "Office", "Restaurant"
)
