(defproject kosmos/kosmos-dynamodb-local "0.0.2"

  :description "kosmos local dynamodb database component (for testing)"

  :url "https://bitbucket.org/pupcus/kosmos-dynamodb-local"

  :scm {:url "git@bitbucket.org:bitbucket/kosmos-dynamodb-local"}

  :author "Michael Pendergrass"

  :dependencies [
                 [com.almworks.sqlite4java/sqlite4java "1.0.392"]
                 [com.amazonaws/DynamoDBLocal "1.11.0.1"]
                 [kosmos "0.0.7"]
                 [kosmos/kosmos-dynamodb-local-native "1.0.0"]
                 [org.clojure/java.classpath "0.2.3"]
                 ]

  :repositories [["amazon" {:url "https://s3-us-west-2.amazonaws.com/dynamodb-local/release"}]]

  :profiles {:dev {:resource-paths ["dev-resources"]
                   :dependencies [[org.clojure/clojure "1.8.0"]
                                  [org.clojure/tools.logging "0.4.0"]
                                  [org.slf4j/slf4j-log4j12 "1.7.25"]]}}

  :deploy-repositories [["snapshots"
                         {:url "https://clojars.org/repo"
                          :sign-releases false
                          :creds :gpg}]
                        ["releases"
                         {:url "https://clojars.org/repo"
                          :sign-releases false
                          :creds :gpg}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])
