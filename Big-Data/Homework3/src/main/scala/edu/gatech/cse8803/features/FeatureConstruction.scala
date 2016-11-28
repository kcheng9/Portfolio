/**
 * @author Hang Su
 */
package edu.gatech.cse8803.features

import edu.gatech.cse8803.model.{LabResult, Medication, Diagnostic}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext._


object FeatureConstruction {

  /**
   * ((patient-id, feature-name), feature-value)
   */
  type FeatureTuple = ((String, String), Double)

  /**
   * Aggregate feature tuples from diagnostic with COUNT aggregation,
   * @param diagnostic RDD of diagnostic
   * @return RDD of feature tuples
   */
  def constructDiagnosticFeatureTuple(diagnostic: RDD[Diagnostic]): RDD[FeatureTuple] = {
    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */
    val diagCounts = diagnostic.map(m => ((m.patientID, m.code),1.0)).groupByKey().mapValues{(tuples) => tuples.size}
    val diag = diagCounts.map(m => ((m._1._1, m._1._2),m._2.toDouble))
    //println(diag.take(10).foreach(println))
//    println(diag.getClass)
//    println(diag.count())
    return diag.sparkContext.parallelize(diag.collect())
    //diagnostic.sparkContext.parallelize(List((("patient", "diagnostics"), 1.0)))
  }

  /**
   * Aggregate feature tuples from medication with COUNT aggregation,
   * @param medication RDD of medication
   * @return RDD of feature tuples
   */
  def constructMedicationFeatureTuple(medication: RDD[Medication]): RDD[FeatureTuple] = {
    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */
    val medCounts = medication.map(m => ((m.patientID, m.medicine), 1.0)).groupByKey().mapValues{(tuples) => tuples.size}
    val med = medCounts.map(m => ((m._1._1, m._1._2),m._2.toDouble))
    //println(med.take(10).foreach(println))
    //medication.sparkContext.parallelize(List((("patient", "med"), 1.0)))
//    println(med.count())
    return med.sparkContext.parallelize(med.collect())
  }

  /**
   * Aggregate feature tuples from lab result, using AVERAGE aggregation
   * @param labResult RDD of lab result
   * @return RDD of feature tuples
   */
  def constructLabFeatureTuple(labResult: RDD[LabResult]): RDD[FeatureTuple] = {
    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */
    val labTup = labResult.map(m => ((m.patientID, m.testName), m.value))
    val labAvg = labTup.groupByKey().mapValues{(tuples) => tuples.sum/tuples.size}
    //labResult.sparkContext.parallelize(List((("patient", "lab"), 1.0)))
    //println(labAvg.take(10).foreach(println))
//    println(labAvg.count())
    return labAvg.sparkContext.parallelize(labAvg.collect())
  }

  /**
   * Aggregate feature tuple from diagnostics with COUNT aggregation, but use code that is+
   * available in the given set only and drop all others.
   * @param diagnostic RDD of diagnostics
   * @param candiateCode set of candidate code, filter diagnostics based on this set
   * @return RDD of feature tuples
   */
  def constructDiagnosticFeatureTuple(diagnostic: RDD[Diagnostic], candiateCode: Set[String]): RDD[FeatureTuple] = {
    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */
    val diagCode = diagnostic.map(m => ((m.patientID, m.code), 1.0))
    val cand = diagCode.filter(f => candiateCode.contains(f._1._2))
    val candCount = cand.groupByKey().mapValues{(tuples) => tuples.size}
    val candDiag = candCount.map(m => ((m._1._1, m._1._2),m._2.toDouble))
    //println(candDiag.take(10).foreach(println))
    //diagnostic.sparkContext.parallelize(List((("patient", "diagnostics"), 1.0)))
//    println(candDiag.count())
    return candDiag.sparkContext.parallelize(candDiag.collect())
  }

