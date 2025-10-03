package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.organisation.Organisation

data class OrganisationEntries(
        var organisation: Organisation = Organisation(),
        var entries: Int = 0
)
