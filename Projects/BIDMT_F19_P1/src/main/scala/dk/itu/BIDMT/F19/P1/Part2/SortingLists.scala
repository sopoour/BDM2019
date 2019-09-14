package dk.itu.BIDMT.F19.P1.Part2

object SortingLists {

  /**
    *
    * Sort the input listOfLists in an ascending order  based on the length of each sub-list and return the sorted list
    *
    * For example, the sorting of the list: List(List('a', 'b', 'c'), List('d', 'e'), List('f', 'g', 'h'), List('d', 'e'), List('i', 'j', 'k', 'l'), List('m', 'n'), List('o'))
    * should be: List(List(o), List(d, e), List(d, e), List(m, n), List(a, b, c), List(f, g, h), List(i, j, k, l))
    */
  def sortListLength(listOfLists: List[List[Char]]): List[List[Char]] = {
    //sort list from small length to bigger length
    listOfLists.sortWith {
      _.length < _.length
    }
  }

  /**
    * Sort the sublists of the input list in an ascending order  based on the frequency of the lengths of these sublists.
    *
    * For example, the sorting of the list: List(List('a', 'b', 'c'), List('d', 'e'), List('f', 'g', 'h'), List('d', 'e'), List('i', 'j', 'k', 'l'), List('m', 'n'), List('o'))
    * should be: List(List(i, j, k, l), List(o), List(a, b, c), List(f, g, h), List(d, e), List(d, e), List(m, n))
    *
    * since:
    *   freq of sub-lists of length 4 is 1
    *   freq of sub-lists of length 1 is 1
    *   freq of sub-lists of length 3 is 2
    *   freq of sub-lists of length 2 is 3
    *
    *  Hint:
    *  Follow the following steps:
    *   - pair each sub-list with its length
    *   - find the freq of each length, you can use groupby for that
    *   - sort the groups according to the number of sub-lists in each group
    *   - generate the final sorted list
    */
  def sortListFreq(listOfLists: List[List[Char]]): List[List[Char]] = {
    //1. groupBy: Group my sub-lists by its length
    //2. toList: Push these grouped sub-lists into a new list
    //Evaluation 1+2: List((1,List(List(o))), (2,List(List(d, e), List(d, e), List(m, n))), (3,List(List(a, b, c), List(f, g, h))), (4,List(List(i, j, k, l))))
    //3. map(x=>x._2): Get only the second index (the actual lists without the index number)
    //Evaluation 3: List(List(List(o)), List(List(d, e), List(d, e), List(m, n)), List(List(a, b, c), List(f, g, h)), List(List(i, j, k, l)))
    //4. sortBy(_.length): now the length of "Evaluation 3" list is basically the frequency of how often each list appears
    //Evaluation 4: List(List(List(o)), List(List(i, j, k, l)), List(List(a, b, c), List(f, g, h)), List(List(d, e), List(d, e), List(m, n)))
    //5. flatten: make the list flatter by removing redundant nested lists
    listOfLists.groupBy(_.length).toList.map(x => x._2).sortBy(_.length).flatten
  }

  def main(args: Array[String]):Unit = {
    val ls = List(List('a', 'b', 'c'), List('d', 'e'), List('f', 'g', 'h'), List('d', 'e'), List('i', 'j', 'k', 'l'), List('m', 'n'), List('o'))
    println("Sorted list according to lengths of sublists: "+ sortListLength(ls))
    println("Sorted list according to frequency of lengths of sublists: "+ sortListFreq(ls))
  }
}
