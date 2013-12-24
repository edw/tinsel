(ns tinsel.core)

(def ^:dynamic *trace* nil)

(defn- normalize
  [expr]
  (cond (keyword? expr)
        (list expr)
        (symbol? expr)
        (list expr)
        (and (seq? expr)
             (contains? #{'fn 'fn*} (first expr)))
        (list expr)
        :else
        expr))

(defn- trace
  [x exprs trace-fn]
  (loop [exprs exprs
         form `(do (swap! *trace* conj ['~x ~x]) ~x)]
    (if (seq exprs)
      (let [expr (first exprs)
            n-expr (normalize expr)]
        (recur (rest exprs) (trace-fn expr n-expr form)))
      `(binding [*trace* (atom [])] [~form @*trace*]))))

(defmacro =>
  [x & exprs]
  (trace x exprs
         (fn [expr n-expr x]
           `(let [x# (~(first n-expr) ~x ~@(rest n-expr))]
              (swap! *trace* conj [(quote ~expr) x#])
              x#))))

(defmacro =>>
  [x & exprs]
  (trace x exprs
         (fn [expr n-expr x]
           `(let [x# (~@n-expr ~x)]
              (swap! *trace* conj [(quote ~expr) x#])
              x#))))
