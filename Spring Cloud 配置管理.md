# Spring Cloud 配置管理

# 什么是配置

### 基本概念

配置（Configuration）是应用程序重要的元信息（Metadata），几乎所有软件依赖配置调控程序行为，比如 MySQL 中的 my.ini 文件，Apache Maven 的配置文件 settings.xml 文件，以及其项目管理文件 pom.xml。尽管配置常以文件的形式承载，然而并仅限于此。对于应用程序而言，配置的内容远比存储介质重要，同时，应用也有偏好的配置格式，比如 Key-Value 格式、XML 格式、JSON 格式以及自定义格式等。



### 配置源分类

- 本地配置（Local Configuration） 

- - 内部配置（Internal Configuration）
  - 外部配置（External Configuration）

- 远程配置（Remote Configuration） 

- - 版本化配置（Versioned Configuration）

  - 分布式配置（Distributed Configuration）

    

### 本地配置（Local Configuration）

顾名思义，本地配置即配置存放在应用程序所在的物理环境，比如程序内部、物理机器或虚拟化容器。存放在程序进程内部的配置称之为“内部配置（Internal Configuration）”，相反则是“外部配置（External Configuration）”。

### 内部配置（Internal Configuration）

通常内部配置是通过程序代码，甚至是硬编码（Hard Code）实现。

### 外部配置（External Configuration）

外部配置是软件使用者、开发者以及运维者最常见和熟悉的配置手段，如 MySQL my.ini 文件等。在 Java 生态体系中，JDK 层面允许程序读取来自 Java System Properties、操作系统环境变量以及 JNDI 等多方配置来源。

### 远程配置（Remote Configuration）

在分布式场景中，远程配置的内容来自于 配置应用端程序进程物理环境以外的环境，其作用相当重要，是大多数互联网企业的基础设施标配，大多数实现采用 C/S（客户端/服务器）架构。此处的 C（Client）并非狭隘地使用特定的客户端，可以泛指 Web 客户端或 HTTP Client，换言之，C/S 架构广义地包含了 B/S（浏览器/服务器）架构，这两种实现均采用 B/S 架构，属于“分布式配置（Distributed Configuration）B/S 架构，然而都归类于“分布式配置实现，即应用需要依赖一个专属的 HTTP 客户端，通过访问远端的 Web服务器获取配置。实现上，配置客户端版本化配置（Versioned Configuration）”。



### 配置源优先级设计技巧

1. **优先读取外部配置，将内部配置作为默认策略**

2. **当存在多源外部配置时，源作用（共享）范围越小通常优先级越高**



## Spring Framework 配置来源 - 配置大于编码

### Environment 抽象

表示当前应用程序运行环境的接口。对应用程序环境的两个关键方面进行建模：Profiles 和 Properties。

#### Profiles

- Profiles 是一个命名的、逻辑上的 bean 定义组，只有在给定的配置文件处于激活状态时才会被注册到容器中。无论是用 XML 定义的还是用注解定义的，Bean 都可以被分配给 Profile。
- Environment 在 Profiles 中的作用：
  - 指定哪些 Profiles 是激活的
  - 指定哪些 Profiles 是默认激活的

#### Properties 

- 属性配置在几乎所有的应用程序中都扮演着重要的角色，它可能来自各种来源：属性文件、JVM系统属性、系统环境变量、JNDI、Servlet上下文参数、特设的属性对象、Map对象等等。
- Environment 在 Properties 中的作用
  - 为用户提供一个方便的服务接口
  - 从属性配置源解析得到属性
  - ${...} 占位符解析



### Environment  相关 API 

- org.springframework.core.env.PropertyResolver：属性访问、占位符解析

  - 属性解析实现：PropertySourcesPropertyResolver 
    - 类型转换：org.springframework.core.convert.support.ConfigurableConversionService

- org.springframework.core.env.Environment：Proflie 相关

- org.springframework.core.env.ConfigurableEnvironment：配置 Profiles、获取配置属性源（可变）、获取 JVM 系统属性、获取操作系统环境变量

  - org.springframework.core.env.MutablePropertySources
    - List<PropertySource<?>> propertySourceList

