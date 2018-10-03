(ns movieglurp-react.front.main)

(def nav [{:key "Home" :label "Home" :href "/"}
          {:key "Week" :label "Week" :info "Get last week movies" :href "/week"}
          {:key "Actors" :label "Actors" :info "Show actors" :href "/actor"}])

(defn head []
  [:head
   [:title "Alloglurp!"]
   [:link {:type "text/css", :href "/lib/material_icon.css" :rel "stylesheet"}]
   [:script {:type "text/javascript", :src "/lib/jquery-3.1.1.min.js"}]
   [:script {:type "text/javascript", :src "/semantic/dist/semantic.min.js"}]
   [:script {:type "text/javascript", :src "/js/main.js"}]
   [:link {:type "text/css", :href "/semantic/dist/semantic.min.css" :rel "stylesheet"}]
   [:link {:type "text/css", :href "/css/main.css" :rel "stylesheet"}]])

(defn nav-html [nav]
  [:div {:class "ui large secondary inverted pointing menu"}
   [:a {:class "toc item"}
    [:i {:class "sidebar icon"}]]
   (map (fn [row]
          [:a {:key (:label row) :class "item" :href (:href row)} (:label row)]) nav)])

(defn header-html []
  [:div
   [:div.ui {:class "ui inverted menu"}
    [:div {:class "header item"} "Glurps!"]
    [:div {:class "ui container"}
     (nav-html nav)]]])

(defn breadcrumb-html []
  [:div {:class "ui large breadcrumb"}
   [:a.section "Home"]
   [:i {:class "right chevron icon divider"}]
   [:a.section "Actor"]
   [:i {:class "right chevron icon divider"}]
   [:a.section "List"]])

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