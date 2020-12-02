# POC-Reitit-JWT

Proof of concept of authorizing access to a webservice build with the Clojure libraries Reitit and Ring-JWT

## Overview

[Reitit](https://github.com/metosin/reitit) does not ship with a JWT authorization option, although there is a ticket open to [integrate buddy-auth into reitit](https://github.com/metosin/reitit/issues/154). Instead of going down the buddy route, I opted to use [ring-jwt](https://github.com/ovotech/ring-jwt) instead, because it is more specifically concerned with JWT and is built atop a proven JWT library from auth0.

This repository serves the purpose to demonstrate the (actually very easy) integration. The only interesting pieces here are the two middleware / wrappers around ring handlers in `routes.clj` and the generation of a signed JWT token.

Note that this small example repository only uses HMAC256 for the JWT, where in a real application you probably want the private/public key method, cf. the ring-jwt documentation.

## Run it

This repository uses deps.edn, so clone the repository and execute `clojure -M -m reititjwtpoc.core`.

	> clojure -M -m reititjwtpoc.core
	2020-12-02 10:30:26.151:INFO::main: Logging initialized @1335ms to org.eclipse.jetty.util.log.StdErrLog
	2020-12-02 10:30:27.481:INFO:oejs.Server:main: jetty-9.4.12.v20180830; built: 2018-08-30T13:59:14.071Z; git: 27208684755d94a92186989f695db2d7b21ebc51; jvm 11.0.9+11-post-Debian-1deb10u1
	2020-12-02 10:30:27.514:INFO:oejs.AbstractConnector:main: Started ServerConnector@6eaa6b0c{HTTP/1.1,[http/1.1]}{0.0.0.0:3001}
	2020-12-02 10:30:27.514:INFO:oejs.Server:main: Started @2698ms
	Hello, World!
	Please kill the process to stop the server


This will create a server on port 3001. You can point browser at [localhost:3001/ping](http://localhost:3001/ping) to access the non-secured URL, but to check the secured URL it's easier to use cURL like so:

    > curl -H 'Accept: application/json' -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbWUubG9jYWxob3N0L2lzc3VlciIsInVzZXIiOiJUZXN0ZXIifQ.naCn5dqpXLfIi2HCYhijbNAflt3Rkt4D6Jjo4IO_1RM" http://localhost:3001/sping
    pong

When you use an invalid token, you'll receive an error:

	> curl -H 'Accept: application/json' -H "Authorization: Bearer invalidtoken" http://localhost:3001/sping
	One or more claims were invalid.

If you don't provide a token, you'll receive an Unauthorized error as expected:

	> curl -H 'Accept: application/json' http://localhost:3001/sping
	Unauthorized


## Test it

The relevant tests can be executed via kaocha:

	> clojure -M:test/kaocha
	[(.)(....)]
	2 tests, 5 assertions, 0 failures.

## License

Copyright © 2020 Holger Schauer <holger.schauer@gmx.de>

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
