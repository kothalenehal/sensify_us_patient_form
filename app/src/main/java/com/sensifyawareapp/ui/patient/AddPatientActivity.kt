package com.sensifyawareapp.ui.patient
import PatientRequest
import PatientResponse
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.widget.Button
import android.graphics.Bitmap
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import android.widget.ImageView
import com.google.gson.GsonBuilder
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.api.ApiInterface
import com.sensifyawareapp.api.ApiManager
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.ApiConstants
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.OkHttpClient


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class AddPatientActivity : AppCompatActivity() {
    private lateinit var apiService: ApiInterface
    lateinit var apiManager: ApiManager
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        Log.d("AddPatient", "hereee ApiManager initialized")

        // Initialize Retrofit
        // Initialize Retrofit with error handling
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    try {
                        chain.proceed(chain.request())
                    } catch (e: Exception) {
                        Log.e("AddPatient", "hereee Network error", e)
                        throw e
                    }
                }
                .build())
            .build()

        apiService = retrofit.create(ApiInterface::class.java)

        apiManager = ApiManager(apiService, apiService) // Assuming patientApiService is also of type ApiInterface



        // Initialize EditText fields
        val firstNameField: EditText = findViewById(R.id.et_first_name)
        val lastNameField: EditText = findViewById(R.id.et_last_name)
        val dateOfBirthField: EditText = findViewById(R.id.date_of_birth_field)
        val addressField1: EditText = findViewById(R.id.et_address_line1)
        val addressField2: EditText = findViewById(R.id.et_address_line2)
        val cityField: EditText = findViewById(R.id.et_city)
        val pinCodeField: EditText = findViewById(R.id.et_pin_code)
        val stateField: EditText = findViewById(R.id.et_state)
        val medicationField: EditText = findViewById(R.id.et_Medication)
        val personalHealthConditionField: EditText = findViewById(R.id.et_known_personal_health_conditions)
        val familyHealthConditionField: EditText = findViewById(R.id.et_known_family_health_condition)
        val professionField: EditText = findViewById(R.id.et_prof)
        //val ethnicityField: EditText = findViewById(R.id.et_ethni)

        // Initialize Spinner for Gender and Education
        val genderSpinner: Spinner = findViewById(R.id.gender)
        val educationSpinner: Spinner = findViewById(R.id.education_spinner)
        val smokeSpinner: Spinner = findViewById(R.id.smoke_spinner)
        val ethnicitySpinner: Spinner = findViewById(R.id.ethnicity_spinner)

        val generateQRButton: Button = findViewById(R.id.btn_generate_qr)
        generateQRButton.isEnabled = false




        val qrCodeImage: ImageView = findViewById(R.id.qr_code_image)

        val clearButton: Button = findViewById(R.id.btn_discard_form)

        val discardButton: Button = findViewById(R.id.btn_discard_form_new)

        var prefUtils: PrefUtils = SensifyAwareApplication.getAppComponent().providePrefUtil()



        fun getUserId(context: Context): String {
            val userId = prefUtils.getIntData(
                context,
                AppConstant.SharedPreferences.USER_ID
            )

            // Check if userId is null or empty and handle it accordingly
            return userId.toString()
        }




        val userId = getUserId(this) // Pass your activity/fragment context

        clearButton.setOnClickListener {
            // Clear all EditText fields
            Log.d("AddPatient", "Clear button pressed")
            clearForm()
            generateQRButton.isEnabled = false
            saveButton.isEnabled = true
            saveButton.alpha = 1.0f

        }

        discardButton.setOnClickListener {
            finish()
        }


        generateQRButton.setOnClickListener {
            val formData = collectFormData(
                firstNameField,
                lastNameField,
                dateOfBirthField,
                addressField1,
                addressField2,
                cityField,
                pinCodeField,
                stateField,
                medicationField,
                personalHealthConditionField,
                familyHealthConditionField,
                genderSpinner,
                educationSpinner,
                professionField,
                ethnicitySpinner,
                smokeSpinner
            )
            val qrBitmap = generateQRCode(formData)


            qrCodeImage.setImageBitmap(qrBitmap)
        }

        // Date of Birth Picker
        dateOfBirthField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                    dateOfBirthField.setText(formattedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        // Save Form Button
//        val saveButton: View = findViewById(R.id.btn_save_form)
        saveButton = findViewById(R.id.btn_save_form)

        saveButton.setOnClickListener {
            validateForm(firstNameField, lastNameField, dateOfBirthField, addressField1, addressField2,
                cityField, pinCodeField, stateField, medicationField, personalHealthConditionField,
                familyHealthConditionField, genderSpinner, educationSpinner, smokeSpinner, professionField, ethnicitySpinner)

            if (isFormValid(firstNameField, lastNameField, dateOfBirthField, addressField1, addressField2,
                    cityField, pinCodeField, stateField, medicationField, personalHealthConditionField,
                    familyHealthConditionField, genderSpinner, educationSpinner,  smokeSpinner, professionField, ethnicitySpinner)) {

                submitPatientData(
                    firstName = firstNameField.text.toString(),
                    lastName = lastNameField.text.toString(),
                    gender = genderSpinner.selectedItem.toString(),
                    dateOfBirth = dateOfBirthField.text.toString(),
                    city = cityField.text.toString(),
                    addressLine2 = addressField2.text.toString(),
                    knownFamilyHealthCondition = familyHealthConditionField.text.toString(),
                    medicalHistoryDescription = personalHealthConditionField.text.toString(),
                    knownPersonalHealthCondition = personalHealthConditionField.text.toString(),
                    pinCode = pinCodeField.text.toString(),
                    medication = medicationField.text.toString(),
//                    addressLine1 = addressField1.text.toString(),
                    educationLevel = educationSpinner.selectedItem.toString(),
                    profession = professionField.text.toString(),
                    ethnicity = ethnicitySpinner.selectedItem.toString(),
                    isSmoker = smokeSpinner.selectedItem.toString(),
                    state = stateField.text.toString(),
                    userId = userId.toInt()
                )
                generateQRButton.isEnabled = true

            }
        }
    }

    private fun clearForm() {
        // Clear all EditText fields
        findViewById<EditText>(R.id.et_first_name).text.clear()
        findViewById<EditText>(R.id.et_last_name).text.clear()
        findViewById<EditText>(R.id.date_of_birth_field).text.clear()
        findViewById<EditText>(R.id.et_address_line1).text.clear()
        findViewById<EditText>(R.id.et_address_line2).text.clear()
        findViewById<EditText>(R.id.et_city).text.clear()
        findViewById<EditText>(R.id.et_pin_code).text.clear()
        findViewById<EditText>(R.id.et_state).text.clear()
        findViewById<EditText>(R.id.et_Medication).text.clear()
        findViewById<EditText>(R.id.et_known_personal_health_conditions).text.clear()
        findViewById<EditText>(R.id.et_known_family_health_condition).text.clear()
        findViewById<EditText>(R.id.et_prof).text.clear()
        findViewById<EditText>(R.id.et_lang).text.clear()

        // Reset spinners
        findViewById<Spinner>(R.id.gender).setSelection(0)
        findViewById<Spinner>(R.id.education_spinner).setSelection(0)
        findViewById<Spinner>(R.id.smoke_spinner).setSelection(0)
        findViewById<Spinner>(R.id.ethnicity_spinner).setSelection(0)

        // Clear any error states
        val editTextIds = listOf(
            R.id.et_first_name,
            R.id.et_last_name,
            R.id.date_of_birth_field,
            R.id.et_address_line1,
            R.id.et_address_line2,
            R.id.et_city,
            R.id.et_pin_code,
            R.id.et_state,
            R.id.et_Medication,
            R.id.et_known_personal_health_conditions,
            R.id.et_known_family_health_condition,
            R.id.et_prof
        )
        editTextIds.forEach {
            findViewById<EditText>(it).error = null
        }

        // Optional: Reset the QR Code image if any was generated
        val qrCodeImage: ImageView = findViewById(R.id.qr_code_image)
        qrCodeImage.setImageBitmap(null)

    }

    private fun submitPatientData(
        firstName: String,
        lastName: String,
        gender: String,
        dateOfBirth: String,
        city: String,
        addressLine2: String,
        knownFamilyHealthCondition: String,
        medicalHistoryDescription: String,
        knownPersonalHealthCondition: String,
        pinCode: String,
        medication: String,
        educationLevel: String,
        profession: String,
        ethnicity: String,
        isSmoker: String,
        state: String,
        userId: Int
    ) {
        val patientRequest = PatientRequest(
            Age = calculateAge(dateOfBirth),
            FirstName = firstName,
            LastName = lastName,
            Gender = gender,
            DateOfBirth = formatDateForApi(dateOfBirth),
            City = city,
            DefaultAddress = addressLine2,
            FamilyHealthHistory = knownFamilyHealthCondition,
            MedicalHistoryDescription = medicalHistoryDescription,
            PersonalHealthHistory = knownPersonalHealthCondition,
            ZipCode = pinCode,
            Medication = medication,
            EducationLevel = educationLevel,
            Profession = profession,
            Ethnicity = ethnicity,
            IsSmoker = isSmoker.toBooleanStrictOrNull() ?: false, // Ensures valid boolean
            State = state,
            UserId = userId,
            PatientId = generatePatientId(),
            Url = "/create-participant", // Default API endpoint
            LanguageFluency = "Hindi" // Default or dynamic value
        )

        Log.d("AddPatient", "hereee About to make API call with request: $patientRequest")
        println("hereee Data being submitted: $patientRequest")

        // Make API call

        try {
            apiManager.createPatient(patientRequest).enqueue(object : Callback<PatientResponse> {
                override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Toast.makeText(
                            this@AddPatientActivity,
                            "Patient created successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        saveButton.isEnabled = false
                        saveButton.alpha = 0.5f



                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(
                            this@AddPatientActivity,
                            "Error: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }



                override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddPatientActivity,
                        "API call failed: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        } catch (e: Exception) {
            Log.e("AddPatient", "hereee Exception while making API call", e)
        }
    }

    private fun calculateAge(dateOfBirth: String): Int {
        // Parse the date string (MM/DD/YYYY)
        val parts = dateOfBirth.split("/")
        val calendar = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()
        birthCalendar.set(parts[2].toInt(), parts[0].toInt() - 1, parts[1].toInt())

        var age = calendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (calendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun formatDateForApi(dateOfBirth: String): String {
        // Convert from MM/DD/YYYY to YYYY-MM-DD
        val parts = dateOfBirth.split("/")
        return "${parts[2]}-${parts[0].padStart(2, '0')}-${parts[1].padStart(2, '0')}"
    }



    private fun generatePatientId(): String {
        // Implement your patient ID generation logic
        return "patient_${System.currentTimeMillis()}"
    }

    private fun isFormValid(
        firstNameField: EditText,
        lastNameField: EditText,
        dateOfBirthField: EditText,
        addressField1: EditText,
        addressField2: EditText,
        cityField: EditText,
        pinCodeField: EditText,
        stateField: EditText,
        medicationField: EditText,
        personalHealthConditionField: EditText,
        familyHealthConditionField: EditText,
        genderSpinner: Spinner,
        educationSpinner: Spinner,
        smokeSpinner: Spinner,
        professionField: EditText,
        ethnicitySpinner: Spinner
    ): Boolean {
        return !firstNameField.text.isNullOrEmpty() &&
                !lastNameField.text.isNullOrEmpty() &&
                !dateOfBirthField.text.isNullOrEmpty() &&
                !addressField1.text.isNullOrEmpty() &&
                !addressField2.text.isNullOrEmpty() &&
                !cityField.text.isNullOrEmpty() &&
                !pinCodeField.text.isNullOrEmpty() &&
                !stateField.text.isNullOrEmpty() &&
                !medicationField.text.isNullOrEmpty() &&
                !personalHealthConditionField.text.isNullOrEmpty() &&
                !familyHealthConditionField.text.isNullOrEmpty() &&
                genderSpinner.selectedItemPosition != 0 &&
                educationSpinner.selectedItemPosition != 0 &&
                smokeSpinner.selectedItemPosition!= 0 &&
               !professionField.text.isNullOrEmpty() &&
                ethnicitySpinner.selectedItemPosition!= 0

    }

    private fun validateForm(
        firstNameField: EditText,
        lastNameField: EditText,
        dateOfBirthField: EditText,
        addressField1: EditText,
        addressField2: EditText,
        cityField: EditText,
        pinCodeField: EditText,
        stateField: EditText,
        medicationField: EditText,
        personalHealthConditionField: EditText,
        familyHealthConditionField: EditText,
        genderSpinner: Spinner,
        educationSpinner: Spinner,
        smokeSpinner: Spinner,
        professionField: EditText,
        ethnicitySpinner: Spinner
    ) {
        if (firstNameField.text.isNullOrEmpty()) firstNameField.error = "First name is required"
        if (lastNameField.text.isNullOrEmpty()) lastNameField.error = "Last name is required"
        if (dateOfBirthField.text.isNullOrEmpty()) dateOfBirthField.error = "Date of birth is required"
        if (addressField1.text.isNullOrEmpty()) addressField1.error = "Address line 1 is required"
        if (addressField2.text.isNullOrEmpty()) addressField2.error = "Address line 2 is required"
        if (cityField.text.isNullOrEmpty()) cityField.error = "City is required"
        if (pinCodeField.text.isNullOrEmpty()) pinCodeField.error = "Pin code is required"
        if (stateField.text.isNullOrEmpty()) stateField.error = "State is required"
        if (medicationField.text.isNullOrEmpty()) medicationField.error = "Medication is required"
        if (personalHealthConditionField.text.isNullOrEmpty()) personalHealthConditionField.error = "Known personal health condition is required"
        if (familyHealthConditionField.text.isNullOrEmpty()) familyHealthConditionField.error = "Known family health condition is required"
        if (genderSpinner.selectedItemPosition == 0) Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
        if (educationSpinner.selectedItemPosition == 0) Toast.makeText(this, "Please select education level", Toast.LENGTH_SHORT).show()
        if (smokeSpinner.selectedItemPosition == 0) Toast.makeText(this, "Please select whether you smoke", Toast.LENGTH_SHORT).show()
        if (professionField.text.isNullOrEmpty()) professionField.error = "Profession is required"
        if (ethnicitySpinner.selectedItemPosition == 0) Toast.makeText(this, "Please select ethnicity", Toast.LENGTH_SHORT).show()
    }

    private fun collectFormData(
        firstNameField: EditText,
        lastNameField: EditText,
        dateOfBirthField: EditText,
        addressField1: EditText,
        addressField2: EditText,
        cityField: EditText,
        pinCodeField: EditText,
        stateField: EditText,
        medicationField: EditText,
        personalHealthConditionField: EditText,
        familyHealthConditionField: EditText,
        genderSpinner: Spinner,
        educationSpinner: Spinner,
        professionField: EditText,
        ethnicitySpinner: Spinner,
        smokeSpinner: Spinner
    ): String {
        return """
            First Name: ${firstNameField.text}
            Last Name: ${lastNameField.text}
            Date of Birth: ${dateOfBirthField.text}
             Address Line 1: ${addressField1.text}
            Address Line 2: ${addressField2.text}
            
            City:  ${cityField.text}
            Pin Code: ${pinCodeField.text}
            State: ${stateField.text}
            Medication: ${medicationField.text}
            Known Personal Health Condition: ${personalHealthConditionField.text}
            Known Family Health Condition: ${familyHealthConditionField.text}
            Gender: ${genderSpinner.selectedItem}
            Education: ${educationSpinner.selectedItem}
            Smoke: ${smokeSpinner.selectedItem}
            Profession: ${professionField.text}
            Ethnicity: ${ethnicitySpinner.selectedItem}
            
        """.trimIndent()
    }

    private fun generateQRCode(data: String): Bitmap? {
        val size = 512 // Pixels
        return try {
            val bitMatrix: BitMatrix =
                com.google.zxing.qrcode.QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

}