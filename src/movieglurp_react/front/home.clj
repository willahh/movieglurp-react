(ns movieglurp-react.front.home
  (:require [movieglurp-react.component.card :as card]
            [movieglurp-react.front.main :as main]
            [movieglurp-react.model.movie.movie-dao :as movie-dao]
            [movieglurp-react.model.movie.movie-schema :refer [map-movie-record-to-card-record]]
            [movieglurp-react.process.crud.list :as crud-list]
            [movieglurp-react.service.session.session :refer [merge-params-session]]
            [clojure.string :as str]))

(defn- card-list-html [context movie-records-list]
  [:div {:class "ui stackable six column grid"}
   (let [html-records (map map-movie-record-to-card-record movie-records-list)]
     (map (fn [html-record]
            [:div {:class "column"}
             (apply card/card-html context (map #(second %) html-record))])
          html-records))])

(defn get-pagination-offset [page limit count]
  "Get pagination offset from page number, limit and table rows count."
  (* (- page 1) limit))

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

(defn debug-html [request context session params page-params count offset limit total]
  [:div {:style "padding-top: 40px;"}
   [:div "context"
    [:div (pr-str context)]]
   [:div "request"
    [:div (pr-str request)]]
   [:div "params"
    [:div (pr-str params)]]
   [:div
    [:div "session"
     [:div (pr-str session)]]
    [:div "page-params"
     [:div (pr-str page-params)]]
    [:div
     [:div
      "count: "(pr-str count)
      "offset: " (pr-str offset)
      "limit: "(pr-str limit)
      "total:" (pr-str total)]]]])

(defn- facet-html [movie-facet genre-list]
  [:div.ui.labels
   (->> movie-facet
        (map (fn [m]
               [:button {:class (str/join " " ["ui" "label"
                                               (when (contains? (set genre-list) (:label m))
                                                 "red")])
                         :onclick (str/join ["toggleFacet('" (:label m) "');"])}
                (:label m)
                [:div.detail (:count m)]
                [:i.icon.close]])))
   [:input {:type "text" :name "genre" :value (str/join "," genre-list)}]
   [:script
    "function toggleFacet(genre) {
         var el = document.querySelector('input[name=\"genre\"]');
         var genreList = el.value.split(',');
         if (genreList.includes(genre)) {
             var index = genreList.indexOf(genre);
             genreList.splice(index, 1);
             el.value = genreList;
         } else {
            el.value += ',' + genre;
         }
    }
"]])

(defn get-html [request]
  (let [session (:session request)
        params (:params request)
        page-params (merge-params-session (:context request) params session)
        context (:context request)
        page (Integer. (or (:page page-params) 1))

        ;; Parameters
        genre (or (:genre page-params) "")

        genre-list (str/split genre #",")
        
        {total :total
         count :count
         records :records
         offset :offset
         limit :limit} (movie-dao/find-list-for-home session page-params) 
        movie-facet (movie-dao/get-movie-facet genre-list)]
    (-> [:div {:style "padding-top: 20px;"}
         (debug-html request context session params page-params count offset limit total)
         [:form {:class "left floated" :method "get" :action ""}
          [:input {:type "hidden" :name "page" :value 1}]
          [:div
           (facet-html movie-facet genre-list)
           (crud-list/filter-option-html {:limit 10 :q "t"} context page offset limit count)
           (card-list-html context records)]]]
        (main/wrap-page-html request))))



