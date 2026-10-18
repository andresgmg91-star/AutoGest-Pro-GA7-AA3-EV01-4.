import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Prueba mínima que confirma que JUnit 5 y Maven quedaron bien configurados. */
class PruebaInicialTest {
    @Test
    void sumaBasica() {
        assertEquals(4, 2 + 2);
    }
}