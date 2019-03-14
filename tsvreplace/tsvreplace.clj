#!/usr/bin/env boot

(merge-env! :dependencies '[[cheshire "5.8.0"]])

(require '[boot.cli :refer [defclifn]]
         '[cheshire.core :as Json]
         '[clojure.string :as Str]
         '[clojure.java.io :as Io])

(set! *warn-on-reflection* false)

(defn rsplit [re s]
  (Str/split s re))

(defn rprint [rows]
  (doseq [row rows]
    (println row)))

(def format-row (partial Str/join \tab))
(def format-rows (partial map format-row))

(def parse-row (partial rsplit #"\t"))
(def parse-rows (partial map parse-row))
(def parse-lines (comp parse-rows line-seq))
(def parse-reader (comp parse-lines Io/reader))
(def parse-targets (comp Json/parse-string slurp))

(defclifn -main
  "Replace values from a tsv file using a json file."
  [t targets JSON file "map of replacement pairs."]
  (let [inrows (parse-reader *in*)
        targets (parse-targets targets)
        outrows (map (partial replace targets) inrows)]
    (rprint (format-rows outrows))))
