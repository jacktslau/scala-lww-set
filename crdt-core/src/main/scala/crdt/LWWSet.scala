package crdt

/**
  * LWW-Set: Last-Write-Wins-Element Set.
  * Uses 'timestamps' associated with addition and deletion operations for picking the winner.
  *
  * An alternative LWW-based approach, which we call LWW-element-Set, attaches a timestamp to each element (rather than to the whole set).
  * Consider add-set A and remove-set R, each containing (element, timestamp) pairs. To add (resp. remove) an element e,
  * add the pair (e, now()), where now was specified earlier, to A (resp. to R). Merging two replicas takes the union of their add-sets and remove-sets.
  * An element e is in the set if it is in A, and it is not in R with a higher timestamp: lookup(e) = ∃ t, ∀ t 0 > t: (e,t) ∈ A ∧ (e,t0) / ∈ R).
  * Since it is based on LWW, this data type is convergent.
  *
  */
case class LWWSet[E](val addSet: Set[Element[E]] = Set.empty[Element[E]],
                     val removeSet: Set[Element[E]] = Set.empty[Element[E]]) {

  /**
    * Tests if this LWW-Set is empty.
    *
    * @return `true` if there is no element in the set, `false` otherwise.
    */
  def isEmpty: Boolean = lookupElements.isEmpty

  /**
    * Tests if some element is contained in this set.
    *
    * @param elem the element to test for membership.
    * @return     `true` if `elem` is contained in this set, `false` otherwise.
    */
  def contains(elem: E) = lookup.contains(elem)

  /**
    * Creates a new LWW-Set with an additional element, unless the element is already present.
    *
    * @param elem an element to add
    * @return     a new LWW-Set that contains all elements of this set and that also contains `elem`.
    */
  def add(elem: Element[E]): LWWSet[E] = {
    this.copy(addSet = addSet + elem)
  }

  /**
    * Creates a new LWW-Set by adding all elements contained in another collection to this LWW-Set, omitting duplicates.
    *
    * @param elems elements to add
    * @return      a new LWW-Set that contains all elements of this set and that also contains `elems`.
    */
  def add(elems: Element[E]*): LWWSet[E] = {
    this.copy(addSet = addSet ++ elems)
  }

  /**
    * Creates a new LWW-Set with a given element removed from this set.
    *
    * @param elem an element to be removed
    * @return     a new LWW-Set that contains all elements of this set but that does not contain `elem`.
    */
  def remove(elem: Element[E]): LWWSet[E] = {
    this.copy(removeSet = removeSet + elem)
  }

  /**
    * Creates a new LWW-Set by removing all elements contained in another collection `elems`.
    *
    * @param elems elements to be removed
    * @return      a new LWW-Set that contains all element of this set but that does not contain `elems`.
    */
  def remove(elems: Element[E]*): LWWSet[E] = {
    this.copy(removeSet = removeSet ++ elems)
  }

  /**
    * Computes the union between of LWW-Set and another LWW-Set.
    *
    * @param other the LWW-Set to form the union with.
    * @return      a new LWW-Set consisting of all elements that are in this
    *              set or in the given set `other`.
    */
  def union(other: LWWSet[E]): LWWSet[E] = {
    this.copy(addSet = addSet ++ other.addSet, removeSet = removeSet ++ other.removeSet)
  }

  /**
    * Computes the difference of this LWW-Set and another LWW-Set.
    *
    * @param other the LWW-Set of elements to exclude.
    * @return      a LWW-Set containing those elements of this
    *              set that are not also contained in the given set `other`.
    */
  def diff(other: LWWSet[E]): LWWSet[E] = {
    this.copy(addSet = addSet.diff(other.addSet), removeSet = removeSet.diff(other.removeSet))
  }

  /**
    * Computes the current state of LWW-Set
    *
    * @return Current State elements of LWW-Set
    */
  def lookupElements: Set[Element[E]] = {
    val flattenSet = this.flatten
    flattenSet.addSet.filter { addElem =>
      !flattenSet.removeSet.exists { removeElem =>
        removeElem.value == addElem.value && removeElem.ts > addElem.ts
      }
    }
  }

  /**
    * Computes the current state of LWW-Set
    *
    * @return Current State values of LWW-Set
    */
  def lookup: Set[E] = {
    lookupElements.map(_.value)
  }


  /**
    * Create a new LWW-Set with old timestamp of the same element removed
    *
    * @return a LWW-Set containing same elements but old timestamps removed
    */
  def flatten: LWWSet[E] = {
    this.copy(addSet = flattenSet(addSet), removeSet = flattenSet(removeSet))
  }


  /**
    * Remove old timestamp of the same element in a Set
    *
    * @param set set to be flatten
    * @return    a new Set containing same elements bu old timestamps removed
    */
  protected def flattenSet(set: Set[Element[E]]): Set[Element[E]] = {
    // group by element value and get the latest element
    val map: Map[E, Set[Element[E]]] = set.groupBy(_.value).mapValues(e => e)

    map.foldLeft(Set.empty[Element[E]]) { case (set, (_, elems)) =>
      val latestElem = elems.maxBy(_.ts)  // get the latest element
      set + latestElem
    }
  }
}

