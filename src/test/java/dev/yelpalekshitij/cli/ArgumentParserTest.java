package dev.yelpalekshitij.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTest {

    // ================ Happy Path Tests ================
    @Test
    void parseAllRequiredArgsWithKeyValue() {
        // given
        String[] args = {"--start", "Berlin", "--end", "Hamburg", "--transportation-method", "diesel-car-medium"};

        // when
        var map = ArgumentParser.parse(args);

        // then
        assertEquals("Berlin", map.get("start"));
        assertEquals("Hamburg", map.get("end"));
        assertEquals("diesel-car-medium", map.get("transportation-method"));
    }

    @Test
    void parseAllRequiredArgsWithKeyEqualsValue() {
        // given
        String[] args = {"--start=Berlin", "--end=Hamburg", "--transportation-method=diesel-car-medium"};

        // when
        var map = ArgumentParser.parse(args);

        // then
        assertEquals("Berlin", map.get("start"));
        assertEquals("Hamburg", map.get("end"));
        assertEquals("diesel-car-medium", map.get("transportation-method"));
    }

    @Test
    void parseMixedStyles() {
        // given
        String[] args = {"--start", "Berlin", "--end=Hamburg", "--transportation-method", "diesel-car-medium"};

        // when
        var map = ArgumentParser.parse(args);

        // then
        assertEquals("Berlin", map.get("start"));
        assertEquals("Hamburg", map.get("end"));
        assertEquals("diesel-car-medium", map.get("transportation-method"));
    }

    @Test
    void parseRandomOrder() {
        // given
        String[] args = {"--end=Hamburg", "--start", "Berlin", "--transportation-method", "diesel-car-medium"};

        // when
        var map = ArgumentParser.parse(args);

        // then
        assertEquals("Berlin", map.get("start"));
        assertEquals("Hamburg", map.get("end"));
        assertEquals("diesel-car-medium", map.get("transportation-method"));
    }

    // ================ Missing Required Argument Tests ================
    @Test
    void missingStartThrows() {
        // given
        String[] args = {"--end", "Berlin", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Missing required argument: start"));
    }

    @Test
    void missingEndThrows() {
        // given
        String[] args = {"--start", "Berlin", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Missing required argument: end"));
    }

    @Test
    void missingTransportationMethodThrows() {
        // given
        String[] args = {"--start", "Berlin", "--end", "Hamburg"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Missing required argument: transportation-method"));
    }

    // ================ Missing Value Tests ================
    @Test
    void missingValueLastArgThrows() {
        // given
        String[] args = {"--start", "Berlin", "--end"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Missing value for end"));
    }

    @Test
    void missingValueFollowedByAnotherKeyThrows() {
        // given
        String[] args = {"--start", "Berlin", "--end", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Missing value for end"));
    }

    // ================ Invalid Format Tests ================
    @Test
    void invalidArgumentNoPrefixThrows() {
        // given
        String[] args = {"start", "Berlin", "--end", "Hamburg", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Invalid argument: start"));
    }

    @Test
    void invalidRandomStringThrows() {
        // given
        String[] args = {"--start", "Berlin", "abcd", "--end", "Hamburg", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Invalid argument: abcd"));
    }

    // ================ Duplicate Argument Tests ================
    @Test
    void duplicateKeyThrows() {
        // given
        String[] args = {"--start", "Berlin", "--start", "Hamburg", "--end", "Berlin", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Duplicate argument: start"));
    }

    @Test
    void duplicateKeyEqualsThrows() {
        // given
        String[] args = {"--start=Berlin", "--start=Hamburg", "--end=Berlin", "--transportation-method=diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Duplicate argument: start"));
    }

    // ================ Empty / Null Arguments ================
    @Test
    void emptyArgsThrows() {
        // given
        String[] args = {};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("No arguments provided"));
    }

    @Test
    void nullArgsThrows() {
        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(null));

        // then
        assertTrue(ex.getMessage().contains("No arguments provided"));
    }

    // ================ Edge Cases ================
    @Test
    void extraArgumentThrows() {
        // given
        String[] args = {"--start", "Berlin", "--end", "Hamburg", "--transportation-method", "diesel-car-medium", "extra"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Invalid argument: extra"));
    }

    @Test
    void valueStartsWithDashIsRejected() {
        // given
        String[] args = {"--start", "-Berlin", "--end", "Hamburg", "--transportation-method", "diesel-car-medium"};

        // when
        var ex = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(args));

        // then
        assertTrue(ex.getMessage().contains("Missing value for start"));
    }

    @Test
    void valueContainsEqualsIsAccepted() {
        // given
        String[] args = {"--start", "Berlin=East", "--end", "Hamburg", "--transportation-method", "diesel-car-medium"};

        // when
        var map = ArgumentParser.parse(args);

        // then
        assertEquals("Berlin=East", map.get("start"));
    }
}
