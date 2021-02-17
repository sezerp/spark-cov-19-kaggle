package com.pawelzabczynski.config

case class DataSourceConfig(baseDirectory: String, dateFormat: DateFormat, timestampFormat: TimestampFormat)

case class DateFormat(value: String) extends AnyVal

case class TimestampFormat(value: String) extends AnyVal
