# 4488 Shockwave coding style guide #
This document will be used as a reference for all good practices that should be followed when
writing software for the team. This doc may be referenced during code review for ease of explanation
as to why we do or do not allow certain constructs and paradigms into our source code. Any common
issues found in code review should have a special note here to avoid further mistakes.

*Note* - At the time of writing this document, a lot of the source code does not follow these
conventions. During any given pull request, you may be asked to fix some of the surrounding code,
even if you did not write it in the first place.

This is a live document so please feel free to push a pull request.

### Google Java Style Guide ###
A great reference that we should adhere to is the Google style guide:
https://google.github.io/styleguide/javaguide.html

All code will be run against the
[google-java-formatter](https://github.com/google/google-java-format)

The GJF has been integrated into our Gradle build flow using the [GoJF Gradle plugin](https://github.com/sherter/google-java-format-gradle-plugin)

You can run the formatter by running:
```bash
    $ ./gradlew GoJF
```
This will automatically format all of your code. You can also do a verification by running
```bash
    $ ./gradlew verGJF
```
The verification step will run whenever you run `gradlew build`, so if you see a failure, simply run `GoJF` to fix it.

To manually run the formatter in bash (Note - this takes up to 3 minutes):

    $ curl -L -O https://github.com/google/google-java-format/releases/download/google-java-format-1.5/google-java-format-1.5-all-deps.jar
    $ find . -name "*.java" -exec java -jar google-java-format-1.5-all-deps.jar -i {} \;

### File names ###
Source code file names should be Upper camelCase, no underscores.

Documentation file names should be all lower case with dashes.

### Functions ###
Functions must be lower camelCase, no underscores.

### Variables ###
Please define variables that are self documenting and easy to read. Single character variables are
not allowed. WPILib variables should follow a hungarian-like notation. E.g.

    WPI_TalonSRX m_masterMotor;

They must be lower camelCase.

All magic numbers must be defined as an immutable variable. I.E.

	static final int NUMBER = 5;

*Note* many variables do not follow this convention and slowly need to be cleaned up.

### Lines ###
Please limit all source code lines to under 100 characters. You can adjust your IDE if needed as to
auto format your code for you.

Make sure to not check in new lines with your commits. Before you push code to your feature branch,
type "git diff" or "git show" to see if there are any new lines lingering. When you do push your
code and create a pull request, please look over the source code to ensure you didn't check in
anything unintentionally.

### Comments ###
Comments are an integral part of software - we should always write code for the reader, meaning your
code should be self documenting. Good variable names, easy to read constructs, and good comments for
when things get freaky. If you have the option between writing less code, and writing readable code,
always choose readable code - As long as it does not effect efficiency. You will not be here in 4
years or less, so please leave behind source code that is easily readable by others.

Use JavaDoc as the way to document classes and methods. The description provided in the document
['How to Write Doc Comments for the Javadoc Tool'](https://www.oracle.com/technetwork/java/javase/documentation/index-137868.html)
is a good reference for beginners.

### Patch sets ###
Patch sets (code commits) should attempt to be under 200 lines of code. I know it can be difficult
sometimes when adding an entire module, but if you commit early and often (which is good practice),
this should be no trouble at all.

The reason for this is that code takes time to review, and the simpler a commit is, the easier and
faster it is to get it into master.

### Documentation ###
Depending on the complexity of the code you are writing, you may be asked to add documentation to
google drive, or a markdown.md page in this directory.

### Dangerous Functionality ###
Sometimes there is *dangerous* functionality in the robot code. It should always be well
documentation so that others do not attempt to use it outside the scope it ought to be used. One of
those dangerous functions is a wait(); Adding arbitrary waits to code can lead to undefined
behavior, race conditions, holding threads hostage, etc. Try to find a way around them whenever
possible. If you must use them, attempt to use them use them in such a way to make the reader aware
they are present (more than just a comment).

A good example would be adding a wait to an action. If you have series of actions in a routine, try
to leave the wait() outside of the action itself, and move it to the routine. That way the reader
will know we are explicelty calling a wait inbetween two other serial actions. Helping to avoid any
confusion or frustration.

### Other ###
When in doubt, ask questions.
