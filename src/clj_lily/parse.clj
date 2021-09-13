(ns clj-lily.parse
  (:require
    [clojure.string :as str]))
	
(defn format-key [k]
  (let [s (name k) 
        compound (str/split s #"--")
        simple   (map #(str/replace % #"-" " ") compound)
        formatted (map #(if (str/starts-with? % "*") 
                          (subs % 1)
                          (str "\\" %)) 
                       simple)]
    (str/join " " formatted)))

(defn parse [m s]
  (reduce-kv (fn [acc k v]
               (cond
                 (map? v)
                 (parse v (str acc (format-key k) "\n"))

                 (and (vector? v) (vector? (first v)))
                 (parse v acc)

                 (and (vector? v) (keyword? (first v)))
                 (str acc (format-key (first v)) " {" (last v) "}\n")

                 (and (vector? v) (string? (first v)))
                 (str acc (first v) "\n")

                 (= :score k)
                 (str acc 
                      (format-key k) " {\n<<\n"
                      (str/join "}}\n" (map #(parse % "") v))
                      "\n}}\n>>\n")

                 (= :midi k)
                 (str acc 
                      (format-key k) " {\n"
                      (str/join "}\n" (map #(parse % "") v))
                      "}}}\n")

                 (= :version k)
                 (str acc (format-key k) " " "\"" v "\"" "\n")

                 (vector? v)
                 (str acc (format-key k) " " (map format-key v) "\n")

                 :else 
                 (str acc (format-key k) " " v "\n")))
             s
             m))

(defn adorn [s]
  (let [lines (str/split-lines s)
        processed (reduce (fn [acc l]
                           (cond
                             (str/starts-with? l "\\new Staff")
                             (conj acc (str l " {"))

                             (str/starts-with? l "\\new Voice")
                             (conj acc (str l " {"))

                             (str/starts-with? l "\\context")
                             (conj acc (str l " {"))

                             :else
                             (conj acc l)))
                         []
                         lines)]
    (str/join "\n" processed)))

