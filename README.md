
About 
======
Spring Boot provides a variety of features. The Spring Cloud project (which is basically a meta (or super) framework built on the top of Spring Boot) proposes even more tools and convenient components. 
Spring Cloud facilitates the development of applications by providing solutions to many of the common problems faced when moving to a distributed environment, as a result there is no need to write a boilerplate code.

The goal of this project is to create a minimal cloud, which can be served as a toy cloud to test and develop optimized and hugely scalable applications. 

This project leverages the powerful NoSQL database (MongoDB) and Spring Boot's state-of-the-art WebFlux framework. I investigated the latest features of Spring Boot, especially those Reactor-based and the latest versions of Spring Cloud modules.

Note: Hoxton is not compatible with Spring Boot 2.4, that is why Spring Cloud Version was set to 2020.0.1; for Spring Boot < 2.4 one can use Hoxton.SR10

The project itself is a kind of classical one and implements a scalable Instagram-like web service allowing users to upload/see images

Overview
=========
All of the code is organized into six separate services, three of these services are run in docker containers

# Configuration service

name: config-server, 
port: 8888

## Description
Implemented on the basis of Spring Boot this service holds configuration data for all other microservices (externalized configuration pattern)
See the details here: https://spring.io/guides/gs/centralized-configuration/

In this example I am using the native source for config data, but in production it's much more convenient to hold all data in git. 
For production-grade variant it is recommended to use git 


The Git-backed configuration API provided by our server can be queried using the following paths:

```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

In which the {label} placeholder refers to a Git branch, {application} to the client's application name and the {profile} to the client's current active application profile - this is how ConfigServer knows which set of settings to return

## Testing

If git used, one can retrieve the configuration for our planned config client running under `development` profile in the branch `master` via:

```
curl http://user:user@localhost:8888/eureka/development/master
```
If local file system is used the command should be as follows:

```
curl http://user:user@localhost:8888/eureka/default
```

The server should return the response (all what we have in configuration/eureka.yml):

```
{
  "name": "eureka",
  "profiles": [
    "default"
  ],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "classpath:configuration/eureka.yml",
      "source": {
        "server.port": 8761,
        "eureka.instance.hostname": "localhost",
        "eureka.client.registerWithEureka": false,
        "eureka.client.fetchRegistry": false,
        "eureka.client.serviceUrl.defaultZone": "http://${eureka.instance.hostname}:${server.port}/eureka/"
      }
    }
  ]
}
```

Note 1:  the client application name needs to be the same as the properties name in the repository. For example, if config client application name is config-client, then properties file in repository should be config-client-dev.properties. Overwise one can get the Could not resolve placeholder ${xxx} error.

Note 2: With Spring Cloud Vault 3.0 and Spring Boot 2.4, the bootstrap context initialization (bootstrap.yml, bootstrap.properties) of property sources was deprecated. So the further development is possible in tow ways:

1. Use Spring Boot 2.4.0 Config Data API to import configuration from Vault (Preferred)
2. Enable Legacy Processing: Enable the bootstrap context either by setting the configuration property spring.cloud.bootstrap.enabled=true or by including the dependency spring-cloud-starter-bootstrap from Spring Cloud package. See the details at https://stackoverflow.com/questions/64994034/bootstrap-yml-configuration-not-processed-anymore-with-spring-cloud-2020-0

# Eureka
name: eureka, 
port: 8761

## Description
Used as a defaul Registry service in cloud network

## Starting

Execute the following command from the root directory

```
java -jar ./eureka/target/cloud-eureka-1.0.0-SNAPSHOT.jar
```

## Testing
Navigate browser to localhost:8761, after authorization one can see the information screen with details about registered instances (docs/eureka_1.png, docs/eureka_1.png).
The default user is user/user

One can use eureka API to query statuses, f.e. the following request

```
curl http://user:user@localhost:8761/eureka/apps
```

will result in response

```
<applications>
  <versions__delta>1</versions__delta>
  <apps__hashcode></apps__hashcode>
