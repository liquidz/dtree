(ns dtree.dataset.dummy
  (:require
    [dtree.dataset :refer :all]))

(defrecord DummyDataSet [coll]
  dtree.dataset/DataSet
  (samples [this] (:coll this)))

(defrecord DummySample [coll]
  dtree.dataset/Sample
  (label [this] (-> this :coll :label))
  (features [this] (-> this :coll :feature)))

(defn dummy
  []
  (->DummyDataSet
    [(->DummySample {:feature [0.0 0.0] :label 0})
     (->DummySample {:feature [0.1 0.0] :label 0})
     (->DummySample {:feature [0.0 0.1] :label 0})
     (->DummySample {:feature [0.1 0.1] :label 0})

     (->DummySample {:feature [1.0 0.0] :label 1})
     (->DummySample {:feature [1.0 0.1] :label 1})
     (->DummySample {:feature [0.9 0.0] :label 1})
     (->DummySample {:feature [0.9 0.1] :label 1})

     (->DummySample {:feature [0.0 1.0] :label 1})
     (->DummySample {:feature [0.0 0.9] :label 1})
     (->DummySample {:feature [0.1 1.0] :label 1})
     (->DummySample {:feature [0.1 0.9] :label 1})

     (->DummySample {:feature [1.0 1.0] :label 0})
     (->DummySample {:feature [1.0 0.9] :label 0})
     (->DummySample {:feature [0.9 1.0] :label 0})
     (->DummySample {:feature [0.9 0.9] :label 0})]))
