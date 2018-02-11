# Push Docker Images to AWS ECR

source `dirname $0`/.docker.config

eval "$(aws ecr get-login --no-include-email --region ap-southeast-1)"
docker push $AWS_ECR_PATH/$DOCKER_IMAGE:latest
