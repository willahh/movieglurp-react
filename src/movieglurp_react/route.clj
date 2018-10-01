(ns movieglurp-react.route
  (:require
   ;; [alloglurp.api.api :as api]
   ;; [alloglurp.front.home :as home]
   ;; [alloglurp.front.movie.detail :as movie-detail]
   ;; [alloglurp.service.session.session :refer [wrap-site-route]]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   [movieglurp-react.model.movie.movie-dao :as movie-dao]
   [movieglurp-react.model.movie.movie-schema :as movie-schema]))

(def site-routes
  (wrap-defaults
   (routes
    (context "/api/movies" []
             (GET "/" request
                  (pr-str (->> (movie-dao/find-list)
                               :response :docs
                               (map movie-schema/map-movie-record-from-query-row)
                               (into []))))
             (GET "/facet" [genre]
                  (pr-str (->> (movie-dao/get-movie-facet genre)
                               (into []))))

             (GET "/home" [genre]
                  (pr-str (->> (movie-dao/find-list-for-home 0 10 genre)
                               :records
                               (into []))))))
   (assoc-in site-defaults [:security :anti-forgery] false)))







(defroutes main-route
  site-routes
  (route/not-found "Not Found"))

(def app
  (-> (routes main-route)
      (wrap-session)))


