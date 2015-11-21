"""wordsearchapp.py                    Richard Bonett | Last Edit: 03/10/2015

First generates a Word Search Selection menu. Then, generates Word Search
Puzzles based on the user's selections. 

Class WordPuzzle: The Word Search puzzle itself. Contains no GUI elements

Class NewSolution: Allows for words to be highlighted in the Word Search

Class SelectionBox: The Game Menu used for generating Word Search Puzzles

Class PuzzleFrame: Generates a new Word Search Puzzle window when called, 
                   using the information developed from the selection menu
"""

from tkinter import *
import random

#---- Puzzle Classes: Control Puzzle Values ----#
class WordPuzzle :
    def __init__(self, puzzleType, numWords) :
        """Constructor for WordPuzzle
        Arguments:
        puzzleType -- The kind of words to be used in the puzzle
        numWords -- The number of words to be used in the puzzle
        """
        self.numWords = numWords
        self.puzzleType = puzzleType
        self.words = list() #List of words in the puzzle
        self.rows = list() #2DArray of letters in the puzzle
        
        #Generates empty 2DArray
        for i in range(20) :
            self.rows.append(i)
            self.rows[i] = [" "]*20     
            
        self.solution = dict()  #Dictionary of solution indices
        self.genWords() #Generate word list
        self.genPuzzle() #Place words randomly in puzzle
        self.fill() #Fill empty spaces in puzzle with random letters

        
    def genWords(self) :
        if self.puzzleType == "random" :
            file = open("dictionary.txt", "r")
            maxW = 60388
        elif self.puzzleType == "fruit" :
            file = open("fruit.txt", "r")
            maxW = 48
        elif self.puzzleType == "states" :
            file = open("states.txt", "r")
            maxW = 49
        elif self.puzzleType == "countries" :
            file = open("countries.txt", "r")
            maxW = 194
        elif self.puzzleType == "space" :
            file = open("space.txt", "r")
            maxW = 28
        rands = list()
        wordList = []
        for line in file :
            wordList.append(line.strip())
        rands = []
        while len(self.words) < self.numWords :
            rand = random.randint(0, maxW)
            if rand not in rands: 
                self.words.append(wordList[rand].upper())
                rands.append(rand)
        self.words = sorted(self.words)
        file.close()
        
        
    def genPuzzle(self) :
        count = 0
        while len(self.solution) < len(self.words)  :
            ndxR = random.randint(0, 20)
            ndxC = random.randint(0, 20)
            direc = random.randint(1, 8)
            if self.tryNdx(count, ndxR, ndxC, direc) :
                self.add(count, ndxR, ndxC, direc)
                count += 1
                
    
    def tryNdx(self, count, ndxR, ndxC, direc) :
        word = self.words[count]
        for char in word :
            try :
                self.rows[ndxR][ndxC]
            except :
                return False
            
            if char != self.rows[ndxR][ndxC] and self.rows[ndxR][ndxC] != " " :
                return False
            
            if ndxR < 0 or ndxC < 0 :
                return False
            
            if direc == 1 : # Forward Neutral
                ndxR += 1
            elif direc == 2 : # Reverse Neutral
                ndxR -= 1
            elif direc == 3 : # Forward Up
                ndxR += 1
                ndxC += 1
            elif direc == 4 : # Reverse Up
                ndxR -= 1
                ndxC += 1
            elif direc == 5 :# Forward Down
                ndxR += 1
                ndxC -= 1
            elif direc == 6 : # Reverse Down
                ndxR -= 1
                ndxC -= 1   
            elif direc == 7 : # Neutral Up
                ndxC += 1
            elif direc == 8 : # Neutral Down
                ndxC -= 1
        return True      
    
    
    def add(self, count, ndxR, ndxC, direc) :
        solution = []
        for char in self.words[count] :
            self.rows[ndxR][ndxC] = " " + char
            solution.append((ndxR, ndxC))
            if direc == 1 : # Forward Neutral
                ndxR += 1
            elif direc == 2 : # Reverse Neutral
                ndxR -= 1
            elif direc == 3 : # Forward Up
                ndxR += 1
                ndxC += 1
            elif direc == 4 : # Reverse Up
                ndxR -= 1
                ndxC += 1
            elif direc == 5 :# Forward Down
                ndxR += 1
                ndxC -= 1
            elif direc == 6 : # Reverse Down
                ndxR -= 1
                ndxC -= 1   
            elif direc == 7 : # Neutral Up
                ndxC += 1
            elif direc == 8 : # Neutral Down
                ndxC -= 1        
        self.solution[self.words[count]] = solution
        
        
    def fill(self) :
        alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        ndx = 0
        for array in self.rows :
            for i in range(array.count(" ")) :
                rand = alphabet[random.randint(0, 25)]
                self.rows[ndx][array.index(" ")] = " " + rand 
            ndx += 1
                
        
            
    def __getitem(self, ndx) :
        return self.rows[ndx[0]][ndx[1]]
    
    
    def __str__(self) :
        retStr = ""
        for row in self.rows:
            for col in row:
                retStr += col + " "
            retStr += "\n"
        return retStr
    
    
    def __iter__(self) :
        return _WordPuzzleIterator(self.rows)
        
        
