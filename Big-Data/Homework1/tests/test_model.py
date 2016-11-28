from nose.tools import with_setup, ok_, eq_, assert_almost_equals, nottest
from code.utils import get_data_from_svmlight
from code.models import logistic_regression_pred,svm_pred,decisionTree_pred,classification_metrics
from code.cross import get_acc_auc_kfold

def setup_module ():
    global X_train, Y_train, X_test, Y_test
    X_train, Y_train = get_data_from_svmlight("deliverables/features_svmlight.train")
    X_test, Y_test = get_data_from_svmlight("data/features_svmlight.validate")

def test_accuracy_lr():
	expected = 0.766666666667
	Y_pred = logistic_regression_pred(X_train,Y_train,X_test)
	actual = classification_metrics(Y_pred,Y_test)[0]
	assert_almost_equals(expected, actual,places=2, msg="UNEQUAL Expected:%s, Actual:%s" %(expected, actual))

def test_auc_svm():
	expected = 0.725
	Y_pred = svm_pred(X_train,Y_train,X_test)
	actual = classification_metrics(Y_pred,Y_test)[1]
	assert_almost_equals(expected, actual,places=1, msg="UNEQUAL Expected:%s, Actual:%s" %(expected, actual))

def test_fscore_dt():
	expected = 0.534090909091
	Y_pred = decisionTree_pred(X_train,Y_train,X_test)
	actual = classification_metrics(Y_pred,Y_test)[4]
	assert_almost_equals(expected, actual,places=2, msg="UNEQUAL Expected:%s, Actual:%s" %(expected, actual))
