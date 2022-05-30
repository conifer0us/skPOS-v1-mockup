from flask import Flask, make_response, redirect, render_template, request, send_file
import hashlib
from os.path import exists
from random import getrandbits

# Change to Alter Password Salt. Admin accounts will lose access if this is done with passwords already set.
salt = "naohoinvoaisouaoiweniojfoihas88920903"

# Port that Flask Application will listen on.
app_port = 443

# Basic Flask Application Creation
app = Flask(__name__)

# Creates Non-Persistent Admin Cookie Storage (hashed with salt)
adminCookies = []

# Returns a Basic skPOS Welcome Page
@app.route("/servertest", methods=['GET'])
def appInformation():
	return "Welcome to skPOS"

# Provides a Developer Login Page (Secured by Admin Username and Password configured at system initialization)
@app.route("/developerlogin", methods=['GET', 'POST'])
def developerSignIn():
	if isAdmin(request):
		return redirect("/adminpanel")
	elif request.method == 'GET':
		return render_template("developerlogin.html")
	elif request.method == 'POST':
		if request.form["uname"] + ":" + hash(request.form["pwd"], salt=salt) + "\n" in open("admin_login.db", "r").readlines():
			newCookieVal = getrandbits(64)
			createCookie(newCookieVal)
			resp = make_response("Accepted", 200)
			resp.set_cookie("login", str(newCookieVal))
			return resp
		return "Denied", 401

# Returns the adminpanel HTML file; Must have a valid login cookie to visit this page
@app.route("/adminpanel", methods=['GET'])
def returnAdminPanel():
	if isAdmin(request):
		return render_template("adminpanel.html")
	return redirect("/developerlogin", 302)

# Returns a png representation of the SKPOS Logo
@app.route("/logoDark.png", methods=['GET'])
def returnDarkLogoSvg():
	return send_file("./images/logoDark.png", mimetype='image/png'), 200

# Registers an Ordering Device (Possible from the Developer Dashboard)
@app.route("/registerOrderDevice", methods=['POST'])
def registerOrderDevice(): 
	return ""

# Removes all valid login cookies and forces admins to log back in
@app.route("/adminlogout", methods=["POST"])
def logOutAdmin():
	if not isAdmin(request):
		return "", 401
	global adminCookies
	adminCookies = []
	resp = make_response("", 200)
	resp.delete_cookie("login")
	return resp

# Returns a string representation of the md5 hash of a supplied input string when combined with a salt string
def hash(information : str, salt : str) -> str:
	str2hash = information + salt
	return hashlib.md5(str2hash.encode()).hexdigest()

# Creates the "admin_login.db" file to store an admin username and password if not already created
def addAdminLogin():
	admin_user = input("Enter Admin Username: ")
	admin_password = input("Enter Admin Password: ")
	with open("admin_login.db", "a") as adminFile:
		adminFile.write(admin_user + ":" + hash(admin_password, salt=salt)+"\n")

# Checks if a specific request was sent by an already logged in admin user
def isAdmin(req : request):
	cookieval = req.cookies.get("login")
	if cookieval is None:
		return False
	elif hash(str(cookieval), salt=salt) in adminCookies:
		return True
	return False

# Adds a value as a valid cookie
def createCookie(val):
	adminCookies.append(hash(str(val), salt=salt))

# Runs the Flask Application on Port 443 
if __name__ == "__main__":
	import sys
	if not exists("admin_login.db"):
		addAdminLogin()
	if len(sys.argv) < 2:
		from waitress import serve
		serve(app, host="0.0.0.0", port=app_port)
	elif sys.argv[1] == "debug":
		app.run(debug=True, port=app_port)
	else:
		print("Unrecognized Arguments.\nSupply no arguments for a production server.\nSupply 'debug' as the first argument for a flask development server.")