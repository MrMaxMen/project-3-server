package com.project_3.server.exceptions





class ExtractIdErrorException(
    val exceptionMessage: String = " error of extracting Id"
) : RuntimeException(exceptionMessage)

class ProductCreationErrorException(
    val exceptionMessage: String = "product must contain at least one item"
) : RuntimeException(exceptionMessage)
