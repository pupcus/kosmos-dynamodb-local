# kosmos-dynamodb-local

A `kosmos` component for starting an Amazon AWS DynamoDb instance locally for testing and exploration.

## Usage

See the options for the local DynamoDB server instance [here](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

In memory database example configuration map:

```clojure
{
  :dynamodb
  {
    :kosmos/init :kosmos.server/LocalDynamoDbServerComponent
    :port 3000
    :in-memory? true
    :shared-db? true
  }
}
```

On disk database example configuration map:

```clojure
{
  :dynamodb
  {
    :kosmos/init :kosmos.server/LocalDynamoDbServerComponent
    :port 3000
    :db-path "target/db" ;; directory where database files will be stored
    :delay-transient-statuses? true
    :optimize-db-before-startup? true
    :cors? "*.somedomain.com"
    :shared-db? true
  }
}
```

See the examples for amazonica for DynamoDbv2 for an example of setting creds for the local database [here](https://github.com/mcohen01/amazonica#dynamodbv2)

## License

Kosmos is distributed under the [Eclipse Public License](http://opensource.org/licenses/eclipse-1.0.php), the same as Clojure.
