package dk.itu.BIDMT.F19.P1.Part3

object HuffmanCoding {

  /**
    * The Huffman coding is represented as a tree where:
    * * each leaf representing a symbol; we also store the frequency of that symbol
    * * each non-leaf represents all the symbols stored in the sub-tree rooted by it and the sum of the frequencies of these symbols
    */
  abstract class HuffmanCodingTree{
    def chars : List[Char]
    def nodeWeight : Int
    def printTree : Unit
  }

  case class HuffmanCodingTreeLeaf(symbol: Char, weight: Int) extends HuffmanCodingTree{
    def chars = List(symbol)
    def nodeWeight = weight
    def printTree = println("LEAF: synmbols: " + List(symbol) + " weight: " + weight)
  }
  case class HuffmanCodingTreeNonLeaf(symbols: List[Char], weight: Int, left: HuffmanCodingTree, right: HuffmanCodingTree) extends HuffmanCodingTree{
    def chars = symbols
    def nodeWeight = weight
    def leftChild = left
    def rightChild = right
    def printTree = {
      println("NonLEAF: synmbols: " + symbols + " weight: " + weight)
      print("Left Subtree: ")
      left.printTree
      print("Right Subtree: ")
      right.printTree
      println()
    }
  }

  /*---- Start: Helper functions ----*/

  /**
    * Given a list of characters, group characters together and assign for each character found in the list a frequency
    * @param text : list of characters
    * @return a list of pairs, where each pair is a character and its frequency in the input list
    */
  def extractCharFrequencies(text: List[Char]): List[(Char, Int)] = {
    //1. map to each char the total frequency of that char in the list = make tuples of (char, frequency of char)
    //output:  List((A,8), (A,8), (A,8), (A,8), (A,8), (A,8), (A,8), (A,8), (B,3), (B,3), (B,3), (C,1), (D,1), (E,1), (F,1), (G,1), (H,1))
    //2. distinct: remove duplicates
    //final output: List((A,8), (B,3), (C,1), (D,1), (E,1), (F,1), (G,1), (H,1))
    text.map((c: Char) => (c, text.count(_ == c))).distinct
  }

  /**
    * Given a list of pairs of characters and their frequencies, return a list of HuffmanCodingTree nodes for each character
    * the weight of each node is the corresponding frequency for that character
    * @param charFreqs : list of pairs of characters and their frequencies
    * @return list of HuffmanCodingTree nodes, where each node represent a character from the input list
    */
  def makeTreeLeaves(charFreqs: List[(Char, Int)]): List[HuffmanCodingTreeLeaf] = {
    //1. map: pass the charFreqs' tuples to the case class HuffmanCodingTreeLeaf (=generate list of HuffmanCodingNodes)
    //2. sort the returned list based on weight of leaf (= frequency of character)
    //it's in ascending order = the head of the list has the smallest weight
    charFreqs.map(y => HuffmanCodingTreeLeaf(y._1, y._2)).sortBy(x => x.weight)
  }

  /**
    * Given two HuffmanCodingTree nodes, merge their info into one HuffmanCodingTree node
    * @param a : a HuffmanCodingTree node
    * @param b : a HuffmanCodingTree node
    * @return a new non-leaf HuffmanCodingTree node
    */
  def makeNonLeaf(a: HuffmanCodingTree, b: HuffmanCodingTree): HuffmanCodingTree = {
    //1. merge the chars from b and c to {b,c}
    //2. sum up the weights
    HuffmanCodingTreeNonLeaf(a.chars ::: b.chars, a.nodeWeight + b.nodeWeight, a, b)
  }

  /**
    * Insert a HuffmanCodingTree node into a list of HuffmanCodingTree nodes. Make sure that the nodes are in an  ascending ordered in the list
    * according to their frequencies
    * @param treeNode : new HuffmanCodingTree to insert in the list
    * @param listTreeNodes : list of HuffmanCodingTree nodes
    * @return a list of HuffmanCodingTree nodes sorted in an ascending order according to their frequencies
    */

