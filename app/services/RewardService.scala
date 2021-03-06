package services

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

import scala.math.pow

/**
  * Calculates the points earned by an users based on the number of confirmed invites they did.
  *
  * The inviter gets (1/2)^k points for each confirmed invitation, where k is the level of the
  * invitation: level 0 (people directly invited) yields 1 point, level 1 (people invited by someone
  * invited by the original customer) gives 1/2 points, level 2 invitations (people invited by
  * someone on level 1) awards 1/4 points and so on. Only the first invitation counts: multiple
  * invites sent to the same person don't produce any further points, even if they come from
  * different inviters.
  *
  * Also, to count as a valid invitation, the invited customer must have invited someone (so
  * customers that didn't invite anyone don't count as points for the customer that invited them).
  */
object RewardService {

  /**
    * Returns a Rewards map with points earned by each user.
    * @param invites The list of invites
    * @return The rewards map
    */
  def apply(invites: Seq[(Int, Int)]): Map[Int, Double] = {
    val graph = buildGraph(invites)
    // First Collect a set of IDs from the invites and the calculate the points for each one.
    invites.flatMap(i => Set(i._1, i._2)).map(id => (id, getPoints(graph, id))).toMap
  }

  /**
    * Returns an tree graph like structure as an Map, removing duplicated routes.
    * @param list The list of invitations
    * @return The tree graph map
    */
  private def buildGraph(list: Seq[(Int, Int)]): Map[Int, Set[Int]] = {
    /**
      * Removes duplicated routes, hence will not allow one node to be accessible by two at the same
      * time.
      * @param list The list of nodes to be processed
      * @param computed The set of computed paths
      */
    def removeDuplicates(list: Seq[(Int, Int)], computed: Set[Int]): Seq[(Int, Int)] = list match {
      case Nil => Seq.empty
      case head::tail =>
        // If path already computed set the node to zero
        val current = if (computed contains head._2) (head._1, 0) else head

        // Append everything
        current +: removeDuplicates(tail, computed + head._2)
    }
    // Group by the first value of the tuple and map the second to each of it
    removeDuplicates(list, Set.empty).groupBy(_._1).map { case (k,v) => (k,v.map(_._2).toSet) }
  }

  /**
    * Returns the points from given user, given by the formula:
    *   f(user) = Σ n * (1/2)^k
    *
    * Whereas n is the branch ramification and k is the depth of the graph.
    *
    * @param graph The tree graph map
    * @param root The start node
    * @return The points for given
    */
  private def getPoints(graph: Map[Int, Set[Int]], root: Int): Double = {
    /**
      * Returns a list of tuple with the node and depth of every children of one node by depth-first
      * search algorithm.
      */
    def traverse(node: Int, visited: Set[Int], depth: Int): Seq[(Int, Int)] = {
      // Not expand repeated states (i.e. circular references)
      val children = graph.getOrElse(node, Set.empty) -- visited
      if (children.nonEmpty) {
        // Expand each children node that not goes to node zero
        (node, depth) +: children.filterNot(_ == 0)
          .flatMap(traverse(_, Set(node) ++ children ++ visited, depth + 1)).toSeq
      } else {
        Seq.empty
      }
    }
    // Remove the root node, calculate the points
    traverse(root, Set.empty, -1).drop(1).map(x => pow(0.5, x._2)).sum
  }
}

/**
  * Implicit conversions for the [[RewardService]] map object.
  */
trait RewardFormat {

  /**
    * Implicit JSON read for [[RewardService]] map object.
    */
  implicit val mapReads: Reads[Map[Int, Double]] = new Reads[Map[Int, Double]] {
    def reads(json: JsValue): JsResult[Map[Int, Double]] =
      JsSuccess(json.as[Map[String, Double]].map { case (k, v) =>  k.toInt -> v.toDouble })
  }

  /**
    * Implicit JSON write for [[RewardService]] map object.
    */
  implicit val mapWrites: Writes[Map[Int, Double]] = new Writes[Map[Int, Double]] {
    def writes(obj: Map[Int, Double]): JsValue =
      Json.obj(obj.map { case (k, v) =>
        val kv: (String, JsValueWrapper) = (k.toString, JsNumber(v))
        kv
      }.toSeq:_*)
  }

  /**
    * Implicit JSON format for [[RewardService]] map object.
    */
  implicit val mapFormat: Format[Map[Int, Double]] = Format(mapReads, mapWrites)
}
