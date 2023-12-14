# Twitter to Kafka Microservice

This is a Java-based microservice that streams data from Twitter and feeds it into Kafka.

## Project Structure

The project is structured as a Maven project with two main modules:

- `app-config-data`: This module contains the configuration data for the application.
- `twitter-to-kafka`: This is the main module that streams data from Twitter and feeds it into Kafka.

## Getting Started

To get started with this project, you need to have Java and Maven installed on your machine.

### Prerequisites

- Java 8 or higher
- Maven

### Building the Project

You can build the project using the Maven wrapper script included in the project:

``./mvnw clean install``

Running the Project
After building the project, you can run it using the following command:

`./mvnw spring-boot:run`

Contributing
Contributions are welcome. Please feel free to submit a pull request or open an issue.

License
This project is licensed under the Apache License 2.0. See the LICENSE file for details.

