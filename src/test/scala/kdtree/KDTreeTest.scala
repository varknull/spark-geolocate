package kdtree

import org.scalatest.FunSuite

class KDTreeTest extends FunSuite {
  val kdtree = new KDTree

  test("KDTree.searchKNeighbors") {
    val k = 1

    kdtree.insert(1,1,"test1")
    kdtree.insert(3,3,"test2")
    kdtree.insert(3,-3,"test3")
    kdtree.insert(-2,2,"test4")
    kdtree.insert(-1,2,"testLeft")
    kdtree.insert(1,-2,"testRight")
    kdtree.insert(2,-2,"test5")

    val closest = kdtree.searchKNeighbors(-2,3, k).elems(0)

    assert("test4" === closest.data)
    assert(-2 === closest.x)
    assert(2 === closest.y)
  }
}
