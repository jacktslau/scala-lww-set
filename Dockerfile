FROM java:openjdk-8-jdk

COPY dist /svc

ENV PLAY_SECRET secret-key

ENV HTTP_PORT 9000

ENV REDIS_HOST localhost

ENV REDIS_PORT 6379

EXPOSE $HTTP_PORT $HTTPS_PORT

CMD /svc/bin/start -Dhttp.port=$HTTP_PORT -Dplay.http.secret.key=$PLAY_SECRET -Dredis.host=$REDIS_HOST -Dredis.port=$REDIS_PORT
