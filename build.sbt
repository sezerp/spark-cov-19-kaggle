name := "spark-cov-19-kaggle"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "2.2.0"
val spark = Seq("spark-core", "spark-sql", "spark-hive")
  .map( "org.apache.spark" %% _ % sparkVersion)
  .map(_.excludeAll(
    ExclusionRule("log4j"),
    ExclusionRule("slf4j-log4j12")
  ))

val configDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.12.2"
)

val baseDependencies = Seq(
  "io.monix" %% "monix" % "3.1.0",
  "com.softwaremill.common" %% "tagging" % "2.2.1",
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.12"
)

val loggingDependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "de.siegmar" % "logback-gelf" % "2.2.0",
  "commons-logging" % "commons-logging" % "1.2",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25"
)

val scalatest = "org.scalatest" %% "scalatest" % "3.1.1" % Test
val unitTestingStack = Seq(scalatest)

val embeddedPostgres = "com.opentable.components" % "otj-pg-embedded" % "0.13.3" % Test
val dbTestingStack = Seq(embeddedPostgres)

libraryDependencies ++= spark ++ configDependencies ++ baseDependencies ++ loggingDependencies ++ unitTestingStack ++ dbTestingStack
