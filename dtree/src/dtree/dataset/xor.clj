(ns dtree.dataset.xor
  (:require
    [dtree.dataset :refer :all]))

(defn xor
  []
  (->SimpleDataSet
    [(->SimpleSample [0.0 0.0] 0)
     (->SimpleSample [0.1 0.0] 0)
     (->SimpleSample [0.0 0.1] 0)
     (->SimpleSample [0.1 0.1] 0)

     (->SimpleSample [1.0 0.0] 1)
     (->SimpleSample [1.0 0.1] 1)
     (->SimpleSample [0.9 0.0] 1)
     (->SimpleSample [0.9 0.1] 1)

     (->SimpleSample [0.0 1.0] 1)
     (->SimpleSample [0.0 0.9] 1)
     (->SimpleSample [0.1 1.0] 1)
     (->SimpleSample [0.1 0.9] 1)

     (->SimpleSample [1.0 1.0] 0)
     (->SimpleSample [1.0 0.9] 0)
     (->SimpleSample [0.9 1.0] 0)
     (->SimpleSample [0.9 0.9] 0)]))
