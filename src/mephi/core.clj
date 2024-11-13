(ns mephi.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [buddy.core.hash :as hash]
            [clojure.java.io :as io]))

;; --- Helper Functions ---

(defn current-epoch-seconds
  "Returns the current time in seconds since the Unix epoch."
  []
  (quot (System/currentTimeMillis) 1000))

(defn sha-512-hash
  "Hashes the input string using SHA-512."
  [str]
  (hash/sha512 str))

(defn hex-to-bytes
  "Converts a hex string into a byte array."
  [hex]
  (let [len (count hex)
        bytes (byte-array (quot len 2))]
    (dotimes [i (quot len 2)]
      (let [byte (Integer/parseInt (subs hex (* 2 i) (* 2 (inc i))) 16)]
        (aset-byte bytes i (byte byte))))
    bytes))

(defn bytes-to-hex
  "Converts a byte array into a hex string."
  [bytes]
  (apply str (map #(format "%02x" (bit-and % 0xFF)) bytes)))

(defn bytes-to-string
  "Converts a byte array into a UTF-8 string."
  [bytes]
  (String. bytes "UTF-8"))

(defn wrap-in-quotes
  "Wraps the given string in double quotes."
  [s]
  (str "\"" s "\""))

(defn strip-quotes
  "Removes wrapping double quotes from a string if they exist."
  [s]
  (if (and (.startsWith s "\"") (.endsWith s "\""))
    (subs s 1 (dec (count s)))
    s))

;; --- Main Crypto Logic ---

(def secret-key "FACE THE SIN, SAVE THE E.G.O")

(defn get-initial-secret-key-index
  "Calculates the initial index for the secret key based on the encrypted time."
  [encrypted-time]
  (mod encrypted-time (count (sha-512-hash secret-key))))

(defn encrypt
  "Encrypts bytes using the secret key and the encrypted-time."
  [bytes encrypted-time]
  (let [key (sha-512-hash secret-key)
        bytes-len (count bytes)
        start-index (get-initial-secret-key-index encrypted-time)]
    (->> (mapv (fn [i]
                (bit-xor (nth bytes i)
                         (nth key (mod (+ start-index i) (count key)))))
              (range bytes-len))
         bytes-to-hex
         wrap-in-quotes)))

(defn decrypt
  "Decrypts a hex string using the secret key and the encrypted-time."
  [hex-string encrypted-time]
  (let [key (sha-512-hash secret-key)
        bytes (hex-to-bytes (strip-quotes hex-string))
        bytes-len (count bytes)
        start-index (get-initial-secret-key-index encrypted-time)]
    (->> (mapv (fn [i]
                 (bit-xor (nth bytes i)
                          (nth key (mod (+ start-index i) (count key)))))
               (range bytes-len))
         bytes-to-string)))

;; --- HTTP Handlers ---

(defn handle-check-client-version
  "Handles the HTTP request for /login/CheckClientVersion."
  [request]
  (let [response-data "{\"serverInfo\": {\"version\": \"product\"}, \"state\": \"ok\", \"result\": {\"timeoffset\": 0}}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(defn handle-get-terms-of-use-state-all
  "Handles the HTTP request for /login/GetTermsOfUseStateAll."
  [request]
  (let [response-data "{\"serverInfo\": {\"version\": \"product\"}, \"state\": \"ok\", \"result\": {\"version\": 1, \"termsOfUseStateList\": [{\"uid\": 1, \"version\": 1, \"state\": 1}]}}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(defn handle-sign-in-as-steam
  "Handles the HTTP request for /login/SignInAsSteam."
  [request]
  (let [response-data "{\"serverInfo\": {\"version\": \"product\"}, \"state\": \"ok\", \"result\": {\"userAuth\": {\"uid\": 1, \"public_id\": 1, \"db_id\": 0, \"auth_code\": \"1\", \"last_login_date\": \"2024-11-13T12:46:40.923Z\", \"last_update_date\": \"2024-11-13T12:46:40.923Z\", \"data_version\": 16}, \"accountInfo\": {\"uid\": 1}, \"walletCurrency\": \"USD\"}}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(defn handle-check-season-log
  "Handles the HTTP request for /api/CheckSeasonLog."
  [request]
  (let [response-data "{\"serverInfo\": {\"version\": \"product\"}, \"state\": \"ok\", \"result\": {\"seasonLog\": null}}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(defn handle-fetch-latest-synchronous-data
  "Handles the HTTP request for /api/FetchLatestSynchronousData."
  [request]
  (let [response-data "{\"serverInfo\": {\"version\": \"product\"}, \"state\": \"ok\", \"synchronized\": null}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(defn handle-load-user-data-all
  "Handles the HTTP request for /api/LoadUserDataAll."
  [request]
  (let [response-data "{\"serverInfo\":{\"version\":\"product\"},\"state\":\"ok\",\"updated\":{\"isInitialized\":true,\"userInfo\":{\"uid\":1,\"level\":1,\"exp\":0,\"stamina\":1,\"current_storybattle_nodeid\":-1}},\"result\":{\"profile\":{\"public_uid\":\"A000000001\",\"illust_id\":10101,\"illust_gacksung_level\":1,\"sentence_id\":1,\"word_id\":1,\"banners\":[],\"support_personalities\":[],\"level\":1,\"leftborder_id\":-1,\"rightborder_id\":-1,\"egobackground_id\":-1}}}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(defn handle-update-steam-pending-purchase
  "Handles the HTTP request for /iap/UpdateSteamPendingPurchase."
  [request]
  (let [response-data "{\"serverInfo\": {\"version\": \"product\"}, \"state\": \"ok\", \"result\": {\"finalizedTransactionIds\": []}}"
        encrypted-time (current-epoch-seconds)
        encrypted-body (encrypt (.getBytes response-data "UTF-8") encrypted-time)]
    {:status 200
     :headers {"Content-Type" "application/json"
               "Content-Encrypted" (str encrypted-time)}
     :body encrypted-body}))

(def routes
  (route/expand-routes
    #{["/login/CheckClientVersion" :post handle-check-client-version :route-name :check-client-version]
      ["/login/GetTermsOfUseStateAll" :post handle-get-terms-of-use-state-all :route-name :get-terms-of-use-state-all]
      ["/login/SignInAsSteam" :post handle-sign-in-as-steam :route-name :sign-in-as-steam]
      ["/api/CheckSeasonLog" :post handle-check-season-log :route-name :check-season-log]
      ["/api/FetchLatestSynchronousData" :post handle-fetch-latest-synchronous-data :route-name :fetch-latest-synchronous-data]
      ["/api/LoadUserDataAll" :post handle-load-user-data-all :route-name :load-user-data-all]
      ["/iap/UpdateSteamPendingPurchase" :post handle-update-steam-pending-purchase :route-name :update-steam-pending-purchase]}))

;; --- Server Setup ---

(def service
  {:env :prod
   ::http/routes routes
   ::http/type :jetty
   ::http/port 9772})

(defn start []
  "Function to start the Pedestal server."
  (let [server (http/create-server service)]
    (http/start server)
    server))

(defn -main [& args]
  "Main entry point for starting the server."
  (start))
