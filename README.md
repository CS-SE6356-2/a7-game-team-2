# Go Fish

### Team: 
Antonio

Cameron

Chris

Matthew

## Compilation Instructions:
1. Make sure you have a Java Development Kit (JDK) installed.

	a) Find the JDK (should be in c:\Program Files\Java)
	
	b) Add JDK to path (set path=%path%;<path-to-jdk>\jdk-9.0.1\bin)
	
2. Change Directory to the location of this project.

### To compile and run the project: 
```
javac src\view\ClientLauncher.java
java -cp src.controller.ClientLauncher
```
 
### To compile and run Tests:
```
javac -cp tests model\<name-of-test>Test.java
java -cp tests;src model.<name-of-test>Test
```