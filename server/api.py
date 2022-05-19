from flask import Flask
app = Flask(__name__)

@app.route("/", methods=['GET'])
def appInformation():
	return "Welcome to Sub King POS"

app.run(port=443)