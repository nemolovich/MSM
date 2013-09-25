/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import bean.ConnectedUser;
import bean.Utils;
import bean.log.ApplicationLogger;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nemo
 */
@Entity
@Table(name = "USERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
    @NamedQuery(name = "Users.findByMail", query = "SELECT u FROM Users u WHERE u.mail = :mail"),
    @NamedQuery(name = "Users.findByName", query = "SELECT u FROM Users u WHERE u.name = :name"),
    @NamedQuery(name = "Users.findByFirstname", query = "SELECT u FROM Users u WHERE u.firstname = :firstname"),
    @NamedQuery(name = "Users.findByRights", query = "SELECT u FROM Users u WHERE u.rights = :rights"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password")})
public class Users implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    @Column(name = "MAIL")
    private String mail;
    @Basic(optional = false)
    @NotNull
    @Size(max = 30)
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(max = 30)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PASSWORD")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RIGHTS")
    private String rights = Utils.UNKNOWN_RIGHTS;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstId")
    private List<FriendsRelation> friendsRelationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondId")
    private List<FriendsRelation> friendsRelationList1;

    public Users() {
    }

    public Users(Users user)
    {
        this.id=user.id;
        this.mail=user.mail;
        this.name=user.name;
        this.firstname=user.firstname;
        this.password=user.password;
        this.rights=user.rights;
    }

    public Users(Integer id) {
        this.id = id;
    }

    public Users(Integer id, String mail, String name, String firstname, String password, String rights) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.firstname = firstname;
        this.password = password;
        this.rights = rights;
    }

    public Integer getId() {
        return this.id==null?-1:this.id;
    }
    
    public boolean getSleeping()
    {
        return !ConnectedUser.USERS_LIST().contains(this);
    }

    public String getFieldValue(String fieldName)
    {
        try
        {
            Method m=Users.class.getMethod("get"+fieldName);
            String res=(String) m.invoke((Object)this);
            return res;
        }
        catch (NoSuchMethodException ex)
        {
            ApplicationLogger.writeError("La méthode \"get"+fieldName+"\" n'a pas"+
                    " été trouvée pour la classe \""+Users.class.getName()+"\"", ex);
        }
        catch (SecurityException ex)
        {
            ApplicationLogger.writeError("La méthode \"get"+fieldName+"\" n'est pas"+
                    " accessible pour la classe \""+Users.class.getName()+"\"", ex);
        }
        catch (IllegalAccessException ex)
        {
            ApplicationLogger.writeError("La méthode \"get"+fieldName+"\" est"+
                    " interdite d'accès pour la classe \""+Users.class.getName()+"\"", ex);
        }
        catch (IllegalArgumentException ex)
        {
            ApplicationLogger.writeError("Les arguments pour la méthode \"get"+
                    fieldName+"\" sont incorrects pour la classe \""+
                    Users.class.getName()+"\"", ex);
        }
        catch (InvocationTargetException ex)
        {
            ApplicationLogger.writeError("La méthode \"get"+fieldName+"\" n'est"+
                    " pas applicable pour la classe \""+Users.class.getName()+"\"", ex);
        }
        return null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setRights(String rights)
    {
        if(Utils.getEnumRights().contains(rights))
        {
        this.rights = rights;
    }
        else
        {
//            ApplicationLogger.writeWarning("Les droits ne sont pas corrects");
        }
    }

    public String getRights() {
        return this.rights;
    }

    public void setEncryptedPassword(byte[] md5)
    {
        this.password=new String(md5/*,"UTF-8"*/);
    }

    public void setPassword(String password)
    {
        if(password.isEmpty())
        {
//            ApplicationLogger.writeWarning("Utilisation de l'ancien mot de passe");
            return;
        }
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException ex)
        {
//            ApplicationLogger.writeError("Cryptage du mot de passe imposible", ex);
            this.password="nopass";
            return;
        }
        md.update(password.getBytes(/*"UTF-8"*/));
        byte[] md5 = md.digest();
        this.password=new String(md5/*,"UTF-8"*/);
    }

    @XmlTransient
    public List<FriendsRelation> getFriendsRelationList() {
        return friendsRelationList;
    }

    public void setFriendsRelationList(List<FriendsRelation> friendsRelationList) {
        this.friendsRelationList = friendsRelationList;
    }

    @XmlTransient
    public List<FriendsRelation> getFriendsRelationList1() {
        return friendsRelationList1;
    }

    public void setFriendsRelationList1(List<FriendsRelation> friendsRelationList1) {
        this.friendsRelationList1 = friendsRelationList1;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.mail != null ? this.mail.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.firstname != null ? this.firstname.hashCode() : 0);
        hash = 89 * hash + (this.rights != null ? this.rights.hashCode() : 0);
        hash = 89 * hash + (this.password != null ? this.password.hashCode() : 0);
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
        final Users other = (Users) obj;
        if ((this.mail == null) ? (other.mail != null) : !this.mail.equals(other.mail)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.firstname == null) ? (other.firstname != null) : !this.firstname.equals(other.firstname)) {
            return false;
        }
        if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password)) {
            return false;
        }
        if ((this.rights == null) ? (other.rights != null) : !this.rights.equals(other.rights)) {
            return false;
        }
        return true;
    }

    public String getFullString()
    {
        return "entity.Users{" + "id=" + id + ", mail=" + mail + ", name=" + name + ", firstname=" + firstname + ", rights=" + rights + '}';
    }
    
    @Override
    public String toString()
    {
        return this.firstname+" "+this.name+" ["+this.rights+"](id="+this.id+")";
    }
    
}
