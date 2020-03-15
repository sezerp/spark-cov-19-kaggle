package com.pawelzabczynski

import com.pawelzabczynski.covid.{CovidModule, DataRepository}
import com.pawelzabczynski.covid.model.CovidObservation
import org.apache.spark.sql.Dataset

object Main extends MainModule {

  def main(args: Array[String]): Unit = {
    logConfig()
    import sparkSession.implicits._
    import com.pawelzabczynski.util.SparkImplicits.DataFrameImplicits

    val ds: Dataset[CovidObservation] = dataRepository.load[CovidObservation](DataRepository.COVID_OBSERVATION_DS)
    val ds2: Dataset[CovidObservation] = dataRepository.loadAndFit[CovidObservation](DataRepository.COVID_OBSERVATION_DS)
    ds.show()
    ds.printSchema()

    ds2.show()
    ds2.printSchema()
  }
}
