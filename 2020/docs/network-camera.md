# Network Camera #

Using a network IP camera to stream video may seem old school in FRC, but it
does free up the roborio from having to process any additional workloads to
stream video.

It does however eat up a port on the wifi router (meaning you need a hub since
you definitely NEED to be able to tether in to the network to troubleshoot the
camera and/or the roborio).

Caveats about using the webcam:
- You must assign it a static IP to use with smartdashboard
- Don't go above 10.44.88.50. Anything above, especially getting into the
  X.X.X.200 range is for use by FRC only
