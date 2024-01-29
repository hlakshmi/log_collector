# Log Collector Service
REST API Server for reading logs

# How to Install

## Prerequisites
* Install Maven
  * https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
* JAVA 8 is required    
    
## Installing the Server
* Checkout the source code
* cd log_collector
* mvn package
* java -cp target/log_collector-1.0-SNAPSHOT.jar com.log_collector.java.LogCollectorServer
    * This would start a HTTP Server on port 8000

## Sending API requests
* Send HTTP Requests to the server using any client (CURL/Postman)
* Request URI: curl -X POST http://localhost:8000
* Sample Request Body
  {
    "filename": "/var/log/agent.log",
    "num_lines": 10,
    "keywords": [
        "XCompleted"
    ]
  }