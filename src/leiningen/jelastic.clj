(ns leiningen.jelastic
  (:use [leiningen.help :only (help-for)])
  (:import [com.jelastic JelasticService] 
           [org.apache.tools.ant Project]))

(defn log [& s] (apply println s))

(def ant-project-proxy 
  (proxy [Project] [] 
    (log [s t] (log s))))

(defn jelastic-service
  [{:keys [apihoster context environment]}]
  (doto (JelasticService. ant-project-proxy)
    (.setApiHoster apihoster)
    (.setContext context)
    (.setEnvironment environment)))

(defn authenticate
  [service {:keys [email password]}]
  (let [resp (.authentication service email password)]
    (if (zero? (.getResult resp)) 
      
      ((log "Authentication : SUCCESS")
       (log "       Session : " (.getSession resp))
       (log "           Uid : " (.getUid resp))
       resp)
     
      ((log "Authentication : FAILED")
       (log "        Error  : " (.getError resp)) 
       (throw (Exception. (.getError resp)))))))

(defn upload 
  [project] 
  "Upload the current project to Jelastic"
  (let [conf    (:jelastic project)
        service (jelastic-service conf)
        auth    (authenticate service conf)]

    ))


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

    
