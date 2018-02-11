# Build Docker Image

source `dirname $0`/.docker.config

if [ -d "dist" ]; then rm -r dist; fi

sbt dist

mkdir -p dist
unzip -d dist crdt-server/target/universal/*-SNAPSHOT.zip
mv dist/*/* dist/
rm dist/bin/*.bat
rm -r dist/*-SNAPSHOT
mv dist/bin/* dist/bin/start

docker build -t $DOCKER_IMAGE .
docker tag lww-set-server:latest $AWS_ECR_PATH/$DOCKER_IMAGE:latest
