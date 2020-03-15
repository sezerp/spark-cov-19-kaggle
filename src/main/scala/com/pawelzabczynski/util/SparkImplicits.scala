package com.pawelzabczynski.util

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.{StructField, StructType}

import scala.reflect.runtime.universe.TypeTag

object SparkImplicits extends StrictLogging {

  def generateSchema[T <: Product]()(implicit typeTag: TypeTag[T]): StructType = {
    ScalaReflection.schemaFor[T].dataType.asInstanceOf[StructType]
  }

  implicit class SchemaImplicits(val schema: StructType) {
    def ===(that: StructType): Boolean = {
      val thatGr = that.toSet.size > schema.size
      if (thatGr) false
      else that
        .map(_.name.toUpperCase)
        .toSet
        .diff(schema.map(_.name.toUpperCase).toSet)
        .isEmpty
    }

    def !==(that: StructType): Boolean =  ! ===(that)

    def diffByName(that: StructType): List[StructField] = {
      val thatNames = that.map(_.name.toUpperCase).toSet
      schema
        .filter(sf => !thatNames.contains(sf.name.toUpperCase))
        .toList
    }
  }

  implicit class DataFrameImplicits(val df: DataFrame) {
    def fitToModel[T <: Product]()(implicit typeTag: TypeTag[T]): Dataset[T] = {
      import df.sparkSession.implicits._
      logger.info(s"Incoming schema: ${df.schema.map(_.name)}")
      val newSchema = generateSchema[T]
      val toDropColumns: Seq[String] = df.schema.diffByName(newSchema).map(_.name.toUpperCase)
      logger.warn(s"Dropping: ${toDropColumns} columns")
      val afterDropDF = df.drop(toDropColumns: _*)
      val afterDropSchema = afterDropDF.schema.map(_.name.toUpperCase).toSet
      val toAddColumns = StructType(newSchema.diffByName(afterDropDF.schema))
      logger.warn(s"Adding: ${toAddColumns.map(_.name)} columns")
      val resultDF = toAddColumns.foldLeft(afterDropDF) { (acc, ft) =>
        if (afterDropSchema.contains(ft.name.toUpperCase)) acc
        else acc.withColumn(ft.name, lit(null).cast(ft.dataType))
      }
      logger.info(s"Result schema: ${resultDF.schema.map(_.name)}")

      resultDF.as[T]
    }
  }

}
