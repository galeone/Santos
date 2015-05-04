package com.viaagnolettisrl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.viaagnolettisrl.hibernate.User;

public class GetServlet extends HttpServlet {
    
    private static final long serialVersionUID = 74377157203911L;
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        HttpSession session = request.getSession(true);
        ServletOutputStream out = response.getOutputStream();
        User user = (User) session.getAttribute(LoginServlet.USER);
        Gson gson = new Gson();
        
        if (user == null) { // not logged in
            out.print("login");
            return;
        }
        
        Map<String, String> params = ServletUtils.getParameters(request, new String[] { "what" });
        String what;
        if ((what = params.get("what")) == null) {
            out.println("error, invalid parameters");
            return;
        }
        
        switch(what) {
            case "nonworkingday":
                out.print(gson.toJson(GetCollection.NonWorkingDays(user.getIsAdmin())));
                break;
             default:
                 out.print("Error");
        }
    }
}
