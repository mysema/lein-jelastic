(ns leiningen.jelastic
  (:use [leiningen.help :only (help-for)])
  (:import [com.jelastic JelasticService] 
           [org.apache.tools.ant Project]))

(defn log [& s] (println (apply str s)))

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

(defn upload-file
  [service auth dir filename]
  (doto service (.setDir dir) (.setFilename filename))
  (if-let [upload-resp (try 
                         (.upload service auth) 
                         (catch Exception e
                           (do (log "File upload : FAILED")
                               (log "File does not exist : " dir filename)
                               nil)))]
    (if (zero? (.getResult upload-resp))
      (do (log "File upload : SUCCESS")
          (log "   File url : " (.getFile upload-resp))
          (log "  File size : " (.getSize upload-resp)))
      (do (log "File upload : FAILED")
          (log "      Error : " (.getError upload-resp))
          (throw (Exception. (.getError upload-resp)))))))
       
(defn upload
  [project] 
  "Upload the current project to Jelastic"
  (let [{:keys [apihoster 
                context 
                environment
                email
                password]} (:jelastic project)
        path    (str (:target-path project) "/")
        ; TODO Is there better way to get project output?
        file    (str (:name project) "-" (:version project) ".war")
        service (jelastic-service apihoster context environment)
        auth    (authenticate service email password)]
    (upload-file service auth path file)))


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

    
