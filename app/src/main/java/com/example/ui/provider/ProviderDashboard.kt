package com.example.ui.provider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.*
import com.example.data.repository.MarketplaceRepository
import com.example.ui.components.BrandLogo
import com.example.ui.components.GeometricBackground
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboard(
    repository: MarketplaceRepository,
    currentProviderId: String,
    onLogout: () -> Unit,
    coroutineScope: CoroutineScope
) {
    var selectedTab by remember { mutableStateOf(0) }
    var hindiMode by remember { mutableStateOf(false) }

    // Navigation and titles based on chosen language mode (Hindi/English helper)
    val tabTitles = if (hindiMode) {
        listOf("नए ठेके (Leads)", "कोटेशन (Quotes)", "काम (Jobs)", "कमाई (Wallet)")
    } else {
        listOf("Lead Inbox", "My Quotes", "Job Board", "Earnings")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandLogo(showText = false, heightDp = 36)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hindiMode) "सेतु ठेकेदार पोर्टल" else "Setu Contractor Portal",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                },
                actions = {
                    // Vernacular language switch toggle (highly praised for Indian partners)
                    TextButton(onClick = { hindiMode = !hindiMode }) {
                        Text(
                            text = if (hindiMode) "English" else "हिन्दी (A/अ)",
                            color = OrangeAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = OrangeAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = NavyPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabTitles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title, fontSize = 9.sp) },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Inbox
                                    1 -> Icons.Default.Description
                                    2 -> Icons.Default.DirectionsRun
                                    else -> Icons.Default.AccountBalanceWallet
                                },
                                contentDescription = title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = NavyPrimary,
                            indicatorColor = OrangeAccent.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            GeometricBackground()
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
            // Seeding Provider credentials card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = TealSuccess,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Amit Sharma (Noida BuildCon Corp)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TealSuccess.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GOLD PARTNER",
                                    color = TealSuccess,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aadhaar verified • Noida NCR",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Tab router
            when (selectedTab) {
                0 -> LeadInboxTab(repository, currentProviderId, hindiMode, coroutineScope)
                1 -> MyQuotesTab(repository, currentProviderId, hindiMode)
                2 -> JobBoardTab(repository, currentProviderId, hindiMode, coroutineScope)
                3 -> WalletTab(repository, currentProviderId, hindiMode)
            }
        }
    }
}
}

