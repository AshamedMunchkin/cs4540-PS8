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
 * Servlet implementation class PatronListAjax
 */
@WebServlet("/patron-list-ajax")
public class PatronListAjax extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			Library library = new Library();
			ResultPage<Patron> patrons = library.getPatronList(
					Integer.parseInt(request.getParameter("offset")));
			JSONObject result = new JSONObject();
			JSONArray results = new JSONArray();
			for (Patron patron : patrons.getResults()) {
				results.put(patron.asJSONObject());
			}
			result.put("results", results);
			result.put("pageNumber", patrons.getPageNumber());
			result.put("isBeginning", patrons.getIsBeginning());
			result.put("isEnd", patrons.getIsEnd());
			
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
