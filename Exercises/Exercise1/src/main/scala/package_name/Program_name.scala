package package_name

import scala.annotation.tailrec

object Program_name {

    // This is the program entry point
    def main(args: Array[String]): Unit = {
        //printPascal(3)
        //factorialTR(5)
        factorial_diff(5)
    }

    def pascal(c: Int, r: Int): Int = {
        //get a 1 in first column and/or the last column of the row
        if (c == 0 || c == r) 1
        else {
            //Each subsequent row of Pascal's triangle is obtained by adding the two entries diagonally above
            //this calculation comes from the binomial coefficient identity (see calculations in OneNote)
            pascal(c - 1, r - 1) + pascal(c, r - 1)
        }
    }

    // x defines the number of rows
    def printPascal(x:Int): Unit = {
        println("Pascal's Triangle")
        //first loop through my rows and start at index 0
        for (row <- 0 to x) {
            //then loop through columns depending on the number of rows
            //always calculate the pascal triangle numbers through calling the function pascal()
            for (col <- 0 to row)
                print(pascal(col, row) + " ")
            println()
        }
    }

    //non tail-recursive function:
    def factorial_diff(n: Int): Int = {
        if (n == 0) 1
        else factorial_diff(n - 1) * n
    }

    //turn above function into tail recursion:
    //f = result
    def factorialTR(n: Int): Int = {
        @tailrec
        def factorial(n: Int, f: Int): Int = {
            if (n == 0) f
            else factorial(n-1, n*f)
        }
        factorial(n, 1)
    }
}
