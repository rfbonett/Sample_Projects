$max_blocks = 10
$cursor_offset = 4
$blocks = ["\u258F", "\u258E", "\u258D", "\u258C",
          "\u258B", "\u258A", "\u2589", "\u2588"]
$iterations = $max_blocks * $blocks.size

def loadingBar(c)
  full_blocks = c / $blocks.size
  print "\r\e[K"
  print $blocks[-1] * full_blocks
  print $blocks[c % $blocks.size]
  print " " * (($max_blocks - full_blocks) + 1)
  print (c * 100 / $iterations).round(1)
  print "%" + (" " * $cursor_offset)
  c += 1
end

c = 1
until c > $iterations do
  loadingBar(c)
  c += 1
  sleep(0.01)
end
