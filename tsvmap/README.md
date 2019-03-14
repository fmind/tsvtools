# Motivation

Linux provides many utilities to handle CSV/TSV files. However, there is use cases when these tools are not enough.

The goal of this script is to provide transformation functions with the full power of a programming language.

NOTE: this script requires [Clojure Boot](http://boot-clj.com/)

# Usage

```
cat input.tsv | ./tsvmap.clj -oIDENT -oUNIQ -OFREQALL > output.tsv 
```

Where -o are positional operators and -O is a catchall operator.

# Examples

You can find examples in the examples/ folder.
