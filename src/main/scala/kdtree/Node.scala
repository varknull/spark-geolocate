package kdtree

/**
  * Defines a node of the tree.
  * It contains:
  * data, x, y: position and information of the node
  * greater, lesser, father: points a node's leafs and father
  * switch: true if you have to look at x, false if y
  **/
class Node(val data: String, val x: Double, val y: Double, var greater: Node, var lesser: Node, var father: Node, val switch: Boolean)
  extends Serializable {

  def hasGreater(): Boolean = if (greater != null) true else false

  def hasLesser(): Boolean = if (lesser != null) true else false

  override def equals(that: Any): Boolean = that match {
    case that: Node => that.data.equals(this.data) && that.x.equals(this.x) && that.y.equals(this.y)
    case _ => false
  }

  override def toString = "(" + data + "," + x + "," + y + ")"
}
