package crdt.test

import crdt._
import org.scalatest._

class LWWSetSpec extends FlatSpec with Matchers {
  "LWWSet" should "computes lookup with added elements only" in {
    val lwwSet = LWWSet[String]()
    val result = lwwSet.add(Element("a", 1), Element("b", 2), Element("c", 3)).lookup
    result.isEmpty should be(false)
    result.size should equal(3)
    result should contain theSameElementsAs (Seq("a", "b", "c"))
  }

  it should "computes lookup with removed elements only" in {
    val lwwSet = LWWSet[String]()
    val result = lwwSet.remove(Element("a", 1), Element("b", 2), Element("c", 3)).lookup
    result.isEmpty should be(true)
    result shouldBe empty
  }

  it should "computes lookup correctly by adding and then removing elements" in {
    val lwwSet = LWWSet[String]()
    val result = lwwSet
      .add(Element("a", 1), Element("b", 2), Element("c", 3))
      .remove(Element("a", 2))
      .lookup

    result.size should equal(2)
    result should contain theSameElementsAs (Seq("b", "c"))
  }

  it should "computes lookup correctly by removing and then adding elements" in {
    val lwwSet = LWWSet[String]()
    val result = lwwSet
      .add(Element("a", 1), Element("b", 2), Element("c", 3))
      .remove(Element("a", 2), Element("c", 2))
      .lookup

    result.size should equal(2)
    result should contain theSameElementsAs (Seq("b", "c"))
  }

  it should "computes lookup correctly by adding, removing and then adding back elements" in {
    val lwwSet = LWWSet[String]()
    val result = lwwSet
      .add(Element("a", 1), Element("b", 2), Element("c", 3))
      .remove(Element("a", 2), Element("c", 2))
      .add(Element("a", 3), Element("d", 5))
      .lookup

    result.size should equal(4)
    result should contain theSameElementsAs (Seq("a", "b", "c", "d"))
  }


  it should "union another LWW-Set correctly" in {
    val lwwSet1 = LWWSet[String]()
      .add(Element("a", 1), Element("b", 1), Element("c", 1))
      .remove(Element("a", 2))

    val lwwSet2 = LWWSet[String]()
      .add(Element("b", 2), Element("d", 3))
      .remove(Element("a", 1), Element("c", 4))

    val result = (lwwSet1 union lwwSet2).lookup
    result.size should equal(2)
    result should contain theSameElementsAs (Seq("b", "d"))
  }

  it should "diff another LWW-Set correctly" in {
    val lwwSet1 = LWWSet[String]()
      .add(Element("a", 1), Element("b", 1), Element("c", 1))
      .remove(Element("a", 2), Element("b", 2))

    val lwwSet2 = LWWSet[String]()
      .add(Element("a", 1), Element("b", 2), Element("d", 3))
      .remove(Element("a", 2), Element("b", 2), Element("c", 4))

    val result = (lwwSet1 diff lwwSet2).lookup
    result.size should equal(2)
    result should contain theSameElementsAs (Seq("b", "c"))

  }

  it should "equals to each other with different insert order" in {
    val lwwSet1 = LWWSet[String]()
      .add(Element("a", 1), Element("b", 2), Element("c", 3))
      .remove(Element("a", 2))
      .add(Element("d", 3))

    val lwwSet2 = LWWSet[String]()
      .remove(Element("a", 2))
      .add(Element("a", 1), Element("b", 2))
      .add(Element("d", 3), Element("c", 3))

    (lwwSet1 == lwwSet2) should be(true)
  }

  it should "computes lookup by discarding elements with old timestamp" in {
    val lwwSet = LWWSet[String]()
    val result = lwwSet
      .add(Element("a", 1), Element("b", 2), Element("c", 3))
      .remove(Element("a", 2), Element("c", 2))
      .add(Element("a", 3), Element("c", 4))
      .lookupElements

    result.size should equal(3)
    System.out.println(result)
    result should contain theSameElementsAs (Seq(Element("a", 3), Element("b", 2), Element("c", 4)))
  }
}

