package org.osteopathy.back

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CorsFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest?, res: ServletResponse, chain: FilterChain) {
        val response = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Content-Type,Access-Control-Allow-Methods,Access-Control-Allow-Origin")
        chain.doFilter(req, res)
    }

    override fun init(filterConfig: FilterConfig?) {}

    override fun destroy() {}
}