- org.springframework.core.env.AbstractEnvironment: ConfigurableEnvironment 接口的基本实现

  - 子类自定义配置属性源：构造器 AbstractEnvironment() -> protected customizePropertySources

- org.springframework.core.env.StandardEnvironment：非 web 环境的 Environment

  - 配置属性源
    -  JVM 系统属性：systemProperties
    - 操作系统环境变量：systemEnvironment

- org.springframework.web.context.support.StandardServletEnvironment：web 环境的 Environment

  - 配置属性源

    - Servlet 配置初始化参数 - servletConfigInitParams

    - Servlet 上下文初始化参数 - servletContextInitParams

    - JNDI 配置属性 - jndiProperties [optional]

    - JVM 系统属性：systemProperties

    - 操作系统环境变量：systemEnvironment

      

### ApplicationContext 创建 Environment

- 非 web 环境 - org.springframework.context.support.AbstractApplicationContext#createEnvironment
- web 环境 - org.springframework.web.context.support.GenericWebApplicationContext#createEnvironment



### 扩展属性源 - 在 ApplicationContext 刷新时才加入

- org.springframework.context.annotation.PropertySource
- org.springframework.context.annotation.PropertySources

#### 源码实现

org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry

-> org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions

-> org.springframework.context.annotation.ConfigurationClassParser#parse(java.util.Set<org.springframework.beans.factory.config.BeanDefinitionHolder>)

-> org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass

-> org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass 

-> org.springframework.context.annotation.ConfigurationClassParser#processPropertySource



#### @Value 属性注入

#### 解析源码

org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties

-> org.springframework.beans.factory.annotation.InjectionMetadata#inject

-> org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject

-> org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency

-> org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency

​	->解析得到占位符表达式： org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#getSuggestedValue

​	-> org.springframework.beans.factory.support.AbstractBeanFactory#resolveEmbeddedValue



#### StringValueResolver 

BeanFactory 未配置 StringValueResolver -  AbstractBeanFactory#hasEmbeddedValueResolver - 基于Enviroment 的 Lambda StringValueResolver  Lambda 

​	org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization

```java
strVal -> getEnvironment().resolvePlaceholders(strVal)
```

通过 org.springframework.context.support.PropertySourcesPlaceholderConfigurer 添加 StringValueResolver 







## Spring Boot 配置来源

### 外部化配置 - Externalized Configuration

https://docs.spring.io/spring-boot/docs/2.3.12.RELEASE/reference/htmlsingle/#boot-features-external-config

#### 扩展配置源步骤

核心 API

- SpringApplication
- org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent

- org.springframework.boot.context.config.ConfigFileApplicationListener 

1. SpringBoot 上下文启动前提前准备好应用 Environment， 再将准备好的 Environment 设置回 ApplicationContext。

   org.springframework.boot.SpringApplication#prepareEnvironment

2. 根据 ApplicationContext 场景创建对应的 Environment

   org.springframework.boot.SpringApplication#getOrCreateEnvironment

   ```java
   private ConfigurableEnvironment getOrCreateEnvironment() {
       if (this.environment != null) {
           return this.environment;
       }
       switch (this.webApplicationType) {
           case SERVLET:
               return new StandardServletEnvironment();
           case REACTIVE:
               return new StandardReactiveWebEnvironment();
           default:
               return new StandardEnvironment();
       }
   }
   ```

   web 环境下此时配置源列表有

- Servlet 配置初始化参数 - servletConfigInitParams

- Servlet 上下文初始化参数 - servletContextInitParams
- JNDI 配置属性 - jndiProperties  [optional]
- JVM 系统属性：systemProperties
- 操作系统环境变量：systemEnvironment

3. 添加 defaultProperties 属性源 和 commandLineArgs 属性源

   org.springframework.boot.SpringApplication#configurePropertySources

​	web 环境下此时配置源列表有

- Propertysources 引用副本 - configurationProperties [忽略]
- 命令行参数 -commandLineArgs
- Servlet 配置初始化参数 - servletConfigInitParams

