(ns leiningen.jelastic
  (:use [leiningen.help :only (help-for)])
  (:import [com.jelastic JelasticService] 
           [org.apache.tools.ant Project]))

(def ant-project-proxy 
  (proxy [Project] [] 
    (log [s t] (println s))))

(defn jelastic-service
  [args] 
  (let [service (JelasticService. ant-project-proxy)]
    (doto service
        (.setApiHoster (:apihoster args))
        (.setContext (:context args))
        (.setEnvironment (:environment args)))
    service))

(defn authenticate
  [service email password]
  (let [resp (.authentication service email password)]
    (if (zero? (.getResult resp)) 
      resp
      false)))

(defn upload 
  [project] 
  "Upload the current project to Jelastic"
  (let [conf (:jelastic project)
        service (jelastic-service conf)]
    (authenticate service (:email conf) (:password conf))))


(defn deploy 
  [project] 
  "Deploy the current project to Jelastic")


(defn jelastic
  "Manage Jelastic service"
  {:help-arglists '([upload deploy])
   :subtasks [#'upload #'deploy]}
  ([project]
   (println (help-for "jelastic")))
  ([project subtask & args]
   (case subtask
     "upload" (apply upload project args))))

    
