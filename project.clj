(defproject lein-jelastic "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.jelastic/ant-task "1.1"]
                 [com.google.code.gson/gson "2.2.2"]
                 [org.apache.ant/ant "1.8.2"]
                 [org.codehaus.jackson/jackson-mapper-asl "1.8.1"]
                 [org.apache.httpcomponents/httpcore "4.1.1"]
                 [org.apache.httpcomponents/httpclient "4.1.1"]
                 [org.apache.httpcomponents/httpmime "4.1.1"]
                 [commons-codec/commons-codec "1.4"]
                 [commons-logging/commons-logging "1.1.1"]]
  :repositories [["project" "file:repo"]
                 ["oss" "https://oss.sonatype.org/content/groups/public/"]] 
  )
