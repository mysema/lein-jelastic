(ns leiningen.jelastic
  (:use [leiningen.help :only (help-for)])
  (:import [com.jelastic JelasticService] 
           [org.apache.tools.ant Project]))

(defn log [& s] (println (apply str s)))

(def ant-project-proxy 
  (proxy [Project] [] 
    (log [s t] (log s))))

(defn jelastic-service
  [apihoster environment context]
  (doto (JelasticService. ant-project-proxy)
    (.setApiHoster apihoster)
    (.setEnvironment environment)
    (.setContext context)))

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
  (let [resp (.createObject service upload-resp auth)]
    (if (zero? (.getResult resp))
      (do (log "File registration : SUCCESS")
          (log "  Registration ID : " (-> resp .getResponse .getObject .getId))
          (log "     Developer ID : " (-> resp .getResponse .getObject .getDeveloper))
          resp)
      (do (log "File registration : FAILED")
          (log "            Error : " (.getError resp)) 
          nil))))

(defn with-auth
  [project f]
  (let [{:keys [apihoster 
                context 
                environment
                email
                password]} (:jelastic project)
        service (jelastic-service apihoster environment context)
        auth    (authenticate service email password)]
    (f service auth)))

(defn upload
  [project service auth] 
  "Upload the current project to Jelastic"
  (let [path    (str (:target-path project) "/")
        ; TODO Is there better way to get project output?
        file    (str (:name project) "-" (:version project) ".war")
        upload-resp (upload-file service auth path file)]
    ; TODO Cleaner way to do this?
    (if (nil? upload-resp)
      nil
      (do (register-file service auth upload-resp)
          upload-resp))))

(defn deploy
  [project service auth] 
  "Upload and deploy the current project to Jelastic"
  (let [upload-resp (upload project service auth)
        deploy-resp (.deploy service auth upload-resp)]
    (if (every? zero? [(.getResult deploy-resp) (-> deploy-resp .getResponse .getResult)])
      (do (log "Deploy file : SUCCESS")
          (log " Deploy log : " (-> deploy-resp .getResponse .getResponses (aget 0) .getOut)))
      (do (log "Deploy file : FAILED")
          (log "      Error : " (-> deploy-resp .getResponse .getError))))))

(defn jelastic
  "Manage Jelastic service"
  {:help-arglists '([upload deploy])
   :subtasks [#'upload #'deploy]}
  ([project]
   (println (help-for "jelastic")))
  ([project subtask & args]
   (with-auth project
     (fn [service auth]
       (case subtask
         "upload" (upload project service auth)
         "deploy" (deploy project service auth))))))


