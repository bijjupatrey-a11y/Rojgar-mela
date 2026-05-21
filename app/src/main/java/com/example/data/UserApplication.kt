package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_applications")
data class UserApplication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobId: Int,
    val appliedAt: Long = System.currentTimeMillis(),
    val statusEn: String = "Applied",
    val statusHi: String = "आवेदन किया",
    val contactMethod: String = "Call" // "Call" or "WhatsApp"
) {
    fun getLocalizedStatus(isHindi: Boolean): String = if (isHindi) statusHi else statusEn
}
