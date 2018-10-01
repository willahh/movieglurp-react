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
                 :genre ""
                 :movie-facet []
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

(defn fetch-actor [genre]
  (def anim-delay 300)
  (swap! state update-in [:genre] (fn [m] genre))
  #?(:cljs
     (do (.map (js/$ ".card") (fn [i m] (.hide (js/$ m) anim-delay)))
         (js/setTimeout
          (fn []
            (GET (str "http://localhost:9500/api/movies/home?genre=" genre)
                 :handler 
                 (fn [response]
                   #?(:cljs
                      ;; $('.card').map((i, m) => $(m).hide(1000))
                      (.map (js/$ ".card") (fn [i m] (.show (js/$ m) anim-delay))))
                   
                   (swap! state update-in [:records] (fn [v]
                                                       #?(:cljs (cljs.reader/read-string response))))))) anim-delay))
     
     )
  )

(defn- facet-html [movie-facet genre-list]
  [:div.ui.labels
   (->> movie-facet
        (map (fn [m]
               [:div {:class (clojure.string/join " " ["ui" "label"
                                                       (when (= (:label m) (:genre @state))
                                                         "red")])
                      :on-click #(fetch-actor (:label m))}
                (:label m)
                [:div.detail (:count m)]
                [:i.icon.close]])))])

(defn- card-list-html [context movie-records-list]
  [:div.ui.stackable.six.column.grid
   ;; {:class "ui stackable six column grid"}

   (let [html-records (map movieglurp-react.model.movie.movie-schema/map-movie-record-to-card-record movie-records-list)]
     (map (fn [html-record]
            [:div.column
             (apply card/card-html context (map #(second %) html-record))])
          html-records))])



(defn fetch-facet []
  (GET (str "http://localhost:9500/api/movies/facet")
       :handler 
       (fn [response]
         (swap! state update-in [:movie-facet]
                (fn [v]
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
     [:button {:on-click (fn [genre]
                           (fetch-actor "action"))} "Fetch actor"]
     [:button {:on-click fetch-facet} "Fetch facet"]
     ;; (debug-html request context session params page-params count offset limit total)
     [:form {:class "left floated" :method "get" :action ""}
      [:input {:type "hidden" :name "page" :value 1}]
      [:div
       (facet-html (:movie-facet @state) genre-list)
       ;; (crud-list/filter-option-html {:limit 10 :q "t"} context page offset limit count)

       (card-list-html context (:records @state))
       

       ]]]))

