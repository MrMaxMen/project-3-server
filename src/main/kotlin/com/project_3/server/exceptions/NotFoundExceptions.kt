package com.project_3.server.exceptions


abstract class NotFoundByIdException(
    val id: Long
) : RuntimeException() {
    override val message: String =
        "${this::class.simpleName?.removeSuffix("NotFoundByIdException")} with id: $id not found in DB"
}


class ProductNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class SellerNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class CategoryNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class ProductGroupNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class PickupPointNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class BuyerNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class DeliveryNotFoundByIdException(id: Long) : NotFoundByIdException(id)

class VehicleNotFoundByIdException(id: Long) : NotFoundByIdException(id)



class UserNotFoundByEmailException(val email: String) : RuntimeException("User with email: $email not found")


