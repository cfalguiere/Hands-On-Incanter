(ns hoincanter.summary
  (:use [incanter.core :only [$ $data $rollup col-names conj-cols dataset dataset? sel]])
  (:use [incanter.stats :only [mean sd quantile]])
  (:use [clojure.test :only [function?]] ))

;; step5

(def metric-functions {:count count :mean mean :sd sd})


(defn compute-metric1
  "compute a metric grouped by label"
  [ds metric-name metric-function]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? metric-function))] } 
  "TODO") 


(defn compute-metrics
  "compute metrics grouped by label"
  [metric-functions ds]
  {:pre [(and (dataset? ds) (function? metric-functions))] } 
  "TODO")

(defn metrics-to-dataset
  "compute metrics grouped by label"
  [metric-functions metrics]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? metric-functions))] } 
 "TODO")