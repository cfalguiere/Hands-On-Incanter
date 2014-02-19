(ns hoincanter.core
  (:use [incanter.stats :only [quantile]]))

(defn q [p serie] (quantile serie :probs [p])) 