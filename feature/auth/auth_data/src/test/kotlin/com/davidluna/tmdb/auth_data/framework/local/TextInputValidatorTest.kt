package com.davidluna.tmdb.auth_data.framework.local

import com.davidluna.tmdb.auth_domain.entities.TextInputError
import com.davidluna.tmdb.auth_domain.entities.TextInputType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TextInputValidatorTest(
    private val input: String?,
    private val type: TextInputType,
    private val expected: TextInputError?
) {

    private lateinit var sut: TextInputValidator

    @Before
    fun setUp() {
        sut = TextInputValidator()
    }

    @Test
    fun `parameterized validation`() {
        val result = sut.invoke(input, type)
        Assert.assertEquals(expected, result)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "GIVEN input={0} AND type={1} WHEN invoked THEN should deliver {2}")
        fun data(): Collection<Array<Any?>> = listOf(
            arrayOf(null, TextInputType.USERNAME, TextInputError.Required),
            arrayOf(" ", TextInputType.USERNAME, TextInputError.Required),
            arrayOf("invalidemail", TextInputType.USERNAME, TextInputError.InvalidEmail),
            arrayOf("test@domain", TextInputType.USERNAME, TextInputError.InvalidEmail),
            arrayOf("test@example.com", TextInputType.USERNAME, null),
            arrayOf("test@sub.example.com", TextInputType.USERNAME, null),
            arrayOf("test+alias@example.com", TextInputType.USERNAME, null),
            arrayOf(null, TextInputType.PASSWORD, TextInputError.Required),
            arrayOf(" ", TextInputType.PASSWORD, TextInputError.Required),
            arrayOf("short", TextInputType.PASSWORD, TextInputError.InvalidLength(8)),
            arrayOf("12345678", TextInputType.PASSWORD, null),
            arrayOf("longpassword123", TextInputType.PASSWORD, null)
        )
    }
}