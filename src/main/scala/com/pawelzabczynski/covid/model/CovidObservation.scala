package com.pawelzabczynski.covid.model

import java.sql.{Date, Timestamp}


case class CovidObservation(SNo: Int,
                            OBSERVATION_DATE: Date,
                            PROVINCE_STATE: String,
                            COUNTRY_REGION: String,
                            LAST_UPDATE: Timestamp,
                            CONFIRMED: Int,
                            DEATHS: Int,
                            RECOVERED: Int)
    extends Model[CovidObservation] {
  override def name: String = "CovidObservation"
}

object CovidObservation {

  val SNO = "SNO"
  val OBSERVATION_DATE = "OBSERVATION_DATE"
  val PROVINCE_STATE = "PROVINCE_STATE"
  val COUNTRY_REGION = "COUNTRY_REGION"
  val LAST_UPDATE = "LAST_UPDATE"
  val CONFIRMED = "CONFIRMED"
  val DEATHS = "DEATHS"
  val RECOVERED = "RECOVERD"

}
