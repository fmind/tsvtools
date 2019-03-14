#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.java.shell :as Sh])
(require '[clojure.test :refer :all])
(require '[clojure.string :as Str])

(def TEST (partial run-tests *ns*))
(def PROG (partial Sh/sh "./tsvdescribe.clj"))

(deftest test-help
  (let [{:keys [out exit]} (PROG "--help")]
    (is (not (= out "")))
    (is (= exit 0))))

(deftest test-simple
  (let [input (slurp "examples/in.tsv")
        expected (slurp "examples/out.tsv")]
    (testing "with header"
      (let [{:keys [out exit]} (PROG :in input)]
        (is (= out expected))
        (is (= exit 0))))
    (testing "without header"
      (let [{:keys [out exit]} (PROG "-n" :in input)]
        (is (= (-> out Str/split-lines)
               (-> expected Str/split-lines next)))
        (is (= exit 0))))))

(defclifn -main "Run all tests." [] (TEST))
