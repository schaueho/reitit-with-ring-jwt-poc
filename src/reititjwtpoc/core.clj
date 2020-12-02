(ns reititjwtpoc.core
  (:require
   [ring.adapter.jetty :as jetty]
   [reititjwtpoc.routes :refer [create-ring-handler]])
  (:gen-class))

(def app (atom nil))

(defn restart-app []
  (reset! app (create-ring-handler)))

(defn start []
  (-> (restart-app)
      (jetty/run-jetty {:port 3001, :join? false, :async true})))

(defonce server (atom (start)))

(defn restart []
  (when @server
    (.stop @server))
  (reset! server (start)))

(defn -main
  [& args]
  (println "Hello, World!")
  (println "Please kill the process to stop the server"))

