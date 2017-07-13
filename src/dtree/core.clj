(ns dtree.core
  (:require
    [clojure.java.io  :as io]
    [clojure.string   :as str]
    [clojure.data.csv :as csv]
    ;[net.cgrand.xforms :as x]
    )
  )

(defprotocol DataSet
  (samples [this] "Return `Sample` sequence."))

(defprotocol Sample
  (label [this] "Return label of this sample.")
  (features [this] "Return feature sequence of this sample."))

(defrecord IrisDataSet [samples]
  DataSet
  (samples [this] (:samples this)))

(defrecord IrisSample [coll]
  Sample
  (label [this] (-> this :coll last int))
  (features [this] (-> this :coll drop-last)))

(defn- read-csv [url]
  (with-open [r (io/reader url)]
    (doall (csv/read-csv r))))

(defn- convert-to-double-list
  [coll]
  (map #(map (fn [s] (Double/parseDouble s)) %) coll))

(defn- convert-last-to-int
  [coll]
  (map #(concat (drop-last %) (list (int (last %)))) coll))

(defn iris
  []
  (->> (io/resource "iris.data")
       read-csv 
       convert-to-double-list
       (map ->IrisSample)
       ->IrisDataSet))

;(defrecord Node [level left-node right-node label]
;  (leaf? [this]
;    (not (or (:left-node this) (:right-node this))))
;  (child-nodes [this]
;    (concat (:left-node this) (:right-node this))
;    )
;  (set-label [this label]
;    (assoc this :label label))
;  ;(classify [this feature]
;  ;  )
;  )

(defn nth-features [samples feature-index]
  (->> samples
       (map #(nth (features %) feature-index))
       distinct
       sort))

(defn t-values [samples feature-index]
  (->> (nth-features samples feature-index)
       (partition 2 1)
       (map #(/ (apply + %) 2))))

(defn- filter-label [samples label*]
  (filter #(= label* (label %)) samples))

(defn labels [samples]
  (distinct (map label samples)))

(defn gini* [samples]
  (transduce 
    (map #(Math/pow (/ (count (filter-label samples %))
                       (count samples))
                    2))
    + (labels samples)))

(defn gini
  [samples feature-index t-value]
  (let [pred (fn [sample] (< (nth (features sample) feature-index) t-value))
        {left true right false} (group-by pred samples)]
    (when (and (seq left) (seq right))
      {:gini (/ (+ (* (count left)  (- 1 (gini* left)))
                   (* (count right) (- 1 (gini* right))))
                (count samples))
       :index feature-index
       :t-value t-value
       :left left
       :right right})))

(defn collect-gini [samples]
  (mapcat
    (fn [index]
        (into [] (comp (map #(gini samples index %))
                       (remove nil?))
              (t-values samples index)))
    (-> samples first features count range)))

(defn build-nodes
  [& {:keys [samples max-depth min-samples]
      :or {max-depth 10, min-samples 3} }]
  (apply min-key :gini (collect-gini samples))
  ; TODO
  ; 2分できていなかったら決定木は作らない
  ;    leaf とする
  ; ノードに属する最小のサンプル数を下回っている場合も決定木は作らない
  ;    過学習対策らしい
  ; ノードを組み立てて、再帰的に子ノードも組み立てる
  )


;(let [samples (samples (iris))
;      ]
;  (apply min-key :gini (collect-gini samples))
;  )

;(defn build-nodes
;  [& {:keys [n-class samples max-depth min-samples node]
;      :or {max-depth 10, min-samples 3}}]
;
;  (apply min-key :gini
;         (pmap #(assoc (gini docset idx %)
;                       :index idx
;                       :t-value %)
;               (t-values docset idx)))
;  (if (>= (:level node) max-depth)
;    (set-label node (label (first samples)))
;    (map
;      (fn [i]
;          )
;      (range (count (first samples))))
;    )
;
;  )
;
;(defn fit [dataset]
;  (let [samples (samples dataset)
;
;        ]
;    )
;  )


