(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all])
  (:use [incanter.core :only [$]])
  (:use [clojure.contrib.generic.math-functions :only [round]]))


;; step 2
(fact "it should convert a string into a long"
      (str-to-long "123456789") => 123456789)

;(fact "it should return a vector consisting in 3 last groups"
;      ( build-reading [ "Full line text" "2013-12-13 08:00:00,924" "RS_OW_AgencyDataSupplierService" "208"])
;	=> ["2013-12-13 08:00:00,924" "RS_OW_AgencyDataSupplierService" 208])

(defn test-mapper
  [[s t l]]
  [t l] )

(fact "it should return a reading with 2 columns "
      (parse-line #"(\d*) (.*)" test-mapper "12 ab") => ["12" "ab"])

(defn test-parser
  "returns the file line as a vector representing the reading"
  [source]
  (first source))

(fact "it should return 3 lines "
      (count (parse-file "resources/sample.log" test-parser)) => 3)

(fact "it should return 3 lines and 3rd item of first line is 261"
        (nth (first (extract-data "resources/sample.log")) 2) => 261)

(fact "it should return a dataset which has 42 as duration on first line"
      (first ($ :duration
		(readings-to-dataset (lazy-seq [["ts1" "l1" 42 1234]["ts2" "l2" 5 3456]]) )
		)) => 42)

(fact "it should return a dataset and duration of first line is 261"
       ($ 0 :duration (load-dataset "resources/sample.log")) => 261)

;; step 3

(fact "it should return a vector consisting in 3 last groups and time stamp"
      ( build-reading [ "Full line text" "2013-12-13 08:00:00,924" "RS_OW_AgencyDataSupplierService" "208"])
	=> ["2013-12-13 08:00:00,924" "RS_OW_AgencyDataSupplierService" 208 1386921600924])

(fact "it should convert string to iso 8601"
      (str-to-ts "2013-12-19 08:00:01,004") => 1387440001004)

