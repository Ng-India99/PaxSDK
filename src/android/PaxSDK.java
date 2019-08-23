package paxsdk;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* Other Imports */
import android.util.Log;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.math.BigDecimal;


/* SDK Related Imports */
import com.pax.dal.IPrinter;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

/**
 * This class echoes a string called from JavaScript.
 */
public class PaxSDK extends CordovaPlugin {

    private IPrinter printer;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("TPP", "Enter to plugin exe");
        if (action.equals("printReceipt")) {
            this.printReceipt(args.getJSONObject(0), callbackContext);
            return true;
        }
        if (action.equals("printSummary")) {
            this.printSummary(args, callbackContext);
            return true;
        }
        return false;
    }



    private void printReceipt(final JSONObject printData, CallbackContext callbackContext) {
        try {
            Context context = cordova.getActivity().getApplicationContext();
            Log.d("TPP", "context Done");
            printer = NeptuneLiteUser.getInstance().getDal(context).getPrinter();
            printer.init();
            Log.d("main", "complete init");
        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("SystemAPi Initialize " + e.getMessage());
            callbackContext.error("Printer not supported");

        }

        if (printData != null) {
            String title = "Receipt";
            String copyType = "Customer Copy";
            String paymentDate = "";
            String receiptNumber = "";
            String invoiceNumber = "";
            String paymentMode = "Cash";
            String amount = "";
            Boolean isThanksNote = false;

            try {
                title = printData.has("title") ? printData.getString("title") : "Receipt";
                copyType = printData.has("copyType") ? printData.getString("copyType") : "Customer Copy";
                paymentDate = printData.has("paymentDate") ? printData.getString("paymentDate") : null;
                receiptNumber = printData.has("receiptNumber") ? printData.getString("receiptNumber") : null;
                invoiceNumber = printData.has("invoiceNo") ? printData.getString("invoiceNo") : null;
                paymentMode = printData.has("paymentMode") ? printData.getString("paymentMode") : "Cash";
                amount = printData.has("amountPaid") ? printData.getString("amountPaid") : "0";
                isThanksNote = printData.has("isThanksNote") ? printData.getBoolean("isThanksNote") : false;
            } catch (Exception e) {
                System.out.println("Fetch Data " + e.getMessage());
                callbackContext.error("Data Conversion Failed");
            }

            try {
                Resources activityRes = cordova.getActivity().getResources();
                int logoId2 = activityRes.getIdentifier("baharanlogo", "drawable",
                cordova.getActivity().getPackageName());
                Drawable logoDrawable2 = activityRes.getDrawable(logoId2);
                Bitmap bitmap2 = ((BitmapDrawable) logoDrawable2).getBitmap();
                printer.printBitmap(bitmap2);
            } catch (Exception e) {
                System.out.println("Logo Print " + e.getMessage());
            }

            Log.d("TPP", "Enter to Print Receipt Method");
            try {
                printer.spaceSet((byte) 0,(byte) 10);
                printer.fontSet(EFontTypeAscii.FONT_24_48,EFontTypeExtCode.FONT_48_48);
                printer.leftIndent(100);
                printer.printStr(title+"\n", null);
    
                printer.fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_24_24);
                printer.leftIndent(90);
                printer.printStr(copyType+"\n", null);
                printer.leftIndent(0);
                printer.printStr("_______________________________\n", null);
    
                printer.setGray(500);
                printer.fontSet(EFontTypeAscii.FONT_16_32,EFontTypeExtCode.FONT_48_24);
                if (paymentDate != null && !paymentDate.isEmpty()) {
                    printer.printStr("Date: " + paymentDate+"\n", null);
                }
                if (invoiceNumber != null && !invoiceNumber.isEmpty() && invoiceNumber != "null") {
                    printer.printStr("Invoice No: " + invoiceNumber+"\n", null);
                  }
                if (receiptNumber != null && !receiptNumber.isEmpty()) {
                    printer.printStr("Receipt No: " + receiptNumber+"\n", null);
                }
                printer.printStr("Payment Mode: " + paymentMode+"\n", null);
                printer.printStr("Amount Paid: " + amount+"\n", null);

                if (isThanksNote == true) {

                    printer.fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_24_24);
                    printer.printStr("_______________________________\n", null);
                    printer.setGray(75);
                    printer.fontSet(EFontTypeAscii.FONT_24_48,EFontTypeExtCode.FONT_48_48);
                    printer.leftIndent(80);
                    printer.printStr("Thank You\n", null);

                    printer.fontSet(EFontTypeAscii.FONT_8_16,EFontTypeExtCode.FONT_16_16);
                    printer.leftIndent(0);
                    printer.printStr("For any queries contact 07702399999 or\n", null);

                    printer.printStr("visit Malik Mahmood 60th Street\n",null);
                    printer.printStr("Baharan City - Block 13 - Ground Floor\n",null);
                }
                printer.fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_24_24);
                printer.printStr("_______________________________\n", null);
    
                printer.leftIndent(60);
                printer.printStr("-- Powered by EcoPay --\n", null);
                printer.printStr("\n", null);
    
                int printResponse = printer.start();

                if (printResponse == 0) {
                    callbackContext.success("Print Success");
                } else {
                    callbackContext.error(Integer.toString(printResponse));
                }
            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                System.out.println("SystemAPi Initialize " + e.getMessage());
                callbackContext.error(e.getMessage());
                       }

        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }

    }

    private void printSummary(final JSONArray printData, final CallbackContext callbackContext) {
        if (printData != null) {
            try {
                Context context = cordova.getActivity().getApplicationContext();
                Log.d("TPP", "context Done");
                printer = NeptuneLiteUser.getInstance().getDal(context).getPrinter();
                printer.init();
                Log.d("main", "complete init");
            } catch (Exception e) {
                // This will catch any exception, because they are all descended from Exception
                System.out.println("SystemAPi Initialize " + e.getMessage());
                callbackContext.error("Printer not supported");
    
            }

        try{
            printer.fontSet(EFontTypeAscii.FONT_24_48,EFontTypeExtCode.FONT_48_48);
            printer.leftIndent(20);
            printer.printStr("Summary Report\n", null);

           printer.fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_24_24);
           printer.leftIndent(0);
           printer.printStr("_______________________________\n", null);

           printer.printStr("Unit Number              Amount\n",null);
           printer.printStr("_______________________________\n", null);
            Float totalAmount = 0f;
            try {
                if (printData != null && printData.length() > 0) {
                    for (int i = 0; i < printData.length(); i++) {
                        JSONObject data = printData.getJSONObject(i);
                        String accountNumber = data.has("AccountNumber") ? data.getString("AccountNumber") : null;
                        String paidAmount = data.has("PaidAmount") ? data.getString("PaidAmount") : null;
                        totalAmount += data.has("PaidAmount")
                                ? BigDecimal.valueOf(data.getDouble("PaidAmount")).floatValue()
                                : 0f;
                        printer.printStr(accountNumber + "              " + paidAmount+"\n",null);
                    }
                }
            } catch (Exception e) {
                System.out.println("Summary Report Calculation " + e.getMessage());
                callbackContext.error(e.getMessage());
            }
            printer.printStr("_______________________________\n", null);
            printer.printStr("Total                    " + Float.toString(totalAmount)+"\n",null);
            printer.printStr("_______________________________\n", null);

            int printResponse = printer.start();

            if (printResponse == 0) {
                callbackContext.success("Print Success");
            } else {
                callbackContext.error(Integer.toString(printResponse));
            }
        }catch (Exception e) {
    System.out.println("Summary Report Calculation " + e.getMessage());
    callbackContext.error(e.getMessage());
        }
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}