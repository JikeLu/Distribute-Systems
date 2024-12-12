package ds;

// Work by Jike Lu, jikelu, assisted by ChatGPT


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@WebServlet(name = "StateInformationServlet",
        urlPatterns = {"/getAnStateInformation"})
public class StateInformationServlet extends HttpServlet {

    private final List<String> states = Arrays.asList(
            "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
            "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
            "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
            "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
            "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
            "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
            "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming");
    StateInformationModel sim = null;

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        sim = new StateInformationModel();
    }
    private boolean isValidState(String input) {
        return states.contains(input);
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("state");
        if (request.getParameter("submitCustom") != null) {
            // Redirect the user to Error.jsp
            String userInput = request.getParameter("userInput");
            if(isValidState(userInput)) {
                search = userInput;
            }else {
                response.sendRedirect("Error.jsp");
                return; // Stop further execution
            }
        }


        // determine what type of device our user is
        String ua = request.getHeader("User-Agent");

        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
            * This is the latest XHTML Mobile doctype. To see the difference it
            * makes, comment it out so that a default desktop doctype is used
            * and view on an Android or iPhone.
            */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        String nextView;
        /*
        * Check if the search parameter is present.
        * If not, then give the user instructions and prompt for a search string.
        * If there is a search parameter, then do the search and return the result.
        */
        if (search != null) {
            String picSize = (mobile) ? "mobile" : "desktop";
            // use model to do the search and choose the result view
            String state = search;
            String flagURL = sim.doFlagSearch(search);
            String population = sim.getPopulation(search);
            //String nickname = sim.getNickName(search);
            String capital = sim.getCapital(search);
            String song = sim.getSong(search);
            String bird = sim.getBird(search);
            String birdURL = sim.doBirdSearch(search);
            request.setAttribute("state", state);
            request.setAttribute("population", population);
            request.setAttribute("capital", capital);
            request.setAttribute("song", song);
            request.setAttribute("bird", bird);
            request.setAttribute("birdURL", birdURL);
            request.setAttribute("flagURL", flagURL);
            // Pass the user search string (pictureTag) also to the view.
            nextView = "result.jsp";
        } else {
            nextView = "prompt.jsp";
        }
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }
}

