package webprogramming.csc1106.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional; // Add this import statement for Optional
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import webprogramming.csc1106.Entities.DashboardSidebarItems;
import webprogramming.csc1106.Repositories.DashboardSidebarItemsRepository;

@RestController
public class DashboardRestController {
    private final DashboardSidebarItemsRepository dashboardSidebarItemsRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardRestController.class);

    @Autowired
    public DashboardRestController(DashboardSidebarItemsRepository dashboardSidebarItemsRepository) {
        this.dashboardSidebarItemsRepository = dashboardSidebarItemsRepository;
    }

    @GetMapping("/dashboard/dashboard-sidebar-items")
    public List<DashboardSidebarItems> getAllSidebarItems() {
        return dashboardSidebarItemsRepository.findAll();
    }
}
