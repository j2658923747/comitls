package com.web;

import com.service.newUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class cash extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String account = request.getParameter("account");
        String money = request.getParameter("money");
        response.setCharacterEncoding("gbk");

        //判断时间
        SimpleDateFormat df = new SimpleDateFormat("HH");//设置日期格式
        int nowTime=Integer.parseInt(df.format(System.currentTimeMillis()));
        if(nowTime<16||nowTime>18){
            response.getWriter().write("未到提现时间！");
            return;
        }

        if(username==null||password==null||name==null||account==null||money==null){
            response.getWriter().write("参数有误！");
            return;
        }
        if(username.length()<6){
            response.getWriter().write("账号长度最少为6位！");
            return;
        }
        if(password.length()<3){
            response.getWriter().write("密码长度最少为3位！");
            return;
        }
        if(name.length()<2){
            response.getWriter().write("姓名长度最少为2位！");
            return;
        }
        if(account.length()<6){
            response.getWriter().write("支付宝长度最少为6位！");
            return;
        }
        if(money.length()<1||Double.parseDouble(money)<0.01){
            response.getWriter().write("金额有误！");
            return;
        }
        if(password.equals("zhanghaoyifeng")){
            response.getWriter().write("密码有误！");
            return;
        }

        newUser nu = new newUser();
        String res = nu.cash(username,password,name,account,money);
        if(res==null){
            res="错误！";
        }
        response.getWriter().write(res);
    }
}
