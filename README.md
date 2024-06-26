# technical-coding-challenge

## the used technologies
    java 11
    spring-boot
    swagger
    docker
    postgres

# Run the application
    ./execute.sh

# DockerFile
    mvn clean backage
    docker build -f "Dockerfile" .
    docker images   #(to show the created image ID)
    docker run <ImageID>

# API Requests
add a device

    curl --location 'http://localhost:8080/api/devices' \
    --header 'Content-Type: application/json' \
    --data '{
    "name": "iphone 13 pro Max",
    "brand": "apple"
    }'

get device by ID

    curl --location 'http://localhost:8080/api/devices/1'

update device PUT

    curl --location --request PUT 'http://localhost:8080/api/devices/1' \
    --header 'Content-Type: application/json' \
    --data '{
    "name": "iphone12",
    "brand": "apple"
    }'

update device PATCH

    curl --location --request PATCH 'http://localhost:8080/api/devices/1' \
    --header 'Content-Type: application/json' \
    --data '{
    "name": "iphone13 proMax"
    }'

search by brand

    curl --location 'http://localhost:8080/api/devices/search?brand=samsung'