1. Start a redis server instance using docker

* Navigate to application root path: `/benefits-calculation-microservice-parent`
* Using terminal run:
`docker-compose up -d`

2. Running the application

* **IDE - IntelliJ Idea**

Menu -> Run -> Edit Configurations...

Click in "+" button and Add New Configuration -> "Application" type

Set "Name": run

Set "Main Class": org.example.BenefitsCalculationMicroservice

Set "Use class module": benefits-calculation-ioc

Use JRE 17

Click on "Apply" to save changes

Select "run" configuration and click on "run" button

How to run tests

* **IDE - IntelliJ Idea**
Menu -> Run -> Edit Configurations...

Click in "+" button and Add New Configuration -> "JUnit" type

Set "Name": run all tests

Set "Test Kind": All in package

Click on "Apply" to save changes

Select "run all tests" configuration and click on "run" button