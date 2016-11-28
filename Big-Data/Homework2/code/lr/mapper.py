#!/usr/bin/env python

import sys
import random

from optparse import OptionParser

parser = OptionParser()
parser.add_option("-n", "--model-num", action="store", dest="n_model",
                  help="number of models to train", type="int")
parser.add_option("-r", "--sample-ratio", action="store", dest="ratio",
                  help="ratio to sample for each ensemble", type="float")

options, args = parser.parse_args(sys.argv)

# INPUT:
# r: ratio of sampling
# M: number of models
# (k,v): input pair
# OUTPUT:
# key-value pairs

#What I want. For i from 1 to M, m is a random number. if m<r, print i,v

# for line in sys.stdin:
#     key = random.randint(0, options.n_model - 1)
#     value = line.strip()
#     if len(value) > 0:
#         print "%d\t%s" % (key, value)

for line in sys.stdin:
    modelNumber = options.n_model
    sampleRatio = options.ratio
    value = line.strip()
    for i in range(0,modelNumber):
        randomModel = random.randint(0,1)
        if randomModel < sampleRatio:
            print "%d\t%s" % (i+1, value)