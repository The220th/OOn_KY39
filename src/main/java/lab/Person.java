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
//import javax.persistence.Inheritance;
//import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;


//@Entity
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
/**
 * Класс человек. От него наследуются Employee и Client
 * 
 */
@MappedSuperclass
@Table(name="PERSON")
public class Person
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PERSONID")
	private int personID;

	@Column(name="FIRSTNAME")
	private String firstName;

	@Column(name="SECONDNAME")
	private String secondName;

	@Column(name="BIRTHDAY")
	private long birthdayI;

	//private Date birthday;

	@Column(name="PASSPORT")
	private String passport;

	/*1-man, 0-woman*/
	@Column(name="GENDER")
	private int gender;

	public Person(){}

	public Person(String firstName, String secondName, boolean gender, String passport, Date birthday)
	{
		this.firstName = firstName;
		this.secondName = secondName;
		this.gender = gender==true?1:0;
		//this.birthday = birthday;
		this.birthdayI = birthday.getTime();
		this.personID = personID;
		this.passport = passport;
	}

	/**
	 * Получить ID человека
	 * 
	 * @return ID человека
	 */
	public int getID()
	{
		return personID;
	}

	/**
	 * Получить полное имя человека
	 * 
	 * @return полное имя человека
	 */
	public String getName()
	{
		return secondName + " " + firstName;
	}

	/**
	 * Получить только имя человека
	 * 
	 * @return имя человека
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Получить фамилию человека
	 * 
	 * @return фамилия человека
	 */
	public String getSecondName()
	{
		return secondName;
	}

	/**
	 * Установить имя человека
	 * 
	 * @param firstName имя
	 * @param secondName фамилия
	 */
	public void setName(String firstName, String secondName)
	{
		this.firstName = firstName;
		this.secondName = secondName;
	}

	/**
	 * Установить паспорт человека
	 * 
	 * @param passport паспорт человека
	 */
	public void setPassport(String passport)
	{
		this.passport = passport;
	}

	/**
	 * Установить пол человека
	 * 
	 * @param s true=man, false=female
	 */
	public void setGender(boolean s)
	{
		this.gender = (s==true?1:0);
	}

	/**
	 * Задать дату рождения человека
	 * 
	 * @param d дата рождения человека
	 */
	public void setBirthday(Date d)
	{
		this.birthdayI = d.getTime();
	}

	/**
	 * Получить дату рождения
	 * 
	 * @return дата рождения
	 */
	public Date getBirthday()
	{
		return new Date(birthdayI);
	}

	/**
	 * Получить паспорт человека
	 * 
	 * @return паспорт человека
	 */
	public String getPassport()
	{
		return passport;
	}

	/**
	 * Получить пол человека
	 * 
	 * @return true=man, false=female
	 */
	public boolean getGender()
	{
		return gender==1?true:false;
	}

    public String toString()
    {
        return personID + ": " + firstName + ", " + secondName + " is " + (gender==1?"male":"female") + ", passport=" + passport + ", birthday is " + new Date(birthdayI);
    }

    @Override
    public boolean equals(Object otherObj) 
	{
		if (otherObj == this) return true; 
		if (otherObj == null) return false;
		if( this.getClass() != otherObj.getClass() ) return false;
		Person other = (Person)otherObj;

		return this.birthdayI == other.birthdayI
		&& this.gender == other.gender
		&& this.firstName.equals(other.firstName) 
		&& this.secondName.equals(other.secondName)
		&& this.passport.equals(other.passport);
    } 
}
