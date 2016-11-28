package edu.gatech.cse8803.clustering

/**
  * @author Hang Su <hangsu@gatech.edu>
  */


import breeze.linalg.{DenseVector => BDV, DenseMatrix => BDM, sum}
import breeze.linalg._
import breeze.numerics._
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.mllib.linalg.distributed.RowMatrix


object NMF {

  /**
   * Run NMF clustering
    *
    * @param V The original non-negative matrix
   * @param k The number of clusters to be formed, also the number of cols in W and number of rows in H
   * @param maxIterations The maximum number of iterations to perform
   * @param convergenceTol The maximum change in error at which convergence occurs.
   * @return two matrixes W and H in RowMatrix and DenseMatrix format respectively 
   */
  def run(V: RowMatrix, k: Int, maxIterations: Int, convergenceTol: Double = 1e-4): (RowMatrix, BDM[Double]) = {

    /**
      * TODO 1: Implement your code here
      * Initialize W, H randomly
      * Calculate the initial error (Euclidean distance between V and W * H)
      */
    val W = (new RowMatrix(V.rows.map(_ => BDV.rand[Double](k)).map(fromBreeze).cache), BDM.rand[Double](k, V.numCols().toInt))
    val H = (new RowMatrix(V.rows.map(_ => BDV.rand[Double](k)).map(fromBreeze).cache), BDM.rand[Double](k, V.numCols().toInt))
    //val Wrows = W._1.rows.context
    //println(Wrows)
    //val WH = Wrows.
    val WH = multiply(W._1, H._2)
    val rows = V.rows.zip(WH.rows).map { case (v1: Vector, v2: Vector) => toBreezeVector(v1) - toBreezeVector(v2) }
      .map(fromBreeze)
    val WHminV = new RowMatrix(rows)
    //println(WHminV)
    val WHminVSquared = dotProd(WHminV, WHminV)

    //println(WHminVSquared)
    //println(WHminVSquared.rows.take(10).foreach(println))
    //val onesStarter = new DenseMatrix[Double](WHminVSquared.numRows().toInt, WHminVSquared.numCols().toInt)
    val WHminVSquaredSum = WHminVSquared.rows.map { case (v1) => toBreezeVector(v1).reduce(_ + _) }
    val tot = WHminVSquaredSum.reduce((v1, v2) => (v1 + v2).reduce(_ + _))
    //println(WHminVSquaredSum.take(10).foreach(println))
    //println(tot)
    val finTot = tot.reduce((e1, e2) => (e1 + e2))
    val initialError = finTot.sum
    //println(initialError.getClass())
    //println(initialError)

    /**
      * TODO 2: Implement your code here
      * Iteratively update W, H in a parallel fashion until error falls below the tolerance value
      * The updating equations are,
      * H = H.* W^T^V ./ (W^T^W H)
      * W = W.* VH^T^ ./ (W H H^T^)
      */
    var H2 = H._2
    var W2 = W._1
    var iter = 0
    var err = initialError
    while (iter < maxIterations && err > convergenceTol) {
      var WTV = computeWTV(W2, V)
      var WTW = computeWTV(W2, W2)
      var HWTV = H2 :* WTV
      var WTWH = (WTW * H2)
      WTWH +=  .002
      H2 = HWTV :/ WTWH

      var VHT = multiply(V, H2.t)
      var HHT = H2 * H2.t
      var WVHT = dotProd(W2, VHT)
      var WHHT = multiply(W2, HHT)
      W2 = dotDiv(WVHT, WHHT)

      iter += 1
      err = findErr(V, W2, H2)
      W2.rows.cache()
      V.rows.cache()
//      println(iter)
//      println(err)
    }
    //      //Update W[i,:]
    //      var HT = H2.t
    //      var Hs = H2*HT
    //      W2 = dotDiv(dotProd(W2, V*HT), W2.multiply(Hs))
    //
    //      //Update H[:,i]
    //      var WTW = computeWTV(W2, W2)
    //      var WTWH = multiply(WTW, H2)
    //      var WTV = computeWTV(W2, V)
    //      var HWTV = dotProd(H._1, WTV)
    //      H2 = getDenseMatrix(dotDiv(HWTV, WTWH))
    //
    //      iter = iter+1
    //    }


    /** TODO: Remove the placeholder for return and replace with correct values */
    return (W2, H2)
  }


