# Exercise 1 

This document is about the setup and execution of Scala in connection with SBT.
SBT is an abbreviation of Scala Build Tool and the purpose of using it is to manage a Scala project and its dependencies. You can read more about SBT at https://www.scala-sbt.org/1.x/docs/index.html. 
It is possible to install Scala and run it without using SBT, but a Java installation is still required because Scala runs on the Java Virtual Machine (JVM). The versions of Scala and Java have to match otherwise execution will fail. 

The following steps take you through how to install and execute a simple "Hello World" example and introduce a basic SBT project structure. 

## INSTALL

To execute the code for this course two things must be installed.
- Java version 8
- SBT 1.2.8

To verify if these are installed type these commands in the console:

for Java:

```bash
java -version
```
The Java version has to be 1.8.0_222

for SBT:
```bash
sbt sbtVersion
```
The SBT version has to be 1.2.8

### WINDOWS INSTALL

#### Java:

To remove older Java versions, open the "Apps & features" settings section.

Then find anything called "Java ... Development Kit"


Then install the Java Development Kit (JDK) 8 by visiting this link:
https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

And accepting the license terms. Then use the download link by the "Windows x64" line.

Install by executing the downloaded file.

#### SBT:

Install SBT by using this link: https://piccolo.link/sbt-1.2.8.msi

And executing the downloaded file.

for more info: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Windows.html

### LINUX INSTALL

#### Java:

To remove the old Java version, do the following in Terminal:

```bash
dpkg --list
```
find *OpenJDK* and remove all OpenJDK and other Java packages:

```bash
sudo apt-get purge remove openjdk* 
```

Install Java by executing the following in the Terminal:

```bash
sudo apt install openjdk-8-JDK
```

#### SBT:
```bash
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
```

for more info: https://www.scala-sbt.org/1.0/docs/Installing-sbt-on-Linux.html


