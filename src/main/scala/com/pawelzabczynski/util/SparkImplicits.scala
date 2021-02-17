package com.pawelzabczynski.util

import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.{DecimalType, IntegerType, LongType, StructField, StructType}

import scala.reflect.runtime.universe.TypeTag

object SparkImplicits extends StrictLogging {

  def toSchema[SCHEMA <: Product]()(implicit typeTag: TypeTag[SCHEMA]): StructType = {
    ScalaReflection.schemaFor[SCHEMA].dataType.asInstanceOf[StructType]
  }

  implicit class SchemaImplicits(val schema: StructType) {
    def ===(that: StructType): Boolean = {
      val schemasSizeNotEquals = that.toSet.size > schema.size
      if (schemasSizeNotEquals) false
      else
        that
          .map(_.name.toUpperCase)
          .toSet
          .diff(schema.map(_.name.toUpperCase).toSet)
          .isEmpty
    }

    def !==(that: StructType): Boolean = ! ===(that)

    def diffByName(that: StructType): List[StructField] = {
      val thatNames = that.map(_.name.toUpperCase).toSet
      schema
        .filter(sf => !thatNames.contains(sf.name.toUpperCase))
        .toList
    }
  }

  implicit class DataFrameImplicits(val df: DataFrame) {

    def fitToModel(newSchema: StructType): DataFrame = {
      logger.info(s"Incoming schema: ${df.schema.map(_.name)}")
      val toDropColumns: Seq[String] = df.schema.diffByName(newSchema).map(_.name.toUpperCase)
      logger.warn(s"Dropping: ${toDropColumns} columns")
      val afterDropDF     = df.drop(toDropColumns: _*)
      val afterDropSchema = afterDropDF.schema.map(_.name.toUpperCase).toSet
      val toAddColumns    = StructType(newSchema.diffByName(afterDropDF.schema))
      logger.warn(s"Adding: ${toAddColumns.map(_.name)} columns")
      val resultDF = toAddColumns.foldLeft(afterDropDF) { (acc, ft) =>
        if (afterDropSchema.contains(ft.name.toUpperCase)) acc
        else acc.withColumn(ft.name, lit(null).cast(ft.dataType))
      }
      logger.info(s"Result schema: ${resultDF.schema.map(_.name)}")

      val columns = resultDF.schema.map(_.name)
      resultDF.select(columns.head, columns.tail: _*)
    }

    def fitToModel[SCHEMA <: Product]()(implicit typeTag: TypeTag[SCHEMA]): Dataset[SCHEMA] = {
      import df.sparkSession.implicits._
      val newSchema = toSchema[SCHEMA]
      val resultDF  = df.fitToModel(newSchema)

      resultDF.as[SCHEMA]
    }

    def casToNumberTypes[SCHEMA <: Product](implicit typeTag: TypeTag[SCHEMA]): Dataset[Row] = {
      val schema = toSchema[SCHEMA]
      schema.foldLeft(df) {
        case (acc, st) if st.dataType == LongType    => acc.withColumn(st.name, col(st.name).cast(DecimalType(38, 0)).cast(LongType))
        case (acc, st) if st.dataType == IntegerType => acc.withColumn(st.name, col(st.name).cast(DecimalType(38, 0)).cast(IntegerType))
        case (acc, _)                                => acc
      }
    }
  }
}
