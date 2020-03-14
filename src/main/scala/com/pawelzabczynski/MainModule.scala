package com.pawelzabczynski

import com.pawelzabczynski.config.{ConfigModule, DataSourceConfig}
import com.pawelzabczynski.covid.CovidModule
import com.pawelzabczynski.spark.SparkModule

trait MainModule
  extends ConfigModule
    with SparkModule
    with CovidModule {

  override def dataSourceConf: DataSourceConfig = config.data
}