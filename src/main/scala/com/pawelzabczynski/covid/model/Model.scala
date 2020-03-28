package com.pawelzabczynski.covid.model

import org.apache.spark.sql.types.{StructField, StructType}
import com.pawelzabczynski.util.SparkImplicits.toSchema

import scala.reflect.runtime.universe.TypeTag

trait Model[T <: Product] extends Product {
  def name: String
  protected val prohibitedSequenceMapping: Map[String, String] = Model.prohibitedSequencesMapping
}

object Model {

  /**
   * prohibitedSequencesMapping contains characters upper when on case class is used `` operator
   *                            to allow using spaces and other special characters upper in csv files.
   *                            Allow remap it from compiled values into real character appear in csv file as head
   * */
  private val prohibitedSequencesMapping: Map[String, String] = Map(
    "\\$div" -> "/",
    "\\$u0020" -> " "
  )
  /**
   * unicodeToSqlCharacters contains characters upper when on case class is used `` operator
   *                            to allow using spaces and other special characters upper in csv files.
   *                            Allow remap and replace into underscore.
   * */
  private val unicodeToSqlCharacters: Set[String] = Set(
    "\\$div",
    "\\$u0020"
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
      val newName = unicodeToSqlCharacters.mkString("|").r
        .replaceAllIn(fs.name, _ => "_")
      StructField(newName, fs.dataType, fs.nullable, fs.metadata)
    }.toArray
    new StructType(newSchema)
  }

  def schema[T <: Model[T]]()(implicit typeTag: TypeTag[T]): StructType = {
    val sch: StructType = toSchema[T]
    cleanSchema(sch)
  }

  def sqlSchema[T <: Model[T]]()(implicit typeTag: TypeTag[T]): StructType = {
    val sch: StructType = toSchema[T]
    toSqlSchema(sch)
  }
}
