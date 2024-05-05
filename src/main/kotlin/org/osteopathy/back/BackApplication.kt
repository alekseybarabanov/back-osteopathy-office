package org.osteopathy.back

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages=["org.osteopathy.back"])
@EnableJpaRepositories(basePackages=["org.osteopathy.back.repositories"])
@EnableTransactionManagement
@EntityScan(basePackages=["org.osteopathy.back.entities"])
class BackApplication

fun main(args: Array<String>) {
	runApplication<BackApplication>(*args)
}
