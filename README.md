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

## API

### Add Elements
`POST /:key`

```bash
curl -X POST \
  http://localhost:9000/key \
  -H 'Content-Type: application/json' \
  -d '[{ "value": "test", "ts": 123457789 }]'
```

### Remove Elements
`DELETE /:key`

```bash
curl -X DELETE \
  http://localhost:9000/key \
  -H 'Content-Type: application/json' \
  -d '[{ "value": "test", "ts": 123457789 }]'
```

### Lookup
`GET /:key`

```bash
curl -X GET http://localhost:9000/key
```

## Prerequisite
Please install the following tools in order to build/develop/deploy this project

* JDK 8
* [SBT](https://www.scala-sbt.org/index.html)
* [Docker](https://docs.docker.com/install/)
* [AWS EB Client](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3-install.html)

## Building
To compile this project
```bash
> sbt compile
```

## Run Test
Run core & server unit test
```bash
> sbt test
```

## Running and Developing
To run this project locally, you have to setup a Redis server. Use the following command to start Redis server locally.
```bash
> docker-compose up -d
```

Starting Local Server in SBT. Server can be accessible on `http://localhost:9000`
```bash
> sbt run
```


## Packaging
Dockerize the application into a Docker image and push the image into AWS ECR
```
> sh bin/dockerize.sh
```


## Running Server as Docker Image
After dockerized, you can run the docker image locally by the following command:
```
> docker run -d -p 80:80 -e REDIS_HOST='localhost' -e REDIS_PORT='6379' lww-set-server:latest
```

## AWS Setup
To deploy this project to AWS. You need to setup AWS account by following steps

1. Signup AWS Account
2. Create an IAM User with following permissions and save the access key and secret into `~/.aws/credentials`
  * AWSElasticBeanstalkFullAccess
  * AmazonECS_FullAccess
2. Create ElasticCache with Redis Instance
3. Create Elastic Container Service (ECS)
  * checked only `Store container images securely with Amazon ECR`
  * follow the `Push Commands` instructions on the screen in order to push docker images into AWS ECR
4. Update AWS ECR variables in `bin/.docker.config`
5. Run `eb init --profile [aws]` to initialize ElasticBeanstalk Environment
6. Create ElasticBeanstalk Environment with Docker platform, ensure the aws-elasticbeanstalk-role contains read access of ECR
7. Copy Redis host and port settings from ElasticCache to ElasticBeanstalk `Environment Properties` with property name: `REDIS_HOST` and `REDIS_PORT`
8. Add following lines in `.elasticbeanstalk/config.yml` to let `eb deploy` script deploy docker config instead of whole profile files
> ```
> deploy:
>  artifact: bin/Dockerrun.aws.json
> ```
9. Run `bin/deploy.sh` to deploy the latest docker image into ElasticBeanstalk.


## Chaos Monkeys
3 Chaos Monkeys implemented in the gatling folder

### Normal Scenario
This Monkey adds and removes items from server at random intervals.
```
> sbt "project gatling" "gatling:testOnly crdt.test.NormalChaosMonkeys"
```

### Offline Online Sync
This Monkey mimics a user who adds and removes set offline, and then goes online to sync with server.
```
> sbt "project gatling" "gatling:testOnly crdt.test.OfflineOnelineSync"
```

### Client Viewer
This Monkey views a set by merging server's set into its own local copy
```
> sbt "project gatling" "gatling:testOnly crdt.test.ClientViewer"
```

## TODO
A todo list to enhance this project
* implements LWWSet to template trait SetLike that can provides common Set functionality
* add pagination to Lookup API
* Use proper mocking library to write the spec (like Mockito, ScalaMock)
* Add Sync API (sync both add set and remove set locally, lookup API not enough to do it)
* Redis Connection Problems found during Load Test, need further lookup
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