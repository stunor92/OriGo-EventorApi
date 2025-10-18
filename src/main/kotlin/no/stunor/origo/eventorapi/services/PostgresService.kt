package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.ClassRepository
import no.stunor.origo.eventorapi.data.EventRepository
import no.stunor.origo.eventorapi.data.FeeRepository
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.services.converter.FeeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class PostgresService {
    @Autowired
    private lateinit var eventRepository: EventRepository
    @Autowired
    private lateinit var classRepository: ClassRepository
    @Autowired
    private lateinit var feeRepository: FeeRepository
    @Autowired
    private lateinit var eventorService: EventorService

    @Async
    open fun runAsyncPostgresUpdates(event: Event, eventClassList: List<org.iof.eventor.EventClass>) {
        eventRepository.save(event)
        val existingClasses = classRepository.findAllByEventIdAndEventorId(event.eventId, event.eventor.eventorId)
        val newClassIds = event.classes.map { it.classId }.toSet()
        val deletedClasses = existingClasses.filter { it.classId !in newClassIds }
        classRepository.deleteAll(deletedClasses)
        val entryFees = eventorService.getEventEntryFees(event.eventor, event.eventId)
        val fees = FeeConverter.convertEntryFees(entryFees, event, eventClassList)
        feeRepository.saveAll(fees)
        val existingFees = feeRepository.findAllByEventIdAndEventorId(event.eventId, event.eventor.eventorId)
        val newFeeIds = fees.map { it.feeId }.toSet()
        val deletedFees = existingFees.filter { it.feeId !in newFeeIds }
        feeRepository.deleteAll(deletedFees)

    }
}