/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
    @NamedQuery(name = "FriendsRelation.findByFirstId", query = "SELECT f FROM FriendsRelation f WHERE f.firstId = :firstId")})
public class FriendsRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "FIRST_ID")
    private Integer firstId;
    @JoinColumn(name = "FIRST_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users users;
    @JoinColumn(name = "SECOND_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Users secondId;

    public FriendsRelation() {
    }

    public FriendsRelation(Integer firstId) {
        this.firstId = firstId;
    }

    public Integer getFirstId() {
        return firstId;
    }

    public void setFirstId(Integer firstId) {
        this.firstId = firstId;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Users getSecondId() {
        return secondId;
    }

    public void setSecondId(Users secondId) {
        this.secondId = secondId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.firstId != null ? this.firstId.hashCode() : 0);
        hash = 53 * hash + (this.secondId != null ? this.secondId.hashCode() : 0);
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
        return "entity.FriendsRelation{" + "firstId=" + firstId + ", users=" + users + ", secondId=" + secondId + '}';
    }
    
    @Override
    public String toString() {
        return "Relation between: " + firstId + " and" + secondId;
    }
}
