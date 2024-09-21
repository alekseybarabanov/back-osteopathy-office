package org.osteopathy.back

import org.osteopathy.back.entities.Patient
import org.osteopathy.back.mapper.PatientMapper
import org.osteopathy.back.repositories.PatientRepository
import org.osteopathy.back.repositories.PatientService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class Controller(
    private val patientRepository: PatientRepository,
    private val mapper: PatientMapper,
    private val patientService: PatientService
) {

    @PostMapping("/{contextpath}/api/patient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addPatient(@RequestBody patient: org.osteopathy.back.dto.Patient): org.osteopathy.back.dto.Patient? {
        if (patient.id != null) {
            throw IllegalArgumentException("cannot add patient with existing id")
        }
        val entityPatient = mapper.dtoToEntity(patient)
        patient.id = patientService.createPatient(entityPatient)

        return mapper.entityToDto(entityPatient)
    }

    @PutMapping("/{contextpath}/api/patient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updatePatient(@RequestBody patient: org.osteopathy.back.dto.Patient): org.osteopathy.back.dto.Patient? {
        if (patient.id == null) {
            throw IllegalArgumentException("cannot update patient without id")
        }
        kotlin.runCatching {
            if (patient.currentVisit != null) {
                if (patient.visits == null || patient.visits!!.size < 1) {
                    patient.visits = Collections.singletonList(patient.currentVisit)
                } else {
                    if (patient.currentVisit!!.id == null) {
                        patient.visits!!.add(patient.currentVisit!!)
                    } else {
                        val ind = patient.visits!!.indexOfFirst { it.id == patient.currentVisit!!.id }
                        if (ind < 0) {
                            patient.visits!!.add(patient.currentVisit!!)
                        } else {
                            patient.visits!![ind] = patient.currentVisit!!
                        }
                    }
                }
            }
        }.onFailure {
            println(patient.id)
        }
        val entityPatient = mapper.dtoToEntity(patient)
        patientService.updatePatient(entityPatient)

        return mapper.entityToDto(entityPatient)
    }

    @GetMapping("/{contextpath}/api/patient/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPatient(@PathVariable("id") id: Long): org.osteopathy.back.dto.Patient {
        val entityPatient = patientService.readPatient(id, true)

        return mapper.entityToDto(entityPatient)
    }

    @GetMapping("/{contextpath}/api/patient/search")
    fun findByName(@RequestParam("name") name: String): List<org.osteopathy.back.dto.Patient> {
        val result = ArrayList<Patient>()
        result.addAll(patientRepository.findByFirstNameOrMiddleNameOrLastName(name))

        return result.map { mapper.entityToDto(it) }
    }

    @GetMapping("/{contextpath}/api/patient/latest")
    fun latestPatients(@PathVariable("contextpath") cp: String): List<org.osteopathy.back.dto.Patient> {
	println("contextpath: $cp")
        return patientService.latestPatients().map { mapper.entityToDto(it) }
    }

}
