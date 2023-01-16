package ru.core.base;
import java.io.Serializable;


/**
 * @// FIXME: 16.01.2023
 */
public enum Statistic implements Serializable {
    NULL("NULL"),
    ABORTED("ABORTED"),
   REGISTERED("REGISTERED"),
    RECEIVED_REFERRAL("RECEIVED_REFERRAL"),
    VISITED_DOCTOR("VISITED_DOCTOR"),
   WAITS_FOR_TESTS("WAITS_FOR_TESTS"),
   CURED("CURED");

    private final String value;

    Statistic(final String type) {
        value = type;
    }

    @Override
    public String toString() {
        return value;
    }

    public Statistic convert(String value) {
        if(value.equals("NULL")) return Statistic.NULL;
        if(value.equals("ABORTED")) return Statistic.ABORTED;
        if(value.equals("REGISTERED")) return Statistic.REGISTERED;
        if(value.equals("RECEIVED_REFERRAL")) return Statistic.RECEIVED_REFERRAL;
        if(value.equals("VISITED_DOCTOR")) return Statistic.VISITED_DOCTOR;
        if(value.equals("WAITS_FOR_TESTS")) return Statistic.WAITS_FOR_TESTS;
        if(value.equals("CURED")) return Statistic.CURED;

        return null;
    }


}
