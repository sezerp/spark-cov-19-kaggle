package com.pawelzabczynski.covid

import com.pawelzabczynski.config.DataSourceConfig
import com.pawelzabczynski.covid.model.{CovidDataSources, CovidObservation, DataSourceDescriber, Model}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import com.pawelzabczynski.covid.DataRepository.CSV

import scala.reflect.runtime.universe.TypeTag

class DataRepository(spark: SparkSession, dataSourceConf: DataSourceConfig) {

  def load[T <: Model[T]](data: DataSourceDescriber[T])(implicit typeTag: TypeTag[T]): Dataset[T] = {
    import spark.implicits._

    spark.read
      .format(CSV)
      .option("header", "true")
      .option("dateFormat", dataSourceConf.dateFormat.value)
      .option("timestampFormat", dataSourceConf.timestampFormat.value)
      .schema(data.schema)
      .load(data.sourceFile(dataSourceConf.baseDirectory))
      .as[T]
  }

  def loadAndFit[T <: Model[T]](data: DataSourceDescriber[T])(implicit typeTag: TypeTag[T]): Dataset[T] = {
    import com.pawelzabczynski.util.SparkImplicits.DataFrameImplicits

    val ds: DataFrame = spark.read
      .format(CSV)
      .option("header", "true")
      .option("inferSchema", "true")
      .option("dateFormat", dataSourceConf.dateFormat.value)
      .option("timestampFormat", dataSourceConf.timestampFormat.value)
      .load(data.sourceFile(dataSourceConf.baseDirectory))

    ds.fitToModel[T]
  }

  def loadMalformedData[T <: Model[T]](data: DataSourceDescriber[T])(implicit typeTag: TypeTag[T]): Dataset[Row] =
    spark.read
      .format(CSV)
      .option("header", "true")
      .option("dateFormat", dataSourceConf.dateFormat.value)
      .option("timestampFormat", dataSourceConf.timestampFormat.value)
      .load(data.sourceFile(dataSourceConf.baseDirectory))

}

object DataRepository {
  val CSV       = "csv"
  val NAMESPACE = "COVID"

  val COVID_OBSERVATION_DS: DataSourceDescriber[CovidObservation] =
    DataSourceDescriber[CovidObservation]("covid_19_data.csv", NAMESPACE, "COVID_OBSERVATION")
  val COVID_DATA_SOURCES_DS: DataSourceDescriber[CovidDataSources] =
    DataSourceDescriber[CovidDataSources]("COVID19_line_list_data.csv", NAMESPACE, "COVID_DATA_SOURCES")

}
