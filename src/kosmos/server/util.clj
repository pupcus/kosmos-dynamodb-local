(ns kosmos.server.util
  (:require [clojure.java.classpath :refer [classpath-jarfiles]]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))

(defn create-unique-tmp-dir!
  "Creates a unique temporary directory on the filesystem.
  Returns a java.io.File object pointing to the new directory.
  Raises an exception if the directory couldn't be created after 10000 tries."
  ([] (create-unique-tmp-dir! ""))
  ([name]
   (let [base-dir (System/getProperty "java.io.tmpdir")
         base-name (str (if-not (empty? name) name "dir") "-" (java.util.UUID/randomUUID))
         tmp-base (str base-dir java.io.File/separator base-name)
         max-attempts 10000]
     (loop [num-attempts 1]
       (if (= num-attempts max-attempts)
         (throw (Exception. (str "Failed to create temporary directory after " max-attempts " attempts.")))
         (let [tmp-dir-name (str tmp-base "-" num-attempts ".d")
               tmp-dir (io/as-file tmp-dir-name)]
           (if (.mkdir tmp-dir)
             tmp-dir
             (recur (inc num-attempts)))))))))

(defn- unpack [entry stream dest-dir]
  (let [dest-file (io/as-file (str dest-dir java.io.File/separator (.getName entry)))]
    (when (not (.isDirectory entry))
      (.mkdirs (io/as-file (.getParent dest-file)))
      (io/copy stream dest-file))))

(defn expand [src to-dest-dir]
  (with-open [zis (java.util.zip.ZipInputStream. (io/input-stream (io/as-file src)))]
    (loop [entry (.getNextEntry zis)]
      (when entry
        (log/debug "entry" (.getName entry))
        (unpack entry zis to-dest-dir)
        (recur (.getNextEntry zis))))))


(defn get-jar-location [artifact]
  (let [pattern (re-pattern (str ".*/" artifact "-.*?.jar"))
        location (->> (classpath-jarfiles)
                      (map (comp :name bean))
                      (keep (fn [jar] (re-matches pattern jar)))
                      first)]
    (or location :not-found)))

(defn setup-native-libraries []
  (let [temp-directory (create-unique-tmp-dir! "dynamodb-natives")
        target-jar-file (get-jar-location "kosmos-dynamodb-local-native")]
    (.deleteOnExit temp-directory)
    (expand target-jar-file (.getPath temp-directory))
    (System/setProperty "sqlite4java.library.path" (.getPath temp-directory))
    temp-directory))

(defn camelize [input-string]
  (let [words (str/split input-string #"[\s_-]+")]
    (str/join "" (cons (str/lower-case (first words)) (map str/capitalize (rest words))))))

(defn argify [input-string]
  (as-> input-string %
    (camelize %)
    (str "-" %)))

(defn scrub-arg [arg-name] {:pre [(string? arg-name)]}
  (let [length (count arg-name)
        flag? (.endsWith arg-name "?")
        arg-name (if flag?
                   (subs arg-name 0 (dec length))
                   arg-name)]
    [flag?  (argify arg-name)]))

(defn build-command-line-args [component]
  (reduce
   (fn [strs [[flag? option] value]]
     (cond-> strs
       (not flag?)
       (conj option)

       (not flag?)
       (conj (str value))

       (and flag? (true? value))
       (conj option)))
   []
   (remove
    nil?
    (map
     (fn [[k v]]
       (when v
         [(scrub-arg (name k)) v]))
     (select-keys
      component
      [:cors? :port :in-memory? :db-path :delay-transient-statuses? :optimize-db-before-startup? :shared-db?])))))

(defn ensure-directory-exists [directory] {:pre [(string? directory)]}
  (let [dir (io/as-file directory)]
    (when-not (.exists dir)
      (.mkdir dir))))

(defn delete [target]
  (let [target (io/as-file target)]
    (when (.isDirectory target)
      (doseq [f (.listFiles target)]
        (delete f)))
    (io/delete-file target)))

