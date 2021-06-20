/*
Задание 21. Разработать ПК (программный комплекс) для администратора салона красоты. 
В ПК должна храниться информация о клиентах салона, мастерах с указанием их специализации, 
услугах и их ценах. 
Администратор может добавлять, изменять или удалять эту информацию. 
Ему могут понадобиться следующие сведения и возможности:
    • Просмотр расписания работы салона на выбранный день
    • Просмотр загруженности определённого мастера на день/неделю/месяц
    • Запись клиента на процедуру к определённому мастеру
    • Отчет о работе салона за неделю/месяц: 
    	сколько клиентов было обслужено,
    	сколько денег пришло, 
    	какие мастера самые востребованные 
    	и какие – самые доходные
https://imgur.com/dPRseoY
*/

package lab;

import lab.*;

import java.lang.*;
import java.util.*;

import javax.persistence.*;

/**
 * Салон красоты
 * 
 */
@Entity
@Table(name="SALON")
public class Salon
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SALONID")
	private int salonID;

	@Column(name="SALONNAME")
	private String salonName;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "salon", cascade = CascadeType.ALL)
	private Set<ServiceType> serviceTypes;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "salon", cascade = CascadeType.ALL)
	private Set<Service> allServices;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "salon", cascade = CascadeType.ALL)
	private Set<Employee> allEmployees;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "salon", cascade = CascadeType.ALL)
	private Set<Client> allClients;

	public Salon(){init4Const();}

	public Salon(String name)
	{
		init4Const();
		this.salonName = name;
	}

	private void init4Const()
	{
		this.serviceTypes = new HashSet<ServiceType>();
		this.allServices = new HashSet<Service>();
		this.allEmployees = new HashSet<Employee>();
		this.allClients = new HashSet<Client>();
	}

	//Сотрудники
	/**
	 * Добавить сотрудника в салон красоты
	 * 
	 * @param employee сотрудник, принимаемый на работу
	 */
	public synchronized void addEmployee(Employee employee)
	{
		if(!allEmployees.contains(employee))
			this.allEmployees.add(employee);
	}

	/**
	 * Добавить сотрудников в салон красоты
	 * 
	 * @param employees сотрудники, принимаемые на работу
	 */
	public synchronized void addEmployee(Set<Employee> employees)
	{
		for(Employee e : employees)
			if(!this.allEmployees.contains(e))
				this.allEmployees.add(e);
	}

	/**
	 * Удалить сотрудника из салона красоты
	 * 
	 * @param employee удаляемый сотрудник
	 */
	public synchronized void fireEmployee(Employee employee)
	{
		this.allEmployees.remove(employee);
	}

	//Клиенты
	/**
	 * Добавить клиента
	 * 
	 * @param client добавляемый клиент
	 */
	public synchronized void addClient(Client client)
	{
		if(!this.allClients.contains(client))
			this.allClients.add(client);
	}

	/**
	 * Добавить клиентов
	 * 
	 * @param clients добавляемые клиенты
	 */
	public synchronized void addClient(Set<Client> clients)
	{
		for(Client c : clients)
			if(!this.allClients.contains(c))
				this.allClients.add(c);
	}

	//Сервисы
	/**
	 * Добавить сервис
	 * 
	 * @param service добавляемый сервис
	 */
	public synchronized void addService(Service service)
	{
		if(!this.allServices.contains(service))
			this.allServices.add(service);
	}

	/**
	 * Добавить сервисы
	 * 
	 * @param services добавляемые сервисы
	 */
	public synchronized void addService(Set<Service> services)
	{
		for(Service s : services)
			if(!this.allServices.contains(s))
				this.allServices.add(s);
	}

	//Типы сервисов
	/**
	 * Добавить тип сервиса
	 * 
	 * @param serviceType добавляемый тип сервиса
	 */
	public synchronized void addServiceType(ServiceType serviceType)
	{
		if(!this.serviceTypes.contains(serviceType))
			this.serviceTypes.add(serviceType);
	}

	/**
	 * Добавить типы сервисов
	 * 
	 * @param serviceTypes добавляемый типы сервисов
	 */
	public synchronized void addServiceType(Set<ServiceType> serviceTypes)
	{
		for(ServiceType st : serviceTypes)
			if(!this.serviceTypes.contains(st))
				this.serviceTypes.add(st);
	}

	/**
	 * Получить название салона красоты
	 * 
	 * @return название салона красоты
	 */
	public String getName()
	{
		return this.salonName;
	}

	/**
	 * Получить сотрудников
	 * 
	 * @return сотрудники салона
	 */
	public synchronized Set<Employee> getEmployees()
	{
		return new HashSet<Employee>(this.allEmployees);
	}

	/**
	 * Получить клиентов
	 * 
	 * @return клиенты салона
	 */
	public synchronized Set<Client> getClients()
	{
		return new HashSet<Client>(this.allClients);
	}

	/**
	 * Получить сервисы
	 * 
	 * @return проведённые сервисы
	 */
	public synchronized Set<Service> getDeals()
	{
		return new HashSet<Service>(this.allServices);
	}

	/**
	 * Получить типы сервисов
	 * 
	 * @return проводимые типы сервисов
	 */
	public synchronized Set<ServiceType> getProvidedServices()
	{
		return new HashSet<ServiceType>(this.serviceTypes);
	}

	/**
	 * Получить ID салона красоты
	 * 
	 * @return ID салона красоты
	 */
	public int getID()
	{
		return this.salonID;
	}

	/**
	 * Инициализация салона
	 * 
	 * @return количество элементов в салоне
	 */
	public synchronized int initAllLazyRecords()
	{
		int counter = 0;
		for(Employee e : allEmployees)
		{
			++counter;
			e.getMasteredServices();
		}
		for(Client c : allClients)
			++counter;
		for(Service s : allServices)
			++counter;
		for(ServiceType st : serviceTypes)
		{
			++counter;
			st.getWhoMasteredThisServiceTypes();
		}
		return counter;
	}

	//============================================Задания==================================================================

	/**
	* Посмотреть все услуги, которые проводились в день day (укажите любое время этого дня)
	*/
	@SuppressWarnings( "deprecation" )
	public synchronized Set<Service> showServicesPerDay(Date day)
	{
		int neededDay = day.getDate();
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		for(Service s : allServices)
			if(s.getDateBegin().getMonth() == day.getMonth() && s.getDateBegin().getYear() == day.getYear() && s.isRELEVANT() == true)
				if(s.getDateBegin().getDate() == neededDay)
					res.add(s);
		return res;
	}

	/**
	* Посмотреть все услуги, которые проводились в неделю week (укажите любое время этой недели)
	*/
	@SuppressWarnings( "deprecation" )
	public synchronized Set<Service> showServicesPerWeek(Date week)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Date[] ds = getWeekKnowsDay(week);
		for(Service s : allServices)
			if(s.getDateBegin().getMonth() == week.getMonth() && s.getDateBegin().getYear() == week.getYear() && s.isRELEVANT() == true)
				if(
					s.getDateBegin().getDate() == ds[0].getDate()
					|| s.getDateBegin().getDate() == ds[1].getDate()
					|| s.getDateBegin().getDate() == ds[2].getDate()
					|| s.getDateBegin().getDate() == ds[3].getDate()
					|| s.getDateBegin().getDate() == ds[4].getDate()
					|| s.getDateBegin().getDate() == ds[5].getDate()
					|| s.getDateBegin().getDate() == ds[6].getDate()
				)
					res.add(s);
		return res;
	}

	/**
	 * Получить дни недели
	 * 
	 * @param oneDayOfThisWeek день недели
	 * @return массив дней, принадлежащей недели
	 */
	@SuppressWarnings( "deprecation" )
	public synchronized static Date[] getWeekKnowsDay(Date oneDayOfThisWeek)
	{
		Date one = new Date(oneDayOfThisWeek.getTime());
		if(one.getDay() == 0)
			one.setDate(one.getDate()-6);
		else
		{
			one.setDate(one.getDate()-(one.getDay()-1));
		}
		Date[] ds = new Date[7];
		Date buff;
		for(int i = 0; i < 7; ++i)
		{
			buff = new Date(one.getTime());
			buff.setDate(buff.getDate()+i);
			ds[i] = buff;
		}
		return ds;
	}

	/**
	* Посмотреть все услуги, которые проводились в месяц month (укажите любое время этого месяца)
	*/
	@SuppressWarnings( "deprecation" )
	public synchronized Set<Service> showServicesPerMonth(Date month)
	{

		int neededMonth = month.getMonth();
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		for(Service s : allServices)
			if(s.getDateBegin().getYear() == month.getYear() && s.isRELEVANT() == true)
				if(s.getDateBegin().getMonth() == neededMonth)
					res.add(s);
		return res;
	}

	/**
	* Вычислить загруженность сотрудника за месяц month (укажите любую дату из этого месяца)
	*/
	public Set<Service> calculateWorkloadMonth(Date month, Employee employee)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Set<Service> perMonth = showServicesPerMonth(month);
		for(Service s : perMonth)
			if(s.getEmployee().getID() == employee.getID())
				res.add(s);
		return res;
	}

	/**
	* Вычислить загруженность сотрудника за неделю week (укажите любой день интересующей недели)
	*/
	public Set<Service> calculateWorkloadWeek(Date week, Employee employee)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Set<Service> perWeek = showServicesPerWeek(week);
		for(Service s : perWeek)
			if(s.getEmployee().getID() == employee.getID())
				res.add(s);
		return res;
	}

	/**
	* Вычислить загруженность сотрудника за день day (укажите любое время интересующего дня)
	*/
	public Set<Service> calculateWorkloadDay(Date day, Employee employee)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Set<Service> perDay = showServicesPerDay(day);
		for(Service s : perDay)
			if(s.getEmployee().getID() == employee.getID())
				res.add(s);
		return res;
	}

	/**
	* Вычислить клиента client специалисту employee за месяц month (укажите любую дату из этого месяца)
	*/
	public Set<Service> calculateClientRaspMonth(Date month, Employee employee, Client client)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Set<Service> perMonth = showServicesPerMonth(month);
		for(Service s : perMonth)
			if(s.getEmployee().getID() == employee.getID() && s.getClient().getID() == client.getID())
				res.add(s);
		return res;
	}

	/**
	* Вычислить клиента client специалисту employee за неделю week (укажите любой день интересующей недели)
	*/
	public Set<Service> calculateClientRaspWeek(Date week, Employee employee, Client client)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Set<Service> perWeek = showServicesPerWeek(week);
		for(Service s : perWeek)
			if(s.getEmployee().getID() == employee.getID() && s.getClient().getID() == client.getID())
				res.add(s);
		return res;
	}

	/**
	* Вычислить клиента client специалисту employee за день day (укажите любое время интересующего дня)
	*/
	public Set<Service> calculateClientRaspDay(Date day, Employee employee, Client client)
	{
		Set<Service> res = new TreeSet<Service>(GreatGenerator.getServicesDateComporator());
		Set<Service> perDay = showServicesPerDay(day);
		for(Service s : perDay)
			if(s.getEmployee().getID() == employee.getID() && s.getClient().getID() == client.getID())
				res.add(s);
		return res;
	}

	/**
	* Посчитать клиентов за месяц (укажите любую дату интересующего месяца)
	* Если unique == true, то покажет только уникальных клиентов
	*/
	public int calculateСlientsNumMonth(Date month, boolean unique)
	{
		int resInt;
		Set<Client> res = new HashSet<Client>();
		Set<Service> perMonth = showServicesPerMonth(month);
		resInt = 0;
		for(Service s : perMonth)
		{
			res.add(s.getClient());
			++resInt;
		}
		if(unique == true)
			return res.size();
		else
			return resInt;
	}

	/**
	* Посчитать клиентов за неделю (укажите любой день интересующей недели)
	* Если unique == true, то покажет только уникальных клиентов
	*/
	public int calculateСlientsNumWeek(Date week, boolean unique)
	{
		int resInt;
		Set<Client> res = new HashSet<Client>();
		Set<Service> perWeek = showServicesPerWeek(week);
		resInt = 0;
		for(Service s : perWeek)
		{
			res.add(s.getClient());
			++resInt;
		}
		if(unique == true)
			return res.size();
		else
			return resInt;
	}

	/**
	* Посчитать клиентов за день
	* Если unique == true, то покажет только уникальных клиентов
	*/
	public int calculateСlientsNumDay(Date day, boolean unique)
	{
		int resInt;
		Set<Client> res = new HashSet<Client>();
		Set<Service> perDay = showServicesPerDay(day);
		resInt = 0;
		for(Service s : perDay)
		{
			res.add(s.getClient());
			++resInt;
		}
		if(unique == true)
			return res.size();
		else
			return resInt;
	}

	/**
	* Вычислить приход денег за месяц (укажите любую дату интересующего месяца)
	* Если percent == true, то вычислит доход с учётом выплаченных процентов сотрудникам, иначе полную сумму
	*/
	public double calculateIncomeCashMonth(Date month, boolean percent)
	{
		double res;
		Set<Service> perMonth = showServicesPerMonth(month);
		res = 0;
		for(Service s : perMonth)
			res += percent==true?(s.getPrice()-s.getCashReward()):(s.getPrice());
		return res;
	}

	/**
	* Вычислить приход денег за неделю (укажите любой день интересующей недели)
	* Если percent == true, то вычислит доход с учётом выплаченных процентов сотрудникам, иначе полную сумму
	*/
	public double calculateIncomeCashWeek(Date week, boolean percent)
	{
		double res;
		Set<Service> perWeek = showServicesPerWeek(week);
		res = 0;
		for(Service s : perWeek)
			res += percent==true?(s.getPrice()-s.getCashReward()):(s.getPrice());
		return res;
	}

	/**
	* Вычислить приход денег за день
	* Если percent == true, то вычислит доход с учётом выплаченных процентов сотрудникам, иначе полную сумму
	*/
	public double calculateIncomeCashDay(Date day, boolean percent)
	{
		double res;
		Set<Service> perDay = showServicesPerDay(day);
		res = 0;
		for(Service s : perDay)
			res += percent==true?(s.getPrice()-s.getCashReward()):(s.getPrice());
		return res;
	}

	/**
	* Вычислить лучшего по прибыли работника за месяц (укажите любую дату интересующего месяца)
	*/
	public Employee cal_BestCashEmployee_Month(Date month)
	{
		HashMap<Employee, Double> dict = new HashMap<Employee, Double>();
		Set<Service> perMonth = showServicesPerMonth(month);
		for(Service s : perMonth)
			dict.put(s.getEmployee(), Double.valueOf( 0 ));
		for(Service s : perMonth)
			dict.put(s.getEmployee(), Double.valueOf( dict.get(s.getEmployee()).doubleValue() + (s.getPrice() - s.getCashReward()) ));
		Employee res = null;
		double max = -1;
		Set<Employee> es = dict.keySet();
		for(Employee e : es)
			if(max < dict.get(e).doubleValue())
			{
				max = dict.get(e).doubleValue();
				res = e;
			}
		return res;
	}

	/**
	* Вычислить лучшего по прибыли работника за неделю (укажите любой день интересующей недели)
	*/
	public Employee cal_BestCashEmployee_Week(Date week)
	{
		HashMap<Employee, Double> dict = new HashMap<Employee, Double>();
		Set<Service> perWeek = showServicesPerWeek(week);
		for(Service s : perWeek)
			dict.put(s.getEmployee(), Double.valueOf( 0 ));
		for(Service s : perWeek)
			dict.put(s.getEmployee(), Double.valueOf( dict.get(s.getEmployee()).doubleValue() + (s.getPrice() - s.getCashReward()) ));
		Employee res = null;
		double max = -1;
		Set<Employee> es = dict.keySet();
		for(Employee e : es)
			if(max < dict.get(e).doubleValue())
			{
				max = dict.get(e).doubleValue();
				res = e;
			}
		return res;
	}

	/**
	* Вычислить лучшего по прибыли работника за день
	*/
	public Employee cal_BestCashEmployee_Day(Date day)
	{
		HashMap<Employee, Double> dict = new HashMap<Employee, Double>();
		Set<Service> perDay = showServicesPerDay(day);
		for(Service s : perDay)
			dict.put(s.getEmployee(), Double.valueOf( 0 ));
		for(Service s : perDay)
			dict.put(s.getEmployee(), Double.valueOf( dict.get(s.getEmployee()).doubleValue() + (s.getPrice() - s.getCashReward()) ));
		Employee res = null;
		double max = -1;
		Set<Employee> es = dict.keySet();
		for(Employee e : es)
			if(max < dict.get(e).doubleValue())
			{
				max = dict.get(e).doubleValue();
				res = e;
			}
		return res;
	}

	/**
	* Вычислить лучшего по посещаемости работника за месяц (укажите любую дату интересующего месяца)
	*/
	public Employee cal_BestTrafficEmployee_Month(Date month)
	{
		HashMap<Employee, Integer> dict = new HashMap<Employee, Integer>();
		Set<Service> perMonth = showServicesPerMonth(month);
		for(Service s : perMonth)
			dict.put(s.getEmployee(), Integer.valueOf( 0 ));
		for(Service s : perMonth)
			dict.put(s.getEmployee(), Integer.valueOf( dict.get(s.getEmployee()).intValue() + 1 ) );
		Employee res = null;
		double max = -1;
		Set<Employee> es = dict.keySet();
		for(Employee e : es)
			if(max < dict.get(e).intValue())
			{
				max = dict.get(e).intValue();
				res = e;
			}
		return res;
	}

	/**
	* Вычислить лучшего по посещаемости работника за неделю (укажите любой день интересующей недели)
	*/
	public Employee cal_BestTrafficEmployee_Week(Date week)
	{
		HashMap<Employee, Integer> dict = new HashMap<Employee, Integer>();
		Set<Service> perWeek = showServicesPerWeek(week);
		for(Service s : perWeek)
			dict.put(s.getEmployee(), Integer.valueOf( 0 ));
		for(Service s : perWeek)
			dict.put(s.getEmployee(), Integer.valueOf( dict.get(s.getEmployee()).intValue() + 1 ) );
		Employee res = null;
		double max = -1;
		Set<Employee> es = dict.keySet();
		for(Employee e : es)
			if(max < dict.get(e).intValue())
			{
				max = dict.get(e).intValue();
				res = e;
			}
		return res;
	}

	/**
	* Вычислить лучшего по посещаемости работника за день
	*/
	public Employee cal_BestTrafficEmployee_Day(Date day)
	{
		HashMap<Employee, Integer> dict = new HashMap<Employee, Integer>();
		Set<Service> perDay = showServicesPerDay(day);
		for(Service s : perDay)
			dict.put(s.getEmployee(), Integer.valueOf( 0 ));
		for(Service s : perDay)
			dict.put(s.getEmployee(), Integer.valueOf( dict.get(s.getEmployee()).intValue() + 1 ) );
		Employee res = null;
		double max = -1;
		Set<Employee> es = dict.keySet();
		for(Employee e : es)
			if(max < dict.get(e).intValue())
			{
				max = dict.get(e).intValue();
				res = e;
			}
		return res;
	}

	public String toString()
	{
		StringBuilder s;
		synchronized(this)
		{
			s = new StringBuilder();

			s.append("Salon " + this.getID() + " " + this.getName() + ": \n");

			s.append("\nCan provide services: \n");
			for(ServiceType st : serviceTypes)
				s.append("\t" + st + "\n");

			s.append("\nHave employees: \n");
			for(Employee e : allEmployees)
				s.append("\t" + e + "\n");

			s.append("\nHad clients: \n");
			for(Client c : allClients)
				s.append("\t" + c + "\n");

			s.append("\nProvided services: \n");
			for(Service se : allServices)
				s.append("\t" + se + "\n");

			s.append("\n");
		}

		return s.toString();
	}

    @Override
    public boolean equals(Object otherObj) 
	{
		if (otherObj == this) return true; 
		if (otherObj == null) return false;
		if( this.getClass() != otherObj.getClass() ) return false;
		Salon other = (Salon)otherObj;
		return this == other;
		//return this.getID() == other.getID();
    }

	/**
	 * Получить сотрудника по его ID
	 * 
	 * @param ID ид сотрудника
	 * @return сотрудник
	 */
    public synchronized Employee getEmployeeByID(int ID)
    {
    	for(Employee e : allEmployees)
    		if(e.getID() == ID)
    			return e;
    	return null;
    }

	/**
	 * Получить клиента по его ID
	 * 
	 * @param ID ид клиента
	 * @return клиент
	 */
    public synchronized Client getClientByID(int ID)
    {
    	for(Client c : allClients)
    		if(c.getID() == ID)
    			return c;
    	return null;
    }

	/**
	 * Получить сервис по его ID
	 * 
	 * @param ID ид сервиса
	 * @return сервис
	 */
    public synchronized Service getServiceByID(int ID)
    {
    	for(Service s : allServices)
    		if(s.getID() == ID)
    			return s;
    	return null;
    }

	/**
	 * Получить тип сервиса по его ID
	 * 
	 * @param ID ид типа сервиса
	 * @return тип сервиса
	 */
    public synchronized ServiceType getServiceTypeByID(int ID)
    {
    	for(ServiceType st : serviceTypes)
    		if(st.getID() == ID)
    			return st;
    	return null;
    }

	/**
	 * Генерирует рандомный новый сервис и добавляет его в салон красоты
	 * 
	 * @param from начиная с какой даты
	 * @param to какой датой заканчивая
	 */
	@SuppressWarnings( "deprecation" )
	public void genNewService(Date from, Date to)
	{
		Random r = new Random();
		long from_i = from.getTime();
		long to_i = to.getTime();
		long cur_i = genLong(to_i-from_i) + from_i;

		int st_id = r.nextInt(serviceTypes.size())+1;
		int e_id = r.nextInt(allEmployees.size())+1;
		int c_id = r.nextInt(allClients.size())+1;
		ServiceType st = this.getServiceTypeByID(st_id);

		Service s = new Service(
			st,
			new Date(cur_i),
			this.getEmployeeByID(e_id),
			st.getCurrentPrice() * st.getPercent()*0.01,
			this.getClientByID(c_id),
			st.getCurrentPrice(),
			this
		);
		synchronized(this)
		{
			this.addService(s);
		}
		System.out.println(s);
	}

	private long genLong(long n)
	{
		// error checking and 2^x checking removed for simplicity.
		Random rng = new Random();
		long bits, val;
		do {
		   bits = (rng.nextLong() << 1) >>> 1;
		   val = bits % n;
		} while (bits-val+(n-1) < 0L);
		return val;
	 }

	/**
	 * Преобразует дату в красиво читаемый вид
	 * 
	 * @param d дата для преобразования
	 * @return строка, представляющей дату
	 */
    @SuppressWarnings( "deprecation" )
    public static String doNiceDate(Date d)
    {
        String toOut = (d.getYear()+1900) + ".";
        toOut += (d.getMonth()+1>9?d.getMonth()+1:"0"+(d.getMonth()+1)) + ".";
        toOut += (d.getDate()>9?d.getDate():"0"+d.getDate()) + " ";
        toOut += (d.getHours()>9?d.getHours():"0"+d.getHours()) + ":";
        toOut += (d.getMinutes()>9?d.getMinutes():"0"+d.getMinutes()) + ":";
        toOut += (d.getSeconds()>9?d.getSeconds():"0"+d.getSeconds());
        return toOut;
    }

	/**
	 * Преобразует дату в красиво читаемый вид, но только год, месяц, день
	 * 
	 * @param d дата для преобразования
	 * @return строка, представляющей дату
	 */
    @SuppressWarnings( "deprecation" )
    public static String doOnlyDate(Date d)
    {
        String toOut = "";
        toOut += (d.getDate()>9?d.getDate():"0"+d.getDate()) + ".";
        toOut += (d.getMonth()+1>9?d.getMonth()+1:"0"+(d.getMonth()+1)) + ".";
        toOut += (d.getYear()+1900);
        return toOut;
    }

	/**
	 * Проверить имя на валидность
	 * 
	 * @param name имя для проверки
	 * @return имя, если проверка прошла успешно, иначе исключение
	 * 
	 * @throws InvalidNameException
	 */
	public static String checkName4Salon(String name)
	{
		String controlStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890 zyxwvutsrqponmlkjihgfedcba_-,";
		for(int i = 0 ; i < name.length(); ++i)
			if(controlStr.contains("" + name.charAt(i)) == false)
				throw new InvalidNameException("You can only enter alphabetic characters, numbers and characters \"_\", \"-\", \",\". You inputted: \"" + name + "\". ", "" + name.charAt(i));
		return name;
	}
}
