package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarketplaceRepository(private val dao: MarketplaceDao) {

    // User Operations
    fun getUserById(userId: String): Flow<UserEntity?> = dao.getUserByIdFlow(userId)

    suspend fun getDirectUser(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserById(userId)
    }

    suspend fun registerOrUpdateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.insertUser(user)
    }

    // Providers
    fun getAllProviders(): Flow<List<ProviderEntity>> = dao.getAllProvidersFlow()
    fun getProviderDetails(userId: String): Flow<ProviderEntity?> = dao.getProviderByIdFlow(userId)

    suspend fun updateProviderBalance(userId: String, amount: Double) = withContext(Dispatchers.IO) {
        dao.updateProviderBalance(userId, amount)
    }

    // Requirements & Leads
    fun getAllRequirements(): Flow<List<RequirementEntity>> = dao.getAllRequirementsFlow()
    fun getMyRequirements(homeownerId: String): Flow<List<RequirementEntity>> = dao.getRequirementsForHomeownerFlow(homeownerId)

    suspend fun createRequirement(req: RequirementEntity): Int = withContext(Dispatchers.IO) {
        val reqId = dao.insertRequirement(req).toInt()

        // Auto-match system (Lead Engine simulating Phase 1-2 SLA routing):
        // Automatically assign this posted requirement as a lead to a matching contractor in background!
        val categories = listOf("Civil Contractor", "Interior Design", "Renovation")
        val matchingProviderId = when {
            req.categoryId.contains("civil") -> "provider_1"
            req.categoryId.contains("interior") -> "provider_3"
            else -> "provider_2"
        }

        // Create the lead
        dao.insertLead(
            LeadEntity(
                requirementId = reqId,
                providerId = matchingProviderId,
                status = "PENDING"
            )
        )
        reqId
    }

    suspend fun updateRequirementStatus(reqId: Int, status: String) = withContext(Dispatchers.IO) {
        dao.updateRequirementStatus(reqId, status)
    }

    // Leads & Bids
    fun getMyLeads(providerId: String): Flow<List<LeadEntity>> = dao.getLeadsForProviderFlow(providerId)

    suspend fun getLead(leadId: Int): LeadEntity? = withContext(Dispatchers.IO) {
        dao.getLeadById(leadId)
    }

    suspend fun updateLeadStatus(leadId: Int, status: String) = withContext(Dispatchers.IO) {
        dao.updateLeadStatus(leadId, status, System.currentTimeMillis())
        val lead = dao.getLeadById(leadId)
        if (lead != null && status == "DECLINED") {
            // Update associated requirement/quotes if declined
        }
    }

    // Quotes
    fun getQuotesForLead(leadId: Int): Flow<List<QuoteEntity>> = dao.getQuotesForLeadFlow(leadId)
    fun getMySubmittedQuotes(providerId: String): Flow<List<QuoteEntity>> = dao.getQuotesForProviderFlow(providerId)

    suspend fun submitQuote(quote: QuoteEntity): Int = withContext(Dispatchers.IO) {
        // Change related lead status to ACCEPTED
        val quoteId = dao.insertQuote(quote).toInt()
        dao.updateLeadStatus(quote.leadId, "ACCEPTED", System.currentTimeMillis())
        // Update requirement status to MATCHED (at least one response received!)
        val lead = dao.getLeadById(quote.leadId)
        if (lead != null) {
            dao.updateRequirementStatus(lead.requirementId, "MATCHED")
        }
        quoteId
    }

    suspend fun acceptQuotation(quoteId: Int) = withContext(Dispatchers.IO) {
        dao.updateQuoteStatus(quoteId, "ACCEPTED")
        val quote = dao.getQuoteById(quoteId)
        if (quote != null) {
            val lead = dao.getLeadById(quote.leadId)
            if (lead != null) {
                // Update requirement to active progress
                dao.updateRequirementStatus(lead.requirementId, "IN_PROGRESS")

                // Create active Project with 3 escrow Milestones as defined in payment lifecycle
                val milestonesJson = """
                    [
                        {"title": "Milestone 1: Foundation & Framing Construction", "percent": 30, "status": "PENDING", "cost": ${quote.totalAmount * 0.30}},
                        {"title": "Milestone 2: Lintels, Roofing & Plumbing Works", "percent": 40, "status": "PENDING", "cost": ${quote.totalAmount * 0.40}},
                        {"title": "Milestone 3: Exterior Plastering & Cleaning Handover", "percent": 30, "status": "PENDING", "cost": ${quote.totalAmount * 0.30}}
                    ]
                """.trimIndent()

                val projectId = dao.insertProject(
                    ProjectEntity(
                        requirementId = lead.requirementId,
                        quotationId = quoteId,
                        providerId = quote.providerId,
                        providerName = quote.providerName,
                        homeownerId = "homeowner_demo", // Assuming demo user
                        totalAmount = quote.totalAmount,
                        milestonesJson = milestonesJson,
                        status = "IN_PROGRESS"
                    )
                ).toInt()

                // Register initial escrow payment order
                dao.insertPayment(
                    PaymentEntity(
                        projectId = projectId,
                        milestoneIndex = 0,
                        amount = quote.totalAmount * 0.30,
                        razorpayOrderId = "order_rzp_${System.currentTimeMillis()}",
                        status = "CREATED"
                    )
                )
            }
        }
    }

    // Projects
    fun getActiveProjectsHomeowner(homeownerId: String): Flow<List<ProjectEntity>> = dao.getProjectsForHomeownerFlow(homeownerId)
    fun getActiveProjectsProvider(providerId: String): Flow<List<ProjectEntity>> = dao.getProjectsForProviderFlow(providerId)

    suspend fun getProjectById(projectId: Int): ProjectEntity? = withContext(Dispatchers.IO) {
        dao.getProjectById(projectId)
    }

    suspend fun releaseMilestone(projectId: Int, index: Int, paymentId: String) = withContext(Dispatchers.IO) {
        val project = dao.getProjectById(projectId)
        if (project != null) {
            // Milestone is marked COMPLETED
            // In product: Release payout from escrow to Provider linked account
            val updatedMilestones = project.milestonesJson.replace(
                "\"status\": \"PENDING\"",
                "\"status\": \"RELEASED\"" // Simple simulation
            )

            // Update local provider's wallet balance
            val releaseAmount = when (index) {
                0 -> project.totalAmount * 0.30
                1 -> project.totalAmount * 0.40
                else -> project.totalAmount * 0.30
            }

            dao.updateProjectStatus(projectId, if (index == 2) "COMPLETED" else "IN_PROGRESS", index + 1)

            val provider = dao.getProviderById(project.providerId)
            if (provider != null) {
                dao.updateProviderBalance(project.providerId, provider.earningsBalance + releaseAmount)
            }

            dao.insertPayment(
                PaymentEntity(
                    projectId = projectId,
                    milestoneIndex = index,
                    amount = releaseAmount,
                    razorpayOrderId = "order_rzp_${System.currentTimeMillis()}",
                    razorpayPaymentId = paymentId,
                    status = "RELEASED"
                )
            )
        }
    }

    // Reviews
    fun getProviderReviews(providerId: String): Flow<List<ReviewEntity>> = dao.getReviewsForProviderFlow(providerId)

    suspend fun submitReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        dao.insertReview(review)
    }
}
