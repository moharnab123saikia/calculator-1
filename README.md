Calculator
========================================================================

To compile the code from the src directory:

  $> javac calculator/Main.java calculator/Test.java
  
To run the tests from the src directory:

  $> java calculator.Test
  
To see the Usage message and examples from the src directory:

  $> java calculator.Main
  
Assumptions
------------------------------------------------------------------------

* Since no Java version has been specified, the code uses Java 8 and depends on features specific to Java 8. The use of Lambdas in the case of this assignment significantly reduced the amount of boilerplate that the creation of individual ASTNode classes would have required.
* Since no example of subtraction was provided, the second argument is assumed to be the subtrahend, while the first is the minuend.
* Since no 3rd-party libraries were permitted, I implemented a barebones microtesting framework in order to automate the running of test cases in order to drive the design of the application.
* In order to handle cases of Integer overflow or underflow and to handle the loss of precision in division operations that might happen for intermediate integer expression calculations, I implemented a custom Rational number class that is reduced only after all calculations are finished.