class _WordPuzzleIterator :
    def __init__(self, theArray) :
        self._arrayRef = theArray
        self._curNdx = 0
        
    def __iter__(self) :
        return self
    
    def __next__(self) :
        if self._curNdx < len(self._arrayRef) :
            entry = self._arrayRef[self._curNdx]
            self._curNdx += 1
            return entry
        else :
            raise StopIteration
        
    
#---- GUI Classes ----#
class newSolution :
    def __init__(self, rootA, puzzle, word, rowC) :
        self.rootA = rootA
        self.puzzle = puzzle
        self.revealLabels = list()
        self.word = word
        self.var = 0
        self.label = Label(self.rootA, text=word.title(), font="Verdana 20 bold")
        self.label.grid(row=rowC, column=0)
        self.solveButton = Checkbutton(self.rootA, command=self.reveal)
        self.solveButton.grid(row=rowC, column=1)
        
    def reveal(self) :
        if self.var == 0 :
            self.var = 1
            color = "red"
        else :
            self.var = 0
            color = "black"
        c = 0 
        for label in self.revealLabels :
            label.destroy()
        for ndx in self.puzzle.solution[self.word] :
            self.revealLabel = Label(self.rootA, text=(" " + self.word[c]), fg=color, 
                                     font="Verdana 20 bold")
            self.revealLabel.grid(row=ndx[0] + 2, column=ndx[1] + 2)
            self.revealLabels.append(self.revealLabel)
            c += 1        
        
   
