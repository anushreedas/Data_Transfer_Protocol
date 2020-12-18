# TOTO 2
Name: Anushree Sitaram Das

Implementation of reliable data transfer protocol.

## To create a network in Docker
Only needs to be done once.

`docker network create --subnet=100.18.0.0/16 net`


## To Run Server
This will ultimately run the java Server class as an application.
### To build
Run the below command in Server directory of the project
It build any java files in the current directory in the container.

`docker build -t server . `

### To Run 
This will ultimately run the java Server class as an application.
`docker run -it --net net --ip 100.18.0.22 server`

## To Run Client
This will ultimately run the java Client class as an application.
### To build
Run the below command in Client directory of the project
It build any java files in the current directory in the container.

`docker build -t client .`

### To Run 
This will ultimately run the java Client class as an application.
`docker run -it --net net --ip 100.18.0.23 client 100.18.0.22 image.jpeg`
