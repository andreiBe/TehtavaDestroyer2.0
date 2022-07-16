lexer grammar AMTokens;

/* main tokens */

U	   : 'sqrt'|'abs'|'floor'|'ceil'|'norm'|'vec'|'hat'|'bar'|'ul'|'dot'|'ddot'|'cancel'|'ubrace'|'obrace';
B	   : 'root'|'overset'|'underset'|'frac';
L	   : '('|'['|'{:'|'{'|'(:'|'<<';
R	   : ')'|']'|':}'|'}'|':)'|'>>';

WS	   : [ \t] -> skip;
NEWLINE : ('\n'|'\r')+ -> skip;

/* constant tokens */

C : NUMBER | LETTER | SYMBOL;


NUMBER         : INT | DECIMAL;
DECIMAL        : DIGIT+ ',' DIGIT* | '.' DIGIT+;
INT            : DIGIT+;
DIGIT          : [0-9];

LETTER         : LATIN | GREEK;
LATIN          : [a-zA-Z];
GREEK          : 'alpha'|'beta'|'gamma'|'Gamma'|'delta'|'Delta'|'epsilon'|'varepsilon'|'zeta'|'eta'|'theta'|'Theta'|'vartheta'|'iota'|'kappa'|'lambda'|'Lambda'|'mu'|'nu'|'xi'|'Xi'|'pi'|'Pi'|'rho'|'sigma'|'Sigma'|'tau'|'upsilon'|'phi'|'Phi'|'varphi'|'chi'|'psi'|'Psi'|'omega'|'Omega';

SYMBOL         : SIMPLE_SYM | RELATION_SYM | ARROW | LOGICAL | OP_SYM | DOTS | LARGE_OP | NUM_SET | SINGLE_DLM | SPACE | STANDARD_FUNC | DIFF;
SIMPLE_SYM     : '='|'+'|'-';
RELATION_SYM   : '!='|'<'|'<='|'>'|'>='|'-<'|'-<='|'>-'|'<-='|'in'|'!in'|'sub'|'sup'|'sube'|'supe'|'-='|'~='|'~~'|'prop';
ARROW          : 'uarr'|'darr'|'rarr'|'->'|'>->'|'->>'|'>->>'|'|->'|'larr'|'harr'|'rArr'|'lArr'|'hArr'|'=>'|'<=>';
LOGICAL        : 'and'|'or'|'not'|'if'|'AA'|'EE'|'_|_'|'TT'|'|--'|'|==';
OP_SYM         : '*'|'**'|'***'|'\\'|'xx'|'-:'|'|><'|'><|'|'@'|'o+'|'ox'|'o.'|'^^'|'^^^'|'vv'|'vvv'|'nn'|'uu'|'oo'|'O/'|'+-'|'del'|'grad'|'aleph'|'/_'|'frown'|'/_\\'|'diamond'|'square';
DOTS           : ':.'|':\''|'...'|'cdots'|'vdots'|'ddots';
LARGE_OP       : 'sum'|'prod'|'int'|'oint'|'nnn'|'uuu';
NUM_SET        : 'CC'|'RR'|'QQ'|'ZZ'|'NN';
SINGLE_DLM     : '|__'|'|~~'|'__|'|'~|';
SPACE          : 'quad';
STANDARD_FUNC  : 'sin'|'cos'|'tan'|'sec'|'csc'|'cot'|'arcsin'|'arccos'|'arctan'|'sinh'|'cosh'|'tanh'|'sech'|'csch'|'coth'|'exp'|'log'|'ln'|'det'|'dim'|'mod'|'gcd'|'lcm'|'lim'|'lub'|'glb'|'min'|'max';
DIFF           : 'dx' | 'dy' | 'dz' | 'dt';
