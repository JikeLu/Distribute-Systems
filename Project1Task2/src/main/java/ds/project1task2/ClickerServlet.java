package ds.project1task2;

import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "ClickerServlet", urlPatterns = {"/submit", "/getResults"})
public class ClickerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Map<String, Integer> results = new HashMap<>();
    private List<String> pastAnswers = new ArrayList<>();
    private String[] choices = {"A", "B", "C", "D"};
    private int questionIndex = 0;

    /*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String answer = request.getParameter("answer");
        pastAnswers.add(answer);
        results.put(answer, results.getOrDefault(answer, 0) + 1);

        // Set attribute to indicate answer submission
        request.setAttribute("submitted", answer);

        // Redirect back to index.jsp to display another set of options
        response.sendRedirect("index.jsp");
    }*/
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String answer = request.getParameter("answer");
        pastAnswers.add(answer);
        results.put(answer, results.getOrDefault(answer, 0) + 1);

        // Set attribute to indicate answer submission
        request.setAttribute("submitted", answer);

        // Forward to index.jsp to display the next question along with the result
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        dispatcher.forward(request, response);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("/getResults".equals(request.getServletPath())) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            if (results.isEmpty()) {
                out.println("<h2>No results available.</h2>");
            } else {
                out.println("<h2>Results:</h2>");
                List<String> sortedAnswers = new ArrayList<>(results.keySet());
                Collections.sort(sortedAnswers);
                for (String answer : sortedAnswers) {
                    out.println(answer + ": " + results.get(answer) + "<br>");
                }
                if (results.isEmpty()) {
                    out.println("<h2>No Past Answers Avilable. </h2>");
                }
                results.clear();
            }
            out.println("<h2>Past Answers:</h2>");
            for (String pastAnswer : pastAnswers) {
                out.println(pastAnswer + "<br>");
            }
            out.println("</body></html>");
        }
    }
}
