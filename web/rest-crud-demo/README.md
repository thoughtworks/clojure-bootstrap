# rest-crud-demo

FIXME: description

## Installation

### Dependencies

- Install (Clojure)[https://clojure.org/guides/getting_started]
- Install (Leiningen)[https://leiningen.org/] as a package manager.

### Setup database

We simply create a database using this script.

```
> createdb restful-crud

> psql -d restful-crud

restful-crud:> CREATE TABLE "users" (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
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
lein run
```

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
