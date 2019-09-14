package Lecture3_InClass

object ListExamples {
  def main(args: Array[String]): Unit = {
    println(concatLists(List(1, 2, 3, 4, 5, 6), List(2,4,5)))
  }
  //my return value should be an Int
  def lastElm(ls:List[Int]): Int = ls match {
    case Nil => throw new Error("empty List")
      //when there is just one element in the list, print the element h
    case List(h) => h
      //otherwise go through each element until it has only tail left und print that tail
    case _ :: tail => lastElm(tail)
  }
  //return value should be a List[Int]
  def initList(ls: List[Int]) : List[Int]  = {
    ls match {
      case Nil => throw new Error("empty List")
        //if the list has one element print empty List
      case List(h) => List()
        //otherwise make a new list with the first element h followed by
        //when using :: then the you append only an element to a list
        //when using ::: then you append a lst to another list
      case h::tail => h::initList(tail)
    }
  }

  def concatLists(xs: List[Int] , ys: List[Int]) : List[Int] ={
    xs match{
        //if empty then return second list (ys)
      case Nil => ys
        //otherwise make a new list with the first element z followed by the rest of the lists until done
      case z::zs => z:: concatLists(zs, ys)
    }
  }
}
