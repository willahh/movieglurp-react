(ns movieglurp-react.route

  ;; #?(:cljr
  ;;    (:require [movieglurp-react.route-page :as route-page]))
  
  #?(:clj
     (:require  [compojure.core :as core :refer :all]
                [compojure.route :as route]
                [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
                [ring.middleware.session :refer [wrap-session]]
                [movieglurp-react.route-page :as route-page]
                [movieglurp-react.front.wrapper :as html-wrapper]
                [movieglurp-react.model.movie.movie-schema :as movie-schema]
                [movieglurp-react.front.week.week :as week])
     :cljs
     (:require  [accountant.core :as accountant]
                [secretary.core :as secretary :include-macros true]
                [reagent.core :as reagent :refer [atom]]
                ;; [movieglurp-react.route-page :as route-page]
                [movieglurp-react.front.wrapper :as html-wrapper]
                [movieglurp-react.front.week.week :as week]
                [movieglurp-react.front.movie.list :as list]
                [movieglurp-react.front.movie.detail :as detail]))
  #?(:cljs
     (:require-macros [secretary.core :refer [defroute]])))

(defn home-page []
  [:div "home-page"])

(defonce page (atom #'home-page))

(defn current-page []
  [@page])

;; CLJ
(def site-routes
  #?(:clj
     (wrap-defaults
      (apply routes
             (into [] (for [route route-page/routes]
                        (GET (:uri route) [request]
                             (-> (@ (:handler route) request)
                                 (html-wrapper/wrap-page-html))))))
      (assoc-in site-defaults [:security :anti-forgery] false))))

(def app #?(:clj
            (-> (defroutes main-route
                  site-routes
                  (route/not-found "Not Found"))
                (wrap-session))))





;; CLJS

;; (do #?(:cljs
;;        (defonce page (atom #'home-page))))

(do #?(:cljs
       (do
         (defroute "/" []
           (reset! page (fn []
                          (-> (list/get-html [])
                              (html-wrapper/wrap-page-html)))))

         (defroute "/detail/:imdb-id" [imdb-id]
           (reset! page (fn []
                          (-> (detail/get-html imdb-id)
                              (html-wrapper/wrap-page-html)))))

         (defroute "/week" []
           (reset! page (fn []
                          (-> (week/html-ui [])
                              (html-wrapper/wrap-page-html)))))

         (defroute "/about" []
           (do (reset! page (fn []
                              (-> 
                               (list/get-html [])
                               (html-wrapper/wrap-page-html)))))))))

(defn mount []
  #?(:cljs
     (reagent/render [current-page] (.getElementById js/document "app"))))

(defn init! []
  #?(:cljs
     (do
       (accountant/configure-navigation!
        {:nav-handler
         (fn [path]
           (secretary/dispatch! path))
         :path-exists?
         (fn [path]
           (secretary/locate-route path))})
       (accountant/dispatch-current!)
       (mount))))

(do #?(:cljs
       (init!)))
