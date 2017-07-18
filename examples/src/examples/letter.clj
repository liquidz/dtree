(ns examples.letter
  (:require
    [clojure.java.io :as io]
    [clojure.string  :as str]
    [dtree.dataset   :as dd]
    [rforest.core    :as rf]))

(defn letter-dataset
  []
  (let [s (slurp (io/resource "letter-recognition.data"))
        coll (map #(str/split % #",") (str/split s #"[\r\n]+"))]
    (for [x coll]
      (dd/->SimpleSample (map #(Double/parseDouble %) (rest x))
                         (first x)))))

(let [samples (letter-dataset)
      forest (rf/build-forest samples
                              :sample-num 5000
                              )]
  (rf/accuracy forest (map (fn [_] (rand-nth samples)) (range 50))))

