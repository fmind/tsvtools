#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]]
         '[clojure.string :as Str]
         '[clojure.java.io :as Io])

(set! *warn-on-reflection* false)


(defn rsplit [re s]
  (Str/split s re))

(defn rprint [rows]
  (doseq [row rows]
    (println row)))

(def max-val (partial max-key val))

(def format-row (partial Str/join \tab))
(def format-rows (partial map format-row))

(def parse-row (partial rsplit #"\t"))
(def parse-rows (partial map parse-row))
(def parse-lines (comp parse-rows line-seq))
(def parse-reader (comp parse-lines Io/reader))

(defn describe "Provide statistics about a row."
  [row]
  (when (> (count row) 1)
    (let [[id & vs] row
          fs (frequencies vs)
          count- (-> vs count)
          unique (-> fs keys count)
          [top freq] (apply max-val fs)]
      [id top freq count- unique])))

(def names ["id" "top" "freq" "count" "unique"])

(defclifn -main
  "Generate statistics about categorical values."
  [n noheader bool "don't print the column names."]
  (let [inrows (parse-reader *in*)
        outrows (map describe inrows)
        outrows (remove nil? outrows)]
    (when-not noheader
      (println (format-row names)))
    (rprint (format-rows outrows))))
