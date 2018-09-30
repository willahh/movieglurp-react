(ns movieglurp-react.component.card
  (:require [clojure.string :as str]))

(defn card-html [context id title description image meta]
  [:a.ui.card
   ;; {:class "ui card" :data-id (str id) :href (str/join [context "/" id])}
   
   [:div 
    [:img {:src image }]]
   [:div {:class "content"}
    [:div {:class "header"}
     title]
    [:div {:class "meta"}
     (str/join "," meta)]
    [:div.description
     ;; {:class "description" :style "min-height: 52px;"}
     description]]
   ])
