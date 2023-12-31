# Auth Service
This service is for authentication of customers, admin and restaurant employees. 

## Functionality
- Create user
- User login
- User logout

## Architecture 
This project is build with REST, N-layered architecture and N-tier architecture. We have implemented 3 layers within our microservice: Controller, Service and Repository. 

## Security
We are using JWT Tokens For authenticating customers, admin and restaurant employees. JWT Tokens are stored in the Cookies. Passwords are hashed with Argon2.

## Extra diagrams
Extra diagrams such as sequence diagrams and use cases are located in the repository /docs/diagrams

## Tech Stack
- Spring Boot
- Argon2
- JJWT (JWT)
- Cucumber
- Datafaker 
- PostgreSQL
