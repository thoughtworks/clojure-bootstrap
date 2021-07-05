(ns rest-crud-demo.utils.coll-utils)

(defn map-by
  "Returns a map, keys are return values of (keyfn coll-elem). Values
   are either coll elements directly or (valfn coll-elem)."
  ([keyfn coll]
   (map-by keyfn identity coll))
  ([keyfn valfn coll]
   (into {} (map (juxt keyfn valfn)) coll)))
