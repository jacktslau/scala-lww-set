package crdt.example

import example.Hello
import org.scalatest._

class LWWSetSpec extends FlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    Hello.greeting shouldEqual "hello"
  }
}

