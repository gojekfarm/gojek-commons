# GoJek Commons [![Build Status](https://travis-ci.org/gojek-engineering/gojek-commons.svg?branch=master)](https://travis-ci.org/gojek-engineering/gojek-commons)
GoJek Commons is a collection of useful abstractions aimed at making micro-service development faster and easier to manage.
## Introduction
From our experience, we have seen that most micro-service development has similar system requirements and they all end up with,
* a data store
* a centralized cache store
* a queuing system
* event processing
* scheduled job processing
* application/business metrics for monitoring & alerting
* input validations and error handling
* health checks

We end up writing boiler plate code in every service making the development slower and bulkier codebase. `GoJek Commons` was created to abstract out most of these dependencies and keep the service smaller and simpler.

## Components
GoJek Commons is built on top of [Dropwizard Framework](http://www.dropwizard.io/) for its out-of-the-box support for service hygiene (configuration, metrics, logging etc..). The following are the components used,

* RESTful API - Jaxrs ([Jersey])
* Data Access - [ActiveJPA](https://github.com/ActiveJpa/activejpa) (Relational Databases)
* Dependency Injection - [Guice](https://github.com/google/guice)
* Message Broker - AMQP ([Rabbitmq](https://www.rabbitmq.com/))
* Event Streaming - Kafka
* Caching - Redis ([Jedis](https://github.com/xetorthio/jedis))
* Metrics - [Dropwizard Metrics](http://metrics.dropwizard.io/)
* Jobs - [Quartz](http://www.quartz-scheduler.org/)
* API Documentation - [Swagger](https://swagger.io/)

## Getting Started 

### Setting Up Maven

```xml
   <dependencies>
     <dependency>
       <groupId>com.gojek</groupId>
       <artifactId>gojek-commons-application</artifactId>
       <version>2.1.1</version>
     </dependency>
   </dependencies>
   
   <repositories>
     <repository>
       <id>jcenter</id>
       <url>https://jcenter.bintray.com</url>
       <snapshots>
         <enabled>false</enabled>
       </snapshots>
     </repository>
   </repositories>
```
### Base Application
[gojek-commons-application](/gojek-commons-application) allows you to create a basic application with out-of-the-box support for,
* Exception mapper for `WebApplicationException` that transforms the exception into a json response
* Localization of error messsages
* Useful filters like `RequestTrackingFilter`, `CorsFilter` etc.
* Swagger integration and many more

### Jpa Support
[gojek-commons-jpa](/gojek-commons-jpa) exposes a JpaBundle with,
* [ActiveJPA](https://github.com/ActiveJpa/activejpa) support
* Database Health check 
* Connection pool metrics
* Database migrations using [Flyway](https://flywaydb.org/)

### Amqp Support
[gojek-commons-amqp](/gojek-commons-amqp) exposes a AmqpBundle with,
* Rabbitmq as message broker
* Amqp Health check
* Connection pool metrics
* Support for retrying failed messages before dead lettering

### Kafka Support
[gojek-commons-kafka](/gojek-commons-kafka) exposes a KafkaBundle with,
* Basic support for producing and consuming messages
* Pool of consumers

### Cache Support
[gojek-commons-cache](/gojek-commons-cache) exposes a AmqpBundle with,
* Redis as cache server
* Redis Health check
* Connection pool metrics
* Utiities like `AtomicExecutor` that uses redis locks for atomic operations in a cluster

### Job Support
[gojek-commons-job](/gojek-commons-job) exposes a JobBundle with,
* Quartz scheduler
* Job registration & scheduling using configuration
* Job execution metrics

### Guice Support
[gojek-commons-guice](/gojek-commons-guice) exposes a GuiceBundle & GuiceJpaBundle with,
* Dependency injection support
* Jpa integration

## Support
Create an [issue](https://github.com/gojek-engineering/gojek-commons/issues) explaining your issue as detail as possible.
## License
gojek-commons is offered under Apache License, Version 2.0
