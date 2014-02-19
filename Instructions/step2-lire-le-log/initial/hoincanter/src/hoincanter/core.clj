(ns hoincanter.core
  (:use [incanter.core :only [dataset]])
  (:use [incanter.stats :only [quantile]])
  (:use [clojure.test :only [function?]] )
  (:require [clojure.java.io :as io]) )

;; step1
(defn q [p serie] (quantile serie :probs [p]))

;; step2

(def time-pattern #"^([0-9- :,]*) INFO.*TIME MONITOR.*;(.*);(.*) ms$")

(defn str-to-long
  "convert a string into a long"
  [x]
  {:pre  [(string? x)]}
  (Long/parseLong (apply str (filter #(Character/isDigit %) x))))

(defn parse-file 
  "read the file and returns a list of readings"
  [filename parser]
  {:pre  [(and  (function? parser) (string? filename))]} 
  (with-open [rdr (io/reader filename)]
     (doall (map parser (line-seq rdr)))))
