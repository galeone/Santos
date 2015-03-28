package com.viaagnolettisrl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.User;

public class DeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 7437071575710203911L;
	public static final String USER = "user",
			LOGIN_NEXT = "/pages/home.jsp",
			LOGIN_FORM = "/pages/login.jsp";

	@Override
	public void init() throws ServletException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(true);
		ServletOutputStream out = response.getOutputStream();
		User user = (User) session.getAttribute(USER);

		if (user == null || !user.getIsAdmin()) { // not logged in || ! is admin
			response.sendRedirect(request.getContextPath() + LOGIN_NEXT);
			return;
		}
		
		Map<String, String> params = ServletUtils.getParameters(request, new String[]{"what", "id"});
		String what;
		Long id;
		if ( (what = params.get("what")) == null || params.get("id") == null) {
			out.println("error");
			return;
		}
		
		id = Long.parseLong(params.get("id"));

		Session hibSession = HibernateUtil.getSessionFactory().openSession();
		hibSession.beginTransaction();
		
		boolean ok = false;
		Object toDelete;
		
		switch (what) {
		case "user":
			toDelete = hibSession.get(User.class, id);
			if(toDelete != null) { //exists
				hibSession.delete((User)toDelete);
				ok = true;
			}
			break;
			
		case "client":
			toDelete = hibSession.get(Client.class, id);
			if(toDelete != null) { //exists
				hibSession.delete((Client)toDelete);
				ok = true;
			}
			break;
			
		case "machine":
			toDelete = hibSession.get(Machine.class, id);
			if(toDelete != null) { //exists
				hibSession.delete((Machine)toDelete);
				ok = true;
			}
			break;
			
		case "joborder":
			toDelete = hibSession.get(JobOrder.class, id);
			if(toDelete != null) { //exists
				hibSession.delete((JobOrder)toDelete);
				ok = true;
			}
			break;
		}
		if(ok) {
			hibSession.getTransaction().commit();
			out.print("ok");
		} else {
			hibSession.getTransaction().rollback();
			out.print("error, invalid ID");
		}
		
		hibSession.close();
	}

}
