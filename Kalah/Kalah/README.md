# Kalah Game with Spring Boot Backend

This is a Spring Boot powered Kalah game backend

## Installation & Running

Use the package manager [maven](https://maven.apache.org/) to install and run Kalah. You'll also need Java 8 as JVM.

First build the project and run tests with the command :

```bash
mvn clean install
```

and then

```bash
mvn spring-boot:run
```

to run the backend

## Usage

**1) Create a game (POST REQUEST)**

Use Postman or another tool such as curl to make a POST request

curl --header "Content-Type: application/json" \
--request POST \
http://<host>:<port>/games

and you will get a response containing the newly generated game id and the games uri

```javascript
{"id":0,"uri":"http://192.168.0.36:9090/games"}
```

**2) Make a move (PUT REQUEST)**

curl --header "Content-Type: application/json" \
--request PUT \
http://< host >:< port >/games/{gameId}/pits/{pitId}


## Customising parameters
Feel free to change your **running port**, **game pit count** or **stone count** using the **application.properties** file
