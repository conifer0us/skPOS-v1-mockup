from flask import Flask, render_template, request, send_file
import hashlib
from os.path import exists

# Change to Alter Password Salt
salt = "naohoinvoaisouaoiweniojfoihas88920903"
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
	return send_file("./templates/logoDark.png", mimetype='image/png')

# Registers an Ordering Device (Possible from the Developer Dashboard)
@app.route("/registerOrderDevice", methods=['GET', 'POST'])
def postRegisterDeveloper(): 
	return ""

# Returns a string representation of the md5 hash of a supplied input string when combined with a salt string
def hash(information : str, salt : str) -> str:
	str2hash = information + salt
	return hashlib.md5(str2hash.encode()).hexdigest()

# Creates the "admin_pass.db" file to store an admin username and password if not already created
if not exists("admin_pass.db"):
	admin_user = input("Enter Admin Username: ")
	admin_password = input("Enter Admin Password: ")
	with open("admin_pass.db", "a") as adminFile:
		adminFile.write(admin_user + ":" + hash(admin_password, salt=salt)+"\n")

# Runs the Flask Application on Port 443 
app.run(port=443)