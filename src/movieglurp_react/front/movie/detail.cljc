(ns movieglurp-react.front.movie.detail
  #?(:clj
     (:require [clojure.string :as str]
               [ajax.core :refer [GET POST raw-response-format text-response-format]]
               [movieglurp-react.component.card :as card]
               ;; [movieglurp-react.process.movie.movie :as movie]
               [movieglurp-react.front.main :as main]
               ;; [movieglurp-react.model.movie.movie-dao :as movie-dao]
               )
     :cljs (:require
            [clojure.string :as str]
            [ajax.core :refer [GET POST raw-response-format text-response-format]]
            [movieglurp-react.component.card :as card]
            ;; [movieglurp-react.process.movie.movie :as movie]
            [movieglurp-react.front.main :as main]
            ;; [movieglurp-react.model.movie.movie-dao :as movie-dao]
            )))

(defonce state (atom 
                {:page 1
                 :movie-record {}}))

(defn get-thumb-path [movie-record]
  (str/join ["../../" "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"]))



(defn fetch [imdb-id]
  #?(:cljs
     (GET (str "http://localhost:9500/api/movies/detail?imdb-id=" imdb-id)
          :handler 
          (fn [response]
            (let [response-data #?(:cljs (cljs.reader/read-string response))]
              (swap! state update-in [:movie-record]
                     (fn [v]
                       #?(:cljs response-data))))))))


(defn get-html [imdb-id]
  (let [movie-record (:movie-record @state)]
    [:div
     [:button {:on-click (fn [imdb-id]
                           (fetch imdb-id))} "Fetch"]
     ;; {:style "padding-top: 20px;"}
     (card/card-html "context"
                     (:imdb-id movie-record)
                     (:title movie-record)
                     (:short-description movie-record)
                     (get-thumb-path movie-record)
                     (:genre movie-record))]))
