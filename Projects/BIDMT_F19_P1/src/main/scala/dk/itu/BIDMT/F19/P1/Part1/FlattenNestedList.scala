package dk.itu.BIDMT.F19.P1.Part1

object FlattenNestedList {

  /**
    * Given a list of nested list, flatten that list into its components
    * Notes:
    * (1) the input list can contain elements or nested lists
    * (2) all the elements in the list and  nested lists should be of the same type
    * (3) you should not use code from scala libraries to flatten the list, this means that you should not use, flat, flatten, or flatmap
    * @param nestedList : input list of nested lists
    * @return : flattened list
    */
  def flatten (nestedList : List[Any]) : List[Any] =  ???

  def main(args: Array[String]):Unit = {
    val ls = List(List(1, 1),List(2,2), 2, List(List(10, 10), 20) ,List(3, List(5, 8)),3)
    println("Flattened list: "+ flatten(ls))
  }
}
