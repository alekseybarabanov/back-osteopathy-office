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
            val hasColumn = conn.metaData
                .getColumns(null, null, "VISIT", "REGION_NECK_STRUCT_DETAILS")
                .use { it.next() }
                || conn.metaData
                .getColumns(null, null, "visit", "region_neck_struct_details")
                .use { it.next() }

            if (!hasColumn) {
                log.info("Migration: adding region_neck_struct_details column to visit table")
                conn.createStatement().use { stmt ->
                    stmt.execute("ALTER TABLE visit ADD COLUMN region_neck_struct_details VARCHAR(255)")
                }
                log.info("Migration: column added successfully")
            } else {
                log.info("Migration: region_neck_struct_details column already exists, skipping")
            }
        }
    }
}
