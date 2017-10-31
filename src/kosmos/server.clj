(ns kosmos.server
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [kosmos.server.util :as util]))

(defrecord LocalDynamoDbServerComponent [cors port in-memory? shared-db? db-path optimize-db-before-startup? delay-trainsient-statuses?]
  component/Lifecycle
  (start [{:keys [in-memory? db-path] :as component}]
    (log/info "starting local amazon dynamodb server instance for testing ...")
    (assert (not (and in-memory? db-path)) "cannot specify in memory database AND a database directory at the same time")
    (let [command-line-args (util/build-command-line-args component)
          native-directory (util/setup-native-libraries)]
      (when db-path (util/ensure-directory-exists db-path))
      (let [server (com.amazonaws.services.dynamodbv2.local.main.ServerRunner/createServerFromCommandLineArgs
                    (into-array command-line-args))]
        (.start server)
        (log/info "local amazon dynamodb server instance started")
        (assoc component :server server :native-directory native-directory))))

  (stop [{:keys [server native-directory] :as component}]
    (log/info "stopping local amazon dynamodb server instance for testing ...")
    (.stop server)
    (util/delete native-directory)
    (dissoc component :server)
    (log/info "local amazon dynamodb server instance stopped")))
