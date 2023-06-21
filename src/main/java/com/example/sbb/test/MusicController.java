package com.example.sbb.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Controller
public class MusicController {

    private final WebClient webClient;

    public MusicController() {
        this.webClient = WebClient.builder().baseUrl("https://api.manana.kr/karaoke.json").build();
    }

    // 신곡 리스트
    @GetMapping("/music")
    public String getNewSong(Model model) {
        String path = "https://api.manana.kr/karaoke.json";

        String responseData = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        List<MusicData> songs = null;
        try {
            songs = objectMapper.readValue(responseData, new TypeReference<List<MusicData>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("songs", songs);
        return "new_songs";
    }

    // 곡 검색
    @PostMapping("/music")
    public String searchPostMusic(Model model, @RequestParam("inputValue") String value) {
        String path = "https://api.manana.kr/karaoke/song/" + value + ".json";

        String responseData = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        List<MusicData> songs = null;
        try {
            songs = objectMapper.readValue(responseData, new TypeReference<List<MusicData>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("songs", songs);
        return "search_song";
    }

    // 곡 검색
    @GetMapping("/music/{name}")
    public String searchGetMusic(Model model, @PathVariable("name") String name) {
        String path = "https://api.manana.kr/karaoke/song/" + name + ".json";

        String responseData = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        List<MusicData> songs = null;
        try {
            songs = objectMapper.readValue(responseData, new TypeReference<List<MusicData>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("songs", songs);
        return "search_song";
    }

    public static class MusicData {
        private String brand;
        private String no;
        private String title;
        private String singer;
        private String composer;
        private String lyricist;
        private String release;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getComposer() {
            return composer;
        }

        public void setComposer(String composer) {
            this.composer = composer;
        }

        public String getLyricist() {
            return lyricist;
        }

        public void setLyricist(String lyricist) {
            this.lyricist = lyricist;
        }

        public String getRelease() {
            return release;
        }

        public void setRelease(String release) {
            this.release = release;
        }
    }

}
