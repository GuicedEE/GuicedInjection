package com.guicedee.guicedinjection.json;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class StaticStrings
{
	public static final String HTML_HEADER_JSON = "application/json";
	public static final String HTML_HEADER_JAVASCRIPT = "application/javascript";
	public static final String HTML_HEADER_CSS = "text/css";
	public static final String HTML_HEADER_ZIP = "application/zip";
	public static final String HTML_HEADER_PDF = "application/pdf";
	public static final String HTML_HEADER_JPG = "img/jpg";
	public static final String HTML_HEADER_MOV = "video/mov";
	public static final String HTML_HEADER_DEFAULT_CONTENT_TYPE = "text/html";

	public static final String UTF8 = "UTF-8";

	public static final String STRING_EMPTY = "";
	public static final String STRING_SPACE = " ";
	public static final String STRING_FORWARD_SLASH = "/";
	public static final String STRING_BACK_SLASH = "\\";
	public static final String STRING_TAB = "\t";
	public static final String STRING_DOLLAR = "$";
	public static final String STRING_DOLLAR_ESCAPED = "\\$";
	public static final String STRING_EQUALS = "=";
	public static final String STRING_NEWLINE_TEXT = "\n";
	public static final String STRING_DOUBLE_COLON = ":";
	public static final String STRING_ASTERISK = "*";
	public static final String STRING_QUESTIONMARK = "?";
	public static final String STRING_SPACE_DOUBLE_COLON_SPACE = " : ";
	public static final String STRING_SINGLE_QUOTES = "'";
	public static final String STRING_DOUBLE_QUOTES = "\"";
	public static final String STRING_SINGLE_QUOTES_SPACE = "' ";
	public static final String STRING_DOUBLE_QUOTES_SPACE = "\" ";
	public static final String STRING_EQUALS_SINGLE_QUOTES = "='";
	public static final String STRING_EQUALS_DOUBLE_QUOTES = "=\"";
	public static final String STRING_SHARP_BRACE_OPEN = "<";
	public static final String STRING_SHARP_BRACE_SLASH_OPEN = "</";
	public static final String STRING_SQUARE_BRACE_OPEN = "[";
	public static final String STRING_SQUARE_BRACE_CLOSED = "]";
	public static final String STRING_SHARP_BRACE_CLOSED = ">";
	public static final String STRING_HASH = "#";
	public static final String STRING_DASH = "-";
	public static final String STRING_SPACE_DASH_SPACE = " - ";
	public static final String STRING_COMMNA = ",";
	public static final String STRING_COMMNA_SPACE = ", ";
	public static final String STRING_COMMNA_SEMICOLON = ",'";
	public static final String STRING_DOT = ".";
	public static final String STRING_EQUALS_SPACE_EQUALS = " = ";
	public static final String STRING_SEMICOLON = ";";
	public static final String STRING_BRACES_OPEN = "{";
	public static final String STRING_BRACES_CLOSE = "}";
	public static final String STRING_CLOSING_BRACKET_SEMICOLON = ");";
	public static final String STRING_SPACE_OPEN_BRACKET = " (";
	public static final String STRING_AMPERSAND = "&";


	public static final String FAKE_KEY = "fake";

	public static final String STRING_INSERT_INTO_SQL = "INSERT INTO ";
	public static final String STRING_DELETE_FROM_SQL = "DELETE FROM ";
	public static final String STRING_UPDATE_SQL = "UPDATE ";
	public static final String STRING_UPDATE_SET_SQL = " SET ";
	public static final String STRING_WHERE_SQL = "WHERE ";
	public static final String STRING_VALUES_SQL_INSERT = ") VALUES (";
	public static final String STRING_HEX_SQL_START = "0x";

	public static final String STRING_NULL = "NULL";
	public static final String STRING_DOT_ESCAPED = "\\.";
	public static final String STRING_0 = "0";
	public static final String STRING_1 = "1";

	public static final String STRING_AUTHORIZATION = "Authorization: ";
	public static final String STRING_AUTHORIZATION_HEADER = "Authorization";
	public static final String STRING_AUTHORIZATION_BASIC = "Basic";
	public static final String STRING_AUTHORIZATION_BASIC_SPACE = "Basic ";
	public static final String STRING_AUTHORIZATION_BEARER = "Bearer";
	public static final String STRING_AUTHORIZATION_BEARER_SPACE = "Bearer ";

	public static final String STRING_DURATION_TIME = "PT";


	public static final String STRING_HMAC_256 = "HMAC256";
	public static final String STRING_HMAC_512 = "HMAC512";

	/**
	 * A default regex to identify query parameters
	 */
	public static final String QUERY_PARAMETERS_REGEX = "(\\?.*)?";
	public static final String NOT_WEB_SOCKETS = "(?!wssocket)";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER_NAME = "Access-Control-Allow-Origin";
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER_NAME = "Access-Control-Allow-Credentials";
	public static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER_NAME = "Access-Control-Allow-Methods";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER_NAME = "Access-Control-Allow-Headers";
	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_XML_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String STRING_SELECTED = "selected";
	/**
	 * The &times; string
	 */
	public static final String HTML_TIMES = "&times;";
	/**
	 * The &nbsp; string
	 */
	public static final String HTML_TAB = "&nbsp;";
	public static final String HTML_AMPERSAND = "&amp;";
	/**
	 * Default text for Latin
	 */
	public static final String ShortLatin = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eleifend...";
	/**
	 * Default Medium text for Latin
	 */
	public static final String MediumLatin = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Libero laboriosam dolor perspiciatis omnis\n " +
	                                         "exercitationem. Beatae, officia pariatur? Est cum veniam excepturi. Maiores praesentium, porro voluptas\n" +
	                                         " dicta, \n" +
	                                         "debitis...\n";
	public static final char CHAR_SPACE = ' ';
	public static final char CHAR_DOT = '.';
	public static final char CHAR_EQUALS = '=';
	public static final char CHAR_COMMA = ',';
	public static final char CHAR_SEMI_COLON = ';';
	public static final char CHAR_DOUBLE_COLON = ':';
	public static final char CHAR_UNDERSCORE = '_';
	public static final char CHAR_PERCENT = '%';
	public static final char CHAR_DOLLAR = '$';
	public static final char CHAR_DASH = '-';
	public static final char CHAR_QUESTIONMARK = '?';
	public static final char CHAR_SLASH = '/';
	public static final char CHAR_BACKSLASH = '\\';
	public static final char CHAR_HASH = '#';
	public static final char CHAR_PLUS = '+';
	public static final char CHAR_BRACES_OPEN = '{';
	public static final char CHAR_BRACES_CLOSE = '}';
	public static final char CHAR_AMPERSAND = '&';

	public static final String P = "P";
	public static final String H = "H";
	public static final String M = "M";
	public static final String S = "S";
	public static final String E = "E";

	public static Charset UTF_CHARSET = StandardCharsets.UTF_8;

	private StaticStrings()
	{
		//No config required
	}

	/**
	 * Sets the default charset used across the baord
	 *
	 * @param charset
	 * 		The charset to use, default StandardCharset.UTF_8
	 */
	public static void setDefaultChartset(Charset charset)
	{
		UTF_CHARSET = charset;
	}
}
