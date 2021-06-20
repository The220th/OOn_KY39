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
 * Сотрудник салона красоты
 * 
 */
@Entity
@Table(name="EMPLOYEE")
public class Employee extends Person
{
	@Column(name="ACTIVE")
	private boolean ACTIVE;

	//CommentLink ste1
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "buffE", cascade = CascadeType.ALL)
	//private List<ServiceType> permittedServices;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable (name="EMPLOYEE_SERVICETYPE",
				joinColumns=@JoinColumn (name="M2M_EMPLOYEE_ID"),
				inverseJoinColumns=@JoinColumn(name="M2M_SERVICETYPE_ID"))
	private Set<ServiceType> permittedServices;

	@OneToOne (/*optional=false, */mappedBy="whoDo")
	private Service buffService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SALON_ID")
	private Salon salon;

	public Employee(){init4Const();}

	public Employee(String firstName, String secondName, boolean gender, String passport, Date birthday, Set<ServiceType> permittedServices, Salon salon)
	{
		super(firstName, secondName, gender, passport, birthday);
		init4Const();
		this.ACTIVE = true;

		if(permittedServices != null)
			for(ServiceType st : permittedServices)
				this.addService(st);

		this.salon = salon;
	}

	private void init4Const()
	{
		permittedServices = new HashSet<ServiceType>();
	}

	/**
	 * Проверить активен ли сотрудник
	 * 
	 * @return true=активный, false=неактивный
	 */
	public boolean isActive()
	{
		return this.ACTIVE;
	}

	/**
	 * Установить активность сотрудника
	 * 
	 * @param a true=активен, false=неактивен
	 */
	public void active(boolean a)
	{
		this.ACTIVE = a;
	}

	/**
	* Показывает в салоне услуги, которые сотрудник может оказывать
	*/
	public synchronized Set<ServiceType> getMasteredServices()
	{
		return new HashSet<ServiceType>(permittedServices);
	}

	/**
	* Теперь, если сотрудник компетентен в оказании услуги what, добавьте её к списку услуг, которые может оказывать сотрудник 
	*/
	public synchronized void addService(ServiceType what)
	{
		//need to link with ServiceType.addServiceTypeMaster(...)
		//6JlArO 3DECb PEKYPCu9 HE nPEDEJl!
		if(!permittedServices.contains(what))
		{
			permittedServices.add(what);
			what.addServiceTypeMaster(this);
		}
	}

	/**
	* Теперь, если сотрудник компетентен в оказании услуги what, добавьте её к списку услуг, которые может оказывать сотрудник 
	*/
	public synchronized void addService(Set<ServiceType> sts, int[] which)
	{
		int i, j, target;
		for(i = 0; i < which.length; ++i)
		{
			target = which[i];
			j = 1;
			for(ServiceType _st : sts)
			{
				if(j == target)
				{
					this.addService(_st);
					break;
				}
				++j;
			}
		}
	}

	/**
	* Запрещает сотруднику оказывать услугу what
	*/
	public synchronized void forbidService(ServiceType what)
	{
		//need to link with ServiceType.removeServiceTypeMaster(...)
		if(/*=(*/ permittedServices.contains(what) /*)=*/)
		{
			permittedServices.remove(what);
			what.removeServiceTypeMaster(this);
		}

	}

	/**
	 * Запретить сотруднику оказывать любые сервисы
	 * 
	 */
	public synchronized void forbidAllServices()
	{
		permittedServices.clear();
	}

    public String toString()
    {
    	StringBuilder s = new StringBuilder();
		synchronized(this)
		{
			for(ServiceType st : this.permittedServices)
				s.append(st.getID() + " ");
		}
        return "Employee " + this.getID() + ": " + this.getName() + (ACTIVE==true?", ":", not") + "active" + ". Can conduct services: { " + s + "}";
    }

    @Override
    public boolean equals(Object otherObj) 
	{
		if(super.equals(otherObj))
		{
			synchronized(this)
			{
				Employee other = (Employee)otherObj;
				return this.ACTIVE == other.ACTIVE
				&& this.permittedServices.equals(other.permittedServices)
				&& this.salon.equals(other.salon);
			}
		}
		return false;
    } 
}