### MAC INSTALL
Homebrew is a good package manager for macOS. 
If you do not have Homebrew installed, it can be installed by running the following (as specified at https://brew.sh): 

```bash
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

#### Java:
Remove old Java SDK: 

```bash
cd /Library/Java/JavaVirtualMachines/
```
Remove all files in the directory.

Install Java with Homebrew (as defined in https://stackoverflow.com/questions/24342886/how-to-install-java-8-on-mac): 
```bash
brew tap caskroom/versions
brew search java
```
This will display a list of older Java versions. We need Java 8, hence you type the following command:

```bash
brew cask install homebrew/cask-versions/adoptopenjdk8
```
Check that Java is installed by executing the version command: 

```bash
java -version
```

If you do not have Homebrew, download it manually from https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html. 


#### SBT: 
If you have Homebrew installed, then type the following in the Terminal (as specified at https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html):

```bash
brew install sbt@1
```

You may need to change ownership of some directories. If this is the case, Homebrew prints a guide for you in the Terminal. 



## IntelliJ
IntelliJ is an Integrated Development Environment (IDE) for Java and Scala.
You can write your Scala code, manage the SBT file and run your code in IntelliJ. 
The Community edition is sufficient for most purposes, but the Ultimate edition is also available to ITU students. 

### Installing IntelliJ & Activating License
1. Go to: https://www.jetbrains.com/idea/download/
2. Press "Download" on the version you are interested in (either "Ultimate" or "Community").
3. Press the logo that looks like a person in the upper right corner of the web page. 
4. Create a user or log into your existing user. 
5. Renew or request a student license. You will receive a mail from Jet Brains with a description of how to activate the license. 
6. When IntelliJ has been downloaded, open the program and log in to your JetBrains Account when prompted. 


### Using Scala in IntelliJ
1. Press "Create Project"
2. Give the project a name and make sure that the sbt version is 1.2.8 and the Scala version is 2.13.0.
3. Press the "New" button next to "JDK" and press "Open" in the Finder window. The JDK version should be 1.8.0_222 (this is the version you installed before). 
4. Press "Finish" and wait for the processes running in the lower-left corner of your window. 
5. Go to: https://docs.scala-lang.org/getting-started/intellij-track/getting-started-with-scala-in-intellij.html#writing-code and do as described in section "Writing Code", "Running It" and "Experimenting with Scala". When adding framework support, choose Scala SDK version 2.12.7. 


# EXECUTE
Now if everything has been installed correctly you are ready to execute some Scala code.

Open the Terminal/Console to the folder containing the build.sbt file (it should be in the same folder as this MD file). 

Execute the following command:

```bash
sbt run
```

The first time this command is run, it downloads Scala and locates all main functions in the src folder. If only one main function is found, the function is executed. 
The next time the command is run, it does not download Scala, but only executes the main function. 

The "sbt run" command prints a lot of information, which is sometimes relevant, but often it is not necessary. To suppress the information prints, the following command can be used instead: 

```bash
sbt --error run
```

If you want the program to be compiled and run every time you change a file use ~. This makes development slightly faster.

```bash
sbt --error ~run
```

# SBT Project Structure
The SBT project structure has three folders and an SBT-file:
```bash
+-- Project
|   # This folder contains project-specific settings.
|   # You probably don't need to look here.
+-- src
|   # This folder contains your source code
|   +-- main
|   |   +-- scala
|   |   # main and scala have to be there in the folder structure, 
|   |   # to specify that this is scala code.
|   |   |   +-- package_name
|   |   |   |   # This is the package name of your code. 
|   |   |   |   # You can nest this further if you like with multiple folders
|   |   |   |   Program_name.scala
|   |   |   |   # Your program code.
+-- target
|   # This contains the compiled code of your program.
|   +--  scala-2.13
|   |    # Your compiled jar file is here, name and version can be specifed in build.sbt
|   |    exercise1_2.13-1.0.jar
Build.sbt
# The main file that controls the project, 
# It controls the dependencies and project settings.
# It also marks the root of the project structure.
```


# SBT File
The SBT file contains specifications of different dependencies of the Scala project. 

The first line specifies the name of the project, which is "Exercise1".

The second line defines the version of the project, which is "1.0". 
This is defined by the developer, hence it could be called anything. 

The third line defines the Scala version, which in our case is "2.13.0".

The next line introduces initial commands that are executed whenever the console is launched. 

Other dependencies can be added to the SBT file and we will see this later in the course. 

# CODE!

## Scala Function
In this exercise, you are going to implement and run a function. 

1. Create a new package in this project or create your project with a single new package.
2. Create a new Scala file. 
3. Open the file and create a new object. 
4. Implement the main function for the object that prints a line. 

## Run Expressions in REPL
Scala expressions can be run interactively with REPL. You can read more about REPL at: https://docs.scala-lang.org/overviews/repl/overview.html

You can open REPL through SBT by running the following command in the path of your SBT file (as presented earlier in this document): 

```bash
sbt console
```

Run the expressions presented during the lecture and any other expressions you are curious to try. 

If you want to load a package you have in the project then:

```scala
// Import the package:
import package_name._
// Call the function:
Program_name.hello
```   

You can make the console do some commands at startup, like importing packages in the build.sbt file.
This functionallity is currently commented out, but if you renable it by edditing the build.sbt file then the "package_name" will be imported automatically in console mode.

## Recursion
You can choose to do either or both of the exercises. The code is also provided inside the "Program_name" file.

### Pascal's Triangle
Do you know Pascal's Triangle? 
Read about it here: https://www.mathsisfun.com/pascals-triangle.html

Implement a recursive pascal function defined by: 

```scala
// Take two numbers and add them like in pascals triangle
def pascal(c: Int, r: Int): Int = ???

// Print the triangle (use the other function)
def printPascal(h: Int): Unit = ???
```

### Factorial
Implement a tail recursive factorial function. The function is defined by:

```scala
import scala.annotation.tailrec
def factorialTR(n : Int) : Int = {
    //@tailrec
    //def factorial(n: Int, f: Int): Int = {
    //  ???
    //}
    ???
}
```

Hint: Use the keyword @tailrec before the function definition to ensure the compiler only accepts tail recursion.
