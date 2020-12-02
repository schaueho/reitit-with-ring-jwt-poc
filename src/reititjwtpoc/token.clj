(ns reititjwtpoc.token
  (:require [clojure.walk :refer [stringify-keys walk]])
  (:import (java.util UUID HashMap)
           (com.auth0.jwt.algorithms Algorithm)
           (com.auth0.jwt JWT JWTCreator$Builder)))

;; code mostly reworked from ring-jwt/jwt-test-utils,
;; cf. https://github.com/ovotech/ring-jwt/blob/master/src/ring/middleware/jwt_test_utils.clj
(defn- walk-map [map f]
  (walk f identity map))

(defn- recurse-hash-map [map]
  (let [updated     (walk-map map (fn [[k v]] (if (map? v) [k (recurse-hash-map v)] [k v])))
        stringified (stringify-keys updated)]
    (HashMap. stringified)))

;; The java library refuses to accept maps as claims so we are using reflection here to force maps into the claims
(def ^:private payload-claims
  (.getDeclaredField JWTCreator$Builder "payloadClaims"))

(defn- force-add-claim [token key value]
  (.setAccessible payload-claims true)
  (let [claims (.get payload-claims token)]
    (.put claims key value))
  token)

(defn- add-claim [token [k v]]
  (let [key (name k)]
    (cond
      (map? v) (force-add-claim token key (recurse-hash-map v))
      (vector? v) (force-add-claim token key v)
      :else (.withClaim token key v))))

(defn sign-encode-token [claims secret]
  (let [hashed (Algorithm/HMAC256 secret)
        jwtoken (JWT/create)
        payload (->> claims
                     (reduce add-claim jwtoken))]
    (.sign payload hashed)))

(defn add-jwt-token [request claims secret]
  (assoc-in request [:headers "Authorization"]
            (str "Bearer " (sign-encode-token claims secret))))

