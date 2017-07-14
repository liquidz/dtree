(ns dtree.dataset)

(defprotocol DataSet
  (samples [this] "Return `Sample` sequence."))

(defprotocol Sample
  (label [this] "Return label of this sample.")
  (features [this] "Return feature sequence of this sample."))
