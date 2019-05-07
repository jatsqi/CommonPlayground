package commonplayground.controller.cucumber.api;

import commonplayground.model.Session;
import commonplayground.model.TestData;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static io.restassured.RestAssured.get;


public class PostSessionStepDefinitions {

    TestData testData = new TestData();

    @When("^I post a new Session with correct Data$")
    public void iPostANewSessionWithCorrectData(){
        for (Session testSession : testData.getTestSessions()
        ) {
            try {
                String body =
                        "title=" + URLEncoder.encode(testSession.getTitle(), "UTF-8") + "&" +
                                "description=" + URLEncoder.encode(testSession.getDescription(), "UTF-8") + "&" +
                                "game=" + URLEncoder.encode(testSession.getGame(), "UTF-8") + "&" +
                                "place=" + URLEncoder.encode(testSession.getPlace(), "UTF-8") + "&" +
                                "date=" + URLEncoder.encode(testSession.getDate(), "UTF-8") + "&" +
                                "time=" + URLEncoder.encode(testSession.getTime(), "UTF-8") + "&" +
                                "numberOfPlayers=" + URLEncoder.encode(String.valueOf(testSession.getNumberOfPlayers()), "UTF-8") + "&" +
                                "idOfHost=" + URLEncoder.encode(String.valueOf(testSession.getIdOfHost()), "UTF-8") + "&" +
                                "genre=" + URLEncoder.encode(testSession.getGenre(), "UTF-8") + "&" +
                                "isOnline=" + URLEncoder.encode(testSession.getIsOnline(), "UTF-8");

                URL url = new URL("http://localhost:8080/postNewSession");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                for (String line; (line = reader.readLine()) != null; ) {
                    System.out.println(line);
                }

                writer.close();
                reader.close();
            } catch (Exception e) {
                assert false;
            }
        }
    }

    @Then("^There should be my PostedSession with correct Data$")
    public void thereShouldBeMyPostedSessionWithCorrectData(){
        for (Session testSession : testData.getTestSessions()
        ) {
            assert get("/getSessionList").jsonPath().getList("title").contains(testSession.getTitle());
            assert get("/getSessionList").jsonPath().getList("description").contains(testSession.getDescription());
            assert get("/getSessionList").jsonPath().getList("game").contains(testSession.getGame());
            assert get("/getSessionList").jsonPath().getList("place").contains(testSession.getPlace());
            assert get("/getSessionList").jsonPath().getList("date").contains(testSession.getDate());
            assert get("/getSessionList").jsonPath().getList("time").contains(testSession.getTime());
            assert get("/getSessionList").jsonPath().getList("numberOfPlayers").contains(testSession.getNumberOfPlayers());
            assert get("/getSessionList").jsonPath().getList("idOfHost").contains(testSession.getIdOfHost());
            assert get("/getSessionList").jsonPath().getList("genre").contains(testSession.getGenre());
            assert get("/getSessionList").jsonPath().getList("isOnline").contains(testSession.getIsOnline());
        }
    }
}
