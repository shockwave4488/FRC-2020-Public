# Pre-competition Checklist

Before each competition, there are many things we need to check on the robot during the week leading up to competition. This helps prevent issues from showing up at the worst times.

## Administrative

 - [ ] All controller mappings up to date
 - [ ] Check the driver station for Windows updates

## Robot tasks

 - [ ] Auto routine selector tested with all routines
 - [ ] All sensors tested

## Code quality

 - [ ] Run robot code for ~15min without restarting
 - [ ] Check that there are no unexpected loop overruns
 - [ ] Use VisualVM to check RoboRIO CPU and memory usage (should be stable)
 - [ ] Check the aborts on the routines so we never get stuck

## Logging

 - [ ] Ensure every subsystem has its Trackables enabled
 - [ ] Collect a log file that emulates a match from driver practice
 - [ ] Give example log file to strategy programming
 - [ ] Run our log analysis tools on the example log file to ensure compatibility
 
