# README Exercise 2
This exercise session includes some more tips about working with SBT followed by Scala programming exercises.

## Run SBT with Arguments
Running SBT one time with arguments "arg0" "arg1" "arg2":

```bash
sbt "run arg0 arg1 arg2"
```

Another way is to first open the SBT shell and then pass the run command including the arguments to the executor:

```bash
> sbt
> run arg0 arg1 arg2
```

This is a better option if you are running several successive commands.

## Execute SBT Command on Save
By adding ~ before an SBT command, the command will be executed every time a file in the project is saved. For instance, execute the run command on save:

```bash
sbt "~run arg0 arg1 arg2"
```

## SBT Dependencies
SBT defines something called "Managed Dependencies" which enables it to download the specified libraries. Last week we had no library dependencies, but if you look in the SBT file this week, it has the keyword "libraryDependencies".
Adding library dependencies has the following format:

```sbt
libraryDependencies += groupID % artifactID % revision
```

The names groupID, artifactID and revision are all defined as strings meaning that they are written with "".
An example of how to add the scalactic library is seen in the SBT file:

```sbt
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
```

See this link for more information about library dependencies with SBT: https://www.scala-sbt.org/1.x/docs/Library-Dependencies.html


### SBT Clean
It is possible to delete all generated files by running:

```bash
sbt clean
```

## SBT Define Main Function
It is possible to define which main function is used when "sbt run" is executed. This is done by adding the following to the SBT file:

```sbt
mainClass in (Compile, run) := Some("path")
```

Here, "path" needs to be replaced with the path to the package where the Scala file with the main function is.
For instance, choosing the main function in SimpleMath.scala:

```sbt
mainClass in (Compile, run) := Some("BIDMTMath.SimpleMath")
```

# Problems
Add a new package to this project and a new file within that package.
In each problem, you need to implement a function in your new file that takes a certain input and returns a certain output. 
Use pattern matching whenever possible to become better at doing pattern matching. 

The problems are specified in the following:

## 1. P05 - Reverse a list
```scala
reverse(List(1, 1, 2, 3, 5, 8))
res0: List[Int] = List(8, 5, 3, 2, 1, 1)
```

## 2. P06 - Find out whether a list is a palindrome
```scala
isPalindrome(List(1, 2, 3, 2, 1))
res0: Boolean = true
```

## 3. P09 - Pack consecutive duplicates of list elements into sublists
If a list contains repeated elements they should be placed in separate sublists.

```scala
pack(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e))
res0: List[List[Symbol]] = List(List('a, 'a, 'a, 'a), List('b), List('c, 'c), List('a, 'a), List('d), List('e, 'e, 'e, 'e))
```

## 4. P10 - Run-length encoding of a list
Use the result of problem P09 to implement the so-called run-length encoding data compression method. Consecutive duplicates of elements are encoded as tuples (N, E) where N is the number of duplicates of the element E.

```scala
encode(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e))
res0: List[(Int, Symbol)] = List((4,'a), (1,'b), (2,'c), (2,'a), (1,'d), (4,'e))
```

## 5. P12 - Decode a run-length encoded list
Given a run-length code list generated as specified in problem P10, construct its uncompressed version.

```scala
decode(List((4, 'a), (1, 'b), (2, 'c), (2, 'a), (1, 'd), (4, 'e)))
res0: List[Symbol] = List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)
```

The problems are p05, p06, p09, p10 and p12 from the following link: http://aperiodic.net/phil/scala/s-99/