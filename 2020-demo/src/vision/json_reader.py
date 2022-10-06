'''
Formats json labels from WPIlib dataset to txt labels
'''

import json
import os

directory = 'YOUR_DIRECTORY'
out_dir = 'OUT_DIRECTORY'
max_height = 480
max_width = 640

for filename in os.listdir(directory):
    if filename.endswith('.json'):
        # parse json file to get object(s) coordinates(')
        full_path = os.path.join(directory, filename)
        y = []
        with open(full_path) as json_file:
            data = json.load(json_file)
            for o in data['objects']:
                x = o['points']['exterior']
                y.append(x)
        
        # Replaces .json tag with .txt tag
        txt_name = out_dir + '/' + filename[0:filename.index('.')] + '.txt'

        # Algorithm converts json coordinate format to txt coordinate format
        if len(y) is not 0:
            for r in y:
                x = (r[0][0] + r[1][0]) / 2.0
                y = (r[0][1] + r[1][1]) / 2.0
                width = r[1][0] - r[0][0] * 1.0
                height = r[1][1] - r[0][1] * 1.0

                x = x / max_width
                y = y / max_height
                width = width / max_width
                height = height / max_height

                with open(txt_name, 'w') as txt_file:
                    in_str = '0' + ' ' + str(x) + ' ' + str(y) + ' ' + str(width) + ' ' + str(height)
                    txt_file.write(in_str)
                
