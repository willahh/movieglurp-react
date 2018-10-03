(ns ^:figwheel-hooks-react movieglurp-react.core
  (:require
   [goog.dom :as gdom]
   [ajax.core :refer [GET POST raw-response-format text-response-format]]
   [reagent.core :as reagent :refer [atom]]
   [secretary.core :as secretary :include-macros true]
   [accountant.core :as accountant]
   [movieglurp-react.front.movie.list :as list]
   [movieglurp-react.front.movie.detail :as detail] 
   [movieglurp-react.front.wrapper :as html-wrapper])
  (:require-macros [secretary.core :refer [defroute]]))

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

(defn fetch-actor [state genre]
  (def anim-delay 300)
  (swap! state update-in [:genre] (fn [m] genre))
  (do (-> (js/$ ".card")
          (.map (fn [i m] (.hide (js/$ m) anim-delay))))
      (js/setTimeout
       (fn []
         (GET (str "http://localhost:9500/api/movies/home?genre=" genre)
              :handler 
              (fn [response]
                (-> (js/$ ".card")
                    (.map (fn [i m] (.show (js/$ m) anim-delay))))
                ;; (:context @state)
                ;; (:page @state)
                ;; (:offset @state)
                ;; (:limit @state)
                ;; (:total @state)
                (let [response-data (cljs.reader/read-string response)]
                  (swap! state update-in [:total] (fn [v]
                                                    (count (:records response-data))))
                  (swap! state update-in [:records]
                         (fn [v]
                           (-> response-data
                               :records))))))) anim-delay)))

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

(defroute "/test" []
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
