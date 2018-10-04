(ns movieglurp-react.route
  #?(:clj
     (:require  [compojure.core :as core]
                [compojure.route :as route]
                [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
                [ring.middleware.session :refer [wrap-session]]
                [movieglurp-react.front.wrapper :as html-wrapper]
                [movieglurp-react.model.movie.movie-schema :as movie-schema]
                [movieglurp-react.front.week.week :as week])
     :cljs
     (:require  [accountant.core :as accountant]
                [secretary.core :as secretary :include-macros true]
                ;; [secretary.core :as secretary]
                [reagent.core :as reagent :refer [atom]]
                [movieglurp-react.front.wrapper :as html-wrapper]
                [movieglurp-react.front.week.week :as week]
                [movieglurp-react.front.movie.list :as list]))
  #?(:cljs
     (:require-macros [secretary.core :refer [defroute]])))

;; CLJ
(def site-routes
  #?(:clj
     (wrap-defaults
      (core/routes
       (core/context "/week" []
                     (core/GET "/" []
                               (-> (week/html-ui)
                                   (html-wrapper/wrap-page-html)))))
      (assoc-in site-defaults [:security :anti-forgery] false))))

(do #?(:clj
       (core/defroutes main-route
         site-routes
         (route/not-found "Not Found"))))

(def app #?(:clj
            (-> (core/routes main-route)
                (wrap-session))))

;; CLJS
(defn home-page []
  [:div "home-page"])

(do #?(:cljs
       (defonce page (atom #'home-page))))
(defonce page (atom #'home-page))

(defn current-page []
  [@page])

(do #?(:cljs
       (do
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
