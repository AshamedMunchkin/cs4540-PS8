package ps8;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class PatronRecordAjax
 */
@WebServlet("/patron-record-ajax")
public class PatronRecordAjax extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			Library library = new Library();
			ResultPage<Book> books = library.getPatronRecord(
					((Patron) request.getSession().getAttribute("patron"))
							.getCardNumber(),
					request.getParameter("search"),
					request.getParameter("searchColumn"),
					request.getParameter("orderColumn"),
					Integer.parseInt(request.getParameter("offset")));
			JSONObject result = new JSONObject();
			JSONArray results = new JSONArray();
			for (Book book : books.getResults()) {
				results.put(book.asJSONObject());
			}
			result.put("results", results);
			result.put("pageNumber", books.getPageNumber());
			result.put("isBeginning", books.getIsBeginning());
			result.put("isEnd", books.getIsEnd());
			
			response.setContentType("application/json");
			response.getWriter().print(result);
			response.getWriter().close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
