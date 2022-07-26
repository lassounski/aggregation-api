# Aggregation API
TNT back-end software developer assessment.

### Pre requisites
#### JDK 17
`brew install openjdk@17`

When executing `java -version` should see **openjdk version "17.0.3"**

#### Docker
`brew install docker`

### Before running
#### Pull the 3rd party API's service
`docker pull xyzassessment/backend-services`
#### Start the services
`docker run -p 8080:8080 xyzassessment/backend-services`
### Starting the application
`mvn spring-boot:run`

The application will be ready on port 8081

## Request sample
`curl http://localhost:8081/aggregation?countryNames=NL,FR&trackingNumbers=109347263,109347261,109347265,109347254,109147261&shippingNumbers=109347263,109347261,109347265,109347254,109147261`

## Unit + IT
The IT test will start the application on port 8081 and the docker container `xyzassessment/backend-services` will start on port 8080. 
Keep those ports clear before running this profile.

`mvn verify -PintegrationTest`
## Performance test
The application was tested with 100 TPS and stayed stable during a 1-hour load test.

`mvn verify -PperformanceTest`

## Design decisions
This application is a mix between the Reactive and Blocking world.

The controller and the invocation of the 3rd party API's (WebClient) are reactive to prevent the Tomcat server from blocking.

The layer that takes care of the queuing of requests for the 2nd and 3rd user stories is thread based.
An amount of 100 threads per queue is used to wait for the 3rd party API to respond with the results.
Running performance tests with around 100 TPS showed me in the VisualVM that the thread count remained stable.
CPU usage was below 4% and memory consumption limited to max of 700 mb.

I tried using reactive for that part by using bufferTimeout and asynchronous sinks but couldn't get it working in time.
I added some validation on the request attributes to check for the length of the input parameters.

Also didn't have enough time to properly unit test the whole business logic, something that would be a must for a PR.