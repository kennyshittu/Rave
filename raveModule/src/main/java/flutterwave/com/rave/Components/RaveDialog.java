package flutterwave.com.rave.Components;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import flutterwave.com.rave.R;
import flutterwave.com.rave.models.ShortCodeRequestData;
import flutterwave.com.rave.service.RaveRestClient;
import flutterwave.com.rave.models.AccountChargeRequestData;
import flutterwave.com.rave.models.CardChargeRequestData;
import flutterwave.com.rave.models.BaseRequestData;
import flutterwave.com.rave.models.RaveData;
import flutterwave.com.rave.utils.RaveAuthModel;
import flutterwave.com.rave.utils.RaveUtil;
import okhttp3.Response;


/**
 * Created by Shittu on 17/12/2016.
 */

public class RaveDialog extends Dialog {

    private static final String CHARGE_ENDPOINT = "/charge";
    private static final String VALIDATE_ENDPOINT = "/validate";
    private static final String VALIDATE_CHARGE_ENDPOINT = "/validatecharge";
    private static final String BANKS_URL = "http://flw-pms-dev.eu-west-1.elasticbeanstalk.com/banks";
    private static final String PAY_FORMAT = "PAY NGN %.2f";
    private static final String PRICE_FORMAT = "NGN %.2f";


    private static final String VBVSECURECODE = "VBVSECURECODE";
    private static final String NOAUTH = "NOAUTH";
    private static final String PIN = "PIN";
    private static final String RANDOM_DEBIT = "RANDOM_DEBIT";


    private static final int CARD_DETAILS = 1;
    private static final int ACCOUNT_DETAILS = 2;
    private static final int ALERT_MESSAGE = 3;
    private static final int CARD_AND_ALERT_MESSAGE = 4;
    private static final int OTP_VIEW = 5;
    private static final int OTP_AND_ALERT_MESSAGE = 6;
    private static final int ACCOUNT_AND_ALERT_MESSAGE = 7;
    private static final String TAG = RaveDialog.class.getCanonicalName();

    private Button mPayBtn;
    private ImageButton mCloseButton;
    private RaveData mRaveData;

    private RadioButton mCardButton;
    private RadioButton mAccountButton;

    private LinearLayout mCardDetailView;
    private LinearLayout mAccountDetailView;
    private LinearLayout mAlertMessageView;
    private LinearLayout mOtpDetailView;

    private EditText mCardNumber;
    private EditText mUserToken;
    private EditText mExpiryDate;
    private EditText mCvv;
    private EditText mAccountNumber;
    private EditText mOtpNumber;
    private EditText mCardPin;
    private EditText mAmountCharge;
    private TextView mAlertMessage;

    private Spinner mBankSpinner;
    private Spinner mOtpSpinner;

    private WebView mWebView;
    private ProgressDialog mProgressBar;
    private CheckBox mRememberBox;
    private CheckBox mUseToken;

    //TODO: replace with enums
    private boolean mIsCardTransaction = true;
    private boolean mIsAccountValidate = true;
    private boolean mShouldRememberCardDetails = false;
    private boolean mShouldUseToken = false;

    private String mAuthUrlString = "";
    private String mValidateTxRef = "";
    private String mUserCode = "";
    private List<String> mBankNames = new ArrayList<String>();
    private List<String> mBankCodes = new ArrayList<String>();


