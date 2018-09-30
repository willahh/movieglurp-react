(ns movieglurp-react.process.imbd-scrapper.list
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [pl.danieljanus.tagsoup :as tagsoup]
            [clojure.data.json :as json])
  (:use [clj-webdriver.taxi]
        [clj-webdriver.driver :only [init-driver]]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

(set-driver! (init-driver {:webdriver (PhantomJSDriver. (DesiredCapabilities.))}))

(defn cleanup [str]
  "Removes excess spaces at the beginning and end of the chain, as well as line
breaks."
  (if str (-> (clojure.string/replace str #"\n" "")
              (clojure.string/replace #" +$" "")
              (clojure.string/replace #"^ +" "")
              (clojure.string/replace #"" ""))
      ""))

(defn remove-meta-itemprop [html]
  (str/replace html #"<meta itemprop=\"(\w+)\" content=\"(.+)\">" ""))

;; (defn write-to-file [parsed-data-map file-path]
;;   (with-open [output-buffer (io/writer (str/join ["resources/data/" file-path]))]
;;     (.write output-buffer (with-out-str (json/pprint parsed-data-map)))))

;; (defn file-path-from-url [url]
;;   (str/join [(-> url
;;                  (str/replace #"https:\/\/www\.imdb\.com\/" "")
;;                  (str/replace #"/" "_")
;;                  (str/replace #"\?" "-")
;;                  (str/replace #"&" "-"))
;;              ".json"]))

(defn get-parsed-html-from-url [url]
  (get-url url)
  (-> (html "body")
      (remove-meta-itemprop)
      (html/html-snippet)))

;; (defn scrap-data-from-url-and-write-to-file [scrap-fn url]
;;   (let [file-path (file-path-from-url url)]
;;     (write-to-file (scrap-fn url) file-path)))

(def get-parsed-html-from-url-memoized (memoize get-parsed-html-from-url))

(defn get-movie-list-data [url]
  "(get-movie-list-data \"https://www.imdb.com/movies-in-theaters\")
   (get-movie-list-data \"https://www.imdb.com/movies-coming-soon\")"
  (let [parsed-html (get-parsed-html-from-url-memoized url)
        items (-> parsed-html
                  (html/select [:.list_item]))
        html-row (take 1 items)]
    (letfn [(get-id [html-row]
              (try (second (re-find #"\/title\/(\w+)\/"
                                    (-> html-row
                                        (html/select [:.overview-top :h4 :a])
                                        first :attrs :href)))
                   (catch Exception e "-")))
            (get-title [html-row]
              (try (-> html-row
                       (html/select [:.overview-top :h4 :a])
                       first :content first cleanup)
                   (catch Exception e "-")))
            (get-pg [html-row]
              (try (-> html-row
                       (html/select [:.certimage])
                       first :attrs :title cleanup)
                   (catch Exception e "-")))
            (get-time [html-row]
              (try (-> html-row
                       (html/select [:time])
                       first :content first cleanup
                       (str/replace " min" "")
                       (Integer.))
                   (catch Exception e "-")))
            (get-genre [html-row]
              (try (-> html-row
                       (html/select [:.cert-runtime-genre :span])
                       first :content first cleanup)
                   (catch Exception e "-")))
            (get-short-description [html-row]
              (try (-> html-row
                       (html/select [:.outline])
                       first :content first cleanup)
                   (catch Exception e "-")))
            (get-director [html-row]
              (try (let [a (-> html-row
                               (html/select [:.txt-block])
                               first (html/select [:a]) first)]
                     {:name (-> a :content first cleanup)
                      :id (second (re-find #"\/name/(\w+)\/" (-> a :attrs :href)))})
                   (catch Exception e "-")))
            (get-stars [html-row]
              (into [] (map (fn [m]
                              {:name (-> m :content first cleanup)
                               :id (second (re-find #"\/name\/(\w+)\/" (-> m :attrs :href)))})
                            (-> html-row
                                (html/select [:.txt-block])
                                second (html/select [:a])))))
            (get-poster [html-row]
              (-> html-row
                  (html/select [:.poster])
                  first :attrs :src))
            (map-list-data [html-row]
              {:director (get-director html-row)
               :genre (get-genre html-row)
               :pg (get-pg html-row)
               :poster (get-poster html-row)
               :short-description (get-short-description html-row)
               :stars (get-stars html-row)
               :time (get-time html-row)
               :title (get-title html-row)
               :imdb-id (get-id html-row)})]
      (map map-list-data items))))


;; Top rated movie list
(defn get-top-rated-movie-list [url]
  (let [parsed-html (get-parsed-html-from-url-memoized url)
        items (-> parsed-html
                  (html/select [:.lister-list :tr]))]
    (letfn [(map-list-data [html-row]
              {:id (let [a (-> html-row
                               (html/select [:.titleColumn :a])
                               first)]
                     (second (re-find #"\/title\/(\w+)\/" (-> a :attrs :href))))
               :thumb (-> html-row
                          (html/select [:.posterColumn :img])
                          first :attrs :src)
               :name (let [a (-> html-row
                                 (html/select [:.titleColumn :a])
                                 first)]
                       :name (-> a :content first))
               :rank (let [a (-> html-row
                                 (html/select [:.titleColumn])
                                 first)]
                       (second (re-find #"(\w+)\." (-> a :content first cleanup))))
               :date (-> html-row
                         (html/select [:.titleColumn :.secondaryInfo])
                         first :content first (str/replace "(" "") (str/replace ")" ""))
               :imdb-rating (-> html-row
                                (html/select [:.imdbRating])
                                first :content second :content first)})]
      (map map-list-data items))))

(defn get-movie-detail [url]
  (let [parsed-html (get-parsed-html-from-url-memoized url)
        items (-> parsed-html (html/select [:body]))]
    {:title
     (-> items (html/select [:h1])
         first :content first cleanup)
     :rating
     (-> items (html/select [:.ratingValue :span])
         first :content first)
     :year
     (-> items (html/select [:#titleYear :a])
         first :content first)
     :audience
     (-> items (html/select [:.subtext])
         first :content first cleanup)
     :time
     (-> items (html/select [:time])
         first :content first cleanup)
     :genre (into []
                  (let [a
                        (-> items
                            (html/select [:.subtext :a]))]
                    (map (fn [m]

                           (-> m :content first cleanup)) a)))
     :summary
     (-> items
         (html/select [:.summary_text])
         first :content first cleanup)}))

(defn get-movie-list-data-from-search [url]
  (into []
        (let [parsed-html (get-parsed-html-from-url-memoized url)
              nodes (-> parsed-html
                        (html/select [:.lister-item]))]
          (letfn [(map-list-data [node]
                    {:director (try (-> node
                                        (html/select [:p :a])
                                        first :content first cleanup)
                                    (catch Exception e ""))
                     :genre (try (-> node
                                     (html/select [:.genre])
                                     first :content first cleanup) 
                                 (catch Exception e ""))
                     :pg (try (-> node
                                  (html/select [:.text-muted :.certificate])
                                  first :content first cleanup)
                              (catch Exception e ""))
                     :poster (try (-> node
                                      (html/select [:img])
                                      first :attrs :src)
                                  (catch Exception e ""))
                     :short-description (try (-> node
                                                 (html/select [:.text-muted])
                                                 (nth 2) :content first cleanup) 
                                             (catch Exception e ""))
                     :stars (try (-> node
                                     (html/select [:.rating-rating :span])
                                     first :content first cleanup)
                                 (catch Exception e ""))
                     :time (try (-> node
                                    (html/select [:.text-muted :.runtime])
                                    first :content first cleanup (str/replace " min" ""))
                                (catch Exception e ""))
                     :title (try (-> node
                                     (html/select [:.lister-item-header :a])
                                     first :content first cleanup)
                                 (catch Exception e ""))
                     :imdb-id (try (second (re-find #"\/title\/(\w+)\/"
                                                    (-> node
                                                        (html/select [:.lister-item-header :a])
                                                        first :attrs :href cleanup)))
                                   (catch Exception e ""))})]
            (map map-list-data nodes)))))
