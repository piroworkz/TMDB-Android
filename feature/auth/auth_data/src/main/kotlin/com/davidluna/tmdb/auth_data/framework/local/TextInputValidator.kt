package com.davidluna.tmdb.auth_data.framework.local

import com.davidluna.tmdb.auth_domain.entities.TextInputError
import com.davidluna.tmdb.auth_domain.entities.TextInputType
import com.davidluna.tmdb.auth_domain.usecases.GetTextInputError
import javax.inject.Inject

class TextInputValidator @Inject constructor() : GetTextInputError {

    override fun invoke(text: String?, type: TextInputType): TextInputError? = when (type) {
        TextInputType.USERNAME -> validateUsername(text)
        TextInputType.PASSWORD -> validatePassword(text)
    }

    private fun validateUsername(text: String?): TextInputError? = when {
        text.isNullOrBlank() -> TextInputError.Required
        !EMAIL_ADDRESS_PATTERN.toRegex().matches(text) -> TextInputError.InvalidEmail
        else -> null
    }

    private fun validatePassword(text: String?): TextInputError? = when {
        text.isNullOrBlank() -> TextInputError.Required
        text.length < 8 -> TextInputError.InvalidLength(8)
        else -> null
    }

    companion object {
        private const val EMAIL_ADDRESS_PATTERN =
            "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    }
}