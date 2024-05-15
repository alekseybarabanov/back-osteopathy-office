package org.osteopathy.back.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.osteopathy.back.entities.Patient
import org.osteopathy.back.entities.Visit
import org.springframework.data.jpa.repository.Query

@Repository
interface PatientRepository : CrudRepository<Patient, Long> {
    @Query("SELECT t FROM Patient t where t.firstName like '%' || ?1 || '%' OR t.lastName like '%' || ?1 || '%' OR t.middleName like '%' || ?1 || '%'")
    fun findByFirstNameOrMiddleNameOrLastName(name: String): List<Patient>

    @Query("SELECT t FROM Patient t order by id desc limit 10")
    fun latestPatients(): List<Patient>
}

@Repository
interface VisitRepository: CrudRepository<Visit, Long>