package org.osteopathy.back.mapper

import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries
import java.time.temporal.TemporalQuery
import java.util.*


@Mapper(componentModel = "spring")
@Component
abstract class PatientMapper {
     abstract fun dtoToEntity(patient: org.osteopathy.back.dto.Patient): org.osteopathy.back.entities.Patient
     abstract fun entityToDto(patient: org.osteopathy.back.entities.Patient): org.osteopathy.back.dto.Patient
     fun mapStrToTimestamp(str: String): Timestamp {
          val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
          val parsedDate = dateFormat.parse(str.substringBefore("Z"))
          return Timestamp(parsedDate.time)
     }

     fun mapTimestampToStr(timestamp: Timestamp): String {
          return SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS").format(timestamp)+"Z"
     }

     @AfterMapping
     fun fillCurrentVisit(@MappingTarget patient: org.osteopathy.back.dto.Patient) {

          val currentDate = LocalDate.now()

          patient.visits?.find {
               val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS")
               val parsedDate = dateFormat.parse(it.visitDate!!.substringBefore("Z")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

               parsedDate == currentDate
          }?.let {
               patient.currentVisit = it
          }
     }

     @AfterMapping
     fun addCurrentVisitToEntity(@MappingTarget patient: org.osteopathy.back.entities.Patient) {

     }
}