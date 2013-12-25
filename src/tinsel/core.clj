(ns tinsel.core)

(defn- normalize
  [expr]
  (cond (integer? expr)
        `((fn [x#] (get x# ~expr)))
        (keyword? expr)
        (list expr)
        (symbol? expr)
        (list expr)
        (and (seq? expr)
             (contains? #{'fn 'fn*} (first expr)))
        (list expr)
        :else
        expr))

(defn- splice> [expr] `(fn [x#] (~(first expr) x# ~@(rest expr))))
(defn- splice>> [expr ]`(fn [x#] (~@expr x#)))

(defn- thread-forms [splicer x exprs]
  (let [innermost-form `(let [x# ~x
                              log# [['~x ~x]]]
                          [x# log#])]
    (->> (map normalize exprs)
         (interleave exprs)
         (partition 2)
         (reduce
          (fn [inner-form [raw-expr norm-expr]]
            `(let [[x1# log1#] ~inner-form
                   x2# (~(splicer norm-expr) x1#)
                   log2# (conj log1# ['~raw-expr x2#])]
               [x2# log2#]))
          innermost-form))))

(defmacro => [x & exprs] (thread-forms splice> x exprs))
(defmacro =>> [x & exprs] (thread-forms splice>> x exprs))
