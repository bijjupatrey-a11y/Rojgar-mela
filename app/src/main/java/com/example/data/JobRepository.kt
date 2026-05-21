package com.example.data

import kotlinx.coroutines.flow.Flow

class JobRepository(private val jobDao: JobDao) {

    val allJobs: Flow<List<Job>> = jobDao.getAllJobsFlow()
    val userProfile: Flow<UserProfile?> = jobDao.getProfileFlow()
    val appliedJobs: Flow<List<AppliedJobDetail>> = jobDao.getAppliedJobDetailsFlow()

    suspend fun getJobById(id: Int): Job? {
        return jobDao.getJobById(id)
    }

    suspend fun getProfileDirect(): UserProfile? {
        return jobDao.getProfileDirect()
    }

    suspend fun applyToJob(jobId: Int, contactMethod: String) {
        val app = UserApplication(
            jobId = jobId,
            appliedAt = System.currentTimeMillis(),
            statusEn = "Applied (Contacted)",
            statusHi = "आवेदन किया (सम्पर्क किया)",
            contactMethod = contactMethod
        )
        jobDao.insertApplication(app)
    }

    suspend fun isAlreadyApplied(jobId: Int): Boolean {
        return jobDao.getApplicationForJob(jobId) != null
    }

    suspend fun saveProfile(profile: UserProfile) {
        jobDao.insertProfile(profile)
    }

    suspend fun postJob(job: Job) {
        jobDao.insertJob(job)
    }

    suspend fun ensureProfileExists() {
        val current = jobDao.getProfileDirect()
        if (current == null) {
            val defaultProfile = UserProfile(
                id = 1,
                name = "",
                phone = "",
                preferredCategory = "ALL",
                experienceEn = "No Experience",
                experienceHi = "कोई अनुभव नहीं",
                cityEn = "Noida",
                cityHi = "नोएडा"
            )
            jobDao.insertProfile(defaultProfile)
        }
    }

