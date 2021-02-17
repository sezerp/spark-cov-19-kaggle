package com.pawelzabczynski.config

import com.pawelzabczynski.infrastructure.DbConfig
import com.pawelzabczynski.spark.SparkConfig

case class Config(db: DbConfig, spark: SparkConfig, data: DataSourceConfig)
