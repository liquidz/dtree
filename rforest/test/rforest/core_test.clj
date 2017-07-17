(ns rforest.core-test
  (:require
    [clojure.test       :refer :all]
    [dtree.dataset.iris :refer [iris]]
    [dtree.dataset      :as dd]
    [rforest.core       :refer :all]))

(deftest random-forest
  (let [samples (dd/samples (iris))
        rforest (build-forest samples :tree-num 5 :sample-num 50)
        n       20]
    (is (> (accuracy rforest (bootstrap-samples samples n)) 0.9))))

