package com.google;

import java.util.ArrayList;
import java.util.HashMap;

/** A class used to represent a Playlist */
class VideoPlaylist {

    private final ArrayList<Video> content = new ArrayList<>();
    private final Printer myPrinter = new Printer();

    public void addItem(String listName, Video myVideo) {
        if (content.contains(myVideo)) {
            System.out.printf("Cannot add video to %s: Video already added\n", listName);
        } else {
            content.add(myVideo);
            System.out.printf("Added video to %s: %s\n", listName, myVideo.getTitle());
        }
    }


    public void printList(HashMap<String, String> flagMap) {
        if (content.isEmpty()) {
            System.out.println("No videos here yet");
        } else {
            for (Video curVideo : content) {
                if (flagMap.containsKey(curVideo.getVideoId())) {
                    System.out.println(myPrinter.printVideoFlagged(curVideo, flagMap));
                } else {
                    System.out.println(myPrinter.printVideo(curVideo));
                }
            }
        }
    }

    public void removeItem(String listName, Video myVideo) {
        if (!content.contains(myVideo)) {
            System.out.printf("Cannot remove video from %s: Video is not in playlist\n", listName);
        } else {
            content.remove(myVideo);
            System.out.printf("Removed video from %s: %s\n", listName, myVideo.getTitle());
        }
    }

    public void clearItem(String listName) {
        content.clear();
        System.out.printf("Successfully removed all videos from %s\n", listName);
    }

}
