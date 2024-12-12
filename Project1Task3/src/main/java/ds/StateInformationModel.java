package ds;
// Work by Jike Lu, jikelu, assisted by ChatGPT

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class StateInformationModel {
    static final String[][] fips = {{"Alabama", "01"}, {"Alaska", "02"}, {"Arizona", "04"}, {"Arkansas", "05"}, {"California", "06"},
            {"Colorado", "08"}, {"Connecticut", "09"}, {"Delaware", "10"}, {"Florida", "12"}, {"Georgia", "13"}, {"Hawaii", "15"},
            {"Idaho", "16"}, {"Illinois", "17"}, {"Indiana", "18"}, {"Iowa", "19"}, {"Kansas", "20"}, {"Kentucky", "21"},
            {"Louisiana", "22"}, {"Maine", "23"}, {"Maryland", "24"}, {"Massachusetts", "25"}, {"Michigan", "26"},
            {"Minnesota", "27"}, {"Mississippi", "28"}, {"Missouri", "29"}, {"Montana", "30"}, {"Nebraska", "31"}, {"Nevada", "32"},
            {"New Hampshire", "33"}, {"New Jersey", "34"}, {"New Mexico", "35"}, {"New York", "36"}, {"North Carolina", "37"},
            {"North Dakota", "38"}, {"Ohio", "39"}, {"Oklahoma", "40"}, {"Oregon", "41"}, {"Pennsylvania", "42"},
            {"Rhode Island", "44"}, {"South Carolina", "45"}, {"South Dakota", "46"}, {"Tennessee", "47"}, {"Texas", "48"},
            {"Utah", "49"}, {"Vermont", "50"}, {"Virginia", "51"}, {"Washington", "53"}, {"West Virginia", "54"},
            {"Wisconsin", "55"}, {"Wyoming", "56"}};

    String capitalResponse = fetch("https://gisgeography.com/united-states-map-with-capitals/");
    String songResponse = fetch("https://www.50states.com/songs/");
    String birdResponse = fetch("https://statesymbolsusa.org/categories/bird");
    String flagResponse = fetch("https://www.states101.com/flags");
    HashMap<String, String> fips_code;

    StateInformationModel() {
        fips_code = new HashMap<>();
        for (String[] state : fips) {
            fips_code.put(state[0], state[1]);
        }
    }

    public String getPopulation(String searchTag) {
        String stateCode = fips_code.get(searchTag);
        String url = "https://api.census.gov/data/2020/dec/pl?get=NAME,P1_001N&for=state:"
                + stateCode
                + "&key=a5406f80f9aefd5891b625e07ca533e36106e6f5";
        String response = fetch(url);

        // Use Gson to parse JSON response
        JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
        JsonElement populationElement = jsonArray.get(1).getAsJsonArray().get(1); // Assuming population value is at index 1

        return populationElement.getAsString();
    }

    public String getCapital(String searchTag){
        int cutLeft = capitalResponse.indexOf(searchTag + " (") + searchTag.length() + 2;
        int cutRight = capitalResponse.indexOf(")", cutLeft);
        return capitalResponse.substring(cutLeft, cutRight);
    }

    public String getSong(String searchTag){
        int cut1 = songResponse.indexOf(searchTag + "</dt><dd><a")+searchTag.length() + 11;
        int cutLeft = songResponse.indexOf(">", cut1) + 1;
        int cutRight = songResponse.indexOf("<", cutLeft);
        return songResponse.substring(cutLeft, cutRight);
    }

    public String getBird(String searchTag){
        int cut1 = birdResponse.indexOf(searchTag + "<br><a")+searchTag.length() + 12;
        int cutLeft = birdResponse.indexOf(">", cut1) + 1;
        int cutRight = birdResponse.indexOf("<", cutLeft);
        return birdResponse.substring(cutLeft, cutRight);
    }

    public String doBirdSearch(String searchTag) {
        String hyphen = searchTag.replace(' ', '-');
        int cut1 = birdResponse.indexOf(hyphen.toLowerCase() + "/state-bird");
        int cutLeft = birdResponse.indexOf("src=", cut1) + 5;
        int cutRight = birdResponse.indexOf("width=", cutLeft) -2;
        return "https://statesymbolsusa.org" + birdResponse.substring(cutLeft, cutRight);
    }

    public String doFlagSearch(String searchTag) {
        String hyphen = searchTag.replace(' ', '-');
        int cut1 = flagResponse.indexOf("flags/" + hyphen.toLowerCase());
        int cutLeft = flagResponse.indexOf("src=", cut1) + 5;
        int cutRight = flagResponse.indexOf("alt=", cutLeft) -2;
        return "https://www.states101.com" + flagResponse.substring(cutLeft, cutRight);
    }

    private String fetch(String urlString) {
        StringBuilder responseBuilder = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Error fetching data from URL: " + urlString);
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }
}
