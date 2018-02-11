source `dirname $0`/.docker.config

eb init --profile personal

eb setenv REDIS_HOST=$REDIS_HOST
eb setenv REDIS_PORT=$REDIS_PORT
