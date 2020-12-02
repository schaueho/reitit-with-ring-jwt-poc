(ns reititjwtpoc.token-test
  (:require
   [clojure.test :refer [deftest is]]
   [reititjwtpoc.token :as sut]))

(deftest sign-encode-token-returns-an-encoded-string
  (let [claims {:user "Tester" :iss "http://test.localhost/issuer"}
        secret "mysecret"
        encoded "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vdGVzdC5sb2NhbGhvc3QvaXNzdWVyIiwidXNlciI6IlRlc3RlciJ9.Ct6Z-Zuhy3Dfo1Vhed_Fau1FBJIPxj4g-eF4jW9aC1c"]
    (is (= encoded (sut/sign-encode-token claims secret)))))
