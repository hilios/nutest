package services

import scala.math.pow

/**
  * This class calculates the points earned by users based on the number of invites they did.
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
class RewardService(invites: Seq[(Int, Int)]) {
  val graph: Map[Int, Set[Int]] = buildGraph(invites)

  /**
    * Returns the points earned from each ID.
    */
  def toMap: Map[Int, Double] = keys.map(id => (id, getPoints(graph, id))).toMap

  /**
    * Returns a set of IDs from the invites.
    */
  private def keys: Set[Int] = invites.flatMap(i => Set(i._1, i._2)).toSet

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
        head +: removeDuplicates(tail, computed + head._2)
    }
    // Group by the first value of the tuple and map the second to each of it
    removeDuplicates(list, Set.empty).groupBy(_._1).map { case (k,v) => (k,v.map(_._2).toSet)}
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
    def DFS(node: Int, visited: Set[Int], depth: Int): Seq[(Int, Int)] = {
      // Not expand repeated states (i.e. circular references) removing the visited
      val children = graph.getOrElse(node, Set.empty) -- visited -- Set(0)
      if (children.nonEmpty) {
        // Expand each children node
        (node, depth) +: children.flatMap(DFS(_, Set(node) ++ children ++ visited, depth + 1)).toSeq
      } else {
        Seq.empty
      }
    }
    // Filter the root node, map the
    DFS(root, Set.empty, -1).filterNot(_._2 < 0).map(x => pow(0.5, x._2)).sum
  }
}

/**
  * Companion object for the RewardService.
  */
object RewardService {
  def apply(invites: Seq[(Int, Int)]): RewardService = new RewardService(invites)
}