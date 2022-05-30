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
import com.example.Provider5;
import com.example.SelfService;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.RandomAccess;
import java.util.ServiceLoader;
import java.util.Set;

public final class ServicesTest {

    public void testInterfaceService() {
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

    public void testAbstractService() {
        check(
                AbstractService.class,
                AbstractService._1.class,
                AbstractService._2.class,
                AbstractService._3.class,
                AbstractService._4.class,
                AbstractService._5.class,
                Provider2.class,
                Provider5.class
        );
    }

    public void testConcreteService() {
        check(
                ConcreteService.class,
                ConcreteService._1.class,
                ConcreteService._2.class,
                Provider3.class
        );
    }

    public void testNoProviders() {
        check(NoProvidersService.class);
    }

    public void testSelfService() {
        check(
                SelfService.class,
                SelfService.class,
                SelfService._1.class,
                SelfService._2$.class
        );
    }

    public void testInnerService() {
        check(
                Enum1.Service.class,
                Enum1._1.class,
                Enum1._1._2.class,
                Enum1.Ann._3.class
        );
    }

    public void testGenericService() {
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

    public void testJdkService() {
        check(RandomAccess.class, Provider4.class);
    }

    public void testJdkServiceNoProviders() {
        check(Cloneable.class);
    }

    private static void check(Class<?> service, Class<?>... providers) {
        Set<Class<?>> expected = new LinkedHashSet<Class<?>>(Arrays.asList(providers));
        Set<Class<?>> actual = new LinkedHashSet<Class<?>>();
        for (Object p : ServiceLoader.load(service)) actual.add(p.getClass());
        assert actual.equals(expected) : actual;
    }
}
