package com.pawelzabczynski.spark

import org.apache.spark.sql.SparkSession


object SparkSessionBuilder {

  /**
   * Create Spark Session based on configuration
   * The SparkContext is part of SparkSession
   * */
  def sparkSession(config: SparkConfig): SparkSession = {
    SparkSession
      .builder()
      .master(config.master)
      .config("spark.sql.warehouse.dir", config.warehouseLocation)
      .appName(config.name)
      .enableHiveSupport()
      .getOrCreate()
  }
}
