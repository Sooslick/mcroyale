package ru.sooslick.royale;

@FunctionalInterface
public interface Validator {

    int validate(int value, int dflt);

    //todo
    //double validate(double value, double dflt);

}
