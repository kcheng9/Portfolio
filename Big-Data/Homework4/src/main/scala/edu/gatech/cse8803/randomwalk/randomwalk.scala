package edu.gatech.cse8803.randomwalk

import edu.gatech.cse8803.model.{PatientProperty, EdgeProperty, VertexProperty}
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.graphx._

object RandomWalk {

  def randomWalkOneVsAll(graph: Graph[VertexProperty, EdgeProperty], patientID: Long, numIter: Int = 100, alpha: Double = 0.15): List[Long] = {
    /**
      * Given a patient ID, compute the random walk probability w.r.t. to all other patients.
      * Return a List of patient IDs ordered by the highest to the lowest similarity.
      * For ties, random order is okay
    */

    /** Remove this placeholder and implement your code */

    val patients = graph.vertices
      .filter{case(id, property) => property.toString().contains("PatientProperty")}
      .map(m => m._1.toLong)

    val patientMax = patients.max.toLong

    var resetProb = alpha

//    println(resetProb)
//    println(alpha)

    var rankGraph: Graph[Double, Double] = graph
      // Associate the degree with each vertex
      .outerJoinVertices(graph.outDegrees) { (vid, vdata, deg) => deg.getOrElse(0) }
      // Set the weight on the edges based on the degree
      .mapTriplets( e => 1.0 / e.srcAttr, TripletFields.Src )
      // Set the vertex attributes to the initial pagerank values
      .mapVertices( (id, attr) => resetProb )

//    graph.vertices.take(3).foreach(println)
//    graph.edges.take(3).foreach(println)
//    graph.triplets.take(3).foreach(println)
//
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.vertices.take(3).foreach(println)
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.edges.take(3).foreach(println)
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.triplets.take(3).foreach(println)
//
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.mapTriplets(e => 1.0/e.srcAttr, TripletFields.Src)
//      .vertices.take(3).foreach(println)
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.mapTriplets(e => 1.0/e.srcAttr, TripletFields.Src)
//      .edges.take(3).foreach(println)
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.mapTriplets(e => 1.0/e.srcAttr, TripletFields.Src)
//      .triplets.take(3).foreach(println)
//
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.mapTriplets(e => 1.0/e.srcAttr, TripletFields.Src)
//        .mapVertices((id, attr) => resetProb).vertices.take(3).foreach(println)
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.mapTriplets(e => 1.0/e.srcAttr, TripletFields.Src)
//      .mapVertices((id, attr) => resetProb).edges.take(3).foreach(println)
//    graph.outerJoinVertices(graph.outDegrees){(vid, vdata, deg) => deg.getOrElse(0)}.mapTriplets(e => 1.0/e.srcAttr, TripletFields.Src)
//      .mapVertices((id, attr) => resetProb).triplets.take(3).foreach(println)

    var iteration = 0
    //var prevRankGraph: Graph[Double, Double] = null
    while (iteration < numIter) {
//      println(iteration)
      rankGraph.cache()

      // Compute the outgoing rank contributions of each vertex, perform local preaggregation, and
      // do the final aggregation at the receiving vertices. Requires a shuffle for aggregation.
      val rankUpdates = rankGraph.aggregateMessages[Double](
        ctx => ctx.sendToDst(ctx.srcAttr * ctx.attr), _ + _, TripletFields.Src)
//      rankUpdates.take(10).foreach(println)
      // Apply the final rank updates to get the new ranks, using join to preserve ranks of vertices
      // that didn't receive a message. Requires a shuffle for broadcasting updated ranks to the
      // edge partitions.
      var prevRankGraph = rankGraph
      rankGraph = rankGraph.joinVertices(rankUpdates) {
        (id, oldRank, msgSum) => {
          if (id.toLong == patientID){
            resetProb + (1.0 - resetProb) * msgSum
          }
          else{
            (1.0 - resetProb) * msgSum
          }
        }
      }.cache()



//      rankGraph.vertices.take(3).foreach(println)
//      rankGraph.edges.take(3).foreach(println)
//      rankGraph.triplets.take(3).foreach(println)

      rankGraph.edges.foreachPartition(x => {}) // also materializes rankGraph.vertices
      prevRankGraph.vertices.unpersist(false)
      prevRankGraph.edges.unpersist(false)

      iteration += 1
    }

//    rankGraph.vertices.filter(f => f._1.toLong <= patientMax).filter(f => f._2 < 0.15).sortBy(s => -s._2)
//      .take(15).foreach(println)

    //rankGraph.vertices.filter(f => f._1.toLong <=patientMax).foreach(println)

//    println(patientMax)
//
//    // Initialize the pagerankGraph with each edge attribute having
//    // weight 1/outDegree and each vertex with attribute 1.0.
//    val pagerankGraph: Graph[Double, Double] = graph
//      // Associate the degree with each vertex
//      .outerJoinVertices(graph.outDegrees) { (vid, vdata, deg) => deg.getOrElse(0) }
//      // Set the weight on the edges based on the degree
//      .mapTriplets( e => 1.0 / e.srcAttr )
//      // Set the vertex attributes to the initial pagerank values
//      .mapVertices( (id, attr) => 1.0 )
//      .cache()
//
//    // Define the three functions needed to implement PageRank in the GraphX
//    // version of Pregel
//    def vertexProgram(id: VertexId, attr: Double, msgSum: Double): Double =
//      if(id.toLong == patientID){
//        resetProb + (1.0 - resetProb) * msgSum
//      }
//      else{
//          (1.0 - resetProb) * msgSum
//      }
//    def sendMessage(edge: EdgeTriplet[Double, Double]) =
//      Iterator((edge.dstId, edge.srcAttr * edge.attr))
//    def messageCombiner(a: Double, b: Double): Double = a + b
//    // The initial message received by all vertices in PageRank
//    val initialMessage = 0.0
//
//
//    // Execute pregel for a fixed number of iterations.
//    val rankings = Pregel(pagerankGraph, initialMessage, numIter, activeDirection = EdgeDirection.Out)(
//      vertexProgram, sendMessage, messageCombiner)
//
//    rankings.vertices.filter(f => f._1.toLong <= patientMax).sortBy(s => -s._2).take(15).foreach(println)
//    rankings.edges.map(m => (m.srcId, m.dstId, m.attr)).sortBy(s => -s._3).take(15).foreach(println)

//    val pageranktry = graph.pageRank(100, .15)
//    pageranktry.vertices.foreach(println)
//    println("rankgraph")
    //rankGraph.vertices.filter(f => f._1.toLong <= patientMax).filter(f => f._2 < 0.15).sortBy(s => -s._2).take(15).foreach(println)

    val ranks = rankGraph.vertices.filter(f => f._1.toLong <= patientMax).filter(f => f._2 < 0.15).sortBy(s => -s._2).map(m => m._1.toLong)

    val rankList = ranks.collect().toList

    return rankList

    //List(1,2,3,4,5)
  }
}