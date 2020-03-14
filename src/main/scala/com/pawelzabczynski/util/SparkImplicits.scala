package com.pawelzabczynski.util

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.types.{StructField, StructType}

import scala.reflect.runtime.universe.TypeTag

object SparkImplicits {

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
}
