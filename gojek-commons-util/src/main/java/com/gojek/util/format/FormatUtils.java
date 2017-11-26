/**
 *
 */
package com.gojek.util.format;

import com.google.common.base.Strings;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * @author ganeshs
 *
 */
public final class FormatUtils {

    /**
     * Normalizes the phone number to 
     * @param phone
     * @return
     */
    public static String normalizePhone(String phone) {
    	if (Strings.isNullOrEmpty(phone)) {
    		return null;
    	}
        try {
        	PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phone, "IN");
	        return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
	        return null;
        }
    }
}
