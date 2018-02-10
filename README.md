Scala Last-Writer-Wins (LWW) Element Set REST API Server
--------------------------------------------------------

## CRDT
A conflict-free replicated data type (CRDT) is a type of data structure
which can provide strong eventual consistency.

This code is a Scala CRDT Implementation for LWW-Element-Set with REST API Server.


## LWW-Element-Set
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

### LWW-Element-Set
By using two immutable Set as "add set" and "remove set",
all methods ensures its immutability that can use in distributed environment.

### Redis Datastore
By using redis sorted set datatype,

### API Server
The server is implemented by Play Framework. API is operated by HTTP verb.
Keys and value

## API

### Add Element
`POST /:key`

```bash
curl -X POST \
  http://localhost:9000/key \
  -H 'Content-Type: application/json' \
  -d '{ "value": "test", "ts": 123457789 }'
```

### Remove Element
`DELETE /:key`

```bash
curl -X DELETE \
  http://localhost:9000/key \
  -H 'Content-Type: application/json' \
  -d '{ "value": "test", "ts": 123457789 }'
```

### Lookup
`GET /:key`

```bash
curl -X GET http://localhost:9000/key
```



## Building
1. Install JDK 8
2. Install [SBT](https://www.scala-sbt.org/index.html)
3. Install [Docker](https://docs.docker.com/install/)
4. Run the following command to compile code

```
> sbt compile
```

## Run Test
```
> sbt test
```

## Developing
To develop this project, you need to setup docker to startup a redis server

```
> docker-compose up -d
```


## Packaging
There is a dockerize.sh to automatically to build a docker image that can run in anywhere
```
> sh dockerize.sh
```


## Running
After packaging, you can run the docker image by the following command:
```
> docker run -d -p 80:9000 scala-lww-set-server
```

## Deploy
This project allows you to deploy to AWS
```

```

## TODO
A todo list to enhance this project
* implements LWWSet to template trait SetLike that can provides common Set functionality
* add pagination to Lookup API
* Use proper mocking library to write the spec (like Mockito, ScalaMock)
* CI/CD Script
* Integration Test
* Resolve library conflict in PlayFramework
* Use Terrform to create AWS infrastructure automatically

## Reference
* [Conflict-free replicated data type](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)
* [CRDT notes by pfrazee](https://github.com/pfrazee/crdt_notes)

## How to ensure server to support 1 million concurrent request
1. Load testing the application server to find out how many concurrent users can handle in one single server
2. Load testing Redis how large in one sorted set can support
3. Implement Redis Data Sharding if necessary
4. Setup testing environment with Auto Scaling Group, Metrics and Monitoring enabled
5. Load Testing to simulate large amount users
6. Constantly analyze test results and do optimization on servers/code until reaching 1 million users