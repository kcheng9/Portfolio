/**
 * @author Hang Su <hangsu@gatech.edu>.
 */

package edu.gatech.cse8803.clustering

import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

object Metrics {
  /**
   * Given input RDD with tuples of assigned cluster id by clustering,
   * and corresponding real class. Calculate the purity of clustering.
   * Purity is defined as
   *             \fract{1}{N}\sum_K max_j |w_k \cap c_j|
   * where N is the number of samples, K is number of clusters and j
   * is index of class. w_k denotes the set of samples in k-th cluster
   * and c_j denotes set of samples of class j.
   * @param clusterAssignmentAndLabel RDD in the tuple format
   *                                  (assigned_cluster_id, class)
   * @return purity
   */
  def purity(clusterAssignmentAndLabel: RDD[(Int, Int)]): Double = {
    /**
     * TODO: Remove the placeholder and implement your code here
     */
//    println("start metrics")
    //This is N
    val N = clusterAssignmentAndLabel.map(m => m._1).collect().length
    //println(N)
    //val frac = 1/N
    //println(frac)
    //val clusters = clusterAssignmentAndLabel.map(m => m._1).collect().distinct //This is an array of the cluster #s
    //val classes = clusterAssignmentAndLabel.map(m => m._2).collect().distinct //This is an array of the class #s

    //Grouping like ((cluster, class), 1.0)
    val grouped = clusterAssignmentAndLabel.map(m => ((m._1, m._2), 1.0)).groupByKey()
    //println(grouped.take(10).foreach(println))
    //((cluster, class), count)
    val count = grouped.mapValues{(tuples) => tuples.size}
    //println(count.take(10).foreach(println))
    //(cluster, count)
    val ungrouped = count.map(m => (m._1._1, (m._2)))
    //println(ungrouped.take(10).foreach(println))
    //(cluster, (count))
    val groupByK = ungrouped.groupByKey()
    //println(groupByK.take(10).foreach(println))
    //(cluster, (countMax))
    val maxCount = groupByK.map(m => m._2.max)
    //println(maxCount.take(10).foreach(println))
    val sumMaxHelp = maxCount.sum()
    val purity = sumMaxHelp/N
    //val sumMax = sumMaxHelp.collect().sum
    //val purity = sumMax * frac

    return purity
  }
//(cluster, class)
  def percentagesOfClusters(clusterAssignmentAndLabel: RDD[(Int, Int)]): Double = {
    val uniqueClusters = clusterAssignmentAndLabel.map(m => m._1).collect().distinct

    println("Cluster % for 1")
    val justTheOnes = clusterAssignmentAndLabel.filter(f => f._2==1)
    val uniqueOnesTotal = justTheOnes.collect().length
    val onesGrouped = justTheOnes.groupByKey
    val onesCounted = onesGrouped.mapValues{(tuples) => tuples.size.toDouble/uniqueOnesTotal.toDouble}
    println(onesCounted.foreach(println))

    println("Cluster % for 2")
    val justTheTwos = clusterAssignmentAndLabel.filter(f => f._2==2)
    val uniqueTwosTotal = justTheTwos.collect().length
    val twosGrouped = justTheTwos.groupByKey
    val twosCounted = twosGrouped.mapValues{(tuples) => tuples.size.toDouble/uniqueTwosTotal.toDouble}
    println(twosCounted.foreach(println))

    println("%Cluster % for 3")
    val justTheThrees = clusterAssignmentAndLabel.filter(f => f._2==3)
    val uniqueThreesTotal = justTheThrees.collect().length
    val threesGrouped = justTheThrees.groupByKey
    val threesCounted = threesGrouped.mapValues{(tuples) => tuples.size.toDouble/uniqueThreesTotal.toDouble}
    println(threesCounted.foreach(println))

  return 0.0
  }
}
