""" Coded By: Richard Bonett

Creates a GUI to encrypt text.

Implemented as three text boxes; 
    --The leftmost text box allows for text to be inputted
    --The middle text box allows for a password
    --The rightmost text box presents the encrypted text
"""

from tkinter import *

class Encoded :
    """ The Encoded class has methods to encode data"""
    def __init__(self, text, code) :
        self.alphabet = {'U': '000100', 'e': '100101', '0': '010110', 'G': '110110', 
                    'b': '110010', 'g': '110111', 'u': '110011', '6': '100001', 
                    'p': '000011', 'k': '011101', '3': '000010', 'h': '100110', 
                    'c': '010101', 'v': '110000', 'J': '111111', 'H': '101010', 
                    'K': '001101', '9': '111110', 'P': '101101', 'i': '000101', 
                    's': '111010', '8': '111001', '.': '010111', 'O': '011110', 
                    'n': '001011', 'M': '000001', 'X': '001000', 'N': '010011', 
                    'a': '010100', 'm': '101011', 'o': '111101', 'E': '001100', 
                    'T': '011000', 'Z': '011111', 'V': '101000', 't': '110100', 
                    ',': '001010', 'C': '010010', 'L': '100100', 'B': '101100', 
                    'x': '101001', 'S': '100010', 'w': '100011', 'F': '001001', 
                    'f': '101111', 'y': '110001', '2': '000000', '5': '001110', 
                    '7': '100111', 'j': '111000', 'I': '100000', 'q': '001111', 
                    '4': '101110', 'Q': '011001', '1': '010000', 'l': '000110', 
                    'A': '111011', 'R': '111100', 'd': '011100', 'W': '010001', 
                    'Y': '000111', 'r': '011010', 'z': '011011', 'D': '110101'}  
        self.revAlphabet = self.reverse(self.alphabet)
        self.text = self.returnBinary(text)
        self.code = self.affCode(code)
        self.encode()
        self.translate()
        
    
    """ Reverse the input dictionary"""
    def reverse(self, dictionary) :
        revDict = dict()
        for key in dictionary :
            value = dictionary[key]
            revDict[value] = key
        return revDict
    
        
    """ Convert the input string to 'binary' defined by the dictionary"""
    def returnBinary(self, string) :
        data = ""
        for char in string:
            if char in self.alphabet:
                data += self.alphabet[char]
            else:
                data += char   
        return data
        
        
    """ 'Encodes' input code string"""
    def affCode(self, code) :
        aff = str()
        for char in code :
            if ord(char) % 5 == 1 :
                aff += "101"
            elif ord(char) % 5 == 2 :
                aff += "010"
            elif ord(char) % 5 == 3 :
                aff += "011"
            elif ord(char) % 5 == 4 :
                aff += "110"
            else :
                aff += "11"
        return aff
    
        
    """ Encodes text stored in self.text"""
    def encode(self) :
        retData = ""
        charC = 0
        for char in self.text :
            if charC > len(self.code) - 1 :
                charC = 0
            if char == "0" and self.code[charC] == "1":
                retData += "1"
            elif char == "1" and self.code[charC] == "1":
                retData += "0"
            else:
                retData += char
            charC += 1   
        self.text = retData
            
    
    """ Translates 'binary' data to ASCII characters"""
    def translate(self):
        retData = ""
        retChar = ""
        for char in self.text :
            if char == "0" or char == "1" :
                retChar += char
            else :
                retData += char
            if len(retChar) == 6 :
                retData += self.revAlphabet[retChar]
                retChar = ""
        self.text = retData
      
    
class EncryptGUI :
    """ """
    def __init__(self) :
        self.root = Tk()
        self.title = Label(text="Text Encryption Service", font="Verdana 20")
        self.textTitle = Label(text="Raw Text", font="Verdana 15")
        self.cryptTitle = Label(text="Encrypted Text", font="Verdana 15")
        self.textTitle.grid(row=0, column=0)
        self.cryptTitle.grid(row=0, column=2)
        self.title.grid(row=0, column=1)
        self.text = Text(highlightbackground="black")
        self.text.grid(row=1, column=0, rowspan=10)
        self.codeEntry = Entry()
        self.codeEntry.grid(row=3, column=1)
        self.codeEntry.bind("<Return>", self.setCode)
        self.codeLabel = Label(text="Password", font="Verdana 20 underline")
        self.codeLabel.grid(row=2, column=1)
        self.code = "2"
        self.crypt = Text(highlightbackground="black")
        self.crypt.grid(row=1, column=2, rowspan=10)
        self.quit = Button(text="Exit", command=self.root.destroy)
        self.quit.grid(row=0, column=10)
        self.update()
        self.copyText = Button(text="Copy to Clipboard", 
                               command=self.clipText)
        self.copyCrypt = Button(text="Copy to Clipboard", 
                                command=self.clipCrypt)
        self.copyText.grid(row=11, column=0)
        self.copyCrypt.grid(row=11, column=2)
        self.run()
        self.root.mainloop()        

        
    def update(self) :
        self.encoded = Encoded(self.text.get(0.0, END), self.code)
        self.crypt.delete(0.0, END)
        self.crypt.insert(INSERT, self.encoded.text)
        
    
    def run(self) :
        self.update()
        self.root.after(10, self.run)
        
        
    def clipText(self) :
        self.root.clipboard_clear()
        self.root.clipboard_append(self.text.get(0.0, END))
        
    
    def clipCrypt(self) :
        self.root.clipboard_clear()
        self.root.clipboard_append(self.encoded.text)
        
        
    def setCode(self, event) :
        if len(self.codeEntry.get()) > 0 :
            self.code = self.codeEntry.get()
            self.codeEntry.delete(0, END)
        

app = EncryptGUI()
