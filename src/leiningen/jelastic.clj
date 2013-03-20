(ns leiningen.jelastic
 (:use [leiningen.help :only (help-for)]))

(defn upload [] )
(defn deploy [] )


(defn jelastic
  "Manage jelastic service"
  {:help-arglists '([upload deploy])
   :subtasks [#'upload #'deploy]}
  ([project]
   (println (help-for "jelastic")))
  ([project subtask & args]
   (println "Hi! there")))

    
