package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleEn: String,
    val titleHi: String,
    val companyEn: String,
    val companyHi: String,
    val category: String, // e.g. "DELIVERY", "DRIVER", "HELPER", "COOK", "MAID", "SECURITY", "CONSTRUCTION", "FACTORY"
    val salaryRangeEn: String,
    val salaryRangeHi: String,
    val locationEn: String,
    val locationHi: String,
    val distanceKm: Double,
    val workHoursEn: String,
    val workHoursHi: String,
    val phone: String,
    val whatsapp: String = "",
    val descriptionEn: String,
    val descriptionHi: String,
    val badgesEn: String, // Comma separated list of badges, e.g. "Immediate join,Free food"
    val badgesHi: String, // Comma separated list in Hindi, e.g. "तुरंत जुड़ें,मुफ़्त खाना"
    val isVerified: Boolean = true,
    val postedDateEn: String = "Today",
    val postedDateHi: String = "आज"
) {
    fun getLocalizedTitle(isHindi: Boolean): String = if (isHindi) titleHi else titleEn
    fun getLocalizedCompany(isHindi: Boolean): String = if (isHindi) companyHi else companyEn
    fun getLocalizedSalary(isHindi: Boolean): String = if (isHindi) salaryRangeHi else salaryRangeEn
    fun getLocalizedLocation(isHindi: Boolean): String = if (isHindi) locationHi else locationEn
    fun getLocalizedWorkHours(isHindi: Boolean): String = if (isHindi) workHoursHi else workHoursEn
    fun getLocalizedDescription(isHindi: Boolean): String = if (isHindi) descriptionHi else descriptionEn
    fun getLocalizedPostedDate(isHindi: Boolean): String = if (isHindi) postedDateHi else postedDateEn

    fun getLocalizedBadges(isHindi: Boolean): List<String> {
        val raw = if (isHindi) badgesHi else badgesEn
        if (raw.isBlank()) return emptyList()
        return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
