<div align="center">

# ☕ JavaShock
## OpenShock Wrapper for Java

</div>

> [!Note]
> 
> I am not affiliated with OpenShock or any of its developers.

Obviously, you need a [OpenShock](https://openshock.org) account with API Token to use this wrapper.

## Installation
<img src="https://repo.joshicodes.de/api/badge/latest/releases/de/joshicodes/JavaShock?prefix=v&name=Version">

To use this Wrapper, you need to add the following repository and dependency to your `pom.xml` file.
Replace `VERSION` with the latest version seen above or found [here](https://github.com/JoshiCodes/JavaShock/releases).

```xml
<repositories>
    <repository>
        <id>joshicodes-de-releases</id>
        <name>JoshiCodes Repository</name>
        <url>https://repo.joshicodes.de/releases</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>de.joshicodes</groupId>
        <artifactId>JavaShock</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```
## Usage
To use JavaShock, you need to create a new instance with your API Token.
You can create an API Token [here](https://openshock.app/#/dashboard/tokens).

```java
final JavaShock shock = new JavaShock("TOKEN-HERE");
// or if you have a custom API URL:
final JavaShock shock = new JavaShock("TOKEN-HERE", "https://api.openshock.app");
// If no URL is provided, the default URL is used.
```

Everything from here on now is pretty self-explanatory. <br>
