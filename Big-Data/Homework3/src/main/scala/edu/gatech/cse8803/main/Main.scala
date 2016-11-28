/**
 * @author Hang Su <hangsu@gatech.edu>.
 */

package edu.gatech.cse8803.main

import java.text.SimpleDateFormat

import edu.gatech.cse8803.clustering.{NMF, Metrics}
import edu.gatech.cse8803.features.FeatureConstruction
import edu.gatech.cse8803.ioutils.CSVUtils
import edu.gatech.cse8803.model.{Diagnostic, LabResult, Medication}
import edu.gatech.cse8803.phenotyping.T2dmPhenotype
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.clustering.{GaussianMixture, KMeans}
import org.apache.spark.mllib.linalg.{DenseMatrix, Matrices, Vectors, Vector}
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source


object Main {
  def main(args: Array[String]) {
    import org.apache.log4j.Logger
    import org.apache.log4j.Level

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val sc = createContext
    val sqlContext = new SQLContext(sc)

    /** initialize loading of data */
//    println("start")
    val (medication, labResult, diagnostic) = loadRddRawData(sqlContext)
//    println("loaded")
    val (candidateMedication, candidateLab, candidateDiagnostic) = loadLocalRawData
//    println("loaded2")

    /** conduct phenotyping */
//    println("start phenotype")
    val phenotypeLabel = T2dmPhenotype.transform(medication, labResult, diagnostic)
//    println("end phenotype")
    /** feature construction with all features */
//    println("start All Features")
    //println(FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic).collect().length))
    val featureTuples = sc.union(
      FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic),
      FeatureConstruction.constructLabFeatureTuple(labResult),
      FeatureConstruction.constructMedicationFeatureTuple(medication)
    )
//    val featureTuples = FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic)
//      .union(FeatureConstruction.constructLabFeatureTuple(labResult))
//      .union(FeatureConstruction.constructMedicationFeatureTuple(medication))
//    println(featureTuples.collect().length)
    val rawFeatures = FeatureConstruction.construct(sc, featureTuples)

    val (kMeansPurity, gaussianMixturePurity, nmfPurity) = testClustering(phenotypeLabel, rawFeatures)
    println(f"[All feature] purity of kMeans is: $kMeansPurity%.5f")
    println(f"[All feature] purity of GMM is: $gaussianMixturePurity%.5f")
    println(f"[All feature] purity of NMF is: $nmfPurity%.5f")

