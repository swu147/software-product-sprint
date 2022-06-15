package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/form-handler")
public class FormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name-input");
    String email = request.getParameter("email-input");
    String message = request.getParameter("message-input");

    System.out.println("name: " + name);
    System.out.println("email: " + email);
    System.out.println("message: " + message);

    response.sendRedirect("https://swu-sps-summer22.appspot.com/#contact");
  }
}

