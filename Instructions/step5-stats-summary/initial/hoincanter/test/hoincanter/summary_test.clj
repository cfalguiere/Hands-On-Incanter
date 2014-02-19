(ns hoincanter.summary-test
  (:use [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.summary :refer :all]
            [hoincanter.core :refer :all])
  (:use [incanter.core :only [$ $where dataset? nrow col-names]])
  (:use [incanter.stats :only [mean]])
  (:use [clojure.contrib.generic.math-functions :only [round]]))

;; step5

(fact "quantile 0 equals min of the list"
      (round (first (q 0 [3 2 1]))) => 1 )

(fact "it should return a dataset "
      (let [ds (convert-to-dataset "resources/sample.log")
	    metric-ds (compute-metric ds :count count) ]
	($ :count ($where {:servicename "WS_1R_DetailAbonne"} metric-ds)) => 1))
 

(fact "it should return a list of 2 datasets. First one is WS_1R_DetailAbonne count 1"
      (let [ds (convert-to-dataset "resources/sample.log")
	    metric-ds-list (compute-metrics {:count count, :mean mean} ds) ]
	(print  metric-ds-list)
	(count metric-ds-list) => 2
	($ :count ($where {:servicename "WS_1R_DetailAbonne"} (nth metric-ds-list 0))) => 1))

(fact "it should return a dataset"
      (let [ds (convert-to-dataset "resources/sample.log")
	    metrics (compute-metrics {:count count, :mean mean} ds) 
	    metrics-ds (metrics-to-dataset {:count count, :mean mean} metrics) ]
	(print  metrics-ds)
	(nrow metrics-ds) => 2
	(col-names metrics-ds) => [:servicename :count :mean]))

(fact "it should return Service WS_1R_DetailAbonne has count 1. RS_OW_AgencyDataSupplierService has count 2"
      (let [ds (convert-to-dataset "resources/sample.log")
	    metrics (compute-metrics {:count count, :mean mean} ds) 
	    metrics-ds (metrics-to-dataset {:count count, :mean mean} metrics) ]
	($ :count ($where {:servicename "WS_1R_DetailAbonne"} metrics-ds)) => 1
	($ :count ($where {:servicename "RS_OW_AgencyDataSupplierService"} metrics-ds)) => 2))

