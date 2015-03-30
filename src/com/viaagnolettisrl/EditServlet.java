package com.viaagnolettisrl;

import java.io.IOException;
import java.util.Arrays;
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

public class EditServlet extends HttpServlet {

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
				new String[] { "what", "id" });
		String what;
		Long id;
		if ((what = params.get("what")) == null || params.get("id") == null) {
			out.println("message, invalid parameters");
			return;
		}

		id = Long.parseLong(params.get("id"));

		Session hibSession = HibernateUtil.getSessionFactory().openSession();
		hibSession.beginTransaction();

		String message = "ok";
		Object toEdit;

		switch (what) {
		case "user":
			String[] fields = new String[] { "id", "name", "surname",
					"username", "password", "canaddjoborder", "canaddclient",
					"canaddmachine" };
			Arrays.sort(fields);

			if (!user.getIsAdmin()) {
				message = "Non sei admin";
			} else {
				toEdit = (User) hibSession.get(User.class, id);

				User u = new User();

				if (toEdit != null) { // edit
					u = (User) toEdit;
					params = ServletUtils.getParameters(request, new String[] {
							"columnName", "value" });
					String field = params.get("columnName");
					if (params.containsValue(null) || params.containsValue("")) {
						message = "Richiesta di edit errata";
					} else if (Arrays.binarySearch(fields, field) == -1) {
						message = "Nome colonna non valido";
					}

					String value = params.get("value");
					boolean result = false; //checkbox
					if (message.equals("ok")) {
						switch (field) {
						case "id":
							u.setId(Long.parseLong(value));
							break;
						case "name":
							u.setName(value);
							break;
						case "surname":
							u.setSurname(value);
							break;
						case "username":
							u.setUsername(value);
							break;
						case "password":
							u.setPassword(value);
							break;
						case "canaddjoborder":
							result = value.equals("Si");
							message = Boolean.toString(result);
							u.setCanAddJobOrder(result);
							break;
						case "canaddmachine":
							result = value.equals("Si");
							message = Boolean.toString(result);
							u.setCanAddMachine(result);
							break;
						case "canaddclient":
							result = value.equals("Si");
							message = Boolean.toString(result);
							u.setCanAddClient(result);
							break;

						default:
							message = "Campo non riconosciuto";
							break;
						}// switch
					}
				} else { // add
					params = ServletUtils.getParameters(request, fields);
					if (params.containsValue(null) || params.containsValue("")) {
						message = "Completare tutti i campi";
					} else {
						u.setCanAddClient(params.get("canaddclient").equals(
								"Si"));
						u.setCanAddJobOrder(params.get("canaddjoborder")
								.equals("Si"));
						u.setCanAddMachine(params.get("canaddmachine").equals(
								"Si"));
						u.setId(Long.parseLong(params.get("id")));
						u.setIsAdmin(false);
						u.setName(params.get("name"));
						u.setPassword(params.get("password"));
						u.setSurname(params.get("surname"));
						u.setUsername(params.get("username"));
					}
				}

				if (message.equals("ok")) {
					hibSession.saveOrUpdate(u);
				}
			}// isadmin
			break;

		case "client":
			if (!user.getCanAddClient()) {
				message = "Non puoi aggiungere clienti";
			} else {
				toEdit = hibSession.get(Client.class, id);
				if (toEdit != null) { // edit
					hibSession.saveOrUpdate((Client) toEdit);
				} else { // create

				}
			}
			break;

		case "machine":
			if (!user.getCanAddMachine()) {
				message = "Non puoi aggiungere macchine";
			} else {
				toEdit = hibSession.get(Machine.class, id);
				if (toEdit != null) { // edit
					hibSession.saveOrUpdate((Machine) toEdit);
				} else { // create

				}
			}
			break;

		case "joborder":
			if (!user.getCanAddJobOrder()) {
				message = "Non puoi aggiungere commesse";
			} else {
				toEdit = hibSession.get(JobOrder.class, id);
				if (toEdit != null) { // edit
					hibSession.saveOrUpdate((JobOrder) toEdit);
				} else { // create

				}
			}
			break;
		}
		if (message.equals("ok")) {
			hibSession.getTransaction().commit();
		} else {
			hibSession.getTransaction().rollback();
		}
		out.print(message);

		hibSession.close();
	}

}
