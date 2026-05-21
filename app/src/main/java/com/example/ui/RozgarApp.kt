package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppliedJobDetail
import com.example.data.Job
import com.example.data.UserProfile
import com.example.ui.theme.*
import androidx.compose.foundation.border
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CurvedHeader(
    isHi: Boolean,
    onLanguageToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 28.dp)
            .testTag("app_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Group: Rounded work emblem + Title block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Work Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Column {
                    Text(
                        text = if (isHi) "रोज़गार मेला" else "Rozgar Mela",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isHi) "नोएडा और दिल्ली एनसीआर" else "Noida & Delhi NCR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            
            // Right Group: High-contrast bivernacular pill
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.28f), CircleShape)
                    .clickable { onLanguageToggle() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("language_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isHi) "EN" else "हिंदी",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RozgarApp(viewModel: JobViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Bottom Navigation Screens
    var currentTab by remember { mutableStateOf("jobs") } // "jobs", "profile", "applications"
    
    // Detailed dialog tracker
    var selectedJobForDetails by remember { mutableStateOf<Job?>(null) }
    
    // Contact prompt tracker
    var contactingJob by remember { mutableStateOf<Job?>(null) }

    when (val state = uiState) {
        is JobUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("loading_indicator")
                )
            }
        }
        is JobUiState.Success -> {
            val isHi = state.isHindi

            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == "jobs",
                            onClick = { currentTab = "jobs" },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Jobs") },
                            label = { Text(if (isHi) "नौकरियां" else "Find Jobs") },
                            modifier = Modifier.testTag("nav_jobs")
                        )
                        NavigationBarItem(
                            selected = currentTab == "profile",
                            onClick = { currentTab = "profile" },
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                            label = { Text(if (isHi) "प्रोफाइल" else "My Profile") },
                            modifier = Modifier.testTag("nav_profile")
                        )
                        NavigationBarItem(
                            selected = currentTab == "applications",
                            onClick = { currentTab = "applications" },
                            icon = { Icon(Icons.Default.List, contentDescription = "Applications") },
                            label = { Text(if (isHi) "आवेदन" else "Applied") },
                            modifier = Modifier.testTag("nav_applications")
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Curved Header
                    CurvedHeader(
                        isHi = isHi,
                        onLanguageToggle = { viewModel.toggleLanguage() }
                    )

                    when (currentTab) {
                        "jobs" -> {
                            JobsTabContent(
                                state = state,
                                viewModel = viewModel,
                                onJobClick = { selectedJobForDetails = it },
                                onApplyClick = { contactingJob = it }
                            )
                        }
                        "profile" -> {
                            ProfileTabContent(
                                profile = state.profile,
                                isHindi = isHi,
                                onSaveProfile = { updated ->
                                    viewModel.updateProfile(updated)
                                    Toast.makeText(context, if (isHi) "प्रोफ़ाइल सुरक्षित हो गई!" else "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
                                },
                                onPostJob = { newJob ->
                                    viewModel.postJob(newJob)
                                    Toast.makeText(context, if (isHi) "नया काम सफलतापूर्वक पोस्ट किया गया!" else "Job Posted Successfully!", Toast.LENGTH_LONG).show()
                                    currentTab = "jobs"
                                }
                            )
                        }
                        "applications" -> {
                            ApplicationsTabContent(
                                appliedJobs = state.appliedJobs,
                                isHindi = isHi
                            )
                        }
                    }
                }
            }

            // --- JOB DETAIL MODAL SHEET ---
            selectedJobForDetails?.let { job ->
                JobDetailsDialog(
                    job = job,
                    isHindi = isHi,
                    onDismiss = { selectedJobForDetails = null },
                    onApply = {
                        selectedJobForDetails = null
                        contactingJob = job
                    },
                    hasApplied = state.appliedJobs.any { it.jobId == job.id }
                )
            }

            // --- CONTACT & APPLY DIALOG ---
            contactingJob?.let { job ->
                ContactActionDialog(
                    job = job,
                    isHindi = isHi,
                    onDismiss = { contactingJob = null },
                    onCallAction = { method ->
                        contactingJob = null
                        // Log Application locally to DB
                        viewModel.applyToJob(job.id, method)
                        
                        // Handle Native Action Intents
                        if (method == "Call") {
                            sendDialerIntent(context, job.phone)
                        } else {
                            sendWhatsAppIntent(context, job.phone, job.getLocalizedTitle(isHi), job.getLocalizedCompany(isHi))
                        }
                    }
                )
            }
        }
    }
}

