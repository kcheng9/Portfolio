package edu.gatech.cse8803.main

import java.text.SimpleDateFormat

import edu.gatech.cse8803.ioutils.CSVUtils
import edu.gatech.cse8803.model._
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{GradientBoostedTrees, RandomForest}
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics}
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}

import scala.io.Source


object Main {
  def main(args: Array[String]) {
    import org.apache.log4j.Logger
    import org.apache.log4j.Level

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val sc = createContext
    val sqlContext = new SQLContext(sc)

    /* Load prefiltered patients and features from CSV into RDDs.
     * Prefilters include filtering only patients in the cohort, prelabeling them case or control, finding their
      * index date (28 hours before onset of septic shock for case, discharge date for control), and filtering
      * the feature CSVs (chart events, input events, diagnostics, labs) so only features from before the index
      * date and only interesting features are included.*/
    val patients = loadPatient(sqlContext)
    val filteredChartEvents = loadChartEvents(sqlContext)
    val filteredInputEvents = loadInputEvents(sqlContext)
    val filteredDiagnostics = loadDiagnostics(sqlContext)
    val filteredLabs = loadLabs(sqlContext)

    /* Create sets of the itemIDs that define the different features. These were found based on observation of the
     * data set. */
    val diabetesCodes = Set("25000", "25001", "25002", "25003", "25010", "25011", "25012", "25013", "25020", "25021",
      "25022", "25023", "25030", "25031", "25032", "25033", "25040", "25041", "25042", "25043", "25050", "25051", "25052",
    "25053", "25060", "25061", "25062", "25063", "25070", "25071", "25072", "25073", "25080", "25081", "25082", "25083",
      "25090", "25092", "25093")
    val heartFailureCodes = Set("4280", "4281", "42820", "42821", "42822", "42823", "42830", "42831", "42832", "42833",
      "42840", "42841", "42842", "42843", "4289")
    val hematologicalCode = Set("20000", "20001", "20002", "20003", "20004", "20005", "20006", "20007", "20008", "20010",
      "20011", "20012", "20013", "20014", "20015", "20016", "20017", "20018", "20020", "20021", "20022", "20023", "20024",
      "20025", "20026", "20027", "20028", "20030", "20031", "20032", "20033", "20034", "20035", "20036", "20037", "20038",
      "20040", "20041", "20042", "20043", "20044", "20045", "20046", "20047", "20048", "20050", "20051", "20052", "20053",
      "20054", "20055", "20056", "20057", "20058", "20060", "20061", "20062", "20063", "20064", "20065", "20066", "20067",
      "20068", "20070", "20071", "20072", "20073", "20074", "20075", "20076", "20077", "20078", "20080", "20081", "20082",
      "20083", "20084", "20085", "20086", "20087", "20088", "20100", "20101", "20102", "20103", "20104", "20105", "20106",
      "20107", "20108", "20110", "20111", "20112", "20113", "20114", "20115", "20116", "20117", "20118", "20120", "20121",
      "20122", "20123", "20124", "20125", "20126", "20127", "20128", "20140", "20141", "20142", "20143", "20144", "20145",
      "20146", "20147", "20148", "20150", "20151", "20152", "20153", "20154", "20155", "20156", "20157", "20158", "20160",
      "20161", "20162", "20163", "20164", "20165", "20166", "20167", "20168", "20170", "20171", "20172", "20173", "20174",
      "20175", "20176", "20177", "20178", "20190", "20191", "20192", "20193", "20194", "20195", "20196", "20197", "20198",
      "20200", "20201", "20202", "20203", "20204", "20205", "20206", "20207", "20208", "20210", "20211", "20212", "20213",
      "20214", "20215", "20216", "20217", "20218", "20220", "20221", "20222", "20223", "20224", "20225", "20226", "20227",
      "20228", "20230", "20231", "20232", "20233", "20234", "20235", "20236", "20237", "20238", "20240", "20241", "20242",
      "20243", "20244", "20245", "20246", "20247", "20248", "20250", "20251", "20252", "20253", "20254", "20255", "20256",
      "20257", "20258", "20260", "20261", "20262", "20263", "20264", "20265", "20266", "20267", "20268", "20270", "20271",
      "20272", "20273", "20274", "20275", "20276", "20277", "20278", "20280", "20281", "20282", "20283", "20284", "20285",
      "20286", "20287", "20288", "20290", "20291", "20292", "20293", "20294", "20295", "20296", "20297", "20298", "20300",
      "20301", "20302", "20310", "20311", "20312", "20380", "20381", "20382", "20400", "20401", "20402", "20410", "20411",
      "20412", "20420", "20421", "20422", "20480", "20481", "20482", "20490", "20491", "20492", "20500", "20501", "20502",
      "20510", "20511", "20512", "20520", "20521", "20522", "20530", "20531", "20532", "20580", "20581", "20582", "20590",
      "20591", "20592", "20600", "20601", "20602", "20610", "20611", "20612", "20620", "20621", "20622", "20680", "20681",
      "20682", "20690", "20691", "20692", "20700", "20701", "20702", "20710", "20711", "20712", "20720", "20721", "20722",
      "20780", "20781", "20782", "20800", "20801", "20802", "20810", "20811", "20812", "20820", "20821", "20822", "20880",
      "20881", "20882", "20890", "20891", "20892")
    val immunocompromisedCode = Set("42", "V580", "V5811", "V5812", "V5865", "20200", "20201", "20202", "20203", "20204",
      "20205", "20206", "20207", "20208", "20210", "20211", "20212", "20213", "20214", "20215", "20216", "20217", "20218",
      "20220", "20221", "20222", "20223", "20224", "20225", "20226", "20227", "20228", "20230", "20231", "20232", "20233",
      "20234", "20235", "20236", "20237", "20238", "20240", "20241", "20242", "20243", "20244", "20245", "20246", "20247",
      "20248", "20250", "20251", "20252", "20253", "20254", "20255", "20256", "20257", "20258", "20260", "20261", "20262",
      "20263", "20264", "20265", "20266", "20267", "20268", "20270", "20271", "20272", "20273", "20274", "20275", "20276",
      "20277", "20278", "20280", "20281", "20282", "20283", "20284", "20285", "20286", "20287", "20288", "20290", "20291",
      "20292", "20293", "20294", "20295", "20296", "20297", "20298", "20800", "20801", "20802", "20810", "20811", "20812",
      "20820", "20821", "20822", "20880", "20881", "20882", "20890", "20891", "20892")
    val liverDiseaseCode = Set("5710", "5711", "5712", "5713", "57140", "57141", "57142", "57149", "5715", "5716", "5718",
      "5719")
    val carcinomaCode = Set("1400", "1401", "1403", "1404", "1405", "1406", "1408", "1409", "1410", "1411", "1412", "1413",
      "1414", "1415", "1416", "1418", "1419", "1420", "1421", "1422", "1428", "1429", "1430", "1431", "1438", "1439", "1440",
    "1441", "1448", "1449", "1450", "1451", "1452", "1453", "1454", "1455", "1456", "1458", "1459", "1460", "1461", "1462",
      "1463", "1464", "1465", "1466", "1467", "1468", "1469", "1470", "1472", "1472", "1473", "1478", "1479", "1480", "1481",
    "1481", "1482", "1483", "1488", "1489", "1490", "1491", "1498", "1499", "1500", "1501", "1502", "1503", "1504", "1505",
      "1508", "1509", "1510", "1511", "1512", "1513", "1514", "1515", "1516", "1518", "1519", "1520", "1521", "1522", "1523",
    "1528", "1529", "1530", "1531", "1532", "1533", "1534", "1535", "1536", "1537", "1538", "1539", "1540", "1541", "1542",
      "1543", "1548", "1550", "1551", "1552", "1560", "1561", "1562", "1568", "1569", "1570", "1571", "1572", "1573", "1574",
    "1578", "1579", "1580", "1588", "1589", "1590", "1591", "1598", "1599", "1600", "1601", "1602", "1603", "1604", "1605",
      "1608", "1609", "1610", "1611", "1612", "1613", "1618", "1619", "1620", "1622", "1623", "1624", "1625", "1628", "1629",
    "1630", "1631", "1638", "1639", "1640", "1641", "1642", "1643", "1648", "1649", "1650", "1658", "1659", "1700", "1701",
      "1702", "1703", "1704", "1705", "1706", "1707", "1708", "1709", "1710", "1712", "1713", "1714", "1715", "1716", "1717",
    "1718", "1719", "1720", "1721", "1722", "1723", "1724", "1725", "1726", "1727", "1728", "1729", "17300", "17301",
      "17302", "17309", "17310", "17311", "17312", "17319", "17320", "17321", "17322", "17329", "17330", "17331", "17332",
    "17339", "17340", "17341", "17342", "17349", "17350", "17351", "17352", "17359", "17360", "17361", "17362", "17369",
      "17370", "17371", "17372", "17379", "17380", "17381", "17382", "17389", "17390", "17391", "17392", "17399", "1740",
    "1741", "1742", "1743", "1744", "1745", "1746", "1748", "1749", "1750", "1759", "179", "1800", "1801", "1808", "1809",
      "181", "1820", "1821", "1828", "1830", "1832", "1833", "1834", "1835", "1838", "1839", "1840", "1841", "1842", "1843",
      "1844", "1848", "1849", "185", "1860", "1869", "1871", "1872", "1873", "1874", "1875", "1876", "1877", "1878", "1879",
    "1880", "1881", "1882", "1883", "1884", "1885", "1886", "1887", "1888", "1889", "1890", "1891", "1892", "1893",
      "1894", "1898", "1899", "1900", "1901", "1902", "1903", "1904", "1905", "1906", "1907", "1908", "1909", "1910", "1911",
    "1912", "1913", "1914", "1915", "1916", "1917", "1918", "1919", "1920", "1921", "1922", "1923", "1928", "1929",
      "1931940", "1941", "1943", "1944", "1945", "1946", "1948", "1949", "1950", "1951", "1952", "1953", "1954", "1955",
    "1958", "1960", "1961", "1962", "1963", "1965", "1966", "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975",
      "1976", "1977", "1978", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "19881", "19882", "19889",
    "1990", "1991", "1992")
    val organInsufficiencyCode = Set("5856", "5719", "5718", "5716", "5715", "57149", "57142", "57141", "57140", "5713",
      "5712", "5711", "5710", "51883", "42842", "42832", "42822")
    val renalInsufficiencyCode = Set("5859", "V4511")
    val bunCode = Set("51006")
    val creatinineCode = Set("50912")
    val gcsCode = Set("184", "220739", "454", "223901", "198", "723", "223900")
    val hrCode = Set("211", "20045")
    val sbpCode = Set("51", "442", "455", "6701", "220179")

//    filteredDiagnostics.take(10).foreach(println)

    /* Create RDDs of the features.
     */
    val diabetes = filterDiags(filteredDiagnostics, diabetesCodes, 0)
    val heartFailure = filterDiags(filteredDiagnostics, heartFailureCodes, 1)
    val hematological = filterDiags(filteredDiagnostics, hematologicalCode, 2)
    val immunocompromised = filterDiags(filteredDiagnostics, immunocompromisedCode, 3)
    val liverDisease = filterDiags(filteredDiagnostics, liverDiseaseCode, 4)
    val carcinoma = filterDiags(filteredDiagnostics, carcinomaCode, 5)
    val organInsufficiency = filterDiags(filteredDiagnostics, organInsufficiencyCode, 6)
    val renalInsufficiency = filterDiags(filteredDiagnostics, renalInsufficiencyCode, 7)
    val bun = filterEvents(filteredLabs, bunCode, 8)
    val creatinine = filterEvents(filteredLabs, creatinineCode, 9)
    val gcs = filterEvents(filteredChartEvents, gcsCode, 10)
    val hr = filterEvents(filteredChartEvents, hrCode, 11)
    val sbp = filterEvents(filteredChartEvents, sbpCode, 12)
    val bun_over_cr = divider(bun, creatinine, 13)
    val hr_over_sbp = divider(hr, sbp, 14)

    /* Join all of the RDDs together into one RDD. */

    val featuregather = diabetes
      .union(heartFailure)
      .union(hematological)
      .union(immunocompromised)
      .union(liverDisease)
      .union(carcinoma)
      .union(organInsufficiency)
      .union(renalInsufficiency)
      .union(bun)
      .union(creatinine)
      .union(gcs)
      .union(hr)
      .union(sbp)
      .union(bun_over_cr)
      .union(hr_over_sbp)
      .map(m => (m._2, (m._1, m._3.toDouble)))
      .groupByKey.map(m => (m._1, m._2.toSeq))

    /* Create RDD[LabeledPoint] where Labeled Points are labeled based off of target (0 or 1 for control or case based
     off of patientID) and a sparse vector of feature values. Diagnostic feature values are 0 or 1. Event values are
     taken at the last time before the index date.*/
    val patientsMapped = patients.map(m => (m.patientID, m.label))

    val featuresWithLabel = featuregather.join(patientsMapped).map(m => (m._2._2, m._2._1))

    val vectors = featuresWithLabel.map(m => LabeledPoint(m._1.toDouble, Vectors.sparse(15, m._2)))

    /* Split the dataset into training and test. This is done with an 80%/20% random split */
    val splits = vectors.randomSplit(Array(0.8, 0.2), seed = 15L)
    val train = splits(0).cache()
    val test = splits(1).cache()

    /* Run the different models. NEED TO KNOW: Comment out the models you do not want to use and comment in the ones
     * you do want to use. */

    //RANDOM FOREST CLASSIFIER MODEL
    //println("RANDOM FOREST")
    val maxDepth = 10
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 50
    val featureSubsetStrategy = "auto"
    val impurity = "gini"
    val maxBins = 32

    val rfModel = RandomForest.trainClassifier(train, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    //val model = RandomForest.trainRegressor(train, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    val rfPredictionAndLabel = test.map{case LabeledPoint(label, features) => val prediction = rfModel.predict(features)
      (prediction, label)}

    //GRADIENT BOOSTED TREES CLASSIFIER
    //println("GRADIENT BOOSTED TREES")
    val boostingStrategy = BoostingStrategy.defaultParams("Classification")
    boostingStrategy.numIterations = 10
    boostingStrategy.treeStrategy.numClasses = 2
    boostingStrategy.treeStrategy.maxDepth = 5
    boostingStrategy.treeStrategy.categoricalFeaturesInfo = Map[Int, Int]()

    val gbtModel = GradientBoostedTrees.train(train, boostingStrategy)
    val gbtPredictionAndLabel = test.map{case LabeledPoint(label, features) => val prediction = gbtModel.predict(features)
            (prediction, label)}


    //LOGISTIC REGRESSION
    //println("LOGISTIC")
    val lrModel = new LogisticRegressionWithLBFGS().setNumClasses(2).run(train)
    val lrPredictionAndLabel = test.map{case LabeledPoint(label, features) => val prediction = lrModel.predict(features)
      (prediction, label)}
    lrModel.clearThreshold


//    SVM WITH SGD
    //println("SVM WITH SGD")
    val svmModel = SVMWithSGD.train(train, 100)
    val scModel = sc.broadcast(svmModel)
    val svmPredictionAndLabel = test.map(x => (scModel.value.predict(x.features), x.label))
    svmModel.clearThreshold


    //METRICS
    val rfMetrics = new BinaryClassificationMetrics(rfPredictionAndLabel,100)
    val gbtMetrics = new BinaryClassificationMetrics(gbtPredictionAndLabel,100)
    val lrMetrics = new BinaryClassificationMetrics(lrPredictionAndLabel,100)
    val svmMetrics = new BinaryClassificationMetrics(svmPredictionAndLabel,100)
    //ROC
    val rfROC = rfMetrics.roc
    val gbtROC = gbtMetrics.roc
    val lrROC = lrMetrics.roc
    val svmROC = svmMetrics.roc
    //PRECISION
    val rfPrecision = rfMetrics.precisionByThreshold
    val gbtPrecision = gbtMetrics.precisionByThreshold
    val lrPrecision = lrMetrics.precisionByThreshold
    val svmPrecision = svmMetrics.precisionByThreshold
    //RECALL
    val rfRecall = rfMetrics.recallByThreshold
    val gbtRecall = gbtMetrics.recallByThreshold
    val lrRecall = lrMetrics.recallByThreshold
    val svmRecall = svmMetrics.recallByThreshold
    //FSCORE
    val rfFScore = rfMetrics.fMeasureByThreshold
    val gbtFScore = gbtMetrics.fMeasureByThreshold
    val lrFScore = lrMetrics.fMeasureByThreshold
    val svmFScore = svmMetrics.fMeasureByThreshold
    //AUC
    val rfAUC = rfMetrics.areaUnderROC()
    val gbtAUC = gbtMetrics.areaUnderROC()
    val lrAUC = lrMetrics.areaUnderROC()
    val svmAUC = svmMetrics.areaUnderRO

    //PRINT STATISTICS
    println("RANDOM FOREST")
    println("precision")
    rfPrecision.take(1).foreach(println)
    println("recall")
    rfRecall.take(1).foreach(println)
    println("f-Score")
    rfFScore.take(1).foreach(println)
    println("auc")
    println(rfAUC)
    println("GRADIENT BOOSTED TREES")
    println("precision")
    gbtPrecision.take(1).foreach(println)
    println("recall")
    gbtRecall.take(1).foreach(println)
    println("f-Score")
    gbtFScore.take(1).foreach(println)
    println("auc")
    println(gbtAUC)
    println("LOGISTIC REGRESSION")
    println("precision")
    lrPrecision.take(1).foreach(println)
    println("recall")
    lrRecall.take(1).foreach(println)
    println("f-Score")
    lrFScore.take(1).foreach(println)
    println("auc")
    println(lrAUC)
    println("SVM WITH SGD")
    println("precision")
    svmPrecision.take(1).foreach(println)
    println("recall")
    svmRecall.take(1).foreach(println)
    println("f-Score")
    svmFScore.take(1).foreach(println)
    println("auc")
    println(svmAUC)

 //START START START START START START
//    val patients = loadPatients(sqlContext) //This is a unique set of patients
//    val labs = loadLabs(sqlContext) //Lab results
//    val labNames = loadLabNames(sqlContext) //Lab dictionary
//    val meds = loadMedications(sqlContext) //Medications
//    val itemNames = loadItemNames(sqlContext) //Item dictionary for all but labs
//    val chartEvents = loadChartEvents(sqlContext) //Events that go on charts
//    val outputEvents = loadOutputEvents(sqlContext) //Things output from the patient
//    val sepsisFeaturesItemIDs = loadSepsisFeatures(sqlContext) //Sepsis Features Not Diagnostic
//    val sepsisDiagICD9 = loadSepsisDiag(sqlContext) //Sepsis Diagnostic Features
//    val diags = loadDiag(sqlContext) //Diagnoses
//    val inputMV = loadInputMV(sqlContext)
//    val inputCV = loadInputCV(sqlContext)
//    val admitTimes = loadAdmit(sqlContext)

    //val featFilt = organDysfunctionFilter(diags, chartEvents, outputEvents, labs)
    //val patientsOver15 = patientFilter(patients, admitTimes, chartEvents, labs)

    //val featureComp = featureComputation(chartEvents)
//    val patientsWithIndex = loadPatientsWithIndex(sqlContext)
//    val sbp = loadLabel(sqlContext, 0, "data/patient_feature_sbp.csv")
//    val hr = loadLabel(sqlContext, 1, "data/patient_feature_hr.csv")
//    val hr_div_sbp = loadLabel(sqlContext, 2, "data/patient_feature_hr_div_sbp.csv")
//    val antibiotics = loadLabel(sqlContext,3, "data/patient_feature_antibiotics.csv")
//    val diabetes = loadLabel(sqlContext, 4, "data/patient_feature_diabetes.csv")
//    val heartFailure = loadLabel(sqlContext, 5, "data/patient_feature_heartfailure.csv")
//    val hematologicalMalignancy = loadLabel(sqlContext, 6, "data/patient_feature_hematologicalmalignancy.csv")
//    val immunocompromised = loadLabel(sqlContext, 7, "data/patient_feature_immunocompromised.csv")
//    val liverDisease = loadLabel(sqlContext, 8, "data/patient_feature_liverdisease.csv")
//    val metastaticCarcinoma = loadLabel(sqlContext, 9, "data/patient_feature_metastaticcarcinoma.csv")
//    val organInsufficiency = loadLabel(sqlContext, 10, "data/patient_feature_organinsufficiency.csv")
//    val renalInsufficiency = loadLabel(sqlContext, 11, "data/patient_feature_renalinsufficiency.csv")
//    val cr = loadLabel(sqlContext, 12, "data/patient_feature_cr.csv")
//    val bun = loadLabel(sqlContext, 13, "data/patient_feature_bun.csv")
//    val bun_div_cr = loadLabel(sqlContext, 14, "data/patient_feature_bun_div_cr.csv")
//
//    val featuregather = sbp
//        .union(hr)
//        .union(hr_div_sbp)
//        .union(antibiotics)
//        .union(diabetes)
//        .union(heartFailure)
//        .union(hematologicalMalignancy)
//        .union(immunocompromised)
//        .union(liverDisease)
//        .union(metastaticCarcinoma)
//        .union(organInsufficiency)
//        .union(renalInsufficiency)
//        .union(cr)
//        .union(bun)
//        .union(bun_div_cr)
//      .map(m => (m._2, (m._1, m._3.toDouble)))
//      .groupByKey.map(m => (m._1, m._2.toSeq))
//
//    val featuresWithLabel = featuregather.join(patientsWithIndex).map(m => (m._2._2, m._2._1))
//
//    val vectors = featuresWithLabel.map(m => LabeledPoint(m._1.toDouble, Vectors.sparse(15, m._2)))
//
//    val splits = vectors.randomSplit(Array(0.8, 0.2), seed = 15L)
//    val train = splits(0).cache()
//    val test = splits(1).cache()
//
//
//    //RANDOM FOREST CLASSIFIER
//    println("RANDOM FOREST")
//    val maxDepth = 10
//    val numClasses = 2
//    val categoricalFeaturesInfo = Map[Int, Int]()
//    val numTrees = 50
//    val featureSubsetStrategy = "auto"
//    val impurity = "gini"
//    val maxBins = 32
//
//    val model = RandomForest.trainClassifier(train, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
//    //val model = RandomForest.trainRegressor(train, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
//    val predictionAndLabel = test.map{case LabeledPoint(label, features) => val prediction = model.predict(features)
//      (prediction, label)}
//
//
//    //
////
////
////    //GRADIENT BOOSTED TREES CLASSIFIER
////    println("GRADIENT BOOSTED TREES")
////    val boostingStrategy = BoostingStrategy.defaultParams("Classification")
////    boostingStrategy.numIterations = 10
////    boostingStrategy.treeStrategy.numClasses = 2
////    boostingStrategy.treeStrategy.maxDepth = 5
////    boostingStrategy.treeStrategy.categoricalFeaturesInfo = Map[Int, Int]()
////
////    val model = GradientBoostedTrees.train(train, boostingStrategy)
////    val predictionAndLabel = test.map{case LabeledPoint(label, features) => val prediction = model.predict(features)
////            (prediction, label)}
//
////
////    //LOGISTIC REGRESSION
////    println("LOGISTIC")
////    val model = new LogisticRegressionWithLBFGS().setNumClasses(2).run(train)
////    val predictionAndLabel = test.map{case LabeledPoint(label, features) => val prediction = model.predict(features)
////      (prediction, label)}
////    model.clearThreshold
////
////
//////    SVM WITH SGD
////    println("SVM WITH SGD")
////    val model = SVMWithSGD.train(train, 100)
////    val scModel = sc.broadcast(model)
////    val predictionAndLabel = test.map(x => (scModel.value.predict(x.features), x.label))
////    model.clearThreshold
//
//
//    //METRICS
//    val metrics = new BinaryClassificationMetrics(predictionAndLabel,100)
//    val roc = metrics.roc
//    println("roc")
//    roc.foreach(println)
//    val precision = metrics.precisionByThreshold
//    println("precision")
//    precision.take(1).foreach(println)
//    val recall = metrics.recallByThreshold
//    println("recall")
//    recall.take(1).foreach(println)
//    val fScore = metrics.fMeasureByThreshold
//    println("fScore")
//    fScore.take(1).foreach(println)
//    val auc = metrics.areaUnderROC()
//    println("auc")
//    println(auc)
//
////    val metrics = new BinaryClassificationMetrics(predictionAndLabel,100)
////    val precision = metrics.precisionByThreshold
////    println("precision")
////    precision.take(1).foreach(println)
////    val recall = metrics.recallByThreshold
////    println("recall")
////    recall.take(1).foreach(println)
////    val fScore = metrics.fMeasureByThreshold
////    println("fScore")
////    fScore.take(1).foreach(println)
////    val roc = metrics.roc
////    println("roc")
////    roc.foreach(println)
////    val auc = metrics.areaUnderROC()
////    println("auc")
////    println(auc)
//
//// END END END END END END END
//    val numIterations = 100
//    val model = SVMWithSGD.train(train, numIterations)
//    val scModel = sc.broadcast(model)
//    val predictionAndLabel = test.map(x => (scModel.value.predict(x.features), x.label))
//    val accuracy = predictionAndLabel.filter(x => x._1==x._2).count / test.count.toFloat
//    println("testing Accuracy = " + accuracy)

  }

