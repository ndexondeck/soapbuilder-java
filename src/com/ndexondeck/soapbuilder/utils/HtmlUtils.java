package com.ndexondeck.soapbuilder.utils;

/**
 * Copied from org.springframework.web.util and simplified by Nduka Ohadoma on 5/1/2019.
 * For html escaping
 */
public class HtmlUtils {

    private static final HtmlCharacterEntityReferences characterEntityReferences = new HtmlCharacterEntityReferences();

    public static String htmlEscape(String input) {
        return htmlEscape(input, "ISO-8859-1");
    }

    public static String htmlEscape(String input, String encoding) {

        StringBuilder escaped = new StringBuilder(input.length() * 2);

        for(int i = 0; i < input.length(); ++i) {
            char character = input.charAt(i);
            String reference = characterEntityReferences.convertToReference(character, encoding);
            if(reference != null) {
                escaped.append(reference);
            } else {
                escaped.append(character);
            }
        }

        return escaped.toString();
    }

}
