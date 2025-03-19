package no.stunor.origo.eventorapi.services.converter

import org.iof.eventor.Tele
import org.springframework.stereotype.Component

@Component

class ContactConverter {
    fun convertEmail(teleList: List<Tele>): String? {
        for (tele in teleList) {
            if (tele.teleType.value == "official" && tele.mailAddress != null) {
                return tele.mailAddress
            }
        }
        return null
    }

    fun convertPhone(teleList: List<Tele>): String? {
        for (tele in teleList) {
            if (tele.teleType.value == "official" && tele.mobilePhoneNumber != null) {
                return tele.mobilePhoneNumber
            }
        }
        return null
    }
}
