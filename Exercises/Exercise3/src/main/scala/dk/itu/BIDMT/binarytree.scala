package dk.itu.BIDMT

object binarytree {

  abstract class BinaryTree {
    def isEmpty:Boolean
  }

  case class Node(value:Int, leftChild:BinaryTree, rightChild:BinaryTree) extends BinaryTree{
    def isEmpty = false
  }

  case object End extends BinaryTree {
    def isEmpty = true
  }

  //def isBinarySearchTree(bt:BinaryTree):Boolean = ???

  //def addNode(x:Int,tree:BinaryTree):BinaryTree = ???

  def binaryCode1(n: Int): List[String] =
    if (n == 0) List()
    else if (n == 1) List("0", "1")
    else {
      binaryCode1(n - 1).flatMap(x => List(x + 0, x + 1))
    }

  def binaryCode2(n: Int): List[String] =
    if (n == 0) List()
    else if (n == 1) List("0", "1")
    else {
      val xs = binaryCode2(n - 1)
      xs.map(x => "0" + x) ::: xs.map(x => "1" + x)
    }

  def grayCode(n: Int): List[String] =
    if (n == 0) List()
    else if (n == 1) List("0", "1")
    else {
      val xs = grayCode(n - 1)
      xs.map(x => "0" + x) ::: xs.reverse.map(x => "1" + x)
    }
}
