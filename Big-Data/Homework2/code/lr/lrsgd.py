#!/usr/bin/env python
"""
Implement your own version of logistic regression with stochastic
gradient descent.

Author: Kelly Cheng
Email : k.cheng@gatech.edu
"""

import collections
import math


class LogisticRegressionSGD:

    def __init__(self, eta, mu, n_feature):
        """
        Initialization of model parameters
        """
        self.eta = eta
	self.mu = mu
        self.weight = [0.0] * n_feature
	self.n_feature = n_feature

    def fit(self, X, y):
        """
        Update model using a pair of training sample
        """
        w = self.weight
	mu = self.mu
	wtx = 0
	for i in range(0, len(X)-1):
		wtx = wtx + w[X[i][0]]*X[i][1]
	sigmoid = 1/(1+math.exp(-wtx))
	helper = sigmoid - y
	helper = helper * self.eta
	x = [0.0]*self.n_feature
	for l in range(0, len(X)-1):
		x[X[l][0]] = X[l][1]
	x = [s*helper for s in x]
	for p in range(0, len(w)-1):
		w[p] = w[p] - x[p]
	norm = 0
	for k in range(0, len(w)-1):
		norm += w[k]*w[k]
	w = [x - norm for x in w]
#	print w[2]
#	pred = LogisticRegressionSGD.predict(self, X)
#	sigma = LogisticRegressionSGD.predict_prob(self, X)
#	update = sigma - y
#	update = update*X[0][1]
#	w[:] = [x - self.eta*update for x in w]
#	w[:] = [x - self.eta*mu*normw for x in w]
#	wtx = (self.weight[f]*v for f,v in X)
#	print (self.weight[f]*v for f,v in X)
#	wtx = math.fsum((self.weight[f]*f for f,v in X))
#	wtx = 0
#	print "w"
#	print w[X[3][0]]
#	print X[3][1]
#	for j in range(0,len(X)-1):
#		wtx = wtx + w[X[j][0]]*X[j][1]
#	print "wtx"
#	print wtx
#	wtx = (self.weight[X[f][0]]*v for f,v in X)
#	sigma = 1/(1+math.exp(-wtx))
#	print sigma
#	sigma = LogisticRegressionSGD.predict_prob(self,X)
#	h = y-sigma
#	for i in range(0,len(X)-1):
#		w[i] = w[i] - self.eta*X[i][1]*h
#	h = [self.eta*(sigma-y)*x for x in X[x][1]]
#	print h
#	print xj
#	w[:] = [x - h for x in w]
#	this is the part for L2
#	normw = 0
#	for k in range (0, len(w)):
#		normw += w[k]*w[k]
#	l2 = normw*self.eta*mu
#	for l in range(0, len(w)):
#		w[l] = w[l] - l2
#	print w[12]
	
#	w[:] = [x - self.eta*mu*normw for x in w]
        pass

    def predict(self, X):
        return 1 if LogisticRegressionSGD.predict_prob(self,X) > 0.5 else 0

    def predict_prob(self, X):
        return 1.0 / (1.0 + math.exp(-math.fsum((self.weight[f]*v for f, v in X))))
