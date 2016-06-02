package services

import javax.inject.{Inject, Singleton}
import play.api.cache.CacheApi

/**
  * Provides a facade for interacting with the [[CacheApi]].
  */
@Singleton
class InviteService @Inject()(cacheApi: CacheApi) {
  private val key = "invites"

  /** Returns the invites */
  def get: Seq[(Int, Int)] = cacheApi.getOrElse(key)(Seq.empty)

  /** Overwrite all invites */
  def set(seq: Seq[(Int, Int)]) = cacheApi.set(key, seq)

  /** Append a new invite */
  def add(seq: Seq[(Int, Int)]) = cacheApi.set(key, get ++ seq)

  /** Removes all invites */
  def clear() = cacheApi.remove(key)
}

/**
  * Companion object for the [[InviteService]].
  */
object InviteService {

  /**
    * Returns some file as list of invitation in the format (From, To).
    *
    * @param string The string to parse
    * @return A list of invitations
    */
  def parse(string: String): Seq[(Int, Int)] =
    string trim() split "\n" filterNot (_.isEmpty) map(_.split(" ") match {
      case Array(from, to) => (from.toInt, to.toInt)
    }) toList
}