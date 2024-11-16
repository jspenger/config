package config

import scala.annotation.implicitNotFound

/** Type-safe and simple config built using heterogeneous maps. */
private[config] class Config[T <: Tuple](hmap: Config.HMap[T]):
  import Config.HMap.Types.*

  /** Hack to get the config's *type*. Typically used to get the *type* of the
    * default config.
    *
    * @example
    *   {{{
    * type HelloConf = HelloConf.default._type
    * object HelloConf:
    *   val default = Config.empty
    *     .set("name", "John")
    *     .set("age", 42)
    *   }}}
    */
  type _type = Config[T]

  /** Set the key to the value. Note: you cannot set the same key twice, use
    * `replace` instead.
    */
  def set[K <: Singleton, V](key: K, value: V)(using
      @implicitNotFound(CanSet.MSG) ev: CanSet[T, K]
  ): Config[Set[T, K, V]] =
    Config(hmap.set(key, value)(using ev))

  /** Get the value of the key. Note: this will fail if the key does not exist.
    */
  def get[K <: Singleton](key: K)(using
      @implicitNotFound(CanGet.MSG) ev: CanGet[T, K]
  ): Get[T, K] =
    hmap.get(key)

  /** Replace the key with the value. Note: this will fail if the key does not
    * exist.
    */
  def replace[K <: Singleton, V](key: K, value: V)(using
      @implicitNotFound(CanReplace.MSG) ev: CanReplace[T, K, V]
  ): Config[Replace[T, K, V]] =
    Config(hmap.replace(key, value)(using ev))

end Config // class

/** Type-safe and simple config built using heterogeneous maps.
  *
  * @example
  *   {{{
  * type HelloConf = HelloConf.default._type
  * object HelloConf:
  *   val default = Config.empty
  *     .set("name", "John")
  *     .set("age", 42)
  * val conf = HelloConf.default
  *   .replace("name", "Alice")
  *   .replace("age", 43)
  *   }}}
  */
object Config:
  /** Create an empty config. Typically used for creating a default config, by
    * repeatedly applying `set` to the empty config.
    */
  val empty = Config(HMap.empty)

  private[config] class HMap[T <: Tuple](map: Map[Any, Any]):
    import HMap.Types.*

    def set[K <: Singleton, V](key: K, value: V)(using
        @implicitNotFound(CanSet.MSG) ev: CanSet[T, K]
    ): HMap[Set[T, K, V]] =
      new HMap(map + (key -> value))

    def get[K <: Singleton](key: K)(using
        @implicitNotFound(CanGet.MSG) ev: CanGet[T, K]
    ): Get[T, K] =
      map(key).asInstanceOf[Get[T, K]]

    def replace[K <: Singleton, V](key: K, value: V)(using
        @implicitNotFound(CanReplace.MSG) ev: CanReplace[T, K, V]
    ): HMap[Replace[T, K, V]] =
      new HMap(map + (key -> value))

  end HMap // class

  private[config] object HMap:
    def empty: HMap[EmptyTuple] = new HMap(Map.empty)

    object Types:
      type Contains[T <: Tuple, K] = T match
        case (K, _) *: t => true
        case _ *: t      => Contains[t, K]
        case _           => false

      type ContainsKV[T <: Tuple, K, V] = T match
        case (K, V) *: t => true
        case _ *: t      => ContainsKV[t, K, V]
        case _           => false

      type CanGet[T <: Tuple, K] = true =:= Contains[T, K]
      object CanGet { inline val MSG = "Cannot get key ${K} from HMap, no such key found" }

      type CanSet[T <: Tuple, K] = false =:= Contains[T, K]
      object CanSet { inline val MSG = "Cannot set key ${K} in HMap, key already exists" }

      type CanReplace[T <: Tuple, K, V] = true =:= ContainsKV[T, K, V]
      object CanReplace { inline val MSG = "Cannot replace key ${K} and value ${V} in HMap, this pair does not exist" }

      type Get[T <: Tuple, K] = T match
        case (K, v) *: t => v
        case _ *: t      => Get[t, K]

      type Set[T <: Tuple, K, V] = (K, V) *: T

      type Replace[T <: Tuple, K, V] = T

    end Types // object
  end HMap // object
end Config // object
