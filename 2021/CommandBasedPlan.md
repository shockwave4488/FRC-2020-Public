Command Based Framework Upper Architecture Plan:

    Robot.java makes an object of a new class RobotSelector which...
    1. Retrieves information on which robot we're using from PreferencesParser
    2. Then returns the correct RobotContainer object (based on which robot we're using)

    A single Robot.java operates using the RobotCotainer obtained from RobotSelector

Lower Architecture Plan:
    Ideas:
    1. Have a folder under robot that contains all the commands/subsystems for a robot. Ex: robot > blackout > commands
        Common classes would be in a central subfolder of robot
    2. Each robot has a folder under each common folder. Ex: robot > commands > blackout
        Common classes would be in their correct folder just outside of a certain robot
        Ex: DefaultWestCoastDrive being in robot > commands and used by multiple folders inside of commands
    
    Things to consider: 
      Some classes are used by multiple robots, where do you put these classes in each structure?
    
    Currently using idea #2
