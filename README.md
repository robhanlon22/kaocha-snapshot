# krispie

[![ci](https://github.com/robhanlon22/krispie/workflows/ci/badge.svg)](https://github.com/robhanlon22/krispie/actions?query=workflow%3Aci) [![codecov](https://codecov.io/gh/robhanlon22/krispie/branch/main/graph/badge.svg?token=l4F8aSFIyH)](https://codecov.io/gh/robhanlon22/krispie)

Snapshot testing for Kaocha.

## Usage

FIXME: write usage documentation!

Invoke a library API function from the command-line:

    $ clojure -X robhanlon22.krispie/foo :a 1 :b '"two"'
    {:a 1, :b "two"} "Hello, World!"

Run the project's tests (they'll fail until you edit them):

    $ clojure -M:test:runner

Build a deployable jar of this library:

    $ clojure -M:jar

Install it locally:

    $ clojure -M:install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables:

    $ clojure -M:deploy

## License

Copyright © 2020 Rob Hanlon

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
