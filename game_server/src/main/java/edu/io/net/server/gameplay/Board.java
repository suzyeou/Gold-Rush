package edu.io.net.server.gameplay;

import edu.io.net.command.UpdateState;
import edu.io.net.command.UpdateStateFactory;
import edu.io.net.command.GameState;

public class Board implements UpdateStateFactory.StateSource {
    private int size = 32;

    public Board() {
        UpdateStateFactory.register(this);
    }

    @Override
    public void populateStatePack(UpdateState.Cmd cmd) {
        switch (cmd.pack()) {
            case GameState.Pack.AFTER_JOIN_GAME -> {
                    cmd.add(new GameState.BoardInfo(size));
                    generateDemo(cmd);
            }
        }
    }

    private void generateDemo(UpdateState.Cmd cmd) {
       var t = ("::⋅;:⋅<:⋅=:⋅>:⋅?:⋅@:⋅A:⋅B:⋅C:⋅D:⋅E:⋅F:⋅G:⋅H:⋅I:⋅"+
               "J:⋅K:⋅L:⋅M:⋅N:⋅O:⋅P:⋅Q:⋅R:⋅S:⋅T:⋅U:⋅V:⋅W:⋅X:⋅Y:⋅"+
               ":;⋅;;⋅<;⋅=;⋅>;⋅?;⋅@;⋅A;⋅B;⋅C;⋅D;⋅E;⋅F;⋅G;⋅H;⋅I;⋅"+
               "J;⋅K;⋅L;⋅M;⋅N;⋅O;⋅P;⋅Q;⋅R;⋅S;⋅T;⋅U;⋅V;⋅W;⋅X;⋅Y;⋅"+
               ":<⋅;<⋅<<⋅=<⋅><⋅?<⋅@<⋅A<⋅B<⋅C<⋅D<⋅E<⋅F<⋅G<⋅H<⋅I<⋅"+
               "J<⋅K<⋅L<⋅M<⋅N<⋅O<⋅P<⋅Q<⋅R<⋅S<⋅T<⋅U<⋅V<⋅W<⋅X<⋅Y<⋅"+
               ":=⋅;=⋅<=⋅==⋅>=⋅?=⋅@=⋅A=⋅B=⋅C=⋅D=⋅E=⋅F=⋅G=⋅H=⋅I=⋅"+
               "J=⋅K=⋅L=⋅M=⋅N=⋅O=⋅P=⋅Q=⋅R=⋅S=⋅T=⋅U=⋅V=⋅W=⋅X=⋅Y=⋅"+
               ":>⋅;>⋅<>⋅=>⋅>>⋅?>⋅@>⋅A>⋅B>⋅C>⋅D>⋅E>⋅F>⋅G>⋅H>⋅I>⋅"+
               "J>⋅K>⋅L>⋅M>⋅N>⋅O>⋅P>⋅Q>⋅R>⋅S>⋅T>⋅U>⋅V>⋅W>⋅X>⋅Y>⋅"+
               ":?⋅;?⋅<?⋅=?⋅>?⋅??⋅@?⋅A?⋅B?⋅C?⋅D?⋅E?⋅F?⋅G?⋅H?⋅I?⋅"+
               "J?⋅K?⋅L?⋅M?⋅N?⋅O?⋅P?⋅Q?⋅R?⋅S?⋅T?⋅U?⋅V?⋅W?⋅X?⋅Y?⋅"+
               ":@⋅;@⋅<@⋅=@⋅>@⋅?@⋅@@⋅A@⋅B@⋅C@⋅D@⋅E@⋅F@⋅G@⋅H@⋅I@⋅"+
               "J@⋅K@⋅L@⋅M@⋅N@⋅O@⋅P@⋅Q@⋅R@⋅S@⋅T@⋅U@⋅V@⋅W@⋅X@⋅Y@⋅"+
               ":A⋅;A⋅<A⋅=A⋅>A⋅?A⋅@A⋅AA⋅BA⋅CA⋅DA⋅EA⋅FA⋅GA⋅HA⋅IA⋅"+
               "JA⋅KA⋅LA⣠MA⣴NA⣶OA⣤PA⡀QA⋅RA⋅SA⋅TA⋅UA⋅VA⋅WA⋅XA⋅YA⋅"+
               ":B⋅;B⋅<B⋅=B⋅>B⋅?B⋅@B⋅AB⋅BB⋅CB⋅DB⋅EB⋅FB⋅GB⋅HB⋅IB⋅"+
               "JB⋅KB⣼LB⡟MB⋅NB⋅OB⠘PB⣿QB⋅RB⋅SB⋅TB⋅UB⋅VB⋅WB⋅XB⋅YB⋅"+
               ":C⋅;C⋅<C⋅=C⋅>C⋅?C⋅@C⋅AC⋅BC⋅CC⋅DC⋅EC⋅FC⋅GC⋅HC⋅IC⋅"+
               "JC⣰KC⡟LC⋅MC⋅NC⋅OC⋅PC⣿QC⋅RC⋅SC⋅TC⋅UC⋅VC⋅WC⋅XC⋅YC⋅"+
               ":D⋅;D⋅<D⋅=D⋅>D⋅?D⋅@D⋅AD⋅BD⋅CD⋅DD⋅ED⋅FD⋅GD⋅HD⋅ID⣰"+
               "JD⡿KD⠁LD⋅MD⋅ND⋅OD⢀PD⣿QD⋅RD⋅SD⋅TD⋅UD⋅VD⋅WD⋅XD⋅YD⋅"+
               ":E⋅;E⋅<E⋅=E⋅>E⋅?E⋅@E⋅AE⋅BE⋅CE⋅DE⋅EE⋅FE⋅GE⣠HE⣾IE⠏"+
               "JE⋅KE⋅LE⋅ME⋅NE⋅OE⣸PE⡟QE⋅RE⋅SE⋅TE⋅UE⋅VE⋅WE⋅XE⋅YE⋅"+
               ":F⋅;F⋅<F⣴=F⡾>F⠛?F⠛@F⠛AF⠛BF⢻CF⣆DF⣤EF⡶FF⠟GF⠋HF⋅IF⋅"+
               "JF⋅KF⋅LF⋅MF⋅NF⣰OF⡟PF⋅QF⋅RF⋅SF⋅TF⋅UF⋅VF⋅WF⋅XF⋅YF⋅"+
               ":G⋅;G⢸<G⣿=G⋅>G⋅?G⋅@G⋅AG⋅BG⢸CG⡏DG⠁EG⋅FG⋅GG⋅HG⋅IG⋅"+
               "JG⋅KG⋅LG⋅MG⠸NG⠿OG⠿PG⠿QG⠿RG⠿SG⠿TG⠿UG⠿VG⢷WG⣦XG⋅YG⋅"+
               ":H⋅;H⢸<H⣿=H⋅>H⋅?H⋅@H⋅AH⋅BH⢸CH⡇DH⋅EH⋅FH⋅GH⋅HH⋅IH⋅"+
               "JH⋅KH⋅LH⋅MH⋅NH⋅OH⋅PH⋅QH⋅RH⋅SH⋅TH⋅UH⋅VH⋅WH⣿XH⠇YH⋅"+
               ":I⋅;I⢸<I⣿=I⋅>I⋅?I⋅@I⋅AI⋅BI⢸CI⡇DI⋅EI⋅FI⋅GI⋅HI⋅II⋅"+
               "JI⋅KI⋅LI⋅MI⋅NI⋅OI⋅PI⋅QI⋅RI⠿SI⠿TI⠿UI⠿VI⣿WI⡋XI⋅YI⋅"+
               ":J⋅;J⢸<J⣿=J⋅>J⋅?J⋅@J⋅AJ⋅BJ⢸CJ⡇DJ⋅EJ⋅FJ⋅GJ⋅HJ⋅IJ⋅"+
               "JJ⋅KJ⋅LJ⋅MJ⋅NJ⋅OJ⋅PJ⋅QJ⋅RJ⋅SJ⋅TJ⋅UJ⋅VJ⣹WJ⡇XJ⋅YJ⋅"+
               ":K⋅;K⢸<K⣿=K⋅>K⋅?K⋅@K⋅AK⋅BK⢸CK⡇DK⋅EK⋅FK⋅GK⋅HK⋅IK⋅"+
               "JK⋅KK⋅LK⋅MK⋅NK⋅OK⋅PK⠐QK⠿RK⠿SK⠿TK⠿UK⣿VK⠋WK⋅XK⋅YK⋅"+
               ":L⋅;L⢸<L⣿=L⋅>L⋅?L⋅@L⋅AL⋅BL⢸CL⡇DL⋅EL⋅FL⋅GL⋅HL⋅IL⋅"+
               "JL⋅KL⋅LL⋅ML⋅NL⋅OL⋅PL⋅QL⋅RL⋅SL⋅TL⋅UL⣽VL⡇WL⋅XL⋅YL⋅"+
               ":M⋅;M⢸<M⣿=M⋅>M⋅?M⋅@M⋅AM⋅BM⢸CM⡇DM⋅EM⋅FM⋅GM⋅HM⋅IM⋅"+
               "JM⋅KM⋅LM⋅MM⋅NM⋅OM⠐PM⠿QM⠿RM⠿SM⢿TM⣿UM⠋VM⋅WM⋅XM⋅YM⋅"+
               ":N⋅;N⠈<N⢿=N⣄>N⣀?N⣀@N⣀AN⣀BN⣸CN⡷DN⣦EN⣤FN⣄GN⣀HN⡀IN⋅"+
               "JN⋅KN⋅LN⋅MN⋅NN⋅ON⋅PN⋅QN⋅RN⋅SN⋅TN⣿UN⠇VN⋅WN⋅XN⋅YN⋅"+
               ":O⋅;O⋅<O⋅=O⠉>O⠛?O⠛@O⠛AO⠛BO⠛CO⠁DO⋅EO⋅FO⠉GO⠉HO⠛IO⠛"+
               "JO⠻KO⠿LO⠿MO⠿NO⠿OO⠿PO⠿QO⠿RO⠿SO⠟TO⠋UO⋅VO⋅WO⋅XO⋅YO⋅"+
               ":P⋅;P⋅<P⋅=P⋅>P⋅?P⋅@P⋅AP⋅BP⋅CP⋅DP⋅EP⋅FP⋅GP⋅HP⋅IP⋅"+
               "JP⋅KP⋅LP⋅MP⋅NP⋅OP⋅PP⋅QP⋅RP⋅SP⋅TP⋅UP⋅VP⋅WP⋅XP⋅YP⋅"+
               ":Q⋅;Q⋅<Q⋅=Q⋅>Q⋅?Q⋅@Q⋅AQ⋅BQ⋅CQ⋅DQ⋅EQ⋅FQ⋅GQ⋅HQ⋅IQ⋅"+
               "JQ⋅KQ⋅LQ⋅MQ⋅NQ⋅OQ⋅PQ⋅QQ⋅RQ⋅SQ⋅TQ⋅UQ⋅VQ⋅WQ⋅XQ⋅YQ⋅"+
               ":R⋅;R⋅<R⋅=R⋅>R⋅?R⋅@R⋅AR⋅BR⋅CR⋅DR⋅ER⋅FR⋅GR⋅HR⋅IR⋅"+
               "JR⋅KR⋅LR⋅MR⋅NR⋅OR⋅PR⋅QR⋅RR⋅SR⋅TR⋅UR⋅VR⋅WR⋅XR⋅YR⋅"+
               ":S⋅;S⋅<S⋅=S⋅>S⋅?S⋅@S⋅AS⋅BS⋅CS⋅DS⋅ES⋅FS⋅GS⋅HS⋅IS⋅"+
               "JS⋅KS⋅LS⋅MS⋅NS⋅OS⋅PS⋅QS⋅RS⋅SS⋅TS⋅US⋅VS⋅WS⋅XS⋅YS⋅"+
               ":T⋅;T⋅<T⋅=T⋅>T⋅?T⋅@T⋅AT⋅BT⋅CT⋅DT⋅ET⋅FT⋅GT⋅HT⋅IT⋅"+
               "JT⋅KT⋅LT⋅MT⋅NT⋅OT⋅PT⋅QT⋅RT⋅ST⋅TT⋅UT⋅VT⋅WT⋅XT⋅YT⋅"+
               ":U⋅;U⋅<U⋅=U⋅>U⋅?U⋅@U⋅AU⋅BU⋅CU⋅DU⋅EU⋅FU⋅GU⋅HU⋅IU⋅"+
               "JU⋅KU⋅LU⋅MU⋅NU⋅OU⋅PU⋅QU⋅RU⋅SU⋅TU⋅UU⋅VU⋅WU⋅XU⋅YU⋅"+
               ":V⋅;V⋅<V⋅=V⋅>V⋅?V⋅@V⋅AV⋅BV⋅CV⋅DV⋅EV⋅FV⋅GV⋅HV⋅IV⋅"+
               "JV⋅KV⋅LV⋅MV⋅NV⋅OV⋅PV⋅QV⋅RV⋅SV⋅TV⋅UV⋅VV⋅WV⋅XV⋅YV⋅"+
               ":W⋅;W⋅<W⋅=W⋅>W⋅?W⋅@W⋅AW⋅BW⋅CW⋅DW⋅EW⋅FW⋅GW⋅HW⋅IW⋅"+
               "JW⋅KW⋅LW⋅MW⋅NW⋅OW⋅PW⋅QW⋅RW⋅SW⋅TW⋅UW⋅VW⋅WW⋅XW⋅YW⋅"+
               ":X⋅;X⋅<X⋅=X⋅>X⋅?X⋅@X⋅AX⋅BX⋅CX⋅DX⋅EX⋅FX⋅GX⋅HX⋅IX⋅"+
               "JX⋅KX⋅LX⋅MX⋅NX⋅OX⋅PX⋅QX⋅RX⋅SX⋅TX⋅UX⋅VX⋅WX⋅XX⋅YX⋅"+
               ":Y⋅;Y⋅<Y⋅=Y⋅>Y⋅?Y⋅@Y⋅AY⋅BY⋅CY⋅DY⋅EY⋅FY⋅GY⋅HY⋅IY⋅"+
               "JY⋅KY⋅LY⋅MY⋅NY⋅OY⋅PY⋅QY⋅RY⋅SY⋅TY⋅UY⋅VY⋅WY⋅XY⋅YY⋅").toCharArray();
       var i = 0;
       while (i < t.length) {
           var c = t[i++] - 58; // magic ;)
           var r = t[i++] - 58;
           var label = t[i++] + "";
           cmd.add(new GameState.BoardSquareInfo(new GameState.Position(c, r), label));
       }
    }
}
