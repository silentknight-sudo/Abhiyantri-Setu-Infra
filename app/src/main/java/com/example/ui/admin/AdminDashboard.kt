package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.*
import com.example.data.repository.MarketplaceRepository
import com.example.ui.components.BrandLogo
import com.example.ui.components.GeometricBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    repository: MarketplaceRepository,
    currentUser: UserEntity,
    onLogout: () -> Unit,
    coroutineScope: CoroutineScope
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: KYC Queue, 1: SLA Map, 2: Disputes

    // In-memory admin mutable state representing KYC updates
    var pendingKycList by remember {
        mutableStateOf(
            listOf(
                KycItem("VJ901", "Vijay Kumar (Vijay Civil & Engineers)", "Greater Noida", "Aadhaar Card, MCD Grade License", "PENDING_REVIEW"),
                KycItem("SN200", "Sneha Patel (Spacio Designs)", "Noida Area", "Aadhaar Card, PAN Card, GSTIN Document", "PENDING_REVIEW")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandLogo(showText = false, heightDp = 36)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abhiyantri Control Portal", fontWeight = FontWeight.Bold, color = NavyPrimary)
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
            // Stats Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PLATFORM ESCROW POOL VOLUME", fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("₹12,45,000", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Active Slabs Casting", fontSize = 9.sp, color = Color.LightGray)
                            Text("28 Projects", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Auto-Match SLA Time", fontSize = 9.sp, color = Color.LightGray)
                            Text("12.4 Mins (NCR)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TealSuccess)
                        }
                        Column {
                            Text("Contractor Nodes", fontSize = 9.sp, color = Color.LightGray)
                            Text("1,842 verified", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Quick Filters Navigation row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { selectedSection = 0 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedSection == 0) NavyPrimary.copy(alpha = 0.1f) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (selectedSection == 0) NavyPrimary else Color.LightGray),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("KYC Approval", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = { selectedSection = 1 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedSection == 1) NavyPrimary.copy(alpha = 0.1f) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (selectedSection == 1) NavyPrimary else Color.LightGray),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SLA Tracker", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = { selectedSection = 2 },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedSection == 2) NavyPrimary.copy(alpha = 0.1f) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (selectedSection == 2) NavyPrimary else Color.LightGray),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dispute Desk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body content routing
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (selectedSection) {
                    0 -> {
                        item {
                            Text("Pending Aadhaar/MCD Verifications Queue", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 14.sp)
                            Text("Admins must audit uploaded license details prior to granting Gold Contractor levels.", fontSize = 11.sp, color = Color.Gray)
                        }

                        if (pendingKycList.none { it.status == "PENDING_REVIEW" }) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("KYC queue fully vetted! Clean inbox.", fontWeight = FontWeight.Bold, color = TealSuccess)
                                }
                            }
                        }

                        items(pendingKycList) { kyc ->
                            if (kyc.status == "PENDING_REVIEW") {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(kyc.agencyName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyPrimary)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(OrangeAccent.copy(alpha = 0.12f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("PENDING", color = OrangeAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Region: ${kyc.city} NCR", fontSize = 11.sp, color = Color.Gray)
                                        Text("Documents Attached: ${kyc.documents}", fontSize = 11.sp, color = Color.DarkGray)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row {
                                            OutlinedButton(
                                                onClick = {
                                                    pendingKycList = pendingKycList.map {
                                                        if (it.id == kyc.id) it.copy(status = "REJECTED") else it
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Reject Spec", fontSize = 11.sp)
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Button(
                                                onClick = {
                                                    pendingKycList = pendingKycList.map {
                                                        if (it.id == kyc.id) it.copy(status = "VERIFIED") else it
                                                    }
                                                },
                                                modifier = Modifier.weight(1.2f),
                                                colors = ButtonDefaults.buttonColors(containerColor = TealSuccess),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Approve Active Verification", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        item {
                            Text("SLA Performance & Auto-Matching Metrics", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 14.sp)
                            Text("Active geo-matching thresholds for sub-districts.", fontSize = 11.sp, color = Color.Gray)
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Sector 12, Greater Noida", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                                        Text("14 active leads dispatched", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Average Contractor Response SLA: 8.2 mins (Target: 30m)", fontSize = 11.sp, color = TealSuccess)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Sector 150, Noida", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyPrimary)
                                        Text("29 active leads dispatched", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Average Contractor Response SLA: 16.5 mins (Target: 30m)", fontSize = 11.sp, color = TealSuccess)
                                }
                            }
                        }
                    }

                    2 -> {
                        item {
                            Text("Escrow Support & Complaint Dispatch Desk", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 14.sp)
                            Text("Real-time ticket ledger for milestone payment disputes.", fontSize = 11.sp, color = Color.Gray)
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Ticket #DIS_8829", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Red)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(OrangeAccent.copy(alpha = 0.12f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("OPEN DISPUTE", color = OrangeAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Project Client: Karan Malhotra", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Complaint: 'Contractor did not finish curing layer for Milestone 2 but requesting escrow funds release.'", fontSize = 11.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Contractor Response: 'Curing was completed. 3 photographs sent with timestamp verification app tags.'", fontSize = 11.sp, color = Color.Gray)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { /* Arbitrate simulation */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Dispatch Field Arbitrator to Noida Site", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
}

data class KycItem(
    val id: String,
    val agencyName: String,
    val city: String,
    val documents: String,
    val status: String
)
