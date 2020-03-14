package com.pawelzabczynski.config

import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

trait ConfigModule extends StrictLogging {

  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]

  def logConfig(): Unit = {
    val baseInfo =
      s"""
       |Spark: ${config.spark}
       |DB: ${config.db}
       |DataConfig: ${config.data}
       |""".stripMargin

    logger.info(baseInfo)
  }

}