  /* This method does a calculation for value of over/under of features that require calculation of two other features. */

  def divider(over: RDD[(Int, String, Double)], under: RDD[(Int, String, Double)], label: Int): RDD[(Int, String, Double)] = {
    val overGrouped = over.map(m => (m._2, (m._3)))
    val underGrouped = under.map(m => (m._2, (m._3))).filter(f => f._2 != 0)

    val joined = overGrouped.join(underGrouped)

      .map(m => (m._1, m._2._1/m._2._2))

    val divided = joined.map(m => (label, m._1, m._2))

    return divided
  }

  /* This filters the feature values down to a single value per feature. This is done by choosing the value which is
  taken closest to the index date. Also, if there are multiple values taken at that time, they are averaged.
   */

  def maxEvents(filteredEvents: RDD[ChartEvent]): RDD[(String, Double)] = {
    val averageEvents = filteredEvents.map(m => ((m.patientID, m.date), m.value))
      .groupByKey()
      .mapValues{(value) => value.sum/value.size}

    val maxDates = averageEvents.map(m => ((m._1._1), m._1._2, m._2))
      .groupBy(g => g._1)
      .map(_._2.maxBy(b => b._2))

    val finalEvents = maxDates.map(m => (m._1, m._3))

    return finalEvents

  }