- Servlet 上下文初始化参数 - servletContextInitParams

- JNDI 配置属性 - jndiProperties  [optional]

- JVM 系统属性：systemProperties

- 操作系统环境变量：systemEnvironment
- SpringBoot Default Properites - defaultProperties [optional]

4. ApplicationEnvironmentPreparedEvent 事件发布与监听

   1. ApplicationEnvironmentPreparedEvent 事件发布 
      SpringApplicationRunListeners#environmentPrepared -> EventPublishingRunListener#environmentPrepared

   2. ApplicationEnvironmentPreparedEvent 事件监听 - ConfigFileApplicationListener

      1. EnrionmentPostProcessor

      - SystemEnvironmentPropertySourceEnvironmentPostProcessor
      - SpringApplicationJsonEnvironmentPostProcessor
      - CloudFoundryVcapEnvironmentPostProcessor
      - **ConfigFileApplicationListener**
      - DebugAgentEnvironmentPostProcessor

5.  添加更多配置属性源 - ConfigFileApplicationListener#postProcessEnvironment - > addPropertySources

   1. 添加 random 配置属性源到 systemEnvironment 后 RandomValuePropertySource#addToEnvironment

   2. 添加 applicationConfig:[xxx-{profile}.yml/properties] 配置源

      1. org.springframework.boot.context.config.ConfigFileApplicationListener.Loader#getSearchLocations() 获取搜索文档的资源路径
      2. org.springframework.boot.context.config.ConfigFileApplicationListener.Loader#getSearchNames 获取文档名称

      ```java
      // Note the order is from least to most specific (last one wins)
      	private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/,file:./,file:./config/*/,file:./config/"; // 后添加具有高优先级
      
      	private static final String DEFAULT_NAMES = "application";
      ```

      2. 通过 PropertysourceLoader 加载文档资源 - PropertiesPropertySourceLoader、YamlPropertySourceLoader

​	web 环境下此时配置源列表有

- Propertysources 引用副本 - configurationProperties [忽略]
- 命令行参数 -commandLineArgs
- Servlet 配置初始化参数 - servletConfigInitParams

- Servlet 上下文初始化参数 - servletContextInitParams

- JNDI 配置属性 - jndiProperties  [optional]

- JVM 系统属性：systemProperties

- 操作系统环境变量：systemEnvironment
- SpringBoot Default Properites - defaultProperties [optional]
- 包外 application-profile 配置 - applicationConfig: [file:./[config/]application-profile.yml/properties]
- 包内 application-profile 配置 - - applicationConfig: [classpath:./[config/]application-profile.yml/properties]
- 包外 application 配置 - applicationConfig: [file:./[config/]application.yml/properties]
- 包内 application 配置 - - applicationConfig: [classpath:./[config/]application.yml/properties]



### Actuator - /actuator/env

maven: spring-boot-starter-actuator

Endpoint: org.springframework.boot.actuate.env.EnvironmentEndpoint

EndpointExposure

- JMX: management.endpoints.jmx.exposure.include
- WEB: management.endpoints.web.exposure.include



### @ConfigurationProperties 和 @EnableConfigurationProperties

Endpoint: ConfigurationPropertiesReportEndpoint

