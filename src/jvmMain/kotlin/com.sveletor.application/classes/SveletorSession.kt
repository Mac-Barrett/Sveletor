package com.sveletor.application.classes

import kotlinx.serialization.Serializable

@Serializable
class SveletorSession(val username: String) {

    companion object {
        fun validate(session: SveletorSession): Boolean {
            return session.username.isNotBlank()
        }
    }
}