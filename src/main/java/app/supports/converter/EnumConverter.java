package app.supports.converter;

import java.beans.PropertyEditorSupport;

/**
 * Convert String to Enum
 * @author ngnmhieu
 */
public class EnumConverter extends PropertyEditorSupport
{
    protected Class enumClass;

    public EnumConverter(Class<? extends Enum> enumClass)
    {
        this.enumClass = enumClass;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        setValue(Enum.valueOf(enumClass, text));
    }
}
