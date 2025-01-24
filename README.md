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

After creating the object, just use one of the available methods.
Almost every method, returns a RestAction Object. To execute the request, you need to call the `#execute()` method.
This method is blocking and will return the result.
If you want to execute the request asynchronously, you can use the `#queue()` method.
This method will return `void`, but can take a `Consumer` as a parameter, which will be called when the request is completed.

Everything from here on now is pretty self-explanatory. <br>

To get a Shocker by ID, use
```java

// #getShocker will check if a shocker with this ID is cached,
// if so, it will return the cached object.
// If not, it will fetch the shocker from the API.

final RestAction<Shocker> shocker = shock.getShocker("ID").execute();
// or
shock.getShocker("ID").queue(shocker -> {
    // do something with the shocker
});
```
Alternatively, you can use `#retrieveShocker(String)` to fetch a `Shocker` object directly from the api.
Or, use `#getCachedShocker(String)` to get a cached `Shocker` object or null if not cached.

Almost everything is used like that. Shockers and Hubs (old: Devices) are cached upon creating the JavaShock Object.

<b>Note:</b> Not everything is implemented yet. If you are unsure, check the [JavaDocs](https://repo.joshicodes.de/javadoc/releases/de/joshicodes/JavaShock/latest). If not found, it is possible that the method is not implemented yet.