(ns movieglurp-react.route
  (:require [accountant.core :as accountant]
            [movieglurp-react.front.movie.list :as list]
            [movieglurp-react.front.movie.detail :as detail]
            [movieglurp-react.front.wrapper :as html-wrapper])
  (:require-macros [secretary.core :refer [defroute]]))


(defroute "/" []
  (reset! page (fn []
                 (-> (list/get-html)
                     (html-wrapper/wrap-page-html)))))

(defroute "/detail/:imdb-id" [imdb-id]
  (reset! page (fn []
                 (-> (detail/get-html imdb-id)
                     (html-wrapper/wrap-page-html)))))

(defroute "/about" []
  (do (reset! page (fn []
                     (-> 
                      (list/get-html)
                      (html-wrapper/wrap-page-html))))
      ;; (fetch-actor list/state "action")
      ))

(defn current-page []
  [:div [@page]])

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

(defn multiply [a b] (* a b))