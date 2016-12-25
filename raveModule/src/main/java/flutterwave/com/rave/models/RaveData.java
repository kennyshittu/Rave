package flutterwave.com.rave.models;

import android.graphics.Bitmap;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by Shittu on 17/12/2016.
 */

public class RaveData {
    private Bitmap mItemImage;
    private String mItemName;
    private String mItemDescription;
    private Double mItemPrice;
    private String mPBFPubKey;
    private String mSecretKey;
    private String mCustomerEmailAddress;
    private String mIP;
    private String mTxRef;

    // no compulsory
    private String mCurrency;
    private String mCountry;
    private String mFirstName;
    private String mLastName;
    private String mNarration;
    private List<Map<String, Object>> mMeta;

    public RaveData(Bitmap mItemImage, String mItemName, String mItemDescription,
                    Double mItemPrice, String mPBFPubKey, String mSecretKey, String mCustomerEmailAddress, String mTxRef) {
        this.mItemImage = mItemImage;
        this.mItemName = mItemName;
        this.mItemDescription = mItemDescription;
        this.mItemPrice = mItemPrice;
        this.mPBFPubKey = mPBFPubKey;
        this.mSecretKey = mSecretKey;
        this.mCustomerEmailAddress = mCustomerEmailAddress;
        this.mIP = "127.0.0.1";
        this.mTxRef = mTxRef;

        this.mCurrency = "NGN";
        this.mCountry = "Nigeria";
        this.mFirstName = "";
        this.mLastName = "";
        this.mNarration = "";
        this.mMeta = Lists.newArrayList();
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

    public String getmPBFPubKey() {
        return mPBFPubKey;
    }

    public String getmSecretKey() {
        return mSecretKey;
    }

    public String getmCustomerEmailAddress() {
        return mCustomerEmailAddress;
    }

    public String getmIP() {
        return mIP;
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

    public void setmMeta(List<Map<String, Object>> mMeta) {
        this.mMeta = mMeta;
    }

    public void setmNarration(String mNarration) {
        this.mNarration = mNarration;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public void setmCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public void setmTxRef(String mTxRef) {
        this.mTxRef = mTxRef;
    }

    public void setmIP(String mIP) {
        this.mIP = mIP;
    }

    public void setmCustomerEmailAddress(String mCustomerEmailAddress) {
        this.mCustomerEmailAddress = mCustomerEmailAddress;
    }

    public void setmSecretKey(String mSecretKey) {
        this.mSecretKey = mSecretKey;
    }

    public void setmPBFPubKey(String mPBFPubKey) {
        this.mPBFPubKey = mPBFPubKey;
    }

    public void setmItemPrice(Double mItemPrice) {
        this.mItemPrice = mItemPrice;
    }

    public void setmItemDescription(String mItemDescription) {
        this.mItemDescription = mItemDescription;
    }

    public void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public void setmItemImage(Bitmap mItemImage) {
        this.mItemImage = mItemImage;
    }
}
