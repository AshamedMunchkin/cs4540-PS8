<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>PS8 | Browsing</title>
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
			bindCheckOut();
		});
		
		function bindCheckOut() {
			$('button.available').click(function () {
				checkOut($(this).val());
			});
		}
		
		function login(cardNumber) {
			$('#name').empty();
			$('#login-error').empty();
			$.post('login-ajax', {'cardNumber': $('#card-number').val()},
					loginCallback);
		}

		function loginCallback(result) {
			if (result.error) {
				$('#login-error').append(result.error);
			} else {
				loggedIn = true;
				$('#name').append(result.name);
				$('.available').prop('disabled', false);
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
			$.get('browsing-ajax', {
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
				if (result.results[i].checkedOut) {
					contents += '<button disabled>Checked Out</button>';
				} else {
					if (loggedIn) {
						contents += '<button class="available" value="' +
								result.results[i].bookNumber +
								'">Check Out</button>';
					} else {
						contents += '<button class="available" disabled' +
								' value="' + result.results[i].bookNumber +
								'">Check Out</button>';
					}
				}
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
			bindCheckOut();
		}
		
		function checkOut(bookNumber) {
			$.post('check-out-ajax', {'bookNumber': bookNumber},
					checkOutCallback);
		}
		
		function checkOutCallback(result) {
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
						<c:choose>
						<c:when test="${bookResult.checkedOut}">
						<button disabled>
							Checked Out
						</button>
						</c:when>
						<c:otherwise>
						<button value="${bookResult.bookNumber}"
								class="available"
								<c:if test="${!loggedIn}">disabled</c:if>>
							Check Out
						</button>
						</c:otherwise>
						</c:choose>
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
	</body>
</html>