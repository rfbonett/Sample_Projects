def hanoi(n, source, helper, target):
    if n > 0:
        print "hanoi( ", n, source, helper, target, " called"
        # move tower of size n - 1 to helper:
        hanoi(n - 1, source, target, helper)
        # move disk from source peg to target peg
        if source[0]:
            disk = source[0].pop()
            #print "moving " + str(disk) + " from " + source[1] + " to " + target[1]
            target[0].append(disk)
        # move tower of size n-1 from helper to target
        hanoi(n - 1, helper, source, target)

source = ([], "source")
target = ([], "target")
helper = ([], "helper")
hanoi(4,source,helper,target)

print source, helper, target
