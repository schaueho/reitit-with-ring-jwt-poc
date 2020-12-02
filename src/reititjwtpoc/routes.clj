(ns reititjwtpoc.routes
  (:require
   [reitit.ring :as ring]
   [reitit.middleware :as middleware]
   [ring.middleware.jwt :as jwt]))

(defn ping-handler [_]
  {:status 200, :body "pong"})

;; TODO: Take issuer and secret from environment or component
(def issuers-map
  {"https://some.localhost/issuer" {:alg :HS256 :secret "replaceme"}})

(defn jwt-wrapper [handler]
  (jwt/wrap-jwt handler {:issuers issuers-map}))

(def wrap-jwt
  {:name ::wrap-jwt
   :description "Claims handling via JWT"
   :wrap jwt-wrapper})

(defn authentication-wrapper [handler]
  (fn [request]
    (if (seq (:claims request))
      (do (println "claimed" (:claims request))
          (handler request))
      {:status 401, :body "Unauthorized"})))

(def wrap-auth
  {:name ::wrap-auth
   :description "Ensure authorization"
   :wrap authentication-wrapper})

(defn create-ping-router []
  (ring/router
   [["/"
     ["ping" {:name ::ping
              :description "Ping handler"
              :get ping-handler}]
     ["sping" {:name ::sping
               :description "Secure Ping handler"
               :get {:middleware [[:wrap-jwt] [:wrap-auth]]
                     :handler ping-handler}}]]]
   {::middleware/registry {:wrap-jwt wrap-jwt,
                           :wrap-auth wrap-auth}}))
(defn create-ring-handler []
  (ring/ring-handler
   (create-ping-router)
   (ring/create-default-handler)))
