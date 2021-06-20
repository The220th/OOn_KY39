import java.lang.*;
import java.util.*;
import java.io.*;

import lab.*;
import labgui.*;

import org.apache.log4j.Logger;

/**
 * Класс, который всё запускает
 * 
 */
public class KY39
{
	private static final Logger log = Logger.getLogger(KY39.class);

	/**
	 * Запуск
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		//> mvn compile exec:java -Dexec.mainClass="KY39"
		//> mvn test
		//> mvn javadoc:aggregate-jar
		log.info("Starting");
		SalonGUI.start();
	}
}
