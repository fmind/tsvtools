#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.string :as Str])
(require '[clojure.java.io :as Io])

(defn split [re s]
  (Str/split s re))

(defn rsplit [n rows]
  (split-at n rows))

(defn rprint [rows]
  (doseq [row rows]
    (println row)))

(defn assert-file-exists [file]
  (if (and (some? file) (-> file Io/as-file .exists)) true
      (throw (AssertionError. (str "File does not exist: " file)))))

(def lines (comp (partial remove Str/blank?)
               line-seq Io/reader))

(def format-cols (partial Str/join \tab))
(def format-rows (partial map format-cols))

(def parse-cols (partial split #"\t"))
(def parse-rows (comp (partial map parse-cols) lines))
(def parse-index (comp (partial into (hash-map)) parse-rows))

                                        ; OPERATORS

(defn IDEN []
  identity)

(defn LINE []
  (let [ln (atom 0)]
    (fn [_] (swap! ln inc))))

(defn UNIQ []
  (let [incr (atom -1)
        uniq (atom nil)]
    (fn [x]
      (if (= x @uniq) @incr
          (do (reset! uniq x)
              (swap! incr inc))))))

(defn FILE [file]
  (assert-file-exists file)
  (partial get (parse-index file)))

(defn str->operator [s]
  (case s
    "LINE" (LINE)
    "IDEN" (IDEN)
    "UNIQ" (UNIQ)
    (FILE s)))

(defn seq->operator [fs]
  (fn [coll] ;; coll is always the longest
    (let [coll (concat coll (repeat nil))]
      (for [[f x] (mapv vector fs coll)] (f x)))))

                                        ; CATCHALLS

(defn IDENALL []
  identity)

(defn FREQALL []
  (comp flatten seq frequencies))

(defn FILEALL [file]
  (assert-file-exists file)
  (partial mapv (parse-index file)))

(defn str->catchall [s]
  (case s
    "IDENALL" (IDENALL)
    "FREQALL" (FREQALL)
    (FILEALL s)))

                                        ; FUNCTIONS

(defn tsvmap "Apply operators and catchall to the rows"
  [operators catchall rows]
  (let [rspliter (partial rsplit (count operators))
        operator (seq->operator operators)]
  (for [[left right] (map rspliter rows)]
    (concat (operator left) (catchall right)))))

(defclifn -main "Transform tsv fields with custom functions.

Operators:
- IDEN  : identity
- LINE  : line number
- UNIQ  : auto-incremented index
- [FILE]: value mapped from a file

Catchalls:
- IDENALL: identity (default)
- FREQALL: frequency of values
- [FILE] : values mapped from a file"
  [o operator OP [str] "List of operator functions"
   O catchall CA  str  "A single catchall function"]
  (let [inrows (parse-rows *in*)
        catchall (str->catchall catchall)
        operators (map str->operator operator)
        outrows (tsvmap operators catchall inrows)]
    (rprint (format-rows outrows))))
