package com.sveletor.application.components

import com.sveletor.application.classes.SveletorSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import io.netty.handler.codec.http.HttpMethod


/**
 * A server function that only allows requests from authenticated sessions.
 * Call from a [Route] block with a predefined endpoint.
 *
 * @param method The type of [HttpMethod] to execute
 * @param endpoint The endpoint that this action sits on in addition to that
 * of its parent [Route] block.
 * @param action Suspending lambda to execute at this endpoint
 * @param badSessionAction Suspending lambda to execute if the session is invalid
 */
fun Route.authenticatedEndpoint(
    method: HttpMethod,
    endpoint: String,
    action: suspend PipelineContext<Unit, ApplicationCall>.(session: SveletorSession) -> Unit,
    badSessionAction: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit = { call.respondText("session invalid", status = HttpStatusCode.Unauthorized) }
) {
    when (method) {
        HttpMethod.GET -> get(endpoint) { validateAndInvoke(action, badSessionAction) }

        HttpMethod.POST -> post(endpoint) { validateAndInvoke(action, badSessionAction) }

        HttpMethod.PUT -> put(endpoint) { validateAndInvoke(action, badSessionAction) }

        HttpMethod.DELETE -> delete(endpoint) { validateAndInvoke(action, badSessionAction) }

        HttpMethod.PATCH -> patch(endpoint) { validateAndInvoke(action, badSessionAction) }
    }
}


/**
 * Private helper class for endpoints. Validates session then
 * either calls [action] or [badSessionAction]
 */
private suspend fun PipelineContext<Unit, ApplicationCall>.validateAndInvoke(
    action: suspend PipelineContext<Unit, ApplicationCall>.(session: SveletorSession) -> Unit,
    badSessionAction: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit
) {
    val session = call.sessions.get<SveletorSession>()
    if (session == null || !session.validate()) {
        return badSessionAction()
    }
    action(session)
}