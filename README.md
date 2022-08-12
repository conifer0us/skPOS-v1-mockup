<p align="center"><img src="https://github.com/conifer0us/skPOS/blob/main/server/images/logoDark.svg"></p>

<h3 align="center">skPOS: Sub King's Own Point of Sale Software</h3>
<br />

### Backend
---
The backend server for SKPOS is written in python using flask. To create a production quality wsgi server, the waitress library is used.
Web authentication is handled with admin usernames and passwords (which set a cookie). At least one set of credentials is stored when the server is first loaded. 
Passwords are hashed with a predefined salt that can be changed before the server is initialized. This salt is defined as a constant in the flask application.
Administrators who successfully login will be redirected to the admin dashboard where they will be able to register new ordering devices. This should be done on the phone to ensure easy QR code scanning.

#### API Endpoints Used by the Web Panel

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
* /favicon.ico : Returns the skPOS Favicon
    * Request Types Accepted: GET
    * Authentication Required: No
* /adminlogout : Removes all admin cookies from nonpersistent storage and redirects the current admin back to the developerlogin page
    * Request Types Accepted: GET
    * Authentication Required: Yes (Must have a valid admin cookie set to log out all administrators)
* /registerOrderDevice : Registers an ordering device with the server by adding a hashed device ID to the file that tracks device IDs
    * Request Types Accepted: POST
    * Request Requirements: Request body must be in JSON format with the "deviceID" key set to a String with length 256
    * Return Details: Returns a blank string with status code 200 if successful. Otherwise, returns a JSON object with the "err" key set to the details of the problem
    * Authentication Required: Yes (Must have a valid admin cookie set)

#### API Endpoints Used by Ordering Devices

* /checkDeviceRegistration : Checks if a device with a certain ID has been registered with the server using the registerOrderDevice endpoint
    * Request Types Accepted: POST
    * Request Requirements: Request body must be in JSON format with the "deviceID" key set to a String that stores the device ID
    * Return Details: Returns a simple string with a message describing if the device has been registered
    * Authentication Required: No
---

### Order Formats: Customizing Your Software

Order formats are files that define what types of items and ingredients can be included with an order. These should be put in your server's order_formats directory. Order formats are specified with JSON in .orf files. 

An order format is crucial in the ordering process. When an order is placed, a registered tablet communicates with the server to retrieve the currently used order format. The menu for order placement is then dynamically generated based on the order format received. This order format will then be stored on the ordering device. When the order is sent to the server, the tablet will send JSON data corresponding to the order format, which the server can process. 

When an ordering device stores an order for later editing, this data is stored in JSON format with the "format" key set to the hash of the order format that the order corresponds to. When the ordering device later attempts to edit the order for send-off purposes, the format will be read either from local storage or from the server. If the client or server cannot find the proper hash for the ordering format, the order will not be processed. The server therefore stores all order formats that have been previously used in a database, even if some of them are not being actively recommended to ordering devices. 

Order formats can be thought of as hierarchies. The highest level of the hierarchy defines different menus that contain information about the same order. For example, an order may have different types (call-in orders, in-store orders, etc). It may be beneficial to have different menus containing information about what type the order is and what items are a part of the menu. This way, you can separate different information about the order into different classes of information. 

Many submenus can be added for different purposes, but only one will be duplicated when an item is added to the order. In other words, all menu items must fall under one submenu which can be duplicated. The key of the submenu that should be used to represent menu items can be specified by the "ItemKey" key at the base of the order format hierarchy. 

Every level of the order format hierarchy must have the "title" attribute set to a string that can be displayed by the ordering device. Title attributes are just strings and therefore may contain spaces.

Item IDs are slightly more complex than titles. IDs must be explicitly set for the highest level of the order format hierarchy and for individual menu elements, but not for submenus. Submenu IDs are determined by the Key used to define them. For example, a ChooseOne or Categories item must have the ID key set as part of its JSON data value, but the elements of a Categories item may not because they can contain unique IDs as keys. IDs cannot contain spaces. 

The levels of the hierarchy after the base work slightly differently. Each submenu of the order can have different menu elements attached to it. The valid types of menu elements are "Type", "ChooseOne", "LightNormalExtra", "ChooseMultiple", or "Categories." The details of each of these menu elements are specified below:

* Types of Menu Items
    * Type items are text boxes that allow custom input of, for example, a phone number. Type items must be specified by the "Type" key under a submenu. The value of this "Type" key must be JSON data that specifies the title and ID of the text box. 
    * ChooseOne: Lists of items of which only one can be selected. This is useful if, for example, a car type must be selected. These items are specified by the "ChooseOne" key under a submenu. The value of this key must be JSON data that specifies a list of items to choose from with the "ChooseFrom" key. This item also must have a title and ID
    * ChooseMultiple: Lists of items of which as many as desired may be selected. This is useful as a list of ingredients for example. The "ChooseMultiple" key under a submenu must be set to a JSON object. In this object, the "ChooseFrom" key must be specified as a list of items to choose from. In addition, the "title" and "id" keys must also be set. 
    * LightNormalExtra: Items that may be classified as none, light, normal, or extra. This is useful if there is a list of ingredients that can be present in different amounts. This may be placed under a submenu with the "LightNormalExtra" key set to a JSON object. This object must have the title and id keys set. In addition, it must, like the ChooseMultiple item, have the "ChooseFrom" option set to a list of ingredients.
    * Categories: Contains a set of conditional submenus. They are presented like ChooseOne Options, except they add different options depending on what is selected. The Categories item must contain JSON data formatted so that keys are IDs of category options and the values for those keys are submenu JSON data.



### Orders that Fit Order Formats

Orders send by registered devices to the server must follow the order format they were specified. There are rules that submitted orders must follow to adhere to the order format. 

All orders must be send to the server with JSON data in the body of the request.

This order JSON data must contain the "deviceID" key set to a valid deviceID, the "orderNo" key set to a valid order number (obtained from the server), and the "orderFormat" key set to the hash of the order format so that the server can process the response. 

