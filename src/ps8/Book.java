package ps8;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {
	
	private int bookNumber;
	private String title;
	private String author;
	private boolean checkedOut;
	
	public Book(int bookNumber, String title, String author,
			boolean checkedOut) {
		super();
		this.bookNumber = bookNumber;
		this.title = title;
		this.author = author;
		this.checkedOut = checkedOut;
	}

	public int getBookNumber() {
		return bookNumber;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}
	
	public boolean getCheckedOut() {
		return checkedOut;
	}
	
	public JSONObject asJSONObject() throws JSONException {
		JSONObject result = new JSONObject();
		result.put("bookNumber", bookNumber);
		result.put("title", title);
		result.put("author", author);
		result.put("checkedOut", checkedOut);
		return result;
	}

}
