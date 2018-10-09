(ns movieglurp-react.route-page
  (:require [movieglurp-react.front.wrapper :as html-wrapper]
            [movieglurp-react.front.week.week :as week]
            [movieglurp-react.front.movie.list :as list]
            [movieglurp-react.front.movie.detail :as detail]))

(defn test2 [request]
  [:div "test2"])

(def routes
  [{:uri "/"
    :handler list/get-html}
   {:uri "/week"
    :handler week/html-ui}
   {:uri "/week2"
    :handler week/html-ui}
   {:uri "/detail/:idb-id"
    :handler (fn [imdb-id]
               (detail/get-html imdb-id))}
   {:uri "/test"
    :handler test2}
   {:uri "/test2"
    :handler (fn [request]
               [:div "a"])}])
