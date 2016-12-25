package flutterwave.com.rave.models;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

/**
 * Created by Shittu on 19/12/2016.
 */

public abstract class BaseRequestData {
    private String PBFPubKey;
    private String amount;
    private String email;
    private String IP;
    private String txRef;
    private String country;
    private String currency;
    private String firstname;
    private String lastname;
    private String narration;
    private List<Map<String, Object>> meta;

    public BaseRequestData(String PBFPubKey, String amount, String email, String IP, String txRef,
                           String country, String currency, String firstname, String lastname,
                           String narration, List<Map<String, Object>> meta) {
        this.PBFPubKey = PBFPubKey;
        this.amount = amount;
        this.email = email;
        this.IP = IP;
        this.txRef = txRef;
        this.country = country;
        this.currency = currency;
        this.firstname = firstname;
        this.lastname = lastname;
        this.narration = narration;
        this.meta = meta;
    }

    public String getPBFPubKey() {
        return PBFPubKey;
    }

    public String getamount() {
        return amount;
    }

    public String getemail() {
        return email;
    }

    public String getIP() {
        return IP;
    }

    public String gettxRef() {
        return txRef;
    }

    public String getcountry() {
        return country;
    }

    public List<Map<String, Object>> getmeta() {
        return meta;
    }

    public String getnarration() {
        return narration;
    }

    public String getlastname() {
        return lastname;
    }

    public String getfirstname() {
        return firstname;
    }

    public String getcurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("PBFPubKey", PBFPubKey)
                .add("amount", amount)
                .add("email", email)
                .add("IP", IP)
                .add("txRef", txRef)
                .add("country", country)
                .add("currency", currency)
                .add("firstname", firstname)
                .add("lastname", lastname)
                .add("narration", narration)
                .add("meta", meta)
                .toString();
    }
}
