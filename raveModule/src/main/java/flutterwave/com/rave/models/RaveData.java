package flutterwave.com.rave.models;

import android.graphics.Bitmap;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by Shittu on 17/12/2016.
 */

public class RaveData {
    private String mItemName;
    private String mItemDescription;
    private Double mItemPrice;
    private String mPbfPubKey;
    private String mSecretKey;
    private String mCustomerEmailAddress;
    private String mIp;
    private String mTxRef;

    // optional properties
    private Bitmap mItemImage;
    private String mCurrency;
    private String mCountry;
    private String mFirstName;
    private String mLastName;
    private String mNarration;
    private List<Map<String, Object>> mMeta;

    private RaveData(Builder builder) {
        this.mItemName = builder.itemName;
        this.mItemDescription = builder.itemDescription;
        this.mItemPrice = builder.itemPrice;
        this.mPbfPubKey = builder.pbfPubKey;
        this.mSecretKey = builder.secretKey;
        this.mCustomerEmailAddress = builder.customerEmailAddress;
        this.mTxRef = builder.txRef;

        this.mItemImage = builder.itemImage; // will use default rave logo if not set.
        this.mCurrency = Optional.fromNullable(builder.currency).or("NGN");
        this.mCountry = Optional.fromNullable(builder.country).or("Nigeria");
        this.mFirstName = Optional.fromNullable(builder.firstName).or("");
        this.mLastName = Optional.fromNullable(builder.lastName).or("");
        this.mNarration = Optional.fromNullable(builder.narration).or("");
        this.mIp = Optional.fromNullable(builder.ip).or("127.0.0.1");
        this.mMeta = Optional.fromNullable(builder.meta).or(Lists.<Map<String,Object>>newArrayList());
    }

    public Bitmap getmItemImage() {
        return mItemImage;
    }

    public String getmItemName() {
        return mItemName;
    }

    public String getmItemDescription() {
        return mItemDescription;
    }

    public Double getmItemPrice() {
        return mItemPrice;
    }

    public String getmPbfPubKey() {
        return mPbfPubKey;
    }

    public String getmSecretKey() {
        return mSecretKey;
    }

    public String getmCustomerEmailAddress() {
        return mCustomerEmailAddress;
    }

    public String getmIp() {
        return mIp;
    }

    public String getmTxRef() {
        return mTxRef;
    }

    public String getmCurrency() {
        return mCurrency;
    }

    public String getmCountry() {
        return mCountry;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public String getmNarration() {
        return mNarration;
    }

    public List<Map<String, Object>> getmMeta() {
        return mMeta;
    }

    public static class Builder {
        private String itemName;
        private String itemDescription;
        private Double itemPrice;
        private String pbfPubKey;
        private String secretKey;
        private String customerEmailAddress;
        private String txRef;

        // no compulsory
        private Bitmap itemImage;
        private String ip;
        private String currency;
        private String country;
        private String firstName;
        private String lastName;
        private String narration;
        private List<Map<String, Object>> meta;

        public Builder(String itemName, String itemDescription, Double itemPrice, String pbfPubKey,
                       String secretKey, String customerEmailAddress, String txRef) {
            this.itemName = itemName;
            this.itemDescription = itemDescription;
            this.itemPrice = itemPrice;
            this.pbfPubKey = pbfPubKey;
            this.secretKey = secretKey;
            this.customerEmailAddress = customerEmailAddress;
            this.txRef = txRef;
        }

        public Builder withMeta(List<Map<String, Object>> meta) {
            this.meta = meta;
            return this;
        }

        public Builder withNarration(String narration) {
            this.narration = narration;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withItemImage(Bitmap itemImage) {
            this.itemImage = itemImage;
            return this;
        }

        public RaveData build() {
            return new RaveData(this);
        }
    }

}
