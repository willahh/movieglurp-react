(ns ^:figwheel-hooks-react movieglurp-react.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary :include-macros true]
   [accountant.core :as accountant]
   [movieglurp-react.front.movie.list :as list]
   [movieglurp-react.front.movie.detail :as detail] 
   [movieglurp-react.front.wrapper :as html-wrapper]))

(defn home-page []
  [:div [:h2 "Welcome to movieglurp-re-react"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About-react movieglurp-re"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defonce page (atom #'home-page))

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

(secretary/defroute "/" []
  (reset! page (fn []
                 (-> (list/get-html)
                     (html-wrapper/wrap-page-html)))))

(secretary/defroute "/detail" [imdb-id]
  (reset! page (fn []
                 (-> (detail/get-html imdb-id)
                     (html-wrapper/wrap-page-html)))))

(secretary/defroute "/about" []
  (reset! page (fn []
                 (-> (list/get-html)
                     (html-wrapper/wrap-page-html)))))

(secretary/defroute "/test" []
  (reset! page #'about-page))

(init!)




(defn multiply [a b] (* a b))



;; (println "This text is printed from src/alloglurp_re/core.cljs. Go ahead and edit it and see reloading in action.")


;; (defn multiply [a b] (* a b))


;; ;; define your app data so that it doesn't get over-written on reload
;; (defonce app-state (atom {:text "Hello world!"}))

;; (defn get-app-element []
;;   (gdom/getElement "app"))

;; (defn hello-world []
;;   [:div
;;    [:h1 (:text @app-state)]
;;    [:h3 "Edit this in src/alloglurp_re/core.cljs   6 and watch it change!"]])

;; (defn mount [el]
;;   (reagent/render-component [hello-world] el))

;; (defn mount-app-element []
;;   (when-let [el (get-app-element)]
;;     (mount el)))

;; ;; conditionally start your application based on the presence of an "app" element
;; ;; this is particularly helpful for testing this ns without launching the app
;; (mount-app-element)

;; ;; specify reload hook with ^;after-load metadata
;; (defn ^:after-load on-reload []
;;   (mount-app-element)
;;   ;; optionally touch your app-state to force rerendering depending on
;;   ;; your application
;;   ;; (swap! app-state update-in [:__figwheel_counter] inc)
;; )
