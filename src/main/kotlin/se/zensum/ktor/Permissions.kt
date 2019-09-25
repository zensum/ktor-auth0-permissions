package se.zensum.ktor

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor

suspend inline fun PipelineContext<Unit, ApplicationCall>.permission(
    permission: String,
    crossinline block: PipelineInterceptor<Unit, ApplicationCall>
) {
    val token: JwtPrincipal? = token()
    when {
        token == null -> call.respond(HttpStatusCode.Unauthorized, "Missing JSON web token for authorization")
        token.hasPermission(permission) -> block(Unit)
        else -> call.respond(HttpStatusCode.Forbidden, "Missing required permission: $permission")
    }
}

fun PipelineContext<*, ApplicationCall>.token(): JwtPrincipal? =
    context.authentication.principal()