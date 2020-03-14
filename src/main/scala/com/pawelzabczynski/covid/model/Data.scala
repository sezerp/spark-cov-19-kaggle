package com.pawelzabczynski.covid.model

import org.apache.spark.sql.types.StructType

import scala.reflect.runtime.universe.TypeTag

case class Data[T <: Model[T]](fileName: String, namespace: String, tableName: String) {
  def sourceFile(dataDir: String): String = s"$dataDir/$fileName"
  def schema()(implicit typeTag: TypeTag[T]): StructType = Model.schema[T]
  def sqlSchema()(implicit typeTag: TypeTag[T]): StructType = Model.sqlSchema[T]
}
