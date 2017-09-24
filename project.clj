(defproject kosmos/kosmos-dynamodb-local "0.0.1-SNAPSHOT"

  :description "kosmos local dynamodb database component (for testing)"

  :url "https://bitbucket.org/pupcus/kosmos-dynamodb-local"

  :scm {:url "git@bitbucket.org:bitbucket/kosmos-dynamodb-local"}

  :author "Michael Pendergrass"

  :dependencies [
                 [com.almworks.sqlite4java/sqlite4java "1.0.392"]
                 [com.amazonaws/DynamoDBLocal "1.11.0.1"]
                 [kosmos "0.0.6"]
                 [kosmos/kosmos-dynamodb-local-native "1.0.0"]
                 [org.clojure/java.classpath "0.2.3"]
                 ]

  :repositories [["amazon" {:url "https://s3-us-west-2.amazonaws.com/dynamodb-local/release"}]]

  :profiles {:dev {:resource-paths ["dev-resources"]
                   :dependencies [[org.clojure/clojure "1.8.0"]
                                  [org.clojure/tools.logging "0.3.1"]
                                  [log4j "1.2.15"
                                   :exclusions [javax.mail/mail
                                                javax.jms/jms
                                                com.sun.jdmk/jmxtools
                                                com.sun.jmx/jmxri]]
                                  [org.slf4j/slf4j-log4j12 "1.6.1"]]}}

  :deploy-repositories [["snapshots"
                         {:url "https://clojars.org/repo"
                          :creds :gpg}]
                        ["releases"
                         {:url "https://clojars.org/repo"
                          :creds :gpg}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])
