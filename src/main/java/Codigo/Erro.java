/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Codigo;

/**
 *
 * @author MauricioAsenjo
 */
public class Erro {
private static boolean erro;
private static String mens;

public static void setErro(boolean _erro)
{
erro=_erro;
}
public static void setErro(String _mens)
{
erro=true;
mens=_mens;
}
public static boolean getErro() {return erro;}
public static String getMens() {return mens;}
    
}