// --- JOBS TAB SCREEN ---
@Composable
fun JobsTabContent(
    state: JobUiState.Success,
    viewModel: JobViewModel,
    onJobClick: (Job) -> Unit,
    onApplyClick: (Job) -> Unit
) {
    val isHi = state.isHindi

    // 1. Alert profile card if profile name or number is empty
    val isProfileIncomplete = state.profile?.name.isNullOrBlank() || state.profile?.phone.isNullOrBlank()

    Column(modifier = Modifier.fillMaxSize()) {
        
        // Profile Info Prompt Card
        AnimatedVisibility(visible = isProfileIncomplete) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("profile_warning_card"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Alert",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHi) "नियोक्ताओं से फ़ोन कॉल पाने के लिए अपनी प्रोफ़ाइल पूरी करें" else "Complete Profile to get response from owners!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = if (isHi) "नाम और मोबाइल नंबर भरना अनिवार्य है" else "Your contact number is mandatory to apply.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // 2. Search & Hyperlocal Filter Row
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { 
                Text(
                    text = if (isHi) "काम खोजें (जैसे: ड्राइवर, हेल्पर)..." else "Search jobs (e.g. driver, helper)...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ) 
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.primary
                ) 
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // 3. Category Scrolling Bar
        Text(
            text = if (isHi) "काम की श्रेणी चुनें (Category)" else "Choose Work Category",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            items(state.categories) { categoryKey ->
                val categoryDisplayName = getCategoryDisplayName(categoryKey, isHi)
                val categoryIcon = getCategoryIcon(categoryKey)
                val isSelected = state.currentCategory == categoryKey

                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(categoryKey) },
                    label = { 
                        Text(
                            text = categoryDisplayName, 
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = categoryDisplayName,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("category_chip_$categoryKey")
                )
            }
        }

        // 4. GPS Status & Live Nearby Job Counter Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Glowing GPS Pulse dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = if (isHi) "GPS सक्रिय (Live Sector 62, Noida)" else "Live GPS: Sector 62, Noida",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF0F9D58)
                )
            }
            Text(
                text = if (isHi) "कुल: ${state.jobs.size} पास में" else "Found ${state.jobs.size} nearby",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Hyperlocal Quick Sector/Locality Selector Row
        Text(
            text = if (isHi) "अपना नजदीकी सेक्टर चुनें (Change Sector)" else "Select Nearest Locality",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        val sectors = listOf(
            "" to (if (isHi) "सभी जगह" else "All Areas"),
            "62" to (if (isHi) "सेक्टर 62" else "Sec 62"),
            "63" to (if (isHi) "सेक्टर 63" else "Sec 63"),
            "Indirapuram" to (if (isHi) "इंदिरापुरम" else "Indirapuram"),
            "76" to (if (isHi) "सेक्टर 76" else "Sec 76"),
            "Sector 4" to (if (isHi) "सेक्टर 4" else "Sec 4"),
            "50" to (if (isHi) "नोएडा 50" else "Sec 50")
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            items(sectors) { (queryVal, label) ->
                val isSelected = if (queryVal.isEmpty()) state.searchQuery.isEmpty() else state.searchQuery.contains(queryVal, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSearchQuery(queryVal) },
                    label = { 
                        Text(
                            text = label, 
                            fontSize = 11.sp, 
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // 5. Walkable & Proximity Radius Limits
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isHi) "रेंज चुनें:" else "Range:",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )

            val distanceOptions = listOf(
                0.0 to (if (isHi) "सभी" else "All"),
                1.5 to (if (isHi) "🚶 पैदल (<1.5 KM)" else "🚶 Walkable"),
                3.0 to (if (isHi) "नजदीक (<3 KM)" else "Nearby"),
                5.0 to (if (isHi) "<5 KM" else "<5 KM")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                distanceOptions.forEach { (dist, label) ->
                    val isSelected = state.distanceFilter == dist
                    SuggestionChip(
                        onClick = { viewModel.setDistanceFilter(dist) },
                        label = { 
                            Text(
                                text = label, 
                                fontSize = 11.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        border = if (isSelected) {
                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            } else {
                                Color.Transparent
                            },
                            labelColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("distance_chip_${dist.toInt()}")
                    )
                }
            }
        }

        // 5. Jobs list
        if (state.jobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHi) "कोई नौकरी नहीं मिली" else "No jobs found",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isHi) "कृपया श्रेणी, दूरी या सर्च शब्द को बदलें!" else "Please try altering filters or search criteria.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp, start = 12.dp, end = 12.dp, top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("job_list")
            ) {
                items(state.jobs, key = { it.id }) { job ->
                    val alreadyApplied = state.appliedJobs.any { it.jobId == job.id }

                    JobCard(
                        job = job,
                        isHindi = isHi,
                        alreadyApplied = alreadyApplied,
                        onClick = { onJobClick(job) },
                        onApplyClick = { onApplyClick(job) }
                    )
                }
            }
        }
    }
}

// --- SINGLE JOB CARD COMPONENT ---
@Composable
fun JobCard(
    job: Job,
    isHindi: Boolean,
    alreadyApplied: Boolean,
    onClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("job_card_${job.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Employer & Title Row with Left Monogram Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Visually beautiful Company Monogram Icon
                val monogramLetter = job.companyEn.firstOrNull()?.toString()?.uppercase() ?: "J"
                val (bgCol, txtCol) = when (job.category.uppercase()) {
                    "DELIVERY" -> Color(0xFFFFEFE2) to Color(0xFFD84B16) // Orange accent
                    "DRIVER" -> Color(0xFFE3F2FD) to Color(0xFF0D47A1)  // Blue accent
                    "HELPER" -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)  // Purple accent
                    "SECURITY" -> Color(0xFFECEFF1) to Color(0xFF37474F) // Gray accent
                    "FACTORY" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)  // Green accent
                    "COOK" -> Color(0xFFFFFDE7) to Color(0xFFF57F17)    // Yellow accent
                    "MAID" -> Color(0xFFFCE4EC) to Color(0xFFC2185B)    // Pink accent
                    else -> Color(0xFFE0F2F2) to Color(0xFF006A6A)       // Teal default
                }
                
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgCol),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = monogramLetter,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = txtCol
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 2. Company Name and Title
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = job.getLocalizedCompany(isHindi),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        if (job.isVerified) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isHindi) "सत्यापित" else "Verified",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = job.getLocalizedTitle(isHindi),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 3. Status/Urgency Badge Row on Right Corner
                if (job.id == 1 || job.id % 3 == 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(UrgentBg)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isHindi) "शीघ्र आवश्यक" else "URGENT",
                            color = CoralUrgent,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Salary & Hyperlocal Proximity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // High contrast emerald salary tag
                Text(
                    text = job.getLocalizedSalary(isHindi),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldSalary
                )

                // Distance ticker tag (Hyperlocal Proximity Indicators with Walkability support)
                val isWalkable = job.distanceKm <= 1.5
                val distanceBgColor = if (isWalkable) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer
                val distanceTextColor = if (isWalkable) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(distanceBgColor)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (isWalkable) {
                            "🚶 ${job.distanceKm} ${if (isHindi) "किमी (पैदल दूरी)" else "KM (Walkable)"}"
                        } else {
                            "📍 ${job.distanceKm} ${if (isHindi) "किमी दूर" else "KM away"}"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = distanceTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compact Working hours & location parameters
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = job.getLocalizedLocation(isHindi),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = job.getLocalizedWorkHours(isHindi),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges row
            val badges = job.getLocalizedBadges(isHindi)
            if (badges.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    badges.take(3).forEach { badge ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(10.dp))

            // Quick View details and Quick Contact call button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.getLocalizedPostedDate(isHindi),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                // High visual weight CALL & APPLY Button
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("apply_button_${job.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (alreadyApplied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(19.dp)
                ) {
                    Icon(
                        imageVector = if (alreadyApplied) Icons.Default.CheckCircle else Icons.Default.Phone,
                        contentDescription = "Apply/Call",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (alreadyApplied) {
                            if (isHindi) "आवेदन किया!" else "Applied!"
                        } else {
                            if (isHindi) "कॉल करें / बात करें" else "Call Owner"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// --- JOB DETAIL MODAL DESCRIPTION BOX ---
@Composable
fun JobDetailsDialog(
    job: Job,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    hasApplied: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("job_details_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "नौकरी की पूरी जानकारी" else "Job Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Heading Job Title & Verified Flag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = job.getLocalizedTitle(isHindi),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = job.getLocalizedCompany(isHindi),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Info Matrix
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        InfoRow(label = if (isHindi) "💸 वेतन (Salary):" else "💸 Salary:", value = job.getLocalizedSalary(isHindi))
                        InfoRow(label = if (isHindi) "📍 स्थान (Location):" else "📍 Location:", value = job.getLocalizedLocation(isHindi))
                        InfoRow(label = if (isHindi) "⏰ ड्यूटी घंटा (Hours):" else "⏰ Work Hours:", value = job.getLocalizedWorkHours(isHindi))
                        val walkComment = if (job.distanceKm <= 1.5) " (${if (isHindi) "पैदल दूरी - बिल्कुल पास!" else "Walkable - Very Close!"})" else " ${if (isHindi) "किमी दूर" else "KM away"}"
                        InfoRow(label = if (isHindi) "🚗 दूरी (Distance):" else "🚗 Proximity:", value = "${job.distanceKm}$walkComment")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badges Box
                Text(
                    text = if (isHindi) "नौकरी के फायदे:" else "Job Perks & Badges:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRowHelper(
                    items = job.getLocalizedBadges(isHindi),
                    isHindi = isHindi
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Title
                Text(
                    text = if (isHindi) "काम का विवरण:" else "Work Description:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.getLocalizedDescription(isHindi),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Direct Contact Button
                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dialog_contact_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasApplied) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = if (hasApplied) Icons.Default.CheckCircle else Icons.Default.Phone,
                        contentDescription = "Contact"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasApplied) {
                            if (isHindi) "सम्पर्क किया जा चुका है!" else "Already Contacted!"
                        } else {
                            if (isHindi) "मालिक से सीधे बात करें" else "Contact Owner Directly"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowHelper(items: List<String>, isHindi: Boolean) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { badge ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- PROFILE EDIT SCREEN TAB ---
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileTabContent(
    profile: UserProfile?,
    isHindi: Boolean,
    onSaveProfile: (UserProfile) -> Unit,
    onPostJob: (Job) -> Unit
) {
    val context = LocalContext.current
    var userType by remember(profile) { mutableStateOf(profile?.userType ?: "SEEKER") }
    
    // Seeker state variables
    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var city by remember(profile) { mutableStateOf(profile?.cityEn ?: "Noida") }
    var category by remember(profile) { mutableStateOf(profile?.preferredCategory ?: "ALL") }
    var experienceYears by remember(profile) { mutableStateOf(profile?.seekerExperienceYears ?: "Fresher") }
    var transport by remember(profile) { mutableStateOf(profile?.seekerHasVehicle ?: "None") }
    var expectedSalary by remember(profile) { mutableStateOf(profile?.seekerExpectedSalary ?: "₹12,000 - ₹15,000") }
    var education by remember(profile) { mutableStateOf(profile?.seekerEducation ?: "10th Pass") }
    
    // Giver state variables
    var companyName by remember(profile) { mutableStateOf(profile?.companyName ?: "") }
    var contactPerson by remember(profile) { mutableStateOf(profile?.giverContactPerson ?: "") }
    var giverPhone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var giverWhatsapp by remember(profile) { mutableStateOf(profile?.giverWhatsapp ?: "") }
    var giverLocality by remember(profile) { mutableStateOf(profile?.cityEn ?: "Noida") }
    var giverIndustry by remember(profile) { mutableStateOf(profile?.giverIndustry ?: "Household") }

    var showPostJobDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("profile_content")
    ) {
        // Form Title
        Text(
            text = if (isHindi) "👤 पंजीकरण और प्रोफाइल (Registration)" else "👤 Profile & Registration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isHindi) "सही जानकारी भरने से बेहतरीन काम और सही स्टाफ जल्द मिलता है।" else "Accurate info helps find matching jobs and staff faster.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role Switcher Cards
        Text(
            text = if (isHindi) "अपना मुख्य रोल चुनें (Select Your Role):" else "Select Your Role:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seeker Role Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (userType == "SEEKER") MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { userType = "SEEKER" }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Seeker",
                        tint = if (userType == "SEEKER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "काम चाहिए (Seeker)" else "Want Job (Seeker)",
                        color = if (userType == "SEEKER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
            // Giver Role Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (userType == "GIVER") MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { userType = "GIVER" }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Giver",
                        tint = if (userType == "GIVER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "स्टाफ चाहिए (Giver)" else "Hire Staff (Giver)",
                        color = if (userType == "GIVER") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Animated Fields depending on role selected
        if (userType == "SEEKER") {
            // --- JOB SEEKER FORM ---
            
            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isHindi) "आपका शुभ नाम" else "Your Full Name") },
                placeholder = { Text(if (isHindi) "जैसे: संजय कुमार" else "e.g. Sanjay Kumar") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Field
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it },
                label = { Text(if (isHindi) "मोबाइल नंबर" else "Mobile Number") },
                placeholder = { Text("e.g. 9876543210") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_phone_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // City/Locality Field
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(if (isHindi) "रहने का इलाका / सेक्टर" else "Your Locality / Sector") },
                placeholder = { Text("e.g. Sector 62, Noida") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_city_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Job Category Preference Slider/Chips
            Text(
                text = if (isHindi) "किस प्रकार का काम ढूंढ रहे हैं?" else "What kind of work do you want?",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val categoriesList = listOf(
                "ALL" to (if (isHindi) "सभी काम" else "All Work"),
                "DELIVERY" to (if (isHindi) "डिलीवरी" else "Delivery"),
                "DRIVER" to (if (isHindi) "ड्राइवर" else "Driver"),
                "HELPER" to (if (isHindi) "ऑफिस हेल्पर" else "Office Helper"),
                "SECURITY" to (if (isHindi) "सिक्योरिटी गार्ड" else "Security Guard"),
                "MAID" to (if (isHindi) "घर का काम / Maid" else "Maid / Dusting"),
                "COOK" to (if (isHindi) "कुक / रसोईया" else "Cook / Chef"),
                "FACTORY" to (if (isHindi) "फैक्ट्री वर्कर" else "Factory Hand"),
                "CONSTRUCTION" to (if (isHindi) "मजदूर / सहायक" else "Laborer")
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                items(categoriesList) { (key, labelText) ->
                    val isCatSelected = category == key
                    FilterChip(
                        selected = isCatSelected,
                        onClick = { category = key },
                        label = { Text(labelText, fontSize = 11.sp, fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Experience Level Selector
            Text(
                text = if (isHindi) "काम का अनुभव (Work Experience):" else "Work Experience:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val experienceOpts = listOf(
                "Fresher" to (if (isHindi) "प्रारंभिक / फ्रेशर" else "Fresher (0-1 yrs)"),
                "1-2 Years" to (if (isHindi) "१ से २ वर्ष का" else "1-2 Years"),
                "3-5 Years" to (if (isHindi) "३ से ५ वर्ष का" else "3-5 Years"),
                "5+ Years" to (if (isHindi) "५ वर्ष से अधिक" else "5+ Years")
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                experienceOpts.forEach { (key, label) ->
                    val isSelected = experienceYears == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
                            .clickable { experienceYears = key }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Has Vehicle Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "क्या आपके पास खुद का वाहन है?" else "Do you own a vehicle?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isHindi) "डिलीवरी/ड्राइवर के काम में जरूरी" else "Crucial for delivery/driver jobs",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                val vehicleOpts = listOf(
                    "None" to (if (isHindi) "कुछ नहीं" else "No"),
                    "Cycle" to (if (isHindi) "साइकिल" else "Cycle"),
                    "Bike" to (if (isHindi) "बाइक/स्कूटी" else "Bike")
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    vehicleOpts.forEach { (key, label) ->
                        val isSelected = transport == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable { transport = key }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Education Level
            Text(
                text = if (isHindi) "पढ़ाई-लिखाई का स्तर (Education Level):" else "Education Level:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val eduOpts = listOf(
                "Not Studied" to (if (isHindi) "बिना पढ़े" else "None"),
                "8th Pass" to (if (isHindi) "८वीं पास" else "8th Pass"),
                "10th Pass" to (if (isHindi) "१०वीं पास" else "10th Pass"),
                "12th Pass" to (if (isHindi) "१२वीं पास" else "12th Pass"),
                "Graduate" to (if (isHindi) "ग्रेजुएट" else "Graduate")
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                items(eduOpts) { (key, label) ->
                    val isSelected = education == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
                            .clickable { education = key }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Seeker Profile Button
            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank()) {
                        Toast.makeText(context, if (isHindi) "नाम और नंबर भरना ज़रूरी है!" else "Name and Phone are mandatory!", Toast.LENGTH_SHORT).show()
                    } else {
                        val expHi = when(experienceYears) {
                            "Fresher" -> "नया/कोई अनुभव नहीं"
                            "1-2 Years" -> "१-२ साल का अनुभव"
                            "3-5 Years" -> "३-५ साल का अनुभव"
                            else -> "५ साल से ज्यादा अनुभव"
                        }
                        val expEn = when(experienceYears) {
                            "Fresher" -> "Fresher"
                            "1-2 Years" -> "1-2 Years Experience"
                            "3-5 Years" -> "3-5 Years Experience"
                            else -> "5+ Years Experience"
                        }
                        val updated = UserProfile(
                            id = 1,
                            name = name,
                            phone = phone,
                            preferredCategory = category,
                            experienceEn = expEn,
                            experienceHi = expHi,
                            cityEn = city,
                            cityHi = city,
                            userType = "SEEKER",
                            seekerExperienceYears = experienceYears,
                            seekerHasVehicle = transport,
                            seekerExpectedSalary = expectedSalary,
                            seekerEducation = education
                        )
                        onSaveProfile(updated)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("profile_save_button"),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "सीकर प्रोफाइल सुरक्षित करें" else "Save Candidate Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

        } else {
            // --- JOB GIVER (EMPLOYER) FORM ---
            
            // Recruiter Contact Name
            OutlinedTextField(
                value = contactPerson,
                onValueChange = { contactPerson = it },
                label = { Text(if (isHindi) "पंजीकृत मालिक / एचआर का शुभ नाम" else "Owner / Recruiter Full Name") },
                placeholder = { Text(if (isHindi) "जैसे: अमित शर्मा (मालिक)" else "e.g. Amit Sharma (Owner)") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Contact", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Firm / Company Name
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text(if (isHindi) "दुकान, कंपनी या सोसायटी का नाम" else "Shop, Company or Household Name") },
                placeholder = { Text(if (isHindi) "जैसे: शर्मा किराना दुकान या गुप्ता निवास" else "e.g. Sharma Grocery Store or Gupta Family Home") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Business Name", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Giver Calling Phone
            OutlinedTextField(
                value = giverPhone,
                onValueChange = { if (it.length <= 10) giverPhone = it },
                label = { Text(if (isHindi) "कॉल प्राप्त करने का मोबाइल नंबर" else "Mobile Number to Receive Calls") },
                placeholder = { Text("e.g. 9876543210") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Call Phone", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Giver WhatsApp Phone (Optional)
            OutlinedTextField(
                value = giverWhatsapp,
                onValueChange = { if (it.length <= 10) giverWhatsapp = it },
                label = { Text(if (isHindi) "व्हाट्सप्प नंबर (वैकल्पिक)" else "WhatsApp Number (Optional)") },
                placeholder = { Text("e.g. 9876543210") },
                leadingIcon = { Icon(Icons.Default.Share, contentDescription = "WhatsApp Phone", tint = Color(0xFF25D366)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Giver Locality
            OutlinedTextField(
                value = giverLocality,
                onValueChange = { giverLocality = it },
                label = { Text(if (isHindi) "आपकी दुकान / घर का सेक्टर या इलाका" else "Work Place Sector / Locality") },
                placeholder = { Text("e.g. Sector 63, Noida") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Locality", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Employer Category/Industry Selectors
            Text(
                text = if (isHindi) "कार्यस्थल का प्रकार (Workplace Type):" else "Workplace Type:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val industryOpts = listOf(
                "Household" to (if (isHindi) "🏠 निजी घर" else "Household"),
                "Shop / Retail" to (if (isHindi) "🏪 दुकान / रिटेल" else "Shop / Retail"),
                "Factory / Warehouse" to (if (isHindi) "🏭 फैक्ट्री / गोदाम" else "Factory/Warehouse"),
                "Office" to (if (isHindi) "🏢 ऑफिस / कंपनी" else "Office / Corporate"),
                "Restaurant" to (if (isHindi) "🍔 होटल / ढाबा" else "Hotel/Restaurant")
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                items(industryOpts) { (key, label) ->
                    val isSelected = giverIndustry == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent)
                            .clickable { giverIndustry = key }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Employer Profile Button
            Button(
                onClick = {
                    if (contactPerson.isBlank() || companyName.isBlank() || giverPhone.isBlank()) {
                        Toast.makeText(context, if (isHindi) "कृपया सभी मुख्य जानकारी भरें!" else "Please fill contact name, shop name and phone!", Toast.LENGTH_SHORT).show()
                    } else {
                        val updated = UserProfile(
                            id = 1,
                            name = contactPerson,
                            phone = giverPhone,
                            preferredCategory = "ALL",
                            experienceEn = companyName,
                            experienceHi = companyName,
                            cityEn = giverLocality,
                            cityHi = giverLocality,
                            userType = "GIVER",
                            companyName = companyName,
                            giverContactPerson = contactPerson,
                            giverWhatsapp = giverWhatsapp,
                            giverIndustry = giverIndustry
                        )
                        onSaveProfile(updated)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Employer Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "मालिक प्रोफाइल सुरक्षित करें" else "Save Employer Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- RECRUITER ACTION SECTION: POST A NEW JOB ---
            val isGiverRegistered = companyName.isNotEmpty() && giverPhone.isNotEmpty()
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isGiverRegistered) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, if (isGiverRegistered) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Post Job Icon",
                        tint = if (isGiverRegistered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) "📢 नया काम लाइव करें (Post a Job)" else "📢 Want to Hire? Post a New Job",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) {
                            "सत्यापित काम डालकर पास के हज़ारों ड्राइवर, डिलीवरी बॉय, गार्ड व घरेलू सहायकों से सीधा संपर्क पाएं।"
                        } else {
                            "Submit your requirement to helper/driver/security candidates near Noida Sec 62."
                        },
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        enabled = isGiverRegistered,
                        onClick = { showPostJobDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "नया काम पोस्ट करें (+ Post Job)" else "+ Post a Local Job",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    if (!isGiverRegistered) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isHindi) "⚠️ पहले अपनी मालिक प्रोफाइल ऊपर सुरक्षित कर लें!" else "⚠️ Complete and Save your Employer profile first!",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Trust and Security footer statement Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Safe",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isHindi) "रोज़गार मेला आपकी जानकारी पूरी तरफ मुफ़्त और सुरक्षित रखता है। हम आपसे कोई कमीशन कभी भी नहीं मांगते।" else "Rozgar Mela is 100% commissions and fees free. We directly connect neighborhood job providers and helpers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 15.sp
                )
            }
        }
    }

    // --- JOB GIVER POST JOB SHEET DIALOG ---
    if (showPostJobDialog) {
        PostJobDialog(
            isHindi = isHindi,
            employerName = companyName,
            employerPhone = giverPhone,
            employerWhatsapp = giverWhatsapp,
            employerLocality = giverLocality,
            onDismiss = { showPostJobDialog = false },
            onPost = { job ->
                onPostJob(job)
                showPostJobDialog = false
            }
        )
    }
}

// --- RECUTIER POST NEW JOB POPUP FORM ---
@Composable
fun PostJobDialog(
    isHindi: Boolean,
    employerName: String,
    employerPhone: String,
    employerWhatsapp: String,
    employerLocality: String,
    onDismiss: () -> Unit,
    onPost: (Job) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf("HELPER") }
    var locationInput by remember { mutableStateOf(employerLocality) }
    var contactPhoneInput by remember { mutableStateOf(employerPhone) }
    var contactWhatsappInput by remember { mutableStateOf(employerWhatsapp) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp)
                .testTag("post_job_form_dial"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isHindi) "📣 नया स्थानीय काम पोस्ट करें" else "📣 Post a Local Job Requirement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                // 1. Selector category
                Text(
                    text = if (isHindi) "काम किस श्रेणी (Category) का है?" else "Choose Worker Category:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val catOpts = listOf(
                    "DELIVERY" to (if (isHindi) "डिलीवरी" else "Delivery"),
                    "DRIVER" to (if (isHindi) "ड्राइवर" else "Driver"),
                    "HELPER" to (if (isHindi) "ऑफिस हेल्पर / सहायक" else "Helper"),
                    "SECURITY" to (if (isHindi) "सिक्योरिटी गार्ड" else "Security"),
                    "MAID" to (if (isHindi) "घर का काम" else "Maid"),
                    "COOK" to (if (isHindi) "कुक / रसोईया" else "Cook"),
                    "FACTORY" to (if (isHindi) "फैक्ट्री वर्कर" else "Factory"),
                    "CONSTRUCTION" to (if (isHindi) "कंस्ट्रक्शन" else "Labor")
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    items(catOpts) { (key, label) ->
                        val isSelected = categorySelected == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { categorySelected = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Job Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isHindi) "क्या काम है? (Job Title / Role)" else "Job Role Title") },
                    placeholder = { Text(if (isHindi) "जैसे: अनुभवी कार ड्राइवर, पार्सल डिलीवरी बॉय" else "e.g. Personal Car Driver, Helper Peon") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Salary offered
                OutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = { Text(if (isHindi) "महीने की पगार / पगार रेंज (Salary)" else "Salary / Month Offered") },
                    placeholder = { Text(if (isHindi) "जैसे: ₹15,000 / महीना" else "e.g. ₹15,000 / month") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Locality
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    label = { Text(if (isHindi) "ड्यूटी स्थान (Job Location)" else "Job Location / Sector") },
                    placeholder = { Text("e.g. Sector 62, Noida") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Shift hours
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text(if (isHindi) "ड्यूटी का समय (Work Shift Hours)" else "Duty Hours") },
                    placeholder = { Text(if (isHindi) "जैसे (9 AM - 6 PM, सोमवार बंद)" else "e.g. 9:00 AM - 6:30 PM (Sunday off)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 6. Detailed requirements
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(if (isHindi) "काम की जानकारी (Duties / Job Description)" else "Describe Job Requirements") },
                    placeholder = { Text(if (isHindi) "जैसे: ड्राइवर को नोएडा की सड़कों का ज्ञान होना चाहिए..." else "e.g. Driver needs min 3 years exp, clean record...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 7. Phones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = contactPhoneInput,
                        onValueChange = { contactPhoneInput = it },
                        label = { Text(if (isHindi) "कॉल फोन" else "Phone for Calls") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = contactWhatsappInput,
                        onValueChange = { contactWhatsappInput = it },
                        label = { Text(if (isHindi) "व्हाट्सप्प नंबर" else "WhatsApp No.") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 8. Submit buttons
                Button(
                    onClick = {
                        if (title.isBlank() || salary.isBlank()) {
                            Toast.makeText(context, if (isHindi) "काम का नाम और वेतन भरना आवश्यक है!" else "Job role and salary are required!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Assign nearby random distance (0.5 to 5.0 KM) to make it active and hyperlocal
                            val rndDist = String.format(Locale.US, "%.1f", (0.5 + Math.random() * 4.5)).toDouble()
                            val postedEn = "Just Now"
                            val postedHi = "अभी-अभी"
                            
                            val freshJob = Job(
                                id = 0, // Auto-generating in Dao or randomized in VM
                                titleEn = title,
                                titleHi = title,
                                companyEn = employerName,
                                companyHi = employerName,
                                category = categorySelected,
                                salaryRangeEn = salary,
                                salaryRangeHi = salary,
                                locationEn = locationInput,
                                locationHi = locationInput,
                                distanceKm = rndDist,
                                workHoursEn = hours,
                                workHoursHi = hours,
                                phone = contactPhoneInput,
                                whatsapp = contactWhatsappInput,
                                descriptionEn = desc,
                                descriptionHi = desc,
                                badgesEn = "Direct Owner,Immediate Join,No Charges",
                                badgesHi = "मालिक डायरेक्ट,तुरंत जॉइन करें,कोई शुल्क नहीं",
                                isVerified = false,
                                postedDateEn = postedEn,
                                postedDateHi = postedHi
                            )
                            onPost(freshJob)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "📢 लाइव काम पोस्ट करें (Publish Job)" else "📢 Publish Job Requirement",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// --- APPLICATIONS TAB SCREEN ---
@Composable
fun ApplicationsTabContent(
    appliedJobs: List<AppliedJobDetail>,
    isHindi: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("applications_content")
    ) {
        Text(
            text = if (isHindi) "📋 मेरे आवेदन" else "📋 My Applied Jobs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isHindi) "यहाँ वो सब मालिक हैं जिनसे आपने कॉल या व्हाट्सप्प पर बात की है" else "History of direct calls and messages with owners.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (appliedJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "No Applications",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isHindi) "अभी तक कोई आवेदन नहीं किया" else "No applied jobs yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) "नौकरी वाले टैब में जाकर किसी भी मालिक को सीधे कॉल करें!" else "Browse jobs and tap 'Contact Owner' of any listing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("applications_list")
            ) {
                items(appliedJobs) { hJob ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_item_${hJob.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hJob.getLocalizedCompany(isHindi),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontWeight = FontWeight.Bold
                                )

                                val dtStr = dateFormat.format(Date(hJob.appliedAt))
                                Text(
                                    text = dtStr,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = hJob.getLocalizedTitle(isHindi),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = hJob.getLocalizedSalary(isHindi),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = if (hJob.contactMethod == "Call") Icons.Default.Phone else Icons.Default.Send,
                                        contentDescription = "Call Type",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = hJob.getLocalizedStatus(isHindi),
                                        color = Color(0xFF2E7D32),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- POPUP ACTION CONTACT POPUP DIALOG ---
@Composable
fun ContactActionDialog(
    job: Job,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onCallAction: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("contact_selection_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isHindi) "मालिक से सीधे संपर्क करें" else "Direct Connect to Employer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${job.getLocalizedCompany(isHindi)}\n(${job.getLocalizedTitle(isHindi)})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Call Buttons
                // Option A: Phone direct call
                Button(
                    onClick = { onCallAction("Call") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("action_call_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(23.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "फ़ोन से डायरेक्ट कॉल करें" else "Dial Phone Call Direct",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Option B: WhatsApp direct message (If defined)
                OutlinedButton(
                    onClick = { onCallAction("WhatsApp") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("action_whatsapp_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2E7D32)
                    ),
                    border = BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(23.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "WhatsApp"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "व्हाट्सप्प (WhatsApp) संदेश भेजें" else "Send WhatsApp Message",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDismiss, modifier = Modifier.testTag("action_cancel")) {
                    Text(text = if (isHindi) "पीछे जाएं" else "Cancel/Go Back", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

// --- TRANSLATION HELPER UTILS ---
fun getCategoryDisplayName(key: String, isHindi: Boolean): String {
    return when (key.uppercase()) {
        "ALL" -> if (isHindi) "सभी काम" else "All Work"
        "DELIVERY" -> if (isHindi) "डिलीवरी" else "Delivery Boy"
        "DRIVER" -> if (isHindi) "ड्राइवर" else "Driver"
        "HELPER" -> if (isHindi) "हेल्पर / चपरासी" else "Office Helper"
        "COOK" -> if (isHindi) "कुक / रसोइया" else "Cook / Chef"
        "MAID" -> if (isHindi) "कामवाली / बाई" else "House Maid"
        "SECURITY" -> if (isHindi) "सुरक्षा गार्ड" else "Security Guard"
        "CONSTRUCTION" -> if (isHindi) "मज़दूर / राजमिस्त्री" else "Construction"
        "FACTORY" -> if (isHindi) "फैक्ट्री कर्मचारी" else "Factory worker"
        else -> key
    }
}

fun getCategoryIcon(key: String): ImageVector {
    return when (key.uppercase()) {
        "ALL" -> Icons.Default.List
        "DELIVERY" -> Icons.Default.Send
        "DRIVER" -> Icons.Default.LocationOn
        "HELPER" -> Icons.Default.Person
        "COOK" -> Icons.Default.Home
        "MAID" -> Icons.Default.Favorite
        "SECURITY" -> Icons.Default.Lock
        "CONSTRUCTION" -> Icons.Default.Build
        "FACTORY" -> Icons.Default.Settings
        else -> Icons.Default.Search
    }
}

// --- SYSTEM INTENT LAUNCHERS ---
fun sendDialerIntent(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open phone dialer", Toast.LENGTH_SHORT).show()
    }
}

fun sendWhatsAppIntent(context: Context, phoneNumber: String, jobTitle: String, company: String) {
    try {
        // Handle numeric format for WhatsApp
        val formattedNumber = if (!phoneNumber.startsWith("+")) {
            if (phoneNumber.length == 10) "91$phoneNumber" else phoneNumber
        } else {
            phoneNumber.replace("+", "")
        }
        
        val message = "नमस्ते, मुझे रोज़गार मेला ऐप पर आपके यहाँ '$jobTitle' ($company) की नौकरी दिखी। क्या यह स्थान अभी खाली है? धन्यवाद।"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open WhatsApp applet", Toast.LENGTH_SHORT).show()
    }
}

// Simple legacy helper because compose doesn't import FlowRow by default on older BOMs.
@Composable
fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
