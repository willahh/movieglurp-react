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
                 :records []
                 :context ""
                 :offset 1
                 :limit 10
                 :total 0}))

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
           [:div.item {:key g :data-value g}
            [:div.ui.red.empty.circular.label]
            [:span g]])]]]]]))

(defn fetch-actor [genre]
  (def anim-delay 300)
  (swap! state update-in [:genre] (fn [m] genre))
  #?(:cljs
     (do (-> (js/$ ".card")
             (.map (fn [i m] (.hide (js/$ m) anim-delay))))
         (js/setTimeout
          (fn []
            (GET (str "http://localhost:9500/api/movies/home?genre=" genre)
                 :handler 
                 (fn [response]
                   #?(:cljs
                      (-> (js/$ ".card")
                          (.map (fn [i m] (.show (js/$ m) anim-delay)))))
                   ;; (:context @state)
                   ;; (:page @state)
                   ;; (:offset @state)
                   ;; (:limit @state)
                   ;; (:total @state)
                   (let [response-data #?(:cljs (cljs.reader/read-string response))]
                     (swap! state update-in [:total] (fn [v]
                                                       (count (:records response-data))))
                     (swap! state update-in [:records]
                            (fn [v]
                              #?(:cljs (-> response-data
                                           :records)))))))) anim-delay))))

(defn- facet-html [movie-facet genre-list]
  [:div.ui.labels
   (doall (->> movie-facet
               (map (fn [m]
                      [:div {:key (:label m)
                             :class (clojure.string/join " " ["ui" "label"
                                                              (when (= (:label m) (:genre @state))
                                                                "red")])
                             :on-click #(fetch-actor (:label m))}
                       (:label m)
                       [:div.detail (:count m)]
                       [:i.icon.close]]))))])

(defn- card-list-html [context movie-records-list]
  [:div.ui.stackable.six.column.grid
   ;; {:class "ui stackable six column grid"}

   (let [html-records (map movieglurp-react.model.movie.movie-schema/map-movie-record-to-card-record movie-records-list)]
     (map (fn [html-record]
            [:div.column {:key (or (:imdb-id html-record) (rand-int 999))}
             (apply card/card-html context (map #(second %) html-record))
             ])
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
        records []
        ;; a (fetch-actor "action")
        ]
    [:div
     [:div "Page: "(:page @state) "Total:" (:total @state)]
     [:div "about:" [:a {:href "/detail?imdb-id=1"} "detail"]]
     [:button {:on-click (fn [genre]
                           (fetch-actor "action"))} "Fetch actor"]
     [:button {:on-click fetch-facet} "Fetch facet"]
     ;; (debug-html request context session params page-params count offset limit total)
     [:form {:class "left floated" :method "get" :action ""}
      [:input {:type "hidden" :name "page" :value 1}]
      [:div
       (facet-html (:movie-facet @state) genre-list)
       (crud-list/filter-option-html {:q "t"} (:context @state) (:page @state) (:offset @state) (:limit @state) (:total @state))
       (card-list-html context (:records @state))
       ]]]))





