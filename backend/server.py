from flask import Flask
from flask import send_file
from flask import request
from werkzeug import secure_filename
import os
import subprocess
import json

app = Flask(__name__)
app.config['UPLOAD_FOLDER']='./pics'

@app.route("/")
def hello():
    return send_file('1392495957.png', mimetype='image/png')

@app.route('/upload', methods = ['GET', 'POST'])
def upload_file():
   if request.method == 'POST':
      f = request.files['file']
      filename = secure_filename(f.filename)
      f.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
      subprocess.call('th eval.lua -model ../../model_id1-501-1448236541.t7_cpu.t7 -image_folder {} -num_images 1 -gpuid -1'.format(app.config['UPLOAD_FOLDER'],filename),shell=True)
      data=json.load(open('vis/vis.json','r'))
      os.remove('{}/{}'.format(app.config['UPLOAD_FOLDER'],filename))
      return json.dumps(data)

if __name__ == "__main__":
    app.run(host='192.168.100.12')
