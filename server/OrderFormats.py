"""
A class that handles Order Formats. When the software starts, it creates a DB of valid order formats from the ones placed in the "order_formats" directory.

Attributes: 
The ORDER_FORMAT_DIRECTORY attribute says where order formats are stored.

Functions:
__init__: Runs whenever an OrderFormats object is created. It scans the directory where order formats are stored, checks whether the formats are valid, and if so, adds them to a DB where they are identified by hash.
If a default format is specified by file name, then that format will be set as the defualt if it exists. Otherwise, the order format with the filename that comes first alphabetically will be set as the default.

isFormatFileValid: Checks an order format to see if it follows all the necessary Order Format Rules; if it does, the function returns a string with an ID for the Format; if it is not valid, the function returns a blank string.
isFormatJSONValid: Checks an order format's JSON data to see if it follows order fomat rules. Returns a boolean value.
getFormatByID: Returns the JSON data that corresponds to an order format ID
getCurrentFormatID: Returns the ID of the order format currently being used by the server.
"""

import json
from os import listdir
from static_definitions import hash

class OrderFormats:
    ORDER_FORMAT_DIRECTORY = "order_formats"
    order_format_db = {}
    currentFormatID : str

    def __init__(self, default_format : str) -> None:
        first_valid_file_id = ""
        for file in listdir("./{}".format(self.ORDER_FORMAT_DIRECTORY)):
            if not file.endswith(".orf"):
                continue
            fileID = self.isFormatFileValid(file)
            if fileID:
                if first_valid_file_id == "":
                    first_valid_file_id = fileID
                self.addFormatToDB(fileID, file)
                if file == default_format:
                    self.currentFormatID = fileID
        if self.currentFormatID is None and first_valid_file_id != "":
            self.currentFormatID = first_valid_file_id
        else: 
            raise AssertionError("Your Program Cannot Start Because You Do Not Have Any Valid Order Formats.\nYou can fix this error by ensuring there is a properly made order format in the proper directory.")

    def isFormatFileValid(self, fileName : str) -> str:
        json_data = json()
        try:
            json_data = json.load("./{}/{}".format(self.ORDER_FORMAT_DIRECTORY, fileName))
        except:
            return ""
        if self.isFormatJSONValid(json_data):
            return hash(json.dumps(json_data))
        return ""

    def isFormatJSONValid(formatJSONData : json) -> bool:
        return True

    def addFormatToDB(self, ID : str, filename : str):
        self.order_format_db[ID] = filename

    def getCurrentFormatID(self) -> str:
        return self.currentFormatID

    def getFormatByID(self, ID: str) -> json:
        return json.load("./{}/{}".format(self.ORDER_FORMAT_DIRECTORY, self.order_format_db[ID]))