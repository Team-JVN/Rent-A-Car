# Rent-A-Car

This microservice web application offers its users rent-a-car management on-line. They can advertise cars for rent and create rent requests. 

## KT 1

Click on [this link](https://github.com/Team-JVN/Rent-A-Car/blob/master/XWS_KT1_Tim29.pdf) to view microservice achitecture model.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

To get started, clone the project to your local directory:
```
$ git clone https://github.com/Team-JVN/Rent-A-Car.git
```

### Prerequisites

You need to install Docker and Docker Compose on your machine.

*On Windows*:
* Get Docker for Windows from [this link](https://www.docker.com/get-started). Docker Compose is included.

*On Linux*:
* To install Docker, follow the steps from [this link](https://docs.sevenbridges.com/docs/install-docker-on-linux).
* To install Docker Compose, follow the steps from [this link](https://www.digitalocean.com/community/tutorials/how-to-install-docker-compose-on-ubuntu-18-04).

### Starting

To run the entire architecture, open a terminal from the directory where `docker-compose.yml` file is placed and type the following command:
```
$ docker-compose up --build
```
It is going to take some time before everything is up and running.

## Using

### Agent Application

#### Accessing as a client
1. Open your browser and type `https://localhost:8090`
2. Go to the Registration page an create an account
3. Activate your account by clicking on the link in the received e-mail
4. Log in and start exploring

#### Accessing as an agent
1. Open [this link](https://maildrop.cc/inbox/rentacar) and copy the password from the received e-mail
2. Go to the Change password page and fill in required data. E-mail address to use is `rentacar@maildrop.cc`
3. Log in and start exploring

## Built With

* [Angular 8](https://angular.io) - Frontend framework
* [Spring Boot](https://spring.io/) - Backend framework
* [Maven](https://maven.apache.org/) - Dependency management

## Authors

* [Violeta Marić, RA 4/2016](https://github.com/violetamaric)
* [Jelena Popov, RA 29/2016](https://github.com/JelenaPopov)
* [Nikola Brodić, RA 55/2016](https://github.com/NikolaBrodic)
