package BIDMTMath


object SimpleMath {

    /**
     * Example of pattern matching for lists.
    */
    def addOneToAll(l: List[Int]) : List[Int] = match l {
        case Nil => Nil
        case x::xs => x+1::addOneToAll(xs)
    }

    def add(x: Int, y: Int) : Int = {
        x + y
    }

    def main(args: Array[String]): Unit = {
        println(add(1,4))
    }
}
