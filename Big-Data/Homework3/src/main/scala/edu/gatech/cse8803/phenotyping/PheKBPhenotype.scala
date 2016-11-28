/**
  * @author Hang Su <hangsu@gatech.edu>,
  * @author Sungtae An <stan84@gatech.edu>,
  */

package edu.gatech.cse8803.phenotyping

import java.util.Date

import edu.gatech.cse8803.model.{Diagnostic, LabResult, Medication}
import org.apache.spark.rdd.RDD

//case class All(patientID: String, medicine: String, code: String, testName: String, value: Double)

object T2dmPhenotype {

  def minDate(d1: Date, d2: Date): Date = {
    val d1t = d1.getTime()
    val d2t = d2.getTime()
    if (d1t.compareTo(d2t) > 0){return d1}
    else if (d2t.compareTo(d1t) > 0){return d2}
    return d2
  }

  def minDateMatcher(d1: Date, d2: Date, m1: String, m2: String): String = {
    if (minDate(d1,d2).equals(d1)){return m1}
    else if (minDate(d1,d2).equals(d2)){return m2}
    return null
  }

  /**
    * Transform given data set to a RDD of patients and corresponding phenotype
    *
    * @param medication medication RDD
    * @param labResult lab result RDD
    * @param diagnostic diagnostic code RDD
    * @return tuple in the format of (patient-ID, label). label = 1 if the patient is case, label = 2 if control, 3 otherwise
    */
  def transform(medication: RDD[Medication], labResult: RDD[LabResult], diagnostic: RDD[Diagnostic]): RDD[(String, Int)] = {
    /**
      * Remove the place holder and implement your code here.
      * Hard code the medication, lab, icd code etc for phenotype like example code below
      * as while testing your code we expect your function have no side effect.
      * i.e. Do NOT read from file or write file
      *
      * You don't need to follow the example placeholder codes below exactly, once you return the same type of return.
      */

    val sc = medication.sparkContext

    /** Hard code the criteria */
    val type1_dm_dx = Set("250.01", "250.03", "250.11", "250.13", "250.21", "250.23", "250.31", "250.33",
      "250.41", "250.43", "250.51", "250.53", "250.61", "250.63", "250.71", "250.73", "250.81", "250.83", "250.91",
      "250.93")
    val type1_dm_med = Set("lantus", "insulin glargine", "insulin aspart", "insulin detemir", "insulin lente",
      "insulin nph", "insulin reg", "\"insulin,ultralente\"", "insulin,ultralente")
    val type2_dm_dx = Set("250.3", "250.32", "250.2", "250.22", "250.9", "250.92", "250.8", "250.82", "250.7",
      "250.72", "250.6", "250.62", "250.5", "250.52", "250.4", "250.42", "250.00", "250.02")
    val type2_dm_med = Set("chlorpropamide", "diabinese", "diabanase", "diabinase", "glipizide", "glucotrol",
      "glucotrol XL", "glucatrol", "glyburide", "micronase", "glynase", "diabetamide", "diabeta", "glimepiride",
      "amaryl", "repaglinide", "prandin", "nateglinide", "metformin", "rosiglitazone", "acarbose", "miglitol",
      "sitagliptin", "exenatide", "tolazamide", "acetohexamide", "troglitazone", "tolbutamide", "avandia", "actos",
      "ACTOS", "glipizide")
    val dm_related_dx = Set("790.21", "790.22", "790.2", "790.29", "648.81", "648.82", "648.83", "648.84", "648.0",
      "648.00", "648.01", "648.02", "648.03", "648.04", "791.5", "277.7", "V77.1", "256.4", "250.01", "250.03", "250.13",
      "250.11", "250.21", "250.23", "250.31", "250.33", "250.41", "250.43", "250.51", "250.53", "250.61", "250.63",
      "250.71", "250.73", "250.81", "250.83", "250.91", "250.93", "250.3", "250.32", "250.2", "250.22", "250.9", "250.92",
      "250.8", "250.82", "250.7", "250.72", "250.6", "250.62", "250.5", "250.52", "250.4", "250.42", "250.00", "250.02")

    /**Set up**/
    val med = medication.map(m => (m.patientID, (m.date, m.medicine))) //medication RDD
    val lab = labResult.map(m => (m.patientID, (m.date, m.testName, m.value))) //lab RDD
    val diag = diagnostic.map(m => (m.patientID, (m.date, m.code))) //diagnostic RDD

    val labPatientsAll = lab.map(m => m._1).collect().distinct
    val diagPatients = diag.map(m => m._1).collect().distinct
    val medPatients = med.map(m => m._1).collect().distinct
    val allPatients = labPatientsAll.union(diagPatients).union(medPatients)
    val allPatientsDistinct = allPatients.distinct
    //println("all patients")
    //println(allPatientsDistinct.length)


    val diagYesType1DX = diag.filter(f => type1_dm_dx.contains(f._2._2)) //diagnostics that only have type1DMDX
    //val diagNoType1DX = diag.filter(f => !type1_dm_dx.contains(f._2._2)) //diagnostics that DO NOT have type1DMDX
    val patientsYesType1DX = diagYesType1DX.map(m => m._1) //patients with diagnostics that only have type1DMDX
    val patientsYesType1DXCollect = patientsYesType1DX.collect().distinct
    //println(patientsYesType1DXCollect.length)
    val diagNoType1DX = diag.filter(f => !patientsYesType1DXCollect.contains(f._1))
    val patientsNoType1DX = diagNoType1DX.map(m => m._1) //patients with diagnostics that DO NOT have type1DMDX
    val patientsNoType1DXCollect = patientsNoType1DX.collect().distinct
    //println(patientsNoType1DXCollect.length)
    val diagYesType2DX = diag.filter(f => type2_dm_dx.contains(f._2._2)) //diagnostics that only have type2DMDX
    //val diagNoType2DX = diag.filter(f => !type2_dm_dx.contains(f._2._2)) //diagnostics that DO NOT have type1DMDX
    val patientsYesType2DX = diagYesType2DX.map(m => m._1) //patients with diagnostics that only have type2DMDX
    val patientsYesType2DXCollect = patientsYesType2DX.collect().distinct
    val diagNoType2DX = diag.filter(f => !patientsYesType2DXCollect.contains(f._1))
    val patientsNoType2DX = diagNoType2DX.map(m => m._1) //patients with diagnostics that DO NOT have type1DMDX
    val patientsNoType2DXCollect = patientsNoType2DX.collect().distinct
    val diagDMRelated = diag.filter(f => dm_related_dx.contains(f._2._2)) //diagnostics that are related to DM
    val patientsDMRelated = diagDMRelated.map(m => m._1) //patients with diagnostics that are related to DM
    val patientsDMRelatedCollect = patientsDMRelated.collect().distinct
    val labPatients = lab.map(m => m._1)
    val labPatientsCollect = labPatients.collect().distinct

    val medYesType1Med = med.filter(f => type1_dm_med.contains(f._2._2)) //medications that only have type1DMMed
    //val medNoType1Med = med.filter(f => !type1_dm_med.contains(f._2._2)) //medications that DO NOT have type1DMMed
    val patientsYesType1Med = medYesType1Med.map(m => m._1) //patients with medications that only have type1DMMed
    val patientsYesType1MedCollect = patientsYesType1Med.collect().distinct
    val medNoType1Med = med.filter(f => !patientsYesType1MedCollect.contains(f._1))
    val patientsNoType1Med = medNoType1Med.map(m => m._1) //patients with medications that DO NOT have type1DMMed
    val patientsNoType1MedCollect = patientsNoType1Med.collect().distinct
    val medYesType2Med = med.filter(f => type2_dm_med.contains(f._2._2)) //medications that only have type2DMMed
    //val medNoType2Med = med.filter(f => !type2_dm_med.contains(f._2._2)) //medications that DO NOT have type2DMMed
    val patientsYesType2Med = medYesType2Med.map(m => m._1) //patients with medications that only have type2DMMed
    val patientsYesType2MedCollect = patientsYesType2Med.collect().distinct
//    println(medPatients.length)
//    println(patientsYesType2MedCollect.length)
    val medNoType2Med = med.filter(f => !patientsYesType2MedCollect.contains(f._1))
    val patientsNoType2Med = medNoType2Med.map(m => m._1) //patients with medications taht DO NOT have type2DMMed
    val patientsNoType2MedCollect = patientsNoType2Med.collect().distinct
//    println(patientsNoType2MedCollect.length)

    /** Find CASE Patients  */
    val caseDiagNoType1DX = diagNoType1DX
//    println("type1DMDiag")
//    println(caseDiagNoType1DX.map(m => m._1).collect().distinct.length)
    val caseDiagNoType1DXAndYesType2DX = caseDiagNoType1DX.filter(f => patientsYesType2DXCollect.contains(f._1)) //this is true from now on
    val casePatientsNoType1DXAndYesType2DX = caseDiagNoType1DXAndYesType2DX.map(m => m._1)
    val casePatientsNoType1DXAndYesType2DXCollect = casePatientsNoType1DXAndYesType2DX.collect().distinct
//    println("type2DM diag")
//    println(casePatientsNoType1DXAndYesType2DXCollect.length)

    val caseMedYesType1Med = medYesType1Med.filter(f => casePatientsNoType1DXAndYesType2DXCollect.contains(f._1))
    val casePatientsYesType1Med = caseMedYesType1Med.map(m => m._1)
    val casePatientsYesType1MedCollect = casePatientsYesType1Med.collect().distinct
//    println("yestype1med")
//    println(casePatientsYesType1MedCollect.length)

    val caseMedNoType1Med = caseDiagNoType1DXAndYesType2DX.filter(f => !casePatientsYesType1MedCollect.contains(f._1))
    val casePatientsNoType1Med = caseMedNoType1Med.map(m => m._1)
    val casePatientsNoType1MedCollect = casePatientsNoType1Med.collect().distinct //This is part of the cases
//    println("notype1med")
//    println(casePatientsNoType1MedCollect.length)

    val caseMedNoType2Med = medNoType2Med.filter(f => casePatientsYesType1MedCollect.contains(f._1))
    val casePatientsNoType2Med = caseMedNoType2Med.map(m => m._1)
    val casePatientsNoType2MedCollect = casePatientsNoType2Med.collect().distinct //This is part of the cases
//    println("cast2")
//    println(casePatientsNoType2MedCollect.length)

    val caseMedYesType2Med = caseMedYesType1Med.filter(f => !casePatientsNoType2MedCollect.contains(f._1))
    val casePatientsYesType2Med = caseMedYesType2Med.map(m => m._1)
    val casePatientsYesType2MedCollect = casePatientsYesType2Med.collect().distinct
//    println("should be 583")
//    println(casePatientsYesType2MedCollect.length)

    //val caseMinMedHelper = caseMedYesType2Med.map(m => ((m._1, m._2._2),m._2._2))
    //val caseMinmedHelperMinDates = caseMinMedHelper.reduceByKey((x,y) => x.getTime)
    //val minMeds = caseMedYesType2Med.reduceByKey((x,y) => (minDate(x._1, y._1), minDateMatcher(x._1, y._1, x._2, y._2)))
    //val caseMinMedHelper = caseMedYesType2Med.filter(r => (r.filter(f => type1_dm_med.contains(f._2._2)).min()._2._1
    //  .after(r.filter(f => type2_dm_med.contains(f._2._2)).min()._2._1)))
    //val caseMinMedHelper = caseMedYesType2Med.filter(f => type1_dm_med.contains(f._2._2)).min()._2._1
    //  .after(caseMedYesType2Med.filter(f => type2_dm_med.contains(f._2._2)).min()._2._1)
//    val caseMinMedHelper = caseMedYesType2Med.filter(f => type1_dm_med.contains(f._2._2)).min()._2._1.
//      after(caseMedYesType2Med.filter(f => type2_dm_med.contains(f._2._2)).min()._2._1)
//    println(caseMinMedHelper.getClass)

    val medGroup = medication.map(m => (m.patientID,(m.medicine,m.date)))
      .filter(f => casePatientsYesType2MedCollect.contains(f._1)).groupByKey
//    println(medGroup)
//    println(medGroup.take(5).foreach(println))
//    println(medGroup.toJavaRDD().first())
    val medGroupHelp = medGroup.filter(f => f._2.filter(f => type1_dm_med.contains(f._1)).map(m => m._2).min
        .after(f._2.filter(f => type2_dm_med.contains(f._1)).map(m => m._2).min))
//    val medGroupHelp = medGroup.filter(r => r.filter(f => type1_dm_med.contains(f._1)).map(m => m._2).min()
//        .after(medGroup.filter(f => type2_dm_med.contains(f._1)).map(m => m._2).min())))
//    println(medGroupHelp)
//    println(medGroupHelp.getClass)
//    println(medGroupHelp.first())
//    println(medGroupHelp.take(5).foreach(println))
    val medGroupHelpPatients = medGroupHelp.map(m => m._1)
    val medGroupHelpPatientsCollect = medGroupHelpPatients.collect().distinct
//    println(medGroupHelpPatientsCollect.length)



    //val medGroupHelp = medGroup._2.filter(m => type1_dm_med(m.medicine)).map(m => m.date).min)
    //val caseMinMedHelp = medGroup.filter(f => casePatientsYesType2MedCollect.contains(f._1))
    //println(caseMinMedHelp.toJavaRDD().first())
    //val meds = medication.map(m => (m.patientID,(m.medicine,m.date))).filter(f => casePatientsYesType2MedCollect.contains(f._1))
//    val medRed = meds.reduceByKey((x,y) => (x._1, x._2.getTime().min().))
    //val caseMinMed = caseMinMedHelp.filter(f => type1_dm_med.contains(f._2))
    //println(caseMinMed)





    //println(caseMinMedHelper.take(5))
    //val casePatientsMinMed2 = caseMedYesType2Med.filter(f => caseMinMedHelper)
    val casePatientsMinMed2Collect = medGroupHelpPatientsCollect
    //val caseMinMedHelperMed2Mins = caseMedYesType2Med.filter(f => type2_dm_med.contains(f._2._2)).min()
    //val minMed2 = minMeds.filter(f => type2_dm_med.contains(f._2._2))
    //val casePatientsMinMed2 = minMed2.map(m => m._1)
    //val casePatientsMinMed2Collect = casePatientsMinMed2.collect().distinct //This is part of the cases
//    println("should be 294")
    //println(casePatientsMinMed2Collect.length)
    //I know this part is wrong but I can't figure out how to fix it and just have to move on.
    val casePatientsCollect = casePatientsNoType1MedCollect.union(casePatientsNoType2MedCollect)
      .union(casePatientsMinMed2Collect).distinct
//    println("should be 976")
//    println(casePatientsCollect.length)
    val casePatientsArray = casePatientsCollect.map(m => (m, 1))
    val casePatients = sc.parallelize(casePatientsArray)

    /**Find CONTROL Patients */

    val labGlucose = lab.filter(f => f._2._2.contains("glucose"))
    val patientsGlucose = labGlucose.map(m => m._1)
    val patientsGlucoseCollect = patientsGlucose.collect().distinct
    //println("should be 2880")
    //println(patientsGlucoseCollect.length)

    val labAbnormalValues = lab.filter(f => (f._2._2.equals("hba1c") && f._2._3 >= 6.0) ||
          (f._2._2.equals("hemoglobin a1c") && f._2._3 >= 6.0) || (f._2._2.equals("fasting glucose") && f._2._3 >= 110) ||
          (f._2._2.equals("fasting blood glucose") && f._2._3 >= 110) || (f._2._2.equals("fasting plasma glucose") &&
          f._2._3 >= 110) || (f._2._2.equals("glucose") && f._2._3 > 110) || (f._2._2.equals("glucose") &&
          f._2._3 > 110) || (f._2._2.equals("glucose, serum") && f._2._3 > 110) || (f._2._2.equals("\"glucose, serum\"") && f._2._3 > 110))
    val patientAbnormalValues = labAbnormalValues.map(m => m._1)
    val patientAbnormalValuesCollect = patientAbnormalValues.collect().distinct

    val labGlucoseNoAbnormals = labGlucose.filter(f => !patientAbnormalValuesCollect.contains(f._1))
    val patientGlucoseNoAbnormals = labGlucoseNoAbnormals.map(m => m._1)
    val patientGlucoseNoAbnormalsCollect = patientGlucoseNoAbnormals.collect().distinct
//    println("should be 953")
//    println(patientGlucoseNoAbnormalsCollect.length)

//    println(diag.take(10).foreach(println))

    val diagDiab = diag.filter(f => dm_related_dx.contains(f._2._2))
    val patientsDiab = diagDiab.map(m => m._1)
    val patientsDiabCollect = patientsDiab.collect().distinct
//    println("should be 5")
//    println(patientsDiabCollect.length)

    val noDiabDiag = labGlucoseNoAbnormals.filter(f => !patientsDiabCollect.contains(f._1))
    val patientsNoDiabDiag = noDiabDiag.map(m => m._1)
    val patientsNoDiabDiagCollect = patientsNoDiabDiag.collect().distinct
//    println("should be 948")
//    println(patientsNoDiabDiagCollect.length)
    val controlPatientsArray = patientsNoDiabDiagCollect.map(m => (m, 2))
    val controlPatients = sc.parallelize(controlPatientsArray)

    /**Find OTHER Patients */
    val other = allPatientsDistinct.map(m => (m, 3))
    val othersArray = other.filter(f => !patientsNoDiabDiagCollect.contains(f._1))
      .filter(f => !casePatientsCollect.contains(f._1))
    val others = sc.parallelize(othersArray)
//    println("should be 1764")
//    println(others.map(m => m._1).collect().length)




    //    /** Find CASE Patients */
//
////    println(medication)
////    println(medication.getClass.getSimpleName)
////    println(medication.first())
////    println(labResult)
////    println(labResult.first())
////    println(diagnostic)
////    println(diagnostic.first())
////
////    val medRDD = medication.toJavaRDD()
////    val labRDD = labResult.toJavaRDD()
////    val diagRDD = diagnostic.toJavaRDD()
//
//    //val diagFilt = diagnostic.map(r => r.patientID)
////    val type1DM = diagnostic.filter( d => type1_dm_dx.contains(d.code))
////    val type1DMPatients = type1DM.map( r => r.patientID)
////    val type2DM = diagnostic.filter( d => type2_dm_dx.contains(d.code))
////    val type2DMPatients = type2DM.map( r => r.patientID)
////    val type1Med = medication.filter( d => type1_dm_med.contains(d.medicine))
////    println(type1Med.first())
////    val type1MedPatients = type1Med.map( r => r.patientID)
////    val type2Med = medication.filter( d => type2_dm_med.contains(d.medicine))
////    val type2MedPatients = type2Med.map( r => r.patientID)
////    val type2MedGroup = medication.groupBy( g => g.patientID)
//
//    //val patientsGroupedAndMin = medication.map( t => (t.patientID, (t.date, t.medicine))).reduceByKey((x,y) => (minDate(x._1, y._1),minDateMatcher(x._1,y._1,x._2,y._2)))
//    val diagGrouped = diagnostic.map (t => (t.patientID, (t.date, t.code)))
//  //    println(diagGrouped.first())
//    //val diag = diagGrouped.map(m => m._1).collect().distinct
//    //println(diag.length) DISTINCT PATIENTS GOOD.
//    val type1DMDiag = diagGrouped.filter(f => type1_dm_dx.contains(f._2._2))
////    println(type1DMDiag.first())
//    val patientsRem1 = type1DMDiag.map(m => m._1)
//    //println(patientsRem1.collect().distinct.length) GOOD
//    val patRem1 = patientsRem1.collect()
////    println(patientsRem1.first())
//    val patientsNoType1DM = diagGrouped.filter(f => !patRem1.contains(f._1))
////    println(patientsNoType1DM.first())
//    val type2DMDiag = diagGrouped.filter(f => type2_dm_dx.contains(f._2._2))
////    println(type2DMDiag.first())
//    val patientsRem4 = type2DMDiag.map(m => m._1)
////    println(patientsRem4.first(
//    val type2DMDiagList = patientsRem4.collect()
//    val patientsNoType1DMAndYesType2DM = patientsNoType1DM.filter(f => type2DMDiagList.contains(f._1))
//    //val patientsNoType1DMAndYesType2DM = patientsNoType1DM.intersection(type2DMDiag) //This is required for all
////    println(patientsNoType1DMAndYesType2DM.first())
//    //println(patientsNoType1DMAndYesType2DM.map(m => m._1).collect().distinct.length) GOOD
//    println("check1")
//
//    val medGrouped = medication.map (t => (t.patientID, (t.date, t.medicine)))
//    val type1DMMed = medGrouped.filter(f => type1_dm_med.contains(f._2._2))
//    val patientsRem2 = type1DMMed.map(m => m._1)
//    val patientsRem2List = patientsRem2.collect()
//    val patientsNoType1DMMed = patientsNoType1DMAndYesType2DM.filter(f => !patientsRem2List.contains(f._1)) //This is one part of the patients for cases
//    //println(patientsNoType1DMMed.map(m => m._1).collect().distinct.length) GOOD
//    val patientsYesType1DMMed = patientsNoType1DMAndYesType2DM.filter(f => patientsRem2List.contains(f._1))
//    //println(patientsYesType1DMMed.map(m => m._1).collect().distinct.length) GOOD
//    val type2DMMed = medGrouped.filter(f => type2_dm_med.contains(f._2._2))
//    val patientsRem3 = type2DMMed.map(m => m._1)
//    val patientsRem3List = patientsRem3.collect()
//    val patientsYesType1DMMedNoType2DMMed = patientsNoType1DMAndYesType2DM.filter(f => !patientsRem3List.contains(f._1))
//      .filter(f => patientsRem2List.contains(f._1)) //This is one part of the patients for cases
//    //println(patientsYesType1DMMedNoType2DMMed.map(m => m._1).collect().distinct.length) GOOD
//
//    println("check2")
//    val patientsNoType1DMAndYesType2DMArray = patientsNoType1DMAndYesType2DM.map(m => m._1).collect().distinct
//    val patientsYesType1DMMedYesType2DMMed = medGrouped.filter(f => patientsNoType1DMAndYesType2DMArray.contains(f._1))
//        .filter(f => patientsRem2List.contains(f._1)).filter(f => patientsRem3List.contains(f._1))
//    //val patientsYesType1DMMedYesType2DMMed = patientsNoType1DMAndYesType2DM.filter(f => patientsRem2List.contains(f._1))
//      //.filter(f => patientsRem3List.contains(f._1))
//    //val patientsYesType1DMMedYesType2DMMed = patientsNoType1DMAndYesType2DM.intersection(type1DMMed).intersection(type2DMMed)
//    //println(patientsYesType1DMMedYesType2DMMed.map(m => m._1).collect().distinct.length) GOOD
//    println(patientsYesType1DMMedYesType2DMMed.first())
//    val patientsType2DMPrecedeType1DM = patientsYesType1DMMedYesType2DMMed
//      .reduceByKey((x,y) => (minDate(x._1, y._1), minDateMatcher(x._1, y._1, x._2, y._2)))
//    //println(patientsType2DMPrecedeType1DM.first())
//      .filter(f => type2_dm_med.contains(f._2._2)) //This is one part of the patients for cases
//    println(patientsType2DMPrecedeType1DM.first())
//    println(patientsType2DMPrecedeType1DM.map(m => m._1).collect().distinct.length)
//    println("check3")
//    val patients1 = patientsNoType1DMMed.map(f => f._1).filter(f => !patRem1.contains(f))
//      .filter(f => !patientsRem2List.contains(f)).distinct()
//    val patients2 = patientsYesType1DMMedNoType2DMMed.map(f => f._1).filter(f => !patRem1.contains(f))
//      .filter(f => !patientsRem3List.contains(f)).distinct()
//    val patients3 = patientsType2DMPrecedeType1DM.map(f => f._1).filter(f => !patRem1.contains(f)).distinct()
//    //val patients1 = patientsNoType1DMMed.map(f => f._1).subtract(patientsRem1).subtract(patientsRem2)
//    //val patients2 = patientsYesType1DMMedNoType2DMMed.map(f => f._1).subtract(patientsRem1).subtract(patientsRem3)
//    //val patients3 = patientsType2DMPrecedeType1DM.map(f => f._1).subtract(patientsRem1)
//    println("check4")
//    val casePatientsAll = patients1.union(patients2).union(patients3).distinct()
//    println(patients1.first())
//
//    println("check5")
//    println(casePatientsAll.collect().length)
//
//    //Case Rules: 1. No Type 1 DM diagnosis AND 2. Yes Type 2 DM diagnosis AND
//    //      3. No order for Type 1 DM medication OR
//    //      3. Yes order for Type 1 DM medication AND No order for Type 2 DM medication OR
//    //      3. Yes order for Type 1 DM medication AND Order for Type 2 DM medication AND Type 2 DM medication precedes Type 1 DM medication
//    //val patientsWithNoType1YesType2 = type2DM.subtract(type1DM)
//    //val patientsWithNoType1YesType2AndNoType1Med = patientsWithNoType1YesType2.subtract
////    val patientsWithNoType1YesType2 = type2DMPatients.subtract(type1DMPatients)
////    val patientsWithNoType1YesType2AndNoType1Med = patientsWithNoType1YesType2.subtract(type1MedPatients) //this is part of case patient set
////    val patientsWithNoType1YesType2AndYesType1Med = type2MedPatients.intersection(patientsWithNoType1YesType2)
////    val patientsWithNoType1YesType2AndYesType1MedAndNoType2Med = patientsWithNoType1YesType2AndYesType1Med.subtract(type2MedPatients) //this is part of case patient set
//
////    val casePatientsRDD = casePatientsAll.map(f => (f, 1))
//    val casePatients = casePatientsAll.map(f => (f,1))
//    /** Find CONTROL Patients */
////    # ABNORMAL LAB VALUES: CONTROLS
////    'HbA1c' >=6.0%
////      OR
////    'Hemoglobin A1c' >= 6.0%
////      OR
////    'Fasting Glucose' >= 110 mg/dL
////      OR
////    'Fasting blood glucose' >= 110 mg/dL
////      OR
////    'fasting plasma glucose' >= 110 mg/dL
////      OR
////    'Glucose' > 110 mg/dL
////      OR
////    'glucose' > 110 mg/dL
////      OR
////    'Glucose, Serum' > 110 mg/dL
//    //Control Rules: 1. Yes any type of 'glucose' measure AND 2. No abnormal lab value AND 3. No DM related diagnosis
//    val DMPatients = patientsRem1.union(patientsRem2).collect()
//    val labs = labResult.map(m => (m.patientID, (m.date, m.testName, m.value)))
//    val labPeople = labs.filter(f => f._2._2.contains("glucose"))
//    println(labPeople.map(m => m._1).collect().distinct.length)
//    val labAbnormalValues = labs.filter(f => (f._2._2.equals("hba1c") && f._2._3 >= 6.0) ||
//      (f._2._2.equals("hemoglobin a1c") && f._2._3 >= 6.0) || (f._2._2.equals("fasting glucose") && f._2._3 >= 110) ||
//      (f._2._2.equals("gasting blood glucose") && f._2._3 >= 110) || (f._2._2.equals("fasting plasma glucose") &&
//      f._2._3 >= 110) || (f._2._2.equals("glucose") && f._2._3 > 110) || (f._2._2.equals("glucose") &&
//      f._2._3 > 110) || (f._2._2.equals("glucose, serum") && f._2._3 > 110) || (f._2._2.equals("\"glucose, serum\"") && f._2._3 > 110))
//    val abPatients = labAbnormalValues.map(m => m._1).collect()
//    println(abPatients.distinct.length)
//    println("check5")
////    val finControl = labPeople.map(m => m._1).filter(f => !DMPatients.contains(f))
////      .filter(f => !abPatients.contains(f)).distinct()
//    val noAbPat = labPeople.filter(f => !abPatients.contains(f._1))
//
//    //val finControl = noAbPat.filter(f => !dm_related_dx.contains(f._2._2))
//    //val finControl = labPeople.map(m => m._1).subtract(DMPatients).subtract(abPatients).distinct()
//    val controlPatientsRDD = finControl.map(m => (m, 2))
//    val controlPatients = controlPatientsRDD
//    println(finControl.collect().distinct.length)
//    /** Find OTHER Patients */
//
//    val allPatientsDiag = diagnostic.map(m => m.patientID)
//    val allPatientsLab = labResult.map(m => m.patientID)
//    val allPatientsMed = medication.map(m => m.patientID)
//    val allPatientsTotal = allPatientsDiag.union(allPatientsLab).union(allPatientsMed)
//    val allPatientsTot = allPatientsTotal.map(m => (m, 3))
//    val others = allPatientsTot.subtract(controlPatients).subtract(casePatients).distinct()
//    println(others.collect().length)
    /** Once you find patients for each group, make them as a single RDD[(String, Int)] */
    val phenotypeLabel = sc.union(casePatients, controlPatients, others)

    /** Return */
    phenotypeLabel
  }
}