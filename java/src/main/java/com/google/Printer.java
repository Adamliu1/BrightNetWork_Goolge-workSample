package com.google;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Printer {

    public String printVideo(Video targetVideo) {
        if (targetVideo == null) {
            return "";
        }
        String tags = targetVideo.getTags().toString().replaceAll(",", "");
        return String.format("%s (%s) %s", targetVideo.getTitle(), targetVideo.getVideoId(), tags);
    }

    public String printVideoFlagged(Video targetVideo, HashMap<String, String> flagVidMap) {
        if (targetVideo == null) {
            return "";
        }
        String tags = targetVideo.getTags().toString().replaceAll(",", "");
        return String.format("%s (%s) %s - FLAGGED (reason: %s)", targetVideo.getTitle(), targetVideo.getVideoId(), tags,
                flagVidMap.get(targetVideo.getVideoId()));
    }

    // Aux function for printing all videos. Given in list
    public void printVideos(List<Video> listOfVid, HashMap<String, String> flagVidMap) {
        if (listOfVid == null) {
            return;
        }
        // sort listOfVid in lexico. order
        listOfVid.sort(Comparator.comparing(Video::getTitle));
        for (Video curVideo : listOfVid) {
            //checkFlag
            if (flagVidMap.containsKey(curVideo.getVideoId())) {
                System.out.println(printVideoFlagged(curVideo, flagVidMap));
                continue;
            }
            System.out.println(printVideo(curVideo));
        }
    }

}
