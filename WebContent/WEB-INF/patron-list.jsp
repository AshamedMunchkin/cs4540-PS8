<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<!doctype html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>PS8 | Patron List</title>
		<script 
			src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js">
		</script>
		<script>
		var offset;
		$('document').ready(function () {
			offset = 0;
			
			$('#previous').click(function () {
				offset--;
				patronList();
			});
			$('#next').click(function () {
				offset++;
				patronList();
			});
			$('#reset').click(function () {
				offset = 0;
				patronList();
			});
		});

		function patronList() {
			$.get('patron-list-ajax', {
				'offset': offset
				}, patronListCallback);
		}

		function patronListCallback(result) {
			var contents = "";
			for (var i = 0; i < result.results.length; i++) {
				contents += '<tr><td>' + result.results[i].name + '</td><td>' +
						result.results[i].cardNumber + '</td><td>';
				contents += '<a href="patron-record?cardNumber=' +
						result.results[i].cardNumber + '">Patron Record</a>';
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
		}
		</script>
	</head>
	<body>
		<ul>
			<li><a href="browsing">Browsing</a></li>
			<li><a href="patron-record">Patron Record</a></li>
			<li><a href="patron-list">Patron List</a></li>
		</ul>
		<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>Card Number</th>
					<th>Patron Record</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${patronResults.results}" var="patronResult">
				<tr>
					<td>${patronResult.name}</td>
					<td>${patronResult.cardNumber}</td>
					<td>
						<a href="patron-record?cardNumber=${patronResult.cardNumber}">
						Patron Record
						</a>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<div>
			<input type="button" id="previous" value="Previous"
					<c:if test="${patronResults.isBeginning}">disabled</c:if>>
			<input type="button" id="next" value="Next"
					<c:if test="${patronResults.isEnd}">disabled</c:if>>
			<input type="button" id="reset" value="Reset">
		</div>
	</body>
</html>