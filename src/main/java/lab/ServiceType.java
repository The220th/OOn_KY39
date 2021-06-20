package lab;

import lab.*;

import java.lang.*;
import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Тип сервиса (специализация)
 * 
 */
@Entity
@Table(name="SERVICETYPE")
public class ServiceType
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SERVICETYPEID")
	private int serviceTypeID;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="CURRENTPRICE")
	private double currentPrice;

	/*Процент сотрудникам за оказание услуги. Число от 0 до 100.*/
	@Column(name="PERCENTAGETOEMPLOYEE")
	private float percentageToEmployee;

	@Column(name="RELEVANT")
	private boolean RELEVANT;

	//CommentLink ste1
    //@ManyToOne(/*targetEntity = Employee.class, */optional=false, fetch = FetchType.LAZY)
    //@JoinColumn(name = "EMPLOYEE_ID")
	//private Employee buffE;

	//Thanks, hiber_nuts, for making me write extra code. YPOD
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="EMPLOYEE_SERVICETYPE",
		joinColumns=@JoinColumn(name="M2M_SERVICETYPE_ID"), 
		inverseJoinColumns=@JoinColumn(name="M2M_EMPLOYEE_ID"))
	private Set<Employee> whoMastered;

	@OneToOne (/*optional=false, */mappedBy="serviceType")
	private Service buffService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALON_ID")
	private Salon salon;

	public ServiceType(){init4Const();}

	public ServiceType(String description, double price, float employeePercentage, Salon forWhichSalon)
	{
		init4Const();
		this.description = description;
		//this.currentPrice = price;
		this.setPrice(currentPrice);
		this.RELEVANT = true;
		//this.percentageToEmployee = employeePercentage;
		this.setPercent(employeePercentage);
		this.salon = forWhichSalon;
	}

	private void init4Const()
	{
		whoMastered = new HashSet<Employee>();
	}

	/**
	 * Получить описание типа сервиса
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * Получить список сотрудников, которые могут оказывать данный сервис
	 * 
	 * @return список сотрудников, которые могут оказывать данный сервис
	 */
	public synchronized Set<Employee> getWhoMasteredThisServiceTypes()
	{
		return new HashSet<Employee>(whoMastered);
	}

	/**
	 * Поменять описание типа сервиса
	 * 
	 * @param desc новое описание
	 */
	public void changeDescription(String desc)
	{
		this.description = desc;
	}

	/**
	 * Получить ID сервиса
	 * 
	 * @return ID сервиса
	 */
	public int getID()
	{
		return this.serviceTypeID;
	}

	/**
	 * Получить текущую стоимость услуги
	 * 
	 * @return текущая стоимость услуги
	 */
	public double getCurrentPrice()
	{
		return this.currentPrice;
	}

	/**
	 * Установить цену типа сервиса
	 * 
	 * @param price новая цена
	 */
	public void setPrice(double price)
	{
		if(price < 0)
			throw new NumberFormatException("price must be >= 0. You inputted: \"" + price + "\".");
		this.currentPrice = price;
	}

	/**
	 * Проверить, актуален ли данный тип сервиса
	 * 
	 * @return true=актуален, false=неактуален
	 */
	public boolean isRelevant()
	{
		return this.RELEVANT;
	}

	/**
	 * Установить актуальность сервиса
	 * 
	 * @param rel true=актуален, false=неактуален
	 */
	public void relevant(boolean rel)
	{
		this.RELEVANT = rel;
	}

	/**
	* Посмотреть процент, который начисляется сотруднику, от суммы стоимости услуги
	* Число от 0 до 100
	*/
	public float getPercent()
	{
		return this.percentageToEmployee;
	}

	/**
	 * Установить процент, который начисляется сотруднику, от суммы стоимости услуги
	 * Число от 0 до 100, иначе исключение
	 * 
	 * @param employeePercent процент, который начисляется сотруднику, от суммы стоимости услуги
	 * @throws NumberFormatException
	 */
	public void setPercent(float employeePercent)
	{
		if(employeePercent > 100 || employeePercent < 0)
			throw new NumberFormatException("employeePercent must be 0 <= employeePercent <= 100. You inputted \"" + employeePercent + "\".");
		this.percentageToEmployee = employeePercent;
	}

	/**
	 * Добавить сотрудника, который может оказывать данный сервис
	 * 
	 * @param who сотрудник, который может оказывать данный сервис
	 */
	public void addServiceTypeMaster(Employee who)
	{
		//need to link with Employee.addService(...)
		if(!whoMastered.contains(who))
		{
			whoMastered.add(who);
			who.addService(this);
		}
	}

	/**
	 * Убрать сотрудника, который раньше мог оказывать данный сервис
	 * 
	 * @param who сотрудник, который раньше мог оказывать данный сервис
	 */
	public void removeServiceTypeMaster(Employee who)
	{
		//KAK Tbl DOKATuJlC9 DO TAKOrO?!
		//need to link with Employee.forbidService(...)
		if(/*=(*/ whoMastered.contains(who) /*)=*/)
		{
			whoMastered.remove(who);
			who.forbidService(this);
		}
	}

	public String toString()
	{
		StringBuilder whoMastered_strOut = new StringBuilder();
		for(Employee e : whoMastered)
			whoMastered_strOut.append(e.getID() + " ");
		return "Service " + this.getID() + ": " + this.getDescription() + ". It costs " + currentPrice + ". This service can be provided by employees: " + whoMastered_strOut.toString() + ". ";
	}

    @Override
    public boolean equals(Object otherObj) 
	{
		if (otherObj == this) return true; 
		if (otherObj == null) return false;
		if( this.getClass() != otherObj.getClass() ) return false;
		ServiceType other = (ServiceType)otherObj;
		return this.currentPrice == other.currentPrice
		&& this.percentageToEmployee == other.percentageToEmployee
		&& this.RELEVANT == other.RELEVANT
		&& this.description.equals(other.description)
		&& this.whoMastered.equals(other.whoMastered)
		&& this.salon.equals(other.salon);
    }
}