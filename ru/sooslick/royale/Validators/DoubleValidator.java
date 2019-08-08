package ru.sooslick.royale.Validators;

@FunctionalInterface
public interface DoubleValidator {

    double validate(double value, double dflt);

}
