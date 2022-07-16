grammar AM;
import AMTokens;

/* labeled tokens */

OVER    : '/' ;
SUB     : '_' ;
SUPER   : '^' ;

/* grammar rules */

e	: i e?              # append
	;

i   : s                 # simple
    | s SUB s           # sub
	| s SUPER s         # super
	| s SUB s SUPER s   # subSup
	| i OVER i          # frac
	;

s	: C                 # constParse
	| L e R             # parens
	| U s               # unary
	| B s s             # binary
	;
