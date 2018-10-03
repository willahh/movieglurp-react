(ns movieglurp-react.component.form.select)

(defn select-html [field-name selected-value options & conf]
  (let [selected-option (into {} (filter #(= (:value %) selected-value) options))]
    [:div.ui.selection.dropdown
     ;; {:class "ui selection dropdown" :style "min-width: 1em;"}
     [:div.menu
      ;; {:class "menu" :style "min-width: 20em;"}

      (for [option options]
        [:div (conj {:key (:name option)
                     :class "item" :data-value (:name option) :data-name (:name option)}
                    ;; (when (= (:name option) (:value option))
                    ;;   {:selected "true"})
                    (when (:on-click option) 
                      {:onclick (:on-click option)}))
         (when (:icon option) 
           (:icon option))
         (:label option)
         ])
      ]]))