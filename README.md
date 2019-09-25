# ktor-auth0-permissions

Add support for reading permissions from [JSON web tokens](https://jwt.io/)
issued by [Auth0](https://auth0.com) in [Ktor](https://ktor.io/)

## Description
ktor-auth0-permissions is a very small and slightly opinionated library for dealing with
authorization and permissions management. The core features are
- provide an easy way of setting up auhtorization config with JSON web tokens from Auth0
- allow or reject users access to certain endpoints based on if they have certain permissions or not

Permissions are also referred to as "[scopes](https://auth0.com/docs/scopes/current)"
when they are attached to an [M2M](https://auth0.com/docs/applications/concepts/app-types-auth0)
 (machine-to-machine) token.

## Example Use
The following example install JWT authentication with the "Authentication" future
provided by Ktor. Additionally, endpoint `/users` requires the permission `read-users`. 
```kotlin
private const val AUTHENTICATION_NAMESPACE = "my-application"

fun main() {
    embeddedServer(Netty, 80) {
        // Install JWT authentication, with Auth0
        install(Authentication) {
            auth0(AUTHENTICATION_NAMESPACE)
        }
        routing {
            authenticate(AUTHENTICATION_NAMESPACE) {
                get("/users") {
                    // Require that permission "read-users" is present
                    permission("read-users") {
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }.start(wait = true)
}
```
It the user has the permission `read-users` present in their token, they will be
granted access to this endpoint and everything in the `permission` block will be
executed. If a token was presented but the permission was missing, status code _403_
will be returned.


## Configuration
The following properties **must** be configured, either as an environment variable
or set in a configuration file for the application.

|Configuration Key|Environment Variable|Description|
|-----------------|--------------------|-----------|
|jwk.issuer|JWK_ISSUER|URL for issuer of the JWT, this will be an url such as `https://my-tenant.eu.auth0.com`|
|jwk.realm|JWK_REALM|Realm in which the JWT will be used, which is set by your application|
|jwk.audience|JWK_AUDIENCE|The audience for which the JWT is valid. The `aud` field in the JWT must contain this value.|
