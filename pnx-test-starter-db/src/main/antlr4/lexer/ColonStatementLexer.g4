lexer grammar ColonStatementLexer;

fragment QUOTE: '\'';
fragment ESCAPE: '\\';
fragment ESCAPE_QUOTE: ESCAPE QUOTE;
fragment DOUBLE_QUOTE: '"';
fragment COLON: {_input.LA(2) != ':'}? ':';
fragment DOUBLE_COLON: {_input.LA(2) == ':'}? '::';
fragment QUESTION: {_input.LA(2) != '?'}? '?';
fragment DOUBLE_QUESTION: {_input.LA(2) == '?'}? '??';
fragment NAME: JAVA_LETTER | [0-9] | '.' | '?.';

/* Lovingly lifted from https://github.com/antlr/grammars-v4/blob/master/java/JavaLexer.g4 */
fragment JAVA_LETTER : [a-zA-Z$_] | ~[\u0000-\u007F\uD800-\uDBFF] | [\uD800-\uDBFF] [\uDC00-\uDFFF];

COMMENT: '/*' .*? '*/' | '--' ~('\r' | '\n')* | '//' ~('\r' | '\n')*;
QUOTED_TEXT: QUOTE (ESCAPE_QUOTE | ~'\'')* QUOTE;
DOUBLE_QUOTED_TEXT: DOUBLE_QUOTE (~'"')+ DOUBLE_QUOTE;
ESCAPED_TEXT : ESCAPE . ;

NAMED_PARAM: COLON (NAME)+;
POSITIONAL_PARAM: QUESTION;

LITERAL: DOUBLE_COLON | DOUBLE_QUESTION | .;