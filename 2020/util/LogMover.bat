@echo off

:: check arguments
set argCount = 0
for %%x in (%*) do (
	set /A argCount+=1
)

if not %argCount% equ 2 (
	echo Usage: LogMover [usb/wifi] tarName
	exit
)

:: get correct ip for usb or wifi
if %1==usb (
	set ip=172.22.11.2
) else (
	set ip=10.44.88.2
)

:: tar the files, move the tar over, and push to github
cd %USERPROFILE%/git/RobotLogs/2020/
git checkout -b %2
ssh -t lvuser@%ip% 'tar -cvf %2.tar ./logs'
scp lvuser@%ip%:/home/lvuser/%2.tar ./%2.tar
git add ./%2.tar
git commit -m "%2 Log Files"
git push origin %2

:: clean up the rio
ssh -t lvuser@%ip% 'rm %2.tar; rm -rf ./logs/*'
