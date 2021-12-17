'''
Reformat txt labels to work with the google cloud vision
'''

import os
import csv
from random import random

directory = 'YOUR_DIRECTORY'
folder = 'gs://BUCKET_NAME/FOLDER'
data = []

for filename in os.listdir(directory):
    if (filename.endswith('.txt')):
        ### read data ###
        full_path = os.path.join(directory, filename)
        with open(full_path, 'r') as filedata:
            txt = filedata.read()

        ### compute data ###
        line_data = txt.split()

        if (len(line_data) != 5):
            continue

        img = folder + filename[0:filename.index('.')] + '.jpg'

        objType = "PowerCell"

        Cx = float(line_data[1])
        Cy = float(line_data[2])
        width = float(line_data[3])
        height = float(line_data[4])
      
        TLx = Cx - (0.5 * width)
        TLy = Cy - (0.5 * height)
        TRx = Cx + (0.5 * width)
        TRy = Cy - (0.5 * height)
        BRx = Cx + (0.5 * width)
        BRy = Cy + (0.5 * height)
        BLx = Cx - (0.5 * width)
        BLy = Cy + (0.5 * height)
        
        data_type = ''
        num = random()
        if num > 0.85:
            data_type = 'VALIDATE'
        elif num > 0.70:
            data_type = 'TEST'
        else:
            data_type = 'TRAIN'

        data.append([data_type, img, objType, TLx, TLy, TRx, TRy, BRx, BRy, BLx, BLy])

 ### add data ###
with open('NAME.csv', 'w') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerows(data)
