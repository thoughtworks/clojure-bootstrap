# rest-crud-demo

WIP - Template for Clojure REST API applications based on [this blog post](https://www.codementor.io/@tamizhvendan/developing-restful-apis-in-clojure-using-compojure-api-and-toucan-part-1-oc6yzsigc).

## Installation

### Dependencies

- Install [Leiningen](https://leiningen.org/).
- Install [Docker](https://www.docker.com/).

### Setup database

Run PostgreSQL via Docker container.

```
docker run -p 5142:5142 --name clojure-bootstrap-db -e POSTGRES_PASSWORD=a-strong-password -d postgres
```

We simply create a database using this script.

```
> createdb restful-crud -h localhost -U postgres 

> psql -d restful-crud -h localhost -U postgres

restful-crud:> CREATE TABLE "users" (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                role VARCHAR(10) NOT NULL,
                password_hash TEXT NOT NULL
              );
CREATE TABLE
restful-crud:>
```

It is possible to use a database migration tools such as Flyway. Please update this demo if you feel like it is a better way.

## Usage

Run the REPL server

```
lein repl
```

Running in local

```
lein ring server-headless
```

## Manual Tests

1. Point your browser to the [Swagger UI](http://localhost:8080/swagger/index.html).
2. Create a new user via POST on /users.
3. Login on /api/v1/login. Copy the token in the response.
4. Click on the Swagger UI Authorize button. Paste the token and click on Authorize.
5. Retrieve users via GET on /users.

### Bugs

...

## License

Copyright © 2021 ThoughtWorks Inc

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
