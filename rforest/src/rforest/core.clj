(ns rforest.core
  (:require
    [dtree.dataset :as dd]
    [dtree.core    :as dc]))

(defn bootstrap-samples
  [samples n]
  (for [_ (range n)]
    (rand-nth samples)))


(defn build-forest
  [samples & {:keys [tree-num sample-ratio max-depth min-samples]
              :or   {tree-num 10, sample-ratio 2/3, max-depth 10}
              }]
  (let [sample-num (int (* (count samples) sample-ratio))
        feature-num (count (dd/features (first samples)))
        sample-feature-num (Math/sqrt feature-num)
        rand-feature-index #(rand-int feature-num)]
    (for [i (range tree-num)]
      (dc/build-tree (bootstrap-samples samples sample-num)
                     :max-depth max-depth
                     :min-samples min-samples
                     :feature-indexes (repeatedly sample-feature-num rand-feature-index)
                     )))
    )

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

;;;;;;;
;;(require '[clojure.java.io :as io])
;;(require '[clojure.string :as str])
;;(defn letter-dataset
;;  []
;;  (let [s (slurp (io/resource "letter-recognition.data"))
;;        coll (map #(str/split % #",") (str/split s #"[\r\n]+"))]
;;    (for [x coll]
;;      (dd/->SimpleSample (map #(Double/parseDouble %) (rest x))
;;                         (first x)))))
;;
;;(let [samples (letter-dataset)
;;      forest (build-forest samples)
;;      n 100]
;;  (accuracy forest (map (fn [_] (rand-nth samples)) (range 100))))

