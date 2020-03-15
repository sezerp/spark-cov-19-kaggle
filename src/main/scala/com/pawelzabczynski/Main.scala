package com.pawelzabczynski

import com.pawelzabczynski.covid.{CovidModule, DataRepository}
import com.pawelzabczynski.covid.model.CovidObservation
import org.apache.spark.sql.Dataset

object Main extends MainModule {

  def main(args: Array[String]): Unit = {
    logConfig()
    import sparkSession.implicits._

    val ds: Dataset[CovidObservation] = dataRepository.load[CovidObservation](DataRepository.COVID_OBSERVATION_DS)

    ds.show()
    ds.printSchema()
  }
}
