# Tinsel

Shiny threading macros with tracing. Why the name?
[Because](http://doc.poseur.com/xmas-eve-macros).

## Installation

Artifacts are published on [Clojars][1]. 

![latest version][2]

## Usage

Wouldn't it be nice to use the threading macros (`->`, `->>`) but be
able to see what the intermediate values are? I know, I know, that
would be great, wouldn't it?

[Psst, before going any further, type this into your REPL:
`(require '[tinsel.core :refer [=> =>>]])`.]

So what if this code:

```clojure
(let [a 61
      [result trace]
      (=> (inc a)
           inc
           dec
           (fn [x] (+ x 1))
           #(- % 1)
           (+ 1)
           (* 2)
           (/ 3))]
  (clojure.pprint/pprint {:result result
                          :trace trace}))
```

Produced this output?

```
-| {:result 42,
-|  :trace
-|  [[(inc a) 62]
-|   [inc 63]
-|   [dec 62]
-|   [(fn [x] (+ x 1)) 63]
-|   [(fn* [p1__31059#] (- p1__31059# 1)) 62]
-|   [(+ 1) 63]
-|   [(* 2) 126]
-|   [(/ 3) 42]]}
```

By the way, tinsel supports raw function names, sugared anonymous
functions e.g. `#(- % 1)`, "regular" anonymous functions using the
`fn` special form, integers, and keywords. As an example of the last, `(let
[m {:a 42}] (=> m :a))` evaluates to:

```clojure
[42
 [[m {:a 42}]
  [:a 42]]]
```

As for integers, they work similarly to keywords, selecting the item
at the given index. I implemented this feature after deciding against
doing so because retrieving a particular item in a vector is not
possible to do elegantly as an item in a `->>` (or `=>>`) form,
because `get` takes the collection as its first argument. So, `(=>>
[:a :b :c] 2)` evaluates to `[:c [[[:a :b :c] [:a :b :c]] [2 :c]]]`
i.e. `:c` plus the trace.

## License

Copyright © 2013 Edwin Watkeys

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[1]: https://clojars.org/edw/tinsel
[2]: https://clojars.org/edw/tinsel/latest-version.svg
