/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.core.util;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvReader{
    private static final int NUMMARK = 10;
    private static final char COMMA = ',';
    private static final char DQUOTE = '"';
    private static final char SQUOTE = '\'';
    private static final char CRETURN = '\r';
    private static final char LineSeparatorChar = '\n';
    private static final char COMMENT = '#';

    /**
     * Should we ignore multiple carriage-return/newline characters
     * at the end of the record?
     */
    private boolean stripMultipleNewlines;

    /**
     * What should be used as the separator character?
     */
    private char separator;
    private ArrayList<String> fields;
    private boolean eofSeen;
    private Reader br;

    static Reader stripBom(InputStream in) throws IOException{
        PushbackInputStream pin = new PushbackInputStream(in, 3);
        byte[] b = new byte[3];
        int len = pin.read(b, 0, b.length);
        if ( (b[0] & 0xFF) == 0xEF && len == 3 ) {
            if ( (b[1] & 0xFF) == 0xBB &&
                    (b[2] & 0xFF) == 0xBF ) {
                return new InputStreamReader(pin, "UTF-8");
            } else {
                pin.unread(b, 0, len);
            }
        }
        else if ( len >= 2 ) {
            if ( (b[0] & 0xFF) == 0xFE &&
                    (b[1] & 0xFF) == 0xFF ) {
                return new InputStreamReader(pin, "UTF-16BE");
            } else if ( (b[0] & 0xFF) == 0xFF &&
                    (b[1] & 0xFF) == 0xFE ) {
                return new InputStreamReader(pin, "UTF-16LE");
            } else {
                pin.unread(b, 0, len);
            }
        } else if ( len > 0 ) {
            pin.unread(b, 0, len);
        }
        return new InputStreamReader(pin, "UTF-8");
    }

    public CsvReader(Reader reader) {
        this(reader, ',', true);
    }

    public CsvReader(Reader reader, char separator, boolean stripMultipleNewlines) {
        this.stripMultipleNewlines = stripMultipleNewlines;
        this.separator = separator;
        this.fields = new ArrayList<String>();
        this.eofSeen = false;
        this.br = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
    }

    public CsvReader(InputStream input, char separator, boolean stripMultipleNewlines) throws IOException {
        this(new BufferedReader(stripBom(input)), separator, stripMultipleNewlines);
    }

    public boolean hasNext() {
        if (eofSeen) return false;
        fields.clear();
        try {
            eofSeen = split(br, fields);
            if (eofSeen) return !fields.isEmpty();
        }catch (IOException e){

        }
        return true;
    }

    public List<String> next() {
        return fields;
    }

    // Returns true if EOF seen.
    private static boolean discardLinefeed(Reader reader, boolean stripMultiple) throws IOException {
        if ( stripMultiple ) {
            reader.mark(NUMMARK);
            int value = reader.read();
            while ( value != -1 ) {
                char c = (char)value;
                if ( c != CRETURN && c != LineSeparatorChar ) {
                    reader.reset();
                    return false;
                } else {
                    reader.mark(NUMMARK);
                    value = reader.read();
                }
            }
            return true;
        } else {
            reader.mark(NUMMARK);
            int value = reader.read();
            if ( value == -1 ) return true;
            else if ( (char)value != LineSeparatorChar ) reader.reset();
            return false;
        }
    }

    private boolean skipComment(Reader in) throws IOException {
        /* Discard line. */
        int value;
        while ( (value = in.read()) != -1 ) {
            char c = (char)value;
            if ( c == CRETURN )
                return discardLinefeed( in, stripMultipleNewlines );
        }
        return true;
    }

    // Returns true when EOF has been seen.
    private boolean split(Reader in, ArrayList<String> fields) throws IOException {
        StringBuilder sbuf = new StringBuilder();
        int value;
        while ( (value = in.read()) != -1 ) {
            char c = (char)value;
            switch(c) {
                case CRETURN:
                    if ( sbuf.length() > 0 ) {
                        fields.add( sbuf.toString() );
                        sbuf.delete( 0, sbuf.length() );
                    }
                    return discardLinefeed( in, stripMultipleNewlines );

                case LineSeparatorChar:
                    if ( sbuf.length() > 0 ) {
                        fields.add( sbuf.toString() );
                        sbuf.delete( 0, sbuf.length() );
                    }

                    if ( stripMultipleNewlines ) {
                        return discardLinefeed(in, stripMultipleNewlines);
                    }

                    return false;

                case DQUOTE:
                {
                    // Processing double-quoted string ..
                    while ( (value = in.read()) != -1 ) {
                        c = (char)value;
                        if ( c == DQUOTE ) {
                            // Saw another double-quote. Check if
                            // another char can be read.
                            in.mark(NUMMARK);
                            if ( (value = in.read()) == -1 ) {
                                // Nope, found EOF; means End of
                                // field, End of record and End of
                                // File
                                if ( sbuf.length() > 0 ) {
                                    fields.add( sbuf.toString() );
                                    sbuf.delete( 0, sbuf.length() );
                                }
                                return true;
                            } else if ( (c = (char)value) == DQUOTE ) {
                                // Found a second double-quote
                                // character. Means the double-quote
                                // is included.
                                sbuf.append( DQUOTE );
                            } else if ( c == CRETURN ) {
                                // Found End of line. Means End of
                                // field, and End of record.
                                if ( sbuf.length() > 0 ) {
                                    fields.add( sbuf.toString() );
                                    sbuf.delete( 0, sbuf.length() );
                                }
                                // Read and discard a line-feed if we
                                // can indeed do so.
                                return discardLinefeed( in,
                                        stripMultipleNewlines );
                            } else if ( c == LineSeparatorChar ) {
                                // Found end of line. Means End of
                                // field, and End of record.
                                if ( sbuf.length() > 0 ) {
                                    fields.add( sbuf.toString() );
                                    sbuf.delete( 0, sbuf.length() );
                                }
                                // No need to check further. At this
                                // point, we have not yet hit EOF, so
                                // we return false.
                                if ( stripMultipleNewlines )
                                    return discardLinefeed( in, stripMultipleNewlines );
                                else return false;
                            } else {
                                // Not one of EOF, double-quote,
                                // newline or line-feed. Means end of
                                // double-quote processing. Does NOT
                                // mean end-of-field or end-of-record.
                                // System.err.println("EOR on '" + c +
                                // "'");
                                in.reset();
                                break;
                            }
                        } else {
                            // Not a double-quote, so no special meaning.
                            sbuf.append( c );
                        }
                    }
                    // Hit EOF, and did not see the terminating double-quote.
                    if ( value == -1 ) {
                        // We ignore this error, and just add whatever
                        // left as the next field.
                        if ( sbuf.length() > 0 ) {
                            fields.add( sbuf.toString() );
                            sbuf.delete( 0, sbuf.length() );
                        }
                        return true;
                    }
                }
                break;

                default:
                    if ( c == separator ) {
                        fields.add( sbuf.toString() );
                        sbuf.delete(0, sbuf.length());
                    } else {
                        /* A comment line is a line starting with '#' with
                         * optional whitespace at the start. */
                        if ( c == COMMENT && fields.isEmpty() && sbuf.toString().trim().isEmpty() ) {
                            boolean eof = skipComment(in);
                            if (eof) return eof;
                            else sbuf.delete(0, sbuf.length());
                            /* Continue with next line if not eof. */
                        } else sbuf.append(c);
                    }
            }//end switch
        }//end while

        if ( sbuf.length() > 0 ) {
            fields.add( sbuf.toString() );
            sbuf.delete( 0, sbuf.length() );
        }
        return true;
    }



}