</applications>
```
In the case of some applications running in the background the response could be as follow:

```
<applications>
  <versions__delta>1</versions__delta>
  <apps__hashcode>UP_1_</apps__hashcode>
  <application>
    <name>IMAGE-SERVICE</name>
    <instance>
      <instanceId>host.docker.internal:image-service:8050</instanceId>
      <hostName>host.docker.internal</hostName>
      <app>IMAGE-SERVICE</app>
      <ipAddr>172.16.0.2</ipAddr>
      <status>UP</status>
      <overriddenstatus>UNKNOWN</overriddenstatus>
      <port enabled="true">8050</port>
      <securePort enabled="false">443</securePort>
      <countryId>1</countryId>
      <dataCenterInfo class="com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo">
        <name>MyOwn</name>
      </dataCenterInfo>
      <leaseInfo>
        <renewalIntervalInSecs>1</renewalIntervalInSecs>
        <durationInSecs>2</durationInSecs>
        <registrationTimestamp>1614671064650</registrationTimestamp>
        <lastRenewalTimestamp>1614671071330</lastRenewalTimestamp>
        <evictionTimestamp>0</evictionTimestamp>
        <serviceUpTimestamp>1614671064651</serviceUpTimestamp>
      </leaseInfo>
      <metadata>
        <management.port>8050</management.port>
      </metadata>
      <homePageUrl>http://host.docker.internal:8050/</homePageUrl>
      <statusPageUrl>http://host.docker.internal:8050/actuator/info</statusPageUrl>
      <healthCheckUrl>http://host.docker.internal:8050/actuator/health</healthCheckUrl>
      <vipAddress>image-service</vipAddress>
      <secureVipAddress>image-service</secureVipAddress>
      <isCoordinatingDiscoveryServer>false</isCoordinatingDiscoveryServer>
      <lastUpdatedTimestamp>1614671064651</lastUpdatedTimestamp>
      <lastDirtyTimestamp>1614671063832</lastDirtyTimestamp>
      <actionType>ADDED</actionType>
    </instance>
  </application>
</applications>
```
 
See the details on https://github.com/Netflix/eureka/wiki/Eureka-REST-operations

Some usefull information can be found also at https://blog.asarkar.com/technical/netflix-eureka/


# Image service
name: image-service, 
port 8050

## Description
A simple resource service used to serve the content (images and comments). A fail-fast service, which never blocks and uses under the hood the circuit breaker pattern

https://www.baeldung.com/spring-cloud-circuit-breaker


## Testing


The request like so: 

```
curl http://user:user@localhost:8050/images
```

will return the following response:

```
[
  {
    "id": "603e25b7f7a5d2d6b6803e65",
    "name": "apple.jpg",
    "owner": "user"
  },
  {
    "id": "603e25b7f7a5d2d6b6803e66",
    "name": "orange.jpg",
    "owner": "user"
  }
]
```

# Other (Managed) services

(managed) MongoDB - database
(managed) RabbitMQ - message broker


Additional information about how to specify the externalized configuration for the cloud:

https://cloud.spring.io/spring-cloud-config/multi/multi__spring_cloud_config_server.html

https://zgadzaj.com/development/docker/docker-compose/containers/rabbitmq

https://stackoverflow.com/questions/42200317/how-to-configure-rabbitmq-connection-with-spring-rabbit/42206155


Building
========

The building is quite straitforward, use the following command to build all modules:

```
mvn clean package
```


Running
========

First, start all managed services using command:

```
docker-compose -f docker-compose-min.yml up --build
```
This command will start MongoDB, RabbitMQ and config service


In the end of work one can shut down and delete containers:

```
docker-compose -f docker-compose-min.yml down
```



Requirements
=============

* JDK 8+
* Spring Boot 2.4.3 (which in turn requires Java Developer Kit (JDK) 8 or higher)
* Lombok library
* Docker (used to instantiate RabbitMQ 3.6 (https://www.rabbitmq.com/) and MongoDB 4.4 (https://www.mongodb.com/)