    public RaveDialog(Context context, RaveData data) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_layout);

        mRaveData = data;
        fetchBanks();

        setWidgets();
    }

    private void setWidgets() {
        //link and set pay button action
        mPayBtn = (Button) findViewById(R.id.dismiss_btn);
        mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));

        mPayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    if (mPayBtn.getText().toString().equals(getContext().getString(R.string.click_here))) {
                        // complete card validation
                        mWebView.getSettings().setJavaScriptEnabled(true);
                        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(), "INTERFACE");
                        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

                        mProgressBar = ProgressDialog.show(getContext(), "Verifying Transaction", "Authenticating...");

                        mWebView.setWebViewClient(new WebViewClient() {

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                Log.i(TAG, "Finished loading URL: " + url);
                                if (mProgressBar.isShowing()) {
                                    mProgressBar.dismiss();
                                }
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                mWebView.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
                                            }
                                        },
                                        3000);

                            }
                        });
                        //disable button while web view loads
                        mPayBtn.setText(R.string.please_wait);
                        mPayBtn.setEnabled(false);
                        mWebView.setVisibility(View.VISIBLE);
                        mWebView.loadUrl(mAuthUrlString);

                    } else if (mPayBtn.getText().toString().equals(getContext().getString(R.string.validate_otp))) {
                        // otp validation
                        if (validateInputFields()) {
                            String otp = mOtpNumber.getText().toString();

                            if (mIsAccountValidate) {
                                Map<String, String> params = RaveUtil.buildValidateRequestParam(
                                        mRaveData.getmPbfPubKey(),
                                        mValidateTxRef, otp
                                );
                                sendRequest(params, VALIDATE_ENDPOINT);
                            } else {
                                Map<String, String> params = RaveUtil.buildValidateChargeRequestParam(
                                        mRaveData.getmPbfPubKey(),
                                        mValidateTxRef, otp
                                );
                                sendRequest(params, VALIDATE_CHARGE_ENDPOINT);
                            }
                        }

                    } else if (mPayBtn.getText().toString().equals(getContext().getString(R.string.close_form))) {
                        // close form
                        dismiss();
                    } else {
                        // charge request
                        if (validateInputFields()) {
                            BaseRequestData requestData = getRequestData();
                            String jsonData = RaveUtil.getJsonStringFromRequestData(requestData);
                            String encryptedData = RaveUtil.getEncryptedData(jsonData, mRaveData.getmSecretKey());

                            //set request params
                            Map<String, String> params = RaveUtil.buildChargeRequestParam(mRaveData.getmPbfPubKey(), encryptedData);
                            System.out.println("sending charge req : " + params);
                            sendRequest(params, CHARGE_ENDPOINT);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //set item image
        ImageView itemImageView = (ImageView) findViewById(R.id.item_img);
        Bitmap bitmap = Optional.fromNullable(mRaveData.getmItemImage()).or(
                BitmapFactory.decodeResource(getContext().getResources(), R.drawable.coke)
        );

        RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
        roundDrawable.setCircular(true);
        itemImageView.setImageDrawable(roundDrawable);


        //set item name
        TextView itemName = (TextView) findViewById(R.id.item_name);
        itemName.setText(mRaveData.getmItemName());

        //set item description
        TextView itemDescription = (TextView) findViewById(R.id.item_description);
        itemDescription.setText(mRaveData.getmItemDescription());

        //set item price
        TextView itemPrice = (TextView) findViewById(R.id.item_price);
        itemPrice.setText(String.format(PRICE_FORMAT, mRaveData.getmItemPrice()));

        //set message text view
        mAlertMessage = (TextView) findViewById(R.id.message_text_view);

        //set alert message view
        mAlertMessageView = (LinearLayout) findViewById(R.id.alert_message_view);

        //set close image button
        mCloseButton = (ImageButton) findViewById(R.id.close_btn);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //set Radio Buttons
        mCardButton = (RadioButton) findViewById(R.id.card_segment_btn);
        mAccountButton = (RadioButton) findViewById(R.id.account_segment_btn);

        //set detail view
        mCardDetailView = (LinearLayout) findViewById(R.id.card_segment_view);
        mAccountDetailView = (LinearLayout) findViewById(R.id.account_segment_view);
        mOtpDetailView = (LinearLayout) findViewById(R.id.otp_view);

        mOtpNumber = (EditText) findViewById(R.id.otp_number);

        mCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBackground(mCardButton, mAccountButton);
                showView(CARD_DETAILS);
                mIsCardTransaction = true;

                updateSegmentIcons(R.drawable.bank_card_small, R.drawable.account_small_filled);
            }
        });

        mAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBackground(mAccountButton, mCardButton);
                showView(ACCOUNT_DETAILS);
                mIsCardTransaction = false;
                updateSegmentIcons(R.drawable.bank_card_filled_small, R.drawable.account_small);
            }
        });

        //set cvv text field
        mCvv = (EditText) findViewById(R.id.cvv);

        //set account number text field
        mAccountNumber = (EditText) findViewById(R.id.acount_number);

        //set expiry date text field
        mExpiryDate = (EditText) findViewById(R.id.expiry_date);
        mExpiryDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String cleanText = mExpiryDate.getText().toString();
                if (keyCode != 67 && cleanText.length() == 2) {
                    // add `/` after first 2 characters
                    // to put cursor in right position
                    String text = RaveUtil.addPadding("/", cleanText, 2);
                    mExpiryDate.setText("");
                    mExpiryDate.append(text);
                }
                return false;
            }
        });

        //set Card number text field
        mCardNumber = (EditText) findViewById(R.id.card_number);
        mCardNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 67) {
                    String cardText = mCardNumber.getText().toString();
                    String validText = RaveUtil.cleanText(cardText, " ");

                    int visaNumber = 0;
                    int masterNumber = 0;

                    if (validText.length() > 0) {
                        visaNumber = Integer.parseInt(validText.substring(0, 1));
                    }

                    if (validText.length() > 1) {
                        masterNumber = Integer.parseInt(validText.substring(0, 2));
                    }

                    if (visaNumber == 4) {
                        updateCardTextViewIcon(R.drawable.visa);
                    } else if (masterNumber >= 51 && masterNumber <= 55) {
                        updateCardTextViewIcon(R.drawable.mastercard);
                    } else {
                        updateCardTextViewIcon(R.drawable.bank_card_filled);
                    }

                    // add space after every 4 characters
                    if (validText.length() % 4 == 0) {
                        // to put cursor in right position
                        String text = RaveUtil.addPadding(" ", validText, 4);
                        mCardNumber.setText("");
                        mCardNumber.append(text);
                    }
                }
                return false;
            }
        });

        mWebView = (WebView) findViewById(R.id.web_view);
        mRememberBox = (CheckBox) findViewById(R.id.remember_card);
        mUseToken = (CheckBox) findViewById(R.id.use_token);
        mUserToken = (EditText) findViewById(R.id.user_token);
        mCardPin = (EditText) findViewById(R.id.card_pin);
        mAmountCharge = (EditText) findViewById(R.id.amount_charged);

        if (mRaveData.getmAuthModel().equals(RaveAuthModel.PIN)) {
            mCardPin.setVisibility(View.VISIBLE);
        } else {
            mCardPin.setVisibility(View.GONE);
        }


        mRememberBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShouldRememberCardDetails = ((CheckBox) v).isChecked();
            }
        });

        mUseToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShouldUseToken = ((CheckBox) v).isChecked();

                if (mShouldUseToken) {
                    mCardNumber.setVisibility(View.GONE);
                    mExpiryDate.setVisibility(View.GONE);
                    mRememberBox.setVisibility(View.GONE);
//                    if (mRaveData.ismIsPinAuthModel()) {
//                        mCardPin.setVisibility(View.GONE);
//                    }
                    mUserToken.setVisibility(View.VISIBLE);
                } else {
                    mUserToken.setVisibility(View.GONE);
                    mCardNumber.setVisibility(View.VISIBLE);
                    mExpiryDate.setVisibility(View.VISIBLE);
                    mRememberBox.setVisibility(View.VISIBLE);
//                    if (mRaveData.ismIsPinAuthModel()) {
//                        mCardPin.setVisibility(View.VISIBLE);
//                    }
                }
            }
        });

    }

    private void fetchBanks() {
        new RequestTask(null, BANKS_URL, false).execute();
    }

    private void updateCardTextViewIcon(int drawableId) {
        mCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), drawableId), null);
        mCardNumber.invalidate();
    }

    private void updateSegmentIcons(int cardDrawableId, int accountDrawableId) {
        mCardButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), cardDrawableId), null, null, null);
        mAccountButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), accountDrawableId), null, null, null);
        mCardButton.invalidate();
        mAccountButton.invalidate();
    }

    private void updateBackground(RadioButton checked, RadioButton unChecked) {
        unChecked.setBackgroundColor(Color.TRANSPARENT);
        unChecked.setTextColor(Color.BLACK);

        checked.setTextColor(Color.WHITE);
        checked.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.martinique));
    }

    // construct the right request data
    private BaseRequestData getRequestData() {
        try {
            if (mIsCardTransaction) {
                BaseRequestData data;
                String[] dateData = mExpiryDate.getText().toString().split("/");
                if (!mShouldUseToken) {
                    data = new CardChargeRequestData(
                            mRaveData.getmPbfPubKey(),
                            mRaveData.getmItemPrice().toString(),
                            mRaveData.getmCustomerEmailAddress(),
                            mRaveData.getmIp(),
                            mRaveData.getmTxRef(),
                            mRaveData.getmCountry(),
                            mRaveData.getmCurrency(),
                            mRaveData.getmFirstName(),
                            mRaveData.getmLastName(),
                            mRaveData.getmNarration(),
                            mRaveData.getmMeta(),
                            RaveUtil.cleanText(mCardNumber.getText().toString(), " "),
                            mCvv.getText().toString(),
                            dateData[1], // month
                            dateData[0], // year
                            mRaveData.getmAuthModel().equals(RaveAuthModel.PIN) ?
                                    mCardPin.getText().toString() : ""
                    );

                } else {
                    data = new ShortCodeRequestData(
                            mRaveData.getmPbfPubKey(),
                            mRaveData.getmItemPrice().toString(),
                            mRaveData.getmCustomerEmailAddress(),
                            mRaveData.getmIp(),
                            mRaveData.getmTxRef(),
                            mRaveData.getmCountry(),
                            mRaveData.getmCurrency(),
                            mRaveData.getmFirstName(),
                            mRaveData.getmLastName(),
                            mRaveData.getmNarration(),
                            mRaveData.getmMeta(),
                            mCvv.getText().toString(),
                            mUserToken.getText().toString(),
                            mRaveData.getmAuthModel().equals(RaveAuthModel.PIN)  ?
                                    mCardPin.getText().toString() : ""
                    );
                }
                return data;
            } else {
                int index = mBankSpinner.getSelectedItemPosition();
                String bankCode = mBankCodes.get(index);
                return new AccountChargeRequestData(
                        mRaveData.getmPbfPubKey(),
                        mRaveData.getmItemPrice().toString(),
                        mRaveData.getmCustomerEmailAddress(),
                        mRaveData.getmIp(),
                        mRaveData.getmTxRef(),
                        mRaveData.getmCountry(),
                        mRaveData.getmCurrency(),
                        mRaveData.getmFirstName(),
                        mRaveData.getmLastName(),
                        mRaveData.getmNarration(),
                        mRaveData.getmMeta(),
                        mAccountNumber.getText().toString(),
                        bankCode,
                        mOtpSpinner.getSelectedItem().toString()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // basic input validation
    private boolean validateInputFields() {
        boolean isValid = true;
        if (mCardDetailView.isShown()) {

            if (!mShouldUseToken) {
                if (mCardNumber.getText().length() != 19) {
                    mCardNumber.setError(getContext().getString(R.string.card_field_error));
                    isValid = false;
                }

                if (mExpiryDate.getText().length() != 5) {
                    mExpiryDate.setError(getContext().getString(R.string.date_field_error));
                    isValid = false;
                }
            } else {
                if (mUserToken.getText().length() != 5) {
                    mUserToken.setError(getContext().getString(R.string.user_token_field_error));
                    isValid = false;
                }
            }
            if (mCvv.getText().length() != 3) {
                mCvv.setError(getContext().getString(R.string.cvv_field_error));
                isValid = false;
            }

            if (mRaveData.getmAuthModel().equals(RaveAuthModel.PIN)) {
                if (mCardPin.getText().length() != 4) {
                    mCardPin.setError(getContext().getString(R.string.pin_field_error));
                    isValid = false;
                }
            }
        }

        if (mAccountDetailView.isShown()) {
            if (mAccountNumber.getText().length() != 10) {
                mAccountNumber.setError(getContext().getString(R.string.account_number_field_error));
                isValid = false;
            }

            if (mBankSpinner.getSelectedItem().toString().equals(getContext().getString(R.string.select_bank))) {
                showSpinnerError(mBankSpinner);
                isValid = false;
            }

            if (mOtpSpinner.getSelectedItem().toString().equals(getContext().getString(R.string.select_otp))) {
                showSpinnerError(mOtpSpinner);
                isValid = false;
            }
        }

        if (mOtpDetailView.isShown()) {
            if (mOtpNumber.getText().length() != 5) {
                mOtpNumber.setError(getContext().getString(R.string.otp_field_error));
                isValid = false;
            }
        }

        return isValid;
    }

    private void showSpinnerError(Spinner spinner) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(getContext().getString(R.string.spinner_error));
        }
    }

    private void sendRequest(Map<String, String> params, String endpoint) {
        new RequestTask(params, endpoint, true).execute();
        lockorUnlockInputFields(false);
        mPayBtn.setText(R.string.please_wait);

    }

    private void handleCardValidateResponse(Response response) {
        if (response != null) {
            Reader responseReader = response.body().charStream();

            Map<String, Object> mapResponse = RaveUtil.getMapFromJsonReader(responseReader);
            Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
            Map<String, Object> innerData = (Map<String, Object>) data.get("data");

            System.out.println("card charge response : " + mapResponse);
            System.out.println("card charge response outter data: " + data);

            if (response.isSuccessful() && (innerData.get("responsecode").equals("02")
                    || innerData.get("responsecode").equals("00"))) {

                // finished random debit
                System.out.println("card charge response inner data: " + innerData);

                String msg = (String) innerData.get("responsemessage");
                if (mShouldRememberCardDetails) {
                    msg = (msg + "\n To avoid entering card detail on subsequent transactions," +
                            " use the token below. \n user token : "
                            + mUserCode);
                }

                mAlertMessage.setText(msg);
                mAlertMessage.setBackgroundResource(R.drawable.curved_shape_curious_blue);

                showView(ALERT_MESSAGE);
                mPayBtn.setText(R.string.close_form);
                mPayBtn.setBackgroundResource(R.drawable.curved_shape);
                mPayBtn.setTextColor(Color.BLACK);

            } else {
                mAlertMessage.setText((String) data.get("message"));
                mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                lockorUnlockInputFields(true);
                showView(CARD_AND_ALERT_MESSAGE);
            }
        } else {
            mAlertMessage.setText(R.string.network_error);
            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
            lockorUnlockInputFields(true);
            showView(CARD_AND_ALERT_MESSAGE);
        }
    }


    private void handleCardChargeResponse(Response response) {
        if (response != null) {
            Reader responseReader = response.body().charStream();
            Map<String, Object> mapResponse = RaveUtil.getMapFromJsonReader(responseReader);
            Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");

            String authMode = (String) data.get("authModelUsed");
            System.out.println("card charge response : " + data);
            if (response.isSuccessful() && authMode != null) {
                Map<Object, Object> chargeToken = (Map<Object, Object>) data.get("chargeToken");
                mUserCode = (String) chargeToken.get("user_token");

                Number amount = (Number) data.get("amount");
                Number chargedAmount = (Number) data.get("charged_amount");
                Number appFee = (Number) data.get("appfee");

                // this takes care of situation where merchant bears fees charged
                // by ensuring the app fee displayed to customer is 0
                // since it has been paid for by merchant
                if(amount.equals(chargedAmount)){
                    appFee = 0;
                }

                String amountMsg = String.format("%s + %s = %s", amount, appFee, chargedAmount);

                mAmountCharge.setText(amountMsg);

                switch (authMode) {
                    case VBVSECURECODE:
                        if ((data.get("chargeResponseCode").equals("02")
                                || data.get("chargeResponseCode").equals("00"))) {
                            mAuthUrlString = (String) data.get("authurl");
                            System.out.println("card charge response : " + data);

                            mPayBtn.setBackgroundResource(R.drawable.curved_shape_martinique);
                            showView(CARD_DETAILS);
                            mPayBtn.setText(R.string.click_here);

                        } else {
                            mAlertMessage.setText((String) data.get("message"));
                            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                            lockorUnlockInputFields(true);
                            showView(CARD_AND_ALERT_MESSAGE);
                        }
                        break;
                    case NOAUTH:
                        if (data.get("chargeResponseCode").equals("00")) {
                            System.out.println("card charge response : " + data);
                            String msg = (String) data.get("vbvrespmessage");
                            if (mShouldRememberCardDetails) {
                                msg = (msg + "\n To avoid entering card detail on subsequent transactions," +
                                        " use the token below. \n user token : "
                                        + mUserCode);
                            }

                            mAlertMessage.setText(msg);
                            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_curious_blue);

                            showView(ALERT_MESSAGE);
                            mPayBtn.setText(R.string.close_form);
                            mPayBtn.setBackgroundResource(R.drawable.curved_shape);
                            mPayBtn.setTextColor(Color.BLACK);

                        } else {
                            mAlertMessage.setText((String) data.get("message"));
                            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                            lockorUnlockInputFields(true);
                            showView(CARD_AND_ALERT_MESSAGE);
                        }
                        break;
                    case RANDOM_DEBIT:
                        if ((data.get("chargeResponseCode").equals("02")
                                || data.get("chargeResponseCode").equals("00"))) {
                            //no auth url returned
                            mOtpNumber.setEnabled(true);
                            mValidateTxRef = (String) data.get("flwRef");
                            mPayBtn.setText(R.string.validate_otp);
                            mIsAccountValidate = false;
                            showView(OTP_VIEW);
                        } else {
                            mAlertMessage.setText((String) data.get("message"));
                            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                            lockorUnlockInputFields(true);
                            showView(CARD_AND_ALERT_MESSAGE);
                        }
                        break;
                    case PIN:
                        if ((data.get("chargeResponseCode").equals("02")
                                || data.get("chargeResponseCode").equals("00"))) {
                            //no auth url returned
                            mOtpNumber.setEnabled(true);
                            mValidateTxRef = (String) data.get("flwRef");
                            mPayBtn.setText(R.string.validate_otp);
                            mIsAccountValidate = false;
                            showView(OTP_VIEW);
                        } else {
                            mAlertMessage.setText((String) data.get("message"));
                            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                            lockorUnlockInputFields(true);
                            showView(CARD_AND_ALERT_MESSAGE);
                        }
                        break;
                    default:
                        // invalid auth model used
                        System.out.println("Invalid auth model used : " + (String) data.get("authModelUsed"));
                        break;

                }
            } else {
                mAlertMessage.setText((String) data.get("message"));
                mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                lockorUnlockInputFields(true);
                showView(CARD_AND_ALERT_MESSAGE);
            }

        } else {
            mAlertMessage.setText(R.string.network_error);
            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
            lockorUnlockInputFields(true);
            showView(CARD_AND_ALERT_MESSAGE);
        }
    }

    private void handleGetBankResponse(Response response) {

        if (response != null) {
            if (response.isSuccessful()) {
                try {
                    String responseString = response.body().string();
                    Map<String, Object> mapResponse = RaveUtil.getMapFromJsonString(responseString);
                    List<Map<String, Object>> bankObjects = (List<Map<String, Object>>) mapResponse.get("data");

                    // can't use java 8 stream cos of api level restriction
                    for (Map<String, Object> bankObject : bankObjects) {
                        mBankNames.add((String) bankObject.get("name"));
                        mBankCodes.add((String) bankObject.get("code"));
                    }

                    //set spinners
                    mBankSpinner = (Spinner) findViewById(R.id.bank_spinner);
                    mOtpSpinner = (Spinner) findViewById(R.id.otp_spinner);

                    // Create an ArrayAdapter using the string array and a default spinner
                    ArrayAdapter<String> bankAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, mBankNames);

                    ArrayAdapter<CharSequence> otpAdapter = ArrayAdapter
                            .createFromResource(getContext(), R.array.otp_option_array,
                                    android.R.layout.simple_spinner_item);

                    // Specify the layout to use when the list of choices appears
                    bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    otpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Apply the adapter to the spinner
                    mBankSpinner.setAdapter(bankAdapter);
                    mOtpSpinner.setAdapter(otpAdapter);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e(TAG, "Failed to fetch list of bank objects, with response code : " + response.code());
            }
        } else {
            Log.e(TAG, "Could not fetch list of bank objects, response object is : " + response);
        }
    }

    private void handleAccountChargeResponse(Response response) {
        if (response != null) {
            try {
                String responseString = response.body().string();
                Map<String, Object> mapResponse = RaveUtil.getMapFromJsonString(responseString);
                Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");
                if (response.isSuccessful() && (data.get("chargeResponseCode").equals("02") || data.get("chargeResponseCode").equals("00"))) {

                    Number amount = (Number) data.get("amount");
                    Number chargedAmount = (Number) data.get("charged_amount");
                    Number appFee = (Number) data.get("appfee");

                    // this takes care of situation where merchant bears fees charged
                    // by ensuring the app fee displayed to customer is 0
                    // since it has been paid for by merchant
                    if(amount.equals(chargedAmount)){
                        appFee = 0;
                    }

                    String amountMsg = String.format("%s + %s = %s", amount, appFee, chargedAmount);

                    mAmountCharge.setText(amountMsg);

                    mOtpNumber.setEnabled(true);
                    mValidateTxRef = (String) data.get("txRef");
                    mPayBtn.setText(R.string.validate_otp);
                    mIsAccountValidate = true;
                    showView(OTP_VIEW);
                } else {
                    // show error alert message  and account view
                    lockorUnlockInputFields(true);
                    mAccountNumber.setError((String) data.get("message"));
                    mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // response is null
            mAlertMessage.setText(R.string.network_error);
            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
            mPayBtn.setText(String.format(PAY_FORMAT, mRaveData.getmItemPrice()));
            showView(ACCOUNT_AND_ALERT_MESSAGE);
        }
    }

    private void handleAccountValidateResponse(Response response) {
        if (response != null) {
            try {
                String responseString = response.body().string();
                Map<String, Object> mapResponse = RaveUtil.getMapFromJsonString(responseString);
                Map<String, Object> data = (Map<String, Object>) mapResponse.get("data");

                if (response.isSuccessful()) {
                    if (data.get("acctvalrespcode").equals("02") || data.get("acctvalrespcode").equals("00")) {
                        mAlertMessage.setText((String) data.get("acctvalrespmsg"));
                        mAlertMessage.setBackgroundResource(R.drawable.curved_shape_curious_blue);

                        showView(ALERT_MESSAGE);
                        mPayBtn.setText(R.string.close_form);
                        mPayBtn.setBackgroundResource(R.drawable.curved_shape);
                        mPayBtn.setTextColor(Color.BLACK);
                    } else {
                        // show error alert message
                        mAlertMessage.setText((String) data.get("acctvalrespmsg"));
                        mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                        mPayBtn.setText(R.string.close_form);
                        mPayBtn.setBackgroundResource(R.drawable.curved_shape);
                        mPayBtn.setTextColor(Color.BLACK);
                        lockorUnlockInputFields(true);
                        showView(ALERT_MESSAGE);
                    }
                } else {
                    // show error alert message  and otp view
                    mAlertMessage.setText((String) data.get("message"));
                    mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                    mPayBtn.setText(R.string.close_form);
                    mPayBtn.setBackgroundResource(R.drawable.curved_shape);
                    mPayBtn.setTextColor(Color.BLACK);
                    lockorUnlockInputFields(true);
                    showView(ALERT_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //response is null
            mAlertMessage.setText(R.string.network_error);
            mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
            mPayBtn.setText(R.string.validate_otp);
            lockorUnlockInputFields(true);
            showView(OTP_AND_ALERT_MESSAGE);
        }
    }

    private void showView(int viewNumber) {
        switch (viewNumber) {
            case CARD_DETAILS:
                mCardDetailView.setVisibility(View.VISIBLE);
                mAccountDetailView.setVisibility(View.GONE);
                mAlertMessageView.setVisibility(View.GONE);
                mOtpDetailView.setVisibility(View.GONE);
                break;
            case ACCOUNT_DETAILS:
                mCardDetailView.setVisibility(View.GONE);
                mAccountDetailView.setVisibility(View.VISIBLE);
                mAlertMessageView.setVisibility(View.GONE);
                mOtpDetailView.setVisibility(View.GONE);
                break;
            case ALERT_MESSAGE:
                mCardDetailView.setVisibility(View.GONE);
                mAccountDetailView.setVisibility(View.GONE);
                mAlertMessageView.setVisibility(View.VISIBLE);
                mOtpDetailView.setVisibility(View.GONE);
                break;
            case CARD_AND_ALERT_MESSAGE:
                mCardDetailView.setVisibility(View.VISIBLE);
                mAccountDetailView.setVisibility(View.GONE);
                mAlertMessageView.setVisibility(View.VISIBLE);
                mOtpDetailView.setVisibility(View.GONE);
                break;
            case ACCOUNT_AND_ALERT_MESSAGE:
                mCardDetailView.setVisibility(View.GONE);
                mAccountDetailView.setVisibility(View.VISIBLE);
                mAlertMessageView.setVisibility(View.VISIBLE);
                mOtpDetailView.setVisibility(View.GONE);
                break;
            case OTP_AND_ALERT_MESSAGE:
                mCardDetailView.setVisibility(View.GONE);
                mAccountDetailView.setVisibility(View.GONE);
                mAlertMessageView.setVisibility(View.VISIBLE);
                mOtpDetailView.setVisibility(View.VISIBLE);
                break;
            case OTP_VIEW:
                mCardDetailView.setVisibility(View.GONE);
                mAccountDetailView.setVisibility(View.GONE);
                mAlertMessageView.setVisibility(View.GONE);
                mOtpDetailView.setVisibility(View.VISIBLE);
            default:
                break;
        }
    }


    private void lockorUnlockInputFields(boolean unLock) {
        mCardButton.setEnabled(unLock);
        mAccountButton.setEnabled(unLock);
        mAccountNumber.setEnabled(unLock);
        if (mBankSpinner != null) { // might be null if there's no internet connect on client device
            mBankSpinner.setEnabled(unLock);
        }
        mOtpSpinner.setEnabled(unLock);
        mCardNumber.setEnabled(unLock);
        mCvv.setEnabled(unLock);
        mExpiryDate.setEnabled(unLock);
        mOtpNumber.setEnabled(unLock);
        mCardPin.setEnabled(unLock);
        mUseToken.setEnabled(unLock);
        mUserToken.setEnabled(unLock);
        mRememberBox.setEnabled(unLock);
    }

    // TODO: move to Rave Rest client and use a handler to get response
    class RequestTask extends AsyncTask<String, String, Response> {

        private Map<String, String> params;
        private String endpoint;
        private boolean isPostRequest;

        private RequestTask(Map<String, String> params, String endpoint, boolean isPostRequest) {
            this.params = params;
            this.endpoint = endpoint;
            this.isPostRequest = isPostRequest;
        }

        @Override
        protected Response doInBackground(String... uri) {
            try {
                Response response;
                if (isPostRequest) {
                    String json = new ObjectMapper().writeValueAsString(params);
                    response = RaveRestClient.post(endpoint, json);
                } else {
                    response = RaveRestClient.get(endpoint);
                }
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            //TODO: implement uisng enumerations and switch statement
            //Do anything with response..
            if (!isPostRequest) {
                handleGetBankResponse(response);
            } else if (endpoint.equals(CHARGE_ENDPOINT) && mIsCardTransaction) {
                handleCardChargeResponse(response);
            } else if (endpoint.equals(CHARGE_ENDPOINT) && !mIsCardTransaction) {
                handleAccountChargeResponse(response);
            } else if (endpoint.equals(VALIDATE_ENDPOINT) && !mIsCardTransaction) {
                handleAccountValidateResponse(response);
            } else if (endpoint.equals(VALIDATE_CHARGE_ENDPOINT) && mIsCardTransaction) {
                handleCardValidateResponse(response);
            } else {
                Log.e(TAG, "Error handling request response");
            }

        }
    }

    /* An instance of this class will be registered as a JavaScript interface */
    class WebViewJavaScriptInterface {
        public WebViewJavaScriptInterface() {
        }

        @SuppressWarnings("unused")

        @JavascriptInterface
        public void processContent(String aContent) {
            final String content = aContent;

            mAlertMessage.post(new Runnable() {
                public void run() {
                    Map<String, Object> mapResponse = RaveUtil.getMapFromJsonString(content);

                    System.out.println("response in web view : " + mapResponse);
                    mWebView.setVisibility(View.GONE);
                    String errorCode = (String) mapResponse.get("code");
                    if (errorCode != null && errorCode.equals("SERV_ERR")) {
                        mAlertMessage.setText((String) mapResponse.get("message"));
                        mAlertMessage.setBackgroundResource(R.drawable.curved_shape_dark_pastel_red);
                    } else {
                        String msg = (String) mapResponse.get("vbvrespmessage");
                        Map<Object, Object> chargeToken = (Map<Object, Object>) mapResponse.get("chargeToken");
                        if (mShouldRememberCardDetails) {
                            msg = (msg + "\n To avoid entering card detail on subsequent transactions," +
                                    " use the token below. \n user token : "
                                    + chargeToken.get("user_token"));
                        }
                        mAlertMessage.setText(msg);
                        mAlertMessage.setBackgroundResource(R.drawable.curved_shape_curious_blue);
                    }
                    showView(ALERT_MESSAGE);
                    mPayBtn.setEnabled(true);
                    mPayBtn.setText(R.string.close_form);
                    mPayBtn.setBackgroundResource(R.drawable.curved_shape);
                    mPayBtn.setTextColor(Color.BLACK);
                }
            });
        }
    }
}
