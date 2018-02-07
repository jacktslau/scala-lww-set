Scala Last-Writer-Wins (LWW) Element Set
----------------------------------------

## CRDT
A conflict-free replicated data type (CRDT) is a type of data structure
which can provide strong eventual consistency.

This code is a Scala CRDT Implementation for LWW-Element-Set.


## LWW-element-Set
LWW-Element-Set consists of an "add set" and a "remove set", with a timestamp for each element.
Elements are added to an LWW-Element-Set by inserting the element into the add set, with a timestamp.
Elements are removed from the LWW-Element-Set by being added to the remove set, again with a timestamp.
An element is a member of the LWW-Element-Set if it is in the add set, and either not in the remove set,
or in the remove set but with an earlier timestamp than the latest timestamp in the add set.

Merging two replicas of the LWW-Element-Set consists of taking the union of the add sets and
the union of the remove sets. When timestamps are equal, the "bias" of the LWW-Element-Set comes
into play. A LWW-Element-Set can be biased towards adds or removals.

The advantage of LWW-Element-Set allows an element to be reinserted after having been removed.

## Implementation
By using two immutable Set as "add set" and "remove set",
all methods ensures its immutability that can use in distributed environment.

## Build
1. Install JDK 8
2. Install [SBT](https://www.scala-sbt.org/index.html)
3. Run the following command to compile code

```
> sbt compile
```


## Run Test
```
> sbt test
```


## TODO
A todo list to enhance this project
* implements LWWSet to template trait SetLike that can provides common Set functionality

## Reference
* [Conflict-free replicated data type](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)
* [CRDT notes by pfrazee](https://github.com/pfrazee/crdt_notes)