#!/usr/bin/env boot

(merge-env! :dependencies '[[cheshire "5.8.0"]])

(require '[boot.cli :refer [defclifn]]
         '[cheshire.core :as json]
         '[clojure.string :as str]
         '[clojure.java.io :as io])

(def CMAP {\tab (char-escape-string \tab)
           \newline (char-escape-string \newline)})

(defn escape
  [cm s]
  (if-not (string? s) s
    (str/escape s cm)))

(defn print-rows
  [rows]
  (doseq [r rows]
    (println r)))

(def format-val (partial escape CMAP))
(def format-row (partial str/join \tab))
(def format-rows (partial map format-row))

(def parse-lines (comp line-seq io/reader))
(def parse-datalines (comp (partial remove str/blank?) parse-lines))
(def parse-jsonlines (comp (partial map json/parse-string) parse-datalines))

(defn encode "From record to coordinates."
  [exclude rowid index]
  (when (contains? index rowid)
    (let [row (format-val (index rowid))
          cols (apply dissoc index exclude)
          cols (map format-val (keys cols))]
      (map vector (repeat row) cols))))

(defclifn -main
  "Encode a jsonlines file of records to a tsv file of coordinates."
  [E exclude KEYS #{str} "Columns to exclude from the encoding."
   k rowid   KEY    str  "Row identifier (default: ID)"]
  (let [rowid (or rowid "ID")
        indexes (parse-jsonlines *in*)
        encoder (partial encode exclude rowid)
        coordinates (mapcat encoder indexes)]
    (print-rows (format-rows coordinates))))
