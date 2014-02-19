(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all])
  (:use clojure.contrib.generic.math-functions))
	   
(fact "quantile 0 equals min of the list"
      (round (first (q 0 [3 2 1]))) => 1 )

   