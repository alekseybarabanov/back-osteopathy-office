package org.osteopathy.back.repositories

import jakarta.transaction.Transactional
import org.osteopathy.back.entities.Audit
import org.osteopathy.back.entities.Patient
import org.osteopathy.back.entities.Visit
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface PatientRepository : CrudRepository<Patient, Long> {
    @Query("SELECT t FROM Patient t where t.tenant = ?2 AND (t.firstName like '%' || ?1 || '%' OR t.lastName like '%' || ?1 || '%' OR t.middleName like '%' || ?1 || '%')")
    fun findByFirstNameOrMiddleNameOrLastName(name: String, tenant: String?): List<Patient>
}

@Repository
interface VisitRepository: CrudRepository<Visit, Long>

@Repository
interface AuditRepository: org.springframework.data.repository.Repository<Audit, Long> {

    fun save(audit: Audit)

    @Transactional
    @Modifying
    @Query("DELETE FROM Audit WHERE tm < :cutTm")
    fun deleteOld(@Param("cutTm") cutTm: OffsetDateTime)
}