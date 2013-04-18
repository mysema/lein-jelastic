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
  (if-let [resp (try 
                  (.upload service auth) 
                  (catch Exception e
                    (do (log "File upload         : FAILED")
                        (log "File does not exist : " dir filename)
                        nil)))]
    (if (zero? (.getResult resp))
      (do (log "File upload : SUCCESS")
          (log "   File url : " (.getFile resp))
          (log "  File size : " (.getSize resp))
          resp)
      (do (log "File upload : FAILED")
          (log "      Error : " (.getError resp))
          nil))))
      
(defn register-file
  [service auth upload-resp]
  (let [resp (.createObject service auth upload-resp)]
    (if (zero? (.getResult resp))
      (do (log "File registration : SUCCESS")
          (log "  Registration ID : " (-> resp (.getResponse) (.getObject) (.getId)))
          (log "     Developer ID : " (-> resp (.getResponse) (.getObject) (.getDeveloper)))
          true)
      (do (log "File registration : FAILED")
          false))))

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
    (if-let [upload-resp (upload-file service auth path file)]
      (register-file service upload-resp auth))))


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

    