    /** feature construction with filtered features */
//    println("start filtered features")
    val filteredFeatureTuples = sc.union(
      FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic, candidateDiagnostic),
      FeatureConstruction.constructLabFeatureTuple(labResult, candidateLab),
      FeatureConstruction.constructMedicationFeatureTuple(medication, candidateMedication)
    )

    val filteredRawFeatures = FeatureConstruction.construct(sc, filteredFeatureTuples)

    val (kMeansPurity2, gaussianMixturePurity2, nmfPurity2) = testClustering(phenotypeLabel, filteredRawFeatures)
    println(f"[Filtered feature] purity of kMeans is: $kMeansPurity2%.5f")
    println(f"[Filtered feature] purity of GMM is: $gaussianMixturePurity2%.5f")
    println(f"[Filtered feature] purity of NMF is: $nmfPurity2%.5f")
  }

  def testClustering(phenotypeLabel: RDD[(String, Int)], rawFeatures:RDD[(String, Vector)]): (Double, Double, Double) = {
    import org.apache.spark.mllib.linalg.Matrix
    import org.apache.spark.mllib.linalg.distributed.RowMatrix

    /** scale features */
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(rawFeatures.map(_._2))
    val features = rawFeatures.map({ case (patientID, featureVector) => (patientID, scaler.transform(Vectors.dense(featureVector.toArray)))})
    val rawFeatureVectors = features.map(_._2).cache()

    /** reduce dimension */
    val mat: RowMatrix = new RowMatrix(rawFeatureVectors)
    val pc: Matrix = mat.computePrincipalComponents(10) // Principal components are stored in a local dense matrix.
    val featureVectors = mat.multiply(pc).rows

    val densePc = Matrices.dense(pc.numRows, pc.numCols, pc.toArray).asInstanceOf[DenseMatrix]
    def transform(feature: Vector): Vector = {
//      val scaled = scaler.transform(Vectors.dense(feature.toArray))
//      Vectors.dense(Matrices.dense(1, scaled.size, scaled.toArray).multiply(densePc).toArray)
      Vectors.dense(Matrices.dense(1, feature.size, feature.toArray).multiply(densePc).toArray)
    }

    /** TODO: K Means Clustering using spark mllib
      *  Train a k means model using the variabe featureVectors as input
      *  Set maxIterations =20 and seed as 0L
      *  Assign each feature vector to a cluster(predicted Class)
      *  HINT: You might have to use transform function while predicting
      *  Obtain an RDD[(Int, Int)] of the form (cluster number, RealClass)
      *  Find Purity using that RDD as an input to Metrics.purity
      *  Remove the placeholder below after your implementation
      **/
//    println("start kmeans")
    val k = 3
    val kMeansFunc = new KMeans()
    kMeansFunc.setK(k)
    kMeansFunc.setMaxIterations(20)
    kMeansFunc.setSeed(0L)

    val kMeansPurityHelp = kMeansFunc.run(featureVectors)
    val kMeansPredict = kMeansPurityHelp.predict(featureVectors)
    //val labels = features.join(phenotypeLabel).map({ case (patientID, (feature, realClass)) => realClass})
    val featuresZipped = features.zipWithIndex().map(m => (m._2, m._1._1))
    val kMeansZipped = kMeansPredict.zipWithIndex().map(m => (m._2, m._1))
    val featuresAndkMeans = featuresZipped.join(kMeansZipped)
    val featuresAndkMeansSwap = featuresAndkMeans.map(m=>(m._2._1, m._2._2))
    val predAndLab = featuresAndkMeansSwap.join(phenotypeLabel).map(m => (m._2._1, m._2._2))
    val kMeansPurity = Metrics.purity(predAndLab)
    //val runPercentagesOfClustersKmeans = Metrics.percentagesOfClusters(predAndLab)

    /** TODO: GMMM Clustering using spark mllib
      *  Train a Gaussian Mixture model using the variabe featureVectors as input
      *  Set maxIterations =20 and seed as 0L
      *  Assign each feature vector to a cluster(predicted Class)
      *  HINT: You might have to use transform function while predicting
      *  Obtain an RDD[(Int, Int)] of the form (cluster number, RealClass)
      *  Find Purity using that RDD as an input to Metrics.purity
      *  Remove the placeholder below after your implementation
      **/
//    println("start gaussian mixture")
    val gauss = new GaussianMixture()
    gauss.setK(k)
    gauss.setMaxIterations(20)
    gauss.setSeed(0L)
    val gaussPredHelper = gauss.run(featureVectors)
    val gaussPred = gaussPredHelper.predict(featureVectors)
    val gaussZipped = gaussPred.zipWithIndex().map(m => (m._2, m._1))
    val featuresAndGauss = featuresZipped.join(gaussZipped)
    val featuresAndGaussSwap = featuresAndGauss.map(m => (m._2._1, m._2._2))
    val gaussPredAndLab = featuresAndGaussSwap.join(phenotypeLabel).map(m => (m._2._1, m._2._2))
    val gaussianMixturePurity = Metrics.purity(gaussPredAndLab)
    //val runPercentagesOfClusters = Metrics.percentagesOfClusters(gaussPredAndLab)

    /** NMF */
//    val (w, _) = NMF.run(new RowMatrix(rawFeatureVectors), 3, 200)
      val rawFeaturesNonnegative = rawFeatures.map({ case (patientID, f) => Vectors.dense(f.toArray.map(v=>Math.abs(v)))})
      val (w, _) = NMF.run(new RowMatrix(rawFeaturesNonnegative), k, 100)
    // for each row (patient) in W matrix, the index with the max value should be assigned as its cluster type
    val assignments = w.rows.map(_.toArray.zipWithIndex.maxBy(_._1)._2)

//    val labels = features.join(phenotypeLabel).map({ case (patientID, (feature, realClass)) => realClass})

    // zip assignment and label into a tuple for computing purity 
//    val nmfClusterAssignmentAndLabel = assignments.zipWithIndex().map(_.swap).join(labels.zipWithIndex().map(_.swap)).map(_._2)
    //Note that map doesn't change the order of rows
    val assignmentsWithPatientIds=features.map({case (patientId ,f)=>patientId}).zip(assignments)
    //join your cluster assignments and phenotypeLabel on the patientID and obtain a RDD[(Int, Int)]
    //which is a RDD of (clusterNumber, phenotypeLabel) pairs
    val nmfClusterAssignmentAndLabel = assignmentsWithPatientIds.join(phenotypeLabel).map({case (patientID, value)=>value})
    val nmfPurity = Metrics.purity(nmfClusterAssignmentAndLabel)
//    val nmfClusters = Metrics.percentagesOfClusters(nmfClusterAssignmentAndLabel)
    (kMeansPurity, gaussianMixturePurity, nmfPurity)
  }

  /**
   * load the sets of string for filtering of medication
   * lab result and diagnostics
    *
    * @return
   */
  def loadLocalRawData: (Set[String], Set[String], Set[String]) = {
    val candidateMedication = Source.fromFile("data/med_filter.txt").getLines().map(_.toLowerCase).toSet[String]
    val candidateLab = Source.fromFile("data/lab_filter.txt").getLines().map(_.toLowerCase).toSet[String]
    val candidateDiagnostic = Source.fromFile("data/icd9_filter.txt").getLines().map(_.toLowerCase).toSet[String]
    (candidateMedication, candidateLab, candidateDiagnostic)
  }

  def loadRddRawData(sqlContext: SQLContext): (RDD[Medication], RDD[LabResult], RDD[Diagnostic]) = {
    /** You may need to use this date format. */
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")

    /** load data using Spark SQL into three RDDs and return them
      * Hint: You can utilize: edu.gatech.cse8803.ioutils.CSVUtils and SQLContext
      *       Refer to model/models.scala for the shape of Medication, LabResult, Diagnostic data type
      *       Be careful when you deal with String and numbers in String type
      * */

    /** TODO: implement your own code here and remove existing placeholder code below */

    val med = CSVUtils.loadCSVAsTable(sqlContext, "data/medication_orders_INPUT.csv")
    med.registerTempTable("med")
    val medTable = sqlContext.sql("SELECT Member_ID AS patientID, Order_Date AS date, Drug_Name AS medicine FROM med")
//    val medTableVal = medTable.map{
//      case patientID => medTable("patientID")
//      case date => medTable.map(r => dateFormat.parse(r(1).toString))
//      case medicine => medTable("medicine")
//    }
//    println(medTableVal)
//    println(medTableVal.getClass)
    val medTableVal = medTable.map(r => Medication ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase))
