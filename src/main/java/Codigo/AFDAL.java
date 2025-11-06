package Codigo; 

import java.sql.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import Codigo.Erro; 


public class AFDAL {
    private static Connection con;

    public static void conecta(String _bd)
    {
        Erro.setErro(false); 
        try {
            Class.forName("org.postgresql.Driver"); 
            String jdbcURL = "jdbc:postgresql://localhost:5432/Livros";
            String usuario = "adm";
            String senha = "adm";
            con = DriverManager.getConnection(jdbcURL, usuario, senha);
            
            con.setAutoCommit(false); 
            
            System.out.println("AFDAL: Conexão (PostgreSQL) estabelecida! AutoCommit=OFF");
            
        } catch (Exception e) {
            System.err.println("--- ERRO NA CONEXÃO (AFDAL) ---");
            e.printStackTrace();
            Erro.setErro("Erro de Conexão: " + e.getMessage()); 
            Erro.setErro(true); 
        }
    }

    public static void desconecta()
    {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("AFDAL: Conexão fechada.");
            }
        }
        catch (Exception e) { 
            Erro.setErro(e.getMessage()); 
            Erro.setErro(true);
        }
    }
    
    public static void executeSQL(String _sql)
    {
        Erro.setErro(false); 
        Statement st = null; 
        try 
        {
            if (con == null || con.isClosed()) {
                Erro.setErro("Conexão com o banco está fechada ou nula.");
                Erro.setErro(true);
                return;
            }
            st = con.createStatement();
            
            int rows = st.executeUpdate(_sql);
            
            if (rows == 0 && !_sql.trim().toLowerCase().startsWith("create")) {
                 Erro.setErro("Nenhum registro foi afetado. O item pode não existir.");
                 Erro.setErro(true);
                 con.rollback(); 
            } else {
                con.commit();
                System.out.println("AFDAL: COMMIT manual executado!");
            }
        }
        catch(Exception e)
        {
            Erro.setErro(e.getMessage()); 
            Erro.setErro(true);
            System.err.println("AFDAL executeSQL Erro: " + e.getMessage());
            e.printStackTrace();
            
            try {
                if (con != null) {
                    System.err.println("AFDAL: Erro detectado. Executando ROLLBACK...");
                    con.rollback();
                }
            } catch (SQLException rbEx) {
                 System.err.println("AFDAL: Falha ao executar ROLLBACK: " + rbEx.getMessage());
            }
        }
        finally {
             try { if (st != null) st.close(); } catch (Exception e) {}
        }
    }
    
    public static void executeSelect(String _sql, Object obj)
    {
        ResultSet rs;
        Erro.setErro(false); 
        try
        {
            if (con == null || con.isClosed()) {
                Erro.setErro("Conexão com o banco está fechada ou nula.");
                Erro.setErro(true);
                return;
            }
            
            PreparedStatement st = con.prepareStatement(_sql);
            rs = st.executeQuery();
            
            if (rs.next())
            {
                Field[] f  = obj.getClass().getDeclaredFields();
                Method mtd;
                String auxSetterName;
                for(int i=0; i<f.length; ++i)
                {
                    String dbColumnName = f[i].getName().toLowerCase(); 
                    auxSetterName="set"+f[i].getName().substring( 0, 1 ).toUpperCase() + f[i].getName().substring( 1 );
                    try 
                    {
                        mtd = obj.getClass().getMethod(auxSetterName, new Class[] {f[i].getType()});
                        Object valueToSet = null;
                        String fieldType = f[i].getType().getSimpleName();
                        
                        if (fieldType.equals("String")) {
                            if(dbColumnName.equals("anoedicao")) {
                                valueToSet = String.valueOf(rs.getInt(dbColumnName));
                            } else {
                                valueToSet = rs.getString(dbColumnName);
                            }
                        } else if (fieldType.equals("int")) {
                            valueToSet = rs.getInt(dbColumnName);
                        } else {
                            valueToSet = rs.getObject(dbColumnName);
                        }
                        mtd.invoke(obj, new Object[] { valueToSet });
                    }
                    catch(Exception e){
                        System.err.println("Erro ao invocar método: " + auxSetterName + " - " + e.getMessage());
                    }
                }
            }
            else
            {
                Erro.setErro(obj.getClass().getSimpleName() + " não localizado."); 
                Erro.setErro(true);
                return; 
            }
            st.close();
        }
        catch (Exception e)
        {
            Erro.setErro(e.getMessage());
            Erro.setErro(true);
        }
    }
}