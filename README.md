# Cloud Communication Scheduling Platform

<!-- TOC -->
* [Cloud Communication Scheduling Platform](#cloud-communication-scheduling-platform)
  * [Overview](#overview)
  * [Features](#features)
    * [Web Portal Screenshots:](#web-portal-screenshots)
      * [Home Page:](#home-page)
      * [History Page:](#history-page)
      * [Schedules Page:](#schedules-page)
      * [Gateways Page:](#gateways-page)
      * [Preferences Page:](#preferences-page)
      * [Monitoring Page:](#monitoring-page)
  * [Example use cases:](#example-use-cases)
    * [Vodafone Example:](#vodafone-example)
    * [Banking Example:](#banking-example)
  * [Requirements:](#requirements)
  * [Repository Structure:](#repository-structure)
  * [Getting Started:](#getting-started)
<!-- TOC -->

## Overview

This project is a communication scheduling platform that allows business to schedule messages to be sent to customers on
a recurring basis.
The platform is built using a microservices architecture and can be deployed on AWS with Kubernetes (EKS).

[Temporal.io](https://temporal.io/) is used to orchestrate the communication scheduling process using scheduled
workflows.

* Each user has a schedule that specifies the message type and when they receive a message
* Businesses create "communication gateways" that receive a user ID from a scheduled workflow and generate the message
  to be sent to the user.
* This allows for personalised messages to be generated as the gateway can interact with businesses internal user
  databases and usage data.

## Features

* Terragrunt and Helm charts for easy deployment and configuration
* Scalable solution leveraging Kubernetes and EKS
* Uses [Temporal](https://temporal.io) (a fault-tolerant and scalable orchestration engine) to manage the scheduling of
  messages and ensures messages are always delivered.
    * Retry policies can be configured to send messages again if they fail
* Provided [gateway library](gateway-library) makes it easy to develop your own gateways
    * Can choose any type of delivery method, email, SMS, push notification etc
    * Integrate with your own internal systems to generate personalised messages
    * See the reference implementations of gateways in the [gateway library](gateway-library/README.md#getting-started)
* Secure platform leveraging SSL between microservices, JWT tokens for API authentication and encryption of Temporal
  payloads
* API endpoints to enable automation of creating, updating and deleting schedules and gateways
    * Includes bulk operations to remove or update multiple schedules at once.
* A [web portal](web-portal) to manage and monitor platform performance and usage
    * Interact with schedules and gateways
    * View communication history
    * View platform usage statistics
    * Built using React and Material-UI
* Cost-effective solution using AWS spot instances for non-critical services
* Automatic integration testing to ensure the platform is working as expected when updating the cluster or run on a
  regular basis
* Reference CI/CD pipelines using GitHub actions to automate deployment and image building.

### Web Portal Screenshots:

#### Home Page:

![portal-home](Designs/Images/Web%20Portal/portal-home.png)

#### History Page:

* View previously sent communications.
* Filter by status, gateway, schedule ID and user ID

![portal-history](Designs/Images/Web%20Portal/portal-history.png)

#### Schedules Page:

* View, create and update schedules. Filter by gateway, schedule ID and user ID
* Carry out bulk operations to update or delete multiple schedules at once

![portal-schedules](Designs/Images/Web%20Portal/portal-schedules.png)

* Can update the gateway, schedule state or remove multiple schedules at once using bulk actions:

![portal-bulk-actions](Designs/Images/Web%20Portal/portal-bulk-actions.png)

#### Gateways Page:

* View, create and update gateways. Filter by gateway ID, name, description and endpoint URL

![portal-gateways](Designs/Images/Web%20Portal/portal-gateways.png)

#### Preferences Page:

* View and update the platform preferences
* Specify the retry policy and gateway timeout parameters

![preferences-page.png](Designs/Images/Web%20Portal/portal-preferences.png)

#### Monitoring Page:

* View platform usage statistics

![portal-monitoring.png](Designs/Images/Web%20Portal/portal-monitoring.png)

## Example use cases:

To illustrate the project’s potential, the case studies below highlight how companies like Vodafone and banks could
leverage scheduled communications to their benefit.
The case studies serve as an example of how the application developed in this project can be utilised to help businesses
enhance customer relationships and offer better services.

### Vodafone Example:

Vodafone provides internet services for mobile and home broadband. For mobile devices they offer an additional security
package called “Secure Net” to protect customers from ID theft, viruses, malware, and secures their personal
information. Secure Net uses “smart alerts” to notify customers when their information is compromised.
This project aims to create an application that would enable Vodafone to add extra features to services like Secure Net.
If Vodafone had a scheduled communication platform, they could better utilise customers' usage data. For example, a
customer could receive a weekly or monthly SMS message or email that summarises how many threats Secure Net has blocked.
The customer could specify which day of the week and time they want the alerts to be sent and have the option to easily
turn off all or some notifications. This would improve customer relationships and potentially keep high customer
engagement with a product like Secure Net if customers can see that the service is actively protecting them.

In this context, Vodafone would implement an SMS gateway and an email gateway that generates a message containing a
customer’s Secure Net statistics. These gateways would integrate into their existing customer databases and notification
delivery mechanisms. They could then leverage this application to handle scheduling each customer’s Secure Net
notification preferences while ensuring each notification is reliably delivered. Ensuring notifications are delivered
successfully is challenging. What happens if one service is not responding? This project aims to solve the delivery
challenges by using retry mechanisms to ensure successful delivery and provide a resilient solution.

### Banking Example:

In the financial industry, customers often receive banking statements on a reoccurring basis. From my own experience, I
have not been able to choose the day of the week, month or time these are delivered. A bank could implement SMS and
email gateways and offer customers choices about where their statement is delivered to and when. Banks could then rely
on this application’s retry mechanisms to be confident that communications will not get lost and would always be
delivered. This provides customers with greater control and options over their notifications, improving customer
relationships. It also enables businesses to increase their revenue streams by offering tailored communications for new
or existing services.

## Requirements:

* AWS account (Free tier is not sufficient)
* MongoDB Atlas (Free tier is sufficient)
* [Helm](https://helm.sh/docs/intro/install/)
* [Terragrunt](https://terragrunt.gruntwork.io/docs/getting-started/install/)
* [Terraform](https://learn.hashicorp.com/tutorials/terraform/install-cli)
* [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [Lens](https://k8slens.dev/) (Optional but recommended)

## Repository Structure:

Find more information about each component in their respective README files.

* [Auth API](auth-api) - Generates JWT tokens for use with the other APIs
* [Designs](Designs) - Contains context, cost analysis, sequence, deployment and activity diagrams explaining the
  project in more detail
* [Communication Worker](communication-worker) - Worker that polls the Temporal task queue and triggers gateways to send
  messages
* [Data Converter API](data-converter-api) - Use with the Temporal UI to decode the encrypted payloads
* [Deployment](deployment) - Contains the Terragrunt, Helm and Terraform projects to deploy the platform
* [Email Gateway](email-gateway) - Example gateway implementation that sends an email to a user
* [Gateway API](gateway-api) - API to manage the gateways in MongoDB
* [Gateway Library](gateway-library) - The library that all gateways must implement, contains implementation
  walkthroughes.
* [History API](history-api) - API to stop Temporal workflows and retrieve past and running workflows from Temporal
* [Integration Tests](integration-tests) - Integration tests for the platform, useful to test components in isolation or
  the whole platform
* [Mock Gateway](mock-gateway) - Example gateway that always returns a successful response to the worker to complete the
  worker (regardless of the user ID used)
* [Preferences API](preferences-api) - API to manage the platform preferences (stored as a Kubernetes config map)
* [Schedule API](schedule-api) - API to manage the schedules in Temporal
* [SMS Gateway](sms-gateway) - Example gateway implementation that sends an SMS to a user
* [Stress Tests](stress-test) - Starts a large number of workflows using the Mock gateway to test the platform's
  scalability
* [Web Portal](web-portal) - React web portal to manage and monitor the platform
* [Web Portal BFF API](web-portal-bff) - Backend for the web portal, interacts with the other APIs to provide data to
  the web portal

## Getting Started:

- Follow the instructions in the [deployment](deployment/README.md) folder to deploy and configure the platform
- Use the [gateway library](gateway-library/README.md) to create your own gateways
- Once deployed port forward the web portal service to your local machine and use the web portal to manage and monitor
  the platform
    - Using software like [Lens](https://k8slens.dev/) can also be useful to monitor the cluster and manage port
      forwarding services
    - Alternatively, you can port forward the web service using this kubectl
      command: `kubectl port-forward svc/cs-web-portal-service 8080:3000`
    - Then access the web portal at `https://localhost:8080`
- Use the [Temporal UI](https://docs.temporal.io/docs/ui-quick-install) to monitor the workflows and schedules in
  Temporal as an extra debugging tool
    - You can also port forward the service to your local machine
    - Connect the data converter API ([see readme](data-converter-api/README.md#getting-started)) to the Temporal UI to
      decode the payloads
