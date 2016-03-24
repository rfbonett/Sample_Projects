
def solved(board)
  return false
end


def output(board)
  for i in 0..2 do
    print "|"
    for j in 0..2 do
      val = board[(3*i) + j]
      if val == 0
        print " |"
      elsif val % 2 == 1
        print "X|"
      else
        print "O|"
      end
    end
    print "\n"
  end
end

board = [0, 0, 0, 0, 0, 0, 0, 0, 0]
curVal = 1
puts "TicTacToe: 1-9 to set box, 'end' or return to exit"
puts "Input: "
for i in 0..2 do
  puts "| | | |"
end

while (solved(board) == false) do
  print "\e[A"*4 + "\e[K" + "\r"
  print "Input: "
  input = gets.chomp

  if input == "end" || input == ""
    print "\r\e[A"
    print "\e[K\n" * 4
    print "\e[A" * 4
    puts "Thanks for playing!\n\n"
    break
  elsif "123456789".include? input
    board[Integer(input) - 1] = curVal
    curVal += 1
  end
  output(board)
end
