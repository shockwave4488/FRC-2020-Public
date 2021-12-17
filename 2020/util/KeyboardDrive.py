from networktables import NetworkTables
import sys
import time
import logging
import keyboard

logging.basicConfig(level=logging.DEBUG)

if len(sys.argv) != 2:
    print("Specify an IP")
    exit(0)

ip = sys.argv[1]

NetworkTables.initialize(server=ip)
table = NetworkTables.getTable("SmartDashboard")

while True:
    table.putBoolean('keyboardDriveRunning', True)
    table.putBoolean('wPressed', keyboard.is_pressed('w'))
    table.putBoolean('aPressed', keyboard.is_pressed('a'))
    table.putBoolean('sPressed', keyboard.is_pressed('s'))
    table.putBoolean('dPressed', keyboard.is_pressed('d'))
    table.putBoolean('leftPressed', keyboard.is_pressed(75))
    table.putBoolean('rightPressed', keyboard.is_pressed(77))
    
