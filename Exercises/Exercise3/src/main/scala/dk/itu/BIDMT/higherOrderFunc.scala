package dk.itu.BIDMT 


object higherOrderFunc {
    // Find the sum of the list
    // Use function fold or reduce
    def Sum(l: List[Int]):Int     = ???

    // Multiply all elements in the list
    // Use function fold or reduce
    def Product(l: List[Int]):Int = ???

    // Count the total number of words
    // Use functions split and map and reduce
    def WordCount(s: String):Int  = ???
    
    // Count the number of each individual word
    // Hint: you can map each word to a tuple of (word,1)
    // Methods to be used: split, map, groupby, mapValues, reduce
    def WordCountUniqe(s: String):Map[String,Int] = ???

}