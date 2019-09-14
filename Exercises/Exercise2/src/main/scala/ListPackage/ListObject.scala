package ListPackage

import scala.annotation.tailrec

object ListObject {
  def reverse(l:List[Int]):List[Int] = l match {
    //in case the list (l) is empty print an empty list
    case Nil => Nil
    //1. tries to match the incoming list (l) with something in a form like h::tail (that's how a list looks like)
    // h::tail = "a list with a single element head and a list tail" --> when a single letter (h) then it's an element
    //2. divide the head element (h) of a list from the tail --> appends an element at the beginning of a list
    //3. it goes through 1 by 1, e.g. taking out 1 (h::tail) and add the 1 at the end of the list (reverse (tail) ::: list(h)
    //create a new list and take first element of old list & put it to the end of the new list
    case h :: tail => reverse(tail) ::: List(h)
  }

  //tail recursive function
  def reverse2(l:List[Int]):List[Int] = List[Int] {
    def rev(ls: List[Int], outL: List[Int]): List[Int] = ls match {
        case Nil => outL
        case h :: tail => rev(tail, h :: outL)
      }
    rev(l, List())
  }

  def isPalindrome(list: List[Int]):Boolean = list match {
    //an empty list is always palindrome = true
    case Nil => true
    // a list that contains one element = true
    case List (a) => true
    //in the remaining cases (it's a list) head and tail of list have to be equal
    //and go through each element in the list and compare those until to the middle
    case _ =>  (list.head == list.last) && isPalindrome(list.tail.init)
  }

  //pack function with span --> from exercise
  def pack1[A](ls: List[A]): List[List[A]] = {
    ls match {
      case Nil => Nil
      case x::xs => {
        //span splits the list when e==x for example e== "a" then it splits out the a's and remains the rest of the list
        val (l, t) = ls.span((e => e == x))
        l :: pack1(t)
      }
    }
  }
  //nested functions pack and _pack
  def pack2[A](ls: List[A]): List[List[A]] = {
    @tailrec
    def _pack(res: List[List[A]], rem: List[A]): List[List[A]] = rem match {
      //if rem is empty return res (initial list)
      case Nil => res
      //extract the head element from the list and, if res is empty or res doesn't contain that element, append a new list
      //res.last.head is the first element of the last List stored in list-of-lists res
      case h::tail if (res.isEmpty || res.last.head != h) => _pack(res:::List(List(h)), tail)
      //extracted element is already in the last list or res, so we have to modify the latter.
      //Separate res in res.init and res.last, append h to res.last (res.last:::List(h)) and merge again the two pieces.
      case h::tail => _pack(res.init:::List(res.last:::List(h)), tail)
    }
    _pack(List(), ls)
  }

  //using the previous pack2 function, waaaay easier:
  def encode_pack[A](ls: List[A]): List[(Int, A)] = {
    pack2(ls).map(e => (e.length, e.head))
  }

  def encode[A](ls: List[A]): List[(Int, A)] = {
    @tailrec
    def _encode(res: List[(Int, A)], rem: List[List[A]]) : List[(Int, A)] = rem match {
      case Nil => res
      //convert each element (a list of equal elements) into a tuple (h.length, h.head)
      case h::tail => _encode(res:::List((h.length, h.head)), tail)
    }
    //go through pack list and apply _encode function
    _encode(List(), pack(ls))
  }

  //from exercise (functional solution)
  def decode_fill(ls: List[(Int, Char)]): List[Char] = {
    ls match {
      case Nil => Nil
      //fill method accepts as first parameter the length of the list and as second parameter the element to put into the list itself.
      //Scala tuples are indexed from 1 (lists are indexed from 0)
      case x::xs => List.fill(x._1) (x._2) ::: decode_fill(xs)
    }
  }

  //recursive solution
  def decode_rec[A](ls: List[(Int, A)]): List[A] = {
    @tailrec
    def _expand(res: List[A], rem: (Int, A)): List[A] = rem match {
      case (0,_) => res
      //convert a (Int, A) tuple into a List[A]
      case(n, h) => _expand(res:::List(h), (n-1, h))
    }
    //Each element is mapped into a list by the _expand() function, while flatMap() does the flattening job.
    ls flatMap { e => _expand(List(), e)}
  }
  }

  def main(args: Array[String]): Unit = {
    val ls = List(1, 2, 4, 6, 8)
    //println("Reversed list: " + reverse(ls))
    //println("Is the list a palindrome? " + isPalindrome(ls))
    println(encode(List('a', 'a', 'a', 'a', 'b', 'c', 'c', 'a', 'a', 'd', 'e', 'e', 'e', 'e')))
  }
}