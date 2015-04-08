package com.viaagnolettisrl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
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
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
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
			out.println("error, invalid parameters");
			return;
		}

		id = Long.parseLong(params.get("id"));

		Session hibSession = HibernateUtil.getSessionFactory().openSession();
		hibSession.beginTransaction();

		String message = "ok";
		Object toEdit;
		String[] fields = null;
		String outputResult = "";

		switch (what) {
		case "user":
			fields = new String[] { "id", "name", "surname",
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
					boolean result;
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
							outputResult = Boolean.toString(result);
							u.setCanAddJobOrder(result);
							break;
						case "canaddmachine":
							result = value.equals("Si");
							outputResult = Boolean.toString(result);
							u.setCanAddMachine(result);
							break;
						case "canaddclient":
							result = value.equals("Si");
							outputResult = Boolean.toString(result);
							u.setCanAddClient(result);
							break;

						default:
							message = "Campo non riconosciuto";
							break;
						}// switch
					}
				} else {
					message = "Utente da modificare non trovato";
				}

				if (message.equals("ok")) {
					hibSession.saveOrUpdate(u);
				}
			}// isadmin
			break;

		case "nonworkingday":
			if (!user.getIsAdmin()) {
				message = "Non sei admin";
			} else {
				toEdit = (NonWorkingDay) hibSession.get(NonWorkingDay.class, id);

				NonWorkingDay nw = new NonWorkingDay();

				if (toEdit != null) { // edit
					nw = (NonWorkingDay)toEdit;
					String dateS = request.getParameter("date");
					if (dateS == null || "".equals(dateS)) {
						message = "Data non valida (vuota)";
					} else {
						try {
							SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
							Date d = sdf.parse(dateS);
							nw.setStart(d);
							nw.setEnd(d);
							hibSession.saveOrUpdate(nw);
						} catch(ParseException e) {
							message = "formato data non valido";
						}
					}
				}
				else {
					message = "Giorno non lavorativo da modificare non trovato";
				}

				if (message.equals("ok")) {
					hibSession.saveOrUpdate(nw);
				}
			}
			break;
			
		case "client":
			if (!user.getCanAddClient()) {
				message = "Non puoi aggiungere clienti";
			} else {
				fields = new String[] { "name", "code" };
				Arrays.sort(fields);
				toEdit = (Client) hibSession.get(Client.class, id);
				Client c = new Client();
				if (toEdit != null) { // edit
					c = (Client)toEdit;
					params = ServletUtils.getParameters(request, new String[] {
							"columnName", "value" });
					String field = params.get("columnName");
					if (params.containsValue(null) || params.containsValue("")) {
						message = "Richiesta di edit errata";
					} else if (Arrays.binarySearch(fields, field) == -1) {
						message = "Nome colonna non valido";
					}

					String value = params.get("value");
					if (message.equals("ok")) {
						switch (field) {
						case "name":
							c.setName(value);
							break;
						case "code":
							c.setCode(value);
							break;
						default:
							message = "Campo non riconosciuto";
							break;
						}// switch
					}
				} else {
					message = "Cliente da modificare non trovatp";
				}
				
				if (message.equals("ok")) {
					hibSession.saveOrUpdate(c);
				}
			}
			break;

		case "machine":
			if (!user.getCanAddMachine()) {
				message = "Non puoi aggiungere macchine";
			} else {
				fields = new String[] { "name", "type", "nicety", "color" };
				Arrays.sort(fields);
				toEdit = (Machine) hibSession.get(Machine.class, id);

				Machine m = new Machine();
				if (toEdit != null) { // edit
					m = (Machine) toEdit;
					params = ServletUtils.getParameters(request, new String[] {
							"columnName", "value" });
					String field = params.get("columnName");
					if (params.containsValue(null) || params.containsValue("")) {
						message = "Richiesta di edit errata";
					} else if (Arrays.binarySearch(fields, field) == -1) {
						message = "Nome colonna non valido";
					}

					String value = params.get("value");
					if (message.equals("ok")) {
						switch (field) {
						case "name":
							m.setName(value);
							break;
						case "type":
							m.setType(value);
							break;
						case "nicety":
							try {
								m.setNicety(Float.parseFloat(value));
							} catch(NumberFormatException e) {
								message = "Valore della finezza non valido";
							}
							break;
						case "color":
							outputResult = value;
							m.setColor(value);
							break;

						default:
							message = "Campo non riconosciuto";
							break;
						}// switch
					}
				} else {
					message = "Macchina da modificare non trovata";
				}

				if (message.equals("ok")) {
					hibSession.saveOrUpdate(m);
				}
			}
			break;

		case "joborder":
			if (!user.getCanAddJobOrder()) {
				message = "Non puoi aggiungere commesse";
			} else {
				fields = new String[] { "client", "leadTime" };
				Arrays.sort(fields);
				toEdit = (JobOrder) hibSession.get(JobOrder.class, id);

				JobOrder j = new JobOrder();
				if (toEdit != null) { // edit
					j = (JobOrder) toEdit;
					params = ServletUtils.getParameters(request, new String[] {
							"columnName", "value" });
					String field = params.get("columnName");
					if (params.containsValue(null) || params.containsValue("")) {
						message = "Richiesta di edit errata";
					} else if (Arrays.binarySearch(fields, field) == -1) {
						message = "Nome colonna non valido";
					}

					String value = params.get("value");
					if (message.equals("ok")) {
						switch (field) {
						case "client":
							try {
								Client c = (Client) hibSession.get(Client.class, Long.parseLong(value));
								if(c == null) {
									throw new NumberFormatException();
								}
								j.setClient(c);
							} catch(NumberFormatException e) {
								message = "Cliente non trovato";
							}
							break;
						case "leadTime":
							try {
								Long lt = Long.parseLong(value);
								if(lt <= 0) {
									throw new NumberFormatException();
								}
								j.setLeadTime(lt);
								outputResult = value;
							} catch(NumberFormatException e) {
								message = "Tempo di produzione errato";
							}
							break;
						default:
							message = "Campo non riconosciuto";
							break;
						}// switch
					}
				} else {
					message = "Commessa da modificare non trovata";
				}

				if (message.equals("ok")) {
					hibSession.saveOrUpdate(j);
				}
			}
			break;
		}
		if (message.equals("ok")) {
			History h = new History();
			h.setAction("EDIT");
			h.setTime(new Date());
			h.setUser(user);
			h.setWhat(what + "(" + id + "): " + params.get("columnName") + " = " + params.get("value"));
			hibSession.saveOrUpdate(h);
			hibSession.getTransaction().commit();
		} else {
			hibSession.getTransaction().rollback();
		}
		out.print(outputResult.equals("")  ? message : outputResult);

		hibSession.close();
	}

}
