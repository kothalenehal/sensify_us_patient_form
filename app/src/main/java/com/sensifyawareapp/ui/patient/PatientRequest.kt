import com.google.gson.annotations.SerializedName

data class PatientRequest(
    val Age: Int,
    val FirstName: String,
    val Gender: String,
    val LastName: String,
    val Url: String,
    val UserId: Int,
    val PatientId: String,
    val MedicalHistoryDescription: String,
    val DateOfBirth: String,
    val Ethnicity: String,
    val IsSmoker: Boolean,
    val Profession: String,
    val EducationLevel: String,
    val Medication: String,
    val FamilyHealthHistory: String,
    val LanguageFluency: String,
    val PersonalHealthHistory: String,
    val City: String,
    val State: String,
    val ZipCode: String,
    val DefaultAddress: String
)


data class PatientResponse(
    val status: String,
    val message: String,
    val data: Any? // Adjust based on actual API response structure
)
