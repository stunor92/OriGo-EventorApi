package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EventorParsingException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.calendar.UserRace
import no.stunor.origo.eventorapi.services.converter.PersonEntriesConverter
import org.iof.eventor.EntryList
import org.iof.eventor.EventClassList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.ParseException

@Service
class UserEntryService{
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var eventorService: EventorService

    @Autowired
    private lateinit var personEntriesConverter: PersonEntriesConverter
    fun userRaces(userId: String, eventor: Eventor?, eventNumber: String?): List<UserRace> {
        val raceList: MutableList<UserRace> = ArrayList()

        val persons = personRepository.findAllByUsersContains(userId).collectList().block()?: listOf()

        for (person in persons) {
            var personSpecificEventor: Eventor? = null
            if (eventor == null) {
                personSpecificEventor = eventorRepository.findByEventorId(person.eventorId).block()
            } else if (person.eventorId != eventor.eventorId) {
                continue
            }
            val organisationIds: MutableList<String> = ArrayList()
            for (organisationId in person.memberships.keys) {
                organisationIds.add(organisationId)
            }

            var entryList: EntryList?
            try {
                entryList = eventorService.getGetOrganisationEntries((eventor
                        ?: personSpecificEventor)!!, organisationIds, eventNumber)
                val eventClassMap: MutableMap<String, EventClassList> = HashMap()
                for (entry in entryList!!.entry) {
                    for (raceId in entry.eventRaceId) {
                        if (!eventClassMap.containsKey(raceId.content)) {
                            val eventClassList = eventorService.getEventClasses((eventor
                                    ?: personSpecificEventor)!!, entry.event.eventId.content)
                            if(eventClassList !=  null) {
                                eventClassMap[raceId.content] = eventClassList
                            }
                        }
                    }
                }
                val startListList = eventorService.getGetPersonalStarts((eventor
                        ?: personSpecificEventor)!!, person.personId, eventNumber)
                val resultListList = eventorService.getGetPersonalResults((eventor
                        ?: personSpecificEventor)!!, person.personId, eventNumber)
                val personRaces = personEntriesConverter.convertPersonEntries((eventor
                        ?: personSpecificEventor)!!, person, entryList, startListList!!, resultListList!!, eventClassMap)

                for (race in personRaces) {
                    var raceExist = false
                    for ((eventor1, _, _, raceId, _, _, _, userCompetitors, organisationEntries) in raceList) {
                        if (race.eventor.eventorId == eventor1.eventorId && race.raceId == raceId) {
                            raceExist = true
                            userCompetitors.addAll(race.userCompetitors)
                            organisationEntries.putAll(race.organisationEntries)
                        }
                    }
                    if (!raceExist) {
                        raceList.add(race)
                    }
                }
            } catch (e: NumberFormatException) {
                log.warn(e.message)
                throw EventorParsingException()
            } catch (e: ParseException) {
                log.warn(e.message)
                throw EventorParsingException()
            }
        }

        return raceList
    }
}