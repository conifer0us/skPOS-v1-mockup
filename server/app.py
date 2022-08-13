from flask import Flask, jsonify, make_response, redirect, render_template, request, send_file
from random import getrandbits
import Authenticator
import OrderFormats

# Port that Flask Application will listen on.
app_port = 443

# Basic Flask Application Creation
app = Flask(__name__)

# Sets up Authenticator Class to handle device and admin panel authentication
auth = Authenticator.Authenticator()

# Sets up the Order Format DB in the form of an OrderFormats object
formatHandler = OrderFormats.OrderFormats("example.orf")

# Returns a Basic skPOS Welcome Page
@app.route("/servertest", methods=['GET'])
def appInformation():
	return jsonify({"msg": "Welcome to skPOS"}), 200

# Provides a Developer Login Page (Secured by Admin Username and Password configured at system initialization)
@app.route("/developerlogin", methods=['GET', 'POST'])
def developerSignIn():
	if auth.isAdmin(request):
		return redirect("/adminpanel")
	elif request.method == 'GET':
		return render_template("developerlogin.html")
	elif request.method == 'POST':
		if auth.checkAdminCredentials(request):
			newCookieVal = getrandbits(64)
			auth.createCookie(newCookieVal)
			resp = make_response("Accepted", 200)
			resp.set_cookie("login", str(newCookieVal))
			return resp
		return "Denied", 401

# Returns the adminpanel HTML file; Must have a valid login cookie to visit this page
@app.route("/adminpanel", methods=['GET'])
def returnAdminPanel():
	if auth.isAdmin(request):
		return render_template("adminpanel.html")
	return redirect("/developerlogin", 302)

# Returns a png representation of the SKPOS Logo
@app.route("/logoDark.png", methods=['GET'])
def returnDarkLogoSvg():
	return send_file("./images/logoDark.png", mimetype='image/png'), 200

@app.route("/favicon.ico", methods=["GET"])
def returnFavicon():
	return send_file("./images/favicon.ico", mimetype='image/vnd.microsoft.icon'), 200

# Removes all valid login cookies and forces admins to log back in
@app.route("/adminlogout", methods=["POST"])
def logOutAdmin():
	if not auth.isAdmin(request):
		return "", 401
	global adminCookies
	adminCookies = []
	resp = make_response("", 200)
	resp.delete_cookie("login")
	return resp

# Registers an Ordering Device (Possible from the Developer Dashboard)
@app.route("/registerOrderDevice", methods=['POST'])
def registerOrderDevice(): 
	if not auth.isAdmin(request):
		return jsonify({"err": "Access Denied"}), 401
	submittedDeviceID = request.json["deviceID"]
	if auth.isRegisteredDevice(request):
		return jsonify({"err": "Device Already Registered"}), 409
	if len(submittedDeviceID) != 256:
		return jsonify({"err": "Server Could Not Process Device ID"}), 500
	else: 
		auth.addOrderingDeviceToDB(submittedDeviceID)
		return "", 200

## API ENDPOINTS FROM HERE ON ARE USED BY ORDERING DEVICES, NOT THE ADMIN PANEL ##

# Checks if the device being used to send the request is registered or not. A registered device will have the "deviceID" key set to a valid ID in the request's JSON body
@app.route("/checkDeviceRegistration", methods=["POST"])
def checkDeviceRegistration():
	if (auth.isRegisteredDevice(request)):
		return jsonify({"msg":"Device is Registered!"}), 200
	else:
		return jsonify({"err":"Your Device has not been Registered"}), 200

# Delivers the current Order Format to the Ordering Device; must have the deviceID key set to a valid device ID
@app.route("/currentOrderFormat", methods=["GET"])
def getCurrentOrderFormat():
	if (auth.isRegisteredDevice(request)):
		return jsonify({"ID":formatHandler.getCurrentFormatID()}), 200
	else:
		return jsonify({"err":"You do not have permission to access that resource."}), 400

# Delivers order format data for a specific format ID; the deviceID key must be set to an authorized device, and the orderID key set to a valid order ID
@app.route("/formatDataByID", methods=["GET"])
def getFormatDataByID():
	if (auth.isRegisteredDevice(request)):
		try:
			return formatHandler.getFormatByID(request.json["orderID"]), 200
		except:
			return jsonify({"err", "There was a problem retrieving your order format."}), 500
	else:
		return jsonify({"err", "You do not have permission to access that resource."}), 400

# Runs the Flask Application on Port 443 
if __name__ == "__main__":
	import sys
	if len(sys.argv) < 2:
		from waitress import serve
		serve(app, host="0.0.0.0", port=app_port)
	elif sys.argv[1] == "debug":
		app.run(debug=True, port=app_port)
	else:
		print("Unrecognized Arguments.\nSupply no arguments for a production server.\nSupply 'debug' as the first argument for a flask development server.")