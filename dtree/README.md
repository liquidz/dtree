# ents/dtree

Decision tree implementation by Clojure.

## Notice

This is alpha version.
API will be changed.

## Usage

* Iris classification
```clj
(ns foo.core
  (:require
    [dtree.dataset.iris :refer :all]
    [dtree.dataset      :refer :all]
    [dtree.core         :refer :all]))

(let [samples (samples (iris))
      dtree   (build-tree samples)]
  (accuracy dtree (shuffle samples)))
```

## License

Copyright © 2017 Masashi Iizuka

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