@Composable
fun LeadInboxTab(
    repository: MarketplaceRepository,
    providerId: String,
    hindiMode: Boolean,
    scope: CoroutineScope
) {
    val myLeads by repository.getMyLeads(providerId).collectAsState(initial = emptyList())
    val requirements by repository.getAllRequirements().collectAsState(initial = emptyList())

    // Simulated 30-minute countdown SLA timer (highly emphasized in Section 3.3 for contractors)
    var timeLeftSeconds by remember { mutableStateOf(1780) } // ~29 mins
    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
    }

    val displayMinutes = timeLeftSeconds / 60
    val displaySeconds = timeLeftSeconds % 60

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = if (hindiMode) "निकटतम निर्माण कार्य विज्ञापन (Leads)" else "Hyperlocal Bidding Inquiries",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp
            )
            Text(
                text = if (hindiMode) "यह नौकरियां सीधे मकानमालिकों द्वारा डाली गई हैं। जल्द जवाब दें।" else "These projects are posted near your selected cities. Submit quotes within hours.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (myLeads.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (hindiMode) "कोई नया लीड उपलब्ध नहीं है" else "Inbox is Empty",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Loop through leads
        items(myLeads) { lead ->
            val associatedReq = requirements.find { it.id == lead.requirementId }
            if (associatedReq != null && lead.status == "PENDING") {
                var showBidForm by remember { mutableStateOf(false) }
                var bidAmountInput by remember { mutableStateOf("") }
                var timelineInput by remember { mutableStateOf("20") }
                var bidNotesInput by remember { mutableStateOf("") }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEAD #${associatedReq.id}",
                                color = OrangeAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )

                            // Responsive countdown timer box
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Red.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$displayMinutes:${String.format("%02d", displaySeconds)}",
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(associatedReq.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                        Text(associatedReq.description, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (hindiMode) "बजट दायरा: ₹${associatedReq.budgetMin.toInt()} - ₹${associatedReq.budgetMax.toInt()}" else "Budget Range: ₹${associatedReq.budgetMin.toInt()} - ₹${associatedReq.budgetMax.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NavyPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        repository.updateLeadStatus(lead.id, "DECLINED")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (hindiMode) "अस्वीकार (Decline)" else "Decline")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showBidForm = !showBidForm },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (hindiMode) "कोटेशन भेजें (Bid)" else "Create Quote")
                            }
                        }

                        // Submmit Bidding form inline
                        AnimatedVisibility(visible = showBidForm) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (hindiMode) "कोटेशन विवरण तय करें" else "Specify Quote Parameters",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NavyPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = bidAmountInput,
                                    onValueChange = { bidAmountInput = it },
                                    label = { Text(if (hindiMode) "कुल मूल्य (₹)" else "Proposed Total Cost (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = timelineInput,
                                    onValueChange = { timelineInput = it },
                                    label = { Text(if (hindiMode) "जरूरी दिन (Days)" else "Duration (Days)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = bidNotesInput,
                                    onValueChange = { bidNotesInput = it },
                                    label = { Text(if (hindiMode) "गारंटी/शर्तें" else "Warranty / Safety notes") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val amt = bidAmountInput.toDoubleOrNull()
                                        if (amt != null) {
                                            scope.launch {
                                                repository.submitQuote(
                                                    QuoteEntity(
                                                        leadId = lead.id,
                                                        providerId = providerId,
                                                        providerName = "Amit Sharma (Noida BuildCon Corp)",
                                                        providerRating = 4.9f,
                                                        totalAmount = amt,
                                                        timelineDays = timelineInput.toIntOrNull() ?: 20,
                                                        breakdownJson = "[]",
                                                        notes = bidNotesInput.ifEmpty { "Fully engineered concrete reinforcement." },
                                                        status = "SUBMITTED"
                                                    )
                                                )
                                                showBidForm = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealSuccess)
                                ) {
                                    Text(if (hindiMode) "सीधे मकानमालिक को भेजें" else "Submit Bid to Client", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyQuotesTab(
    repository: MarketplaceRepository,
    providerId: String,
    hindiMode: Boolean
) {
    val submittedQuotes by repository.getMySubmittedQuotes(providerId).collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = if (hindiMode) "मेरे भेजे गए कोटेशन" else "Submitted Active Quotes",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp
            )
            Text(
                text = if (hindiMode) "आपके द्वारा भेजे गए दामों की सूची और समीक्षा स्थिति" else "Track review status of your submitted project proposals.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (submittedQuotes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(if (hindiMode) "कोई सबमिट किया गया कोट नहीं मिला" else "No submitted quotes found.")
                    }
                }
            }
        }

        items(submittedQuotes) { quote ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("QUOTE_SETU_${quote.id}", fontWeight = FontWeight.Bold, color = OrangeAccent, fontSize = 11.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NavyPrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(quote.status, color = NavyPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Amount Bid: ₹${quote.totalAmount.toInt()}", fontWeight = FontWeight.ExtraBold, color = NavyPrimary, fontSize = 15.sp)
                    Text(text = "Estimated Time: ${quote.timelineDays} working days", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Notes: ${quote.notes}", fontSize = 11.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun JobBoardTab(
    repository: MarketplaceRepository,
    providerId: String,
    hindiMode: Boolean,
    scope: CoroutineScope
) {
    val projects by repository.getActiveProjectsProvider(providerId).collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = if (hindiMode) "चल रहे निर्माण कार्य (Active Jobs)" else "Active Execution Board",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp
            )
            Text(
                text = if (hindiMode) "प्रत्येक चरण समाप्त होने पर मकानमालिक तुरंत भुगतान जारी करेंगे" else "Track structural milestones. Request payment releases securely.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (projects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(if (hindiMode) "कोई सक्रिय निर्माण कार्य उपलब्ध नहीं" else "No Active Projects Board")
                    }
                }
            }
        }

        // Project Cards
        items(projects) { project ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PROJECT SETU-DX998", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = OrangeAccent, letterSpacing = 1.sp)
                    Text(text = "G+1 Slab Casting Civil Construction", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                    Text(text = "Client Name: Ramesh Chand (Greater Noida)", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (hindiMode) "निर्माण चरण विकास सूचकांक (Milestones)" else "CONSTRUCTION MILESTONES TRAFFIC",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Milestone 1 State Display
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("M1: Footing excavation & pillar framework", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("₹${(project.totalAmount * 0.3).toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(14.dp))
                                    Text(" 2 camera uploads sent", fontSize = 10.sp, color = Color.Gray)
                                }

                                if (project.activeMilestoneIndex == 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(OrangeAccent.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("WAITING CLIENT VERIFY", color = OrangeAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text("FUNDS CLEARED ✅", color = TealSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Milestone 2
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("M2: Cement slabs casting & boundary bricks", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("₹${(project.totalAmount * 0.4).toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            if (project.activeMilestoneIndex == 1) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(14.dp))
                                        Text(" Upload design progress photo", fontSize = 10.sp, color = NavyPrimary)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NavyPrimary.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("IN CIVIL INJECTION", color = NavyPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (project.activeMilestoneIndex > 1) {
                                Text("FUNDS CLEARED ✅", color = TealSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                            } else {
                                Text("NOT STARTED YET", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletTab(
    repository: MarketplaceRepository,
    providerId: String,
    hindiMode: Boolean
) {
    val providerDetails by repository.getProviderDetails(providerId).collectAsState(initial = null)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (hindiMode) "मेरी वॉलेट कमाई व कर कटौती" else "Contractor Finance & Ledger",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp
            )
            Text(
                text = if (hindiMode) "वित्तीय रिपोर्टिंग: इसमें प्लेटफार्म शुल्क (8%) और Section 194C TDS सम्मिलित है।" else "Monitors gross payouts, 8% commissions cuts, and Section 194C Indian TDS variables.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (hindiMode) "निकासी के लिए उपलब्ध राशि" else "WITHDRAWABLE WALLET BALANCE",
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "₹${String.format("%,d", (providerDetails?.earningsBalance ?: 0.0).toLong())}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = TealSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hindiMode) "सुरक्षित रूप से स्टेट बैंक से संबंद्ध" else "Settling directly into SBI Account",
                            color = TealSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Withdraw simulate */ },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (hindiMode) "स्टेट बैंक में ट्रांसफर करें (T+2)" else "Instant Payout Transfer (SBI)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(if (hindiMode) "हाल के लेनदेन विवरण" else "RECENT LEDGER ENTRIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Project M1 Escrow Release", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                            Text("TRX_ID: SETU88290X1", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text("+₹1,08,000", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TealSuccess)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Platform Management Commission (8%)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                            Text("Deducted from gross payout", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text("-₹8,640", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Section 194C TDS (Tax Withheld 1%)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                            Text("Remitted directly to Income Tax portal", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text("-₹1,080", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}
