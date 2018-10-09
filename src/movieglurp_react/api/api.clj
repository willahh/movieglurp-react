(ns movieglurp-react.api.api
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [cheshire.core :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [movieglurp-react.model.movie.movie-dao :as movie-dao]
            [movieglurp-react.model.movie.movie-schema :as movie-schema]
            [clojure.string :as str]))

(require 'sc.api)

(defn query-string-to-params [query-string]
  (if query-string
    (->> (str/split query-string #"&") 
         (map #(str/split % #"=")) 
         (map (fn [[k v]] [(keyword k) v])) 
         (into {}))
    {}))

(defn wrap-solr-response-multiple [solr-response]
  {:message [{:success true :message "ok"}]
   :total (-> solr-response
              :response :numFound)
   :start (-> solr-response :response :start)
   :rows (into [] (-> solr-response
                      (movie-schema/get-movie-record-from-query-result)))})

(defn wrap-solr-response-single [solr-response]
  {:message [{:success true :message "ok"}]
   :row (-> solr-response
            )})

(defroutes app-routes
  (context "/api" []
           (context "/movie" []
                    (GET "/" request
                         (let [session (:session request)
                               params (query-string-to-params (:query-string request))]
                           (-> (movie-dao/find-list)
                               (wrap-solr-response-multiple)
                               (generate-string))))
                    (GET "/:imdb-id" [imdb-id]
                         (-> (movie-dao/find-by-imdb-id imdb-id)
                             (wrap-solr-response-single)
                             (generate-string))))))

(def api-routes
  (-> (handler/api app-routes)
      (wrap-json-body)
      (wrap-json-response)))