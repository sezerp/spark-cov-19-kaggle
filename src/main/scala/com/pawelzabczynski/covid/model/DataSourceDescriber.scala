package com.pawelzabczynski.covid.model

import org.apache.spark.sql.types.StructType

import scala.reflect.runtime.universe.TypeTag

case class DataSourceDescriber[T <: Model[T]](fileName: String, namespace: String, tableName: String, dateFormat: Option[String] = None, timestampFormat: Option[String] = None) {
  def sourceFile(dataDir: String): String = s"$dataDir/$fileName"
  def schema()(implicit typeTag: TypeTag[T]): StructType = Model.schema[T]
  def sqlSchema()(implicit typeTag: TypeTag[T]): StructType = Model.sqlSchema[T]
}
