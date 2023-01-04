package com.sveletor.application.classes

import kotlinx.serialization.Serializable

@Serializable
class SveletorSession(val username: String) {
    // TODO Customize your session :)
    companion object {
        fun validate(session: SveletorSession): Boolean {
            // TODO Perform Session validation
            return session.username.isNotBlank()
        }
    }
}