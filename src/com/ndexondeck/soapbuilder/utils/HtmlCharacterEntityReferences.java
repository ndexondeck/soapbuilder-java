

package com.ndexondeck.soapbuilder.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Copied from org.springframework.web.util and simplified by Nduka Ohadoma on 5/1/2019.
 * HtmlU
 */
class HtmlCharacterEntityReferences {

    private final String[] characterToEntityReferenceMap = new String[3000];
    private final Map<String, Character> entityReferenceToCharacterMap = new HashMap<>(252);

    HtmlCharacterEntityReferences() {
        Properties entityReferences = new Properties();
        InputStream is = HtmlCharacterEntityReferences.class.getResourceAsStream("HtmlCharacterEntityReferences.properties");
        if(is == null) {
            throw new IllegalStateException("Cannot find reference definition file [HtmlCharacterEntityReferences.properties] as class path resource");
        } else {
            try {
                try {
                    entityReferences.load(is);
                } finally {
                    is.close();
                }
            } catch (IOException var11) {
                throw new IllegalStateException("Failed to parse reference definition file [HtmlCharacterEntityReferences.properties]: " + var11.getMessage());
            }

            Enumeration keys = entityReferences.propertyNames();

            while(keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                int referredChar = Integer.parseInt(key);
                if(!(referredChar < 1000 || referredChar >= 8000 && referredChar < 10000)) {
                    throw new RuntimeException("Invalid reference to special HTML entity: " + referredChar);
                }
                int index = referredChar < 1000?referredChar:referredChar - 7000;
                String reference = entityReferences.getProperty(key);
                this.characterToEntityReferenceMap[index] = '&' + reference + ';';
                this.entityReferenceToCharacterMap.put(reference, (char) referredChar);
            }

        }
    }

    public String convertToReference(char character) {
        return this.convertToReference(character, "ISO-8859-1");
    }

    public String convertToReference(char character, String encoding) {
        if(encoding.startsWith("UTF-")) {
            switch(character) {
            case '"':
                return "&quot;";
            case '&':
                return "&amp;";
            case '\'':
                return "&#39;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            }
        } else if(character < 1000 || character >= 8000 && character < 10000) {
            int index = character < 1000?character:character - 7000;
            String entityReference = this.characterToEntityReferenceMap[index];
            if(entityReference != null) {
                return entityReference;
            }
        }

        return null;
    }

    public char convertToCharacter(String entityReference) {
        Character referredCharacter = (Character)this.entityReferenceToCharacterMap.get(entityReference);
        return referredCharacter != null?referredCharacter.charValue():'\uffff';
    }
}
