package com.viaagnolettisrl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.MarshallException;

public class JSONServlet extends HttpServlet {

	private static final long serialVersionUID = 9103338856018467773L;
	private JSONSerializer serializer;

	@Override
	public void init() {
		/*
		 * Inizializza se non esiste già un elemento che dura per tutta la
		 * durata di vita dell'applicazione
		 */

		/*
		 * Esempio: DummyDb Db =
		 * (FDummyDb)this.getServletContext().getAttribute("db"); if ( feedDb ==
		 * null ){ Db = new DummyDb();
		 * this.getServletContext().setAttribute("db", Db); }
		 */

		serializer = new JSONSerializer();
		try {
			// inizializza i tipi serializzatori forniti di default
			serializer.registerDefaultSerializers();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// Vedi doGet per il JSON.
		// Gestisci i parametri via post con request.getParameter(nome);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/javascript");
		PrintWriter out = response.getWriter();

		try {
			// Cambiare il new String[]{} con l'oggetto che vogliamo ritornare
			// in formato json
			out.println(serializer.toJSON(new String[] {}));
		} catch (MarshallException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
}
