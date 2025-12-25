package com.project_3.server.exceptions

class EmailAlreadyExistsException(
    val email : String,
    val exceptionMessage: String = "Email : $email already in use"
) : RuntimeException(exceptionMessage)


class InvalidPasswordException(
    val exceptionMessage: String = "Invalid password"
) : RuntimeException(exceptionMessage)

class InvalidTokenException(
    val exceptionMessage: String = "Invalid token"
) : RuntimeException(exceptionMessage)


class NoTokenException(
    val exceptionMessage: String = "No token"
) : RuntimeException(exceptionMessage)
