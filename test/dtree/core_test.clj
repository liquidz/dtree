(ns dtree.core-test
  (:require [clojure.test :refer :all]
            [dtree.dataset.iris :refer :all]
            [dtree.dataset.dummy :refer :all]
            [dtree.dataset :refer :all]
            [dtree.core :refer :all]))

(deftest nth-feature-test
  (is (= (nth-features (take 3 (samples (iris))) 0)
         [4.7 4.9 5.1])))

(deftest thresholds-test
  (let [samples [(->SimpleSample [1 1] 1)
                 (->SimpleSample [3 3] 3)
                 (->SimpleSample [5 5] 5)]]
    (is (= [2 4] (thresholds samples 0)))))


(deftest gini-test
  (are [expected result] (= expected (int (* 10000 result)))
    3333 (gini* (samples (iris)))))

(deftest split-samples-test
  (let [[left right] (split-samples (samples (iris)) 0 5.0)]
    (are [x y] (= x y)
      22 (count left)
      128  (count right))))

(deftest build-leaf-test
  (let [samples (map #(->SimpleSample [] %) [1 2 1 3 2 1])]
    (is (= (build-leaf samples) {:label 1}))))

(deftest foo-test
  (let [samples       (samples (iris))
        t-value (second (t-values samples 0))
        result  (gini samples 0 t-value)]
    (are [x y] (= x y)
      6484 (int (* 10000 (:gini result)))
      4    (count (:left result))
      146  (count (:right result)))

    (is (nil? (gini samples 0 1.0)))))

(deftest classify-test
  (testing "iris dataset"
    (let [samples (samples (iris))
          dtree   (build-nodes :samples samples)]
      (is (> (accuracy dtree (shuffle samples)) 0.9))))

  (testing "xor dataset"
    (let [samples (samples (dummy))
          dtree (build-nodes :samples samples)]
      (is (> (accuracy dtree (shuffle samples)) 0.5)))))
