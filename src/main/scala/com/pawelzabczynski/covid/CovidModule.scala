package com.pawelzabczynski.covid

import com.pawelzabczynski.config.DataSourceConfig
import com.pawelzabczynski.spark.SparkModule
import com.pawelzabczynski.util.BaseModule

trait CovidModule extends BaseModule with SparkModule {
  def dataRepository: DataRepository = new DataRepository(sparkSession,dataSourceConf)
  def dataSourceConf: DataSourceConfig
}
