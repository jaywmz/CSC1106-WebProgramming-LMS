package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.DashboardSidebarItems;

public interface DashboardSidebarItemsRepository extends JpaRepository<DashboardSidebarItems, Integer>{    
}