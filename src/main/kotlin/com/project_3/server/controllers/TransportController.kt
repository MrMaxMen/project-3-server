package com.project_3.server.controllers

import com.project_3.server.dto.SupplyCreationDTO
import com.project_3.server.service.TransportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/transport")
class TransportController (
    private val transportService: TransportService
){
    @PostMapping("/complete-delivery/{deliveryId}")
    fun completeDelivery(deliveryId: Long): ResponseEntity<String> {

        transportService.completeDelivery(deliveryId)

        return ResponseEntity.ok("Delivery with ID $deliveryId has been completed.")
    }



    @PostMapping("/create-supply-transportation")
    fun createSupplyTransportation(
        @RequestBody supplyCreationDTO: SupplyCreationDTO //ограничение партии по весу и объёму на клиенте
    ): ResponseEntity<String> {

        transportService.createSupply(supplyCreationDTO)

        return ResponseEntity.ok("Supply transportation creation process initiated.")
    }


    //controllers for complete transportation and supply

    @PostMapping("/complete-supply/{supplyId}")
    fun completeSupply(@RequestBody supplyId: Long): ResponseEntity<String> {

        transportService.completeSupply(supplyId)

        return ResponseEntity.ok("Supply with ID $supplyId has been completed.")
    }


    @PostMapping("/complete-transfer/{transferId}")
    fun completeTransfer(@RequestBody transferId: Long): ResponseEntity<String> {

        transportService.completeTransfer(transferId)

        return ResponseEntity.ok("Transfer with ID $transferId has been completed.")
    }



}