    suspend fun populatePredefinedJobsIfEmpty() {
        if (jobDao.getJobsCount() == 0) {
            val list = listOf(
                Job(
                    id = 1,
                    titleEn = "Delivery Partner (Bike riders)",
                    titleHi = "डिलीवरी बॉय (बाइक ज़रूरी)",
                    companyEn = "Zomato Logistics Ltd.",
                    companyHi = "ज़ोमैटो लॉजिस्टिक्स लिमिटेड",
                    category = "DELIVERY",
                    salaryRangeEn = "₹18,000 - ₹28,000 / month",
                    salaryRangeHi = "₹18,000 - ₹28,000 / महीना",
                    locationEn = "Sector 62, Noida, UP",
                    locationHi = "सेक्टर 62, नोएडा, यूपी",
                    distanceKm = 1.2,
                    workHoursEn = "Flexible Shifts (Part/Full Time)",
                    workHoursHi = "लचीली पाली (पार्ट/फुल टाइम)",
                    phone = "9876543210",
                    whatsapp = "9876543210",
                    descriptionEn = "Earn daily payouts. Must have own bike and valid driving license. Weekly bonus and health insurance included. Instant joining!",
                    descriptionHi = "रोजाना पाईये पेमेंट। खुद की बाइक और ड्राइविंग लाइसेंस होना ज़रूरी है। साप्ताहिक बोनस और स्वास्थ्य बीमा शामिल है। तुरंत जुड़ें!",
                    badgesEn = "Weekly Payout,Bike Required,Immediate Join",
                    badgesHi = "साप्ताहिक भुगतान,बाइक जरुरी,तुरंत जॉइन करें",
                    isVerified = true,
                    postedDateEn = "Today",
                    postedDateHi = "आज"
                ),
                Job(
                    id = 2,
                    titleEn = "Personal Car Driver",
                    titleHi = "पर्सनल कार ड्राइवर (अनुभवी)",
                    companyEn = "Sharma Family Residency",
                    companyHi = "शर्मा फैमिली रेजिडेंसी",
                    category = "DRIVER",
                    salaryRangeEn = "₹16,500 / month",
                    salaryRangeHi = "₹16,500 / महीना",
                    locationEn = "Indirapuram, Ghaziabad",
                    locationHi = "इंदिरापुरम, गाजियाबाद",
                    distanceKm = 2.4,
                    workHoursEn = "8:30 AM - 6:30 PM (Sunday off)",
                    workHoursHi = "सुबह 8:30 - शाम 6:30 (रविवार छुट्टी)",
                    phone = "9123456789",
                    whatsapp = "9123456789",
                    descriptionEn = "Require senior driver with minimum 3 years experience for sedan car. Clean driving record. Direct family owner, no agency.",
                    descriptionHi = "सेडान गाड़ी के लिए कम से कम 3 साल का अनुभव रखने वाले ड्राइवर की आवश्यकता है। साफ ड्राइविंग रिकॉर्ड। मालिक से डायरेक्ट संपर्क, कोई एजेंसी नहीं।",
                    badgesEn = "Direct Owner,No Experience Charge,Weekly Off",
                    badgesHi = "मालिक डायरेक्ट,कोई शुल्क नहीं,हफ़्ते में छुट्टी",
                    isVerified = true,
                    postedDateEn = "Today",
                    postedDateHi = "आज"
                ),
                Job(
                    id = 3,
                    titleEn = "Office Peon / Helper / Runner",
                    titleHi = "ऑफिस चपरासी / हेल्पर / रनर",
                    companyEn = "Infolabs Software Corp",
                    companyHi = "इन्फोलैब्स सॉफ्टवेयर कॉर्प",
                    category = "HELPER",
                    salaryRangeEn = "₹13,000 - ₹15,000 / month",
                    salaryRangeHi = "₹13,000 - ₹15,000 / महीना",
                    locationEn = "Sector 63, Noida",
                    locationHi = "सेक्टर 63, नोएडा",
                    distanceKm = 1.8,
                    workHoursEn = "9:30 AM - 6:30 PM (Mon-Sat)",
                    workHoursHi = "सुबह 9:30 - शाम 6:30 (सोम-शनि)",
                    phone = "9234567890",
                    whatsapp = "9234567890",
                    descriptionEn = "Responsibilities include handling bills, serving tea/water, photocopies. Must be 10th pass, basic reading of English address necessary.",
                    descriptionHi = "बिल जमा करना, चाय/पानी देना, ज़ेरॉक्स करना मुख्य काम होगा। दसवीं पास होना जरुरी है, बुनियादी अंग्रेज़ी पढ़ने का ज्ञान होना चाहिए।",
                    badgesEn = "Office Job,Tea-Snacks Free,10th Pass",
                    badgesHi = "ऑफिस की नौकरी,चाय-नाश्ता फ्री,10वी पास",
                    isVerified = true,
                    postedDateEn = "Yesterday",
                    postedDateHi = "कल"
                ),
                Job(
                    id = 4,
                    titleEn = "Society Security Guard",
                    titleHi = "सोसायटी सिक्योरिटी गार्ड (12 घंटे)",
                    companyEn = "Amrapali Group Security",
                    companyHi = "आम्रपाली ग्रुप सिक्योरिटी",
                    category = "SECURITY",
                    salaryRangeEn = "₹14,500 / month",
                    salaryRangeHi = "₹14,500 / महीना",
                    locationEn = "Sector 76, Noida",
                    locationHi = "सेक्टर 76, नोएडा",
                    distanceKm = 3.5,
                    workHoursEn = "8 PM - 8 AM (Night shift/Rotation)",
                    workHoursHi = "रात 8 - सुबह 8 (नाइट शिफ्ट/रोटेशन)",
                    phone = "9345678901",
                    whatsapp = "",
                    descriptionEn = "Require physically fit guard with minimum height of 5'8\". Police verification is mandatory. Free uniform and guard room.",
                    descriptionHi = "कम से कम 5'8\" कद वाले हट्टे-कट्टे गार्ड की ज़रूरत है। पुलिस वेरिफिकेशन करना अनिवार्य है। निःशुल्क वर्दी और रहने की जगह मिलेगी।",
                    badgesEn = "Uniform Free,Accommodation Room,Verified",
                    badgesHi = "वर्दी फ्री,रहने की व्यवस्था,सत्यापित जॉब",
                    isVerified = true,
                    postedDateEn = "Today",
                    postedDateHi = "आज"
                ),
                Job(
                    id = 5,
                    titleEn = "BHK Flat Maid & Housekeeper",
                    titleHi = "घर के काम के लिए कामवाली बाई",
                    companyEn = "Gupta Family Home",
                    companyHi = "गुप्ता फैमिली होम",
                    category = "MAID",
                    salaryRangeEn = "₹9,000 / month (Part-Time)",
                    salaryRangeHi = "₹9,000 / महीना (कम समय)",
                    locationEn = "Shipra Sun City, Indirapuram",
                    locationHi = "शिप्रा सन सिटी, इंदिरापुरम",
                    distanceKm = 0.8,
                    workHoursEn = "7:00 AM - 11:00 AM (Daily)",
                    workHoursHi = "सुबह 7:00 - सुबह 11:00 (दैनिक)",
                    phone = "9456789012",
                    whatsapp = "9456789012",
                    descriptionEn = "Required housemaid for dusting, broom-mop, and washing clothes in 2BHK. Female candidate preferred. Warm polite family.",
                    descriptionHi = "2BHK घर में साफ-सफाई, पोछा लगाने और कपडे धोने के लिए कामवाली बाई की आवश्यकता है। महिला को प्राथमिकता दी जाएगी। सभ्य परिवार।",
                    badgesEn = "Part-Time,Immediate Join,Female Spot",
                    badgesHi = "केवल कुछ समय,तुरंत जुड़ें,महिला के लिए",
                    isVerified = true,
                    postedDateEn = "2 days ago",
                    postedDateHi = "२ दिन पहले"
                ),
                Job(
                    id = 6,
                    titleEn = "Machine Operator Helper",
                    titleHi = "फैक्ट्री मशीन ऑपरेटर हेल्पर",
                    companyEn = "Apex Gear Industries",
                    companyHi = "एपेक्स गियर इंडस्ट्रीज",
                    category = "FACTORY",
                    salaryRangeEn = "₹12,500 + PF + ESI",
                    salaryRangeHi = "₹12,500 + पीएफ + ईएसआई",
                    locationEn = "Sector 4 Industrial Area, Noida",
                    locationHi = "सेक्टर 4 औद्योगिक क्षेत्र, नोएडा",
                    distanceKm = 4.9,
                    workHoursEn = "9:00 AM - 6:00 PM (Overtime extra)",
                    workHoursHi = "सुबह 9:00 - शाम 6:00 (अलग से ओवरटाइम)",
                    phone = "9567890123",
                    whatsapp = "9567890123",
                    descriptionEn = "Assisting machine operators with raw material loading. Regular monthly pay, PF and ESI benefits are fully paid by employer.",
                    descriptionHi = "कच्चा माल लोड करने में मशीन ऑपरेटरों की सहायता करना। नियमित मासिक वेतन, पीएफ और ईएसआई लाभ नियोक्ता द्वारा पूरी तरह से भुगतान किया जाता है।",
                    badgesEn = "PF + ESI Benefit,Overtime Pay,Room Available",
                    badgesHi = "पीएफ+ईएसआई लाभ,ओवरटाइम पैसा,कमरा उपलब्ध",
                    isVerified = true,
                    postedDateEn = "Yesterday",
                    postedDateHi = "कल"
                ),
                Job(
                    id = 7,
                    titleEn = "North Indian Food Cook / Chef",
                    titleHi = "होटल के लिए उत्तर भारतीय हलवाई/कुक",
                    companyEn = "Bikaner Sweets Corner",
                    companyHi = "बीकानेर स्वीट्स कॉर्नर",
                    category = "COOK",
                    salaryRangeEn = "₹17,000 - ₹20,000 / month",
                    salaryRangeHi = "₹17,000 - ₹20,000 / महीना",
                    locationEn = "Sector 62 Market, Noida",
                    locationHi = "सेक्टर 62 मार्केट, नोएडा",
                    distanceKm = 1.5,
                    workHoursEn = "11:00 AM - 10:00 PM (Weekly Off)",
                    workHoursHi = "सुबह 11:00 - रात 10:00 (हफ़्ते में छुट्टी)",
                    phone = "9678901234",
                    whatsapp = "9678901234",
                    descriptionEn = "Looking for a cook skilled in preparing Roti, Paneer gravy, Thali meals. Breakfast and dinner provided free of charge.",
                    descriptionHi = "रोटी, पनीर ग्रेवी, और थाली का भोजन बनाने में कुशल कुक की आवश्यकता है। नाश्ता और रात का भोजन मुफ्त दिया जाएगा।",
                    badgesEn = "Free Food,Owner Direct,Immediate Join",
                    badgesHi = "मुफ़्त भोजन,मालिक डायरेक्ट,तुरंत जुड़ें",
                    isVerified = true,
                    postedDateEn = "3 days ago",
                    postedDateHi = "३ दिन पहले"
                ),
                Job(
                    id = 8,
                    titleEn = "Construction Helper / Laborer",
                    titleHi = "कंस्ट्रक्शन कामगार / सहायक मजदूर",
                    companyEn = "Apex Builders & Projects",
                    companyHi = "एपेक्स बिल्डर्स एंड प्रोजेक्ट्स",
                    category = "CONSTRUCTION",
                    salaryRangeEn = "₹600 - ₹650 / day (Daily Pay)",
                    salaryRangeHi = "₹600 - ₹650 / दिन (रोजाना नकद)",
                    locationEn = "Metro Extension Site, Noida 50",
                    locationHi = "मेट्रो एक्सटेंशन साइट, नोएडा 50",
                    distanceKm = 4.2,
                    workHoursEn = "9:00 AM - 5:30 PM (Daily)",
                    workHoursHi = "सुबह 9:00 - शाम 5:30 (दैनिक)",
                    phone = "9789012345",
                    whatsapp = "",
                    descriptionEn = "General labor tasks at new metro station construction. Lifting material, mixing cement. Cash paid daily every evening.",
                    descriptionHi = "नई मेट्रो स्टेशन निर्माण पर सामान्य मजदूरी और सहायक कार्य। सीमेंट मिलाना, सामग्री उठाना। हर शाम नकद भुगतान।",
                    badgesEn = "Daily Daily Cash,No Commission,Immediate",
                    badgesHi = "रोजाना नकद,कोई कमीशन नहीं,तुरंत काम",
                    isVerified = true,
                    postedDateEn = "Today",
                    postedDateHi = "आज"
                )
            )
            jobDao.insertJobs(list)
        }
    }
}