  /* This filters event features so that only the interesting ones are included (by interesting I mean ones that were
  ideantified by itemID to be included in the dataset.
   */

  def filterEvents(filteredEvents: RDD[ChartEvent], in: Set[String], label: Int): RDD[(Int, String, Double)] = {
    val eventFilter = filteredEvents.filter(f => in.contains(f.itemID))
      .distinct()

    val maxDates = maxEvents(eventFilter)

    val labeledEvent = maxDates.map(m => (label, m._1, m._2))

    return labeledEvent
  }

  /* This filters diagnostic features so that only the interesting ones are included (by interesting I mean ones that were
  ideantified by itemID to be included in the dataset.
   */

  def filterDiags(filteredDiag: RDD[Diag], in: Set[String], label: Int): RDD[(Int, String, Double)] = {
    val thisDiag = filteredDiag.filter(f => in.contains(f.code))
      .map(m => (m.patientID, "1"))
      .distinct()

    val labeledDiag = thisDiag.map(m => (label, m._1, m._2.toDouble))

    return labeledDiag
  }

  /* This loads diagnostic features from the final_filtered_diagnoses.csv file.*/

  def loadDiagnostics(sqlContext: SQLContext): RDD[Diag] = {
    val inp = CSVUtils.loadCSVAsTable(sqlContext, "data/final_filtered_diagnoses.csv")
    inp.registerTempTable("inp")

    val inTable = sqlContext.sql(
      "SELECT " +
        "subject_id as patientID," +
        "label as code" +
        " from inp"
    )

      val inTableVal = inTable.map(
        m => Diag(
          m(0).toString(),
          m(1).toString()
        )
      )


    return inTableVal
  }

