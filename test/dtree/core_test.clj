(ns dtree.core-test
  (:require [clojure.test :refer :all]
            [dtree.core :refer :all]))

(deftest gini*-test
  (are [expected result] (= expected (int (* 10000 result)))
    3333 (gini* (samples (iris)))))

(deftest gini-test
  (let [samples       (samples (iris))
        t-value (second (t-values samples 0))
        result  (gini samples 0 t-value)]
    (are [x y] (= x y)
      6484 (int (* 10000 (:gini result)))
      4    (count (:left result))
      146  (count (:right result)))

    (is (nil? (gini samples 0 1.0)))))


