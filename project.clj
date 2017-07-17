(defproject ents "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-sub "0.2.4"]
            [lein-codox "0.10.3"]
            ]
  :sub ["dtree"
        "rforest"]
  :codox {:output-path "codox"
          :source-uri "http://github.com/liquidz/ents/blob/{version}/{filepath}#L{line}"
          :source-paths ["dtree/src"
                         "rforest/src"]})