  def insertAsc(treeNode: HuffmanCodingTree, listTreeNodes: List[HuffmanCodingTree]): List[HuffmanCodingTree] = listTreeNodes match {
    case Nil => List(treeNode)
    //in case it's a normal list we have two sub-cases:
    case y :: ys =>
      //we compare the new node with the existing list of nodes and if its weight is smaller than the head node's in that list we insert it as a new head
      if (treeNode.nodeWeight <= y.nodeWeight)
        treeNode :: listTreeNodes
      //otherwise (=node has bigger weight) keep the existing head and do insertAsc allover again until I reach my if statement
      else
        y :: insertAsc(treeNode, ys)
  }



  /**
    *  If there is only one HuffmanCodingTree node in the list, return that node
    *  else,
    *  find two nodes with the lowest frequencies,
    *  merge these two nodes into one tree by creating a new tree node with these nodes are its children,
    *  remove those two nodes from the list and add the newly created tree node
    *  repeat the above steps
    *
    * @param treeLeaves list of HuffmanCodingTree nodes
    * @return a HuffmanCodingTree node representing the root of the tree
    */
  def generateTree(treeLeaves: List[HuffmanCodingTree]): HuffmanCodingTree = treeLeaves match {
    //If empty list throw me an error
    case Nil => throw new Error("empty")
    // if just one node and nothing else (Nil) then return that node
    case x :: Nil => x
    // if I have two nodes which are aimed to be merged (x, y) and another node (z)
    //1. makeNonLeaf: Make out of these two nodes (x, y) a non-leaf
    //2a. This node (nonLeaf) is then added back into the remaining elements of treeLeaves (wrapped in generateTree)
    //2b. It is inserted at a position such that the ordering by weights is preserved (insertAsc())
    case x :: y :: z => generateTree(insertAsc(makeNonLeaf(x, y), z))
  }


  /*---- End: Helper functions ----*/



  /**
    * Constructing the Huffman tree from a list of characters
    *
    * Use the following steps
    * 1. Generate a list of all the characters appearing in the $text$ and  pair each character with its frequency based on how many times it appears in the text.
    * 2. From the above created list, generate a list of  HuffmanCodingTree nodes. Initially, this list contains a leaf tree node for each character in the text.
    * 3. generate a tree from the list of HuffmanCodingTree nodes and return the root of that tree
    *
    * @param text : list of chars
    * @return the root node of the Huffman Tree
    */

  def createHuffmanTree(text: List[Char]): HuffmanCodingTree = {
    //create tree (generateTree) out of the tree leaves (makeTreeLeaves) which have the frequencies extracted (extractFrequencies)
    generateTree(makeTreeLeaves(extractCharFrequencies(text)))
  }

  /*---- Encoding a message ----*/

  /**
    * Encoding a message
    *
    * Replace each character in message with each code in the tree
    */
  /**
    * Given the root of a HuffmanCodingTree and a character, find the code representing this char in the tree
    * @param tree : the root of a  HuffmanCodingTree
    * @param c : a character to find its code in the tree
    * @return : a list of 1s and 0s representing the code of the input character in the Huffman tree
    */
  def encodeChar(tree: HuffmanCodingTree, c: Char): List[Int] = tree match {
    //in case my tree matches only leaves (C1, B1 etc) then print me an empty list (basically the root of the tree)
    case t: HuffmanCodingTreeLeaf => List()
    case t: HuffmanCodingTreeNonLeaf =>
      //check if there is a char on the left side, if so then insert a 0 for the left char in my encodeChar list
      if (t.left.chars.contains(c))
        0 :: encodeChar(t.left, c)
      // otherwise insert a 1 for a right char
      else
        1 :: encodeChar(t.right, c)
    //case HuffmanCodingTreeNonLeaf(_,_, left, right) if left.chars.contains(c) => 0 :: encodeChar(left, c)
    //case HuffmanCodingTreeNonLeaf(_,_, left, right) => 1 :: encodeChar(right, c)
  }

