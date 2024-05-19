package webprogramming.csc1106.Securities;

import java.util.Base64;

public class Encoding {
    
    public static String encode(String input) {
        return Base64.getUrlEncoder().encodeToString(input.getBytes());
    }

    public static String decode(String encoded) {
        return new String(Base64.getUrlDecoder().decode(encoded));
    }
}