  /* This loads input event features from the final_filtered_inputevents_mv.csv file.*/
  def loadInputEvents(sqlContext: SQLContext): RDD[ChartEvent] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val inp = CSVUtils.loadCSVAsTable(sqlContext, "data/final_filtered_inputevents_mv.csv")
    inp.registerTempTable("inp")

    val inTable = sqlContext.sql(
      "SELECT " +
        "subject_id as patientID," +
        "itemid as itemID," +
        "starttime as date," +
        "totalamount as value" +
        " FROM inp"
    )

    val inTableTry = inTable.toDF("patientID", "itemID", "date", "value")
    var inTableRep = inTableTry.withColumn("amt", inTableTry("value").cast(DoubleType))

    val inTableVal = inTableRep.map(
      m => ChartEvent(
        m(0).toString,
        m(1).toString,
        dateFormat.parse(m(2).toString),
        m(4).asInstanceOf[Double]
      )
    )

    return inTableVal

  }

  /* This loads lab event features from the final_filtered_labevents.csv file.*/

  def loadLabs(sqlContext: SQLContext): RDD[ChartEvent] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val inp = CSVUtils.loadCSVAsTable(sqlContext, "data/final_filtered_labevents.csv")
    inp.registerTempTable("inp")

    val inTable = sqlContext.sql(
      "SELECT " +
        "subject_id AS patientID," +
        "itemid AS itemID," +
        "charttime AS date," +
        "valuenum AS value" +
        " FROM inp"
    )

    val inTableTry = inTable.toDF("patientID", "itemID", "date", "value")
    var inTableRep = inTableTry.withColumn("amt", inTableTry("value").cast(DoubleType))

    val inTableVal = inTableRep.map(
      m => ChartEvent(
        m(0).toString,
        m(1).toString,
        dateFormat.parse(m(2).toString),
        m(4).asInstanceOf[Double]
      )
    )

    return inTableVal
  }

  /* This loads chart event features from the final_filtered_chartevents.csv file.*/

  def loadChartEvents(sqlContext: SQLContext): RDD[ChartEvent] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val inp = CSVUtils.loadCSVAsTable(sqlContext, "data/final_filtered_chartevents.csv")
    inp.registerTempTable("inp")

    val inTable = sqlContext.sql(
      "SELECT " +
        "subject_id AS patientID," +
        "itemid AS itemID," +
        "charttime AS date," +
        "valuenum AS value" +
        " FROM inp"
    )

    val inTableTry = inTable.toDF("patientID", "itemID", "date", "value")
    var inTableRep = inTableTry.withColumn("amt", inTableTry("value").cast(DoubleType))

    val inTableVal = inTableRep.map(
      m => ChartEvent(
        m(0).toString,
        m(1).toString,
        dateFormat.parse(m(2).toString),
        m(4).asInstanceOf[Double]
      )
    )

    return inTableVal
  }

  /* This loads patients from the final_all_patients.csv file.*/

  def loadPatient(sqlContext: SQLContext): RDD[Patient] = {
    val load = CSVUtils.loadCSVAsTable(sqlContext, "data/final_all_patients.csv")
    load.registerTempTable("load")

    val inTable = sqlContext.sql(
      "SELECT " +
        "subject_id as patientID," +
        "label as label" +
        " from load"
    )

    val out = inTable.map(m=>
      Patient(m(0).toString(),
        m(1).toString())
    )

    return out
  }

//  def loadLabel(sqlContext: SQLContext, label: Int, fileString: String): RDD[(Int, String, String)] = {
//    val load = CSVUtils.loadCSVAsTable(sqlContext, fileString)
//    load.registerTempTable("load")
//
//    val loadTable = sqlContext.sql(
//      "SELECT " +
//        "subject_id AS patientID," +
//        "label as label " +
//        "from load"
//    )
//
//    val loadVal = loadTable.map(
//      m =>
//        (label, m(0).toString(),
//          m(1).toString())
//    )
//    return loadVal
//  }

