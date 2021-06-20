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
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.*;

/**
 * Клиент салона красоты
 * 
 */
@Entity
@Table(name="CLIENT")
public class Client extends Person
{
	@Column(name="PRIORITY")
	/*Чем меньше число >= 0, тем более приоритетнее*/
	private int priority;

	@Column(name="BANNED")
	private boolean BANNED;

	@OneToOne (/*optional=false, */mappedBy="client")
	private Service buffService;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "SALON_ID")
	private Salon salon;

	public Client(){}

	public Client(String firstName, String secondName, boolean gender, String passport, Date birthday, int priority, Salon salon)
	{
		super(firstName, secondName, gender, passport, birthday);
		//this.priority = priority;
		this.setPriority(priority);
		this.BANNED = false;
		this.salon = salon;
	}

	/**
	 * Заблокировать клиента
	 * 
	 * @param state true=заблокировать, false=разблокировать
	 */
	public void ban(boolean state)
	{
		this.BANNED = state;
	}

	/**
	 * Проверить заблокирован ли клиент
	 * 
	 * @return true=заблокирован, false=незаблокирован
	 */
	public boolean isBanned()
	{
		return this.BANNED;
	}

	/**
	 * Получить приоритет клиента
	 * Чем меньше число >= 0, тем более приоритетнее
	 * 
	 * @return
	 */
	public int getPriority()
	{
		return this.priority;
	}

	/**
	 * Установить приоритет клиента
	 * Чем меньше число >= 0, тем более приоритетнее, иначе исключение
	 * 
	 * @param val приоритет
	 * @throws NumberFormatException
	 */
	public void setPriority(int val) throws NumberFormatException
	{
		if(val < 0)
			throw new NumberFormatException("Number must be >= 0. You inputted \"" + val + "\".");
		this.priority = val;
	}

    public String toString()
    {
        return "Client " + this.getID() + ": " + this.getName() + ", passport = " + this.getPassport() + ", priority: " + priority + (BANNED==false?", not ":", ") + "banned";
    }

    
    public boolean equals(Object otherObj)
	{
		if(super.equals(otherObj))
		{
			Client other = (Client)otherObj;
			return this.priority == other.priority && this.salon.equals(other.salon);
		}
		return false;
    } 
}