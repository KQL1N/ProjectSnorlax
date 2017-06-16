package nl.muar.sa.projectsnorlax.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by androideng on 16/06/17.
 */

public class Restaurant {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Date openingTime;
    private Date closingTime;
    private List<MenuItem> menuItems = new ArrayList<MenuItem>();


    public Restaurant(Long id, String name, Double latitude, Double longitude, Date openingTime, Date closingTime) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Restaurant that = (Restaurant) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getLatitude() != null ? !getLatitude().equals(that.getLatitude()) : that.getLatitude() != null)
            return false;
        if (getLongitude() != null ? !getLongitude().equals(that.getLongitude()) : that.getLongitude() != null)
            return false;
        if (getOpeningTime() != null ? !getOpeningTime().equals(that.getOpeningTime()) : that.getOpeningTime() != null)
            return false;
        return getClosingTime() != null ? getClosingTime().equals(that.getClosingTime()) : that.getClosingTime() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getOpeningTime() != null ? getOpeningTime().hashCode() : 0);
        result = 31 * result + (getClosingTime() != null ? getClosingTime().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                '}';
    }
}
