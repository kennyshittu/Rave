package flutterwave.com.rave.models;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

/**
 * Created by Shittu on 19/12/2016.
 */

public class CardChargeRequestData extends BaseRequestData {
    private String cardno;
    private String cvv;
    private String expiryyear;
    private String expirymonth;

    public CardChargeRequestData(String PBFPubKey, String amount, String email, String IP,
                                 String txRef, String country, String currency, String firstname,
                                 String lastname, String narration, List<Map<String, Object>> meta,
                                 String cardno, String cvv, String expiryyear, String expirymonth) {
        super(PBFPubKey, amount, email, IP, txRef, country, currency, firstname, lastname, narration, meta);
        this.cardno = cardno;
        this.cvv = cvv;
        this.expiryyear = expiryyear;
        this.expirymonth = expirymonth;
    }

    public String getcardno() {
        return cardno;
    }

    public String getcvv() {
        return cvv;
    }

    public String getexpiryyear() {
        return expiryyear;
    }

    public String getexpirymonth() {
        return expirymonth;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cardno", cardno)
                .add("cvv", cvv)
                .add("expiryyear", expiryyear)
                .add("expirymonth", expirymonth)
                .toString();
    }
}
