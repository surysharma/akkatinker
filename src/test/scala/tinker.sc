val rng = (1 to 10)

val keys = List("a", "b", "c", "d", "e", "f")

val keyValTuple = keys zip rng

val (keyList, valueList) = keyValTuple unzip