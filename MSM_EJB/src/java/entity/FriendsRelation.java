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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nemo
 */
@Entity
@Table(name = "FRIENDS_RELATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FriendsRelation.findAll", query = "SELECT f FROM FriendsRelation f"),
    @NamedQuery(name = "FriendsRelation.findById", query = "SELECT f FROM FriendsRelation f WHERE f.id = :id")})
public class FriendsRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "FIRST_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Users firstId;
    @JoinColumn(name = "SECOND_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Users secondId;

    public FriendsRelation() {
    }

    public FriendsRelation(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getFirstId() {
        return firstId;
    }

    public void setFirstId(Users firstId) {
        this.firstId = firstId;
    }

    public Users getSecondId() {
        return secondId;
    }

    public void setSecondId(Users secondId) {
        this.secondId = secondId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
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
        final FriendsRelation other = (FriendsRelation) obj;
        if (this.firstId != other.firstId && (this.firstId == null || !this.firstId.equals(other.firstId))) {
            return false;
        }
        if (this.secondId != other.secondId && (this.secondId == null || !this.secondId.equals(other.secondId))) {
            return false;
        }
        return true;
    }

    public String getFullString() {
        return "entity.FriendsRelation{" + "firstId=" + firstId + ", secondId=" + secondId + '}';
    }
    
    @Override
    public String toString() {
        return "Relation between: " + firstId + " and" + secondId;
    }
    
}
