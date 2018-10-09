(ns movieglurp-react.route
  ;; #?(:cljr
  ;;    (:require [movieglurp-react.route-page :as route-page]))
  
  #?(:clj
     (:require  [compojure.core :as core :refer :all]
                [compojure.route :as route]
                [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
                [ring.middleware.session :refer [wrap-session]]
                [movieglurp-react.api.api :as api]
                [movieglurp-react.route-page :as route-page]
                [movieglurp-react.front.wrapper :as html-wrapper])
     :cljs
     (:require  [accountant.core :as accountant]
                [secretary.core :as secretary :include-macros true]
                [reagent.core :as reagent :refer [atom]]
                [movieglurp-react.route-page :as route-page]
                [movieglurp-react.front.wrapper :as html-wrapper]))
  #?(:cljs
     (:require-macros [secretary.core :refer [defroute]])))

(defn home-page []
  [:div "home-page"])

(defonce page (atom #'home-page))

(defn current-page []
  [@page])

(def site-routes
  #?(:clj
     (wrap-defaults
      (apply routes
             (into [] (for [route route-page/routes]
                        (GET (:uri route) [request]
                             (-> ((:handler route) request)
                                 (html-wrapper/wrap-page-html))))))
      (assoc-in site-defaults [:security :anti-forgery] false))))

(do #?(:clj
       (defroutes main-route
         site-routes
         api/api-routes
         (route/not-found "Not Found"))))

(def app #?(:clj
            (-> (core/routes main-route)
                (wrap-session))))

#?(:cljs
   (into [] (for [route route-page/routes]
              (defroute (:uri route) []
                (reset! page (fn []
                               (-> ((:handler route) [])
                                   (html-wrapper/wrap-page-html))))))))

#?(:cljs
   (do
     (accountant/configure-navigation! {:nav-handler
                                        (fn [path]
                                          (secretary/dispatch! path))
                                        :path-exists?
                                        (fn [path]
                                          (secretary/locate-route path))})
     (accountant/dispatch-current!)
     (reagent/render [current-page] (.getElementById js/document "app"))))
