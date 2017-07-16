(ns dtree.core
  (:require
    ;[net.cgrand.xforms :as x]
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

(defn gini [samples]
  (let [n-all (count samples)]
    (- 1 (transduce
           (map (fn [[_ n]] (Math/pow (/ n n-all) 2)))
           + 0 (frequencies (map label samples))))))

(defn split-samples
  [samples feature-index threshold]
  (let [{t-vec true f-vec false}
        (group-by #(< (nth (features %) feature-index) threshold) samples)]
    [t-vec f-vec]))


(defn feature-index-threshold-pairs
  [samples]
  (mapcat
    (fn [index]
        (map (fn [threshold] [index threshold])
             (thresholds samples index)))
    (-> samples first features count range)))

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
               {:gini 1.0}
               {:index     index
                :threshold threshold
                :gini      (calc-gini left right)
                :left      left
                :right     right}))))))

(defn select-best-branch
  [samples]
  (transduce
    (generate-branch-xform samples)
    (fn 
      ([m] m)
      ([best m]
       (min-key :gini best m)))
    {:gini 1.0}
    (feature-index-threshold-pairs samples)))

(defn build-leaf
  [samples]
  (->> samples
       (map label)
       frequencies
       (apply max-key second)
       first
       (hash-map :label)))

(defn build-nodes
  [& {:keys [samples level max-depth min-samples]
      :or   {level 1, max-depth 10, min-samples 3}}]
  (if (>= level max-depth)
    (build-leaf samples)
    (let [{:keys [left right] :as res} (select-best-branch samples)
          base-args [:level (inc level) :max-depth max-depth :min-samples min-samples]]
      (cond
        (or (empty? left) (empty? right))
        (build-leaf samples)

        (< (max (count left) (count right)) min-samples)
        (build-leaf samples)

        :else
        {:threshold (:threshold res)
         :index     (:index res)
         :left      (apply build-nodes :samples left base-args)
         :right     (apply build-nodes :samples right base-args)}))))




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

