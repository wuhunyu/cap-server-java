## What is Cap Server Java?

Cap Server Java is the server component of the open-source project [Cap](https://github.com/tiagorangel1/cap). As you can see, this is a Java version.

The code references the built-in server implementation of [Cap](https://github.com/tiagorangel1/cap) and provides a `CapServer` instance to expose services externally. It implements three methods in total:

1. `createChallenge`: Creates a challenge.
2. `redeemChallenge`: Redeems the challenge and returns a temporary credential.
3. `validateToken`: Validates the temporary credential.

## Features

- Provides the `cap-server-spring-boot-starter` Spring Boot starter for out-of-the-box use.
- Extends functionality with Redis-based caching for challenges and temporary access credentials.

## Structure Overview

```shell
cap-server-java  
├── cap-server-bom                          BOM dependency management  
├── cap-server-core                         Core implementation  
├── cap-server-redis-spring-boot-starter    Redis Spring Boot starter  
├── cap-server-redis-store                  Redis storage extension  
├── cap-server-spring-boot-starter          Cap Server Spring Boot starter  
└── cap-server-spring-boot-web-example      Usage example  
```  

## Usage Example

#### Running

Run the `cap-server-spring-boot-web-example` module.

You can run the `cap-server-spring-boot-web-example` module on your computer. By default, it will listen on port `8080`.

When you see `Started CapServerWebApplication` printed in the console, it means you can access `http://localhost:8080/api/index.html` to test whether **Cap Server Java** is running properly.

![Running Effect](docs/pic/img.png)

#### Integrating Redis

You need to uncomment the following section in **cap-server-java/cap-server-spring-boot-web-example/pom.xml**:

```xml  
        <!-- cap redis store starter -->  
<!--        <dependency>-->  
<!--            <groupId>top.wuhunyu.cap.server</groupId>-->  
<!--            <artifactId>cap-server-redis-spring-boot-starter</artifactId>-->  
<!--            <version>${cap-server.version}</version>-->  
<!--        </dependency>-->  
```  

Then, modify the Redis configuration in **cap-server-java/cap-server-spring-boot-web-example/src/main/resources/redisson.yml** to your own settings.

Restart the application afterward.

#### Modifying Configuration

You can also adjust some configurations, such as **challenge count** and **challenge difficulty**.

Modify the following YAML configuration:

```yaml  
cap:  
  server:  
    # Challenge count  
    challenge-count: 50  
    # Challenge size  
    challenge-size: 32  
    # Challenge difficulty  
    challenge-difficulty: 4  
    # Challenge expiration time (in milliseconds)  
    challenge-expires-ms: 600000  
    # Token expiration time (in milliseconds)  
    token-expires-ms: 1200000  
    # ID size  
    id-size: 16  
    # Token delimiter (usually ":", no need to modify)  
    token-key-splitter: ":"  
```  

Each time the project starts, these configuration details will be printed:

```shell  
2025-06-17 19:10:19.714 [main] DEBUG t.w.c.server.autoconfigure.CapServerAutoConfigure - Cap property configuration: {tokenExpiresMs=1200000, challengeSize=32, idSize=16, challengeCount=50, challengeExpiresMs=600000, tokenKeySplitter=:, challengeDifficulty=4}  
```  

> Note: However, modifying these configurations is not recommended. You can either keep the above settings unchanged or ignore them entirely. If you do not actively configure these properties, default values will be applied.