package Codigo; 

import java.lang.reflect.*;
import Codigo.Erro; 

public class ALDAL {

    private static String getTableName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    public static void geraTabela(Object obj) { }

    public static void set(Object obj)
    {
        Field[] f  = obj.getClass().getDeclaredFields();
        String nomeTabela = getTableName(obj.getClass());
        
        String sqlColunas = " (";
        String sqlValores = ") values (";
        Method mtd;
        
        boolean primeiroItem = true; 

        for(int i=0; i<f.length; ++i)
        {
            String nomeCampo = f[i].getName();
            if (nomeCampo.equals("id")) continue; 
            
            try
            {
                String aux = "get" + nomeCampo.substring(0,1).toUpperCase() + nomeCampo.substring(1);
                mtd = obj.getClass().getMethod(aux);
                Object value = mtd.invoke(obj);
                
                if (primeiroItem) {
                    primeiroItem = false;
                } else {
                    sqlColunas += ", ";
                    sqlValores += ", ";
                }
                
                sqlColunas += nomeCampo.toLowerCase();
                
                if (nomeCampo.equalsIgnoreCase("anoedicao")) {
                    try {
                        Integer.parseInt(value.toString()); 
                        sqlValores += value.toString(); 
                    } catch (NumberFormatException e) {
                        sqlValores += "NULL"; 
                    }
                }
                else if (f[i].getType().getSimpleName().equals("String")) {
                    if (value == null) {
                        sqlValores += "NULL";
                    } else {
                        String valorTratado = value.toString().replace("'", "''");
                        sqlValores += "'" + valorTratado + "'"; 
                    }
                }
                else {
                    sqlValores += (value == null) ? "NULL" : value.toString();
                }
            }
            catch(Exception e) {
                System.err.println("ALDAL.set: Erro no getter " + nomeCampo + ". Erro: " + e.getMessage());
                Erro.setErro("Erro de Reflexão: " + e.getMessage());
                Erro.setErro(true);
                return; 
            }
        }
        
        String sqlFinal = "Insert Into " + nomeTabela + sqlColunas + sqlValores + ")";
        
        System.out.println("ALDAL.set SQL: " + sqlFinal);
        AFDAL.conecta("Livros.mdb");
        if (Erro.getErro()) return; 
        AFDAL.executeSQL(sqlFinal);
        AFDAL.desconecta();
    }

    public static void delete(Object obj)
    {
        Field[] f  = obj.getClass().getDeclaredFields();
        String nomeTabela = getTableName(obj.getClass());
        String sql = "Delete from " + nomeTabela + " where ";
        Method mtd;
        String aux1, aux2;
        boolean flag = false;
            
        for (int i =0; i < f.length; ++i)
        {
            String nomeCampo = f[i].getName();
            try
            {
                aux1 = "get" + nomeCampo.substring(0,1).toUpperCase() + nomeCampo.substring(1);  
                mtd = obj.getClass().getMethod(aux1);
                Object value = mtd.invoke(obj);
                
                if (value != null && !value.toString().isEmpty())
                {
                    aux2 = value.toString();
                    if (flag) sql += " and "; else flag = true;
                    sql += nomeCampo.toLowerCase() + " = "; 
                    if (f[i].getType().getSimpleName().equals("String"))  
                        sql += "'" + aux2.replace("'", "''") + "'";
                    else
                        sql += aux2;
                }
            }
            catch(Exception e) {} 
        }
        
        System.out.println("ALDAL.delete SQL: " + sql);
        AFDAL.conecta("Livros.mdb");
        if (Erro.getErro()) return;
        AFDAL.executeSQL(sql);
        AFDAL.desconecta();
    }

    public static void get(Object obj)
    {
        Field[] f  = obj.getClass().getDeclaredFields();
        String nomeTabela = getTableName(obj.getClass());
        String sql = "Select * from " + nomeTabela + " where ";
        Method mtd;
        String aux1, aux2;
        boolean flag = false;
            
        for (int i =0; i < f.length; ++i)
        {
            String nomeCampo = f[i].getName();
            try
            {
                aux1 = "get" + nomeCampo.substring(0,1).toUpperCase() + nomeCampo.substring(1);  
                mtd = obj.getClass().getMethod(aux1);
                Object value = mtd.invoke(obj);
                
                if (value != null && !value.toString().isEmpty())
                {
                    aux2 = value.toString();
                    if (flag) sql += " and "; else flag = true;
                    sql += nomeCampo.toLowerCase() + " = "; 
                    if (f[i].getType().getSimpleName().equals("String"))  
                        sql += "'" + aux2.replace("'", "''") + "'";
                    else
                        sql += aux2;
                }
            }
            catch(Exception e) {} 
        }
        
        System.out.println("ALDAL.get SQL: " + sql);
        AFDAL.conecta("Livros.mdb");
        if (Erro.getErro()) return;
        AFDAL.executeSelect(sql, obj);
        AFDAL.desconecta();
    }

    public static void update(Object dados, Object chaves)
    {
        Field[] f  = dados.getClass().getDeclaredFields();
        String nomeTabela = getTableName(dados.getClass());
        String sql = "Update " + nomeTabela + " set ";
        Method mtd;
        String aux1, aux2;
        boolean flag = false;

        for (int i =0; i < f.length; ++i)
        {
            String nomeCampo = f[i].getName();
            if (nomeCampo.equals("id")) continue; 
            try
            {
                aux1 = "get" + nomeCampo.substring(0,1).toUpperCase() + nomeCampo.substring(1);  
                mtd = dados.getClass().getMethod(aux1);
                Object value = mtd.invoke(dados);
                
                if (value != null && !value.toString().isEmpty())
                {
                    if (flag) sql += ", "; else flag = true;
                    sql += nomeCampo.toLowerCase() + " = ";

                    if (nomeCampo.equalsIgnoreCase("anoedicao")) {
                        try {
                            Integer.parseInt(value.toString());
                            sql += value.toString(); 
                        } catch (NumberFormatException e) {
                            sql += "NULL";
                        }
                    }
                    else if (f[i].getType().getSimpleName().equals("String")) {
                        sql += "'" + value.toString().replace("'", "''") + "'"; 
                    }
                    else {
                        sql += value.toString();
                    }
                }
            }
            catch(Exception e) {}
        }

        sql += " where ";
        f  = chaves.getClass().getDeclaredFields();
        flag = false;
        
        // Constrói a parte WHERE
        for (int i =0; i < f.length; ++i)
        {
            String nomeCampo = f[i].getName();
            try
            {
                aux1 = "get" + nomeCampo.substring(0,1).toUpperCase() + nomeCampo.substring(1);  
                mtd = chaves.getClass().getMethod(aux1);
                Object value = mtd.invoke(chaves);
                
                if (value != null && !value.toString().isEmpty())
                {
                    aux2 = value.toString();
                    if (flag) sql += " and "; else flag = true;
                    // CORREÇÃO: Força o nome da coluna para minúsculas
                    sql += nomeCampo.toLowerCase() + " = ";
                    if (f[i].getType().getSimpleName().equals("String"))  
                        sql += "'" + aux2.replace("'", "''") + "'"; 
                    else
                        sql += aux2;
                }
            }
            catch(Exception e) {}
        }
        
        System.out.println("ALDAL.update SQL: " + sql);
        AFDAL.conecta("Livros.mdb");
        if (Erro.getErro()) return;
        AFDAL.executeSQL(sql);
        AFDAL.desconecta();
    }
}