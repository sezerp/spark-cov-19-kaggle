package com.pawelzabczynski.covid.core

import java.sql.{Date, Timestamp}

import org.apache.spark.sql.types.{BooleanType, DataType, DateType, DoubleType, IntegerType, LongType, StringType, StructField, TimestampType}
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import com.pawelzabczynski.util.SparkImplicits.toSchema
import com.pawelzabczynski.util.SparkImplicits._
import java.text.SimpleDateFormat

import com.pawelzabczynski.covid.core.DataCleaner.CleanerImplicits.{BooleanTypeToDefaultValue, DateTypeToDefaultValue, DoubleTypeToDefaultValue, IntegerTypeToDefaultValue, LongTypeToDefaultValue, StringTypeToDefaultValue, TimestampTypeToDefaultValue, TypeToDefaultValue}
import com.typesafe.scalalogging.StrictLogging

import scala.reflect.runtime.universe.TypeTag

object DataCleaner extends StrictLogging {

  private case class ReplacementMapping(colName: String, dataType: DataType)

  implicit class CleanerImplicits(val df: DataFrame) {

    def cleanNA[A <: Product]()(implicit tag: TypeTag[A]): Dataset[A] = {
      import com.pawelzabczynski.covid.core.DataCleaner.CleanerImplicits._
      val schema = toSchema[A]
      val toCleanDF: DataFrame = df.fitToModel(schema)
      val colNameToDefaultValue: List[ReplacementMapping] = schema.map(dt => ReplacementMapping(dt.name, dt.dataType)).toList
      val resultDf = colNameToDefaultValue.foldLeft(toCleanDF) {
        case (acc, rm) if rm.dataType == StringType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, StringTypeToDefaultValue)
        case (acc, rm) if rm.dataType == LongType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, LongTypeToDefaultValue)
        case (acc, rm) if rm.dataType == IntegerType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, IntegerTypeToDefaultValue)
        case (acc, rm) if rm.dataType == DoubleType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, DoubleTypeToDefaultValue)
        case (acc, rm) if rm.dataType == BooleanType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, BooleanTypeToDefaultValue)
        case (acc, rm) if rm.dataType == TimestampType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, TimestampTypeToDefaultValue)
        case (acc, rm) if rm.dataType == DateType => acc.fillNull(rm.dataType, rm.colName).na.replace(rm.colName, DateTypeToDefaultValue)
        case (acc, _) => acc
      }

      resultDf.fitToModel(schema).casToNumberTypes[A].fitToModel[A]
   }

    def fillNull(dataType: DataType, columnName: String): DataFrame =
      TypeToDefaultValue.get(dataType).map(defaultVal => df.na.fill(defaultVal, Seq(columnName))).getOrElse(df)
  }

  object CleanerImplicits {
    private val EmptyValues: Set[String] = Set("", "NA", "N/A", " ", "null", null)
    private val DateFormat = new SimpleDateFormat("mm/dd/yyyy")
    private val TimestampFormat = new SimpleDateFormat("mm/dd/yyyy HH:mm")
    private val TypeToDefaultValue: Map[DataType, String] = Map(
      StringType -> "EMPTY",
      DoubleType -> "0.0",
      LongType -> "0",
      BooleanType -> "0",
      IntegerType -> "0",
      DateType -> DateFormat.format(new Date(0L)).toString,
      TimestampType -> TimestampFormat.format(new Timestamp(0L)).toString
    )

    private val StringTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> "EMPTY").toMap
    private val DoubleTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> TypeToDefaultValue(LongType)).toMap
    private val LongTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> TypeToDefaultValue(LongType)).toMap
    private val BooleanTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> TypeToDefaultValue(BooleanType)).toMap
    private val IntegerTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> TypeToDefaultValue(IntegerType)).toMap
    private val TimestampTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> TypeToDefaultValue(TimestampType)).toMap
    private val DateTypeToDefaultValue: Map[String, String] = EmptyValues.map(_ -> TypeToDefaultValue(DateType)).toMap

  }

}
