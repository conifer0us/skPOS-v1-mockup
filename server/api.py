import json
from xmlrpc.client import Boolean
from flask import Flask, jsonify, make_response, redirect, render_template, request, send_file
import hashlib
from os.path import exists
from random import getrandbits

# Change to Alter Password Salt. Admin accounts will lose access if this is done with passwords already set. Registered Ordering Devices will also become unregistered
salt = "naohoinvoaisouaoiweniojfoihas88920903"

# Port that Flask Application will listen on.
app_port = 443

# Basic Flask Application Creation
app = Flask(__name__)

# Creates Non-Persistent Admin Cookie Storage (hashed with salt)
adminCookies = []

# Defines the Name of the file to store Device IDs in
deviceRegistrationFile = "devices.db"

# Defines the Name of the file to store Admin Login information in
adminLoginFile = "admin_login.db"

# Returns a Basic skPOS Welcome Page
@app.route("/servertest", methods=['GET'])
def appInformation():
	return jsonify({"message": "Welcome to skPOS"}), 200

# Provides a Developer Login Page (Secured by Admin Username and Password configured at system initialization)
@app.route("/developerlogin", methods=['GET', 'POST'])
def developerSignIn():
	if isAdmin(request):
		return redirect("/adminpanel")
	elif request.method == 'GET':
		return render_template("developerlogin.html")
	elif request.method == 'POST':
		if request.form["uname"] + ":" + hash(request.form["pwd"], salt=salt) + "\n" in open(adminLoginFile, "r").readlines():
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

@app.route("/favicon.ico", methods=["GET"])
def returnFavicon():
	return send_file("./images/favicon.ico", mimetype='image/vnd.microsoft.icon'), 200

# Registers an Ordering Device (Possible from the Developer Dashboard)
@app.route("/registerOrderDevice", methods=['POST'])
def registerOrderDevice(): 
	if not isAdmin(request):
		return jsonify({"err", "Access Denied"}), 401
	submittedDeviceID = request.json["deviceID"]
	if isRegisteredDevice(request):
		return jsonify({"err", "Device Already Registered"}), 409
	if len(submittedDeviceID) != 256:
		return jsonify({"err", "Server Could Not Process Device ID"}), 500
	else: 
		addOrderingDeviceToDB(submittedDeviceID)
		return "", 200

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

# Creates the file specified by adminLoginFile variable to store an admin username and password if not already created
def addAdminLogin():
	admin_user = input("Enter Admin Username: ")
	admin_password = input("Enter Admin Password: ")
	with open(adminLoginFile, "a") as adminFile:
		adminFile.write(admin_user + ":" + hash(admin_password, salt=salt)+"\n")

# Adds hashed device IDs to the file specified by the deviceRegistrationFile variable (creates file if it does not exist)
def addOrderingDeviceToDB(deviceID : str):
	with open(deviceRegistrationFile, "a") as deviceFile:
		deviceFile.write(hash(deviceID, salt=salt))

# Checks if a specific request was sent by an already logged in admin user
def isAdmin(req : request):
	cookieval = req.cookies.get("login")
	if cookieval is None:
		return False
	elif hash(str(cookieval), salt=salt) in adminCookies:
		return True
	return False

def isRegisteredDevice(req : request) -> Boolean:
	deviceID = req.json["deviceID"]
	if deviceID is None:
		return False
	elif hash(deviceID, salt=salt)+"\n" in open(deviceRegistrationFile, "r").readlines():
		return True
	else: 
		return False

# Adds a value as a valid cookie
def createCookie(val):
	adminCookies.append(hash(str(val), salt=salt))

# Runs the Flask Application on Port 443 
if __name__ == "__main__":
	import sys
	if not exists(adminLoginFile):
		addAdminLogin()
	if len(sys.argv) < 2:
		from waitress import serve
		serve(app, host="0.0.0.0", port=app_port)
	elif sys.argv[1] == "debug":
		app.run(debug=True, port=app_port)
	else:
		print("Unrecognized Arguments.\nSupply no arguments for a production server.\nSupply 'debug' as the first argument for a flask development server.")