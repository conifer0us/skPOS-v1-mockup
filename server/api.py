from flask import Flask, render_template, request, send_file
import hashlib
from os.path import exists

# Change to Alter Password Salt. Admin accounts will lose access if this is done with passwords already set.
salt = "naohoinvoaisouaoiweniojfoihas88920903"

# Port that Flask Application will listen on.
app_port = 443

# Basic Flask Application Creation
app = Flask(__name__)

# Returns a Basic skPOS Welcome Page
@app.route("/", methods=['GET'])
def appInformation():
	return "Welcome to skPOS"

# Provides a Developer Login Page (Secured by Admin Username and Password configured at the outset of the sysytem)
@app.route("/developerlogin", methods=['GET', 'POST'])
def developerSignIn():
	if request.method == 'GET':
		return render_template("developerlogin.html")
	if request.method == 'POST':
		if request.form["uname"] + ":" + hash(request.form["pwd"], salt=salt) + "\n" in open("admin_pass.db", "r").readlines():
			return "Password Accepted"
		return "Password Rejected"

# Returns a png representation of the SKPOS Logo
@app.route("/logoDark.png", methods=['GET'])
def returnDarkLogoSvg():
	return send_file("./images/logoDark.png", mimetype='image/png')

# Registers an Ordering Device (Possible from the Developer Dashboard)
@app.route("/registerOrderDevice", methods=['GET', 'POST'])
def postRegisterDeveloper(): 
	return ""

# Returns a string representation of the md5 hash of a supplied input string when combined with a salt string
def hash(information : str, salt : str) -> str:
	str2hash = information + salt
	return hashlib.md5(str2hash.encode()).hexdigest()

# Creates the "admin_pass.db" file to store an admin username and password if not already created
def addAdminLogin():
	admin_user = input("Enter Admin Username: ")
	admin_password = input("Enter Admin Password: ")
	with open("admin_pass.db", "a") as adminFile:
		adminFile.write(admin_user + ":" + hash(admin_password, salt=salt)+"\n")

# Runs the Flask Application on Port 443 
if __name__ == "__main__":
	import sys
	if not exists("admin_pass.db"):
		addAdminLogin()
	if len(sys.argv) < 2:
		from waitress import serve
		serve(app, host="0.0.0.0", port=app_port)
	elif sys.argv[1] == "debug":
		app.run(debug=True, port=app_port)
	else:
		print("Unrecognized Arguments.\nSupply no arguments for a production server.\nSupply 'debug' as the first argument for a flask development server.")