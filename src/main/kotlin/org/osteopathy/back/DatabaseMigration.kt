package org.osteopathy.back

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseMigrationConfig {

    @Bean
    fun databaseMigration(dataSource: DataSource): DatabaseMigration {
        return DatabaseMigration(dataSource)
    }

    @Bean
    fun migrationDependencyPostProcessor(): BeanFactoryPostProcessor {
        return BeanFactoryPostProcessor { factory ->
            if (factory.containsBeanDefinition("entityManagerFactory")) {
                val emfDef = factory.getBeanDefinition("entityManagerFactory")
                val existing = emfDef.dependsOn ?: emptyArray()
                emfDef.setDependsOn(*existing, "databaseMigration")
            }
        }
    }
}

class DatabaseMigration(private val dataSource: DataSource) : InitializingBean {

    private val log = LoggerFactory.getLogger(DatabaseMigration::class.java)

    override fun afterPropertiesSet() {
        dataSource.connection.use { conn ->
            val columns = listOf("region_neck_struct_details", "region_brest_struct_details", "region_lower_back_struct_details")
            for (column in columns) {
                val hasColumn = conn.metaData
                    .getColumns(null, null, "VISIT", column.uppercase())
                    .use { it.next() }
                    || conn.metaData
                    .getColumns(null, null, "visit", column)
                    .use { it.next() }

                if (!hasColumn) {
                    log.info("Migration: adding $column column to visit table")
                    conn.createStatement().use { stmt ->
                        stmt.execute("ALTER TABLE visit ADD COLUMN $column VARCHAR(255)")
                    }
                    log.info("Migration: $column column added successfully")
                } else {
                    log.info("Migration: $column column already exists, skipping")
                }
            }
        }
    }
}
