package com.pawelzabczynski.covid.model

import org.apache.spark.sql.types.{StructField, StructType}
import com.pawelzabczynski.util.SparkImplicits.generateSchema

import scala.reflect.runtime.universe.TypeTag

trait Model[T <: Product] extends Product {
  def name: String
  protected val prohibitedSequenceMapping: Map[String, String] = Model.prohibitedSequencesMapping
}

object Model {

  private val prohibitedSequencesMapping: Map[String, String] = Map(
    "\\$div" -> "/",
    "\\$u0020" -> " "
  )

  private val unicodeToSqlCharacters: Map[String, String] = Map(
    "\\$div" -> "_",
    "\\$u0020" -> "_"
  )

  private def cleanSchema(schema: StructType): StructType = {
    val newSchema: Array[StructField] = schema.map { fs =>
      val newName = prohibitedSequencesMapping.keys.mkString("|").r
        .replaceAllIn(fs.name, m => prohibitedSequencesMapping(s"\\$m"))
      StructField(newName, fs.dataType, fs.nullable, fs.metadata)
    }.toArray
    new StructType(newSchema)
  }

  private def toSqlSchema(schema: StructType): StructType = {
    val newSchema: Array[StructField] = schema.map { fs =>
      val newName = unicodeToSqlCharacters.keys.mkString("|").r
        .replaceAllIn(fs.name, m => unicodeToSqlCharacters(s"\\$m"))
      StructField(newName, fs.dataType, fs.nullable, fs.metadata)
    }.toArray
    new StructType(newSchema)
  }

  def schema[T <: Model[T]]()(implicit typeTag: TypeTag[T]): StructType = {
    val sch: StructType = generateSchema[T]
    cleanSchema(sch)
  }

  def sqlSchema[T <: Model[T]]()(implicit typeTag: TypeTag[T]): StructType = {
    val sch: StructType = generateSchema[T]
    toSqlSchema(sch)
  }
}
