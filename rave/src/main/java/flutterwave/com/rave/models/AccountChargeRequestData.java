package flutterwave.com.rave.models;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Map;

/**
 * Created by Shittu on 19/12/2016.
 */

public class AccountChargeRequestData extends BaseRequestData {

    private String accountnumber;
    private String accountbank;
    private String validateoption;
    private String payment_type;


    public AccountChargeRequestData(String PBFPubKey, String amount, String email, String IP,
                                    String txRef, String country, String currency, String firstname,
                                    String lastname, String narration, List<Map<String, Object>> meta,
                                    String accountnumber, String accountbank, String validateoption) {
        super(PBFPubKey, amount, email, IP, txRef, country, currency, firstname, lastname, narration, meta);
        this.accountnumber = accountnumber;
        this.accountbank = accountbank;
        this.validateoption = validateoption;
        this.payment_type = "account";
    }

    public String getaccountnumber() {
        return accountnumber;
    }

    public String getaccountbank() {
        return accountbank;
    }

    public String getpayment_type() {
        return payment_type;
    }

    public String getvalidateoption() {
        return validateoption;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountnumber", accountnumber)
                .add("accountbank", accountbank)
                .add("validateoption", validateoption)
                .add("payment_type", payment_type)
                .toString();
    }
}
