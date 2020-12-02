(ns reititjwtpoc.routes-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [reititjwtpoc.token :as token]
   [reititjwtpoc.routes :as sut]))

(deftest ping-handler-tests
  (let [issuer (first (keys sut/issuers-map)) ;; TODO: Properly mock the issuer and secret
        testclaims {:user "Tester" :iss issuer}
        testsecret (get-in sut/issuers-map [issuer :secret])]
    (testing "token creation"
      (is (= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbWUubG9jYWxob3N0L2lzc3VlciIsInVzZXIiOiJUZXN0ZXIifQ.naCn5dqpXLfIi2HCYhijbNAflt3Rkt4D6Jjo4IO_1RM"
             (token/sign-encode-token testclaims testsecret))))

    (testing "ping handler returns pong on get"
      (let [sut-handler (sut/create-ring-handler)
            ping-request {:request-method :get, :uri "/ping"}]
        (is (= {:status 200, :body "pong"} (sut-handler ping-request)))))

    (testing "secure ping handler returns unauthorized without token"
      (let [sut-handler (sut/create-ring-handler)
            sping-request {:request-method :get, :uri "/sping"}]
        (is (= {:status 401, :body "Unauthorized"} (sut-handler sping-request)))))

    (testing "secure ping handler returns pong with correct token"
      (let [sut-handler (sut/create-ring-handler)
            sping-request {:request-method :get, :uri "/sping"}
            sping-request (token/add-jwt-token sping-request testclaims testsecret)]
        (is (= {:status 200, :body "pong"} (sut-handler sping-request)))))))
