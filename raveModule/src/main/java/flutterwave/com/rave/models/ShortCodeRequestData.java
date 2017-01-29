package flutterwave.com.rave.models;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

/**
 * Created by Shittu on 19/12/2016.
 */

public class ShortCodeRequestData extends BaseRequestData {
    private String cvv;
    private String shortcode;
    private String pin;

    public ShortCodeRequestData(String PBFPubKey, String amount, String email, String IP,
                                String txRef, String country, String currency, String firstname,
                                String lastname, String narration, List<Map<String, Object>> meta,
                                String cvv, String shortcode, String pin) {
        super(PBFPubKey, amount, email, IP, txRef, country, currency, firstname, lastname, narration, meta);
        this.cvv = cvv;
        this.shortcode = shortcode;
        this.pin = pin;
    }

    public String getcvv() {
        return cvv;
    }

    public String getshortcode() {
        return shortcode;
    }

    public String getpin() {
        return pin;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cvv", cvv)
                .add("shortcode", shortcode)
                .add("pin", pin)
                .toString();
    }
}
