(ns rforest.core
  (:require
    [dtree.dataset :as dd]
    [dtree.core    :as dc]))

(defn bootstrap-samples
  [samples n]
  (for [_ (range n)]
    (rand-nth samples)))

(defn build-forest
  [samples & {:keys [tree-num sample-num max-depth min-samples]
              :or   {tree-num 10, sample-num 50, max-depth 10, min-samples 3}}]
  (for [i (range tree-num)]
    (dc/build-tree (bootstrap-samples samples sample-num)
                   :max-depth max-depth :min-samples min-samples)))

(defn classify
  [rforest feature]
  (first (max-key second (frequencies (map #(dc/classify % feature) rforest))))
  (->> rforest
       (map #(dc/classify % feature))
       frequencies
       (apply max-key second)
       first))

(defn accuracy
  [rforest test-samples]
  (double
    (/ (count (filter #(= (dd/label %) (classify rforest (dd/features %)))
                      test-samples))
       (count test-samples))))
