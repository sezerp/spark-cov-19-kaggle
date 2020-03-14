package com.pawelzabczynski.spark

import com.pawelzabczynski.test.BaseTest

class TestTest extends BaseTest {

  "it" should "run" in {
    logConfig()
    import sparkSession.implicits._

    val ds = Seq("a", "b").toDS()

    ds.show()
  }

}
