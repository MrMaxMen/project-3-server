package com.project_3.server.exceptions



class ExtractIdErrorException(
    val exceptionMessage: String = " error of extracting Id"
) : RuntimeException(exceptionMessage)

class ProductGroupCreationErrorException(
    val exceptionMessage: String = "productGroup must contain at least one product"
) : RuntimeException(exceptionMessage)

class DeliveryImpossibleException(
    msg : String
) : RuntimeException(msg)