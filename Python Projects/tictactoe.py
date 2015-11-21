import tkinter as gui
    
   
class Pic :
    def __init__(self, image, i, j, board) :
        self.board = board
        self.image = image
        self.i = i
        self.j = j
        self.button = gui.Button(image=self.image, command=self.change)
        self.button.grid(row=i, column=j)
        
        
    def change(self) :
        self.button.destroy()
        self.board.create(self.i, self.j)
            
        
        
class Board :     
    def __init__(self) :
        self.root = gui.Tk()
        self.root.title("DotA 2 Tic Tac Toe")
        self.blankbox = gui.PhotoImage(file="blankbox.gif")
        self.x = gui.PhotoImage(file="x.gif")
        self.o = gui.PhotoImage(file="o.gif")
        self.image = self.x
        self.c = 0
        self.vals = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
        self.genWidgets()
        self.root.mainloop()
        
    
    def genWidgets(self) :
        for j in range(3) :
            for i in range(3) :
                b = Pic(self.blankbox, i, j, self)
        
            
    def create(self, i, j) :
        if self.c % 2 == 0 :
            b = gui.Button(image=self.o)
            self.vals[i][j] = 1
        else :
            b = gui.Button(image=self.x)
            self.vals[i][j] = 2
        b.grid(row=i, column=j)
        self.c += 1
        
        check = self.check()
        if check != 0 :
            self.end(check)
        
    
    def check(self) :
        q = self.vals
        for i in range(3) :
            if q[i][0] == q[i][1] and q[i][1] == q[i][2] :
                return q[i][0]
            elif q[0][i] == q[1][i] and q[1][i] == q[2][i] :
                return q[0][i]
        if q[0][0] == q[1][1] and q[1][1] == q[2][2] or q[0][2] == q[1][1] and q[2][0] :
            return q[1][1]
        return 0
        
        
    def end(self, val) :
        self.root.destroy()
        self.root = gui.Tk()
        if val == 1 :
            self.label = gui.Label(text="O wins!!!")
        else :
            self.label = gui.Label(text="X wins!!!")
        self.exit = gui.Button(text="New Game", command=self.new)
        self.label.pack()
        self.exit.pack()
        self.root.mainloop()
        
        
    def new(self) :
        self.root.destroy()
        board = Board()
        
        
    
board = Board()
board.root.mainloop()