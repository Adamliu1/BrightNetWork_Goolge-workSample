package com.google;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private final Printer myPrinter;
  private final HashMap<String, Video> statusMap = new HashMap<>();
  private final HashMap<String, VideoPlaylist> videoListMap = new HashMap<>();
  //flag vidID, reason
  private final HashMap<String, String> flagVidMap = new HashMap<>();

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    initStatusMap();
    myPrinter = new Printer();
  }

  private void initStatusMap() {
    statusMap.put("PLAY", null);
    statusMap.put("PAUSE", null);
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    myPrinter.printVideos(videoLibrary.getVideos(), flagVidMap);
  }

  private void vidListSorter(List<Video> videoList) {
    if (videoList == null) {
      return;
    }
    videoList.sort(Comparator.comparing(Video::getTitle));
  }

  private boolean stopPlayingAux() {
    if (statusMap.get("PLAY") != null || statusMap.get("PAUSE") != null) {
      Video target;
      if (statusMap.get("PLAY") != null) {
        target = statusMap.get("PLAY");
      } else {
        target = statusMap.get("PAUSE");
      }

      System.out.printf("Stopping video: %s\n", target.getTitle());
      statusMap.put("PLAY", null);
      statusMap.put("PAUSE", null);
      return true;
    }
      return false;
  }

  private boolean checkFlag(String videoId, String msg) {
    if (flagVidMap.containsKey(videoId)) {
      System.out.printf("%s (reason: %s)\n", msg, flagVidMap.get(videoId));
      return true;
    }
    return false;
  }

  public void playVideo(String videoId) {
    //get video object from videoLibrary
    Video targetVideo = videoLibrary.getVideo(videoId);
    if (targetVideo != null) {
      //check flag
      String flagMsg = "Cannot play video: Video is currently flagged";
      if (checkFlag(videoId, flagMsg)) {
        return;
      }
      stopPlayingAux();
      System.out.println("Playing video: " + targetVideo.getTitle());
      //update status
      statusMap.put("PLAY", targetVideo);
    } else {
      System.out.println("Cannot play video: Video does not exist");
    }
  }

  public void stopVideo() {
    if (!stopPlayingAux()) {
      System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  private void playRandomVideo_Aux(List<Video> videoList) {
    if (videoList == null) {
      return;
    }
    Random randomMethod = new Random();
    int randomIndex = randomMethod.nextInt(videoList.size());
    Video randomVideo = videoList.get(randomIndex);
    playVideo(randomVideo.getVideoId());
  }

  //Exclude videos with flag
  private List<Video> playRandomAux(List<Video> videoList) {
    List<String> flaggedIdList = new ArrayList<>(flagVidMap.keySet());
    List<Video> flaggedVideoList = new ArrayList<>();

    for (String curId : flaggedIdList) {
      flaggedVideoList.add(videoLibrary.getVideo(curId));
    }
    return videoList.stream()
            .filter(element -> !flaggedVideoList.contains(element))
            .collect(Collectors.toList());
  }

  public void playRandomVideo() {
    // get random videoId
    List<Video> videoList = videoLibrary.getVideos();
    List<Video> filteredList = playRandomAux(videoList);

    if (filteredList.size() == 0) {
      System.out.println("No videos available");
    } else {
      playRandomVideo_Aux(filteredList);
    }
  }

  private void pauseVideoAux() {
    Video targetVideo = statusMap.get("PLAY");
    statusMap.put("PLAY", null);
    statusMap.put("PAUSE", targetVideo);
    System.out.printf("Pausing video: %s\n", targetVideo.getTitle());
  }

  // return false if there's no video playing or pausing. Else, return true.
  private boolean checkPlayAndPause() {
    return statusMap.get("PLAY") != null || statusMap.get("PAUSE") != null;
  }

  public void pauseVideo() {
    if (checkPlayAndPause()) {
      if (statusMap.get("PLAY") != null) {
        pauseVideoAux(); // there's an action on statusMap
      } else {
        Video targetVideo = statusMap.get("PAUSE");
        System.out.printf("Video already paused: %s\n", targetVideo.getTitle());
      }
    } else {
      System.out.println("Cannot pause video: No video is currently playing");
    }
  }

  private void continueVideoAux() {
    Video targetVideo = statusMap.get("PAUSE");
    statusMap.put("PAUSE", null);
    statusMap.put("PLAY", targetVideo);
    System.out.printf("Continuing video: %s\n", targetVideo.getTitle());
  }

  public void continueVideo() {
      if (checkPlayAndPause()) {
        if (statusMap.get("PLAY") != null) {
          System.out.println("Cannot continue video: Video is not paused");
        } else {
          continueVideoAux();
        }
      } else {
        System.out.println("Cannot continue video: No video is currently playing");
      }
  }

  // check if video is exist in library
  private boolean isTargetVideoExist(Video targetVideo) {
    return targetVideo == null;
  }

  public void showPlaying() {
    if (isTargetVideoExist(statusMap.get("PLAY")) && isTargetVideoExist(statusMap.get("PAUSE"))) {
      System.out.println("No video is currently playing");
    } else if (statusMap.get("PLAY") != null) {
      System.out.printf("Currently playing: %s \n", myPrinter.printVideo(statusMap.get("PLAY")));
    } else {
      System.out.printf("Currently playing: %s - PAUSED \n", myPrinter.printVideo(statusMap.get("PAUSE")));
    }
  }

  public void createPlaylist(String playlistName) {
    if (playlistName == null) {
      return;
    }
    //check if listName contains white space
    if (playlistName.contains(" ")) {
      //remove white space
      System.out.println("PlayListName should not contain white space");
      return;
    }
    //ignore case
    String playlistName_lowered = playlistName.toLowerCase();

    //check if playList already exist.
    if (videoListMap.containsKey(playlistName_lowered)) {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    } else {
      videoListMap.put(playlistName_lowered, new VideoPlaylist());
      System.out.printf("Successfully created new playlist: %s\n", playlistName);
    }
  }

  private void addVideoToPlayList_Aux(String listName, String msg) {
    System.out.printf("Cannot add video to %s: %s\n", listName, msg);
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    if (playlistName == null) {
      return;
    }
    VideoPlaylist targetList = videoListMap.get(playlistName.toLowerCase());
    Video targetVideo = videoLibrary.getVideo(videoId);

    if (targetList == null) {
      addVideoToPlayList_Aux(playlistName, "Playlist does not exist");
    } else if (isTargetVideoExist(targetVideo)) {
      addVideoToPlayList_Aux(playlistName, "Video does not exist");
    } else {
      String flagMsg = String.format("Cannot add video to %s: Video is currently flagged", playlistName);
      if (checkFlag(videoId, flagMsg)) {
        return;
      }
      targetList.addItem(playlistName, targetVideo);
    }
  }

  private void showAllPlayLists_Aux(Set<String> playlistsLabels) {
    List<String> listsStr = new ArrayList<>(playlistsLabels);
    Collections.sort(listsStr);
    for (String curLabel : listsStr) {
      System.out.println(curLabel);
    }
  }

  public void showAllPlaylists() {
    if (videoListMap.isEmpty()) {
      System.out.println("No playlists exist yet");
    } else {
      System.out.println("Showing all playlists:");
      Set<String> playlistsLabels = videoListMap.keySet();
      showAllPlayLists_Aux(playlistsLabels);
    }
  }

  public void showPlaylist(String playlistName) {
    if (playlistName == null) {
      return;
    }
    VideoPlaylist targetList = videoListMap.get(playlistName.toLowerCase());
    if (targetList == null) {
      System.out.printf("Cannot show playlist %s: Playlist does not exist\n", playlistName);
    } else {
      System.out.printf("Showing playlist: %s\n", playlistName);
      targetList.printList(flagVidMap);
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    if (playlistName == null) {
      return;
    }
    VideoPlaylist targetList = videoListMap.get(playlistName.toLowerCase());
    Video targetVideo = videoLibrary.getVideo(videoId);

    if (targetList == null) {
      System.out.printf("Cannot remove video from %s: Playlist does not exist\n", playlistName);
    } else if (isTargetVideoExist(targetVideo)) {
      System.out.printf("Cannot remove video from %s: Video does not exist\n", playlistName);
    } else {
      targetList.removeItem(playlistName, targetVideo);
    }
  }

  public void clearPlaylist(String playlistName) {
    if (playlistName == null) {
      return;
    }
    VideoPlaylist targetList = videoListMap.get(playlistName.toLowerCase());
    if (targetList == null) {
      System.out.printf("Cannot clear playlist %s: Playlist does not exist\n", playlistName);
    } else {
      targetList.clearItem(playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    if (playlistName == null) {
      return;
    }
    VideoPlaylist targetList = videoListMap.get(playlistName.toLowerCase());
    if (targetList == null) {
      System.out.printf("Cannot delete playlist %s: Playlist does not exist\n", playlistName);
    } else {
      videoListMap.remove(playlistName.toLowerCase());
      System.out.printf("Deleted playlist: %s\n", playlistName);
    }
  }

  private void showSearchResult(List<Video> resultList) {
    if (resultList == null) {
      return;
    }
    int counter = 0;
    for (Video curVideo : resultList) {
      //option starts from 1
      System.out.printf("%d) %s\n", counter+1, myPrinter.printVideo(curVideo));
      counter++;
    }
    System.out.print("Would you like to play any of the above? If yes, specify the number of the video.\n" +
            "If your answer is not a valid number, we will assume it's a no.\n");
  }

  private void processAction(List<Video> resultList, String input) {
    int choice;
    try {
      choice = Integer.parseInt(input);
      if (choice <= 0 || choice > resultList.size()) {
        return;
      }
      Video targetVideo = resultList.get(choice-1);
      playVideo(targetVideo.getVideoId());
    } catch (NumberFormatException e) {
      //do nothing
    }
  }

  private void actionResult(List<Video> resultList, String keyWord) {
    if (resultList == null) {
      return;
    }
    if (resultList.size() == 0) {
      System.out.printf("No search results for %s\n", keyWord);
      return;
    }
    System.out.printf("Here are the results for %s:\n", keyWord);
    showSearchResult(resultList);

    //get input from user
    var scanner = new Scanner(System.in);
    var input = scanner.nextLine();
    processAction(resultList, input);
  }

  //Use regex to match keyword
  public void searchVideos(String searchTerm) {
    Pattern pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
    List<Video> videoList = videoLibrary.getVideos();
    List<Video> resultList = new ArrayList<>();

    for (Video curVideo : videoList) {
      Matcher matcher = pattern.matcher(curVideo.getTitle());
      if (matcher.find()) {
        if (!flagVidMap.containsKey(curVideo.getVideoId())) {
          resultList.add(curVideo);
        }
      }
    }
    //sort resultList
    vidListSorter(resultList);
    actionResult(resultList, searchTerm);
  }

  public void searchVideosWithTag(String videoTag) {
    Pattern pattern = Pattern.compile(videoTag, Pattern.CASE_INSENSITIVE);
    List<Video> videoList = videoLibrary.getVideos();
    List<Video> resultList = new ArrayList<>();

    for (Video curVideo : videoList) {
      Matcher matcher = pattern.matcher(curVideo.getTags().toString());
      if (matcher.find()) {
        if (!flagVidMap.containsKey(curVideo.getVideoId())) {
          resultList.add(curVideo);
        }
      }
    }
    //sort resultList
    vidListSorter(resultList);
    actionResult(resultList, videoTag);
  }

  public void flagVideo(String videoId) {
    flagVideo(videoId, "Not supplied");
  }

  public void flagVideo(String videoId, String reason) {
    //check if video exist
    Video targetVideo = videoLibrary.getVideo(videoId);

    //check if it's playing
    if (statusMap.containsValue(targetVideo)) {
      stopVideo();
    }

    if (isTargetVideoExist(targetVideo)) {
      System.out.println("Cannot flag video: Video does not exist");
    } else if (flagVidMap.containsKey(videoId)) {
      System.out.println("Cannot flag video: Video is already flagged");
    } else {
      flagVidMap.put(videoId, reason);
      System.out.printf("Successfully flagged video: %s (reason: %s)\n", targetVideo.getTitle(),reason);
    }
  }

  public void allowVideo(String videoId) {
    //check if video exist
    Video targetVideo = videoLibrary.getVideo(videoId);

    if (isTargetVideoExist(targetVideo)) {
      System.out.println("Cannot remove flag from video: Video does not exist");
    } else if (flagVidMap.containsKey(videoId)) {
      flagVidMap.remove(videoId);
      System.out.printf("Successfully removed flag from video: %s\n", targetVideo.getTitle());
    } else {
      System.out.println("Cannot remove flag from video: Video is not flagged");
    }
  }

}