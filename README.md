# Type-Safe Config Library for Scala 3
... built using heterogeneous maps.

This is a simple config library, built on top of heterogeneous maps. Heterogeneous maps ensure that the right type of value is used for each key.

Short summary of what you get:
- Type-safe configurations
- Checked at compile time, no runtime exceptions
- A simple API
- Implemented in less than 100 lines of code

Example usage:
```scala
type HelloConf = HelloConf.default._type

object HelloConf:
  val default = Config.empty
    .set("name", "John")
    .set("age", 42)

object Hello:
  def main(args: Array[String]): Unit =
    val conf = HelloConf.default
      .replace("name", "Alice")
      .replace("age", 43)

    println(summon[conf.type <:< HelloConf]) // generalized constraint

    println(conf.get("name")) // Alice
    println(conf.get("age")) // 43

    def prettyPrint(conf: HelloConf): String =
      s"Config(${conf.get("name")}, ${conf.get("age")})"

    println(prettyPrint(conf)) // Config(Alice, 43)

    // // This will not compile:
    // val otherConf = Config.empty
    //   .set("colour", "blue")
    // println(prettyPrint(otherConf)) // ... Required: config.examples.HelloConf
    // println(summon[otherConf.type <:< HelloConf]) // Cannot prove that (otherConf ... <:< ... HelloConf)

    // // This will not compile:
    // HelloConf.default.set("name", "Bob") // Cannot set key ("name" : String) in HMap, key already exists

    // // This will not compile:
    // HelloConf.default.get("address") // Cannot get key ("address" : String) from HMap, no such key found

    // // This will not compile:
    // HelloConf.default.replace("name", 42) // Cannot replace key ("name" : String) and value Int in HMap, this pair does not exist
```
