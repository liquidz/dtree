(ns dtree.dataset.iris
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io  :as io]
    [dtree.dataset    :refer :all]))

(defn- read-csv [url]
  (with-open [r (io/reader url)]
    (doall (csv/read-csv r))))

(defn- convert-to-double-list
  [coll]
  (map #(map (fn [s] (Double/parseDouble s)) %) coll))

(defn iris
  []
  (->> (io/resource "iris.data")
       read-csv 
       convert-to-double-list
       (map #(->SimpleSample (drop-last %) (int (last %))))
       ->SimpleDataSet))
