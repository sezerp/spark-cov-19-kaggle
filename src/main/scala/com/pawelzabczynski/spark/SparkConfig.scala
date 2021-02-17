package com.pawelzabczynski.spark

case class SparkConfig(
    name: String,
    master: String,
    warehouseLocation: String,
    sparkDriverMemory: String,
    sparkExecutorMemory: String
)
