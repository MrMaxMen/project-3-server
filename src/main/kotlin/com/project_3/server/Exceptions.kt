package com.project_3.server


class EmailAlreadyExistsException(
    val exceptionMessage: String = "Email already in use"
) : RuntimeException(exceptionMessage)

class UserNotFoundException(
    val exceptionMessage: String = "User not found"
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

class ExtractIdErrorException(
    val exceptionMessage: String = " error of extracting Id"
) : RuntimeException(exceptionMessage)

class ProductCreationErrorException(
    val exceptionMessage: String = "product must contain at least one item"
) : RuntimeException(exceptionMessage)

class SellerNotFoundException(
    val exceptionMessage: String = "seller not found in DB"
) : RuntimeException(exceptionMessage)

class CategoryNotFoundException(
    val exceptionMessage: String = "category not found id DB"
) : RuntimeException(exceptionMessage)


class ProductNotFoundException(
    val exceptionMessage: String = "product not found in DB"
) : RuntimeException(exceptionMessage)

class ProductNotExistException(
    val exceptionMessage: String = "product not found in DB"
) : RuntimeException(exceptionMessage)


class ItemNotExistException(
    val id : Long ,
    val exceptionMessage : String = "item with id : $id not found"
) : RuntimeException(exceptionMessage)