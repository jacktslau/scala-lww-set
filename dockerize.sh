sbt dist

mkdir -p dist
unzip -d dist crdt-server/target/universal/*-SNAPSHOT.zip
mv dist/*/* dist/
rm dist/bin/*.bat
rm -r dist/*-SNAPSHOT
mv dist/bin/* dist/bin/start

docker build -t scala-lww-set-server .