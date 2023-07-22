# Sophos App - Consuming API Services with MVVC Pattern and Geolocation

![Sophos App Logo](link_to_logo.png)

## Introduction

Sophos App is an innovative mobile application that allows users to access relevant geographical information through API services. The app utilizes MVVC (Model-View-View Model-Controller) architecture patterns to ensure a clear and maintainable structure. Additionally, it integrates geolocation functionalities, enabling users to make POST and GET requests to efficiently obtain and send geographical data.

## Key Features

- Real-time visualization of geographical data.
- Search and filtering of information through POST and GET requests.
- MVVC architecture to facilitate code development and maintenance.

## Technologies Used

- Programming Language: Kotlin (Android).
- Architecture Pattern: MVVC.
- Geolocation Tools: Sophos Geolocation API and other providers.

## MVVC Architecture

The MVVC architecture is a variant of the MVC pattern that promotes a higher separation of concerns and a more organized structure. In Sophos App, the architecture is divided into the following components:

- **Model**: Represents the data and business rules of the application. Here, API requests are handled, and the received responses are managed.

- **View**: The presentation layer that displays information to the user and manages interactions with the interface.

- **View Model**: Acts as an intermediary between the Model and the View. Here, data is processed and prepared for visualization on the interface. It is also responsible for handling business logic related to geolocation.

- **Controller**: In the case of Sophos App, the Controller is responsible for handling POST and GET requests to the API and updating the Model and View accordingly.

## Geolocation and POST/GET Requests

The main functionality of Sophos App revolves around geolocation. Users can make POST requests to send their current location to the server and obtain relevant geographical information. Similarly, GET requests allow users to obtain geographical data based on the parameters provided.

The typical flow of a POST/GET request in Sophos App is as follows:

1. The user interacts with the interface to send a location or configure search parameters.
2. The View Model collects the necessary data and constructs the POST or GET request.
3. The Controller sends the request to the Sophos API or another geolocation provider.
4. The server processes the request and returns the requested information.
5. The Controller updates the Model with the received data.
6. The View Model processes the data and prepares it for visualization.
7. The View displays the results to the user clearly and concisely.

## Conclusions

Sophos App is a mobile application that combines the power of geolocation with MVVC architecture patterns to offer users a smooth and enriching experience when accessing geographical information. Through POST and GET requests, users can efficiently send and receive data, making Sophos App a valuable tool for those who wish to explore and better understand the world around them.
