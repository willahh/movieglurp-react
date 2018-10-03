(ns movieglurp-react.route
  (:require
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   [movieglurp-react.model.movie.movie-dao :as movie-dao]
   [movieglurp-react.model.movie.movie-schema :as movie-schema]))

(def site-routes
  (wrap-defaults
   (routes
    (context "/" []
             (GET "/week" [] "a"))
    (context "/api/movies" []
             (GET "/" request
                  (pr-str (->> (movie-dao/find-list)
                               :response :docs
                               (map movie-schema/map-movie-record-from-query-row)
                               (into []))))
             (GET "/detail" [imdb-id]
                  (pr-str (->> (movie-dao/find-by-imdb-id imdb-id)
                               )))
             (GET "/facet" [genre]
                  (pr-str (->> (movie-dao/get-movie-facet genre)
                               (into []))))

             (GET "/home" [genre]
                  (pr-str (->> (movie-dao/find-list-for-home 0 10 genre)
                               ;; :records (into [])
                               )))))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defroutes main-route
  site-routes
  (route/not-found "Not Found"))

(def app
  (-> (routes main-route)
      (wrap-session)))


