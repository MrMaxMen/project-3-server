package com.project_3.server.service

import com.project_3.server.models.logistics.Orthodrome
import com.project_3.server.repos.OrthodromeRepository
import com.project_3.server.repos.PickupPointRepository
import com.project_3.server.repos.StockRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Service
class OrthodromeService(
    private val stockRepository: StockRepository,
    private val pickupPointRepository: PickupPointRepository,
    private val orthodromeRepository: OrthodromeRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)





    @Transactional
    fun calculateAllOrthodromes() {
        logger.info("Начинаем вычисление всех ортодромий...")

        val stocks = stockRepository.findAll()
        val pickupPoints = pickupPointRepository.findAll()

        if (stocks.isEmpty() || pickupPoints.isEmpty()) {
            logger.warn("Нет складов или пунктов выдачи для вычисления ортодромий")
            return
        }

        var calculated = 0
        var skipped = 0
        val total = stocks.size * pickupPoints.size

        stocks.forEach { stock ->
            pickupPoints.forEach { pickupPoint ->

                // Проверяем, не существует ли уже
                if (!orthodromeRepository.existsByStockIdAndPickupPointId(
                        stock.id!!,
                        pickupPoint.id!!
                    )) {

                    val distance = calculateHaversineDistance(
                        stock.latitude,
                        stock.longitude,
                        pickupPoint.latitude,
                        pickupPoint.longitude
                    )

                    val orthodrome = Orthodrome(
                        stock = stock,
                        pickupPoint = pickupPoint,
                        distanceKm = distance
                    )

                    orthodromeRepository.save(orthodrome)
                    calculated++
                } else {
                    skipped++
                }
            }
        }

        logger.info("Вычисление завершено. Создано: $calculated, пропущено: $skipped, всего: $total")
    }


    @Transactional
    fun calculateOrthodromesForStock(stockId: Long) {

        val stock = stockRepository.findById(stockId)
            .orElseThrow { IllegalArgumentException("Склад с ID $stockId не найден") }

        val pickupPoints = pickupPointRepository.findAll()

        pickupPoints.forEach { pickupPoint ->

            if (!orthodromeRepository.existsByStockIdAndPickupPointId(stock.id!!, pickupPoint.id!!)) {

                val distance = calculateHaversineDistance(
                    stock.latitude,
                    stock.longitude,
                    pickupPoint.latitude,
                    pickupPoint.longitude
                )

                val orthodrome = Orthodrome(
                    stock = stock,
                    pickupPoint = pickupPoint,
                    distanceKm = distance
                )

                orthodromeRepository.save(orthodrome)
            }
        }

        logger.info("Вычислены ортодромии для склада '${stock.name}' до ${pickupPoints.size} пунктов выдачи")
    }


    @Transactional
    fun calculateOrthodromesForPickupPoint(pickupPointId: Long) {

        val pickupPoint = pickupPointRepository.findById(pickupPointId)
            .orElseThrow { IllegalArgumentException("Пункт выдачи с ID $pickupPointId не найден") }

        val stocks = stockRepository.findAll()

        stocks.forEach { stock ->

            // Проверяем, не существует ли уже
            if (!orthodromeRepository.existsByStockIdAndPickupPointId(stock.id!!, pickupPoint.id!!)) {

                val distance = calculateHaversineDistance(
                    stock.latitude,
                    stock.longitude,
                    pickupPoint.latitude,
                    pickupPoint.longitude
                )

                val orthodrome = Orthodrome(
                    stock = stock,
                    pickupPoint = pickupPoint,
                    distanceKm = distance
                )

                orthodromeRepository.save(orthodrome)
            }
        }

        logger.info("Вычислены ортодромии для ПВЗ '${pickupPoint.address}' до ${stocks.size} складов")
    }


    @Transactional
    fun recalculateOrthodromesForStock(stockId: Long) {

        logger.info("Пересчёт ортодромий для склада ID: $stockId")


        val oldOrthodromes = orthodromeRepository.findByStockId(stockId)
        orthodromeRepository.deleteAll(oldOrthodromes)


        calculateOrthodromesForStock(stockId)
    }


    @Transactional
    fun recalculateOrthodromesForPickupPoint(pickupPointId: Long) {

        logger.info("Пересчёт ортодромий для ПВЗ ID: $pickupPointId")


        val oldOrthodromes = orthodromeRepository.findByPickupPointId(pickupPointId)
        orthodromeRepository.deleteAll(oldOrthodromes)


        calculateOrthodromesForPickupPoint(pickupPointId)
    }


    private fun calculateHaversineDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371.0


        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        // Формула Haversine
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }


}