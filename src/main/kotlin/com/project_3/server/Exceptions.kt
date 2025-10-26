package com.project_3.server


class EmailAlreadyExistsException(val exceptionMessage: String = "Email already in use") : RuntimeException(exceptionMessage)

class UserNotFoundException(val exceptionMessage: String = "User not found") : RuntimeException(exceptionMessage)

class InvalidPasswordException(val exceptionMessage: String = "Invalid password") : RuntimeException(exceptionMessage)
