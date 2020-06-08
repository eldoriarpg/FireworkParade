import de.eldoria.fireworkfun.listener.ParticleMap;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParticleMapText extends ParticleMap {
    public ParticleMapText() {
        super("name");
    }

    @Test
    public void rotateAroundZeroTest() {
        Vector vector = rotateAroundZero(new Vector(0, 0, 1), 0);
        Assertions.assertEquals(new Vector(0, 0, 1), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 90);
        Assertions.assertEquals(new Vector(-1, 0, 0), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 180);
        Assertions.assertEquals(new Vector(0, 0, -1), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 270);
        Assertions.assertEquals(new Vector(1, 0, 0), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 45);
        Assertions.assertEquals(new Vector(-0.71, 0, 0.71), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 90 + 45);
        Assertions.assertEquals(new Vector(-0.71, 0, -0.71), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 180 + 45);
        Assertions.assertEquals(new Vector(0.71, 0, -0.71), vector);
        vector = rotateAroundZero(new Vector(0, 0, 1), 270 + 45);
        Assertions.assertEquals(new Vector(0.71, 0, 0.71), vector);
    }

}
