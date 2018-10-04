(ns movieglurp-react.front.wrapper
  (:require [hiccup.page :as page]
            ;; [hiccup.core :as core]
            [movieglurp-react.front.main :as main]))

(defn wrap-page-html [html]
  "Html wrapper for all admin pages."
  (page/html5 (main/head)
              [:body {:class "bg-light"}
               [:div.container {:id "app"} (main/header-html) html]
               [:script {:type "text/javascript", :src "/dist/dev-main.js"}]]))



