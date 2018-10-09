(ns movieglurp-react.component.card
  (:require [clojure.string :as str]))

(defn card-html [context id title description image meta]
  [:a.ui.card {:key id :href (str "/detail/" id)}
   ;; {:class "ui card" :data-id (str id) :href (str/join [context "/" id])}
   [:div 
    [:img {:src image :style {:max-width "60px"}}]]
   [:div {:class "content"}
    [:div {:class "header"}
     title]
    [:div {:class "meta"}
     (str/join "," meta)]
    [:div.desription
     {:style {:min-height "60px"}} description]]])
