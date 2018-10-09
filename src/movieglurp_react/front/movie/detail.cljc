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

;; debug
;; (require 'sc.api)

(defonce state (atom 
                {:page 1
                 :movie-record {}}))

(defn get-thumb-path [path]
  (str/join ["../../" "aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"]))

(defn fetch [imdb-id]
  #?(:cljs
     (GET (str/join ["http://localhost:9500/api/movie/" imdb-id])
          :handler
          (fn [response]
            (swap! state assoc :movie-record
                   (:row (js->clj
                          (js/JSON.parse response)
                          :keywordize-keys true)))))))


(defn get-html [imdb-id]
  (let [movie-record (:movie-record @state)
        ;; (comment a (fetch imdb-id))
        ]
    [:div
     [:div "imdb-id:" (pr-str imdb-id)]
     [:a {:href "/"} "Back 2"]
     [:button {:on-click (fn [imdb-id]
                           (fetch "tt0077975"))} (str "Fetch" imdb-id)]

     [:div
      
      (-> @state :movie-record :short-description)
      (-> @state :movie-record :title)
      (card/card-html "context"
                      (-> @state :movie-record :imdb-id)
                      (-> @state :movie-record :title)
                      (-> @state :movie-record :short-description)
                      (get-thumb-path (-> @state :movie-record :poster))
                      (get-thumb-path (-> @state :movie-record :genre)))]
     ]))



