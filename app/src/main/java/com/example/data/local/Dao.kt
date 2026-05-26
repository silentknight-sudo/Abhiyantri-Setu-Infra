package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {

    // Users
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Providers
    @Query("SELECT * FROM providers")
    fun getAllProvidersFlow(): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM providers WHERE userId = :userId LIMIT 1")
    fun getProviderByIdFlow(userId: String): Flow<ProviderEntity?>

    @Query("SELECT * FROM providers WHERE userId = :userId LIMIT 1")
    suspend fun getProviderById(userId: String): ProviderEntity?

    @Query("UPDATE providers SET earningsBalance = :newBalance WHERE userId = :userId")
    suspend fun updateProviderBalance(userId: String, newBalance: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: ProviderEntity)

    // Requirements
    @Query("SELECT * FROM requirements ORDER BY createdAt DESC")
    fun getAllRequirementsFlow(): Flow<List<RequirementEntity>>

    @Query("SELECT * FROM requirements WHERE homeownerId = :homeownerId ORDER BY createdAt DESC")
    fun getRequirementsForHomeownerFlow(homeownerId: String): Flow<List<RequirementEntity>>

    @Query("SELECT * FROM requirements WHERE id = :reqId LIMIT 1")
    suspend fun getRequirementById(reqId: Int): RequirementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(req: RequirementEntity): Long

    @Query("UPDATE requirements SET status = :status WHERE id = :id")
    suspend fun updateRequirementStatus(id: Int, status: String)

    // Leads
    @Query("SELECT * FROM leads WHERE providerId = :providerId ORDER BY assignedAt DESC")
    fun getLeadsForProviderFlow(providerId: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :leadId LIMIT 1")
    suspend fun getLeadById(leadId: Int): LeadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Query("UPDATE leads SET status = :status, respondedAt = :respondedAt WHERE id = :id")
    suspend fun updateLeadStatus(id: Int, status: String, respondedAt: Long)

    // Quotes
    @Query("SELECT * FROM quotes WHERE leadId = :leadId ORDER BY submittedAt DESC")
    fun getQuotesForLeadFlow(leadId: Int): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE providerId = :providerId ORDER BY submittedAt DESC")
    fun getQuotesForProviderFlow(providerId: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE id = :quoteId LIMIT 1")
    suspend fun getQuoteById(quoteId: Int): QuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity): Long

    @Query("UPDATE quotes SET status = :status WHERE id = :id")
    suspend fun updateQuoteStatus(id: Int, status: String)

    // Projects
    @Query("SELECT * FROM projects WHERE homeownerId = :homeownerId ORDER BY startDate DESC")
    fun getProjectsForHomeownerFlow(homeownerId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE providerId = :providerId ORDER BY startDate DESC")
    fun getProjectsForProviderFlow(providerId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: Int): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Query("UPDATE projects SET status = :status, activeMilestoneIndex = :milestoneIndex WHERE id = :id")
    suspend fun updateProjectStatus(id: Int, status: String, milestoneIndex: Int)

    // Payments
    @Query("SELECT * FROM payments WHERE projectId = :projectId ORDER BY milestoneIndex ASC")
    fun getPaymentsForProjectFlow(projectId: Int): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query("UPDATE payments SET status = :status, razorpayPaymentId = :paymentId WHERE id = :id")
    suspend fun updatePaymentStatus(id: Int, status: String, paymentId: String)

    // Reviews
    @Query("SELECT * FROM reviews WHERE providerId = :providerId ORDER BY createdTime DESC")
    fun getReviewsForProviderFlow(providerId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}
