package com.pawelzabczynski

import java.sql.Date
import java.text.SimpleDateFormat

import com.pawelzabczynski.covid.DataRepository
import com.pawelzabczynski.covid.model.{CovidDataSources, CovidObservation}
import org.apache.spark.sql.types.{DateType, IntegerType}
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.functions._
import com.pawelzabczynski.covid.core.DataCleaner._
import com.pawelzabczynski.util.SparkImplicits._

object Main extends MainModule {

  def main(args: Array[String]): Unit = {
    logConfig()
    import sparkSession.implicits._

    val ds: Dataset[CovidObservation]  = dataRepository.load[CovidObservation](DataRepository.COVID_OBSERVATION_DS)
    val ds2: Dataset[CovidObservation] = dataRepository.loadAndFit[CovidObservation](DataRepository.COVID_OBSERVATION_DS)
    val ds3 = dataRepository
      .loadMalformedData[CovidDataSources](DataRepository.COVID_DATA_SOURCES_DS)
      .cleanNA[CovidDataSources]
    val ds4: DataFrame = dataRepository
      .loadMalformedData[CovidDataSources](DataRepository.COVID_DATA_SOURCES_DS)
    ds.show()
    ds.printSchema()

    ds2.show()
    ds2.printSchema()

    ds3.show()
    ds3.printSchema()

    val r1 = ds4.na.fill("0", Seq("case_in_country"))
    r1.show()
    val r2 = r1.na.fill("EMPTY", Seq("symptom"))
    r2.show()
    ds3.filter(r => r.age == 0).show()
  }
}
