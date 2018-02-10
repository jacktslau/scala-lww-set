FROM java:openjdk-8-jdk

COPY dist /svc

EXPOSE 9000

ENV secret secret-key

CMD /svc/bin/start -Dplay.http.secret.key=$secret
