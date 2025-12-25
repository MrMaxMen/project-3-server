package com.project_3.server.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Фильтр для автоматической проверки JWT токена в каждом запросе
 * 
 * Что делает этот фильтр:
 * 1. Перехватывает каждый HTTP запрос
 * 2. Извлекает JWT токен из заголовка Authorization
 * 3. Проверяет валидность токена
 * 4. Достаёт из токена ID пользователя и его роль
 * 5. Помещает эту информацию в SecurityContext (контекст безопасности)
 * 
 * После этого ты можешь достать ID и роль в любом месте (контроллер, сервис):
 * - val userId = authentication.principal.toString().toLong()
 * - val role = authentication.authorities.first().authority // "ROLE_BUYER" или "ROLE_SELLER"
 */

@Component
class JwtFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {

            val authHeader = request.getHeader("Authorization")
            

            val token = jwtService.extractToken(authHeader)

            // Если токен есть И пользователь ещё не аутентифицирован
            if (token != null && SecurityContextHolder.getContext().authentication == null) {
                
                // Проверяем валидность токена
                if (jwtService.validateToken(token)) {
                    

                    val userId = jwtService.extractId(token)
                    

                    val role = jwtService.extractRole(token)
                    

                    if (userId != null && role != null) {
                        

                        val authority = SimpleGrantedAuthority(role.addPrefixROLE())
                        
                        // 9. Создаём объект аутентификации
                        val authentication = UsernamePasswordAuthenticationToken(
                            userId.toString(),      // principal - это ID пользователя
                            null,                   // credentials - не нужны (токен уже проверен)
                            listOf(authority)       // authorities - роль пользователя
                        )
                        
                        // 10. Добавляем детали запроса (IP адрес и т.д.)
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        

                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            }
        } catch (ex: Exception) {

            logger.error("Ошибка JWT аутентификации: ${ex.message}")
        }

        // 12. Продолжаем цепочку фильтров (передаём запрос дальше)
        filterChain.doFilter(request, response)
    }
}