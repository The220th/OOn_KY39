# Перед запуском

Нужно настроить MySQL и устранить зависимости.

## Зависимости

Для работы нужны:

- JDK 1.8

- mysql-server (также может понадобится mysql-client для настройки MySQL)

- Maven

## Настройка MySQL

Для работы приложения необходимо подключение к БД. Используется СУБД MySQL.

### Использование готовых баз данных

В файле `lab.tar.gz` находятся уже готовые записи в большом количестве. Можно использовать их. Сначала узнайте, где находится директория с БД. Для этого выполните SQL запрос:

``` sql

> SELECT @@datadir;

```

Вывод может быть таким:

``` sql

+-----------------+
| @@datadir       |
+-----------------+
| /var/lib/mysql/ |
+-----------------+
1 row in set (0.00 sec)

```

Это значит, что в директории `/var/lib/mysql/` должен быть каталог `lab` из архива `lab.tar.gz`. Теперь, например, у файла `SALON.ibd` должен быть абсолютный путь такой: `/var/lib/mysql/lab/SALON.ibd`.

### Создание таблиц заново

Если вам нужны пустые таблицы, то выполните SQL запросы:

``` sql

> CREATE DATABASE lab;

> use lab;

> CREATE TABLE `PERSON` (
  `PERSONID` int(11) NOT NULL AUTO_INCREMENT,
  `FIRSTNAME` varchar(50) NOT NULL,
  `SECONDNAME` varchar(50) NOT NULL,
  `BIRTHDAY` BIGINT NOT NULL,
  `PASSPORT` varchar(50) NOT NULL,
  `GENDER` int(11) NOT NULL,
  PRIMARY KEY (`PERSONID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

> CREATE TABLE `CLIENT` (
  `PERSONID` int(11) NOT NULL AUTO_INCREMENT,
  `FIRSTNAME` varchar(50) NOT NULL,
  `SECONDNAME` varchar(50) NOT NULL,
  `BIRTHDAY` BIGINT NOT NULL,
  `PASSPORT` varchar(50) NOT NULL,
  `GENDER` int(11) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `BANNED` int(11) NOT NULL,
  `SALON_ID` int(11),
  PRIMARY KEY (`PERSONID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

> CREATE TABLE `EMPLOYEE` (
  `PERSONID` int(11) NOT NULL AUTO_INCREMENT,
  `FIRSTNAME` varchar(50) NOT NULL,
  `SECONDNAME` varchar(50) NOT NULL,
  `BIRTHDAY` BIGINT NOT NULL,
  `PASSPORT` varchar(50) NOT NULL,
  `GENDER` int(11) NOT NULL,
  `ACTIVE` int(11) NOT NULL,
  `SALON_ID` int(11),
  PRIMARY KEY (`PERSONID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

> CREATE TABLE `SERVICETYPE` (
  `SERVICETYPEID` int(11) NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` varchar(50) NOT NULL,
  `CURRENTPRICE` double(11, 4) NOT NULL,
  `PERCENTAGETOEMPLOYEE` double(11, 4) NOT NULL,
  `RELEVANT` int(11) NOT NULL,
  `SALON_ID` int(11),
  PRIMARY KEY (`SERVICETYPEID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

> CREATE TABLE `SERVICE` (
  `SERVICEID` int(11) NOT NULL AUTO_INCREMENT,
  `DATEBEGINI` BIGINT NOT NULL,
  `CASHREWARD` double(11, 4) NOT NULL,
  `THENPRICE` double(11, 4) NOT NULL,
  `REL` int(11) NOT NULL,
  `SERVICETYPE_ID` int(11) NOT NULL,
  `CLIENT_ID` int(11) NOT NULL,
  `EMPLOYEE_ID` int(11) NOT NULL,
  `SALON_ID` int(11),
  PRIMARY KEY (`SERVICEID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

> CREATE TABLE `SALON` (
  `SALONID` int(11) NOT NULL AUTO_INCREMENT,
  `SALONNAME` varchar(50) NOT NULL,
  PRIMARY KEY (`SALONID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

> CREATE TABLE `EMPLOYEE_SERVICETYPE` (
  `M2M_EMPLOYEE_ID` int(11) NOT NULL,
  `M2M_SERVICETYPE_ID` int(11) NOT NULL,
  CONSTRAINT FK_M2M_EMPLOYEE_ID FOREIGN KEY (M2M_EMPLOYEE_ID)
      REFERENCES EMPLOYEE (PERSONID),
  CONSTRAINT FK_M2M_SERVICETYPE_ID FOREIGN KEY (M2M_SERVICETYPE_ID)
      REFERENCES SERVICETYPE (SERVICETYPEID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

```

Таблицу `PERSON` создавать не обязательно.

## Создание нового пользователя в MySQL 

Теперь нужно создать пользователя. Например, пусть это будет `LABUSER`, у которого пароль `123(P)assword`. Выполните SQL запросы:

``` sql

> CREATE USER 'LABUSER' IDENTIFIED BY '123(P)assword';

> GRANT ALL PRIVILEGES ON lab.* TO 'LABUSER';

> FLUSH PRIVILEGES;

```

После нужно поменять содержимое файла `/src/main/resources/hibernate.cfg.xml`. Найдите строки и поменяйти значения на свои:

``` xml

<property name="connection.url">jdbc:mysql://localhost:3306/lab</property>
<property name="connection.username">LABUSER</property>
<property name="connection.password">123(P)assword</property>

```

Подробнее о том как подключить MySQL к Java смотрите [тут](https://the220th.github.io/guides/coding/java-sql/).

#Запуск

Чтобы запустить приложение перейдите в корень проекта и выполните команду:

``` bash

> mvn compile exec:java -Dexec.mainClass="KY39"

```

Также можно запустить тесты, чтобы проверить, что работает правильно:

``` bash

> mvn test

```

Чтобы создать javadoc:

``` bash

> mvn javadoc:aggregate-jar

```