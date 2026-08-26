package com.mgps.common.crypto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EncryptedStringConverterTest {

    private static final String KEY = "VhluCDz9dSgWwDskcR3tJ5gSQmTTgHblq+5qveqm9v4=";

    @Test
    void shouldRoundTripAValue() {
        EncryptedStringConverter converter = new EncryptedStringConverter(KEY);

        String stored = converter.convertToDatabaseColumn("9876543210");

        assertThat(stored).isNotNull().isNotEqualTo("9876543210");
        assertThat(converter.convertToEntityAttribute(stored)).isEqualTo("9876543210");
    }

    @Test
    void shouldProduceDifferentCiphertextForTheSamePlaintext() {
        EncryptedStringConverter converter = new EncryptedStringConverter(KEY);

        String first = converter.convertToDatabaseColumn("same-value");
        String second = converter.convertToDatabaseColumn("same-value");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldTolerateLegacyPlaintextOnDecrypt() {
        EncryptedStringConverter converter = new EncryptedStringConverter(KEY);

        assertThat(converter.convertToEntityAttribute("not-encrypted-at-all")).isEqualTo("not-encrypted-at-all");
    }

    @Test
    void shouldPassThroughNulls() {
        EncryptedStringConverter converter = new EncryptedStringConverter(KEY);

        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void shouldRejectBlankKey() {
        assertThatThrownBy(() -> new EncryptedStringConverter(""))
            .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new EncryptedStringConverter(null))
            .isInstanceOf(IllegalStateException.class);
    }
}