//  def loadAntibiotics(sqlContext: SQLContext): RDD[(Int, String, String)] = {
//    val sbp = CSVUtils.loadCSVAsTable(sqlContext, "data/patient_feature_antibiotics.csv")
//    sbp.registerTempTable("sbp")
//
//    val sbpTable = sqlContext.sql(
//      "SELECT " +
//        "subject_id AS patientID," +
//        "time_since_first_antibiotic AS value " +
//        "from sbp"
//    )
//
//    val sbpVal = sbpTable.map(
//      m =>
//        (3, m(0).toString(),
//          m(1).toString())
//    )
//
//    return sbpVal
//  }
//
//
//  def loadHR_div_SBP(sqlContext: SQLContext): RDD[(Int, String, String)] = {
//    val sbp = CSVUtils.loadCSVAsTable(sqlContext, "data/patient_feature_hr_div_sbp.csv")
//    sbp.registerTempTable("sbp")
//
//    val sbpTable = sqlContext.sql(
//      "SELECT " +
//        "subject_id AS patientID," +
//        "value AS value " +
//        "from sbp"
//    )
//
//    val sbpVal = sbpTable.map(
//      m =>
//        (2, m(0).toString(),
//          m(1).toString())
//    )
//
//    return sbpVal
//  }
//
//  def loadHR(sqlContext: SQLContext): RDD[(Int, String, String)] = {
//    val sbp = CSVUtils.loadCSVAsTable(sqlContext, "data/patient_feature_hr.csv")
//    sbp.registerTempTable("sbp")
//
//    val sbpTable = sqlContext.sql(
//      "SELECT " +
//        "subject_id AS patientID," +
//        "value AS value " +
//        "from sbp"
//    )
//
//    val sbpVal = sbpTable.map(
//      m =>
//        (1, m(0).toString(),
//          m(1).toString())
//    )
//
//    return sbpVal
//  }
//
//  def loadSBP(sqlContext: SQLContext): RDD[(Int, String, String)] = {
//    val sbp = CSVUtils.loadCSVAsTable(sqlContext, "data/patient_feature_sbp.csv")
//    sbp.registerTempTable("sbp")
//
//    val sbpTable = sqlContext.sql(
//      "SELECT " +
//        "subject_id AS patientID," +
//        "value AS value " +
//        "from sbp"
//    )
//
//    val sbpVal = sbpTable.map(
//      m =>
//        (0, m(0).toString(),
//          m(1).toString())
//    )
//
//    return sbpVal
//  }
//
//  def loadPatientsWithIndex(sqlContext: SQLContext): RDD[(String, String)] = {
////    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val pat = CSVUtils.loadCSVAsTable(sqlContext, "data/all_patients_with_label.csv")
//    pat.registerTempTable("pat")
//
//    val patTable = sqlContext.sql(
//      "SELECT " +
//        "subject_id AS patientID," +
//        "label AS label " +
//        " FROM pat"
//    )
//
//    val patVal = patTable.map(
//      m =>
//          (
//          m(0).toString(),
//          m(1).toString()
//          )
//    )
//
//    return patVal
//
//  }
//
//  //  def getAUC(curve: RDD[(Double, Double)]): Double = {
////
////    return auc
////  }
//
//  def testClassifier(sc: SparkContext, model: SVMModel, test: RDD[LabeledPoint]): Float = {
//    val scModel = sc.broadcast(model)
//    val predictionAndLabel = test.map(m => (scModel.value.predict(m.features), m.label))
//    val accuracy = predictionAndLabel.filter(f => f._1 == f._2).count / test.count
//      .toFloat
//    return accuracy
//  }
//
//  def trainClassifier(train: RDD[LabeledPoint], numIterations: Int): SVMModel = {
//    val model = SVMWithSGD.train(train, numIterations)
//    return model
//  }
//
//  def splitData(data: RDD[LabeledPoint]): (RDD[LabeledPoint], RDD[LabeledPoint]) = {
//    val splits = data.randomSplit(Array(0.6, 0.4), seed = 15L)
//    val train = splits(0).cache()
//    val test = splits(1).cache()
//
//    return (train, test)
//  }
//
//  def saveToSVM(data: RDD[LabeledPoint]) = {
//    val dataStr = data.map(x => x.label + "," + x.features.toArray.mkString(" "))
//    dataStr.saveAsTextFile("features.svmlight")
//  }
//
//  def loadFromSVM(sc: SparkContext, fileName: String): RDD[LabeledPoint]= {
//    val data = MLUtils.loadLibSVMFile(sc, fileName)
//
//    return data
//  }
//
//  def featureComputation(chartEvents: RDD[Event]): RDD[Event] = {
//
//
//
//    return chartEvents
//  }
//
////  def patientFilter(patients: RDD[Patient], admitTimes: RDD[Admit], chartEvents: RDD[Event], labEvents: RDD[Event]): RDD[Event] = {
////
////    val GCS = Set("184", "220739", "454", "223901", "198", "723", "223900")
////    val BUN = Set("51006") //labevents
////    val hematocrit = Set("")
////    val HR = Set("211", "220045")
////
////    val admit = admitTimes.map(m => (m.patientID, (m.hosp, m.time)))
////    val pat = patients.map(m => (m.patientID, (m.birthDate)))
////
////    val admitWithDOB = admit.join(pat)
////
////    val admitWithAge = admitWithDOB.map(m => (m._1, m._2._1._1, m._2._1._2.getYear - m._2._2.getYear))
////      .filter(f => f._3 >= 15)
////      .map(m => m._2).collect.distinct
////
//////    val chartWithAges = chartEvents.filter(f => admitWithAge.contains(f.hosp))
//////      .map(m => (m.hosp, (m.itemID)))
//////      .groupByKey
//////
//////    chartWithAges.take(5).foreach(println)
////    val filteredChartEvents = chartEvents.filter(f => admitWithAge.contains(f.hosp)).map(m => (m.hosp, m.itemID))
////    val filteredLabEvents = labEvents.filter(f => admitWithAge.contains(f.hosp)).map(m => (m.hosp, m.itemID))
////
////    val chartWithGCS = filteredChartEvents.filter(f => GCS.contains(f._2)).map(m => m._1).distinct()
////    val labWithBUN = filteredLabEvents.filter(f => BUN.contains(f._2)).map(m => m._1).distinct()
////
////    println(chartEvents.count())
////    println(chartWithGCS.count())
////
//////    val chartWithHR = filteredChartEvents.filter(f => HR.contains(f._2) && labWithBUN.contains(f._2)).map(m => m._1)
//////
//////    chartWithHR.take(5).foreach(println)
////
////
////
////
////
////
////
////    return chartEvents
////  }
//
//  def organDysfunctionFilter(diags: RDD[Diag], chartEvents: RDD[Event], outputEvents: RDD[Event], labEvents: RDD[Event]): RDD[Event] = {
//    val sysBP = Set("51", "422", "455", "6701", "220179", "220050")
//    val lactate = Set("50813")
//    val urine = Set("40055", "43175", "40069", "40094", "40715", "40473", "40085", "40057", "40056", "40056", "40405",
//    "40428", "40086", "40096", "40651", "226559", "226560", "227510", "226561", "226584", "226563", "226564", "226565",
//    "226567", "226557", "226558")
//    val weight = Set("762", "763", "3723", "3580", "3581", "3582", "224639", "226512") //226531 excluded based on values looking too big
//    val creatinine = Set("50912")
//    val renalInsuff = Set("V4511", "5859")
//    val platelet = Set("51265")
//    val inr = Set("51237")
//    val acuteLungInjury = Set("223835", "3420", "3422", "190", "490", "779", "226862")
//    val pneumonia = Set("486")
//    val liverDisease = Set("5710", "5711", "5712", "5713", "57140", "57141", "57142", "57149", "5715", "5716", "5718",
//      "5719")
//    val bilirubin = Set("50885")
//
////    val patientWeight = chartEvents.filter(f => weight.contains(f.itemID))
////      .map(m => (m.patientID, (m.value)))
////      .distinct()
////
////    val chartEventsMapped = chartEvents.map(m => (m.patientID, (m.itemID, m.date, m.value)))
////    val chartEventsWithWeight = chartEventsMapped.join(patientWeight)
//
////    chartEventsWithWeight.take(10).foreach(println)
//
//
//    val filterSysBP = chartEvents.filter(f => (sysBP.contains(f.itemID) && (f.value<90)))  //included in case
//    //val filterLactate = chartEvents.filter(f => (lactate.contains(f.itemID) && (f.value>2.0)))
//    //val filterUrine = outputEvents.filter(f => (urine.contains(f.itemID)) && (f.value<0.5))
//    val diagRenalInsuff = diags.filter(f => !renalInsuff.contains(f.code))
//      .map(m => m.patientID).collect().distinct
//    val filterCreatinine = chartEvents.filter(f => (creatinine.contains(f.itemID)
//      && diagRenalInsuff.contains(f.patientID) && (f.value > 2)))//included in case
//    val filterPlatelet = labEvents.filter(f => platelet.contains(f.itemID)  && (f.value<100))//included in case
//    val filterINR = labEvents.filter(f => inr.contains(f.itemID) && (f.value>1.5))//included in case
//    val fAcuteLungInjury = chartEvents.filter(f => acuteLungInjury.contains(f.itemID))
//    val hasPneumonia = diags.filter(f => pneumonia.contains(f.code))
//      .map(m => m.patientID).collect().distinct
//    val noPneumonia = diags.filter(f => !hasPneumonia.contains(f.patientID))
//      .map(m => m.patientID).collect().distinct
//    val filterALIPneu = fAcuteLungInjury.filter(f => ((f.value < 200 && hasPneumonia.contains(f.patientID))
//      || (f.value < 250 && noPneumonia.contains(f.patientID))))//included in case
//    val patWithLiverDisease = diags.filter(f => liverDisease.contains(f.code))
//      .map(m => m.patientID).collect().distinct
//    val patWOLiverDisease = diags.filter(f => !patWithLiverDisease.contains(f.patientID))
//      .map(m => m.patientID).collect().distinct
//    val filterBilirubin = chartEvents.filter(f => (bilirubin.contains(f.itemID) && (f.value > 2) &&
//      patWOLiverDisease.contains(f.patientID)))
//
//
//    return filterSysBP
//  }
//
//  def loadAdmit(sqlContext: SQLContext): RDD[Admit] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val admit = CSVUtils.loadCSVAsTable(sqlContext, "data/TRANSFERS_DATA_TABLE.csv")
//    admit.registerTempTable("admit")
//
//    val admitTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "HADM_ID AS hosp," +
//        "EVENTTYPE AS event," +
//        "INTIME AS time" +
//        " FROM admit WHERE EVENTTYPE = 'admit'"
//    )
//
//    val admitVal = admitTable.map(
//      m => Admit(
//        m(0).toString(),
//        m(1).toString(),
//        m(2).toString(),
//        dateFormat.parse(m(3).toString())
//      )
//    )
//
//    return admitVal
//
//  }
//
//  def loadInputCV(sqlContext: SQLContext): RDD[Input] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val inp = CSVUtils.loadCSVAsTable(sqlContext, "data/INPUTEVENTS_CV_DATA_TABLE.csv")
//    inp.registerTempTable("inp")
//
//    val inTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "CHARTTIME AS beg," +
//        "STORETIME AS t," +
//        "ITEMID AS itemID," +
//        "AMOUNT AS amount FROM inp"
//    )
//
//    val inTableTry = inTable.toDF("patientID", "beg", "t", "itemID", "amount")
//    var inTableRep = inTableTry.withColumn("amt", inTableTry("amount").cast(DoubleType))
//
//    val inTableVal = inTableRep.map(
//      m => Input(
//        m(0).toString,
//        dateFormat.parse(m(1).toString),
//        dateFormat.parse(m(2).toString),
//        m(3).toString,
//        m(5).asInstanceOf[Double]
//      )
//    )
//
//    return inTableVal
//  }
//  def loadInputMV(sqlContext: SQLContext): RDD[Input] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val inp = CSVUtils.loadCSVAsTable(sqlContext, "data/INPUTEVENTS_MV_DATA_TABLE.csv")
//    inp.registerTempTable("inp")
//
//    val inTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "STARTTIME AS beg," +
//        "ENDTIME AS t," +
//        "ITEMID AS itemID," +
//        "AMOUNT AS amount FROM inp"
//    )
//
//    val inTableTry = inTable.toDF("patientID", "beg", "t", "itemID", "amount")
//    var inTableRep = inTableTry.withColumn("amt", inTableTry("amount").cast(DoubleType))
//
//    val inTableVal = inTableRep.map(
//      m => Input(
//        m(0).toString,
//        dateFormat.parse(m(1).toString),
//        dateFormat.parse(m(2).toString),
//        m(3).toString,
//        m(5).asInstanceOf[Double]
//      )
//    )
//
//    return inTableVal
//
//  }
//
//  def loadDiag(sqlContext: SQLContext): RDD[Diag] = {
//    val sep = CSVUtils.loadCSVAsTable(sqlContext, "data/DIAGNOSES_ICD_DATA_TABLE.csv")
//    sep.registerTempTable("sep")
//
//    val sepTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "ICD9_CODE AS code FROM sep"
//    )
//
//    val sepVal = sepTable.map(
//      m => Diag(
//        m(0).toString(),
//        m(1).toString()
//      )
//    )
//
//    return sepVal
//  }
//
//  def loadSepsisDiag(sqlContext: SQLContext): RDD[Feature] = {
//    val sep = CSVUtils.loadCSVAsTable(sqlContext, "data/SEPSIS_DIAG_FEATURES_ICD9.csv")
//    sep.registerTempTable("sep")
//
//    val sepTable = sqlContext.sql(
//      "SELECT " +
//        "ICD9_CODE AS itemID," +
//        "FEATURE_LABEL AS label," +
//        "SOURCE_TABLE AS source FROM sep"
//    )
//
//    val sepVal = sepTable.map(
//      m => Feature(
//        m(0).toString(),
//        m(1).toString(),
//        m(2).toString()
//      )
//    )
//
//    return sepVal
//  }
//
//  def loadSepsisFeatures(sqlContext: SQLContext): RDD[Feature] = {
//    val sep = CSVUtils.loadCSVAsTable(sqlContext, "data/SEPSIS_FEATURES_ITEMIDS.csv")
//    sep.registerTempTable("sep")
//
//    val sepTable = sqlContext.sql(
//      "SELECT " +
//        "ITEMID AS itemID," +
//        "FEATURE_LABEL AS label," +
//        "SOURCE_TABLE AS source FROM sep"
//    )
//
//    val sepVal = sepTable.map(
//      m => Feature(
//        m(0).toString(),
//        m(1).toString(),
//        m(2).toString()
//      )
//    )
//
//    return sepVal
//  }
//
//  def loadOutputEvents(sqlContext: SQLContext): RDD[Event] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val out = CSVUtils.loadCSVAsTable(sqlContext, "data/OUTPUTEVENTS_DATA_TABLE.csv")
//    out.registerTempTable("out")
//
//    val outTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "ITEMID AS itemID," +
//        "CHARTTIME AS date," +
//        "VALUE AS value," +
//        "VALUEUOM AS uom, HADM_ID AS hosp FROM out"
//    )
//
//    val outTableTry = outTable.toDF("patientID", "itemID", "date", "value", "uom", "hosp")
//    val outTableRep = outTableTry.withColumn("val", outTableTry("value").cast(DoubleType))
//
//    val outVal = outTableRep.map(
//      m => Event (
//        m(0).toString(),
//        m(1).toString(),
//        dateFormat.parse(m(2).toString()),
//        m(6).asInstanceOf[Double],
//        m(4).toString(),
//        m(5).toString())
//      )
//
//    return outVal
//
//  }
//
//  def loadChartEventOLD(sqlContext: SQLContext): RDD[Event] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    val chart = CSVUtils.loadCSVAsTable(sqlContext, "data/CHARTEVENTS_DATA_TABLE.csv")
//    chart.registerTempTable("chart")
//
//    val chartTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "ITEMID AS itemID," +
//        "CHARTTIME AS date1," +
//        "STORETIME AS date2," +
//        "VALUE AS value," +
//        "VALUEUOM AS uom, HADM_ID AS hosp FROM chart WHERE ROW_ID < 3000"
//    )
//
//    val chartTableTry = chartTable.toDF("patientID", "itemID", "date1", "date2", "value", "uom", "hosp")
//    val chartTableRep = chartTableTry.withColumn("val", chartTableTry("value").cast(DoubleType))
//
//    val chartVal = chartTableRep.map(
//      m => Event (
//        m(0).toString(),
//        m(1).toString(),
//        dateFormat.parse((m(2).toString+" "+m(3).toString)),
//        m(7).asInstanceOf[Double],
//        m(5).toString(),
//        m(6).toString()
//      )
//    )
//
//    return chartVal
//  }
//
//  def loadItemNames(sqlContext: SQLContext): RDD[ItemName] = {
//    val item = CSVUtils.loadCSVAsTable(sqlContext, "data/D_ITEMS_DATA_TABLE.csv")
//    item.registerTempTable("item")
//
//    val itemTable = sqlContext.sql(
//      "SELECT " +
//        "ITEMID AS itemID," +
//        "LABEL AS itemName," +
//        "LINKSTO AS linksTo FROM item"
//    )
//
//    val itemVal = itemTable.map(
//      m => ItemName (
//        m(0).toString,
//        m(1).toString,
//        m(2).toString
//      )
//    )
//
//    return itemVal
//  }
//
//  def loadMedications(sqlContext: SQLContext): RDD[Medication] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
//    val med = CSVUtils.loadCSVAsTable(sqlContext, "data/PRESCRIPTIONS_DATA_TABLE.csv")
//    med.registerTempTable("med")
//
//    val medTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "STARTDATE AS date," +
//        "DRUG AS medicine FROM med"
//    )
//
//    val medVal = medTable.map(
//      m => Medication(m(0).toString(),
//        dateFormat.parse(m(1).toString()),
//        m(2).toString())
//    )
//
//    return medVal
//
//  }
//
//  def loadLabNames(sqlContext: SQLContext): RDD[LabName] = {
//    val labNames = CSVUtils.loadCSVAsTable(sqlContext, "data/D_LABITEMS_DATA_TABLE.csv")
//    labNames.registerTempTable("labNames")
//
//    val labNamesTable = sqlContext.sql(
//      "SELECT " +
//        "ITEMID AS testID," +
//        "LABEL AS testName FROM labNames"
//    )
//
//    val labNamesVal = labNamesTable.map(
//      m => LabName(m(0).toString,
//        m(1).toString)
//    )
//
//    return labNamesVal
//  }
//
//  def loadLab(sqlContext: SQLContext): RDD[Event] = {
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
//    val labs = CSVUtils.loadCSVAsTable(sqlContext, "data/LABEVENTS_DATA_TABLE.csv")
//    labs.registerTempTable("lab")
//
//    //    val labTable = sqlContext.sql("SELECT Member_ID AS patientID, Date_Resulted AS date, Result_Name AS testName, Numeric_Result AS rawvalue FROM lab WHERE Numeric_Result <> '' AND Numeric_Result IS NOT NULL")
//    //    val labTableTry = labTable.toDF("patientID", "date", "testName", "rawvalue")
//    //    val labTableRep = labTableTry.withColumn("value", labTableTry("rawvalue").cast(DoubleType))
//    //    val labTableVal = labTableRep.map(r => LabResult ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase, r(4).asInstanceOf[Double]))
//
//    val labTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "ITEMID AS testName," +
//        "CHARTTIME AS date," +
//        "VALUEUOM AS uom," +
//        "VALUENUM AS value, HADM_ID AS hosp FROM lab " +
//        "WHERE VALUENUM <> '' AND VALUENUM IS NOT NULL AND ROW_ID < 3000"
//    )
//
//    val labTableTry = labTable.toDF("patientID", "testName", "date", "uom", "value", "hosp")
//    val labTableRep = labTableTry.withColumn("val", labTableTry("value").cast(DoubleType))
//
//    val labTableVal = labTableRep.map(m =>
//      Event(m(0).toString().toLowerCase(),
//        m(1).toString().toLowerCase(),
//        dateFormat.parse(m(2).toString()),
//        m(6).asInstanceOf[Double],
//        m(3).toString(),
//        m(4).toString()))
//
//    return labTableVal
//  }
//
//  def loadPatients(sqlContext: SQLContext): RDD[Patient] ={
//
//    //case class Patient(patientID: String, birthDate: Date, deathDate: Date, hospitalDeathDate: Date, dead: Boolean)
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
//    val patients = CSVUtils.loadCSVAsTable(sqlContext, "data/a.csv")
//    patients.registerTempTable("pat")
//
//    val patientTable = sqlContext.sql(
//      "SELECT " +
//        "SUBJECT_ID AS patientID," +
//        "DOB AS birthDate," +
////        "DOD AS deathDate," +
////        "DOD_HOSP AS hospitalDeathDate," +
//        "EXPIRE_FLAG AS dead FROM pat"
//    )
//    //    val medTableVal = medTable.map(r => Medication ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase))
//
//    val patientTableVal = patientTable.map(
//      r => Patient ( r(0).toString.toLowerCase,
//        dateFormat.parse(r(1).toString),
////        dateFormat.parse(r(2).toString),
////        dateFormat.parse(r(3).toString),
//        r(2).toString().toInt.equals(1))
//    )
//
//    return patientTableVal
//
//  }

    /** initialize loading of data */
