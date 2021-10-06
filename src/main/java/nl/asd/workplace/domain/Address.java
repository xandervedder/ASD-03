package nl.asd.workplace.domain;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record Address(String postalCode, String streetName, int houseNumber, String addition) {
}
