package com.jamasoftware.services.restclient.jamadomain.values;

import com.jamasoftware.services.restclient.exception.JamaTypeMismatchException;
import com.jamasoftware.services.restclient.jamadomain.JamaRichText;
import com.jamasoftware.services.restclient.exception.RestClientException;

public class RichTextFieldValue extends JamaFieldValue {
    private JamaRichText value = new JamaRichText();

    @Override
    public JamaRichText getValue() {
        return value;
    }

    public void setValue(JamaRichText value) {
        this.value = value;
    }

    public void setValue(Object value) throws JamaTypeMismatchException {
        checkType(String.class, value);
        this.value.setValue((String)value);
    }

    @Override
    public void setValue(String value) throws RestClientException {
        this.value = new JamaRichText();
        this.value.setValue(value);

    }

}
