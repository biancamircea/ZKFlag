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
```

Next, navigate to the ZKFlag folder inside the repository and start the feature flags server along with the admin dashboard and the necessary services:

```bash
docker-compose up
```

Once the services are up and running, you can manage the server at: http://localhost:5173


## 2. Integrating with Client Applications
### 2.1. Integration with SDK

To integrate the server with Java applications, use the following steps:

1. In the pom.xml file of your client application, add the dependency:
```bash
<dependency>
  <groupId>ro.mta.sdk</groupId>
  <artifactId>toggle-system</artifactId>
  <version>1.0.3</version>
</dependency>
```

2. Add the repository to your pom.xml file:

```bash
maven {
    url = uri("https://maven.pkg.github.com/biancamircea/Licenta-Java-Client")
    credentials {
        username = project.findProperty("mavenUsername") ?: System.getenv("MAVEN_USERNAME")
        password = project.findProperty("mavenPassword") ?: System.getenv("MAVEN_PASSWORD")
    }
}
```

3. To create a client instance, use the following code:

```bash
ToggleSystemConfig toggleSystemConfig = ToggleSystemConfig.builder()
            .toggleServerAPI("http://localhost:8080")
            .apiKey("<api-key>")
            .build();
ToggleSystemClient toggleSystem = new ToggleSystemClient(toggleSystemConfig);
```

The API key is created by the Instance Admin and represents a specific project, instance, and environment.

4. To evaluate a flag, you have two options:
With constraints:

```bash
ToggleSystemContext context = ToggleSystemContext.builder()
                .addProperty("key", "value")
                .build();
boolean isEnabled = toggleSystemClient.isEnabled("toggle_name", context);
```

Without constraints:

```bash
boolean isEnabled = toggleSystemClient.isEnabled("toggle_name");
```

5. To retrieve the associated payload:

```bash
toggleSystem.getPayload("toggle_name");  // Without context
toggleSystem.getPayload("toggle_name", context);  // With context
```















