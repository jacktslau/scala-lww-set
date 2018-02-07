package crdt

case class Element[E](value: E, ts: Long = System.currentTimeMillis())
