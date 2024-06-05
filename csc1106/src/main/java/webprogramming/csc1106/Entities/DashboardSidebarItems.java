package webprogramming.csc1106.Entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
@Table(name = "dashboard_sidebar_items")
public class DashboardSidebarItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "name" , nullable = false)
    private String name;

    @Column(name = "icon", nullable = true)
    private String icon;

    @Column(name = "url_redirection", nullable = true)
    private String urlRedirection;

    public DashboardSidebarItems() {}

    public DashboardSidebarItems(Integer index, String name, String icon, String urlRedirection) {
        this.index = index;
        this.name = name;
        this.icon = icon;
        this.urlRedirection = urlRedirection;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrlRedirection() {
        return urlRedirection;
    }

    public void setUrlRedirection(String urlRedirection) {
        this.urlRedirection = urlRedirection;
    }


}
