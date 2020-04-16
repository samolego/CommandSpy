# Container image that runs your code
FROM ubuntu

# Copies your code file from your action repository to the filesystem path `/` of the container
COPY gradlew /gradlew

# Code file to execute when the docker container starts up (`gradlew`)
ENTRYPOINT ["gradlew build"]