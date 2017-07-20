(ns dtree.core
  (:require
    [dtree.dataset :refer :all]))

(defn nth-features
  [samples ^long feature-index]
  (->> samples
       (map #(nth (features %) feature-index))
       distinct
       sort))

(defn thresholds
  [samples feature-index]
  (->> (nth-features samples feature-index)
       (partition 2 1)
       (map #(/ (apply + %) 2))))

(defn- squared [n]
  (* n n))

(defn gini [samples]
  (let [n-all (count samples)]
    (- 1 (transduce
           (map (fn [[_ n]] (squared (/ n n-all))))
           + 0 (frequencies (map label samples))))))

(defn split-samples
  [samples feature-index threshold]
  (let [{t-vec true f-vec false}
        (group-by #(< (nth (features %) feature-index) threshold) samples)]
    [t-vec f-vec]))


(defn feature-index-threshold-pairs
  [samples feature-indexes]
  (mapcat
    (fn [index]
        (map (fn [threshold] [index threshold])
             (thresholds samples index)))
    feature-indexes))

(defn- generate-branch-xform
  [samples]
  (let [n-samples (count samples)
        calc-gini (fn [c1 c2]
                    (/ (+ (* (gini c1) (count c1))
                          (* (gini c2) (count c2)))
                       n-samples))]
    (map (fn [[index threshold]]
           (let [[left right] (split-samples samples index threshold)]
             (if (or (empty? left) (empty? right))
               {:info-gain 1.0}
               {:index     index
                :threshold threshold
                :info-gain (calc-gini left right)
                :left      left
                :right     right}))))))

(defn select-best-branch
  [samples feature-indexes]
  (transduce
    (generate-branch-xform samples)
    (fn 
      ([m] m)
      ([best m]
       (min-key :info-gain best m)))
    {:info-gain 1.0}
    (feature-index-threshold-pairs samples feature-indexes)))

;(defn build-leaf
;  [samples]
;  (->> samples
;       (map label)
;       frequencies
;       (apply max-key second)
;       first
;       (hash-map :label)))
(defn build-leaf
  [samples]
  (->> samples first label (hash-map :label)))

(def ^:dynamic *max-depth* nil)
(def ^:dynamic *min-samples* nil)
(def ^:dynamic *feature-indexes* nil)

(defn- build-tree*
  [samples level]
  (cond
    (>= level *max-depth*)
    (build-leaf samples)

    (apply = (map label samples))
    (build-leaf samples)

    :else
    (let [{:keys [left right] :as res} (select-best-branch samples *feature-indexes*)]
      (cond
        (or (empty? left) (empty? right))
        (build-leaf samples)

        (and *min-samples* (< (max (count left) (count right)) *min-samples*))
        (build-leaf samples)

        :else
        {:threshold (:threshold res)
         :index     (:index res)
         :left      (build-tree* left (inc level))
         :right     (build-tree* right (inc level))}))))


(defn build-tree
  "
  If `feature-indexes` is missing, all features are used to train.
  `min-samples` = nil means no limitation for ...
  "
  [samples & {:keys [level max-depth min-samples feature-indexes]
              :or   {level 1, max-depth 10, min-samples nil}}]
  (binding [*max-depth*       max-depth
            *min-samples*     min-samples
            *feature-indexes* (or feature-indexes
                                  (range (count (features (first samples)))))]
    (build-tree* samples level)))

(defn classify
  [dtree feature]
  (loop [node dtree]
    (let [f (some->> node :index (nth feature))]
      (cond
        (contains? node :label)
        (:label node)

        (< (->> node :index (nth feature)) (:threshold node))
        (recur (:left node))

        :else
        (recur (:right node))))))

(defn accuracy
  [dtree test-samples]
  (let [res (frequencies
              (map #(= (label %) (classify dtree (features %)))
                   test-samples))]
    (double (/ (res true)
               (count test-samples)))))
