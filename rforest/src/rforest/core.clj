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
    (pmap
      (fn [_]
        (let [indexes (repeatedly sample-feature-num rand-feature-index)]
          (dc/build-tree (bootstrap-samples samples sample-num)
                         :max-depth max-depth
                         :min-samples min-samples
                         :feature-indexes indexes)))
      (range tree-num))
    ;(for [i (range tree-num)]
    ;  (let [indexes (repeatedly sample-feature-num rand-feature-index)]
    ;    (dc/build-tree (bootstrap-samples samples sample-num)
    ;                 :max-depth max-depth
    ;                 :min-samples min-samples
    ;                 :feature-indexes indexes))
    ;  #_(dc/build-tree (bootstrap-samples samples sample-num)
    ;                 :max-depth max-depth
    ;                 :min-samples min-samples
    ;                 :feature-indexes (repeatedly sample-feature-num rand-feature-index)))
  )
    )

(defn classify
  [rforest feature]
  (->> rforest
       (pmap #(dc/classify % feature))
       frequencies
       (apply max-key second)
       first))

(defn accuracy
  [rforest test-samples]
  ;(map #(= (dd/label %) (classify rforest (dd/features %))))
  ;(filter true?)
  (let [res (pmap #(= (dd/label %) (classify rforest (dd/features %))) test-samples)]
    (double
      (/ (count (filter true? res))
         (count test-samples)))
    )
  )

;;;;;;;
;(require '[clojure.java.io :as io])
;(require '[clojure.string :as str])
;(defn letter-dataset
;  []
;  (let [s (slurp (io/resource "letter-recognition.data"))
;        coll (map #(str/split % #",") (str/split s #"[\r\n]+"))]
;    (for [x coll]
;      (dd/->SimpleSample (map #(Double/parseDouble %) (rest x))
;                         (first x)))))
;
;(let [samples (shuffle (letter-dataset))
;      [train-data test-data] (split-at 1500 samples)
;      forest (time (doall (build-forest train-data :tree-num 100 :max-depth 15 :sample-ratio 1/10)))
;      ]
;  (time (doall (pmap #(classify forest (dd/features %)) test-data)))
;  ;(time (pmap #(= (dd/label %) (classify forest (dd/features %))) test-data))
;  ;(time (filter true? (pmap #(= (dd/label %) (classify forest (dd/features %))) test-data)))
;  ;(time (double (/ (count (filter true? (pmap #(= (dd/label %) (classify forest (dd/features %))) test-data)))
;  ;         (count test-data)
;  ;         )))
;  1
;  )
;
