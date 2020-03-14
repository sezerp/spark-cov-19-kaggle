package com.pawelzabczynski.test

import com.pawelzabczynski.config.ConfigModule
import com.pawelzabczynski.spark.SparkModule
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


trait BaseTest
  extends AnyFlatSpec
    with Matchers
    with ConfigModule
    with SparkModule
