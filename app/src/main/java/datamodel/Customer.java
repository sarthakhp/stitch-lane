package datamodel;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "customers", indices = {@Index(value = {"name"}, unique = true)})
public class Customer {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "created")
    private Long created;

    @ColumnInfo(name = "modified")
    private Long modified;

    public Customer(@NonNull String name, @NonNull String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Ignore
    public Customer(@NonNull String name) {
        this.name = name;
        this.phoneNumber = "";
    }

    public Customer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }


    // Getter and Setter methods
}