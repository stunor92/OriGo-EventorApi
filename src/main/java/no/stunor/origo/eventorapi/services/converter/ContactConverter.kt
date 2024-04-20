package no.stunor.origo.eventorapi.services.converter;

import java.util.List;

import org.iof.eventor.Tele;

public class ContactConverter {
 
      public static String convertEmail(List<Tele> teleList) {
        if(teleList == null){
            return null;
        }
        for(Tele tele : teleList){
            if(tele.getTeleType().getValue().equals("official")){
                if(tele.getMailAddress() != null){
                    return tele.getMailAddress();
                }
            }
        }
        return null;
    }

    public static String convertPhone(List<Tele> teleList) {
        if(teleList == null){
            return null;
        }
        for(Tele tele : teleList){
            if(tele.getTeleType().getValue().equals("official")){
                if(tele.getMobilePhoneNumber() != null){
                    return tele.getMobilePhoneNumber();
                }
            }
        }
        return null;
    }
}
