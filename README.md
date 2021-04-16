# Fun-Stack

Opinionated example of how to build a scalable web platform full-stack in scala.

The following sections introduce the scala projects for this platform.
Web-Client communicates via rest api with Web-Api.
Web-Api pushes events on a message queue.
The persistency-handler reads from the message queue and does storage transactions.

## Build

Build code:
```sh
sbt webClient/fullOptJS::webpack lambdaApi/fullOptJS
```

## Deploy

Run deployment:
```sh
terraform init
terraform apply
```

### Api

Shared api contract between server and client

### Web-Client

Web frontend using OutWatch.

### Web-Api

Serving the Rest API for clients.

### Persistency-Events

The event data for the Persistency Handler. Contact betwen Web-Api and Persistency-Handler.

### Persistency-Handler

Handles events issued by the server.
