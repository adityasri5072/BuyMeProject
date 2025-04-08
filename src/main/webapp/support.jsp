<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="com.buymeproject.database.DBConfig"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Support Questions</title>
<style>
.question-box {
	border: 2px solid #ddd;
	padding: 10px;
	margin: 10px;
	background-color: #f9f9f9;
	position: relative;
}

.reply-button {
	position: absolute;
	right: 10px;
	top: 10px;
}

.reply-form {
	display: none; /* Initially hidden */
	margin-top: 20px;
}

.reply-display {
	padding: 5px;
	margin-top: 5px;
	background-color: #eef;
}

.reply-display-rep {
	background-color: #d9edf7;
	font-weight: bold;
}

.visible {
	display: block;
}

button {
	cursor: pointer;
}
</style>
</head>
<body>
	<h1>Support Questions</h1>
	<a href="dashboard.jsp"><button type="button">Return Home</button></a>
	<div>
		<form method="GET" action="support.jsp">
			<input type="text" name="searchQuery" id="searchQuery"
				placeholder="Search By Keyword..." />
			<button type="submit">Search</button>
			<button type="button" onclick="window.location.href='support.jsp';">See
				Original</button>
		</form>
		<textarea id="questionText" rows="4" cols="50"></textarea>
		<button onclick="postQuestion()">Post Question</button>
	</div>
	<hr>
	<script type="text/javascript">
		
		function postQuestion() {
			var questionText = document.getElementById('questionText').value;
			if (questionText.trim() === '') {
				alert('Please enter a question.');
				return;
			}
			var xhr = new XMLHttpRequest();
			xhr.open('POST', 'QuestionServlet', true);
			xhr.setRequestHeader('Content-Type',
					'application/x-www-form-urlencoded');
			xhr.onload = function() {
				if (xhr.status === 200) {
					window.location.reload();
				} else {
					alert('Error posting question: ' + xhr.responseText);
				}
			};
			xhr.send('question=' + encodeURIComponent(questionText));
		}

		
		function toggleReplyForm(questionId) {
			var form = document.getElementById('replyForm' + questionId);
			form.style.display = form.style.display === 'block' ? 'none'
					: 'block';
		}

		
		function postReply(questionId) {
			var replyText = document.getElementById('replyText' + questionId).value;
			if (replyText.trim() === '') {
				alert('Please enter a reply.');
				return;
			}
			var xhr = new XMLHttpRequest();
			xhr.open('POST', 'ReplyServlet', true);
			xhr.setRequestHeader('Content-Type',
					'application/x-www-form-urlencoded');
			xhr.onload = function() {
				if (xhr.status === 200) {
					window.location.reload(); 
				} else {
					alert('Error posting reply: ' + xhr.responseText);
				}
			};
			xhr.send('replyText=' + encodeURIComponent(replyText)
					+ '&questionId=' + questionId);
		}
	</script>
	<div id="questionsContainer">
		<%
		String searchQuery = request.getParameter("searchQuery");
		Boolean isCustomerRep = (Boolean) session.getAttribute("isCustomerRep");
		Boolean isLoggedIn = (session != null) && (session.getAttribute("userID") != null);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Connection conn = DBConfig.getConnection();
			String sql = "SELECT q.question_id, q.question_text, u.Username, r.reply_text, r.created_at, cr.Name AS RepName "
			+ "FROM Questions q JOIN User u ON q.user_id = u.userID "
			+ "LEFT JOIN Replies r ON q.question_id = r.question_id "
			+ "LEFT JOIN CustomerRepresentative cr ON r.user_id = cr.userID ";
			if (searchQuery != null && !searchQuery.isEmpty()) {
				sql += "WHERE q.question_text LIKE ? ";
			}
			sql += "ORDER BY q.created_at DESC, cr.Name DESC, r.created_at ASC";
			stmt = conn.prepareStatement(sql);
			if (searchQuery != null && !searchQuery.isEmpty()) {
				stmt.setString(1, "%" + searchQuery.replace(" ", "%") + "%");
			}
			rs = stmt.executeQuery();
			int currentQuestionId = -1;
			while (rs.next()) {
				int questionId = rs.getInt("question_id");
				if (questionId != currentQuestionId) {
			if (currentQuestionId != -1) {
				out.println("</div>"); 
			}
			currentQuestionId = questionId;
			out.println("<div class='question-box'>");
			out.println("<p><strong>" + rs.getString("Username") + " asks:</strong> " + rs.getString("question_text")
					+ "</p>");
			
			if (isLoggedIn) {
				out.println(
						"<button class='reply-button' onclick='toggleReplyForm(" + questionId + ")'>Reply</button>");
				out.println("<div id='replyForm" + questionId + "' class='reply-form'>");
				out.println("<textarea id='replyText" + questionId + "' rows='2' cols='50'></textarea>");
				out.println("<button onclick='postReply(" + questionId + ")'>Submit Reply</button>");
				out.println("</div>");
			}
				}
				String reply = rs.getString("reply_text");
				if (reply != null && !reply.isEmpty()) {
			String repName = rs.getString("RepName");
			String divClass = repName != null ? "reply-display-rep" : "reply-display";
			out.println(
					"<div class='" + divClass + "'><strong>" + (repName != null ? repName + " (Customer Rep)" : "User")
							+ " replies:</strong> " + reply + "</div>");
				}
			}
			if (currentQuestionId != -1) {
				out.println("</div>"); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null)
				try {
			stmt.close();
				} catch (SQLException e) {
			/* ignored */ }
			if (rs != null)
				try {
			rs.close();
				} catch (SQLException e) {
			/* ignored */ }
		}
		%>
	</div>
</body>
</html>
