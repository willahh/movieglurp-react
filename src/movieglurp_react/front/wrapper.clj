(ns movieglurp-react.front.wrapper
  (:require [hiccup.page :as page]
            [movieglurp-react.front.main :as main]))

(defn wrap-page-html [html]
  "Html wrapper for all admin pages."
  (page/html5 (main/head)
              [:body
               [:div {:id "app"}
                [:div (main/header-html)]
                [:div {:class "ui container"} html]]
               [:script {:type "text/javascript", :src "/dist/dev-main.js"}]
               ]))
