package nl.asd.workplace.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
    @Test
    public void shouldThrowWhenPostalCodeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address(null, "Heidelberglaan", 1, ""));
    }

    @Test
    public void shouldThrowWhenPostalCodeIsTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> new Address("123", "Heidelberglaan", 1, ""));
    }

    @Test
    public void shouldThrowWhenPostalCodeHasInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> new Address("aaaaaa", "heidelberglaan", 1, ""));
        assertThrows(IllegalArgumentException.class, () -> new Address("123456", "heidelberglaan", 1, ""));
    }

    @Test
    public void shouldThrowWhenStreetnameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Address("3235XL", null, 1, ""));
    }
}
