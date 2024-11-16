package config

import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert._
import org.junit.Test

object Utils:
  inline def typeCheckSuccess(inline str: String): Boolean =
    scala.compiletime.testing.typeChecks(str)

  inline def typeCheckFail(inline str: String): Boolean =
    !scala.compiletime.testing.typeChecks(str)

@RunWith(classOf[JUnit4])
class ConfigTests:

  type HelloConf = HelloConf.default._type

  object HelloConf:
    val default = Config.empty
      .set("name", "John")
      .set("age", 42)

  @Test
  def testGet(): Unit =
    assertEquals(HelloConf.default.get("name"), "John")
    assertEquals(HelloConf.default.get("age"), 42)

  @Test
  def testSet(): Unit =
    val conf = Config.empty
      .set("name", "Alice")
      .set("age", 43)

    assertEquals(conf.get("name"), "Alice")
    assertEquals(conf.get("age"), 43)

  @Test
  def testReplace(): Unit =
    val conf = HelloConf.default
      .replace("name", "Alice")
      .replace("age", 43)

    assertEquals(conf.get("name"), "Alice")
    assertEquals(conf.get("age"), 43)

  @Test
  def testDefaultPattern(): Unit =
    val conf = HelloConf.default
      .replace("name", "Alice")
      .replace("age", 43)

    assertNotNull(summon[conf.type <:< HelloConf])

  @Test
  def testCompileTimeError(): Unit =
    assertTrue:
      Utils.typeCheckFail:
        """
        HelloConf.default.set("name", "Bob")
        """

    assertTrue:
      Utils.typeCheckFail:
        """
        HelloConf.default.get("address")
        """

    assertTrue:
      Utils.typeCheckFail:
        """
        HelloConf.default.replace("name", 42)
        """

    val otherConf = Config.empty
      .set("colour", "blue")

    assertTrue:
      Utils.typeCheckFail:
        """
        summon[otherConf.type <:< HelloConf]
        """

    assertTrue:
      Utils.typeCheckFail:
        """
        def prettyPrint(conf: HelloConf): String =
          s"Config(${conf.get("name")}, ${conf.get("age")})"
        prettyPrint(otherConf)
        """