//    println("start")
//    val (medication, labResult, diagnostic) = loadRddRawData(sqlContext)
////    println("loaded")
//    val (candidateMedication, candidateLab, candidateDiagnostic) = loadLocalRawData
////    println("loaded2")
//
//    /** conduct phenotyping */
////    println("start phenotype")
//    val phenotypeLabel = T2dmPhenotype.transform(medication, labResult, diagnostic)
////    println("end phenotype")
//    /** feature construction with all features */
////    println("start All Features")
//    //println(FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic).collect().length))
//    val featureTuples = sc.union(
//      FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic),
//      FeatureConstruction.constructLabFeatureTuple(labResult),
//      FeatureConstruction.constructMedicationFeatureTuple(medication)
//    )
////    val featureTuples = FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic)
////      .union(FeatureConstruction.constructLabFeatureTuple(labResult))
////      .union(FeatureConstruction.constructMedicationFeatureTuple(medication))
////    println(featureTuples.collect().length)
//    val rawFeatures = FeatureConstruction.construct(sc, featureTuples)
//
//    val (kMeansPurity, gaussianMixturePurity, nmfPurity) = testClustering(phenotypeLabel, rawFeatures)
//    println(f"[All feature] purity of kMeans is: $kMeansPurity%.5f")
//    println(f"[All feature] purity of GMM is: $gaussianMixturePurity%.5f")
//    println(f"[All feature] purity of NMF is: $nmfPurity%.5f")
//
//    /** feature construction with filtered features */
////    println("start filtered features")
//    val filteredFeatureTuples = sc.union(
//      FeatureConstruction.constructDiagnosticFeatureTuple(diagnostic, candidateDiagnostic),
//      FeatureConstruction.constructLabFeatureTuple(labResult, candidateLab),
//      FeatureConstruction.constructMedicationFeatureTuple(medication, candidateMedication)
//    )
//
//    val filteredRawFeatures = FeatureConstruction.construct(sc, filteredFeatureTuples)
//
//    val (kMeansPurity2, gaussianMixturePurity2, nmfPurity2) = testClustering(phenotypeLabel, filteredRawFeatures)
//    println(f"[Filtered feature] purity of kMeans is: $kMeansPurity2%.5f")
//    println(f"[Filtered feature] purity of GMM is: $gaussianMixturePurity2%.5f")
//    println(f"[Filtered feature] purity of NMF is: $nmfPurity2%.5f")
//  }

