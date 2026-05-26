package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProviderEntity::class,
        RequirementEntity::class,
        LeadEntity::class,
        QuoteEntity::class,
        ProjectEntity::class,
        PaymentEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): MarketplaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "abhiyantrisetu_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.dao())
                }
            }
        }

        private suspend fun populateInitialData(dao: MarketplaceDao) {
            // Pre-seed high-fidelity, professional-looking service providers matching the blueprint
            val users = listOf(
                UserEntity(
                    id = "provider_1",
                    phone = "+919876543210",
                    email = "amit.sharma@abhiyantrisetu.com",
                    name = "Amit Sharma (Noida BuildCon Corp)",
                    role = UserRole.PROVIDER,
                    cityId = "noida",
                    cityName = "Noida"
                ),
                UserEntity(
                    id = "provider_2",
                    phone = "+919911223344",
                    email = "vijay.kr@setuinfra.co.in",
                    name = "Vijay Kumar (Vijay Civil & Engineers)",
                    role = UserRole.PROVIDER,
                    cityId = "g_noida",
                    cityName = "Greater Noida"
                ),
                UserEntity(
                    id = "provider_3",
                    phone = "+919555666777",
                    email = "sneha.contractor@gmail.com",
                    name = "Sneha Patel (Spacio Designs)",
                    role = UserRole.PROVIDER,
                    cityId = "noida",
                    cityName = "Noida Area"
                ),
                UserEntity(
                    id = "homeowner_demo",
                    phone = "+919000000001",
                    email = "project_owner@abhiyantrisetu.com",
                    name = "Ramesh Chand (Homeowner)",
                    role = UserRole.HOMEOWNER,
                    cityId = "g_noida",
                    cityName = "Greater Noida"
                ),
                UserEntity(
                    id = "admin_demo",
                    phone = "+919999999999",
                    email = "admin@abhiyantrisetu.com",
                    name = "Ashok Malhotra (Global Lead Admin)",
                    role = UserRole.ADMIN,
                    cityId = "delhi_ncr",
                    cityName = "Delhi NCR"
                )
            )

            for (user in users) {
                dao.insertUser(user)
            }

            // Populate corresponding provider portfolios and metrics
            dao.insertProvider(
                ProviderEntity(
                    userId = "provider_1",
                    categoriesString = "Civil Contractor, Foundation, Masonry",
                    subCategoriesString = "Excavation, RCC Columns, Plastering, Brickwork",
                    rating = 4.9f,
                    jobCount = 42,
                    verificationLevel = "Gold (Aadhaar Verified & Escrow Bonded)",
                    earningsBalance = 245000.0
                )
            )

            dao.insertProvider(
                ProviderEntity(
                    userId = "provider_2",
                    categoriesString = "Renovation, Structural Engineer, Civil Design",
                    subCategoriesString = "Home Renovation, Beam-Slab Casting, Soil Testing",
                    rating = 4.7f,
                    jobCount = 18,
                    verificationLevel = "Silver (Aadhaar Verified)",
                    earningsBalance = 89000.0
                )
            )

            dao.insertProvider(
                ProviderEntity(
                    userId = "provider_3",
                    categoriesString = "Interior Design, Architecture, Wood Works",
                    subCategoriesString = "Modular Kitchen, False Ceiling, Italian Flooring",
                    rating = 4.8f,
                    jobCount = 29,
                    verificationLevel = "Gold (Aadhaar Verified & Escrow Bonded)",
                    earningsBalance = 175000.0
                )
            )

            // Seed user requirements to display active lead bidding
            val reqId1 = dao.insertRequirement(
                RequirementEntity(
                    homeownerId = "homeowner_demo",
                    categoryId = "civil-contractor",
                    categoryName = "Civil Contractor",
                    cityId = "g_noida",
                    title = "G+1 Residential Slab Casting & Brickwork",
                    description = "Need a premium civil contractor to execute G+1 slab casting (grade M20 concrete) and 9-inch outer fly-ash brickwork in Sector 12. Structure drawing is ready and verified by MCD engineer. Ready to cast by end of month.",
                    budgetMin = 650000.0,
                    budgetMax = 800000.0,
                    status = "MATCHED",
                    photosString = "site_drawing.jpg,site_foundation.jpg"
                )
            ).toInt()

            val reqId2 = dao.insertRequirement(
                RequirementEntity(
                    homeownerId = "homeowner_demo",
                    categoryId = "interior-designer",
                    categoryName = "Interior Design & Woodwork",
                    cityId = "noida",
                    title = "3BHK Modular Kitchen & False Ceiling Wardrobes",
                    description = "Complete interior remodeling in Sector 150 apartment. Looking for heavy commercial-grade marine plywood (ISO certified) for cabinet structures. Design is already modeled in 3D, drawings available.",
                    budgetMin = 350000.0,
                    budgetMax = 450000.0,
                    status = "POSTED",
                    photosString = "kitchen_design.jpg"
                )
            ).toInt()

            // Seed mock quotation leads
            val leadId1 = dao.insertLead(
                LeadEntity(
                    requirementId = reqId1,
                    providerId = "provider_1",
                    status = "ACCEPTED"
                )
            ).toInt()

            val leadId2 = dao.insertLead(
                LeadEntity(
                    requirementId = reqId1,
                    providerId = "provider_2",
                    status = "PENDING"
                )
            ).toInt()

            val leadId3 = dao.insertLead(
                LeadEntity(
                    requirementId = reqId2,
                    providerId = "provider_3",
                    status = "PENDING"
                )
            ).toInt()

            // Seed active bids on reqId1
            dao.insertQuote(
                QuoteEntity(
                    leadId = leadId1,
                    providerId = "provider_1",
                    providerName = "Amit Sharma (Noida BuildCon Corp)",
                    providerRating = 4.9f,
                    totalAmount = 720000.0,
                    timelineDays = 25,
                    breakdownJson = """[{"item": "RCC Column Casting (M20 grade)", "cost": 310000.0}, {"item": "Fly-ash brick boundary wall & plastering", "cost": 290000.0}, {"item": "Steel reinforcement shuttering & curing", "cost": 120000.0}]""",
                    notes = "Quotation is inclusive of labor taxes, supervising engineer daily visits, and safety checklists. Escrow released at 3-stage milestone checkpoints.",
                    status = "SUBMITTED"
                )
            )

            // Review for providers to look authentic
            dao.insertReview(
                ReviewEntity(
                    projectId = 101,
                    reviewerId = "reviewer_external",
                    reviewerName = "Karan Malhotra",
                    providerId = "provider_1",
                    rating = 5.0f,
                    title = "Superb execution on structural steel casting",
                    comment = "Amit Sharma and Noida BuildCon team casted our double storey footing within schedule. Absolute professional, daily photo updates on WhatsApp."
                )
            )
        }
    }
}
