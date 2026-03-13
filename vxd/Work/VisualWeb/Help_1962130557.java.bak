package VisualWeb;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class Help_1962130557
extends HttpServlet
{
  public String html="";


  public void init(ServletConfig conf) throws ServletException
  {
    super.init(conf);
    
    html+="<HTML><BODY>";
      
    html+="</BODY></HTML>";
      
  }

  public void doGet(HttpServletRequest req,HttpServletResponse resp)
    throws ServletException, IOException
  {
    resp.setContentType("text/html");
    PrintWriter out=resp.getWriter();
    out.println(html);
    
  }

  public void doPost(HttpServletRequest req,HttpServletResponse resp)
    throws ServletException, IOException
  {
    doGet(req,resp);
  }
}
