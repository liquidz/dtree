(ns dtree.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.data.csv :as csv]
    )
  )

(defprotocol DataSet
  (labels [this])
  (samples [this]))

(defprotocol Sample
  (label [this])
  (features [this]))

(defrecord IrisDataSet [samples]
  DataSet
  (labels [this] (->> this :samples (map label) distinct))
  (samples [this] (:samples this)))

(defrecord IrisSample [coll]
  Sample
  (label [this] (-> this :coll last int))
  (features [this] (-> this :coll drop-last)))

(defn- read-csv [url]
  (with-open [r (io/reader url)]
    (doall (csv/read-csv r))))

(defn- convert-to-double-list
  [coll]
  (map #(map (fn [s] (Double/parseDouble s)) %) coll))

(defn- convert-last-to-int
  [coll]
  (map #(concat (drop-last %) (list (int (last %)))) coll))

(defn iris
  []
  (->> (io/resource "iris.data")
       read-csv 
       convert-to-double-list
       (map ->IrisSample)
       ->IrisDataSet))

;(defrecord Node [level left-node right-node label]
;  (leaf? [this]
;    (not (or (:left-node this) (:right-node this))))
;  (child-nodes [this]
;    (concat (:left-node this) (:right-node this))
;    )
;  (set-label [this label]
;    (assoc this :label label))
;  ;(classify [this feature]
;  ;  )
;  )

(defn nth-features [docset feature-index]
  (->> (samples docset)
       (map #(nth (features %) feature-index))
       distinct
       sort))


(let [idx 0
      dataset (iris)
      xx (partition 2 1 (nth-features dataset idx))
      t-values (map (fn [x] (/ (apply + x) 2)) xx)
      ]
  (map (fn [t-value]
           (samples dataset)

           ) t-values)
  
  )
;
;(defn build-nodes
;  [& {:keys [n-class samples max-depth min-samples node]
;      :or {max-depth 10, min-samples 3}}]
;
;  (if (>= (:level node) max-depth)
;    (set-label node (label (first samples)))
;    (map
;      (fn [i]
;          )
;      (range (count (first samples))))
;    )
;
;  )
;
;(defn fit [dataset]
;  (let [samples (samples dataset)
;
;        ]
;    )
;  )

