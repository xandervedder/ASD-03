package nl.asd.workplace.domain;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record Address(String postalCode, String streetName, int houseNumber, String addition) {
    public Address {
        if (postalCode == null) {
            throw new IllegalArgumentException("Postalcode cannot be null");
        }

        if (postalCode.length() > 6 || postalCode.length() < 4) {
            throw new IllegalArgumentException("Postalcode has an invalid length");
        }

        if (streetName == null) {
            throw new IllegalArgumentException("Streetname cannot be null");
        }

        char[] charArray = postalCode.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (i < 4 && !Character.isDigit(c)) {
                throw new IllegalArgumentException("Postalcode has an invalid composition");

            }
            if (i > 4 && !Character.isAlphabetic(c)) {
                throw new IllegalArgumentException("Postalcode has an invalid composition");
            }
        }

        for (char c : streetName.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                throw new IllegalArgumentException("Streetname has invalid characters");
            }
        }
    }
}
