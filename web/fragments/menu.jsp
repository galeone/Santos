<div id="menu">
	<ul id="tabs">
		<li <%= request.getRequestURI().indexOf("home") > -1 ? "style=\"background-color: #aaa;\"" : ""%>>
			<a href="<%= request.getContextPath() %>/pages/home.jsp">Home</a>
		</li>
		<li <%= request.getRequestURI().indexOf("catalogue") > -1? "style=\"background-color: #aaa;\"" : ""%>>
			<a href="<%= request.getContextPath() %>/pages/catalogue.jsp">Manage catalogue</a>
		</li>
		<li <%= request.getRequestURI().indexOf("cart") > -1 ? "style=\"background-color: #aaa;\"" : ""%>>
			<a href="<%= request.getContextPath() %>/pages/cart.jsp">Manage cart</a>
		</li>
		<li <%= request.getRequestURI().indexOf("checkout") > -1 ? "style=\"background-color: #aaa;\"" : ""%>>
			<a href="<%= request.getContextPath() %>/pages/checkout.jsp">Checkout</a>
		</li>
	</ul>
</div>