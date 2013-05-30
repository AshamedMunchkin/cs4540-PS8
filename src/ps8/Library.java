package ps8;

import java.sql.*;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Library {
	
	private Connection booksConnection;
	private Connection patronsConnection;

	public Library() {
	}
	
	private void openBooksDatabase() throws SQLException {
		try {
			Context initialContext = new InitialContext();
			Context envContext =
					(Context) initialContext.lookup("java:comp/env");
			DataSource dataSource =
					(DataSource) envContext.lookup("library/books");
			booksConnection = dataSource.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private void openPatronsDatabase() throws SQLException {
		try {
			Context initialContext = new InitialContext();
			Context envContext =
					(Context) initialContext.lookup("java:comp/env");
			DataSource dataSource =
					(DataSource) envContext.lookup("library/patrons");
			patronsConnection = dataSource.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private void closeBooksDatabase() throws SQLException {
		booksConnection.close();
	}
	
	private void closePatronsDatabase() throws SQLException {
		patronsConnection.close();
	}
	
	public ResultPage<Book> getBooks(String search, String searchColumn,
			String orderColumn, int offset) throws SQLException {
		openBooksDatabase();
	
		StringBuilder query =
				new StringBuilder("SELECT * FROM Titles WHERE ");
		if (searchColumn.equals("Title")) {
			query.append("Title");
		} else {
			query.append("Author");
		}
		query.append(" LIKE ? ORDER BY ");
		if (orderColumn.equals("Title")) {
			query.append("Title, Author");
		} else {
			query.append("Author, Title");
		}
		query.append(" LIMIT 11 OFFSET ?");
		
		PreparedStatement statement =
				booksConnection.prepareStatement(query.toString());

		statement.setString(1, '%' + search + '%');
		statement.setInt(2, offset * 10);
		
		ResultSet resultSet = statement.executeQuery();
		
		ArrayList<Book> results = new ArrayList<Book>(10);
		
		openPatronsDatabase();
		
		for (int book = 0; book < 10 && resultSet.next(); book++) {
			PreparedStatement subStatement =
					patronsConnection.prepareStatement(
							"SELECT * FROM CheckedOut " +
							"WHERE BookNumber = ?");
			subStatement.setInt(1, resultSet.getInt(1));
			ResultSet subResultSet = subStatement.executeQuery();
			
			results.add(new Book(resultSet.getInt(1),
					resultSet.getString(2),	resultSet.getString(3),
					subResultSet.next()));
			
			subResultSet.close();
			subStatement.close();
		}
		
		closePatronsDatabase();
		
		boolean isBeginning = false;
		boolean isEnd = false;
		
		if (offset == 0) {
			isBeginning = true;
		}
		if (!resultSet.next()) {
			isEnd = true;
		}
		
		resultSet.close();
		statement.close();
		closeBooksDatabase();
		
		return new ResultPage<Book>(results, offset, isBeginning, isEnd);
	}
	
	public ResultPage<Book> getPatronRecord(int cardNumber, String search,
			String searchColumn, String orderColumn, int offset)
					throws SQLException {
		openPatronsDatabase();
		
		PreparedStatement statement = patronsConnection.prepareStatement(
				"SELECT * FROM CheckedOut WHERE CardNumber = ?");
		statement.setInt(1, cardNumber);
		
		ResultSet resultSet = statement.executeQuery();
		
		StringBuilder subQuery = new StringBuilder();
		
		subQuery.append("SELECT * FROM Titles WHERE ");
		if (resultSet.next()) {
			subQuery.append('(');
			subQuery.append("BookID = ").append(resultSet.getInt(1));
		} else {
			resultSet.close();
			statement.close();
			closePatronsDatabase();
			return new ResultPage<Book>(new ArrayList<Book>(), 0, true, true);
		}
		while (resultSet.next()) {
			subQuery.append(" OR ");
			subQuery.append("BookID = ").append(resultSet.getInt(1));
			if (resultSet.isLast()) {
				subQuery.append(") AND ");
			}
		}
		
		resultSet.close();
		statement.close();
		closePatronsDatabase();
		
		if (searchColumn.equals("Title")) {
			subQuery.append("Title");
		} else {
			subQuery.append("Author");
		}
		subQuery.append(" LIKE ? ORDER BY ");
		if (orderColumn.equals("Title")) {
			subQuery.append("Title, Author");
		} else {
			subQuery.append("Author, Title");
		}
		subQuery.append(" LIMIT 11 OFFSET ?");
		
		openBooksDatabase();
		
		PreparedStatement subStatement =
				booksConnection.prepareStatement(subQuery.toString());
		subStatement.setString(1, '%' + search + '%');
		subStatement.setInt(2, offset * 10);
		
		ResultSet subResultSet = subStatement.executeQuery();
		
		ArrayList<Book> results = new ArrayList<Book>(10);
		
		for (int book = 0; book < 10 && subResultSet.next(); book++) {
			results.add(new Book(subResultSet.getInt(1),
					subResultSet.getString(2), subResultSet.getString(3),
					true));
		}
		
		boolean isBeginning = false;
		boolean isEnd = false;
		
		if (offset == 0) {
			isBeginning = true;
		}
		if (!subResultSet.next()) {
			isEnd = true;
		}				
		
		subResultSet.close();
		subStatement.close();
		closeBooksDatabase();
		
		return new ResultPage<Book>(results, offset, isBeginning, isEnd);
	}
	
	public ResultPage<Patron> getPatronList(int offset) throws SQLException {
		openPatronsDatabase();
		
		PreparedStatement statement = patronsConnection.prepareStatement(
				"SELECT * FROM Patrons ORDER BY Name LIMIT 11 OFFSET ?");
		statement.setInt(1, offset * 10);
		
		ResultSet resultSet = statement.executeQuery();
		
		ArrayList<Patron> results = new ArrayList<Patron>(10);
		
		for (int book = 0; book < 10 && resultSet.next(); book++) {
			results.add(new Patron(resultSet.getInt(1),
					resultSet.getString(2)));
		}
		
		boolean isBeginning = false;
		boolean isEnd = false;
		
		if (offset == 0) {
			isBeginning = true;
		}
		if (!resultSet.next()) {
			isEnd = true;
		}
		
		resultSet.close();
		statement.close();
		closePatronsDatabase();
		
		return new ResultPage<Patron>(results, offset, isBeginning, isEnd);
	}
	
	public Patron getPatron(int cardNumber) throws SQLException {
		openPatronsDatabase();
		
		PreparedStatement statement = patronsConnection.prepareStatement(
				"SELECT * FROM Patrons WHERE CardNumber = ?");
		statement.setInt(1, cardNumber);
		
		ResultSet resultSet = statement.executeQuery();
		
		Patron result;
		
		if (resultSet.next()) {
			result = new Patron(resultSet.getInt(1), resultSet.getString(2));
		} else {
			result = null;
		}
		
		resultSet.close();
		statement.close();
		closePatronsDatabase();
		
		return result;
	}
	
	public void checkOutBook(int bookNumber, int cardNumber)
			throws SQLException {
		openPatronsDatabase();
		PreparedStatement statement = patronsConnection.prepareStatement(
				"INSERT INTO CheckedOut VALUES (?, ?)");
		statement.setInt(1, bookNumber);
		statement.setInt(2, cardNumber);
		
		statement.execute();
		
		statement.close();
		closePatronsDatabase();
	}
	
	public void checkInBook(int bookNumber) throws SQLException {
		openPatronsDatabase();
		PreparedStatement statement = patronsConnection.prepareStatement(
				"DELETE FROM CheckedOut WHERE BookNumber = ?");
		statement.setInt(1, bookNumber);
		
		statement.execute();
		
		statement.close();
		closePatronsDatabase();
	}
}
