(defproject ents "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ents/dtree "0.1.0-SNAPSHOT"]
                 [ents/rforest "0.1.0-SNAPSHOT"]
                 ]
  :plugins [[lein-sub "0.2.4"]
            [lein-codox "0.10.3"]
            ]
  :sub ["dtree"
        "rforest"]
  :codox {:output-path "codox"
          :source-uri "http://github.com/liquidz/ents/blob/{version}/{filepath}#L{line}"
          :source-paths ["dtree/src"
                         "rforest/src"]})
