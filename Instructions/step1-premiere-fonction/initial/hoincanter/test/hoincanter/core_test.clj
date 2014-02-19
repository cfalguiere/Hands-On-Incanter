(ns hoincanter.core-test
  (:use  [midje.sweet])
  (:require [clojure.test :refer :all]
            [hoincanter.core :refer :all]))

(fact "dummy test"
      (+ 1 1) => 2)

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))
