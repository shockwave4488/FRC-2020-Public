# Open Sourcing Code #

Prior to every build season, we must open source our code to ensure that is
publicly accessible to the community so that we may re-use the source code in
the following years.

Open Sourcing our source code has become a much more simple process due to code
review. We don't have to worry about anything inappropriate making it's way
into a comment or code. 

When open sourcing our code, remember that is it only pertains to code that
runs on the actual robot is what we need to open source. So technically we
don't need to share our build scripts, documentation, etc. You can simply copy
the directory FRC-Robot/Robot/src/main/java directory to a new git repository
and push that to a public repo which can then be shared on Chief Delphi.

The thing that we don't like to publish is the .git/ folder which contains all
of the git commit history. This file is always in the very top level of the
repository, so as long as we are just copying the source directory, we won't
have any of the old git history.

```
$ starting from just above the the FRC-Robot directory
$ mkdir FRC-Robot-20XX
$ cp -r FRC-Robot/Robot/src/main/java/* FRC-Robot-20XX
$ cd FRC-Robot-20XX
$ git init
$ git commit -am "Our 20XX source code for the robot <insert robot name>!"
$ git push git@github.com:shockwave4488/<Your new public repo>.git
```

If any code used comes from 3rd party sources (for example, another FRC team)
or uses their code in part, be sure to include their open source license
file. For an example, see the FRC-2018-Public repository, which contains code
from team 254. https://opensource.org/faq contains more useful info about
open source software.

And now share that URL with Chief Delphi, either as a white paper, or a post
with the rest of the CAD and other material we are sharing.