class SelectionBox :
    def __init__(self) :
        self.root = Tk()
        self.root.title("Word Search App Menu")
        self.totWords = 20
        self.puzzleType = "random"
        self.puzzleLabel = Label(text="Generate %s Puzzle with %d words?"%(
            self.puzzleType, self.totWords))
        self.puzzleLabel.grid(row=5, column=0, columnspan=2) 
        self.genPuzzle = Button(text="Generate Puzzle", command=self.gen)
        self.genPuzzle.grid(row=5, column=2)          
        
        self.title = Label(text="Word Search App", font="Verdana 24 bold")
        self.title.grid(row=0, column=0, columnspan=4)
        
        self.instructions = Label(text="Select a type of word search to play")
        self.instructions.grid(row=1, column=0, columnspan=4)
        
        self.randomType = Button(text="Random", command=self.setRandom)
        self.randomType.grid(row=2, column=0)
        self.fruitType = Button(text="Fruits", command=self.setFruit)
        self.fruitType.grid(row=2, column=1)
        self.statesType = Button(text="US States", command=self.setStates)
        self.statesType.grid(row=2, column=2)
        self.countriesType = Button(text="Countries", command=self.setCountries)
        self.countriesType.grid(row=3, column=0)   
        self.spaceType = Button(text="Space", command=self.setSpace)
        self.spaceType.grid(row=3, column=1)
        
        self.numWordsLabel = Label(text="Number of Words: %d"%self.totWords)
        self.numWordsLabel.grid(row=1, column=5, columnspan=3)
        self.add = Button(text="+", command=self.push)
        self.add.grid(row=2, column=5)
        self.add5 = Button(text="+5", command=self.push5)
        self.add5.grid(row=2, column=6)
        self.remove = Button(text="-", command=self.back)
        self.remove.grid(row=3, column=5)
        self.remove5 = Button(text="-5", command=self.back5)
        self.remove5.grid(row=3, column=6)        
    
        self.exit = Button(text="Exit", command=self.exitProg)
        self.exit.grid(row=0, column=6)
        self.root.mainloop()
        
        
    def push(self) :
        if self.totWords < 20 :
            self.totWords += 1
        self.refresh()
        
    
    def push5(self) :
        if self.totWords < 16 :
            self.totWords += 5
        else :
            self.totWords = 20
        self.refresh()
        
        
    def back(self) :
        if self.totWords > 0 :
            self.totWords -= 1
        self.refresh()
        
            
    def back5(self) :
        if self.totWords > 4 :
            self.totWords -= 5
        else :
            self.totWords = 0
        self.refresh()
        
            
    def refresh(self) :
        self.numWordsLabel.destroy()
        self.numWordsLabel = Label(text="Number of Words: %d"%self.totWords)
        self.numWordsLabel.grid(row=1, column=5, columnspan=3)  
        self.puzzleLabel.destroy()
        self.puzzleLabel = Label(text="Generate %s puzzle with %d words?"%(
            self.puzzleType, self.totWords))
        self.puzzleLabel.grid(row=5, column=0, columnspan=2)        

        
    def setRandom(self) :
        self.setType("random")
        
    def setFruit(self) :
        self.setType("fruit")
        
    def setStates(self) :
        self.setType("states")
        
    def setCountries(self) :
        self.setType("countries")
        
    def setSpace(self) :
        self.setType("space")
        
        
    def setType(self, value) :
        self.puzzleType = value
        self.puzzleLabel.destroy()
        self.puzzleLabel = Label(text="Generate %s puzzle with %d words?"%(
            self.puzzleType, self.totWords))
        self.puzzleLabel.grid(row=5, column=0, columnspan=2)
    
    
    def exitProg(self) :
        self.root.destroy()
        
    
    def gen(self) :
        puzzle = PuzzleFrame(self)
            

class PuzzleFrame :
    def __init__(self, window) :
        self.puzzle = WordPuzzle(window.puzzleType, window.totWords)
        self.checkBoxes = list()
        self.root2 = Tk()
        self.root2.title("Word Search App")
        rowC = 2
        colC = 0
        self.title = Label(self.root2, text="Word Search App", font="Verdana 24 bold")
        self.title.grid(row=0, column=1, columnspan=22)
        self.wordLabel = Label(self.root2, text="Words", font="Verdana 20 bold underline")
        self.wordLabel.grid(row=1, column=0)
        self.checkAll = Checkbutton(self.root2, command=self.toggleAll)
        self.checkAll.grid(row=1, column=1)
        self.quitButton = Button(self.root2, text="Exit", command=self.root2.destroy)
        self.quitButton.grid(row=0, column=22)
        for row in self.puzzle :
            for char in row :
                self.newLabel = Label(self.root2, text=char, font="Verdana 20 bold")
                self.newLabel.grid(row=rowC, column=colC + 2)
                colC += 1
            rowC += 1
            colC = 0
        rowC = 2
        for word in self.puzzle.words :
            self.checkBoxes.append(newSolution(self.root2, self.puzzle, word, rowC))
            rowC += 1
        self.border = Label(self.root2, text=" ")
        self.border.grid(row=rowC, column=0)
        self.root2.mainloop()      
        
    def toggleAll(self) :
        for checkBox in self.checkBoxes :
            checkBox.solveButton.invoke()        
            
    
#---- Main Program Start: Generates Selection Box ----#
window = SelectionBox()