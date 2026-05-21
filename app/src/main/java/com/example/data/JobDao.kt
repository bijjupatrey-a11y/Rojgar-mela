package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Custom helper class for applied jobs listing
data class AppliedJobDetail(
    val id: Int,
    val jobId: Int,
    val appliedAt: Long,
    val statusEn: String,
    val statusHi: String,
    val contactMethod: String,
    val titleEn: String?,
    val titleHi: String?,
    val companyEn: String?,
    val companyHi: String?,
    val salaryRangeEn: String?,
    val salaryRangeHi: String?,
    val distanceKm: Double?,
    val category: String?,
    val phone: String?
) {
    fun getLocalizedTitle(isHindi: Boolean): String = if (isHindi) titleHi ?: "नौकरी" else titleEn ?: "Job"
    fun getLocalizedCompany(isHindi: Boolean): String = if (isHindi) companyHi ?: "नियोक्ता" else companyEn ?: "Employer"
    fun getLocalizedSalary(isHindi: Boolean): String = if (isHindi) salaryRangeHi ?: "" else salaryRangeEn ?: ""
    fun getLocalizedStatus(isHindi: Boolean): String = if (isHindi) statusHi else statusEn
}

@Dao
interface JobDao {

    // --- JOBS QUERY ---
    @Query("SELECT * FROM jobs ORDER BY distanceKm ASC")
    fun getAllJobsFlow(): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: Int): Job?

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getJobsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<Job>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job)


    // --- PROFILE QUERY ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)


    // --- APPLICATIONS QUERY ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: UserApplication)

    @Query("SELECT * FROM user_applications WHERE jobId = :jobId LIMIT 1")
    suspend fun getApplicationForJob(jobId: Int): UserApplication?

    @Query("""
        SELECT 
            a.id as id, 
            a.jobId as jobId, 
            a.appliedAt as appliedAt, 
            a.statusEn as statusEn, 
            a.statusHi as statusHi, 
            a.contactMethod as contactMethod,
            j.titleEn as titleEn,
            j.titleHi as titleHi,
            j.companyEn as companyEn,
            j.companyHi as companyHi,
            j.salaryRangeEn as salaryRangeEn,
            j.salaryRangeHi as salaryRangeHi,
            j.distanceKm as distanceKm,
            j.category as category,
            j.phone as phone
        FROM user_applications a 
        LEFT JOIN jobs j ON a.jobId = j.id
        ORDER BY a.appliedAt DESC
    """)
    fun getAppliedJobDetailsFlow(): Flow<List<AppliedJobDetail>>
}
