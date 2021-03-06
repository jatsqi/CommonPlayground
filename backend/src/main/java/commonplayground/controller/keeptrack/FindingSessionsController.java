package commonplayground.controller.keeptrack;

import commonplayground.controller.PlaceAPI;
import commonplayground.model.Session;
import commonplayground.model.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class FindingSessionsController {

    private final SessionRepository sessionRepository;

    private ArrayList<Session> matchingSessions = new ArrayList<>();
    private String genre;
    private String place;
    private HashMap<String, String> onlineGenres = new HashMap<>();
    private HashMap<String, String> offlineGenres = new HashMap<>();
    private PlaceAPI placeAPI= new PlaceAPI();

    @Autowired
    public FindingSessionsController(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;

    }

    @RequestMapping("/findSessions")
    public ArrayList<Session> postNewSession(@RequestParam(value = "place", defaultValue = "not given") String place,
                                             @RequestParam(value = "genre", defaultValue = "not given") String genre,
                                             @RequestParam(value = "isOnline", defaultValue = "not given") String isOnline) {
        this.place= place;

        if ("online".equals(isOnline.toLowerCase())){
            fillOnlineHashMaps();
            this.genre= onlineGenres.get(genre);
            matchingSessions= matchOnlineSessions();
        }else if ("offline".equals(isOnline.toLowerCase())){
            fillOfflineHashMap();
            this.genre= offlineGenres.get(genre);
            matchingSessions= matchOfflineSessions();
        }
        return matchingSessions;
    }

    private void fillOnlineHashMaps() {
        onlineGenres.put("0", "any");
        onlineGenres.put("1", "MMO RPG");
        onlineGenres.put("2", "Shooter");
        onlineGenres.put("3", "MOBA");
        onlineGenres.put("4", "Strategy");
    }

    private void fillOfflineHashMap(){
        offlineGenres.put("0", "any");
        offlineGenres.put("1", "Board Game");
        offlineGenres.put("2", "Card Game");
        offlineGenres.put("3", "Pen and Paper");
        offlineGenres.put("4", "Outdoors Activity");
    }

    private ArrayList<Session> matchOnlineSessions() {
        ArrayList<Session> onlineSessions;
        onlineSessions= sessionRepository.findAllByIsOnline("online");
        onlineSessions= matchSessionsByGenre(onlineSessions);

        return onlineSessions;
    }

    private ArrayList<Session> matchOfflineSessions() {
        ArrayList<Session> offlineSessions;
        offlineSessions= sessionRepository.findAllByIsOnline("offline");
        offlineSessions= matchSessionsByPlace(offlineSessions);
        offlineSessions= matchSessionsByGenre(offlineSessions);
        return offlineSessions;
    }

    private ArrayList<Session> matchSessionsByPlace(ArrayList<Session> sessionsToMatch) {
        ArrayList<Session> sessionsByPlace = new ArrayList<>();
        String cityToMatch= placeAPI.sendRequestToPlaceAPI(place);

        for (Session session: sessionsToMatch) {
            String cityOfSession= session.getPlace();
            if (cityOfSession.equals(cityToMatch)){
                sessionsByPlace.add(session);
            }
        }
        return sessionsByPlace;
    }

    private ArrayList<Session> matchSessionsByGenre(ArrayList<Session> sessionsToMatch) {
        ArrayList<Session> matchingSessions = new ArrayList<>();
        if("any".equals(genre)){
            matchingSessions= sessionsToMatch;
        }else {
            for (Session session: sessionsToMatch) {
                if (session.getGenre().equals(genre)){
                    matchingSessions.add(session);
                }
            }
        }
        return matchingSessions;
    }
}