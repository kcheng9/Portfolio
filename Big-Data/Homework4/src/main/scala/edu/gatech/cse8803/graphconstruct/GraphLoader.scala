/**
 * @author Hang Su <hangsu@gatech.edu>.
 */

package edu.gatech.cse8803.graphconstruct

import edu.gatech.cse8803.model._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object GraphLoader {
  /** Generate Bipartite Graph using RDDs
    *
    * @input: RDDs for Patient, LabResult, Medication, and Diagnostic
    * @return: Constructed Graph
    *
    * */

  def load(patients: RDD[PatientProperty], labResults: RDD[LabResult],
           medications: RDD[Medication], diagnostics: RDD[Diagnostic]): Graph[VertexProperty, EdgeProperty] = {

//    println(labResults.map(l => (l.patientID, l.labName)).filter(f => f._1.toLong == 9L).collect().distinct.toList)
//    println(medications.map(m => (m.patientID, m.medicine)).filter(f => f._1.toLong == 9L).collect().distinct.toList)
//    println(diagnostics.map(d => (d.patientID, d.icd9code)).filter(f => f._1.toLong == 9L).collect().distinct.toList)

    /** HINT: See Example of Making Patient Vertices Below */
    val vertexPatient: RDD[(VertexId, VertexProperty)] = patients
      .map(patient => (patient.patientID.toLong, PatientProperty(patient.patientID, patient.sex,
        patient.dob, patient.dod)))

    val startIndexDiag = patients.map(patient => patient.patientID.toInt).collect().max + 1

    val diagnosticDistinct = diagnostics.map(diag => diag.icd9code).distinct()
      .zipWithIndex.map(diag => (diag._1, diag._2+startIndexDiag))
    val vertexDiagnostic: RDD[(VertexId, VertexProperty)] = diagnosticDistinct
      .map{case(icd9code, index) => (index, DiagnosticProperty(icd9code))}

    val startIndexLab = diagnosticDistinct.map(diag => diag._2.toInt).collect().max + 1

    val labDistinct = labResults.map(lab => lab.labName).distinct()
      .zipWithIndex.map(lab => (lab._1, lab._2+startIndexLab))
    val vertexLabResult: RDD[(VertexId, VertexProperty)] = labDistinct
      .map{case(labName, index) => (index, LabResultProperty(labName))}

    val startIndexMed = labDistinct.map(lab => lab._2.toInt).collect().max +1

    val medDistinct = medications.map(med => med.medicine).distinct()
      .zipWithIndex.map(med => (med._1, med._2+startIndexMed))
    val vertexMedication: RDD[(VertexId, VertexProperty)] = medDistinct
      .map{case(medName, index) => (index, MedicationProperty(medName))}

    /** HINT: See Example of Making PatientPatient Edges Below
      *
      * This is just sample edges to give you an example.
      * You can remove this PatientPatient edges and make edges you really need
      * */
//    case class PatientPatientEdgeProperty(someProperty: SampleEdgeProperty) extends EdgeProperty
//    val edgePatientPatient: RDD[Edge[EdgeProperty]] = patients
//      .map({p =>
//        Edge(p.patientID.toLong, p.patientID.toLong, SampleEdgeProperty("sample").asInstanceOf[EdgeProperty])
//      })

    val latestDiag = diagnostics.map(m => ((m.patientID, m.icd9code), (m.date.toLong, m.sequence)))
      .groupByKey
      .map(m => (m._1, m._2.maxBy(b => b._1)))
      .map(m => Diagnostic(m._1._1, m._2._1, m._1._2, m._2._2))

    val latestLab = labResults.map(m => ((m.patientID, m.labName), (m.date.toLong, m.value)))
      .groupByKey
      .map(m => (m._1, m._2.maxBy(b => b._1)))
      .map(m => LabResult(m._1._1, m._2._1, m._1._2, m._2._2))

    val latestMed = medications.map(m => ((m.patientID, m.medicine), m.date.toLong))
      .groupByKey
      .map(m => (m._1, m._2.max))
      .map(m => Medication(m._1._1, m._2.toLong, m._1._2))

    val vertexPatientID = vertexPatient.map(v => (v._2, v._1))
    val vertexDiagID = vertexDiagnostic.map(v => (v._2, v._1))
    val vertexLabID = vertexLabResult.map(v => (v._2, v._1))
    val vertexMedID = vertexMedication.map(v => (v._2, v._1))

    val vertexPatientMap = vertexPatient.collectAsMap()

    val latestDiagWithPatient = latestDiag
      .map(m => (vertexPatientMap.get(m.patientID.toLong).get, m))
    val latestLabWithPatient = latestLab
      .map(m => (vertexPatientMap.get(m.patientID.toLong).get, m))
    val latestMedWithPatient = latestMed
      .map(m => (vertexPatientMap.get(m.patientID.toLong).get, m))

    val vertexPatientDiag = vertexPatientID.union(vertexDiagID).collectAsMap()
    val vertexPatientLab = vertexPatientID.union(vertexLabID).collectAsMap()
    val vertexPatientMed = vertexPatientID.union(vertexMedID).collectAsMap()

    //println(vertexPatientID)

//    println(vertexDiagID.get(DiagnosticProperty("427.5")))
//    println(vertexDiagID.get(DiagnosticProperty("427.5")).get)

    val edgePatientDiag: RDD[Edge[EdgeProperty]] = latestDiagWithPatient
      .map{case(patient, diag) => (patient, diag.patientID.toLong, diag.icd9code, diag)}
      .map(m => Edge(vertexPatientDiag.get(m._1).get.asInstanceOf[VertexId],
        vertexPatientDiag.get(DiagnosticProperty(m._3)).get.asInstanceOf[VertexId],
        PatientDiagnosticEdgeProperty(m._4)))
//      .map(m => (m.patientID, m.icd9code, m))
//      .map(m => Edge(vertexPatientDiag.get(m._1.asInstanceOf[VertexProperty]).get.asInstanceOf[VertexId],
//        vertexPatientDiag.get(DiagnosticProperty(m._2)).get.asInstanceOf[VertexId],
//        PatientDiagnosticEdgeProperty(m._3)))

    val edgePatientLab: RDD[Edge[EdgeProperty]] = latestLabWithPatient
      .map{case(patient, lab) => (patient, lab.patientID.toLong, lab.labName, lab)}
      .map(m => Edge(vertexPatientLab.get(m._1).get.asInstanceOf[VertexId],
        vertexPatientLab.get(LabResultProperty(m._3)).get.asInstanceOf[VertexId],
          PatientLabEdgeProperty(m._4)))
//      .map(m => (m.patientID, m.labName, m))
//      .map(m => Edge(vertexPatientLab.get(m._1.asInstanceOf[VertexProperty]).get.asInstanceOf[VertexId],
//        vertexPatientLab.get(LabResultProperty(m._2)).get.asInstanceOf[VertexId],
//        PatientLabEdgeProperty(m._3)))

    val edgePatientMed: RDD[Edge[EdgeProperty]] = latestMedWithPatient
      .map{case(patient, med) => (patient, med.patientID.toLong, med.medicine, med)}
      .map(m => Edge(vertexPatientMed.get(m._1).get.asInstanceOf[VertexId],
        vertexPatientMed.get(MedicationProperty(m._3)).get.asInstanceOf[VertexId],
        PatientMedicationEdgeProperty(m._4)))
//      .map(m => (m.patientID, m.medicine, m))
//      .map(m => Edge(vertexPatientMed.get(m._1.asInstanceOf[VertexProperty]).get.asInstanceOf[VertexId],
//        vertexPatientMed.get(MedicationProperty(m._2)).get.asInstanceOf[VertexId],
//        PatientMedicationEdgeProperty(m._3)))


    // Making Graph
    val edgeDiagPatient = edgePatientDiag.map(m => Edge(m.dstId, m.srcId, m.attr))
    val edgeMedPatient = edgePatientMed.map(m => Edge(m.dstId, m.srcId, m.attr))
    val edgeLabPatient = edgePatientLab.map(m => Edge(m.dstId, m.srcId, m.attr))

    val vertices = vertexPatient.union(vertexDiagnostic).union(vertexLabResult).union(vertexMedication)
    val edges = edgePatientDiag.union(edgePatientLab).union(edgePatientMed)
      .union(edgeDiagPatient).union(edgeMedPatient).union(edgeLabPatient)

    //edges.filter(f => f.attr.toString().contains("PatientMedication")).take(10).foreach(println)

//    vertices.take(5).foreach(println)
//    edges.take(20).foreach(println)

    val graph: Graph[VertexProperty, EdgeProperty] = Graph(vertices, edges)

//    println(graph.vertices.collect().length)
//    println(graph.edges.collect().length)

    //graph.edges.map(f => (f.srcId, f.dstId, f.attr)).filter(f => f._1.toLong == 9L).foreach(println)

    graph
  }
}
