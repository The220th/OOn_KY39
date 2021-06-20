package lab;

import lab.*;

import java.util.*;
import java.lang.*;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Класс для тестов
 * 
 */
public class AppTest 
{
    private static int testNUM;

    /**
     * Метод, который будет выполнять перед запуском всех тестов
     * 
     */
    @BeforeClass
    public static void init()
    {
        testNUM = 0;
    }

    /**
     * Тест на проверку связи с СУБД
     * 
     */
    @Test
    public void allEntities_NOT_NULL()
    {
        HibHundler.initFactoryAndSession();
        Salon salon = HibHundler.loadSalon();
        salon.initAllLazyRecords();
        String salonString = salon.toString();
        HibHundler.closeFactoryAndSession();

        assertNotNull(salon);
        assertNotNull(salonString);
        
        Set<Employee> employees = salon.getEmployees();
        assertNotNull(employees);

        Set<Client> clients = salon.getClients();
        assertNotNull(clients);

        Set<Service> services = salon.getDeals();
        assertNotNull(services);

        Set<ServiceType> serviceTypes = salon.getProvidedServices();
        assertNotNull(serviceTypes);

        for(Employee e : employees)
            assertNotNull(e.getMasteredServices());

        for(ServiceType st : serviceTypes)
            assertNotNull(st.getWhoMasteredThisServiceTypes());
    }

    private Salon salon;
    private Set<Employee> esF;
    private Set<Client> csF;
    private Set<Service> ssF;
    private Set<ServiceType> stsF;

    private Employee buffE1;
    private Employee buffE2;
    private Employee buffE3;
    private Employee buffE4;
    private Employee buffE5;

    private Client buffC1;
    private Client buffC2;
    private Client buffC3;
    private Client buffC4;
    private Client buffC5;

    private Service buffS1;
    private Service buffS2;
    private Service buffS3;
    private Service buffS4;
    private Service buffS5;

    private ServiceType buffST1;
    private ServiceType buffST2;
    private ServiceType buffST3;
    private ServiceType buffST4;
    private ServiceType buffST5;

    /**
     * Метод, который будет выполняться перед каждым тестом
     * 
     */
    @Before
    @SuppressWarnings("deprecation")
    public void setUp()
    {
        ++testNUM;

        esF = new HashSet<Employee>();
        csF = new HashSet<Client>();
        ssF = new HashSet<Service>();
        stsF = new HashSet<ServiceType>();

        //========init Salon========
        
        salon = new Salon("Salon Harry Dubua face for test");

        //========init Employees========

        buffST1 = new ServiceType("Paint nails for test", 228, 90, salon);
        stsF.add(buffST1);
        salon.addServiceType(buffST1);

        buffST2 = new ServiceType("Wash hair for test", 1300, 80, salon);
        stsF.add(buffST2);
        salon.addServiceType(buffST2);

        buffST3 = new ServiceType("Massage for test", 3700, 82, salon);
        stsF.add(buffST3);
        salon.addServiceType(buffST3);

        buffST4 = new ServiceType("Manicure for test", 1367.631, 75, salon);
        stsF.add(buffST4);
        salon.addServiceType(buffST4);

        buffST5 = new ServiceType("Restore beauty for test", 2351.2612, 78, salon);
        stsF.add(buffST5);
        salon.addServiceType(buffST5);

        //========init Employees========
        Set<ServiceType> sts4e;

        sts4e = new HashSet<ServiceType>();
        sts4e.add(buffST1); sts4e.add(buffST3); sts4e.add(buffST4);
        buffE1 = new Employee("Boris", "Moris", true, "1223 445678", new Date(1980-1900, 11, 22), sts4e, salon);
        esF.add(buffE1);
        salon.addEmployee(buffE1);

        sts4e = new HashSet<ServiceType>();
        sts4e.add(buffST2); sts4e.add(buffST5);
        buffE2 = new Employee("Angela", "Gorda", false, "1233 425576", new Date(1981-1900, 1, 12), sts4e, salon);
        esF.add(buffE2);
        salon.addEmployee(buffE2);

        sts4e = new HashSet<ServiceType>();
        sts4e.add(buffST2);
        buffE3 = new Employee("Vladimir", "Sun", true, "1111 441618", new Date(1973-1900, 5, 5), sts4e, salon);
        esF.add(buffE3);
        salon.addEmployee(buffE3);

        sts4e = new HashSet<ServiceType>();
        buffE4 = new Employee("Caterina", "Novikova", false, "1236 143648", new Date(1968-1900, 6, 25), sts4e, salon);
        esF.add(buffE4);
        salon.addEmployee(buffE4);

        sts4e = new HashSet<ServiceType>();
        sts4e.add(buffST1); sts4e.add(buffST2); sts4e.add(buffST3); sts4e.add(buffST4); sts4e.add(buffST5);
        buffE5 = new Employee("Muhamed", "Nekajetsyalivam", true, "1326 545277", new Date(1987-1900, 2, 16), sts4e, salon);
        esF.add(buffE5);
        salon.addEmployee(buffE5);

        //========init Clients========

        buffC1 = new Client("Artur", "Piroshkov", true, "1263 436722", new Date(1976-1900, 07, 05), 25, salon);
        csF.add(buffC1);
        salon.addClient(buffC1);

        buffC2 = new Client("Tuko", "Nyka", true, "2323 448258", new Date(1988-1900, 10, 11), 70, salon);
        csF.add(buffC2);
        salon.addClient(buffC2);

        buffC3 = new Client("Maria", "Ra", false, "3329 463498", new Date(2000-1900, 01, 01), 13, salon);
        csF.add(buffC3);
        salon.addClient(buffC3);

        buffC4 = new Client("Jule", "June", false, "6423 433558", new Date(1993-1900, 04, 16), 70, salon);
        csF.add(buffC4);
        salon.addClient(buffC4);

        buffC5 = new Client("Alice", "Akra", false, "1773 463641", new Date(1969-1900, 05, 17), 27, salon);
        csF.add(buffC5);
        salon.addClient(buffC5);

        //========init Clients========

        buffS1 = new Service(buffST1, new Date(2005-1900, 11, 12), buffE1, buffC1, salon);
        ssF.add(buffS1);
        salon.addService(buffS1);

        buffS2 = new Service(buffST2, new Date(2005-1900, 11, 13), buffE1, buffC1, salon);
        ssF.add(buffS2);
        salon.addService(buffS2);

        buffS3 = new Service(buffST2, new Date(2005-1900, 11, 18), buffE1, buffC5, salon);
        ssF.add(buffS3);
        salon.addService(buffS3);

        buffS4 = new Service(buffST3, new Date(2005-1900, 11, 19), buffE2, 1000, buffC2, 100000.76, salon);
        ssF.add(buffS4);
        salon.addService(buffS4);

        buffS5 = new Service(buffST5, new Date(2003-1900, 5, 12), buffE3, buffC4, salon);
        ssF.add(buffS5);
        salon.addService(buffS5);
    }

