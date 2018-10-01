(ns movieglurp-react.front.movie.list
  #?(:clj
     (:require  
      [clojure.string :as str]
      [ajax.core :refer [GET POST raw-response-format text-response-format]]
      [movieglurp-react.component.card :as card]
      [movieglurp-react.process.crud.list :as crud-list]
      [movieglurp-react.model.movie.movie-schema :refer [map-movie-record-to-card-record]])
     :cljs
     (:require [reagent.core :as reagent :refer [atom]]
               [cljs.reader :as reader]
               [ajax.core :refer [GET POST raw-response-format text-response-format]]
               [movieglurp-react.component.card :as card]
               [movieglurp-react.process.crud.list :as crud-list]
               [movieglurp-react.model.movie.movie-schema :refer [map-movie-record-to-card-record]])))

(defonce state (atom 
                {:page 1
                 :tab "first"
                 :actor-selected ""
                 :user-records []
                 :records []}))

(def *genre* '("Western" "Fantastique" "Comédie" "Péplum" "Drame" "Epouvante-horreur"
               "Thriller" "Guerre" "Comédie dramatique" "Comédie musicale" "Biopic"
               "Romance" "Policier" "Historique" "Aventure" "Action" "Animation"
               "Science fiction"))

(defn- filter-html [page-params]
  (let [genre (:genre page-params)]
    [:form.ui.form {:id "filter-fom"}
     [:script "function on_filter_change(event) {
         console.log('onchange', event);
         //document.querySelector('input[name=\"genre\"]').value;
         document.getElementById('filter-fom').submit();
     };"]
     [:div.field
      [:label
       "First Name"]
      [:div.ui.multiple.dropdown
       [:input
        {:name "genre", :type "hidden" :value genre :onchange "on_filter_change(event);"}]
       [:i.filter.icon]
       [:span.text
        "Filter Posts"]
       [:div.menu
        [:div.ui.icon.search.input
         [:i.search.icon]
         [:input
          {:placeholder "Search tags...", :type "text"}]]
        [:div.divider]
        [:div.header
         [:i.tags.icon]
         "Tag Label"]
        [:div.scrolling.menu
         (for [g *genre*]
           [:div.item {:data-value g}
            [:div.ui.red.empty.circular.label]
            [:span g]])]]]]]))

(defn- facet-html [movie-facet genre-list]
  [:div.ui.labels
   (->> movie-facet
        (map (fn [m]
               [:button {:class (clojure.string/join " " ["ui" "label"
                                                          (when (contains? (set genre-list) (:label m))
                                                            "red")])
                         :onclick (clojure.string/join ["toggleFacet('" (:label m) "');"])}
                (:label m)
                [:div.detail (:count m)]
                [:i.icon.close]])))
   [:input {:type "text" clojure.string/join "genre" :value (clojure.string/join "," genre-list)}]
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

(defn- card-list-html [context movie-records-list]
  [:div.ui.stackable.six.column.grid
   ;; {:class "ui stackable six column grid"}

   (let [html-records (map movieglurp-react.model.movie.movie-schema/map-movie-record-to-card-record movie-records-list)]
     (map (fn [html-record]
            [:div.column
             (apply card/card-html context (map #(second %) html-record))])
          html-records))])

(defn fetch-actor []
  (GET (str "http://localhost:9500/api/movies")
       :handler 
       (fn [response]
         (swap! state update-in [:records] (fn [v]
                                             #?(:cljs (cljs.reader/read-string response)))))))

(defn get-html []
  (let [context "/movie/list"
        genre-list ["action" "aventure"]
        movie-facet [{:name "ok"}]
        page 1
        offset 1
        limit 10
        records []]
    [:div
     [:div (:page @state)]
     [:button {:on-click fetch-actor} "click"]
     ;; (debug-html request context session params page-params count offset limit total)
     [:form {:class "left floated" :method "get" :action ""}
      [:input {:type "hidden" :name "page" :value 1}]
      [:div
       (facet-html movie-facet genre-list)
       ;; (crud-list/filter-option-html {:limit 10 :q "t"} context page offset limit count)

       (card-list-html context (:records @state))
       

       ]]]))

