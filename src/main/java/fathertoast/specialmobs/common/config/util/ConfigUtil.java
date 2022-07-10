package fathertoast.specialmobs.common.config.util;

public abstract class ConfigUtil {
    
    /** The plus or minus symbol (+/-). */
    public static final String PLUS_OR_MINUS = "\u00b1";
    /** The less than or equal to symbol (<=). */
    public static final String LESS_OR_EQUAL = "\u2264";
    /** The greater than or equal to symbol (>=). */
    public static final String GREATER_OR_EQUAL = "\u2265";
    
    /** @return The string with all spaces replaced by underscores. Useful for file names. */
    public static String noSpaces( String str ) { return str.replace( ' ', '_' ); }
    
    /** @return The string converted from camel case to lower space case; e.g., "UpperCamelCase" returns "upper camel case". */
    public static String camelCaseToLowerSpace( String str ) {
        final StringBuilder spacedStr = new StringBuilder();
        for( int i = 0; i < str.length(); i++ ) {
            final char c = str.charAt( i );
            if( Character.isUpperCase( c ) ) {
                if( i > 0 ) spacedStr.append( ' ' );
                spacedStr.append( Character.toLowerCase( c ) );
            }
            else {
                spacedStr.append( c );
            }
        }
        return spacedStr.toString();
    }
    
    /** @return The string converted from camel case to lower underscore case; e.g., "UpperCamelCase" returns "upper_camel_case". */
    public static String camelCaseToLowerUnderscore( String str ) { return noSpaces( camelCaseToLowerSpace( str ) ); }
    
    /** @return The string, but with the first character changed to upper case. */
    public static String properCase( String str ) { return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 ); }
}