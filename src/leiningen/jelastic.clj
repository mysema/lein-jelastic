(ns leiningen.jelastic
  (:use [leiningen.help :only (help-for)])
  (:import [com.jelastic JelasticService] 
           [org.apache.tools.ant Project]))

(def ant-project=proxy 
  (proxy [Project] [] 
    (log [s t] (println s))))

(defn jelastic-service
  [args] 
  (let [service (JelasticService. ant-project=proxy)]
    (doto service
        (.setApiHoster (:apihoster args))
        (.setContext (:context args))
        (.setEnvironment (:environment args)))
    service))

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

    
