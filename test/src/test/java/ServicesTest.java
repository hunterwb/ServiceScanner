import com.example.AbstractService;
import com.example.ConcreteService;
import com.example.Enum1;
import com.example.GenericService;
import com.example.InterfaceService;
import com.example.NoProvidersService;
import com.example.Provider1;
import com.example.Provider2;
import com.example.Provider3;
import com.example.Provider4;
import com.example.SelfService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.RandomAccess;
import java.util.ServiceLoader;
import java.util.Set;

public final class ServicesTest {

    @Test public void interfaceService() {
        check(
                InterfaceService.class,
                InterfaceService._1.class,
                InterfaceService._2.class,
                InterfaceService._3.class,
                InterfaceService._4.class,
                InterfaceService._5$.class,
                InterfaceService._6.class,
                InterfaceService._7.class,
                Provider1.class,
                Provider3.class
        );
    }

    @Test public void abstractService() {
        check(
                AbstractService.class,
                AbstractService._1.class,
                AbstractService._2.class,
                AbstractService._3.class,
                AbstractService._4.class,
                AbstractService._5.class,
                Provider2.class
        );
    }

    @Test public void concreteService() {
        check(
                ConcreteService.class,
                ConcreteService._1.class,
                ConcreteService._2.class,
                Provider3.class
        );
    }

    @Test public void noProviders() {
        check(NoProvidersService.class);
    }

    @Test public void selfService() {
        check(
                SelfService.class,
                SelfService.class,
                SelfService._1.class,
                SelfService._2$.class
        );
    }

    @Test public void innerService() {
        check(
                Enum1.Service.class,
                Enum1._1.class,
                Enum1._1._2.class,
                Enum1.Ann._3.class
        );
    }

    @Test public void genericService() {
        check(
                GenericService.class,
                GenericService._1.class,
                GenericService._2.class,
                GenericService._3.class,
                GenericService._4.class,
                GenericService._5.class,
                GenericService._6.class,
                GenericService._7.class,
                GenericService._8.class
        );
    }

    @Test public void jdkService() {
        check(RandomAccess.class, Provider4.class);
    }

    @Test public void jdkServiceNoProviders() {
        check(Cloneable.class);
    }

    private static void check(Class<?> service, Class<?>... providers) {
        Set<Class<?>> expected = new LinkedHashSet<>(Arrays.asList(providers));
        Set<Class<?>> actual = new LinkedHashSet<>();
        for (Object p : ServiceLoader.load(service)) actual.add(p.getClass());
        Assertions.assertEquals(expected, actual);
    }
}
