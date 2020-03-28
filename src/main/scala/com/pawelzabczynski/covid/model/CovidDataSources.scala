package com.pawelzabczynski.covid.model

import java.sql.Date

case class CovidDataSources(
    id: java.lang.Integer,
    case_in_country: java.lang.Integer,
    reporting_date: Date,
    summary: String,
    location: String,
    country: String,
    gender: String,
    age: java.lang.Integer,
    symptom_onset: Date,
    If_onset_approximated: java.lang.Integer,
    hosp_visit_date: Date,
    exposure_start: Date,
    exposure_end: Date,
    visiting_Wuhan: java.lang.Boolean,
    from_Wuhan: java.lang.Boolean,
    death: Date,
    recovered: Date,
    symptom: String,
    source: String,
    link: String
  ) extends Model[CovidDataSources] {
  override def name: String = "CovidDataSources"
}

object CovidDataSources {

//  def apply(row): CovidDataSources = CovidDataSources()

}
