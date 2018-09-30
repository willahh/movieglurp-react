(ns movieglurp-react.process.crud.list
  (:require [movieglurp-react.component.form.select :as select]))

(defn search-html [& value]
  (let [value (first value)]
    [:form {:action "/admin/search" :method "get"}
     [:div.ui.fluid.category.search
      [:div.ui.icon.input
       [:input.prompt (conj {:placeholder "Search ...", :type "text" :name "q"}
                            (when value {:value value}))]
       [:i.search.icon]]
      [:div.results]]]))

(defn pagination-html [path page offset limit total]
  (when (> total limit)
    (let [page-count (int (Math/ceil (float (/ total limit))))
          visible-count 3
          start-list (max 1 (min (int (- page-count 3)) (max 1 (- page 1))))
          end-list (max (+ visible-count 1) (min page-count (min (+ page (- visible-count 1)) (+ page page-count))))]
      [:div.ui.pagination.menu.tiny {:style "text-align: center;"}
       (when (> page (- visible-count 1)) 
         [:a {:class "item" :href (str path "?page=" 1)} 1])
       (when (> page visible-count)
         [:span {:class "item" } "..."])
       (for [i (range start-list end-list)]
         (let [curr i]
           [:a {:class (str "item" (when (= page curr) " active")) :href (str path "?page=" curr)} curr]))
       (when (< page (+ 1 (- page-count visible-count))) 
         [:span {:class "item" } "..."])
       [:a {:class (str "item" (when (= page end-list) " active")) :href (str path "?page=" page-count)} page-count]])))

(defn filter-option-html [state path page offset limit total]
  [:div.row
   ;; {:style "padding: 12px 0; margin: 0 0 6px 0;"}
   [:div.column
    [:div.ui.grid
     ;; (get-bulk-action-html path)
     (pagination-html path page offset limit total)
     
     (select/select-html "limit" limit
                         (for [i ["2" "5" "10" "25" "50" "100" "250" "500"]]
                           {:name i 
                            :value limit :label i}))
     "Per page"
     [:button {:class "ui button"} "ok"]
     [:div (search-html (:q state))]]]])