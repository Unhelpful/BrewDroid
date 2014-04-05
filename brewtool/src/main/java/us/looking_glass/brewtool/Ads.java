package us.looking_glass.brewtool;


import com.google.android.gms.ads.AdRequest;

/**
 * Created by chshrcat on 3/26/14.
 */
public class Ads {
    static final String adUnitId = "ca-app-pub-4786941106056548/6050291711";
    static AdRequest getAdRequest() {
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("9A000D446539AA13D49441B81D218DA2")
                .build();
    }
}
