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

from fileinput import filename
import json
from os import listdir
from static_definitions import hash

class OrderFormats:
    ORDER_FORMAT_DIRECTORY = "order_formats"
    ORDER_FORMAT_FILE_EXTENSION = ".json"
    order_format_db = {}
    currentFormatID : str = None

    def __init__(self, default_format : str) -> None:
        first_valid_file_id = ""
        for file in listdir("./{}".format(self.ORDER_FORMAT_DIRECTORY)):
            if not file.endswith(self.ORDER_FORMAT_FILE_EXTENSION):
                continue
            fileID = self.isFormatFileValid(file)
            if fileID != "":
                if first_valid_file_id == "":
                    first_valid_file_id = fileID
                self.addFormatToDB(fileID, file)
                if file == default_format:
                    self.currentFormatID = fileID
            else: 
                print("{} is not a valid order format".format(file))
        if self.currentFormatID is None and first_valid_file_id != "":
            self.currentFormatID = first_valid_file_id
        else: 
            raise AssertionError("No Valid Order Formats Found. Prgram Cannot Start.")

    def isFormatFileValid(self, fileName : str) -> str:
        try:
            format_path = "./{}/{}".format(self.ORDER_FORMAT_DIRECTORY, fileName)
            json_data = {}
            with open(format_path, 'r') as json_file_data:
                json_data = json.load(json_file_data)
            if self.isFormatJSONValid(json_data):
                return hash(json.dumps(json_data))
            return ""
        except Exception as e:
            print("There was an error converting your file ({}) into valid JSON data. Error Message: ".format(fileName))
            print(e.with_traceback())
            return ""

    def isFormatJSONValid(self, formatJSONData) -> bool:
        return True

    def addFormatToDB(self, ID : str, filename : str):
        self.order_format_db[ID] = filename

    def getCurrentFormatID(self) -> str:
        return self.currentFormatID

    def getFormatByID(self, ID: str):
        return json.load("./{}/{}".format(self.ORDER_FORMAT_DIRECTORY, self.order_format_db[ID]))