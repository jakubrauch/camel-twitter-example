# camel-twitter-example
Example of Camel Twitter component used with Apache CXF web service inside Docker.

## Overview
The project consists of three maven modules:
 - camel-ws - SOAP web service that delegates processing to twitter and filesystem modules via rest
 - camel-twitter - Twitter connector, needs to be configured before use (twitter-template.properties file, see following section)
 - camel-filesystem - Simple message receiver, stores inbound rest requests to local filesystem

## Prerequisites
In order to run the application you need to have Docker installed (tested on Mac with Docker application, on Windows with Docker Toolbox you need to run docker commands from within the toolbox window to make them work).

## Configuration
Before running the project you need to obtain Twitter tokens, copy twitter-template.properties file to twitter.properties and fill in the appropriate keys in the target file, i.e.:
```
twitter.consumerKey=
twitter.consumerSecret=
twitter.accessToken=
twitter.accessTokenSecret=
```

Once this is set up you are ready to start a set of Docker containers.

## Startup
Go to camel-compose project and start the containers using the following command:
```bash
docker-compose up
```
It will start four containers:
 * camel-filesystem-2 - one of filesystem containers, stores incoming data to target/fs-2
 * camel-filesystem-1 - one of filesystem containers, stores incoming data to target/fs-1
 * camel-twitter - obtains data from twitter based on incoming rest query and returns it to the caller
 * camel-ws - when called, invokes camel-twitter to get data and pass it to camel-filesystem-1 and camel-filesystem-2 sequentially

For more details on containers configuration see camel-compose/docker-compose.yml file.

## Usage
When containers are up and running you can post soap requests to the following address:
http://localhost:8880/ws

An example of SOAP request accepted by this service is as follows:
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
<soap:Body>
  <getTweets><query>test</query></getTweets>
</soap:Body>
</soap:Envelope>
```

A successful invocation results with empty response (envelope with empty body) and code 200.
