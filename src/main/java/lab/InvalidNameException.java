package lab;

import lab.*;

import java.lang.*;
import java.util.*;

/**
 * Класс исключения при неправильном вводе имени
 * 
 */
public class InvalidNameException extends IllegalArgumentException
{
    String kaka;

    public InvalidNameException(String message, String InvalidSymbol)
    {
        super(message);
        this.kaka = "\"" + InvalidSymbol + "\"";
    }

    /**
     * Получить символ, который вызвал иключение
     * 
     * @return символ, который вызвал иключение
     */
    public String getInvalidSymbol()
    {
        return this.kaka;
    }
}