[Relaxed Binding](https://docs.spring.io/spring-boot/docs/2.3.12.RELEASE/reference/htmlsingle/#boot-features-external-config-relaxed-binding)

[@ConfigurationPropertiesBean VS @Value](https://docs.spring.io/spring-boot/docs/2.3.12.RELEASE/reference/htmlsingle/#boot-features-external-config-vs-value)

org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor

ConfigurationPropertiesBean





### Spring Boot 2.4.0+

> https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Config-Data-Migration-Guide

> https://github.com/spring-projects/spring-boot/issues/3845

> https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/#boot-features-external-config-files



默认配置文件路径

- org.springframework.boot.context.config.ConfigFileApplicationListener#DEFAULT_SEARCH_LOCATIONS 2.4.0之前
- org.springframework.boot.context.config.ConfigDataEnvironment#DEFAULT_SEARCH_LOCATIONS 2.4.0+



在 spring.factories 中 ConfigFileApplicationListener 被替换为 EnvironmentPostProcessorApplicationListener 和 ConfigDataEnvironmentPostProcessor

ConfigDataEnvironmentContributors 提供配置源



## Spring Cloud 配置来源

### Spring Cloud Config Server

#### Backend

##### 启用 Config Server 

1. 在配置类上标注 @EnableConfigServer 注解
2. @EnableConfigServer 加载了 ConfigServerConfiguration 配置类，注册 Marker 标注 bean，使得 ConfigServerAutoConfiguration 自动配置类条件注解生效。

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(ConfigServerConfiguration.Marker.class)
@EnableConfigurationProperties(ConfigServerProperties.class)
@Import({ EnvironmentRepositoryConfiguration.class, CompositeConfiguration.class, ResourceRepositoryConfiguration.class,
		ConfigServerEncryptionConfiguration.class, ConfigServerMvcConfiguration.class,
		ResourceEncryptorConfiguration.class })
public class ConfigServerAutoConfiguration {

}
```



##### 加载配置仓库 - EnvironmentRepositoryConfiguration、CompositeConfiguration

- GIT - GitRepositoryConfiguration / DefaultRepositoryConfiguration(defualt active) ->  MultipleJGitEnvironmentRepository
  - 启用方式：激活 git Profile
  - ConfigurationProperties: MultipleJGitEnvironmentProperties

- JDBC - JdbcRepositoryConfiguration -> JdbcEnvironmentRepository
  - 启用方式：激活 jdbc Profile
  - ConfigurationProperties: JdbcEnvironmentProperties
- 组合 Environment 仓库 -  CompositeEnvironmentRepository（@Primary）



##### 配置加密 - ConfigServerEncryptionConfiguration 、ResourceEncryptorConfiguration



##### 配置访问 EndPoint - ConfigServerMvcConfiguration

EnvironmentController

访问格式

- JSON

  - /{name}/{profiles:.*[^-].*}

  - /{name}/{profiles:.*[^-].*}

  - /{name}/{profiles}/{label:.*}

- /{name}/{profiles}/{label:.*}





### Spring Cloud Config Client

#### 连接配置中心方式

org.springframework.cloud.config.client.ConfigServerConfigDataLoader

- 基于 DNS 或网关的发现方式
- 基于注册中心的发现方式

ApplicationEnvironmentPreparedEvent 事件监听

org.springframework.boot.env.EnvironmentPostProcessorApplicationListener#onApplicationEnvironmentPreparedEvent

-> org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor#postProcessEnvironment(org.springframework.core.env.ConfigurableEnvironment, org.springframework.boot.SpringApplication)

-> org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor#postProcessEnvironment(org.springframework.core.env.ConfigurableEnvironment, org.springframework.core.io.ResourceLoader, java.util.Collection<java.lang.String>)

-> org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor#getConfigDataEnvironment

-> org.springframework.boot.context.config.ConfigDataEnvironment#ConfigDataEnvironment

-> org.springframework.boot.context.config.ConfigDataEnvironment#createContributors



-> org.springframework.boot.context.config.ConfigDataEnvironment#processAndApply

-> org.springframework.boot.context.config.ConfigDataEnvironment#applyToEnvironment



#### 配置刷新

- Spring Cloud Config Service - 分布式事件 
- Nacos - 轮询



#### API

org.springframework.boot.context.config.ConfigDataEnvironment



org.springframework.cloud.config.client.ConfigServerConfigDataLoader

```
AtomicInteger count = new AtomicInteger(0); contributors.forEach((e) -> {count.incrementAndGet();});
```

#### 读取远程配置

org.springframework.cloud.bootstrap.BootstrapApplicationListener#onApplicationEvent

-> org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration#initialize



##### Spring Cloud BootstrapApplicationContext 和 Spring Boot ApplicationContext的层级关系

> A Spring Cloud application operates by creating a “bootstrap” context, which is a parent context for the main application. This context is responsible for loading configuration properties from the external sources and for decrypting properties in the local external configuration files. The two contexts share an `Environment`, which is the source of external properties for any Spring application. By default, bootstrap properties (not `bootstrap.properties` but properties that are loaded during the bootstrap phase) are added with high precedence, so they cannot be overridden by local configuration.



> If your application needs any application-specific configuration from the server, it is a good idea to set the `spring.application.name` (in `bootstrap.yml` or `application.yml`). For the property `spring.application.name` to be used as the application’s context ID, you must set it in `bootstrap.[properties | yml]`.
>
> If you want to retrieve specific profile configuration, you should also set `spring.profiles.active` in `bootstrap.[properties | yml]`.
>
> You can disable the bootstrap process completely by setting `spring.cloud.bootstrap.enabled=false` (for example, in system properties).



##### 远程配置与本地配置的优先级关系

> The property sources that are added to your application by the bootstrap context are often “remote” (from example, from Spring Cloud Config Server). By default, they cannot be overridden locally. If you want to let your applications override the remote properties with their own system properties or config files, the remote property source has to grant it permission by setting `spring.cloud.config.allowOverride=true` (it does not work to set this locally). Once that flag is set, two finer-grained settings control the location of the remote properties in relation to system properties and the application’s local configuration:

> - `spring.cloud.config.overrideNone=true`: Override from any local property source.

> - `spring.cloud.config.overrideSystemProperties=false`: Only system properties, command line arguments, and environment variables (but not the local config files) should override the remote settings.



##### 配置项和默认值

ConfigPropertiesBean:  org.springframework.cloud.bootstrap.config.PropertySourceBootstrapProperties

spring.cloud.config.allowOverride=true

spring.cloud.config.overrideNone=false

spring.cloud.config.overrideSystemProperties=true



##### 配置项说明

allowOverride: 是否允许远程配置被本地配置覆盖，默认为 true。 

- true：由更细粒度的配置项 overrideNone 和 overrideSystemProperties 控制远程配置、系统配置、应用本地配置之间的相对优先级
  - overrideNone：远程配置是否被本地任意来源覆盖，默认为 false。为 true 时，则远程配置优先级最低
  - overrideSystemProperties：远程配置是否覆盖系统配置，默认为 true。为 true 时远程配置添加到系统配置源之前，为 false 时添加到系统配置源之后。
- false：不能被本地配置覆盖，远程配置具有最高优先级



**默认配置下 SpringCloud 外部配置具有最高优先级。**



源代码位置: org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration#insertPropertySources

```java
private void insertPropertySources(MutablePropertySources propertySources, List<PropertySource<?>> composite) {
    MutablePropertySources incoming = new MutablePropertySources();
    List<PropertySource<?>> reversedComposite = new ArrayList<>(composite);
    // Reverse the list so that when we call addFirst below we are maintaining the
    // same order of PropertySources
    // Wherever we call addLast we can use the order in the List since the first item
    // will end up before the rest
    Collections.reverse(reversedComposite);
    for (PropertySource<?> p : reversedComposite) {
        incoming.addFirst(p);
    }
    PropertySourceBootstrapProperties remoteProperties = new PropertySourceBootstrapProperties();
    Binder.get(environment(incoming)).bind("spring.cloud.config", Bindable.ofInstance(remoteProperties));
   	// 默认生效条件。allowOverride=true, overrideNone=false, overrideSystemProperties=true
    if (!remoteProperties.isAllowOverride()
        || (!remoteProperties.isOverrideNone() && remoteProperties.isOverrideSystemProperties())) {
        for (PropertySource<?> p : reversedComposite) {
            propertySources.addFirst(p);
        }
        return;
    }
    if (remoteProperties.isOverrideNone()) {
        for (PropertySource<?> p : composite) {
            propertySources.addLast(p);
        }
        return;
    }
    if (propertySources.contains(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
        if (!remoteProperties.isOverrideSystemProperties()) {
            for (PropertySource<?> p : reversedComposite) {
                propertySources.addAfter(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, p);
            }
        }
        else {
            for (PropertySource<?> p : composite) {
                propertySources.addBefore(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, p);
            }
        }
    }
    else {
        for (PropertySource<?> p : composite) {
            propertySources.addLast(p);
        }
    }
}
```







