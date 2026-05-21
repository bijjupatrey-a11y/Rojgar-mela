package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppliedJobDetail
import com.example.data.Job
import com.example.data.JobRepository
import com.example.data.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface JobUiState {
    object Loading : JobUiState
    data class Success(
        val jobs: List<Job>,
        val categories: List<String>,
        val currentCategory: String,
        val distanceFilter: Double, // in KM, 0.0 means "all"
        val searchQuery: String,
        val isHindi: Boolean,
        val profile: UserProfile?,
        val appliedJobs: List<AppliedJobDetail>
    ) : JobUiState
}

class JobViewModel(private val repository: JobRepository) : ViewModel() {

    private val _isHindi = MutableStateFlow(true) // Hindi by default as requested
    val isHindi: StateFlow<Boolean> = _isHindi.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL") // "ALL" or "DRIVER", etc.
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _distanceFilter = MutableStateFlow(0.0) // 0.0 = All
    val distanceFilter: StateFlow<Double> = _distanceFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            repository.populatePredefinedJobsIfEmpty()
            repository.ensureProfileExists()
        }
    }

    // Combine flows to produce a responsive Success/Loading UI State
    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<JobUiState> = combine(
        listOf(
            repository.allJobs,
            repository.userProfile,
            repository.appliedJobs,
            _isHindi,
            _selectedCategory,
            _distanceFilter,
            _searchQuery
        )
    ) { array ->
        val jobs = array[0] as List<Job>
        val profile = array[1] as UserProfile?
        val appliedJobs = array[2] as List<AppliedJobDetail>
        val isHindi = array[3] as Boolean
        val category = array[4] as String
        val distance = array[5] as Double
        val search = array[6] as String
        
        // Apply filters
        val filteredJobs = jobs.filter { job ->
            val matchCategory = category == "ALL" || job.category.equals(category, ignoreCase = true)
            val matchDistance = distance == 0.0 || job.distanceKm <= distance
            val matchSearch = if (search.isBlank()) {
                true
            } else {
                job.titleEn.contains(search, ignoreCase = true) ||
                job.titleHi.contains(search) ||
                job.companyEn.contains(search, ignoreCase = true) ||
                job.companyHi.contains(search) ||
                job.locationEn.contains(search, ignoreCase = true) ||
                job.locationHi.contains(search)
            }
            matchCategory && matchDistance && matchSearch
        }

        // Distinct category list from the raw jobs list to avoid hardcoding completely
        val allCategories = listOf("ALL") + jobs.map { it.category }.distinct()

        JobUiState.Success(
            jobs = filteredJobs,
            categories = allCategories,
            currentCategory = category,
            distanceFilter = distance,
            searchQuery = search,
            isHindi = isHindi,
            profile = profile,
            appliedJobs = appliedJobs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JobUiState.Loading
    )

    fun toggleLanguage() {
        _isHindi.value = !_isHindi.value
    }

    fun setLanguage(hindi: Boolean) {
        _isHindi.value = hindi
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setDistanceFilter(distance: Double) {
        _distanceFilter.value = distance
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun applyToJob(jobId: Int, contactMethod: String) {
        viewModelScope.launch {
            repository.applyToJob(jobId, contactMethod)
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun postJob(job: Job) {
        viewModelScope.launch {
            repository.postJob(job)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: JobRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(JobViewModel::class.java)) {
                return JobViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
