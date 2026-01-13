package com.project_3.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Главная конфигурация Spring Security
 * 
 * Что тут настраивается:
 * 1. Какие эндпоинты доступны без токена (публичные)
 * 2. Какие эндпоинты требуют токен (защищённые)
 * 3. Подключение нашего JWT фильтра
 * 4. Настройка stateless сессий (сервер не хранит сессии)
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Включает @PreAuthorize для проверки ролей в контроллерах
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Отключаем CSRF защиту (не нужна для REST API с JWT)
            .csrf { it.disable() }
            
            // Настраиваем правила доступа к эндпоинтам
            .authorizeHttpRequests { auth ->
                auth
                    // ПУБЛИЧНЫЕ эндпоинты (доступны БЕЗ токена)
                    .requestMatchers(
                        "/api/auth/register",  // Регистрация
                        "/api/auth/login",     // Логин
                        "/api/public/**",
                        "/swagger-ui/**",      // Swagger UI (html, js, css)
                        "/v3/api-docs/**"      // OpenAPI описание
                    ).permitAll()
                    
                    // ВСЕ ОСТАЛЬНЫЕ эндпоинты требуют токен
                    // Spring Security автоматически проверит токен и вернёт 401 если его нет
                    .anyRequest().authenticated()
            }
            
            // Настраиваем STATELESS сессии (не храним сессии на сервере)
            // Вся информация о пользователе в JWT токене
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            
            // ВАЖНО: Добавляем наш JWT фильтр в цепочку фильтров Spring Security
            // Он будет выполняться ДО стандартного UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}