//  def testClustering(phenotypeLabel: RDD[(String, Int)], rawFeatures:RDD[(String, Vector)]): (Double, Double, Double) = {
//    import org.apache.spark.mllib.linalg.Matrix
//    import org.apache.spark.mllib.linalg.distributed.RowMatrix
//
//    /** scale features */
//    val scaler = new StandardScaler(withMean = true, withStd = true).fit(rawFeatures.map(_._2))
//    val features = rawFeatures.map({ case (patientID, featureVector) => (patientID, scaler.transform(Vectors.dense(featureVector.toArray)))})
//    val rawFeatureVectors = features.map(_._2).cache()
//
//    /** reduce dimension */
//    val mat: RowMatrix = new RowMatrix(rawFeatureVectors)
//    val pc: Matrix = mat.computePrincipalComponents(10) // Principal components are stored in a local dense matrix.
//    val featureVectors = mat.multiply(pc).rows
//
//    val densePc = Matrices.dense(pc.numRows, pc.numCols, pc.toArray).asInstanceOf[DenseMatrix]
//    def transform(feature: Vector): Vector = {
////      val scaled = scaler.transform(Vectors.dense(feature.toArray))
////      Vectors.dense(Matrices.dense(1, scaled.size, scaled.toArray).multiply(densePc).toArray)
//      Vectors.dense(Matrices.dense(1, feature.size, feature.toArray).multiply(densePc).toArray)
//    }
//
//    /** TODO: K Means Clustering using spark mllib
//      *  Train a k means model using the variabe featureVectors as input
//      *  Set maxIterations =20 and seed as 0L
//      *  Assign each feature vector to a cluster(predicted Class)
//      *  HINT: You might have to use transform function while predicting
//      *  Obtain an RDD[(Int, Int)] of the form (cluster number, RealClass)
//      *  Find Purity using that RDD as an input to Metrics.purity
//      *  Remove the placeholder below after your implementation
//      **/
////    println("start kmeans")
//    val k = 5
//    val kMeansFunc = new KMeans()
//    kMeansFunc.setK(k)
//    kMeansFunc.setMaxIterations(20)
//    kMeansFunc.setSeed(0L)
//
//    val kMeansPurityHelp = kMeansFunc.run(featureVectors)
//    val kMeansPredict = kMeansPurityHelp.predict(featureVectors)
//    //val labels = features.join(phenotypeLabel).map({ case (patientID, (feature, realClass)) => realClass})
//    val featuresZipped = features.zipWithIndex().map(m => (m._2, m._1._1))
//    val kMeansZipped = kMeansPredict.zipWithIndex().map(m => (m._2, m._1))
//    val featuresAndkMeans = featuresZipped.join(kMeansZipped)
//    val featuresAndkMeansSwap = featuresAndkMeans.map(m=>(m._2._1, m._2._2))
//    val predAndLab = featuresAndkMeansSwap.join(phenotypeLabel).map(m => (m._2._1, m._2._2))
//    val kMeansPurity = Metrics.purity(predAndLab)
//    //val runPercentagesOfClustersKmeans = Metrics.percentagesOfClusters(predAndLab)
//
//    /** TODO: GMMM Clustering using spark mllib
//      *  Train a Gaussian Mixture model using the variabe featureVectors as input
//      *  Set maxIterations =20 and seed as 0L
//      *  Assign each feature vector to a cluster(predicted Class)
//      *  HINT: You might have to use transform function while predicting
//      *  Obtain an RDD[(Int, Int)] of the form (cluster number, RealClass)
//      *  Find Purity using that RDD as an input to Metrics.purity
//      *  Remove the placeholder below after your implementation
//      **/
////    println("start gaussian mixture")
//    val gauss = new GaussianMixture()
//    gauss.setK(k)
//    gauss.setMaxIterations(20)
//    gauss.setSeed(0L)
//    val gaussPredHelper = gauss.run(featureVectors)
//    val gaussPred = gaussPredHelper.predict(featureVectors)
//    val gaussZipped = gaussPred.zipWithIndex().map(m => (m._2, m._1))
//    val featuresAndGauss = featuresZipped.join(gaussZipped)
//    val featuresAndGaussSwap = featuresAndGauss.map(m => (m._2._1, m._2._2))
//    val gaussPredAndLab = featuresAndGaussSwap.join(phenotypeLabel).map(m => (m._2._1, m._2._2))
//    val gaussianMixturePurity = Metrics.purity(gaussPredAndLab)
//    //val runPercentagesOfClusters = Metrics.percentagesOfClusters(gaussPredAndLab)
//
//    /** NMF */
////    val (w, _) = NMF.run(new RowMatrix(rawFeatureVectors), 3, 200)
//      val rawFeaturesNonnegative = rawFeatures.map({ case (patientID, f) => Vectors.dense(f.toArray.map(v=>Math.abs(v)))})
//      val (w, _) = NMF.run(new RowMatrix(rawFeaturesNonnegative), k, 100)
//    // for each row (patient) in W matrix, the index with the max value should be assigned as its cluster type
//    val assignments = w.rows.map(_.toArray.zipWithIndex.maxBy(_._1)._2)
//
////    val labels = features.join(phenotypeLabel).map({ case (patientID, (feature, realClass)) => realClass})
//
//    // zip assignment and label into a tuple for computing purity
////    val nmfClusterAssignmentAndLabel = assignments.zipWithIndex().map(_.swap).join(labels.zipWithIndex().map(_.swap)).map(_._2)
//    //Note that map doesn't change the order of rows
//    val assignmentsWithPatientIds=features.map({case (patientId ,f)=>patientId}).zip(assignments)
//    //join your cluster assignments and phenotypeLabel on the patientID and obtain a RDD[(Int, Int)]
//    //which is a RDD of (clusterNumber, phenotypeLabel) pairs
//    val nmfClusterAssignmentAndLabel = assignmentsWithPatientIds.join(phenotypeLabel).map({case (patientID, value)=>value})
//    val nmfPurity = Metrics.purity(nmfClusterAssignmentAndLabel)
////    val nmfClusters = Metrics.percentagesOfClusters(nmfClusterAssignmentAndLabel)
//    (kMeansPurity, gaussianMixturePurity, nmfPurity)
//  }
//
//  /**
//   * load the sets of string for filtering of medication
//   * lab result and diagnostics
//    *
//    * @return
//   */
//  def loadLocalRawData: (Set[String], Set[String], Set[String]) = {
//    val candidateMedication = Source.fromFile("data/med_filter.txt").getLines().map(_.toLowerCase).toSet[String]
//    val candidateLab = Source.fromFile("data/lab_filter.txt").getLines().map(_.toLowerCase).toSet[String]
//    val candidateDiagnostic = Source.fromFile("data/icd9_filter.txt").getLines().map(_.toLowerCase).toSet[String]
//    (candidateMedication, candidateLab, candidateDiagnostic)
//  }

