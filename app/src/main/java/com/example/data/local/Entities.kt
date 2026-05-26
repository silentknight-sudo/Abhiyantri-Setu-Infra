package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

enum class UserRole {
    HOMEOWNER,
    PROVIDER,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val cityId: String,
    val cityName: String,
    val lastActiveAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "providers")
data class ProviderEntity(
    @PrimaryKey val userId: String,
    val categoriesString: String, // Comma-separated categories
    val subCategoriesString: String,
    val rating: Float,
    val jobCount: Int,
    val verificationLevel: String, // "Aadhaar Verified", "Bronze", "Silver", "Gold"
    val earningsBalance: Double,
    val bankAccountMasked: String = "Sbi - ******4321"
)

@Entity(tableName = "requirements")
data class RequirementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val homeownerId: String,
    val categoryId: String,
    val categoryName: String,
    val cityId: String,
    val title: String,
    val description: String,
    val budgetMin: Double,
    val budgetMax: Double,
    val status: String, // "POSTED", "MATCHED", "IN_PROGRESS", "CLOSED"
    val photosString: String, // Comma-separated photo urls/local assets
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requirementId: Int,
    val providerId: String,
    val status: String, // "PENDING", "ACCEPTED", "DECLINED"
    val assignedAt: Long = System.currentTimeMillis(),
    val respondedAt: Long = 0L
)

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leadId: Int,
    val providerId: String,
    val providerName: String,
    val providerRating: Float,
    val totalAmount: Double,
    val timelineDays: Int,
    val breakdownJson: String, // JSON description of line-items
    val notes: String,
    val status: String, // "SUBMITTED", "ACCEPTED", "REJECTED"
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val requirementId: Int,
    val quotationId: Int,
    val providerId: String,
    val providerName: String,
    val homeownerId: String,
    val totalAmount: Double,
    val milestonesJson: String, // Escrow milestone checkpoints
    val activeMilestoneIndex: Int = 0,
    val status: String, // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "DISPUTED"
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = 0L
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val milestoneIndex: Int,
    val amount: Double,
    val razorpayOrderId: String,
    val razorpayPaymentId: String = "",
    val status: String, // "CREATED", "ESCROW_HELD", "RELEASED", "DISPUTED"
    val capturedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val reviewerId: String,
    val reviewerName: String,
    val providerId: String,
    val rating: Float,
    val title: String,
    val comment: String,
    val createdTime: Long = System.currentTimeMillis()
)
