(ns movieglurp-react.front.wrapper
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [movieglurp-react.front.main :as main]))

(defn wrap-page-html [html]
  "Html wrapper for all admin pages."
  [:div (main/header-html)
   [:div {:class "ui container"}html]])
