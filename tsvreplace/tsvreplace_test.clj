#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.java.shell :as Sh])
(require '[clojure.test :refer :all])

(def TEST (partial run-tests *ns*))
(def PROG (partial Sh/sh "./tsvreplace.clj"))

(deftest test-help
  (let [{:keys [out exit]} (PROG "--help")]
    (is (not (= out "")))
    (is (= exit 0))))

(deftest test-example
  (let [input (slurp "examples/in.tsv")
        expected (slurp "examples/out.tsv")
        {:keys [out exit]} (PROG "-t" "examples/targets.json" :in input)]
    (is (= out expected))
    (is (= exit 0))))

(defclifn -main "Run all tests." [] (TEST))
