"""
paint
"""
from tkinter import *

class PaintCanvas :
    def __init__(self) :
        self.color = "black"
        self.paintList = list()
        self.paintedList = list()
        self.tempList = list()
        self.root = Tk()
        self.frame = Frame()
        self.frame.grid(row=0, column=0)
        self.canvas = Canvas(self.frame, width=300, height=300)
        self.canvas.pack()
        self.canvas.bind("<B1-Motion>", self.prepare)
        self.canvas.bind("<ButtonRelease-1>", self.paintFinal)
        self.undoButton = Button(text="Undo", command=self.undo)
        self.undoButton.grid(row=0, column=1)
        self.canvas.create_rectangle(3, 3, 300, 280)
        self.createColorOptions()
        self.root.mainloop()
        
        
        
    
    def createColorOptions(self) :
        self.white = self.canvas.create_rectangle(10, 290, 20, 300, fill="white")
        self.black = self.canvas.create_rectangle(30, 290, 40, 300, fill="black")
        self.red = self.canvas.create_rectangle(50, 290, 60, 300, fill="red")
        self.green = self.canvas.create_rectangle(70, 290, 80, 300, fill="green")
        self.blue = self.canvas.create_rectangle(90, 290, 100, 300, fill="blue")
        self.cyan = self.canvas.create_rectangle(110, 290, 120, 300, fill="cyan")
        self.yellow = self.canvas.create_rectangle(130, 290, 140, 300, fill="yellow")
        self.magenta = self.canvas.create_rectangle(150, 290, 160, 300, fill="magenta")
        self.brown = self.canvas.create_rectangle(170, 290, 180, 300, fill="brown")
        
        self.canvas.tag_bind(self.white, "<Button-1>", lambda x: self.colorChange("white"))
        self.canvas.tag_bind(self.black, "<Button-1>", lambda x: self.colorChange("black"))
        self.canvas.tag_bind(self.red, "<Button-1>", lambda x: self.colorChange("red"))
        self.canvas.tag_bind(self.green, "<Button-1>", lambda x: self.colorChange("green"))
        self.canvas.tag_bind(self.blue, "<Button-1>", lambda x: self.colorChange("blue"))
        self.canvas.tag_bind(self.cyan, "<Button-1>", lambda x: self.colorChange("cyan"))
        self.canvas.tag_bind(self.yellow, "<Button-1>", lambda x: self.colorChange("yellow"))
        self.canvas.tag_bind(self.magenta, "<Button-1>", lambda x: self.colorChange("magenta"))
        self.canvas.tag_bind(self.brown, "<Button-1>", lambda x: self.colorChange("brown"))
        
    
    
    def colorChange(self, color) :
        self.color = color
        
        
    def undo(self) :
        try :
            self.paintedList[-1]
        except IndexError :
            print("Index Error: No items to delete.")
            return
        while self.paintedList[-1] != "break" :
            self.canvas.delete(self.paintedList.pop())
        self.paintedList.pop()
        
        
    def prepare(self, event) :
        x = event.x
        y = event.y
        rec = self.canvas.create_rectangle(x-1, y+1, x+1, y-1, fill=self.color, 
                                           outline=self.color)
        self.tempList.append(rec)
        self.paintList.append((event.x, event.y))

        
        
    def paintFinal(self, event) :
        try :
            self.tempList[-1]
            self.paintList[-1]
        except IndexError :
            return
        while len(self.tempList) > 0 :
            self.canvas.delete(self.tempList.pop())
        self.paintedList.append("break")
        x = self.paintList[0][0]
        y = self.paintList[0][1]
        for coord in self.paintList :
            oldX = x
            oldY = y
            x = coord[0]
            y = coord[1]
            if y < 280 and oldY < 280:
                newLine = self.canvas.create_line(oldX, oldY, x, y, fill=self.color,
                                                  width=3) 
                self.paintedList.append(newLine)
        self.paintList = list()
        

window = PaintCanvas()