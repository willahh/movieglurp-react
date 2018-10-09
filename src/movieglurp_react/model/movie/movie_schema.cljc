(ns movieglurp-react.model.movie.movie-schema
  (:require [clojure.string :as str]
            [wlh.helper.string-helper :refer [ellipsis]]))

(defn parseint [str]
  #?(:cljs (js/parseInt str)
     :clj (Integer. str)))

(defn map-movie-record-to-card-record [movie-record-list]
  {:id (:imdb-id movie-record-list)
   :title (:title movie-record-list)
   :description (ellipsis (:short-description movie-record-list) 10)
   ;; :image (str/join ["../" (movie/get-image-path-from-alloid (:alloid movie-record))])
   :image (str/join ["../" "aaaaaaaaaaaaaaaaaaaaaaa"])
   :meta (:genre movie-record-list)})

(defn get-movie-record-from-query-result [query-result]
  (map (fn [m]
         {:genre (:genre m)
          :director (:director m)
          :short-description (first (:director m))
          ;; :time (try (-> m :time first parseint
          ;;            (catch Exception e ""))
          :title (-> m :title first)
          :imdb-id (-> m :imdb-id first)
          :poster (-> m :poster first)
          :id (-> m :id)
          :_version_ (-> m :_version_)})
       (-> query-result
           :response :docs)))

(defn map-solr-response-to-record [solr-response]
  (map (fn [m]
         {:genre (:genre m)
          :director (first (:director m))
          :short-description (first (:director m))
          ;; :time (try (-> m :time first parseint
          ;;            (catch Exception e ""))
          :title (-> m :title first)
          :imdb-id (-> m :imdb-id first)
          :poster (-> m :poster first)
          :id (-> m :id)
          :_version_ (-> m :_version_)})
       (-> solr-response
           :response :docs)))

(defn map-movie-record-from-query-row [query-row]
  {:genre (:genre query-row)
   :director (first (:director query-row))
   :short-description (first (:director query-row))
   :time (-> query-row :time first parseint) 
   :title (-> query-row :title first)
   :imdb-id (-> query-row :imdb-id first)
   :poster (-> query-row :poster first)
   :id (-> query-row :id)
   :_version_ (-> query-row :_version_)})
