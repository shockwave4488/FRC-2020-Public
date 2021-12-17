# Definition of done #

This doc aims to describe what qualifies as "done", meaning that it can be checked into master.

All code in master must first pass the criteria listed below:

- Compile
- Thoroughly tested
- Formatted
- Documented
- Code reviewed

### Compile ###
All code should compile with no errors. There may currently be some warnings that pop up, but your
code should not introduce any new warnings.

### Tested ###
Code should be run on actual hardware whenever possible. During the first few weeks of the build
season, there will not be a robot to test on, but your code should be loaded onto a RoboRio, and
teleop mode should run just fine without any null pointer exceptions or warnings popping up. These
execptions should be handled by source code checking if devices actually exist (i.e. CAN bus),
before attempting to create a new device on them.

### Formatted ###
Source code and documentation should follow the format defined in coding-style-guide.md.

### Documented ###
You should always ask yourself, did I add enough comments, and does my code need its own whitepaper?

### Code Review ###
All source code must pass through code review via github. When you are ready for others to review
your code, please push it to your feature branch, create a pull request on github, and add the
appropriate people needed to the review.

A minimum of 1 person - other than the author, must review the code. We would rather have at least 3
sets of eyes on each code review, so please feel free to assist!

Code in review should not be pushed to master any sooner than 12 hours after the pull request was
created. This will give ample time to mentors and students for reviewing your source code.

Exceptions may apply, but this is at the discretion on the programming lead mentor.

Code should never be pushed through review quickly just because "we have a match coming up", or "the
team needs it now". We will hold ourselves to a high standard, giving us a better product in the
long run.

Code may be added at competition on the fly, but primarily should simply be tuning values or minute
bug fixes.  Any large changes at competition must be thoroughly tested to ensure we don't break
anything else in an attempt to fix something. The drive team is always happier when we ask them to
simply take another strategy for a match or two, while we properly investigate, fix, and test a bug
fix - this is much better than taking wild guesses in the dark attempting to fix things.
