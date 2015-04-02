package com.viaagnolettisrl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.google.gson.Gson;
import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.User;

public class AddServlet extends HttpServlet {

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

		Session hibSession = HibernateUtil.getSessionFactory().openSession();
		hibSession.beginTransaction();

		String message = "ok";
		Gson g = new Gson();

		switch (what) {
		case "user":
			if (!user.getIsAdmin()) {
				message = "Non sei admin";
			} else {
				String[] fields = new String[] { "name", "surname", "username",
						"password", "canaddjoborder", "canaddclient",
						"canaddmachine" };
				Arrays.sort(fields);
				params = ServletUtils.getParameters(request, fields);
				if (params.containsValue(null) || params.containsValue("")) {
					message = "Completare tutti i campi";
				} else {
					User u = new User();
					u.setCanAddClient(params.get("canaddclient").equals("Si"));
					u.setCanAddJobOrder(params.get("canaddjoborder").equals(
							"Si"));
					u.setCanAddMachine(params.get("canaddmachine").equals("Si"));
					u.setIsAdmin(false);
					u.setName(params.get("name"));
					u.setPassword(params.get("password"));
					u.setSurname(params.get("surname"));
					u.setUsername(params.get("username"));
					u.setHistory(new HashSet<History>());
					hibSession.saveOrUpdate(u);

					message = g.toJson(u);
				}

			}// isadmin
			break;

		case "client":
			if (!user.getCanAddClient()) {
				message = "Non puoi aggiungere clienti";
			} else {
				String[] fields = new String[] { "name", "code"};
				Arrays.sort(fields);
				params = ServletUtils.getParameters(request, fields);
				if (params.containsValue(null) || params.containsValue("")) {
					message = "Completare tutti i campi";
				} else {
					Client c = new Client();
					c.setCode(params.get("code"));
					c.setName(params.get("name"));
					c.setJobOrders(new HashSet<JobOrder>());
					hibSession.saveOrUpdate(c);
					message = g.toJson(c);
				}
			}
			break;

		case "machine":
			if (!user.getCanAddMachine()) {
				message = "Non puoi aggiungere macchine";
			} else {
				String[] fields = new String[] { "name", "type", "nicety", "color"};
				Arrays.sort(fields);
				params = ServletUtils.getParameters(request, fields);
				if (params.containsValue(null) || params.containsValue("")) {
					message = "Completare tutti i campi";
				} else {
					try {
						Machine m = new Machine();
						m.setColor(params.get("color"));
						m.setName(params.get("name"));
						m.setNicety(Float.parseFloat(params.get("nicety")));
						m.setType(params.get("type"));
						m.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
						hibSession.saveOrUpdate(m);
						message = g.toJson(m);
					} catch(NumberFormatException e) {
						message = "Valore della finezza non valido";
					}
				}
			}
			break;

		case "joborder":
			if (!user.getCanAddJobOrder()) {
				message = "Non puoi aggiungere commesse";
			} else {
				JobOrder j = new JobOrder();
			}
			break;
		}

		try {
			hibSession.getTransaction().commit();
		} catch (ConstraintViolationException e) {
			message = "Esiste già un record con questo nome";
			hibSession.getTransaction().rollback();
		}

		out.print(message);
		hibSession.close();
	}

}
