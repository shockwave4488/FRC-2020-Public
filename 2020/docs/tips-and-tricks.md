# Tips and Tricks
This document contains quick tips and tricks accumulated over the years - "tribal knowledge", as it
is often referred to.

## Working with WPILib
- WPILib documentation can be found [here](https://wpilib.screenstepslive.com/).
- VSCode will set up your coding environment Bash automatically. You can avoid annoying setup issues
by using the Bash terminal in VSCode to build and deploy your robot code.

## Working with the roboRIO
The IP address/Host name of the roboRIO changes depending on how you are connecting to it.
- USB: 172.22.11.2
- WiFi: 10.44.88.2 if roboRIO is set up for a static IP
- WiFi: Use the hostname roboRIO-4488-FRC.local instead of the IP address if dynamic

You can log in to the roboRIO's Linux OS using `ssh`. There are two users, `admin` and `lvuser`. The robot
code runs as `lvuser`. `ssh` is included in Git Bash, or can be run from Linux/macOS natively.
Note that you will need to replace 172.22.11.2 if you are connected over WiFi.

```bash
ssh lvuser@172.22.11.2
```

You can also use the `scp` utility to copy files from the roboRIO to your computer. Just like `ssh`,
`scp` needs to be run from your computer, not from the roboRIO. `/path/to/file` is the path to the
file on the roboRIO, `path/to/copy/to` is the path on your local computer to copy the file to.
If you want to copy over a whole folder from the roboRIO, use the `-r` flag.

```bash
scp lvuser@172.22.11.2:path/to/file path/to/copy/to
scp -r lvuser@172.22.11.2:path/to/folder path/to/copy/to
```

Note that paths on the RoboRIO follow Linux/Unix conventions. If you start with a `/`, it will be 
the absolute path. If you do not, it will be relative to the home directory of the user you logged
in with.

## Code architecture
TBD

## Logging
TBD

## Controllers
- The D-Pad wasn't very reliable on Xbox controllers. If D-Pad use is added, ensure it is thoroughly
 tested before relying on it for a competition.
 
