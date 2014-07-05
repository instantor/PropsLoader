PropsLoader
===========

Friendly java properties handling utility for Java 8 and Scala.

Its main use case is to provide a single interface for access to System properties and multiple properties files. Additionally, properties file paths can be specified using keys from System properties.

For instance, PropsLoader can load a file from user's home folder (as defined by `user.home` key in System properties).

Getting PropsLoader
-------------------

PropsLoader is published to Sonatype OSS and Maven Central.

* Group id: *com.ferega.props*
* Latest version is 0.0.2.

#### Java 8 ####

* Artifact id: *propsloader-javaapi*

`libraryDependencies += "com.ferega.props" % "propsloader-javaapi" % "0.0.2"`

#### Scala ####

* Artifacy id: *propsloader-scalaapi*
* 
`libraryDependencies += "com.ferega.props" %% "propsloader-scalaapi" % "0.0.2"`

Java Api
--------

###### Constructor ######

```java
  public PropsLoader(
      final boolean useSystemProps,
      final PropsPath ... resolvablePathList) throws IOException;
```

First parameter, `useSystemProps`, specifies weather to use properties from `System.getProperties()`.
Second variable parameter, `resolvablePathList`, specifies a list of paths that can be resolved based on values in System properties.

`PropsPath` is used to construct a resolvable path to a file. _Resolvable_ means that some of its parts may be looked up as keys in System properties.
Specifically, if a part starts and ends with `%`, it will looked up.

```java
new PropsPath("%user.home%", ".config");
```

Would resolve to config folder is user's home folder as defined by `user.home` key in System properties.

As multiple properties sources could have same keys defined, when resolving keys, props defined earlier have precedence. That is to say, System properties have highest precedence, first file in `resolvablePathList` has next precedence, and last file has lowest precedence.

An example of a path that depends on user's home folder, and a custom key, `branch`.
```java
final PropsLoader props = new PropsLoader(true, new PropsPath("%user.home%", ".config", "project", "%branch%", "server.config")); 
```

###### get and opt ######

```java
public String get(final String key);
public Optional<String> opt(final String key);
```

`get()` and `opt()` are used to retrieve values from collected properties. `get()` will throw an `IllegalArgumentException` if a specified key is not found.

```java
final String myVal = props.get("myValKey");
final Optional<String> myOpt = props.opt("myOptKey");
```


###### getMap and select ######

```java
public Map<String, String> getMap()
public Map<String, String> select(final String prefix)
```

`getMap()` returns all available properties collected in a single map (according to precedence rules).

`select()` selectes a subset of propertes based on a prefix. It returns a map with all keys matching a given prefix, with the prefix stripped.


Scala Api
---------

Scala api has the same functionality as Java Api, but with some syntax sugary goodness.

```scala
import com.ferega.props.sapi._

val props = new PropsLoader(true, "%user.home%" %/ ".config" %/ "project" %/ "%branch%" %/ "server.config") 
val port: Int = props.get[Int]("port")
val host: String= props.get[String]("server.host")
val path: Option[String] = props.opt[String]("path") 
```
