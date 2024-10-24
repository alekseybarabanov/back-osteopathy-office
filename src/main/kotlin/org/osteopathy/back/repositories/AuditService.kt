package org.osteopathy.back.repositories

import jakarta.transaction.Transactional
import org.osteopathy.back.entities.Audit
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface AuditService {
    fun enterPatient(pid: Int)
    fun createPatient(pid: Int)
}

@Service
class AuditServiceImpl(
    private val audit: AuditRepository,
): AuditService {

    private val pool = Executors.newScheduledThreadPool(1)

    init {
        pool.scheduleAtFixedRate({
            audit.deleteOld(OffsetDateTime.now().minusDays(10))
        }, 0, 10, TimeUnit.DAYS)
    }

    @Transactional
    override fun enterPatient(pid: Int) {
        audit.save(Audit().apply {
            recType = "create"
            patientId = pid
        })
    }

    @Transactional
    override fun createPatient(pid: Int) {
        audit.save(Audit().apply {
            recType = "create"
            patientId = pid
        })
    }

}