package lab;

import lab.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.lang.*;
import java.util.*;
import java.io.*;

/**
 * Обработчик для hibernate
 * 
 */
public class HibHundler
{
	private static SessionFactory factory;
	private static Session session;
	
	static
	{
		//initFactoryAndSession();
	}

	/**
	 * Загрузить салон из БД
	 * 
	 * @return загруженный салон
	 */
	public static Salon loadSalon()
	{
		int sad_i = 1;
		Salon loadedSalon = null;
		do
		{
			loadedSalon = session.get(Salon.class, sad_i++);
		}while(loadedSalon == null);
		return loadedSalon;
	}

	/**
	 * Сохранить салон в БД
	 * 
	 * @param salon сохраняемый салон
	 */
	public static void saveSalon(Salon salon)
	{
		session.saveOrUpdate(salon);
	}

	/**
	 * Сохранить сущность в БД
	 * 
	 * @param obj сущность
	 */
	public static void saveObject(Object obj)
	{
		session.saveOrUpdate(obj);
	}

	/**
	 * Проинициализировать factory и session для hibernate
	 * 
	 */
	public static void initFactoryAndSession()
	{
		factory = new Configuration()
								.configure(HibHundler.class.getResource("/hibernate.cfg.xml"))
								/*.configure("hibernate.cfg.xml")*/
								.addAnnotatedClass(Person.class)
								.addAnnotatedClass(Client.class)
								.addAnnotatedClass(Employee.class)
								.addAnnotatedClass(ServiceType.class)
								.addAnnotatedClass(Service.class)
								.addAnnotatedClass(Salon.class)
								.buildSessionFactory();
		session = factory.getCurrentSession();
		session.beginTransaction();
	}

	/**
	 * Закрыть factory и session для hibernate
	 * 
	 */
	public static void closeFactoryAndSession()
	{
		session.getTransaction().commit();
		factory.close();
	}
}