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

(defn get-thumb-path [path]
  (str/join ["../../" "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"]))

(defn fetch [imdb-id]
  #?(:cljs
     (GET (str "http://localhost:9500/api/movies/detail?imdb-id=" imdb-id)
          :handler 
          (fn [response]
            
            (let [response-data #?(:cljs (cljs.reader/read-string response))]
              (do (js/console.log response)
                  (swap! state assoc :movie-record response-data))
              ;; (swap! state update-in [:movie-record]
              ;;        (fn [v]
              ;;          response-data))
              )))))


(defn get-html [imdb-id]
  (let [movie-record (:movie-record @state)
        a (fetch imdb-id)]
    [:div
     [:div "imdb-id:" (pr-str imdb-id)]
     [:a {:href "/"} "Back 2"]
     [:button {:on-click #(fetch imdb-id)} "Fetch" " " imdb-id]
     ;; {:style "padding-top: 20px;"}
     (-> @state :movie-record :short-description)
     (-> @state :movie-record :title)
     (card/card-html "context"
                     (-> @state :movie-record :imdb-id)
                     (-> @state :movie-record :title)
                     (-> @state :movie-record :short-description)
                     (get-thumb-path (-> @state :movie-record :poster))
                     (get-thumb-path (-> @state :movie-record :genre)))
     ]))



