package com.example.ticketmaster.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "Page not found (404)");
                model.addAttribute("message", "The page you are looking for doesn't exist.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "Internal server error (500)");
                model.addAttribute("message", "Something went wrong on our server.");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "Access denied (403)");
                model.addAttribute("message", "You don't have permission to access this page.");
            } else {
                model.addAttribute("error", "Error " + statusCode);
                model.addAttribute("message", "An unexpected error occurred.");
            }
        } else {
            model.addAttribute("error", "Unknown error");
            model.addAttribute("message", "An unexpected error occurred.");
        }

        return "error";
    }
}