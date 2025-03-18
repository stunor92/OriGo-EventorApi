package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.services.converter.CompetitorConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompetitorService{

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var competitorConverter: CompetitorConverter

    @Autowired
    private lateinit var eventorService: EventorService

    fun getCompetitors(eventorId: String, eventId: String, userId: String): List<Competitor> {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val persons = personRepository.findAllByUserIdAndEventorId(userId = userId, eventorId = eventor.eventorId)
        val competitorList: MutableList<Competitor> = mutableListOf()

        for (person in persons) {
            val resultListList = eventorService.getGetPersonalResults(
                    eventor = eventor,
                    personId = person.personId,
                    eventId = eventId,
                    fromDate = null,
                    toDate = null
            )
            val startListList = eventorService.getGetPersonalStarts(
                    eventor = eventor,
                    personId = person.personId,
                    eventId = eventId,
                    fromDate = null,
                    toDate = null
            )
            val entryList = eventorService.getGetOrganisationEntries(
                    eventor = eventor,
                    organisations = person.memberships.keys.toList(),
                    eventId = eventId,
                    fromDate = null,
                    toDate = null
            )

            if(resultListList != null && !resultListList.resultList.isNullOrEmpty()) {
                competitorList.addAll(
                    competitorConverter.generateCompetitors(
                        eventor = eventor,
                        resultListList = resultListList,
                        person = person
                    )
                )
            } else if(startListList != null && !startListList.startList.isNullOrEmpty()){
                competitorList.addAll(
                    competitorConverter.generateCompetitors(
                        eventor = eventor,
                        startListList = startListList,
                        person = person
                    )
                )
            } else if (entryList != null && !entryList.entry.isNullOrEmpty()){
                competitorList.addAll(
                    competitorConverter.generateCompetitors(
                        entryList = entryList,
                        person = person
                    )
                )
            }
        }

        return competitorList
    }
}