  /**  
  * TODO: Implement the helper functions if you needed
  * Below are recommended helper functions for matrix manipulation
  * For the implementation of the first three helper functions (with a null return), 
  * you can refer to dotProd and dotDiv whose implementation are provided
  */
  /**
  * Note:You can find some helper functions to convert vectors and matrices
  * from breeze library to mllib library and vice versa in package.scala
  */

  /** compute the mutiplication of a RowMatrix and a dense matrix */

  private def findErr(V: RowMatrix, W: RowMatrix, H: BDM[Double]): Double = {
    val WH = multiply(W, H)
    val rows = V.rows.zip(WH.rows).map{case (v1: Vector, v2: Vector) => toBreezeVector(v1) - toBreezeVector(v2)}
      .map(fromBreeze)
    val WHminV = new RowMatrix(rows)
    //println(WHminV)
    val WHminVSquared = dotProd(WHminV, WHminV)

    //println(WHminVSquared)
    //println(WHminVSquared.rows.take(10).foreach(println))
    //val onesStarter = new DenseMatrix[Double](WHminVSquared.numRows().toInt, WHminVSquared.numCols().toInt)
    val WHminVSquaredSum = WHminVSquared.rows.map{case (v1) => toBreezeVector(v1).reduce(_+_)}
    val tot = WHminVSquaredSum.reduce((v1, v2) => (v1+v2).reduce(_+_))
    //println(WHminVSquaredSum.take(10).foreach(println))
    //println(tot)
    val finTot = tot.reduce((e1, e2) => (e1+e2))
    val err = finTot.sum
    return err
  }
  private def multiply(X: RowMatrix, d: BDM[Double]): RowMatrix = {
    return X.multiply(fromBreeze(d))
  }

 /** get the dense matrix representation for a RowMatrix */
  private def getDenseMatrix(X: RowMatrix): BDM[Double] = {
   null
  }

  /** matrix multiplication of W.t and V */
  def computeWTV(W: RowMatrix, V: RowMatrix): BDM[Double] = {
    val rows = W.rows.zip(V.rows).map(f => DenseVector(f._1.toArray)
    * DenseVector(f._2.toArray).t).reduce((x,y)=>x+y)
//    val WT = transposeRowMatrix(W)
//    val WTV = WT.multiply(V)
//    return WTV
    return rows
  }

  def transposeRowMatrix(m: RowMatrix): RowMatrix = {
    val transposedRowsRDD = m.rows.zipWithIndex.map{case (row, rowIndex) => rowToTransposedTriplet(row, rowIndex)}
      .flatMap(x => x) // now we have triplets (newRowIndex, (newColIndex, value))
      .groupByKey
      .sortByKey().map(_._2) // sort rows and remove row indexes
      .map(buildRow) // restore order of elements in each row and remove column indexes
    new RowMatrix(transposedRowsRDD)
  }


  def rowToTransposedTriplet(row: Vector, rowIndex: Long): Array[(Long, (Long, Double))] = {
    val indexedRow = row.toArray.zipWithIndex
    indexedRow.map{case (value, colIndex) => (colIndex.toLong, (rowIndex, value))}
  }

  def buildRow(rowWithIndexes: Iterable[(Long, Double)]): Vector = {
    val resArr = new Array[Double](rowWithIndexes.size)
    rowWithIndexes.foreach{case (index, value) =>
      resArr(index.toInt) = value
    }
    Vectors.dense(resArr)
  }

  /** dot product of two RowMatrixes */
  def dotProd(X: RowMatrix, Y: RowMatrix): RowMatrix = {
    val rows = X.rows.zip(Y.rows).map{case (v1: Vector, v2: Vector) =>
      toBreezeVector(v1) :* toBreezeVector(v2)
    }.map(fromBreeze)
    new RowMatrix(rows)
  }

//  def dotProd(X: BDM[Double], Y: BDM[Double]): BDM[Double] = {
//    val rows = X.rows.zip(Y.rows).map{case (v1: Vector, v2: Vector) =>
//      toBreezeVector(v1) :* toBreezeVector(v2)
//    }.map(fromBreeze)
//    new RowMatrix(rows)
//  }

  /** dot division of two RowMatrixes */
  def dotDiv(X: RowMatrix, Y: RowMatrix): RowMatrix = {
    val rows = X.rows.zip(Y.rows).map{case (v1: Vector, v2: Vector) =>
      toBreezeVector(v1) :/ toBreezeVector(v2).mapValues(_ + 2.0e-15)
    }.map(fromBreeze)
    new RowMatrix(rows)
  }


}