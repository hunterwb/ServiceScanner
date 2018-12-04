import com.example.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ServicesTest {

    @Test
    void test() {
        Assertions.assertEquals(
                setOf(
                        InterfaceService._1.class,
                        InterfaceService._2.class,
                        InterfaceService._3.class,
                        InterfaceService._4.class,
                        InterfaceService._5$.class,
                        InterfaceService._6.class,
                        Provider1.class,
                        Provider3.class
                ),
                providers(InterfaceService.class)
        );
        Assertions.assertEquals(
                setOf(
                        AbstractService._1.class,
                        AbstractService._2.class,
                        AbstractService._3.class,
                        AbstractService._4.class,
                        AbstractService._5.class,
                        Provider2.class
                ),
                providers(AbstractService.class)
        );
        Assertions.assertEquals(
                setOf(
                        ConcreteService._1.class,
                        ConcreteService._2.class,
                        Provider3.class
                ),
                providers(ConcreteService.class)
        );
        Assertions.assertEquals(
                setOf(),
                providers(NoProvidersService.class)
        );
        Assertions.assertEquals(
                setOf(
                        SelfService.class,
                        SelfService._1.class,
                        SelfService._2$.class
                ),
                providers(SelfService.class)
        );
    }

    private Set<Class<?>> providers(Class<?> service) {
        Set<Class<?>> set = new HashSet<Class<?>>();
        Iterator<?> itr = ServiceLoader.load(service).iterator();
        while (itr.hasNext()) {
            set.add(itr.next().getClass());
        }
        return set;
    }

    private Set<Class<?>> setOf(Class<?>... classes) {
        return new HashSet<Class<?>>(Arrays.asList(classes));
    }
}
