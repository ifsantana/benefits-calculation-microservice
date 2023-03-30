This is the architecture decision description template published
in ["Architecture Decisions: Demystifying Architecture" by Jeff Tyree and Art Akerman, Capital One Financial](https://www.utdallas.edu/~chung/SA/zz-Impreso-architecture_decisions-tyree-05.pdf)
.

* **Issue**:

We’d like you to develop a “round-up” feature for Starling customers using our public developer API
that is available to all customers and partners.
For a customer, take all the transactions in a given week and round them up to the nearest pound.
For example with spending of £4.35, £5.20 and £0.87, the round-up would be £1.58. This amount should
then be transferred into a savings goal, helping the customer save for future adventures.

API calls
To make this work, the key parts from our public API you will need are:

1. **Accounts** - To retrieve accounts for the customer
2. **Transaction feed** - To retrieve transactions for the customer
3. **Savings Goals** - Create a savings goals and transfer money to savings goals

* **Decision**:

Given the challenge I decided to design the solution using as reference the **_Hexagonal (Ports and
Adapters) Architecture_**.

Here are some PROS on using this kinda of architectural/design approach:

* **Testability**

* The main benefit this architecture provides is the ability of testing the application in
  isolation from external devices it depends on. This is achieved by doing two things:
  For each driver port, develop a test adapter that will run test cases against the port.
  For each driven port, develop a mock adapter.

    * Testing the hexagon in isolation can be useful for:
        * Running regression tests. When source code changes for whatever reason (a new feature is
          added, a bug is fixed, …), these tests are run to ensure that those changes don’t have
          side effects
          on any already existing functionality. To run these tests, the driver adapter uses an
          automated
          test framework.
        * Doing BDD (Behaviour Driven Development). For each driver port functionality, a set of
          acceptance criteria is defined by the user. The functionality will be considered “done”
          when all the
          acceptance criteria are met. These acceptance criteria are called scenarios, which will be
          the test
          cases run by the test adapter. For running these acceptance tests the adapter can use
          tools like
          Cucumber.
        * Nat Pryce (co-author of the book Growing Object-Oriented Software, Guided by Tests)
          defines in his article Visualising Test Terminology different kinds of tests related to
          hexagonal
          architecture:
          Unit Tests: For testing single objects inside the hexagon.
        * Integration Tests: For testing adapters. They ensure that translation between ports and
          the outside world is done by the adapters correctly.
        * Acceptance Tests: For testing driver ports, i.e. the hexagon in isolation. They check that
          application behaves as the user expects, meeting the acceptance criteria he/she previously
          defined for the use cases.
        * System Tests: For testing the whole system, adapters and the hexagon together. They also
          test system deployment and startup.

* **Maintainability**

* Maintainable systems are those who are easy to modify. Hexagonal Architecture increases
  maintainability, because it provides separation of concerns and business logic decoupling,
  which makes it easier to locate the code we want to modify. Application maintainability is a
  long term concept related to technical debt. The more maintainability the less technical debt.
  So, Hexagonal Architecture reduces the technical debt.
  **Flexibility**
* Swapping between different technologies is easy. For a given port, you can have multiple
  adapters, each one using a specific technology. For choosing one of them, you just have to
  configure which adapter to use for that port. This configuration can be as easy as modifying
  an external configuration properties file. No source code modified, no re-compiling, no
  re-building.
  Likewise, adding a new specific technology adapter to a port can be done without touching the
  existing source code. The adapter is developed and compiled on his own. At runtime, it will be
  detected and plugged into the port.
  **Immunity to Technology Evolution**
* Technology evolves more frequently than business logic does. In applications where the
  business logic is tied to technology, you can’t do technology changes without touching
  business logic. This is no good, because business should not change.
  With hexagonal architecture, the technology you want to upgrade is located at an adapter
  outside the application. You just have to change the adapter. The application itself remains
  immutable because it doesn’t depend on adapters.
  **Delay Technical Decisions**
* When you start developing and coding, you can focus just on business logic, deferring
  decisions about which framework and technology you are going to use. You can choose a
  technology later, and code an adapter for it.

* References
    * [1] [Alistair Cockburn first article about Hexagonal architecture](https://archive.ph/5j2NI#selection-41.0-183.17)
    * [2] [Alistair Cockburn Article - Ports And Adapters Architecture](http://wiki.c2.com/?PortsAndAdaptersArchitecture)
    * [3] [Juan Manuel Garrido de Paz Article - Ports and Adapters Pattern (Hexagonal Architecture)](https://jmgarridopaz.github.io/content/hexagonalarchitecture.html#tc6-2-3)
    * [4] [Netflix - Ready for changes with Hexagonal Architecture](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749)


* **Status**: Work in Progress 

* **Group**:

    * Application Layers:
        * Adapters - Output
            * Accounts Api Client
                * Module Responsible to abstract the integration with Starling Accounts Service.
            * Saving Goals Api Client
                * Module Responsible to abstract the integration with Starling Saving Goals Service.
            * Transaction Feed Api Client
                * Module Responsible to abstract the integration with Starling Transaction Feed
                  Service.
            * Redis Cache Client
                * Module Responsible to provide a repository abstracting the integration with Redis
                  Database.
        * Common - Common Artifacts and IoC
            * Cross Cutting
                * All common artifacts used by the application.
            * Cross Cutting IoC
                * Layer responsible for inversion of control (handle the dependency injection).
        * Core - Domain and Business Logic
            * Application
                * Module Responsible for to isolate the applications use cases and to abstract the
                  port layer interaction.
        * Ports - Input
            * Api
                * Module Responsible to abstract http entry points/endpoints for this application.
            * Event Consumers
                * Module Responsible to abstract events consumers for this application.

* **Assumptions**:


* **Constraints**: 

* **Positions**: 

* **Argument**:

* **Implications**: 

* **Related decisions**:

* **Related requirements**:

* **Related artifacts**: 

* **Related principles**: 

* **Notes**:

