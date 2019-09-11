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

  def isBinarySearchTree(bt:BinaryTree):Boolean = ???

  def addNode(x:Int,tree:BinaryTree):BinaryTree = ???
}
