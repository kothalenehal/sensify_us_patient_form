package com.sensifyawareapp.ui.scentaware

class TestData {
    var age = 0.0
    var Gender = 0

    var StuffyNose = 0
    var LossOfSmell = 0
    var LossOfMemory = 0
    var Medication = ""
    var IsStuffyNose = false
    var TotalTime_mins = 0.0f

    var Smell_Intensity_Score = 0.0f //count for right answers in intensity test
    var time_for_question_one = 0.0f  //seconds, time took to answer 1st question
    var time_for_question_two = 0.0f  //seconds, time took to answer 2nd question

    var smell_discrimination_score = 0.0f //count for right answers in discrimination test
    var time_for_question_one1 = 0.0f  //seconds, time took to answer 1st question
    var time_for_question_two1 = 0.0f  //seconds, time took to answer 2nd question

    var Odor_Identification = 0.0f //percent, (total correct answers / total asked answers)*100
    var test_retest_corelation_score = 0.0f  //percent, (corelationCount/selected_kit_size) * 100
    var corelationCount = 0

    var odors = ArrayList<Odor>()
    var Condition = "normal"

    override fun toString(): String {
        var odor = ""
        for (i in 0..15) {
            odor += odors[i].toString()
        }
        return "age - $age\nGender - $Gender\nStuffyNose - $StuffyNose\nLossOfSmell - $LossOfSmell\n" +
                "LossOfMemory - $LossOfMemory\nMedication - $Medication\nTotalTime_mins - $TotalTime_mins\n" +
                "Smell_Intensity_Score - $Smell_Intensity_Score\ntime_for_question_one - $time_for_question_one\n" +
                "time_for_question_two - $time_for_question_two\nsmell_discrimination_score - $smell_discrimination_score\n" +
                "time_for_question_one1 - $time_for_question_one1\n" +
                "time_for_question_two1 - $time_for_question_two1\nOdor_Identification - $Odor_Identification" +
                "\nodors - $odor\nCondition - $Condition\ncorelationCount - $corelationCount"
    }

}

class Odor {
    var TubeId = "" //this is temp variable for mapping tubes with and without timer
    var Odor = 0 //index of correct answer
    var response_in_round_one =
        0 //(Without timer), index of selected answer by user from QR code result
    var under_stress_response =
        0 //(With Timer), index of selected answer by user from QR code result
    var response_time_round_one = 0.0f // (Without timer in seconds)
    var response_time_round_two = 0.0f // (Under stress)(With timer in seconds)

    override fun toString(): String {
        return "\nTubeId-$TubeId; Odor-$Odor; response_in_round_one-$response_in_round_one; under_stress_response-$under_stress_response; " +
                "response_time_round_one-$response_time_round_one; response_time_round_two-$response_time_round_two"
    }
}