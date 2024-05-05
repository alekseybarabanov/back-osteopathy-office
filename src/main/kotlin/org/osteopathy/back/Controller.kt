package org.osteopathy.back

import org.hibernate.SessionFactory
import org.osteopathy.back.entities.Patient
import org.osteopathy.back.repositories.PatientRepository
import org.osteopathy.back.repositories.VisitRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
class Controller(private val patientRepository: PatientRepository,
private val visitRepository: VisitRepository,
private val sessionFactory: SessionFactory) {

    @PostMapping("/api/patient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addPatient(@RequestBody patient: Patient): Long? {
        if (patient.id != null) {
            throw IllegalArgumentException("cannot add patient with existing id")
        }
        sessionFactory.openSession().use { session ->
            val transaction = session.beginTransaction()
            patient.visits?.forEach { visitRepository.save(it) }
            patientRepository.save(patient)
            transaction.commit()
        }

        return patient.id
    }

    @PutMapping("/api/patient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePatient(@RequestBody patient: Patient): Long? {
        if (patient.id == null) {
            throw IllegalArgumentException("cannot update patient without id")
        }
        sessionFactory.openSession().use { session ->
            val transaction = session.beginTransaction()
            patient.visits?.forEach { visitRepository.save(it) }
            patientRepository.save(patient)
            transaction.commit()
        }

        return patient.id
    }

    @GetMapping("/api/patient/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPatient(@PathVariable("id") id: Long): Patient {
        return patientRepository.findById(id).orElseThrow { IllegalArgumentException("Invalid id: $id") }
    }

    @GetMapping("/api/patient/search")
    fun findByName(@RequestParam("name") name: String): List<Patient> {
        val result = ArrayList<Patient>()
        result.addAll(patientRepository.findByFirstNameOrMiddleNameOrLastName(name))

        return result
    }
}