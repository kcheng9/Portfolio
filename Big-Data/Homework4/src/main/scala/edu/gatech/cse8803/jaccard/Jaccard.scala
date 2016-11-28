/**

students: please put your implementation in this file!
  **/
package edu.gatech.cse8803.jaccard

import edu.gatech.cse8803.model._
import edu.gatech.cse8803.model.{EdgeProperty, VertexProperty}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object Jaccard {

  def jaccardSimOne(graph: Graph[VertexProperty, EdgeProperty], patientID: Long): RDD[(Long, Double)] = {

//    val patients = graph.vertices
//      .filter{case(id, property) => property.toString().contains("PatientProperty")}
//      .map(m => m._2.asInstanceOf[PatientProperty])
//
//    val otherPatient = patients.filter(f => f.patientID.toLong != patientID)
//    val thisPatient = patients.filter(f => f.patientID.toLong == patientID)

    val patientDiag = graph.edges
      .filter(f => f.attr.toString.contains("PatientDiagnostic"))
      .map(m => m.attr.asInstanceOf[PatientDiagnosticEdgeProperty])
      .map(m => (m.diagnostic.patientID,m.diagnostic.icd9code))
      .groupByKey()
      .map(m => (m._1.toLong,m._2.toSet))

    val otherPatientDiag = patientDiag.filter(f => f._1 != patientID.toLong)
    val thisPatientDiag = patientDiag.filter(f => f._1 == patientID.toLong)

    val patientLab = graph.edges
      .filter(f => f.attr.toString.contains("PatientLab"))
      .map(m => m.attr.asInstanceOf[PatientLabEdgeProperty])
      .map(m => (m.labResult.patientID, m.labResult.labName))
      .groupByKey()
      .map(m => (m._1.toLong, m._2.toSet))

    val otherPatientLab = patientLab.filter(f => f._1 != patientID.toLong)
    val thisPatientLab = patientLab.filter(f => f._1 == patientID.toLong)

    val patientMed = graph.edges
      .filter(f => f.attr.toString.contains("PatientMedication"))
      .map(m => m.attr.asInstanceOf[PatientMedicationEdgeProperty])
      .map(m => (m.medication.patientID, m.medication.medicine))
      .groupByKey()
      .map(m => (m._1.toLong, m._2.toSet))

    val otherPatientMed = patientMed.filter(f => f._1 != patientID.toLong)
    val thisPatientMed = patientMed.filter(f => f._1 == patientID.toLong)

    val otherPatientSet = otherPatientDiag.union(otherPatientLab).union(otherPatientMed)
    val thisPatientSet = thisPatientDiag.union(thisPatientLab).union(thisPatientMed)
      .map(m => m._2)

    val otherPatientSetCombined = otherPatientSet.reduceByKey((x,y) => x++y)
    val thisPatientSetCombined = thisPatientSet.reduce((x,y) => x++y)

    val jaccardScores = otherPatientSetCombined.map(m => (m._1, jaccard(m._2, thisPatientSetCombined)))

    val jaccardScoresSorted = jaccardScores.sortBy(s => -s._2)

    return jaccardScoresSorted
  }

  def jaccardSimilarityOneVsAll(graph: Graph[VertexProperty, EdgeProperty], patientID: Long): List[Long] = {
    /** 
    Given a patient ID, compute the Jaccard similarity w.r.t. to all other patients. 
    Return a List of patient IDs ordered by the highest to the lowest similarity.
    For ties, random order is okay
    */

    val jaccardScoresSorted = jaccardSimOne(graph, patientID)

    val closestRatedPatients = jaccardScoresSorted.map(m => m._1).collect.toList

    //closestRatedPatients.take(15).foreach(println)

    val closestRatedPatientsTop10 = closestRatedPatients.take(10)

    /** Remove this placeholder and implement your code */
    //List(1,2,3,4,5)

    return closestRatedPatientsTop10
  }

  def jaccardSimilarityAllPatients(graph: Graph[VertexProperty, EdgeProperty]): RDD[(Long, Long, Double)] = {
    /**
    Given a patient, med, diag, lab graph, calculate pairwise similarity between all
    patients. Return a RDD of (patient-1-id, patient-2-id, similarity) where 
    patient-1-id < patient-2-id to avoid duplications
    */

    val patients = graph.vertices
      .filter{case(id, property) => property.toString().contains("PatientProperty")}
      .map(m => m._2.asInstanceOf[PatientProperty])

    val patientDiag = graph.edges
      .filter(f => f.attr.toString.contains("PatientDiagnostic"))
      .map(m => m.attr.asInstanceOf[PatientDiagnosticEdgeProperty])
      .map(m => (m.diagnostic.patientID,m.diagnostic.icd9code))
      .groupByKey()
      .map(m => (m._1.toLong,m._2.toSet))

    val patientLab = graph.edges
      .filter(f => f.attr.toString.contains("PatientLab"))
      .map(m => m.attr.asInstanceOf[PatientLabEdgeProperty])
      .map(m => (m.labResult.patientID, m.labResult.labName))
      .groupByKey()
      .map(m => (m._1.toLong, m._2.toSet))

    val patientMed = graph.edges
      .filter(f => f.attr.toString.contains("PatientMedication"))
      .map(m => m.attr.asInstanceOf[PatientMedicationEdgeProperty])
      .map(m => (m.medication.patientID, m.medication.medicine))
      .groupByKey()
      .map(m => (m._1.toLong, m._2.toSet))

    val patientSet = patientDiag.union(patientLab).union(patientMed)
    val patientSetCombined = patientSet.reduceByKey((x,y) => x++y)
    val patientSetMap = patientSetCombined.collectAsMap()

    val patientsAll = patientSet.map(m => m._1).collect().toList

    val patientOnPatient = patients.map(m => (m.patientID.toLong, patientsAll)).flatMapValues(v => v).filter(f => f._1<f._2)

    //println(patientOnPatient)
    //patientOnPatient.take(20).foreach(println)
    //println(patientOnPatient.collect.map(m => (m._1, m._2, patientSetMap.get(m._1).orNull, patientSetMap.get(m._2).orNull)))
//      patientOnPatient.collect.map(m => (m._1, m._2, patientSetMap.get(m._1).get, patientSetMap.get(m._2))).take(5).foreach(println)
    //patientOnPatient.collect.map(m => (m._1, m._2, jaccard(patientSetMap.get(m._1).orNull, patientSetMap.get(m._2).orNull))).take(5).foreach(println)


    val jaccardPatientOnPatient = patientOnPatient.collect
      .map(m => (m._1, m._2, jaccard(patientSetMap.get(m._1).orNull, patientSetMap.get(m._2).orNull)))

    val jaccardPatientOnPatientSeq = jaccardPatientOnPatient

//    jaccardPatientOnPatient.take(5).foreach(println)
    //println(jaccardPatientOnPatientSeq)

    val sc = graph.edges.sparkContext
    return sc.parallelize(jaccardPatientOnPatientSeq)
    //return jaccardPatientOnPatient
    //sc.parallelize(Seq((1L, 2L, 0.5d), (1L, 3L, 0.4d)))
  }

  def jaccard[A](a: Set[A], b: Set[A]): Double = {
    /** 
    Helper function

    Given two sets, compute its Jaccard similarity and return its result.
    If the union part is zero, then return 0.
    */
    if (a == null || b == null){
      return 0.0
    }

    val aIntersectionb = a.intersect(b)
    val aUnionb = a.union(b)

    val sizeIntersect = aIntersectionb.size
    val sizeUnion = aUnionb.size

    if (sizeIntersect == 0){
      return 0.0
    }

    return sizeIntersect.toDouble/sizeUnion.toDouble

  }
}
