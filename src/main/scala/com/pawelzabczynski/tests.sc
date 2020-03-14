val prohibitedSequenceMapping: Map[String, String] = Map(
  "\\$div" -> "/",
  "\\$u0020" -> "_",
  "\\$u0009" -> "_")

prohibitedSequenceMapping.keys.mkString("|").r
prohibitedSequenceMapping.keys.mkString("|").r.findAllIn("ab$u0009cd").length
prohibitedSequenceMapping.keys.mkString("|").r.findAllIn("ab$u0009cd").foreach(println)
prohibitedSequenceMapping.keys.mkString("|").r.replaceAllIn("ab$u0009cd$u0020op", m => {
  println(s"### m.groupNames ${m.groupNames}")
  println(s"### m ${m.toString}")
  prohibitedSequenceMapping(s"\\$m")
})