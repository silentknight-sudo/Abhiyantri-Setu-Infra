package com.example.ui.homeowner

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.*
import com.example.data.repository.MarketplaceRepository
import com.example.ui.components.BrandLogo
import com.example.ui.components.GeometricBackground
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeownerDashboard(
    repository: MarketplaceRepository,
    currentUser: UserEntity,
    onLogout: () -> Unit,
    coroutineScope: CoroutineScope
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Contractors", "Enquiries", "Projects", "Cost Estimator")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandLogo(showText = false, heightDp = 40)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Setu Client Portal", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }
                },
                actions = {
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
                        label = { Text(title, fontSize = 10.sp) },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Search
                                    1 -> Icons.Default.Assignment
                                    2 -> Icons.Default.Engineering
                                    else -> Icons.Default.Calculate
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
            // User Meta Block
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ramesh Chand (Demo Client)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Primary Sector: Greater Noida West (UP)",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Routing body
            when (selectedTab) {
                0 -> ContractorsTab(repository, coroutineScope)
                1 -> EnquiriesTab(repository, currentUser.id, coroutineScope)
                2 -> ProjectsTab(repository, currentUser.id, coroutineScope)
                3 -> CostEstimatorTab()
            }
        }
    }
}
}

@Composable
fun ContractorsTab(repository: MarketplaceRepository, scope: CoroutineScope) {
    val providers by repository.getAllProviders().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Pre-Verified Construction Providers in Noida Area",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Every listed contracting agency undergoes intensive Aadhaar and escrow bonds checks.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (providers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        items(providers) { provider ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (provider.userId == "provider_1") "Amit Sharma (BuildCon)" else "Sneha Patel (Spacio Designs)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyPrimary
                        )

                        // Verification Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TealSuccess.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = TealSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(provider.verificationLevel, color = TealSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "${provider.rating} Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "• ${provider.jobCount} projects finished", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Expertise: ${provider.categoriesString}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Services: ${provider.subCategoriesString}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnquiriesTab(repository: MarketplaceRepository, userId: String, scope: CoroutineScope) {
    val myRequirements by repository.getMyRequirements("homeowner_demo").collectAsState(initial = emptyList())
    var showCreateForm by remember { mutableStateOf(false) }

    // Form states
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Civil Contractor") }
    var description by remember { mutableStateOf("") }
    var budgetMin by remember { mutableStateOf("250000") }
    var budgetMax by remember { mutableStateOf("450000") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Structural Enquiries",
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    fontSize = 16.sp
                )
                Button(
                    onClick = { showCreateForm = !showCreateForm },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(if (showCreateForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showCreateForm) "Hide Details" else "Post Lead", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (showCreateForm) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Post New Construction Requirement", fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Fill drawings parameters. Auto-matches within 30 minutes SLA.", fontSize = 10.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Project Title (e.g. 2 Storey Slabs)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Detailed Specifications (Grade, Area, Masonry)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            OutlinedTextField(
                                value = budgetMin,
                                onValueChange = { budgetMin = it },
                                label = { Text("Min Budget (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = budgetMax,
                                onValueChange = { budgetMax = it },
                                label = { Text("Max Budget (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (title.isNotEmpty() && description.isNotEmpty()) {
                                    scope.launch {
                                        repository.createRequirement(
                                            RequirementEntity(
                                                homeownerId = "homeowner_demo",
                                                categoryId = "civil-contractor",
                                                categoryName = category,
                                                cityId = "g_noida",
                                                title = title,
                                                description = description,
                                                budgetMin = budgetMin.toDoubleOrNull() ?: 200000.0,
                                                budgetMax = budgetMax.toDoubleOrNull() ?: 500000.0,
                                                status = "POSTED",
                                                photosString = "site_photo.jpg"
                                            )
                                        )
                                        title = ""
                                        description = ""
                                        showCreateForm = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Text("Submit to Verified Engines & Match", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(myRequirements) { req ->
            var expandedBidView by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(req.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (req.status) {
                                        "POSTED" -> OrangeAccent.copy(alpha = 0.12f)
                                        "MATCHED" -> TealSuccess.copy(alpha = 0.12f)
                                        else -> NavyPrimary.copy(alpha = 0.12f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                req.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (req.status) {
                                    "POSTED" -> OrangeAccent
                                    "MATCHED" -> TealSuccess
                                    else -> NavyPrimary
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(req.description, fontSize = 12.sp, color = Color.Gray, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Budget: ₹${req.budgetMin.toInt()} - ₹${req.budgetMax.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )

                        // Trigger quotes expand
                        Text(
                            text = "View Contractor Bids (1 Area Live)",
                            color = OrangeAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { expandedBidView = !expandedBidView }
                                .padding(4.dp)
                        )
                    }

                    AnimatedVisibility(visible = expandedBidView) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Live Quotations (Proposals Received)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Display pre-seeded quotation
                            Card(
                                colors = CardDefaults.cardColors(containerColor = LightBackground),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, GrayBorder, RoundedCornerShape(8.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Amit Sharma (BuildCon Noida)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(10.dp))
                                                Text(" 4.9 Verified Partner", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }
                                        Text("₹7,20,000", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TealSuccess)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Breakdown: RCC Pillar Foundation, Fly-ash bricks wall molding, full mechanical curing included. Guaranteed delivery in 25 working days.", fontSize = 11.sp, color = Color.DarkGray)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.acceptQuotation(1) // Demo quote ID
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Accept Bid & Open Escrow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
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
fun ProjectsTab(repository: MarketplaceRepository, userId: String, scope: CoroutineScope) {
    val projects by repository.getActiveProjectsHomeowner(userId).collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Active ESCROW Construction Projects",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Track structured billing. Approve milestones once work-in-progress is verified by site photos.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (projects.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Construction, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Active Escrow Account Held", fontWeight = FontWeight.Bold, color = NavyPrimary)
                        Text("Accept a contractor's quote in the Enquiries tab to initiate smart milestone agreements.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }
        }

        items(projects) { project ->
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
                        Column {
                            Text("PROJECT SETU-DX998", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OrangeAccent, letterSpacing = 1.sp)
                            Text("G+1 Residential Slab Casting", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TealSuccess.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(project.status, color = TealSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Assigned Contractor: ${project.providerName}", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ACTIVE TRANSACTIONAL MILESTONES (RAZORPAY ROUTE)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Milestone 1 (Always pre-seeded)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, GrayBorder, RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("M1: Foundation excavation & columns (30%)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("₹${(project.totalAmount * 0.3).toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(14.dp))
                                    Text(" Photo updates approved on site", fontSize = 10.sp, color = Color.Gray)
                                }

                                if (project.activeMilestoneIndex == 0) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                repository.releaseMilestone(project.id, 0, "pay_rzp_99x822ff")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealSuccess),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        modifier = Modifier.height(28.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Approve & Release Funds", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text("FUNDS RELEASED", color = TealSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                            .border(1.dp, GrayBorder, RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("M2: Intermediate casting & brick layer (40%)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("₹${(project.totalAmount * 0.4).toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                            }

                            if (project.activeMilestoneIndex == 1) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            repository.releaseMilestone(project.id, 1, "pay_rzp_99x823ff")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealSuccess),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Approve & Release Funds", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (project.activeMilestoneIndex > 1) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("FUNDS RELEASED", color = TealSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("NOT STARTED", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CostEstimatorTab() {
    var houseArea by remember { mutableStateOf("1200") }
    var regionRate by remember { mutableStateOf("1500") } // Noida default rate per sq ft
    var qualityLevel by remember { mutableStateOf("Premium Gold") } // Standard vs Premium

    var steelQty by remember { mutableStateOf(0.0) }
    var cementBags by remember { mutableStateOf(0.0) }
    var sandTons by remember { mutableStateOf(0.0) }
    var totalProjectEstimate by remember { mutableStateOf(0.0) }

    LaunchedEffect(houseArea, regionRate, qualityLevel) {
        val area = houseArea.toDoubleOrNull() ?: 1000.0
        val rate = regionRate.toDoubleOrNull() ?: 1400.0
        val multiplier = if (qualityLevel == "Premium Gold") 1.25 else 1.0

        // Empirical civil estimation algorithms
        totalProjectEstimate = area * rate * multiplier
        cementBags = area * 0.45 * multiplier
        steelQty = area * 4.2 * multiplier // Kgs
        sandTons = area * 0.02 * multiplier
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "AI Structural Engineering Cost Estimator",
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Simulating the Phase 3 ML Model. Generates materials breakdowns for residential complexes in Greater Noida.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimation Parameter Inputs", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = houseArea,
                        onValueChange = { houseArea = it },
                        label = { Text("Total Plot / Slab Construction Area (Sq.Ft.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = regionRate,
                        onValueChange = { regionRate = it },
                        label = { Text("Local Base Area Rate (₹/Sq.Ft.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Materials Specification Quality Preset", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = qualityLevel == "Standard Silver",
                                onClick = { qualityLevel = "Standard Silver" },
                                colors = RadioButtonDefaults.colors(selectedColor = NavyPrimary)
                            )
                            Text("Silver (ISO Standard)", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = qualityLevel == "Premium Gold",
                                onClick = { qualityLevel = "Premium Gold" },
                                colors = RadioButtonDefaults.colors(selectedColor = NavyPrimary)
                            )
                            Text("Gold (Premium Ultra)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary.copy(alpha = 0.05f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, OrangeAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ESTIMATED PROJECT BUDGET", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                        Text(
                            "₹${String.format("%,d", totalProjectEstimate.toLong())}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = OrangeAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = OrangeAccent.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Detailed Materials Requirements Forecast:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Material 1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("SRE Structural Steel (Fe 550D)", fontSize = 12.sp)
                        Text("${String.format("%,d", steelQty.toInt())} Kgs (~${String.format("%.1f", steelQty / 1000.0)} Tons)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Material 2
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("OPC Cement (Ultratech Grade 43/53)", fontSize = 12.sp)
                        Text("${cementBags.toInt()} bags of 50kg", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Material 3
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fine aggregates (Crushed stone/Sand equivalence)", fontSize = 12.sp)
                        Text("${sandTons.toInt()} brass units / tons", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(OrangeAccent.copy(alpha = 0.12f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Estimators are derived on regional specifications for Delhi NCR region in standard humidity and soil load margins.", fontSize = 9.sp, color = NavyPrimary)
                        }
                    }
                }
            }
        }
    }
}