//  def loadRddRawData(sqlContext: SQLContext): (RDD[Medication], RDD[LabResult], RDD[Diagnostic]) = {
//    /** You may need to use this date format. */
//    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
//
//    /** load data using Spark SQL into three RDDs and return them
//      * Hint: You can utilize: edu.gatech.cse8803.ioutils.CSVUtils and SQLContext
//      *       Refer to model/models.scala for the shape of Medication, LabResult, Diagnostic data type
//      *       Be careful when you deal with String and numbers in String type
//      * */
//
//    /** TODO: implement your own code here and remove existing placeholder code below */
//
//    val med = CSVUtils.loadCSVAsTable(sqlContext, "data/medication_orders_INPUT.csv")
//    med.registerTempTable("med")
//    val medTable = sqlContext.sql("SELECT Member_ID AS patientID, Order_Date AS date, Drug_Name AS medicine FROM med")
////    val medTableVal = medTable.map{
////      case patientID => medTable("patientID")
////      case date => medTable.map(r => dateFormat.parse(r(1).toString))
////      case medicine => medTable("medicine")
////    }
////    println(medTableVal)
////    println(medTableVal.getClass)
//    val medTableVal = medTable.map(r => Medication ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase))
////    val medTableVal = medTable.map( r => Medication (
////        medTable("patientID"),
////        medTable("date"),
////        medTable("medicine")
////      )
////    )
////    println(medTableVal)
////    println(medTableVal.getClass)
////    println(medTableVal.first())
////
////    //need to change date to date
//
//    //println(medTableVal.count())
//
//
//    val lab = CSVUtils.loadCSVAsTable(sqlContext, "data/lab_results_INPUT.csv")
//    lab.registerTempTable("lab")
//    //println(lab)
//    val labTable = sqlContext.sql("SELECT Member_ID AS patientID, Date_Resulted AS date, Result_Name AS testName, Numeric_Result AS rawvalue FROM lab WHERE Numeric_Result <> '' AND Numeric_Result IS NOT NULL")
//    val labTableTry = labTable.toDF("patientID", "date", "testName", "rawvalue")
//    val labTableRep = labTableTry.withColumn("value", labTableTry("rawvalue").cast(DoubleType))
//    val labTableVal = labTableRep.map(r => LabResult ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase, r(4).asInstanceOf[Double]))
////    val labTableVal = labTable.withColumn("numtemp", labTable.value.cast(Double))
////        .drop("value")
////        .withColumnRenamed("numtemp","value")
////    val labTableVal = labTable.select( labTable.columns.map{
////      case patientID @ "patientID" => labTable(patientID)
////      case date @ "date" => labTable(date)
////      case testName @ "testName" => labTable(testName)
////      case value @ "value" => labTable(value).cast(DoubleType)
////    }: _*)
////
////    val labTableDateVal = labTable("date")
////    println(labTableDateVal.getClass.getSimpleName)
////    println(labTableVal.first())
////    println(labTableVal.getClass.getSimpleName)
//    //need to change date to date.
//
//    val edx = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_dx_INPUT.csv")
//    val enc = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_INPUT.csv")
//    //   println(edx)
////    println(enc)
//    edx.registerTempTable("edx")
//    enc.registerTempTable("enc")
//    val diagTable = sqlContext.sql("SELECT Member_ID AS patientID, Encounter_DateTime AS date, code FROM enc LEFT OUTER JOIN edx on enc.Encounter_ID = edx.Encounter_ID")
//    //need to change date to date.
//    //println(diagTable)
//    val diagTableVal = diagTable.map( r => Diagnostic ( r(0).toString.toLowerCase, dateFormat.parse(r(1).toString), r(2).toString.toLowerCase))
////    println(diagTableVal.count())
//
////    val medCols = med.map{ (x) => (x(1), x(2), x(3)) }
////    println(medCols)
//    //val labSQL = CSVUtils.loadCSVAsTable(sqlContext, "data/lab_results_INPUT.csv")
//    //val encSQL = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_INPUT.csv")
//    //val edxSQL = CSVUtils.loadCSVAsTable(sqlContext, "data/encounter_dx_INPUT.csv")
////
////    val med = sqlContext.textFile("data/medication_orders_INPUT.csv")
////    val headerAndRows = med.map(line => line.split(",").map(_.trim))
////    val header = headerAndRows.first
////    val data = headerAndRows.filter(_(0) != header(0))
////    val maps = data.map(splits => header.zip(splits).toMap)
//
//
//
//    val medication: RDD[Medication] = medTableVal
//    val labResult: RDD[LabResult] =  labTableVal
//    val diagnostic: RDD[Diagnostic] =  diagTableVal
//
//    (medication, labResult, diagnostic)
//  }

  def createContext(appName: String, masterUrl: String): SparkContext = {
    val conf = new SparkConf().setAppName(appName).setMaster(masterUrl)
    new SparkContext(conf)
  }

  def createContext(appName: String): SparkContext = createContext(appName, "local")

  def createContext: SparkContext = createContext("CSE 8803 Homework Two Application", "local")
}
