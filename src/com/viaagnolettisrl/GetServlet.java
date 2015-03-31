package com.viaagnolettisrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		ServletOutputStream out = response.getOutputStream();
		User user = (User) session.getAttribute(LoginServlet.USER);

		if (user == null) { // not logged in
			out.print("login");
			return;
		}

		Map<String, String> params = ServletUtils.getParameters(request,
				new String[] { "what" });
		String what;
		if ((what = params.get("what")) == null) {
			out.println("error, invalid parameters");
			return;
		}

		String message = "ok";
		Map<String, List> m = new HashMap<String, List>();
		Gson gson = new Gson();
		
		switch (what) {
		case "users":
			if (!user.getIsAdmin()) {
				message = "Non sei admin";
			} else {
				m.put("data", GetList.Users());
				message = gson.toJson(m);
			}// isadmin
			break;

		case "client":
			if (!user.getCanAddClient()) {
				message = "Non puoi aggiungere clienti";
			} else {
			}
			break;

		case "machine":
			if (!user.getCanAddMachine()) {
				message = "Non puoi aggiungere macchine";
			} else {
			}
			break;

		case "joborder":
			if (!user.getCanAddJobOrder()) {
				message = "Non puoi aggiungere commesse";
			} else {
			}
			break;
		}
		
		out.print(message);
	}

}
