package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.services.converter.CompetitorConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompetitorService{
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var competitorConverter: CompetitorConverter

    @Autowired
    private lateinit var eventorService: EventorService

    fun getCompetitors(eventorId: String, eventId: String, userId: String): List<Competitor> {
        val eventor = eventorRepository.findByEventorId(eventorId).block()?: throw EventorNotFoundException()
        val persons = personRepository.findAllByUsersContainsAndEventorId(userId, eventor.eventorId).collectList().block()?: listOf()
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

            if(resultListList != null) {
                competitorList.addAll(competitorConverter.generateCompetitors(eventor = eventor, resultListList = resultListList, person = person))
            } else if(startListList != null){
                competitorList.addAll(competitorConverter.generateCompetitors(eventor = eventor, startListList = startListList, person = person))

            } else if (entryList != null){
                competitorList.addAll(competitorConverter.generateCompetitors(eventor = eventor, entryList = entryList, person = person))
            }
        }

        return competitorList
    }
    /*
  fun userEntries(userId: String): List<CalendarRace> {
      val raceList: MutableList<CalendarRace> = ArrayList()

      val persons = personRepository.findAllByUsersContains(userId).collectList().block()?: listOf()

      for (person in persons) {
          val eventor = eventorRepository.findByEventorId(person.eventorId).block()?: throw EventorNotFoundException()
          val organisationIds = person.memberships.keys.toList()

          var entryList: EntryList?
          try {
              entryList = eventorService.getGetOrganisationEntries(
                      eventor = eventor,
                      organisations = organisationIds,
                      eventId = null,
                      fromDate = LocalDate.now().minusDays(personalEntriesStart),
                      toDate = LocalDate.now().plusDays(personalEntriesEnd)
              )
              val eventClassMap: MutableMap<String, EventClassList> = HashMap()
              if (entryList != null) {
                  for (entry in entryList.entry) {
                      for (raceId in entry.eventRaceId) {
                          if (!eventClassMap.containsKey(raceId.content)) {
                              val eventClassList = eventorService.getEventClasses(eventor, entry.event.eventId.content)
                              if(eventClassList !=  null) {
                                  eventClassMap[raceId.content] = eventClassList
                              }
                          }
                      }
                  }
              }

              val startListList = eventorService.getGetPersonalStarts(
                      eventor = eventor,
                      personId = person.personId,
                      eventId = null,
                      fromDate = LocalDate.now().minusDays(personalStartsStart),
                      toDate = LocalDate.now().plusDays(personalStartsEnd)
              )
              val resultListList = eventorService.getGetPersonalResults(
                      eventor = eventor,
                      personId = person.personId,
                      eventId = null,
                      fromDate = LocalDate.now().minusDays(personalResultsStart),
                      toDate = LocalDate.now().plusDays(personalResultsEnd)
              )
              val personRaces = personEntriesConverter.convertPersonEntries(
                      eventor = eventor,
                      person = person,
                      entryList = entryList,
                      startListList = startListList,
                      resultListList = resultListList,
                      eventClassMap = eventClassMap
              )

              for (race in personRaces) {
                  var raceExist = false
                  for (r in raceList) {
                      if (race.eventor.eventorId == r.eventor.eventorId && race.raceId == r.raceId) {
                          raceExist = true
                          r.userEntries.addAll(race.userEntries)
                          r.organisationEntries.putAll(race.organisationEntries)
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
  */

/*
    fun userEntriesForCalendar(userId: String, eventor: Eventor, fromDate: LocalDate, toDate: LocalDate): List<CalendarRace> {
        val raceList: MutableList<CalendarRace> = ArrayList()

        val persons = personRepository.findAllByUsersContainsAndEventorId(userId, eventor.eventorId).collectList().block()?: listOf()

        for (person in persons) {
            val organisationIds = person.memberships.keys.toList()

            var entryList: EntryList?
            try {
                entryList = eventorService.getGetOrganisationEntries(eventor = eventor, organisations = organisationIds, eventId = null, fromDate = fromDate, toDate = toDate)
                val eventClassMap: MutableMap<String, EventClassList> = HashMap()
                for (entry in entryList!!.entry) {
                    for (raceId in entry.eventRaceId) {
                        if (!eventClassMap.containsKey(raceId.content)) {
                            val eventClassList = eventorService.getEventClasses(eventor = eventor, eventId = entry.event.eventId.content)
                            if(eventClassList !=  null) {
                                eventClassMap[raceId.content] = eventClassList
                            }
                        }
                    }
                }
                val startListList = eventorService.getGetPersonalStarts(
                        eventor = eventor,
                        personId = person.personId,
                        eventId = null,
                        fromDate = fromDate,
                        toDate = toDate
                )
                val resultListList = eventorService.getGetPersonalResults(
                        eventor = eventor,
                        personId = person.personId,
                        eventId = null,
                        fromDate = fromDate,
                        toDate = toDate
                )
                val personRaces = personEntriesConverter.convertPersonEntries(
                        eventor = eventor,
                        person = person,
                        entryList = entryList,
                        startListList = startListList!!,
                        resultListList = resultListList!!,
                        eventClassMap = eventClassMap
                )

                for (race in personRaces) {
                    var raceExist = false
                    for (r in raceList) {
                        if (race.eventor.eventorId == r.eventor.eventorId && race.raceId == r.raceId) {
                            raceExist = true
                            r.userEntries.addAll(race.userEntries)
                            r.organisationEntries.putAll(race.organisationEntries)
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
*/

}