//    val medTableVal = medTable.map( r => Medication (
//        medTable("patientID"),
//        medTable("date"),
//        medTable("medicine")
//      )
//    )
//    println(medTableVal)
//    println(medTableVal.getClass)
//    println(medTableVal.first())
//
//    //need to change date to date

    //println(medTableVal.count())


    val lab = CSVUtils.loadCSVAsTable(sqlContext, "data/lab_results_INPUT.csv")
    lab.registerTempTable("lab")
    //println(lab)
    val labTable = sqlContext.sql("SELECT Member_ID AS patientID, Date_Resulted AS date, Result_Name AS testName, Numeric_Result AS rawvalue FROM lab WHERE Numeric_Result <> '' AND Numeric_Result IS NOT NULL")
    val labTableTry = labTable.toDF("patientID", "date", "testName", "rawvalue")
    val labTableRep = labTableTry.withColumn("value", labTableTry("rawvalue").cast(DoubleType))
    val labTableVal = labTableRep.map(r => LabResult ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase, r(4).asInstanceOf[Double]))
//    val labTableVal = labTable.withColumn("numtemp", labTable.value.cast(Double))
//        .drop("value")
//        .withColumnRenamed("numtemp","value")
//    val labTableVal = labTable.select( labTable.columns.map{
//      case patientID @ "patientID" => labTable(patientID)
//      case date @ "date" => labTable(date)
//      case testName @ "testName" => labTable(testName)
//      case value @ "value" => labTable(value).cast(DoubleType)
//    }: _*)
//
//    val labTableDateVal = labTable("date")
//    println(labTableDateVal.getClass.getSimpleName)
//    println(labTableVal.first())
//    println(labTableVal.getClass.getSimpleName)
    //need to change date to date.

    val edx = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_dx_INPUT.csv")
    val enc = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_INPUT.csv")
    //   println(edx)
//    println(enc)
    edx.registerTempTable("edx")
    enc.registerTempTable("enc")
    val diagTable = sqlContext.sql("SELECT Member_ID AS patientID, Encounter_DateTime AS date, code FROM enc LEFT OUTER JOIN edx on enc.Encounter_ID = edx.Encounter_ID")
    //need to change date to date.
    //println(diagTable)
    val diagTableVal = diagTable.map( r => Diagnostic ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase))
//    println(diagTableVal.count())

//    val medCols = med.map{ (x) => (x(1), x(2), x(3)) }
//    println(medCols)
    //val labSQL = CSVUtils.loadCSVAsTable(sqlContext, "data/lab_results_INPUT.csv")
    //val encSQL = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_INPUT.csv")
    //val edxSQL = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_dx_INPUT.csv")
//
//    val med = sqlContext.textFile("data/medication_orders_INPUT.csv")
//    val headerAndRows = med.map(line => line.split(",").map(_.trim))
//    val header = headerAndRows.first
//    val data = headerAndRows.filter(_(0) != header(0))
//    val maps = data.map(splits => header.zip(splits).toMap)



    val medication: RDD[Medication] = medTableVal
    val labResult: RDD[LabResult] =  labTableVal
    val diagnostic: RDD[Diagnostic] =  diagTableVal

    (medication, labResult, diagnostic)
  }

  def createContext(appName: String, masterUrl: String): SparkContext = {
    val conf = new SparkConf().setAppName(appName).setMaster(masterUrl)
    new SparkContext(conf)
  }

  def createContext(appName: String): SparkContext = createContext(appName, "local")

  def createContext: SparkContext = createContext("CSE 8803 Homework Two Application", "local")
}
