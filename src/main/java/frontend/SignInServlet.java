package frontend;


import interfaces.AccountService;
import main.AccountServiceDevImpl;
import main.UserProfile;
import templater.PageGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 01.08.2015.
 */
public class SignInServlet extends HttpServlet {
    private AccountServiceDevImpl accountService;

    public SignInServlet(AccountServiceDevImpl accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> pageVariables = new HashMap<>();
        UserProfile userProfile = null;
        Cookie[] cookies =  request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("sessionId"))
                userProfile = accountService.getSession(c.getValue());
        }
        if(userProfile != null)
            pageVariables.put("login_status", "User authorised");
        else pageVariables.put("login_status", "User unauthorised");

        try {
            response.getWriter().println(PageGenerator.getPage("signin.html", pageVariables));
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> pageVariables = new HashMap<>();

        String userName = request.getParameter("login");
        String password = request.getParameter("password");

        switch (accountService.signInUser(userName, password)) {
            case AccountService.BAD_CREDENTIALS:
                pageVariables.put("login_status", "Password is incorrect");
                break;
            case AccountService.USER_AUTH_OK:
                pageVariables.put("login_status", "User authorised");
                accountService.setSession(request.getSession().getId(), userName);
                Cookie cookie = new Cookie("sessionId", request.getSession().getId());
                response.addCookie(cookie);
                break;
            case AccountService.USER_NOT_FOUND:
                pageVariables.put("login_status", "User not found");
        }

        try {
            response.getWriter().println(PageGenerator.getPage("signin.html", pageVariables));
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}