<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Codigo.Livro, Codigo.ALDAL, Codigo.Erro"%>
<%@page import="java.io.PrintWriter, java.io.StringWriter"%>

<%
    String cssClass = "";
    String tituloPagina = "";
    String mensagemH1 = "";
    String mensagemP = "";
    String stackTrace = ""; 
    boolean temErro = false;
    String tituloDeletado = ""; 

    String tituloParaConsultar = request.getParameter("titulo");
    tituloDeletado = tituloParaConsultar; 

    Codigo.Livro umLivro = new Codigo.Livro();
    umLivro.setTitulo(tituloParaConsultar);
    
    try {
        Codigo.ALDAL.delete(umLivro);
        
        if (Codigo.Erro.getErro()) {
            temErro = true;
            mensagemH1 = "Ocorreu um erro ao deletar:";
            mensagemP = Codigo.Erro.getMens(); 
        } else {
            temErro = false;
            mensagemH1 = "Livro deletado com SUCESSO";
            mensagemP = "O livro '" + tituloDeletado + "' foi removido do banco de dados.";
        }
        
    } catch (Exception e) {
        temErro = true;
        mensagemH1 = "Ocorreu um ERRO CRÍTICO:";
        mensagemP = e.getMessage();
        
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        stackTrace = sw.toString();
    }
    
    if (temErro) {
        tituloPagina = "Erro ao Deletar";
        cssClass = "error";
    } else {
        tituloPagina = "Sucesso!";
        cssClass = "success";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%= tituloPagina %></title>
    
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background-color: #f0f2f5;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .message-container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            width: 450px;
            text-align: center;
            /* Borda no topo para dar cor */
            border-top: 5px solid #007bff; 
        }

        h1 {
            margin-top: 0;
            font-size: 1.5em;
        }

        .message-container.error {
            border-top-color: #b91c1c; 
        }
        .message-container.error h1 {
            color: #b91c1c;
        }

        .message-container.success {
            border-top-color: #166534; 
        }
        .message-container.success h1 {
            color: #166534;
        }

        .message-container p {
            color: #333;
            font-size: 1.1em;
            background-color: #f8f9fa;
            padding: 10px;
            border-radius: 4px;
            border: 1px solid #ddd;
            word-wrap: break-word;
        }

        .btn-voltar {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #007bff;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }
        
        .btn-voltar:hover {
            background-color: #0056b3;
        }

        pre {
            text-align: left; 
            background: #f1f1f1; 
            padding: 10px; 
            border-radius: 5px; 
            max-height: 200px; 
            overflow-y: auto;
            font-size: 0.8em;
            word-wrap: normal;
            white-space: pre-wrap;
        }
    </style>

</head>
<body>
    
    <div class="message-container <%= cssClass %>">
    
        <h1><%= mensagemH1 %></h1>
        
        <% if (mensagemP != null && !mensagemP.isEmpty()) { %>
            <p><%= mensagemP %></p>
        <% } %>

        <% if (stackTrace != null && !stackTrace.isEmpty()) { %>
            <pre><%= stackTrace %></pre>
        <% } %>

        <a href="index.html" class="btn-voltar">Voltar</a>
        
    </div>
    
</body>
</html>