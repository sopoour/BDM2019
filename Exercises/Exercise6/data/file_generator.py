

import random
import string
import sys 

integers = [random.randrange(300) for i in range(int(sys.argv[1]))]

print("id,first,secound")

for val in integers:
    st1 = ''.join(random.choices(string.ascii_uppercase + string.digits, k=20))
    st2 = ''.join(random.choices(string.ascii_uppercase + string.digits, k=20))
    print(str(val) + "," + st1 + "," + st2)