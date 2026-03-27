# engagelab-sdk-java

This is the Java version of the EngageLab REST API development package, provided by the official EngageLab, generally
supporting the latest API functionalities.

REST API DOC:

* [REST API - Push](https://www.engagelab.com/docs/app-push/rest-api/create-push-api)
* [REST API - Device](https://www.engagelab.com/docs/app-push/rest-api/Tag-Alias-API)
* [REST API - Status](https://www.engagelab.com/docs/app-push/rest-api/statistics-api)
* [REST API - Schedule](https://www.engagelab.com/docs/app-push/rest-api/scheduled-tasks-api)
* [REST API - GroupPush](https://www.engagelab.com/zh_CN/docs/app-push/rest-api/group-push-api)
* [REST API - PushPlan](https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/push-plan-api)
* [REST API - Message Withdraw](https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/message-recall-api)
* [REST API - Delete User](https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/delete-user-api)
* [REST API - Image](https://www.engagelab.com/zh_CN/docs/app-push/developer-guide/rest-api/image-api)

support JDK 1.8.

## 1. Dependency

```xml
<dependencies>
    <!-- engagelab-sdk-java -->
    <dependency>
        <groupId>io.github.engagelab-mt</groupId>
        <artifactId>engagelab-sdk-java</artifactId>
        <version>0.0.21</version>
    </dependency>
</dependencies>
```

## 2. Logging

Configure a logging framework that implements the SLF4J interface, such as logback, log4j, or commons-logging.
If you are using Spring Boot, logback is already included and no extra configuration is needed.

```xml
<!--for example, use logback-->
<dependencies>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.11</version>
    </dependency>
</dependencies>
```

## 3. Dependency Conflicts

The SDK transitively pulls in the following libraries. If your project uses different versions of any of them, Maven's nearest-definition rule may result in version mismatches. Use `<dependencyManagement>` to pin the version you need.

| Library | Version bundled by SDK |
|---|---|
| feign-core / feign-okhttp / feign-jackson | 13.5 |
| okhttp3 | 4.12.0 |
| jackson-databind | 2.18.0 |
| jackson-datatype-jsr310 | 2.15.3 |
| slf4j-api | 2.0.16 |

Example — override jackson to match your project:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${your.jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>${your.jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>${your.jackson.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Or exclude the conflicting transitive dependency entirely and declare your own:

```xml
<dependency>
    <groupId>io.github.engagelab-mt</groupId>
    <artifactId>engagelab-sdk-java</artifactId>
    <version>0.0.21</version>
    <exclusions>
        <exclusion>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 4. Api

`DataCenterHost` specifies the data center region. Choose the one that matches your EngageLab account:

| Value | Region |
|---|---|
| `DataCenterHost.HK` | Hong Kong |
| `DataCenterHost.SG` | Singapore |
| `DataCenterHost.VA` | US Virginia |
| `DataCenterHost.FFM` | Germany Frankfurt |

create api
> also can set client and loggerLevel if you need.

```java
// appKey and masterSecret from EngageLab console
@Bean
public PushApi pushApi() {
    return new PushApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}

@Bean
public DeviceApi deviceApi() {
    return new DeviceApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}

@Bean
public StatusApi statusApi() {
    return new StatusApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}

@Bean
public ScheduleApi scheduleApi() {
    return new ScheduleApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}

@Bean("okHttpClient")
public OkHttpClient okHttpClient() {
    okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient().newBuilder()
            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy_host", proxy_port))) // set proxy
            .connectTimeout(5, TimeUnit.SECONDS) // set connect timeout
            .build();
    return new OkHttpClient(okHttpClient);
}

@Bean
public GroupPushApi groupPushApi(@Qualifier("okHttpClient") OkHttpClient okHttpClient) {
    return new GroupPushApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setClient(okHttpClient)
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}

@Bean
public PushPlanApi pushPlanApi() {
    return new PushPlanApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}

@Bean
public ImageApi imageApi() {
    return new ImageApi.Builder()
            .setHost(DataCenterHost.HK.getUrl())
            .setAppKey(appKey)
            .setMasterSecret(masterSecret)
            .build();
}
```

api-example

* [PushApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/PushApiTest.java)
* [DeviceApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/DeviceApiTest.java)
* [StatusApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/StatusApiTest.java)
* [ScheduleApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/ScheduleApiTest.java)
* [GroupPushApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/GroupPushApiTest.java)
* [PushPlanApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/PushPlanApiTest.java)
* [ImageApi](https://github.com/engagelab-mt/engagelab-sdk-java/blob/main/example-for-spring/src/test/java/io/github/engagelab/api/ImageApiTest.java)
