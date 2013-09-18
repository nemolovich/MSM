/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nemo
 */
@Entity
@Table(name = "ALBUMS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Albums.findAll", query = "SELECT a FROM Albums a"),
    @NamedQuery(name = "Albums.findById", query = "SELECT a FROM Albums a WHERE a.id = :id"),
    @NamedQuery(name = "Albums.findByIdUsr", query = "SELECT a FROM Albums a WHERE a.idUsr = :idUsr"),
    @NamedQuery(name = "Albums.findByName", query = "SELECT a FROM Albums a WHERE a.name = :name"),
    @NamedQuery(name = "Albums.findByVisibility", query = "SELECT a FROM Albums a WHERE a.visibility = :visibility")})
public class Albums implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_USR")
    private int idUsr;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "VISIBILITY")
    private int visibility;

    public Albums() {
    }

    public Albums(Integer id) {
        this.id = id;
    }

    public Albums(Integer id, int idUsr, String name, int visibility) {
        this.id = id;
        this.idUsr = idUsr;
        this.name = name;
        this.visibility = visibility;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdUsr() {
        return idUsr;
    }

    public void setIdUsr(int idUsr) {
        this.idUsr = idUsr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.idUsr;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + this.visibility;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Albums other = (Albums) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.idUsr != other.idUsr) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.visibility != other.visibility) {
            return false;
        }
        return true;
    }
    
    public String getFullString() {
        return "Albums{" + "id=" + id + ", idUsr=" + idUsr + ", name=" + name + ", visibility=" + visibility + '}';
    }

    @Override
    public String toString() {
        return this.name;
    }
    
}
