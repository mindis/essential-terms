package org.allenai.ari.solvers.termselector.learners

import edu.illinois.cs.cogcomp.lbj.pos.POSBaselineLearner
import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet

import scala.collection.JavaConverters._

class CountClassifier extends POSBaselineLearner {
  protected override def computePrediction(example: AnyRef): String = {
    val form = extractor.discreteValue(example)
    val countsMap = table.get(form)
    if (countsMap == null) {
      "UNKNOWN"
    } else {
      countsMap.asScala.toList.maxBy(_._2)._1
    }
  }

  /** get probability score for the baselines */
  override def scores(example: AnyRef): ScoreSet = {
    val scoreSet = new ScoreSet()
    val form = extractor.discreteValue(example)
    val countsMap = table.get(form)
    if (countsMap != null) {
      val countsMapScala = countsMap.asScala
      val countSum = if (countsMap.values.size > 1) {
        countsMapScala.values.foldRight(0)(_ + _)
      } else {
        countsMapScala.values.head.toInt
      }
      countsMapScala.foreach {
        case (label, count) =>
          scoreSet.put(label, count.toDouble / countSum)
      }
    } else {
      scoreSet.put("UNKNOWN", 1)
    }
    scoreSet
  }
}
