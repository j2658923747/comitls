package com.web;

import com.service.newUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class lookMoney extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        response.setCharacterEncoding("gbk");
        if(username==null){
            response.getWriter().write("参数有误！");
            return;
        }
        if(username.length()<8){
            response.getWriter().write("账号长度最少为8位！");
            return;
        }

        newUser nu = new newUser();
        String res = nu.look(username);
        if(res==null){
            res="0.00";
        }
        response.getWriter().write(res);
    }
}
