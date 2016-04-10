(defproject fugue-app "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [replumb "0.2.1"]]
  :plugins [[lein-cljsbuild "1.1.3"]]
  :cljsbuild {
    :builds [{
      :source-paths ["src"]
      :compiler {
        :output-to "resources/public/js/main.js"
        :optimizations :simple
        :pretty-print true}}]})
