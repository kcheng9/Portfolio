from nose.tools import with_setup, ok_, eq_, assert_almost_equals, nottest
from code.utils import get_data_from_svmlight
from code.models import logistic_regression_pred,svm_pred,decisionTree_pred,classification_metrics
from code.cross import get_acc_auc_kfold

def test_auc_cv():
	expected = 0.747295051306
	X,Y = get_data_from_svmlight("deliverables/features_svmlight.train")
	actual = get_acc_auc_kfold(X,Y)[1]
	assert_almost_equals(expected, actual,places=1, msg="UNEQUAL Expected:%s, Actual:%s" %(expected, actual))
