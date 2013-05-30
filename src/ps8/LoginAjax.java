package ps8;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Servlet implementation class LoginAjax
 */
@WebServlet("/login-ajax")
public class LoginAjax extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Library library = new Library();
		
		JSONObject patronJSON = new JSONObject();
		int cardNumber = 0;
		try {
			cardNumber = Integer.parseInt(
					request.getParameter("cardNumber"));
		} catch (NumberFormatException nan) {
			try {
				response.setContentType("application/json");
				response.getWriter().print(new JSONObject().put(
						"error", "Not a number."));
				response.getWriter().close();
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Patron patron = library.getPatron(cardNumber);
			if (patron == null) {
				patronJSON.put("error", "Invalid card number.");
			} else {
				request.getSession().setAttribute("loggedIn", true);
				request.getSession().setAttribute("patron",
						patron);
				patronJSON.put("cardNumber", patron.getCardNumber());
				patronJSON.put("name", patron.getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		response.setContentType("application/json");
		response.getWriter().print(patronJSON);
		response.getWriter().close();
	}

}
