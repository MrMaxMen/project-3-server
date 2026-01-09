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

class SupplyBatchWeightExceededException : RuntimeException("Total weight of the supply batch exceeded the vehicle's carrying capacity")

class SupplyBatchVolumeExceededException : RuntimeException("Total volume of the supply batch exceeded the vehicle's carrying volume")