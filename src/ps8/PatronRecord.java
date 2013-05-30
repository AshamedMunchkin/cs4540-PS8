package ps8;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PatronRecord
 */
@WebServlet("/patron-record")
public class PatronRecord extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			Library library = new Library();
			if (request.getParameter("cardNumber") != null) {
				request.getSession().setAttribute("patron",
						library.getPatron(Integer.parseInt(
								request.getParameter("cardNumber"))));
			}
			if (request.getSession().getAttribute("patron") != null) {
				request.setAttribute("bookResults",
						library.getPatronRecord(
								((Patron) request.getSession().getAttribute(
										"patron")).getCardNumber(),
								"", "Title", "Title", 0));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.getRequestDispatcher("WEB-INF/patron-record.jsp").forward(
				request, response);
	}

}
