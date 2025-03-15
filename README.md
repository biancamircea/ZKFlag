# ZKFlag: Privacy-First Feature Flags with Zero-Knowledge Proofs

Feature flags are mechanisms that provide flexible deployment by 
enabling or disabling certain features of an application in real-time without modifying its source code. This technology helps developers accelerate the delivery of new features while reducing the risks associated with implementation through strategies such as progressive rollout.

ZKFlag provides a **feature flags as a service** solution through a centralized server, allowing users to easily manage the activation and deactivation of features across multiple applications in a flexible and personalized way.

The platform provides a significant security advantage over other feature flag platforms by integrating **Zero Knowledge Proof (ZKP)** technology. Through ZKP, we can evaluate flags based on sensitive information, without explicitly disclosing this data to the feature flags server.

<div align="center">
  <img src="./images/arhitecture_diagram.png" alt="Architecture Diagram" width="600">
</div>

## 1. Starting Server

To install and use our app, clone the repository using the following command:

```bash
git clone https://github.com/biancamircea/ZKFlag.git

Next, navigate to the ZKFlag folder inside the repository and start the feature flags server along with the admin dashboard and the necessary services:

```bash
docker-compose up

Once the services are up and running, you can manage the server at: http://localhost:5173
