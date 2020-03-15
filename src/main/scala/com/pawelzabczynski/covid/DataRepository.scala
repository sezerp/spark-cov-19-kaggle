package com.pawelzabczynski.covid

import com.pawelzabczynski.config.DataSourceConfig
import com.pawelzabczynski.covid.model.{CovidObservation, DataSourceDescriber, Model}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.reflect.runtime.universe.TypeTag

class DataRepository(spark: SparkSession, dataSourceConf: DataSourceConfig) {

  def load[T <: Model[T]](data: DataSourceDescriber[T])(implicit typeTag: TypeTag[T]): Dataset[T] = {
    import spark.implicits._

    spark
      .read
      .format(DataRepository.CSV)
      .option("header", "true")
      .option("dateFormat", dataSourceConf.dateFormat.value)
      .option("timestampFormat", dataSourceConf.timestampFormat.value)
      .schema(data.schema)
      .load(data.sourceFile(dataSourceConf.baseDirectory))
      .as[T]
  }

  def loadAndFit[T <: Model[T]](data: DataSourceDescriber[T])(implicit typeTag: TypeTag[T]): Dataset[T] = {
    import com.pawelzabczynski.util.SparkImplicits.DataFrameImplicits

    val ds: DataFrame = spark
      .read
      .format(DataRepository.CSV)
      .option("header", "true")
      .option("inferSchema", "true")
      .option("dateFormat", dataSourceConf.dateFormat.value)
      .option("timestampFormat", dataSourceConf.timestampFormat.value)
      .load(data.sourceFile(dataSourceConf.baseDirectory))

    ds.fitToModel[T]
  }

}


object DataRepository {
  val CSV = "csv"
  val NAMESPACE = "COVID"

  val COVID_OBSERVATION_DS = DataSourceDescriber[CovidObservation]("covid_19_data.csv", NAMESPACE, "COVID_OBSERVATION")

}