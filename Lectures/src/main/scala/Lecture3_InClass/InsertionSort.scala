package Lecture3_InClass

object InsertionSort {
  def iSort(ls: List[Int]): List[Int] = ls match {
    case Nil => Nil
    case x :: xs => insert(x , iSort(xs))
  }
  def insert(x:Int, xs:List[Int]): List[Int] =
    xs match{
      case Nil => List(x)
      case y :: ys => if(x<=y) x :: xs
      else y :: insert(x,ys)
    }


  def main(args: Array[String]): Unit = {
    val list = List(2,4,1,6,1,5,0)
    println(iSort(list))
  }


}
