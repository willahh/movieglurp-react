(ns movieglurp-react.process.movie.image
  (:require [clojure.string :as str]
            [clj-http.client :as client]
            [movieglurp-react.model.movie.movie-dao :as movie-dao]))

(defn get-image-path-from-imdb-id [imdb-id]
  (str/join ["asset/image/" imdb-id "_thumb" ".jpg"]))

(defn get-image-path-from-imdb-id2 [image-name imdb-id]
  (str/join ["resources/public/asset/image/" image-name]))

(defn create-thumb-image-from-imdb-id [imdb-id]
  (let [image-name (str/join [imdb-id "_thumb" ".jpg"])
        image-path (get-image-path-from-imdb-id2 image-name imdb-id)
        file (clojure.java.io/as-file image-path)]
    (when-not (.exists file)
      (clojure.java.io/copy
       (:body (client/get (:poster (movie-dao/find-by-imdb-id imdb-id)) {:as :stream}))
       (java.io.File. image-path)))
    image-name))

