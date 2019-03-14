# Rationale

Sparse matrix extracted from row oriented databases are painful to transpose to a column oriented format.

This script help you make this transformation, by encoding the structure to a TSV file of coordinates.

Then, you can sort the coordinates to change their orientation or keep the output structure.

NOTE: this script requires [Clojure Boot](http://boot-clj.com/)

# Usage

```clojure
cat rows.jsonlines | ./tsvcoord.clj -k ID -E ID > coords.tsv
```

where `ID` is the column identifier

# Examples

You can find more examples in the `examples/` folder.
