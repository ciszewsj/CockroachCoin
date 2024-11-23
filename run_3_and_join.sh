#!/bin/bash

curl --location --request POST 'localhost:8080/api/v1/nodes/join_network'
sleep 0.2
curl --location --request POST 'localhost:8081/api/v1/nodes/join_network'
sleep 0.2
curl --location --request POST 'localhost:8082/api/v1/nodes/join_network'

sleep 3
# let one node start mining
curl --location --request POST 'localhost:8080/api/v1/powerOnOff'