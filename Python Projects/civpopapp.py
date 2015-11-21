""" Coded By: Richard Bonett (rfbonett)          
Generates a GUI used to calculate the population of a civilization from the
popular Sid Meier's Civilization franchise.

The GUI uses the following layout :
City: Generates a new city entry, containing an entry box for the city's
      name, as well as buttons to alter the city's size or destroy the city
PopValLabel: Generates a label representing the value of the civ's overall
             population based on the individual cities' sizes
             
Title: Text label for the population

Quit: Allows the program to be terminated
"""

import tkinter as gui
    
class City :     
    """ A City class defines the widgets attached to each city"""
    def __init__(self, i) :
        self.val = 0
        self.var = gui.IntVar()
        self.genWidgets(i)
        self.i = i
        
    
    """ Builds the widgets for the City """
    def genWidgets(self, i) :
        self.name = gui.Entry(width=30, font="Verdana 12 bold")
        self.name.bind("<Return>", self.setName)
        self.name.grid(row=i, column=0)
        self.typeName = gui.Button(text="edit", command=self.changeName)
        self.typeName.grid(row=i, column=1)
        self.label = gui.Button(text=str(self.val), command=self.changeVal)
        self.label.grid(row=i, column=2)
        self.add = gui.Button(text="+", command=self.push)
        self.add.grid(row=i, column=3)
        self.add5 = gui.Button(text="+5", command=self.push5)
        self.add5.grid(row=i, column=4)
        self.remove = gui.Button(text="-", command=self.back)
        self.remove.grid(row=i, column=5)
        self.remove5 = gui.Button(text="-5", command=self.back5)
        self.remove5.grid(row=i, column=6)
        self.delCity = gui.Button(text="x", command=self.end)
        self.delCity.grid(row=i, column=7)
        self.useCity = gui.Checkbutton(variable=self.var, command=self.refresh)
        self.useCity.grid(row=i, column=8)
        self.useCity.select()
        
    
    """ Locks the name of a city by replacing the text box with a label"""
    def setName(self, name) :
        name = self.name.get()
        self.name.destroy()
        self.name = gui.Label(text=name, font="Verdana 12 bold")
        self.name.grid(row=self.i, column=0)
        
    
    """ Replaces the name button with an editable text box"""
    def changeName(self) :
        self.name.destroy()
        self.name = gui.Entry(width=30, font="Verdana 12 bold")
        self.name.bind("<Return>", self.setName)
        self.name.grid(row=self.i, column=0)        
        
        
    """ Changes the city's population label to an entry text editor"""
    def changeVal(self) :
        self.label.destroy()
        self.label = gui.Entry(width=5)
        self.label.bind("<Return>", self.updateVal)
        self.label.grid(row=self.i, column=2)
        
        
    """ Update the city's population value to this value"""
    def updateVal(self, val) :
        val = int(self.label.get())
        if val >= 0 :
            self.val = val
        self.refresh()
        
        
    """ Increment the population value by 1"""
    def push(self) :
        self.val += 1
        self.refresh()
        
        
    """ Increments the city's population value by 5"""
    def push5(self) :
        for i in range(5) :
            self.push()
            
            
    def use(self) :
        if self.var.get() == 1 :
            return True
        return False
        
        
    """ When checked, calls useCity"""
    def checkBox(self) :
        self.useCity.invoke()
            
            
    """ Decrements the city's population value by 1"""
    def back(self) :
        if self.val > 0:
            self.val -= 1
        self.refresh()
        
        
    """ Decrements the city's population value by 5"""
    def back5(self) :
        for i in range(5) :
            self.back()
        
        
    """ Refresh the global population value"""
    def refresh(self) :
        updatePopVal(self.i)
        self.label.destroy()
        self.label = gui.Button(text=str(self.val), command=self.changeVal)
        self.label.grid(row=self.i, column=2)      
        
        
    """ Deletes the city"""
    def end(self) :
        self.val = 0
        self.refresh()
        self.name.destroy()
        self.label.destroy()
        self.add.destroy()
        self.add5.destroy()
        self.remove.destroy()
        self.delCity.destroy()
        self.remove5.destroy()
        self.useCity.destroy()
        self.typeName.destroy()
        
        global cities
        cities.remove(self)
        
        
    """ Return Value is the total city population"""
    def returnValue(self) :
        return (self.val**2.8)*1000
        
        
    def __str__(self) :
        return str(self.i)
        
        
class PopValLabel :
    """ The PopValLabel is defined once and displays the global population"""
    def __init__(self) :
        self.popVal = 0
        self.genWidgets() 
        
        
    """ Sets the main Label"""
    def genWidgets(self) :
        self.popValLabel = gui.Label(text="{:,}".format(int(self.popVal)),
                                     fg="blue", font="Verdana 20 bold")
        self.popValLabel.grid(row=0, column=1, columnspan=6)         

        
    """ Refreshes the value of the main Label"""
    def refresh(self, popVal) :
        self.popVal = popVal
        self.popValLabel.destroy()
        self.popValLabel = gui.Label(text="{:,}".format(int(self.popVal)), 
                                     fg="blue", font="Verdana 20 bold")
        self.popValLabel.grid(row=0, column=1, columnspan=6)         
        
        
""" Updates the global population Value"""
def updatePopVal(ndx) :
    popVal = 0
    for city in cities :
        if city.use() :
            popVal += city.returnValue()
    PopValLabel.refresh(popVal)
        

""" Creates a new City"""
def newCity() :
    global cities
    cities.append(City(len(cities) + 2))
            
        
""" Flips all city checkboxes"""
def checkAll() :
    global cities
    for city in cities :
        city.checkBox()
        
        
# Main Program - Build Basic GUI and loop until program exit
root = gui.Tk()
cities = []
newCity()
addCity = gui.Button(text="Add City", command=newCity)
addCity.grid(row=1, column=0)
popVal = 0
title = gui.Label(text="Civ Population Value: ", fg="black", font="Verdana 20 bold")
title.grid(row=0, column=0)
quit = gui.Button(text="Exit", command=root.destroy)
quit.grid(row=0, column=7)
selectAll = gui.Checkbutton(command=checkAll)
selectAll.grid(row=1, column=8)
selectAll.select()
PopValLabel = PopValLabel()
root.mainloop()