  /**
    * Given the root of a HuffmanCodingTree and a string represented as a list of characters, generate the code for
    * that string.
    * You will probably need to call encodeChar for each character in that string
    * @param tree  : the root of a  HuffmanCodingTree
    * @param message : a string represented as a list of characters
    * @return : list of 1s and 0s representing the code for all the characters in the input message
    */
  def encode(tree: HuffmanCodingTree, message: List[Char]): List[Int] = message match {
    case Nil => Nil
    // in any other case, append the encoded char list for each letter to the encode list
    // until I have a whole list of 0s & 1s
    case x :: xs => encodeChar(tree, x) ::: encode(tree, xs)
  }

  /*---- Decoding a code into a message ----*/

  /**
    * Given the root of a HuffmanCodingTree and a list of 1s and 0s representing the code for a character,
    * traverse the tree guied by the code to find the character that it represent
    * @param tree  : the root of a  HuffmanCodingTree
    * @param code : a list of 1s and 0s representing the code of a message
    * @return : a pair, its first element of the pair is a character that is found,
    *         and the second element of the pair is a code  (list of 1s and 0s) after removing
    *         the code of the found character from the input code
    *
    *         Example: using tree in Figure 1, calling getCharCode for List(0 , 1, 0, 1, 1, 1, 0, 1, 1)
    *         the returned value is the pair ('a' , List( 1, 0, 1, 1, 1, 0, 1, 1))
    */
  def getCharCode(tree: HuffmanCodingTree, code: List[Int]): (Char, List[Int]) = tree match {
    //cases are going from root to the top of the tree
    // Two cases: Leaf (=Root/end of the tree) and Non-Leaf
    //Case 1: Leaf
    case HuffmanCodingTreeLeaf(c, _) =>
      //if there is no code then return just the char and empty list
      if (code.isEmpty)
        (c, List())
      //otherwise print the char and the code list
      else
        (c, code)
    //Case 2: Non-Leaf
    case t: HuffmanCodingTreeNonLeaf  =>
      //if the head of the code is 0 then return the left node of the tree for char and the remaining code
      if (code.head == 0)
        getCharCode(t.left, code.tail)
      //otherwise (code.head == 1) return the right node of the tree for char and the remaining code
      else
        getCharCode(t.right, code.tail)
  }

  /**
    * Given the root of a HuffmanCodingTree and a list of 1s and 0s representing the code for a message, decode that code
    * into a message represented as a list of characters
    * You will probably need to call getCharCode several times to achieve that
    *
    * @param tree : the root of a  HuffmanCodingTree
    * @param code : a list of 1s and 0s representing the code of a message
    * @return : a list of characters representing the message that was decoded
    *
    *         Example: using tree in Figure 1, calling decode for List(0 , 1, 0, 1, 1, 1, 0, 1, 1)
    *         the returned value is List( 'a', 'd', 'd')
    */
  def decode(tree: HuffmanCodingTree, code: List[Int]): List[Char] = code match {
    case Nil => Nil
    //extract the first index of the getCharCode tuple (= char) and insert it into my wanted char list
    //need to add the "xs" as a stop otherwise the loop is endless!!
    case _ :: xs => getCharCode(tree, code)._1 :: decode(tree, xs)
  }



  def main(args: Array[String]):Unit = {
    val tree = createHuffmanTree(List('A','A','A','A','A','A','A','A','B','B','B','C','D','E','F','G','H'))
    println("Generated Tree:")
    //for testing purpose !
    tree.printTree

    //test encoding a message
    val encodedMsg = encode(tree, "ABCAH".toList)
    println("encoding of msg: ABCAH = "+encodedMsg)

    //test decoding a code into its corresponding message
    val decodedMsg = decode(tree, List(0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0))
    println("decoding of the above msg: "+ decodedMsg)

  }
}