  /**
   * Aggregate feature tuples from medication with COUNT aggregation, use medications from
   * given set only and drop all others.
   * @param medication RDD of diagnostics
   * @param candidateMedication set of candidate medication
   * @return RDD of feature tuples
   */
  def constructMedicationFeatureTuple(medication: RDD[Medication], candidateMedication: Set[String]): RDD[FeatureTuple] = {
    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */
    //medication.sparkContext.parallelize(List((("patient", "med"), 1.0)))
    val medCode = medication.map(m => ((m.patientID, m.medicine), 1.0))
    val cand = medCode.filter(f => candidateMedication.contains(f._1._2))
    val medCount = cand.groupByKey().mapValues{(tuples) => tuples.size}
    val candMed = medCount.map(m => ((m._1._1, m._1._2), m._2.toDouble))
//    println(candMed.count())
    return candMed.sparkContext.parallelize(candMed.collect())
  }


  /**
   * Aggregate feature tuples from lab result with AVERAGE aggregation, use lab from
   * given set of lab test names only and drop all others.
   * @param labResult RDD of lab result
   * @param candidateLab set of candidate lab test name
   * @return RDD of feature tuples
   */
  def constructLabFeatureTuple(labResult: RDD[LabResult], candidateLab: Set[String]): RDD[FeatureTuple] = {
    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */
    val labCode = labResult.map(m => ((m.patientID, m.testName), m.value))
    val cand = labCode.filter(f => candidateLab.contains(f._1._2))
    val labAvg = cand.groupByKey().mapValues{(tuples) => tuples.sum/tuples.size}
    //labResult.sparkContext.parallelize(List((("patient", "lab"), 1.0)))
//    println(labAvg.count())
    return labAvg.sparkContext.parallelize(labAvg.collect())
  }


