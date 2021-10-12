package nl.asd.workplace.domain;

// https://docs.oracle.com/en/java/javase/14/language/records.html
public record Address(String postalCode, String streetName, int houseNumber, String addition) {
    public Address {
        if (postalCode == null) {
            throw new RuntimeException("Postalcode cannot be null");
        }
        if (postalCode.length() > 6) {
            throw new RuntimeException("Postalcode has an invalid length");
        }

        char[] charArray = postalCode.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (i <= 3) {
                if (!Character.isDigit(c)) {
                    throw new RuntimeException("Postalcode has an invalid composition");
                }
            } else if (!Character.isAlphabetic(c)) {
                throw new RuntimeException("Postalcode has an invalid composition");
            }
        }

        for (char c : streetName.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                throw new RuntimeException("Streetname has invalid characters");
            }
        }

        if (String.valueOf(houseNumber).toCharArray()[0] == 0) {
            throw new RuntimeException("Housenumber cannot start with 0");
        }

        for (char c : addition.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c) && !Character.isSpaceChar(c)) {
                throw new RuntimeException("Invalid addition");
            }
        }
    }
}
