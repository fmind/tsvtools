#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.java.shell :as Sh])
(require '[clojure.test :refer :all])

(def TEST (partial run-tests *ns*))
(def PROG (partial Sh/sh "./tsvfold.clj"))

(deftest test-help
  (let [{:keys [out err exit]} (PROG "--help")]
    (is (not-empty out))
    (is (= exit 0))))

(deftest test-simple
  (let [in (slurp "examples/simple.in.tsv")]
    (testing "-k0"
      (let [{:keys [out exit]} (PROG "-k0" :in in)
            expect (slurp "examples/simple.k0.tsv")]
        (is (= out expect))
        (is (= exit 0))))
    (testing "-k1"
      (let [{:keys [out exit]} (PROG "-k1" :in in)
            expect (slurp "examples/simple.k1.tsv")]
        (is (= out expect))
        (is (= exit 0))))
    (testing "-k9"
      (let [{:keys [out exit]} (PROG "-k9" :in in)]
        (is (empty? out))
        (is (= exit 0))))))

(deftest test-complex
  (let [in (slurp "examples/complex.in.tsv")]
    (testing "-k0"
      (let [{:keys [out err exit]} (PROG "-k0" :in in)
            expect (slurp "examples/complex.k0.tsv")]
        (is (= out expect))
        (is (= exit 0))))
    (testing "-k1"
      (let [{:keys [out exit]} (PROG "-k1" :in in)
            expect (slurp "examples/complex.k1.tsv")]
        (is (= out expect))
        (is (= exit 0))))
    (testing "-k9"
      (let [{:keys [out exit]} (PROG "-k9" :in in)]
        (is (empty? out))
        (is (= exit 0))))))

(defclifn -main "Run all tests." [] (TEST))
