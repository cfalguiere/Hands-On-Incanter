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

(defn build-reading
  "re-seq returns a vector consisting of the line and each group. This function filters out the line and converts duration into a long"
  [[s t l d]]
  [t l (str-to-long d)] )

(defn parse-line
  "returns the file line as a vector representing the reading"
  [pattern mapper source]
  {:pre  [(and  (function? mapper) (string? source) (= java.util.regex.Pattern (type pattern)))]} 
  (first (map mapper (re-seq pattern source)))) 



(defn parse-file 
  "read the file and returns a list of readings"
  [filename parser]
  {:pre  [(and  (function? parser) (string? filename))]} 
  (with-open [rdr (io/reader filename)]
     (doall (map parser (line-seq rdr)))))

(defn extract-data 
  "get readings from the file"
  [filename]
  {:pre  [(string? filename)]} 
  (let [parser (partial (partial parse-line time-pattern)  build-reading) ]
    (parse-file filename parser)))

(defn readings-to-dataset
  "build a dataset from a list of readings"
  [readings]
  {:pre [(seq? readings)] } 
  (dataset [:timestamp :servicename :duration] readings))

(defn load-dataset
  "get a dataset from the file"
  [filename]
  {:pre  [(string? filename)] } 
  (readings-to-dataset (extract-data filename)))