  /**
    * Given a feature tuples RDD, construct features in vector
   * format for each patient. feature name should be mapped
   * to some index and convert to sparse feature format.
   * @param sc SparkContext to run
   * @param feature RDD of input feature tuples
   * @return
   */
  def construct(sc: SparkContext, feature: RDD[FeatureTuple]): RDD[(String, Vector)] = {
    //println("start construction")

    /** save for later usage */
    feature.cache()

    /** create a feature name to id map*/

    /** transform input feature */

    /**
     * Functions maybe helpful:
     *    collect
     *    groupByKey
     */

    /**
     * TODO implement your own code here and remove existing
     * placeholder code
     */

    //println(feature)
    val featurePrint = feature.map(m => (m._1._1, m._1._2, m._2))
    //val featureMapHelper = featurePrint.flatMap(_._2)
    //println(featurePrint.take(10).foreach(println))

    val featureList = featurePrint.map(m => m._2)
    val featureListCollect = featureList.collect().distinct.zipWithIndex.toMap
    val featureNum = featureList.collect().distinct.length
    //println(featureListCollect)
    //println(featureNum)
    val featuresWithMap = featurePrint.map(m => (m._1, (featureListCollect.get(m._2).get, m._3)))
    //val featuresWithMap2 = featuresWithMap.map(m => (m._1, (m._2._1.asInstanceOf[Int], m._2._2)))
    val featuresWithMapGroup = featuresWithMap.groupByKey()
//    println(featuresWithMapGroup.take(5).foreach(println))
    val featuresWithMapGroupSeq = featuresWithMapGroup.map(m => (m._1, m._2.toSeq))
//    println(featuresWithMapGroupSeq.take(5).foreach(println))
    val featuresWithMapGroupSeqFin = featuresWithMapGroupSeq.map(m => (m._1, Vectors.sparse(featureNum, m._2)))
//    println(featuresWithMapGroupSeqFin.take(5).foreach(println))
//    val featuresWithNewMap = featuresWithMap.map(m => ((m._1, m._2._1), m._2._2))
//    val featuresWithNewMapGroup = featuresWithNewMap.groupByKey()
//    val featuresFinal = featuresWithNewMapGroup.map()
    //val featuresWithMapGroupCollect = featuresWithMapGroup.collect()
    //val finalFeatures = featuresWithMapGroupCollect.map(m => (m._1, Vectors.sparse(featureNum, m._2.toSeq)))
    //println(finalFeatures.take(10).foreach(println))
//    val featuresWithMapGroupCollect= featuresWithMapGroup.collect()
//    val featuresFin = featuresWithMapGroupCollect.map(m => (m._1, Vectors.sparse(featureNum, m._2.toSeq)))
//    println(featuresFin.take(5).foreach(println))

    //println(featurePrint)
    //println(featurePrint.collect().length)
    //val featureGroup = featurePrint.groupBy(_._1).map(_._2)
    //val featureMapHelper = featurePrint.flatMap(_._2).distinct.collect.zipWithIndex.toMap
    //println(featureMapHelper)
    //val featuresWithMap = featurePrint.map(m => (m._1, featureMapHelper.get(m._2), m._3))

    //val featureGroup = featurePrint.groupBy(_._1)
    //val featureGroupMapped = featureGroup.map(m => (m._1, m._1.map(f => featureMapHelper.get(f))))
//    val featureMapHelper = featurePrint.map(m => (m._1, Map(m._2, m._3)))
//    val featureMap = featureMapHelper.flatMap(_._2.keys).distinct.collect.zipWithIndex.toMap
//    val scFeatureMap = sc.broadcast(featureMap)
////    val finalFeatures = featurePrint.map(m => (m._1, (featureMap.map(l => l._1 == m._2), m._3)))
////    val finalForReals = finalFeatures.map(m => (m._1, (m._2._1._1, m._2._2)))
////    println(finalForReals)
////    return finalForReals
//    val finalFeatures = featureMapHelper.map{
//      case(patient, features, value) =>
//      val numFeatures = scFeatureMap.value.size
//      val indexedFeatures = features.toList.map{case(featureName, featureValue) =>
//        (scFeatureMap.value(featureName), featureValue)}
//      val featureVector = Vectors.sparse(numFeatures, indexedFeatures)
//      val patient = (patient, featureVector)
//      patient
//  }

    //val featureMap = featureGroup.flatMap(_._2.keys)
//
//    val patientFeatures = featureGroup.map{events =>
//      val targetEvent = events.find(_.).get
//      val target = events
//
//    val features = events.groupBy(_._2).map{case(eventId, grpEvents) =>
//      val featureName = eventId
//      val featureValue = grpEvents.map(_._3).sum
//
//      (featureName, featureValue)
//    }
//      (target, features)
//    }

//    println(patientFeatures.collect())
//    val patientFeatures = featureGroup.map{events =>
//      val targetEvent = events.find(_.eventID == "heartfailure").get
//      val target = targetEvent.value
//
//    val features = events.groupBy(_.eventId).map{case(eventId, grpEvents) =>
//    val featureName = eventId
//    val featureValue = grpEvents.map(_.value).sum(featureName, FeatureValue)  )  }
//      (target, features)}
    //println(featureGroup)
    //println(featureGroup.take(10).foreach(println))
//    val featureGroupCollect = featureGroup.collect()
    //println(featureGroupCollect.head)



//    val featureGroup = feature.groupBy(_._1)
//    println(featureGroup)
//    println(featureGroup)
//    val featureRDD = sc.parallelize(featureGroup)
//    println(featureRDD)
//    println(featureRDD.first())
    val result = sc.parallelize(featuresWithMapGroupSeqFin.collect())
    //println(result.take(10).foreach(println))
//    val result = sc.parallelize(Seq(("Patient-NO-1", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-2", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-3", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-4", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-5", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-6", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-7", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-8", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-9", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)),
//                                    ("Patient-NO-10", Vectors.dense(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))))
    result
    /** The feature vectors returned can be sparse or dense. It is advisable to use sparse */
  }
}


