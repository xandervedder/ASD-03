package nl.asd.workplace.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkplaceTest {
    @Test
    public void workplaceShouldBeReserved() {
        var workplace = new Workplace(1, 2, 1);

        assertTrue(workplace.isReserved());
    }
}
