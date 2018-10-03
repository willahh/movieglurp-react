(ns movieglurp-react.route
  (:require [accountant.core :as accountant]
            [secretary.core :as secretary :include-macros true]
            [reagent.core :as reagent :refer [atom]]
            [movieglurp-react.front.movie.list :as list]
            [movieglurp-react.front.movie.detail :as detail]
            [movieglurp-react.front.week.week :as week]
            [movieglurp-react.front.wrapper :as html-wrapper])
  (:require-macros [secretary.core :refer [defroute]]))

(defn home-page []
  [:div "home-page"])

(defonce page (atom #'home-page)) 

(defn current-page []
  [:div [@page]])

(defroute "/" []
  (reset! page (fn []
                 (-> (list/get-html)
                     (html-wrapper/wrap-page-html)))))

(defroute "/detail/:imdb-id" [imdb-id]
  (reset! page (fn []
                 (-> (detail/get-html imdb-id)
                     (html-wrapper/wrap-page-html)))))

(defroute "/week" []
  (reset! page (fn []
                 (-> (week/html-ui)
                     (html-wrapper/wrap-page-html)))))

(defroute "/about" []
  (do (reset! page (fn []
                     (-> 
                      (list/get-html)
                      (html-wrapper/wrap-page-html))))
      ;; (fetch-actor list/state "action")
      ))

(defn mount []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount))

(init!)
