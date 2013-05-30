<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>PS8 | Patron Record</title>
		<script 
			src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js">
		</script>
		<script>
		var offset;
		var loggedIn;
		$('document').ready(function () {
			offset = 0;
			loggedIn = ${loggedIn};
			
			$("#login-button").click(function () {
				login($('#card-number').val());});
			$("#search, #by-title, #by-author").change(function () {
				offset = 0;
				bookSearch();
			});
			$('#previous').click(function () {
				offset--;
				bookSearch();
			});
			$('#next').click(function () {
				offset++;
				bookSearch();
			});
			$('#reset').click(function () {
				$('#search').val('');
				$('#by-title').prop('checked', true);
				offset = 0;
				bookSearch();
			});
			$('#book-titles, #book-authors').click(function () {
				$('#book-titles, #book-authors').removeClass('sort-by');
				$(this).addClass('sort-by');
				offset = 0;
				bookSearch();
			});
			bindCheckIn();
		});
		
		function bindCheckIn() {
			$('button.check-in').click(function () {
				checkIn($(this).val());
			});
		}
		
		function login(cardNumber) {
			$('#name').empty();
			$('#login-error').empty();
			$('#show-conditionally').prop('hidden', true);
			$.post('login-ajax', {'cardNumber': $('#card-number').val()},
					loginCallback);
		}

		function loginCallback(result) {
			if (result.error) {
				$('#login-error').append(result.error);
			} else {
				loggedIn = true;
				$('#name').append(result.name);
				$('#show-conditionally').prop('hidden', false);
				$('h2').html(result.name);
				bookSearch();
			}
		}

		function bookSearch() {
			var searchColumn;
			if ($('#by-title:checked').length) {
				searchColumn = "Title";
			} else {
				searchColumn = "Author";
			}
			var orderColumn;
			if ($('#book-titles.sort-by').length) {
				orderColumn = "Title";
			} else {
				orderColumn = "Author";
			}
			$.get('patron-record-ajax', {
				'search': $('#search').val(),
				'searchColumn': searchColumn,
				'orderColumn': orderColumn,
				'offset': offset
				}, bookSearchCallback);
		}

		function bookSearchCallback(result) {
			var contents = "";
			for (var i = 0; i < result.results.length; i++) {
				contents += '<tr><td>' + result.results[i].title + '</td><td>' +
						result.results[i].author + '</td><td>';
				contents += '<button class="check-in" value="' +
								result.results[i].bookNumber +
								'">Check In</button>';
				contents += '</td></tr>';
			}
			
			$('tbody').html(contents);
			if (result.isBeginning) {
				$('#previous').prop('disabled', true);
			} else {
				$('#previous').prop('disabled', false);
			}
			if (result.isEnd) {
				$('#next').prop('disabled', true);
			} else {
				$('#next').prop('disabled', false);
			}
			offset = result.pageNumber;
			bindCheckIn();
		}
		
		function checkIn(bookNumber) {
			$.post('check-in-ajax', {'bookNumber': bookNumber},
					checkInCallback);
		}
		
		function checkInCallback(result) {
			bookSearch();
		}
		</script>
	</head>
	<body>
		<ul>
			<li><a href="browsing">Browsing</a></li>
			<li><a href="patron-record">Patron Record</a></li>
			<li><a href="patron-list">Patron List</a></li>
		</ul>
		<div id="login">
			<label>Login</label>
			<input type="text" id="card-number" value="${patron.cardNumber}">
			<span id="name">${patron.name}</span>
			<span id="login-error"></span>
			<input type="button" id="login-button" value="Login">
		</div>
		<div id="show-conditionally" <c:if test="${!loggedIn}">hidden</c:if>>
			<h2>${patron.name}</h2>
			<div>
				<label for="search">Search</label>
				<input type="text" id="search" name="search">
				<label for="by-title">by title</label>
				<input type="radio" id="by-title" name="by" value="title" checked>
				<label for="by-author">by author</label>
				<input type="radio" id="by-author" name="by" value="author">
			</div>
			<table>
				<thead>
					<tr>
						<th>
							<a href="#" id="book-titles" class="sort-by">Title</a>
						</th>
						<th>
							<a href="#" id="book-authors">Author</a>
						</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${bookResults.results}" var="bookResult">
					<tr>
						<td>${bookResult.title}</td>
						<td>${bookResult.author}</td>
						<td>
							<button class="check-in"
									value="${bookResult.bookNumber}">
								Check In
							</button>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<div>
				<input type="button" id="previous" value="Previous"
						<c:if test="${bookResults.isBeginning}">disabled</c:if>>
				<input type="button" id="next" value="Next"
						<c:if test="${bookResults.isEnd}">disabled</c:if>>
				<input type="button" id="reset" value="Reset">
			</div>
		</div>
	</body>
</html>