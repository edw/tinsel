# tinsel

Tinsel: shiny threading macros with tracing.

Why the name? [Because](http://doc.poseur.com/xmas-eve-macros).

## Installation

Add this dependency to your `project.clj`:

```clojure
[edw/tinsel "0.2.0"]
```

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
`fn` special form, or keywords. As an example of the last, `(let
[m {:a 42}] (=> m :a))` evaluates to:

```clojure
[42
 [[m {:a 42}]
  [:a 42]]]
```

That is all for now. I've considered implementing integers, a la
keywords, asbe accessors, but I didn't want to stray too far from
standard-issue Clojure conventions.

## License

Copyright © 2013 Edwin Watkeys

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
