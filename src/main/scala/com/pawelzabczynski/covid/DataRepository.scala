package com.pawelzabczynski.covid

import com.pawelzabczynski.config.DataSourceConfig
import com.pawelzabczynski.covid.model.{CovidObservation, Data, Model}
import org.apache.spark.sql.{Dataset, SparkSession}

import scala.reflect.runtime.universe.TypeTag

class DataRepository(spark: SparkSession, dataSourceConf: DataSourceConfig) {

  def load[T <: Model[T]](data: Data[T])(implicit typeTag: TypeTag[T]): Dataset[T] = {
    import spark.implicits._

    spark
      .read
      .format(DataRepository.CSV)
      .option("header", "true")
      .option("inferSchema", "true")
      .schema(data.schema)
      .load(data.sourceFile(dataSourceConf.baseDirectory))
      .as[T]
  }

}


object DataRepository {
  import com.pawelzabczynski.util.SparkImplicits.generateSchema

  val CSV = "csv"
  val NAMESPACE = "COVID"

  val COVID_OBSERVATION_DS: Data[CovidObservation] = Data[CovidObservation]("covid_19_data.csv", NAMESPACE, "")

}