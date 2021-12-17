'''
This program is intended to check for unsafe function calls that should not be used in 4488 SHOCKWAVE's codebase
it is designed to let a CI build reject it - thus reducing the amount of time spent peer reviewing code.

It can be run manually, you may setup a local git hook to run it for you, or it will always be run in TeamCity upon every check in.
'''

import fileinput
import glob
import re
import sys
import os
import fnmatch

unsafeFunctionCall = re.compile('(resetAll|resetEncoders|resetAngle)\(')
filesToCheck = []
badCodeFound = []

def findJavaFiles(pattern, path):
    result = []
    for root, dirs, files in os.walk(path):
        for name in files:
            if fnmatch.fnmatch(name, pattern):
                result.append(os.path.join(root, name))
    return result


def searchFile(paths):
    for path in paths:
        unsafe = unsafeFunctionCall.search(open(path).read())
        if unsafe != None:
            badCodeFound.append([[unsafe][0],[path]])

if __name__ == '__main__':
    res = findJavaFiles('*.java', os.path.dirname(os.getcwd()))
    print(res)
    searchFile(res)
    count = 0
    if len(badCodeFound) > 3:
        for badStuff in badCodeFound:
            print(badStuff)
        sys.exit(-1)
    else:
        sys.exit(0)