<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Distributed Systems Class Clicker</title>
</head>
<body>
<h1>Welcome to Clicker App</h1>

<% if (request.getAttribute("submitted") != null) { %>
<!-- Show user's result -->
<h2>Your answer (<%= request.getAttribute("submitted") %>) has been registered.</h2>
<% } %>

<!-- Show multiple choice options only if answer not yet submitted -->
<form action="submit" method="post">
  <input type="radio" name="answer" value="A"> A<br>
  <input type="radio" name="answer" value="B"> B<br>
  <input type="radio" name="answer" value="C"> C<br>
  <input type="radio" name="answer" value="D"> D<br>
  <input type="submit" value="Submit Answer">
</form>

<form action="getResults" method="get">
  <input type="submit" value="View Results">
</form>
</body>
</html>
