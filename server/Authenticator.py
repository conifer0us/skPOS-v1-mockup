from static_definitions import hash
from os.path import exists
from flask import request
import json

class Authenticator:

	# Class properties should not be changed; they may compromise the application's functionality

	# Creates Non-Persistent Admin Cookie Storage (hashed with salt)
	adminCookies = []

	# Defines the Name of the file to store Device IDs in
	deviceRegistrationFile = "devices.db"

	# Stores admin usernames and hashed passwords
	adminLoginFile = "admin_login.db"
	
	# Used when hashing
	salt = "naohoinvoaisouaoiweniojfoihas88920903"

	def __init__(self) -> None:
		if not exists(self.adminLoginFile):
			self.addAdminLogin()

	# Creates the file specified by adminLoginFile variable to store an admin username and password if not already created
	def addAdminLogin(self):
		admin_user = input("Enter Admin Username: ")
		admin_password = input("Enter Admin Password: ")
		with open(self.adminLoginFile, "a") as adminFile:
			adminFile.write(admin_user + ":" + hash(admin_password, salt=self.salt)+"\n")

	def checkAdminCredentials(self, req : request) -> bool:
		return req.form["uname"] + ":" + hash(req.form["pwd"], salt=self.salt) + "\n" in open(self.adminLoginFile, "r").readlines()

	# Checks if a specific request was sent by an already logged in admin user
	def isAlreadyAdmin(self, req : request) -> bool:
		cookieval = req.cookies.get("login")
		if cookieval is None:
			return False
		elif hash(str(cookieval), salt=self.salt) in self.adminCookies:
			return True
		return False

	# Adds a value as a valid admin cookie
	def createCookie(self, val):
		self.adminCookies.append(hash(str(val), salt=self.salt))

	## The following functions handle authentication for ordering devices, as opposed to the above functions which handle authentication for admin panel access

	# Adds hashed device IDs to the Device Registration File (creates file if it does not exist)
	def addOrderingDeviceToDB(self, deviceID : str):
		with open(self.deviceRegistrationFile, "a") as deviceFile:
			deviceFile.write(hash(deviceID, salt=self.salt) + "\n")

	# Check if an ordering device sending a request to the server is already registered
	def isRegisteredDevice(self, req : request) -> bool:
		deviceID = req.json["deviceID"]
		if deviceID is None:
			return False
		elif hash(deviceID, salt=self.salt)+"\n" in open(self.deviceRegistrationFile, "r").readlines():
			return True
		else: 
			return False