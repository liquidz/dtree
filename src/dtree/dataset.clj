(ns dtree.dataset)

(defprotocol DataSet
  (samples [this] "Return `Sample` sequence."))

(defprotocol Sample
  (label [this] "Return label of this sample.")
  (features [this] "Return feature sequence of this sample."))

(defrecord SimpleDataSet [coll]
  DataSet
  (samples [this] (:coll this)))

(defrecord SimpleSample [fs l]
  Sample
  (label [this] (:l this))
  (features [this] (:fs this)))
