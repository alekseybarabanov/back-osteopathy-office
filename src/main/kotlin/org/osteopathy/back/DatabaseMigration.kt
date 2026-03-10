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
            val varcharColumns = listOf("region_neck_struct_details", "region_brest_struct_details", "region_lower_back_struct_details")
            for (column in varcharColumns) {
                addColumnIfNotExists(conn, column, "VARCHAR(255)")
            }

            val splitColumns = listOf(
                SplitMigration("region_hands_struct", "region_hands_right_struct", "region_hands_left_struct"),
                SplitMigration("region_legs_struct", "region_legs_right_struct", "region_legs_left_struct"),
            )
            for (split in splitColumns) {
                val addedRight = addColumnIfNotExists(conn, split.rightColumn, "INTEGER")
                val addedLeft = addColumnIfNotExists(conn, split.leftColumn, "INTEGER")
                if (addedRight || addedLeft) {
                    val hasOld = hasColumn(conn, split.oldColumn)
                    if (hasOld) {
                        log.info("Migration: copying ${split.oldColumn} data to ${split.rightColumn} and ${split.leftColumn}")
                        conn.createStatement().use { stmt ->
                            stmt.execute("UPDATE visit SET ${split.rightColumn} = ${split.oldColumn}, ${split.leftColumn} = ${split.oldColumn}")
                        }
                    }
                }
            }
        }
    }

    private fun hasColumn(conn: java.sql.Connection, column: String): Boolean {
        return conn.metaData
            .getColumns(null, null, "VISIT", column.uppercase())
            .use { it.next() }
            || conn.metaData
            .getColumns(null, null, "visit", column)
            .use { it.next() }
    }

    private fun addColumnIfNotExists(conn: java.sql.Connection, column: String, type: String): Boolean {
        if (!hasColumn(conn, column)) {
            log.info("Migration: adding $column column to visit table")
            conn.createStatement().use { stmt ->
                stmt.execute("ALTER TABLE visit ADD COLUMN $column $type")
            }
            log.info("Migration: $column column added successfully")
            return true
        } else {
            log.info("Migration: $column column already exists, skipping")
            return false
        }
    }

    private data class SplitMigration(val oldColumn: String, val rightColumn: String, val leftColumn: String)
}
