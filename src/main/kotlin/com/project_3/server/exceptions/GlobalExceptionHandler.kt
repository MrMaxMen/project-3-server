package com.project_3.server.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ApiError(val message: String)

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExists (ex : EmailAlreadyExistsException) =
        ResponseEntity(ApiError(ex.exceptionMessage), HttpStatus.CONFLICT)


    @ExceptionHandler(UserNotFoundByEmailException::class)
    fun handleUserNotFound(ex: UserNotFoundByEmailException) =
        ResponseEntity(ApiError(ex.message!!), HttpStatus.NOT_FOUND)


    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPassword(ex: InvalidPasswordException) =
        ResponseEntity(ApiError(ex.exceptionMessage), HttpStatus.UNAUTHORIZED)

}