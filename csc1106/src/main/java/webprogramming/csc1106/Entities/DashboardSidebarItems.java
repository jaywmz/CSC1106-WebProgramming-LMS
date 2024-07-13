package webprogramming.csc1106.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "dashboard_sidebar_items")
public class DashboardSidebarItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemid", nullable = false)
    private Integer id;

    @Column(name = "name" , nullable = false)
    private String name;

    @Column(name = "icon", nullable = true)
    private String icon;

    @Column(name = "url_redirection", nullable = true)
    private String urlRedirection;

    public DashboardSidebarItems() {}

    public DashboardSidebarItems(Integer index, String name, String icon, String urlRedirection) {
        this.id = index;
        this.name = name;
        this.icon = icon;
        this.urlRedirection = urlRedirection;
    }

    public Integer getIndex() {
        return id;
    }

    public void setIndex(Integer index) {
        this.id = index;
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
