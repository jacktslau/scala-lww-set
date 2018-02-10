AWS_ECR_PATH=856424103372.dkr.ecr.ap-southeast-1.amazonaws.com
DOCKER_IMAGE=lww-set-server

sbt dist

mkdir -p dist
unzip -d dist crdt-server/target/universal/*-SNAPSHOT.zip
mv dist/*/* dist/
rm dist/bin/*.bat
rm -r dist/*-SNAPSHOT
mv dist/bin/* dist/bin/start

docker build -t $DOCKER_IMAGE .
docker tag lww-set-server:latest $AWS_ECR_PATH/$DOCKER_IMAGE:latest
docker push $AWS_ECR_PATH/$DOCKER_IMAGE:latest