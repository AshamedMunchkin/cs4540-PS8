package ps8;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class CheckOutAjax
 */
@WebServlet("/check-out-ajax")
public class CheckOutAjax extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Library library = new Library();
		JSONObject result = new JSONObject();
		try {
			library.checkOutBook(
					Integer.parseInt(request.getParameter("bookNumber")),
					((Patron) request.getSession().getAttribute("patron"))
							.getCardNumber());
			
			response.setContentType("application/json");
			response.getWriter().print(result);
			response.getWriter().close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
