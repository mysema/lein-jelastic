(ns leiningen.jelastic
  (:use [leiningen.help :only (help-for)])
  (:import [com.jelastic JelasticService] 
           [org.apache.tools.ant Project]))

(defn log [& s] (apply println s))

(def ant-project-proxy 
  (proxy [Project] [] 
    (log [s t] (log s))))

(defn jelastic-service
  [apihoster context environment]
  (doto (JelasticService. ant-project-proxy)
    (.setApiHoster apihoster)
    (.setContext context)
    (.setEnvironment environment)))

(defn authenticate
  [service email password]
  (let [resp (.authentication service email password)]
    (if (zero? (.getResult resp)) 
      (do (log "Authentication : SUCCESS")
          (log "       Session : " (.getSession resp))
          (log "           Uid : " (.getUid resp))
          resp)
      (do (log "Authentication : FAILED")
          (log "        Error  : " (.getError resp)) 
          (throw (Exception. (.getError resp)))))))

(defn upload
  [service auth dir filename]
  (doto service (.setDir dir) (.setFilename filename))
  (let [upload-resp (.upload service auth)]
    (if (zero? (.getResult upload-resp))
      (do (log "File upload : SUCCESS")
          (log "   File url : " (.getFile upload-resp))
          (log "  File size : " (.getSize upload-resp)))
      (do (log "File upload : FAILED")
          (log "      Error : " (.getError upload-resp))
          (throw (Exception. (.getError upload-resp)))))))
       
(defn upload-task
  [project] 
  "Upload the current project to Jelastic"
  (let [conf    (:jelastic project)
        service (jelastic-service conf)
        auth    (authenticate service conf)]

    ))


(defn deploy-task
  [project] 
  "Deploy the current project to Jelastic")


(defn jelastic
  "Manage Jelastic service"
  {:help-arglists '([upload-task deploy-task])
   :subtasks [#'upload-task #'deploy-task]}
  ([project]
   (println (help-for "jelastic")))
  ([project subtask & args]
   (case subtask
     "upload" (apply upload-task project args))))

    
