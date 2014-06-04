Calculator
========================================================================

To compile the code from the src directory:

  $> mvn clean package
  
To run the tests from the target directory:

  $> mvn exec:java -Dexec.args="-ea" -Dexec.classpathScope="test" -Dexec.mainClass="timezra.calculator.Test"
  
To see the Usage message and examples from the src directory:

  $> mvn exec:java -Dexec.args="'add(1, 2)'" -Dexec.mainClass="timezra.calculator.Main"
  
Assumptions
------------------------------------------------------------------------

* The code uses Java 8 and depends on features specific to Java 8. The use of Lambdas significantly reduced the amount of boilerplate that the creation of individual ASTNode classes would have required.
* In order to eliminate the need for 3rd-party libraries, I implemented a barebones microtesting framework in order to automate the running of test cases in order to drive the design of the application.
* In order to handle cases of Integer overflow or underflow and to handle the loss of precision in division operations that might happen for intermediate integer expression calculations, I implemented a custom Rational number class that is reduced only after all calculations are finished.
