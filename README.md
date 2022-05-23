<p align="center"><img src="https://github.com/conifer0us/skPOS/blob/main/server/images/logoDark.svg"></p>

<h1 align="center" style="border-bottom: 0px solid black;">skPOS: Sub King's Own Point of Sale Software</h1>
<br />

# Backend

The backend server for SKPOS is written in python using flask. To create a production quality wsgi server, the waitress library is used.
Authentication is handled with admin usernames and passwords. At least one set of credentials is stored when the server is first loaded. 
Passwords are hashed with a predefined salt that can be changed before the server is initialized. This salt is defined as a constant in the flask application.
Administrators who successfully login will be redirected to the admin dashboard where they will be able to register new ordering devices. This should be done on the phone to ensure easy QR code scanning.
