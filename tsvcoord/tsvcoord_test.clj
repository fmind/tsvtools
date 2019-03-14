#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.java.shell :as sh])
(require '[clojure.test :as t])

(def TEST (partial t/run-tests *ns*))

(def PROG (partial sh/sh "./tsvcoord.clj"))

(t/deftest help-message
  (let [{:keys [out exit]} (PROG "--help")]
    (t/is (not= out ""))
    (t/is (= exit 0))))

(t/deftest simple-example
  (let [input (slurp "examples/simple.jsonl")]
    (t/testing "with ID"
      (let [{:keys [out exit]} (PROG :in input)
            output (slurp "examples/simple.id.tsv")]
      (t/is (= out output))
      (t/is (= exit 0))))
    (t/testing "without ID"
      (let [{:keys [out exit]} (PROG "-EID" :in input)
            output (slurp "examples/simple.tsv")]
        (t/is (= out output))
        (t/is (= exit 0))))))

(t/deftest complex-example
  (let [input (slurp "examples/complex.jsonl")
        prog (partial PROG "-k_id" "-E_rev")]
    (t/testing "with ID"
      (let [{:keys [out exit]} (prog :in input)
            output (slurp "examples/complex.id.tsv")]
        (t/is (= out output))
        (t/is (= exit 0))))
    (t/testing "without ID"
      (let [{:keys [out exit]} (prog "-E_id" :in input)
            output (slurp "examples/complex.tsv")]
        (t/is (= out output))
        (t/is (= exit 0))))))

(defclifn -main "Run all the tests !" [] (TEST))
