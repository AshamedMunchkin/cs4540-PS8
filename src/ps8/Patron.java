package ps8;

import org.json.JSONException;
import org.json.JSONObject;

public class Patron {

	private int cardNumber;
	private String name;
	
	public Patron(int cardNumber, String name) {
		super();
		this.cardNumber = cardNumber;
		this.name = name;
	}

	public int getCardNumber() {
		return cardNumber;
	}

	public String getName() {
		return name;
	}
	
	public JSONObject asJSONObject() {
		JSONObject result = new JSONObject();
		try {
			result.put("cardNumber", cardNumber);
			result.put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
