(ns hoincanter.summary
  (:use [incanter.core :only [$ $data $rollup col-names conj-cols dataset dataset? sel]])
  (:use [incanter.stats :only [mean sd quantile]])
  (:use [clojure.test :only [function?]] ))

;; step5

(defn q [p serie] (quantile serie :probs [p])) 

(def metric-functions
     { :count count
      :mean mean
      :sd sd
      :min (partial q 0)
      :q90 (partial q 0.90)
      :q95 (partial q 0.95)
      :max (partial q 1)})

(defn compute-metric
  "compute a metric grouped by label"
  [ds [metric-name metric-function]]
  {:pre [(and (dataset? ds) (keyword? metric-name) (function? metric-function))] } 
  (col-names
   ($rollup metric-function :duration :servicename ds)
   [:servicename metric-name])) 


(defn compute-metrics
  "compute metrics grouped by label"
  [metric-functions ds]
  {:pre [(and (dataset? ds) (map? metric-functions))] } 
  (map (partial compute-metric ds) metric-functions))

(defn metrics-to-dataset
  "compute metrics grouped by label"
  [metric-functions metrics]
  {:pre [(and (seq? metrics)  (map? metric-functions))] } 
  (let [cols (map #(sel % :cols 1) metrics)
        service-names ($ :servicename (nth metrics 0))
	metric-names (concat [:servicename] (keys metric-functions))]
    (col-names (reduce conj-cols service-names cols) metric-names)))