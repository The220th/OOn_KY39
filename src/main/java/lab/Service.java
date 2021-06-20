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
 * Сервис. Запись о том, какой сотрудник какому клиенту какую оказывал услугу, перечисленную в ServiceType, и за сколько
 * 
 */
@Entity
@Table(name="SERVICE")
public class Service
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="SERVICEID")
	private int serviceID;

	@Column(name="DATEBEGINI")
	private long dateBeginI;

	@Column(name="CASHREWARD")
	private double cashReward;

	@Column(name="THENPRICE")
	private double thenPrice;

	@Column(name="REL")
	private boolean REL;

	@OneToOne (optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="SERVICETYPE_ID")
	private ServiceType serviceType;

	@OneToOne (optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="CLIENT_ID")
	private Client client;

	@OneToOne (optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="EMPLOYEE_ID")
	private Employee whoDo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALON_ID")
	private Salon salon;

	public Service(){}

	public Service(ServiceType serviceType, Date dateBegin, Employee whoDo, double cashReward, Client client, double thenPrice, Salon forWhichSalon)
	{
		if(cashReward > thenPrice || thenPrice < 0 || cashReward < 0)
			throw new NumberFormatException("thenPrice must be more or equals then cashReward and positive: cashReward = \"" + cashReward + "\", thenPrice = \"" + thenPrice + "\".");
		this.serviceType = serviceType;
		this.dateBeginI = dateBegin.getTime();
		this.whoDo = whoDo;
		this.cashReward = cashReward;
		this.client = client;
		this.thenPrice = thenPrice;
		this.REL = true;
		this.salon = forWhichSalon;
	}

	public Service(ServiceType serviceType, Date dateBegin, Employee whoDo, Client client, Salon forWhichSalon)
	{
		this.serviceType = serviceType;
		this.dateBeginI = dateBegin.getTime();
		this.whoDo = whoDo;
		this.cashReward = serviceType.getCurrentPrice() * serviceType.getPercent() * 0.01;
		this.client = client;
		this.thenPrice = serviceType.getCurrentPrice();
		this.REL = true;
		this.salon = forWhichSalon;
	}

	/**
	 * Получить ID сервиса
	 * 
	 * @return ID сервиса
	 */
	public int getID()
	{
		return this.serviceID;
	}

	/**
	 * Получить дату начала сервиса
	 * 
	 * @return дата начала сервиса
	 */
	public Date getDateBegin()
	{
		return new Date(dateBeginI);
	}

	/**
	 * Получить сумму, которую заплатил клиент
	 * 
	 * @return сумма, которую заплатил клиент
	 */
	public double getPrice()
	{
		return this.thenPrice;
	}

	/**
	 * Получить тип сервиса
	 * 
	 * @return
	 */
	public ServiceType getServiceType()
	{
		return this.serviceType;
	}

	/**
	* Возвращает работника, который оказывал услугу
	*/
	public Employee getEmployee()
	{
		return whoDo;
	}

	/**
	* Возвращает сумму, которую получил сотрудник после оказания услуги
	*/
	public double getCashReward()
	{
		return cashReward;
	}

	/**
	* Получить клиента, которому оказывали услугу
	*/
	public Client getClient()
	{
		return client;
	}

	/**
	* Возможно эта запись создана с ошибкой или ещё какая-нибудь причина, по которой она не должна считаться
	* Если true, то будет учитываться и показываться, если false, то эта запись не в счёт
	*/
	public boolean isRELEVANT()
	{
		return this.REL;
	}

	/**
	* Возможно эта запись создана с ошибкой или ещё какая-нибудь причина, по которой она не должна считаться
	* Установите false, чтобы эта запись была не в счёт. Запись НЕ удалится из базы данных, лишь скроется
	*/
	public void setRELEVANT(boolean rel)
	{
		this.REL = rel;
	}

	public String toString()
	{
		return "Deal " + this.getID() + ": " + "The worker " + this.getEmployee().getName() + " rendered a service id=" + this.getServiceType().getID() + " to the client " + this.getClient().getName() +  " on " + this.getDateBegin() + ". The client paid " + this.getPrice();
	}

    @Override
    public boolean equals(Object otherObj) 
	{
		if (otherObj == this) return true; 
		if (otherObj == null) return false;
		if( this.getClass() != otherObj.getClass() ) return false;
		Service other = (Service)otherObj;
		return this.dateBeginI == other.dateBeginI
		&& this.cashReward == other.cashReward
		&& this.thenPrice == other.thenPrice
		&& this.REL == other.REL
		&& this.serviceType.equals(other.serviceType)
		&& this.whoDo.equals(other.whoDo)
		&& this.client.equals(other.client)
		&& this.salon.equals(other.salon);
    }
}
