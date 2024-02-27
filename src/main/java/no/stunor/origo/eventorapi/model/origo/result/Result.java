package no.stunor.origo.eventorapi.model.origo.result;

public record Result(
    Integer time,
    Integer timeBehind,
    Integer position,
    String status){
} 