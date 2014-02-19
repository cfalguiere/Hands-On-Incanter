(ns hoincanter.core
  (:use [incanter.core :only [dataset save]])
  (:use [incanter.stats :only [quantile]])
  (:use [clojure.test :only [function?]] )
  (:use [hoincanter.summary] )
  (:use [hoincanter.charts] )
  (:require [clj-time.coerce :as coerce] )
  (:require [clj-time.format :as format] )
  (:require [clojure.java.io :as io]) )


(def time-pattern #"^([0-9- :,]*) INFO.*TIME MONITOR.*;(.*);(.*) ms$")

(defn str-to-long
  "convert a string into a long"
  [x]
  {:pre  [(string? x)]}
  (Long/parseLong (apply str (filter #(Character/isDigit %) x))))

;; step3
(def custom-formatter (format/formatter "yyyy-MM-dd HH:mm:ss,SSS"))

;; implement functions

(defn str-to-ts
  "convert a string into a numeric timestamp"
  [x]
  {:pre  [(string? x)]}
  (coerce/to-long  (format/parse custom-formatter x)))

(defn build-reading
  "re-seq returns a vector consisting of the line and each group. This function filters out the line and converts duration into a long"
  [[s t l d]]
  [t l (str-to-long d) (str-to-ts t) ] )

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
  (dataset [:timestamp :servicename :duration :ts] readings))

(defn convert-to-dataset
  "get a dataset from the file"
  [filename]
  {:pre  [(string? filename)] } 
  (readings-to-dataset (extract-data filename)))

(defn time-analysis
  "compute and save response time statistics"
  [filename]
  {:pre  [(string? filename)] } 
  (let [ data-ds (convert-to-dataset "resources/sample.log")
	 metrics (compute-metrics metric-functions data-ds) 
	 metrics-ds (metrics-to-dataset metric-functions metrics) ]
    (print metrics-ds)
    (save metrics-ds  "stats.csv")
    (save (count-bar-chart data-ds :servicename) "barchart.png")
    ))

(defn -main
  "usage: logs.txt"
  [& args]
  {:pre [(string? (nth args 0))] } 
  (let [ [filename] args]
    (time-analysis filename)))
 