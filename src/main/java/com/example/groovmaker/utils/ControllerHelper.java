package com.example.groovmaker.utils;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ControllerHelper {

    public static ModelAndView addPaginatedViews (ModelAndView modelAndView, Page<Track> paginatedTracks){
        int totalPages = paginatedTracks.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }

        return modelAndView;
    }


}
