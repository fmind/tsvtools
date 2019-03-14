#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]]
         '[clojure.java.io :as io]
         '[clojure.string :as str])

(defn kth [k coll]
  (nth coll k))

(defn krm [k coll]
  (concat (subvec coll 0 k)
          (subvec coll (inc k))))

(defn split [re s]
  (str/split s re))

(defn rprint [rows]
  (doseq [row rows]
    (println row)))

(def lines (comp (partial remove str/blank?)
               line-seq io/reader))

(def format-cols (partial str/join \tab))
(def format-rows (partial map format-cols))

(def parse-cols (partial split #"\t"))
(def parse-rows (comp (partial map parse-cols) lines))

(defn fold "Regroup rows on an ordered index."
  [k rows]
  (let [fid (partial kth k)  ;; select the k-value
        fvs (partial krm k)] ;; remove the k-value
    (for [[f1 :as fs] (partition-by fid rows)]
      (cons (fid f1) (mapcat fvs fs)))))

(defclifn -main
  "Regroup the lines of a file on an index value."
  [k col KEY int "Index of the column (default: 0)"]
  (let [col (or col 0)
        ok? #(> (count %) col)
        inrows (parse-rows *in*)
        inrows (filter ok? inrows)
        outrows (fold col inrows)]
    (rprint (format-rows outrows))))