    /**
     * Тест на правильное добавление данных в салон
     * 
     */
    @Test
    public void rigthAdd()
    {
        assertEquals(esF, salon.getEmployees());

        assertEquals(csF, salon.getClients());

        assertEquals(stsF, salon.getProvidedServices());

        assertEquals(ssF, salon.getDeals());
    }

    /**
     * Тест на правильный подсчёт лучшего по доходности сотрудника
     * 
     */
    @Test
    @SuppressWarnings("deprecation")
    public void rigthTaskBestMoney()
    {
        assertEquals(buffE2, salon.cal_BestCashEmployee_Month(new Date(2005-1900, 11, 1)));
        assertEquals(buffE1, salon.cal_BestCashEmployee_Week(new Date(2005-1900, 11, 14)));
        assertEquals(null, salon.cal_BestCashEmployee_Week(new Date(2000-1900, 11, 14)));
    }

    /**
     * Тест на правильный подсчёт лучшего по трафику сотрудника
     * 
     */
    @Test
    @SuppressWarnings("deprecation")
    public void rigthTaskBestTraffic()
    {
        assertEquals(buffE1, salon.cal_BestTrafficEmployee_Month(new Date(2005-1900, 11, 1)));
        assertEquals(buffE2, salon.cal_BestTrafficEmployee_Week(new Date(2005-1900, 11, 20)));
        assertEquals(null, salon.cal_BestTrafficEmployee_Day(new Date(2005-1900, 11, 1)));
    }

    /**
     * Тест на правильный подсчёт трафика салона красоты
     * 
     */
    @Test
    @SuppressWarnings("deprecation")
    public void rigthTaskTraffic()
    {
        assertEquals(3, salon.calculateСlientsNumMonth(new Date(2005-1900, 11, 12), true));
        assertEquals(4, salon.calculateСlientsNumMonth(new Date(2005-1900, 11, 12), false));
        assertEquals(2, salon.calculateСlientsNumWeek(new Date(2005-1900, 11, 12), true));
        assertEquals(3, salon.calculateСlientsNumWeek(new Date(2005-1900, 11, 12), false));
        assertEquals(0, salon.calculateСlientsNumDay(new Date(1999-1900, 11, 1), true));
    }

    /**
     * Тест на правильное получение сервисов по датам
     * 
     */
    @Test
    @SuppressWarnings("deprecation")
    public void rigthTaskGetDates()
    {
        Set<Service> forMonth = new HashSet<Service>();
        forMonth.add(buffS1); forMonth.add(buffS2); forMonth.add(buffS3); forMonth.add(buffS4);
        assertEquals(forMonth, salon.showServicesPerMonth(new Date(2005-1900, 11, 12)));

        Set<Service> forWeek = new HashSet<Service>();
        forWeek.add(buffS1); forWeek.add(buffS2); forWeek.add(buffS3);
        assertEquals(forWeek, salon.showServicesPerWeek(new Date(2005-1900, 11, 12)));

        Set<Service> forDay = new HashSet<Service>();
        forDay.add(buffS1);
        assertEquals(forDay, salon.showServicesPerDay(new Date(2005-1900, 11, 12)));

        Set<Service> forDayZero = new HashSet<Service>();
        assertEquals(forDayZero, salon.showServicesPerWeek(new Date(1999-1900, 11, 12)));
    }

    /**
     * Тест на правильное определение пола
     * 
     */
    @Test
    public void truerino()
    {
        assertTrue( buffE1.getGender() == true );
        assertTrue( buffE2.getGender() == false );
        assertTrue( buffE3.getGender() == true );
        assertTrue( buffE4.getGender() == false );
        assertTrue( buffE5.getGender() == true );

        assertTrue( buffC1.getGender() == true );
        assertTrue( buffC2.getGender() == true );
        assertTrue( buffC3.getGender() == false );
        assertTrue( buffC4.getGender() == false );
        assertTrue( buffC5.getGender() == false );
        //Больше тестов!=)
        assertTrue( true );
    }

    /**
     * Метод, который будет запускаться после каждого теста
     * 
     */
    @After
    public void testEnds()
    {
        System.out.println("Test " + AppTest.testNUM + " finished");
    }

    /**
     * Метод, который запуститься после окончания всех тестов в данном классе
     * 
     */
    @AfterClass
    public static void allTestEnds()
    {
        System.out.println("All test finished");
    }
}
