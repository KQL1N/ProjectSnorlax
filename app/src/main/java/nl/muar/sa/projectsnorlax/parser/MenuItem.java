package nl.muar.sa.projectsnorlax.parser;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by androideng on 16/06/17.
 */

public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String section;

    private Date date;

    public MenuItem(Long id, String name, String description, BigDecimal price, String section, Date date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.section = section;
        this.date = date;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        if (getId() != null ? !getId().equals(menuItem.getId()) : menuItem.getId() != null)
            return false;
        if (getName() != null ? !getName().equals(menuItem.getName()) : menuItem.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(menuItem.getDescription()) : menuItem.getDescription() != null)
            return false;
        if (getPrice() != null ? !getPrice().equals(menuItem.getPrice()) : menuItem.getPrice() != null)
            return false;
        if (getSection() != null ? !getSection().equals(menuItem.getSection()) : menuItem.getSection() != null)
            return false;
        return getDate() != null ? getDate().equals(menuItem.getDate()) : menuItem.getDate() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        result = 31 * result + (getSection() != null ? getSection().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", section='" + section + '\'' +
                ", date=" + date +
                '}';
    }
}
