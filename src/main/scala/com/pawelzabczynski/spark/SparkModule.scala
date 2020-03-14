package com.pawelzabczynski.spark

import com.pawelzabczynski.util.BaseModule
import org.apache.spark.sql.SparkSession

trait SparkModule extends BaseModule {

  lazy val sparkSession: SparkSession = SparkSessionBuilder.sparkSession(sparkConfig)

  def sparkConfig: SparkConfig = config.spark
}
