# Motivation

Coordinates files are convenient to orient datasets, but they consumes much more disk space than the original data.

The goal of this script is to fold coordinates values based on an ordered index to optimize their storage.

NOTE: this script requires [Clojure Boot](http://boot-clj.com/)

# Usage

```
cat coords.tsv | ./tsvfold.clj -k0 > folds.tsv 
```

Where -k is the index of the ordered key

# Examples

You can find examples in the examples/ folder.
