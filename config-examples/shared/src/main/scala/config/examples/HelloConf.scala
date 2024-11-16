package config.examples

import config.*

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
