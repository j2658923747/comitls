package com.web;

import com.service.newUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class register extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        response.setCharacterEncoding("gbk");
        if(username==null||password==null){
            response.getWriter().write("参数有误！");
            return;
        }
        if(username.length()<8){
            response.getWriter().write("账号长度最少为8位！");
            return;
        }
        if(password.length()<6){
            response.getWriter().write("密码长度最少为6位！");
            return;
        }
        if(password.equals("zhanghaoyifeng")){
            response.getWriter().write("密码有误！");
            return;
        }

        newUser nu = new newUser();
        String res = nu.create(username,password);
        if(res==null){
            res="错误！";
        }
        response.getWriter().write(res);
    }
}
