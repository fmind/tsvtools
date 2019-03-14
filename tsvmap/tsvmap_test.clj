#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.java.shell :as Sh])
(require '[clojure.test :refer :all])

(def TEST (partial run-tests *ns*))
(def PROG (partial Sh/sh "./tsvmap.clj"))

(deftest test-help
  (let [{:keys [out exit]} (PROG "--help")]
    (is (not (= out "")))
    (is (= exit 0))))

(deftest test-simple
  (let [in (slurp "examples/simple.in.tsv")
        expect (slurp "examples/simple.out.tsv")
        {:keys [out err exit]} (PROG "-oUNIQ" "-oLINE" "-oIDEN"
                                     "-Oexamples/targets.tsv" :in in)]
    (is (= out expect))
    (is (= exit 0))))

(deftest test-complex
  (let [in (slurp "examples/complex.in.tsv")
        expect (slurp "examples/complex.out.tsv")
        {:keys [out err exit]} (PROG "-oUNIQ" "-oexamples/targets.tsv"
                                     "-OFREQALL" :in in)]
    (is (= out expect))
    (is (= exit 0))))

(defclifn -main "Run all tests." [] (TEST))
