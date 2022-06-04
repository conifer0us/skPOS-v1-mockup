<p align="center"><img src="https://github.com/conifer0us/skPOS/blob/main/server/images/logoDark.svg"></p>

<h3 align="center">skPOS: Sub King's Own Point of Sale Software</h3>
<br />

### Backend
---
The backend server for SKPOS is written in python using flask. To create a production quality wsgi server, the waitress library is used.
Web authentication is handled with admin usernames and passwords (which set a cookie). At least one set of credentials is stored when the server is first loaded. 
Passwords are hashed with a predefined salt that can be changed before the server is initialized. This salt is defined as a constant in the flask application.
Administrators who successfully login will be redirected to the admin dashboard where they will be able to register new ordering devices. This should be done on the phone to ensure easy QR code scanning.

#### API Endpoints

* /servertest : Returns JSON data with the "message" key set to "Welcome to skPOS" and a status code of 200. Useful for testing if the server is available or not
    * Request Types Accepted: GET
    * Authentication Required: No
* /developerlogin : Returns HTML for the developer/admin login page. Redirects automatically to the administrator panel if a valid cookie is already set. Sets a cookie used for adminpanel access upon successful login
    * Request Types Accepted: GET, POST
    * Authentication Required: No (However, a valid cookie set will redirect you to the admin panel)
* /adminpanel : Returns HTML for the administrator dashboard. Must have a cookie to access. Used for registering devices.
    * Request Types Accepted: GET
    * Authenticatoin Required: Yes, request must have a valid cookie (obtained automatically from logging in through /developerlogin)
* /logoDark.png : Returns the full skPOS logo
    * Request Types Accepted: GET
    * Authentication Required: No
* /adminlogout : Removes all admin cookies from nonpersistent storage and redirects the current admin back to the developerlogin page
    * Request Types Accepted: GET
    * Authentication Required: Yes (Must have a valid admin cookie set to log out all administrators)