package ru.sooslick.royale.Validators;

@FunctionalInterface
public interface IntValidator {

    int validate(int value, int dflt);

}
