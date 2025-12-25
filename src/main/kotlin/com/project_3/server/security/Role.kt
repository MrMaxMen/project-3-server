package com.project_3.server.security


enum class Role {
    BUYER,   // Покупатель
    SELLER;  // Продавец
    

    fun addPrefixROLE(): String = "ROLE_$name" // нужно для Spring Security
    
    companion object {

        fun removePrefixROLE(role: String): Role? {
            return try {
                valueOf(role.removePrefix("ROLE_"))
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}