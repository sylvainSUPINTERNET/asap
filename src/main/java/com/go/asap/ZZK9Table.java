package com.go.asap;


import java.util.HashSet;
import java.util.Set;

public class ZZK9Table {


    public static Set<String> expectedPossiblesValues(String values) {
        Set<String> result = new HashSet<>();
        String[] lines = values.split("\n");  // Split by new line to process each line

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                String[] parts = line.split("\t");  // Split by tab to separate the key and values
                String key = parts[0].trim();
                String[] codes = parts[1].trim().split(" ");  // Split values by space

                for (String code : codes) {
                    result.add(key + "_" + code);
                }
            }
        }
        return result;
    }
    public final static String EXPECTED_POSSIBLES_VALUES = "B0A\t01 02 \n" +
            "B0D\t1A 1C 5C AF CL K0 K1 SL \n" +
            "B0F\tEF EU EY K9 KE L5 MK MN NN NP P4 P6 Q5 ZI \n" +
            "B0G\t01 05 0F 0K 0W \n" +
            "DAB\t00 13 \n" +
            "DAN\t00 01 05 \n" +
            "DAQ\t00 03 04 06 09 10 11 \n" +
            "DAS\t01 16 \n" +
            "DAX\t02 05 \n" +
            "DCX\t01 02 \n" +
            "DCZ\t01 13 26 34 \n" +
            "DDK\t00 13 \n" +
            "DDZ\tG6 G7 G8 G9 JN QR SB SC SD SE SH XJ ZX \n" +
            "DER\t33 37 42 43 44 45 \n" +
            "DFH\t01 05 \n" +
            "DFX\t00 01 \n" +
            "DHY\t01 02 \n" +
            "DI4\t00 01 \n" +
            "DK6\t00 02 \n" +
            "DL9\t00 01 \n" +
            "DLA\t02 05 10 11 \n" +
            "DLV\t00 02 03 04 \n" +
            "DLZ\t00 02 04 \n" +
            "DNF\t00 11 12 15 \n" +
            "DPC\t00 17 18 19 \n" +
            "DQF\t00 01 \n" +
            "DRE\t01 07 \n" +
            "DRG\t31 37 \n" +
            "DRS\t00 01 09 \n" +
            "DUB\t00 21 25 \n" +
            "DUF\t01 02 \n" +
            "DVT\t07 11 44 49 \n" +
            "DYD\t00 01 \n" +
            "B0B\t0C 0G 0I 0M 0P \n" +
            "B0E\t02 03 04 05 07 08 0A 0E 0F 0N 0P 0Q 0S 0T 0V 0W \n" +
            "B0H\tB0 C0 DB E0 EE F0 G0 GC GD GL J0 JR T0 U0 UB UM V0 Y0 \n" +
            "B0J\t17 26 46 66 A0 DO E1 EN GD UJ \n" +
            "B0M\tE0 M0 P0 \n" +
            "B0N\t1N 1W 4P 4Q 5V 9V DC F4 JH KC M6 MG PR X9 ZR \n" +
            "B0P\t46 CM CP CQ CR CS CZ DV F7 OF \n" +
            "B0R\tFL FN FT FX FY \n" +
            "D25\t00 01 \n" +
            "D48\t00 01 \n" +
            "D5N\t04 07 \n" +
            "D6F\t01 02 \n" +
            "DAL\t00 04 05 46 \n" +
            "DAO\t00 01 \n" +
            "DBJ\t00 02 03 04 06 \n" +
            "DBO\t00 01 02 \n" +
            "DBQ\t00 01 \n" +
            "DBV\t02 04 \n" +
            "DCA\t00 06 \n" +
            "DCB\t00 08 09 \n" +
            "DCO\t00 01 02 \n" +
            "DCP\t01 02 \n" +
            "DCU\t15 26 \n" +
            "DD3\t00 61 62 63 72 BG BI BX CH CR CS DA DB DH DR DS DT DU DV DW DX DY DZ EA EB EC NT RC SZ \n" +
            "DD4\t00 24 27 \n" +
            "DDY\t00 36 \n" +
            "DE2\t00 01 \n" +
            "DE3\t00 01 \n" +
            "DE7\t00 03 04 \n" +
            "DE8\t01 02 \n" +
            "DED\t16 24 27 44 61 62 78 \n" +
            "DEJ\t02 06 11 \n" +
            "DEN\tER VO WL WM \n" +
            "DES\t00 03 08 11 \n" +
            "DFE\t00 10 \n" +
            "DFZ\t00 01 \n" +
            "DGA\t01 03 \n" +
            "DGB\t00 23 33 38 43 44 \n" +
            "DGM\t62 AZ \n" +
            "DGQ\t00 09 \n" +
            "DHU\t02 03 \n" +
            "DI3\t00 01 \n" +
            "DI8\t00 01 \n" +
            "DI9\t00 01 \n" +
            "DIQ\t00 01 \n" +
            "DIT\t01 02 \n" +
            "DIW\t00 01 02 \n" +
            "DJA\t12 18 \n" +
            "DJY\t00 11 \n" +
            "DKA\t17 23 26 31 37 38 50 \n" +
            "DKG\t00 37 38 40 \n" +
            "DKK\t00 01 \n" +
            "DLE\t00 05 \n" +
            "DLI\t00 15 \n" +
            "DLK\t06 08 10 11 13 \n" +
            "DLT\t02 03 \n" +
            "DLU\t02 03 \n" +
            "DLW\t01 02 \n" +
            "DME\t00 08 \n" +
            "DMG\t00 15 \n" +
            "DMP\t00 CB FA GT \n" +
            "DN2\t00 02 \n" +
            "DNA\t00 01 \n" +
            "DNB\t04 08 \n" +
            "DNC\t00 05 \n" +
            "DNM\t00 01 02 \n" +
            "DNN\t00 01 \n" +
            "DOF\t00 43 46 49 50 \n" +
            "DON\t01 02 \n" +
            "DOR\t01 03 \n" +
            "DOU\t00 01 \n" +
            "DOV\t00 01 02 \n" +
            "DPB\t02 04 07 \n" +
            "DPD\t22 23 29 88 \n" +
            "DPQ\t02 13 \n" +
            "DPR\t00 01 \n" +
            "DPX\t00 13 27 35 42 43 \n" +
            "DQK\t00 04 13 \n" +
            "DRB\t36 37 39 40 \n" +
            "DRC\t00 71 \n" +
            "DRH\t00 12 \n" +
            "DRL\t00 02 05 \n" +
            "DRU\t00 20 \n" +
            "DRZ\t48 89 AB AC AE CG CY \n" +
            "DSD\t00 46 79 \n" +
            "DUD\t00 03 \n" +
            "DUE\t00 05 \n" +
            "DUH\t00 10 56 \n" +
            "DUX\t00 37 41 AB \n" +
            "DVB\t04 08 \n" +
            "DVC\t00 02 \n" +
            "DVD\t02 10 \n" +
            "DVG\t00 02 11 18 19 \n" +
            "DVH\t04 07 23 49 \n" +
            "DVI\t01 03 \n" +
            "DXY\t00 01 \n" +
            "DYI\t03 45 \n" +
            "DYM\t00 22 25 \n" +
            "DYQ\t01 02 \n" +
            "DYR\t00 22 \n" +
            "DZJ\tA0 A1 A2 A3 A4 A5 \n" +
            "DZV\tKY KZ L0 \n" +
            "D2A\t00 BA BG BH BM BN CO CR CU CY DO DZ EG FI GF GP GR GT HR ID IE IS JP LT LY MD MQ MU MY NC NZ PF PH PM RE RO RS SI SK SY TN TR TW UA UY VN XT YT ZA";
    public final static String TABLES = "FAM_ZZK9_B0B_DUF\n" +
            "FAM_ZZK9_B0G_DRG\n" +
            "FAM_ZZK9_B0M_B0N\n" +
            "FAM_ZZK9_B0N_B0E\n" +
            "FAM_ZZK9_B0N_B0P\n" +
            "FAM_ZZK9_B0P_B0E\n" +
            "FAM_ZZK9_BDEFJ\n" +
            "FAM_ZZK9_D0N\n" +
            "FAM_ZZK9_D25_B0D\n" +
            "FAM_ZZK9_D48\n" +
            "FAM_ZZK9_DAB\n" +
            "FAM_ZZK9_DAL\n" +
            "FAM_ZZK9_DAN\n" +
            "FAM_ZZK9_DAN_DPC\n" +
            "FAM_ZZK9_DAN_DZH\n" +
            "FAM_ZZK9_DAO\n" +
            "FAM_ZZK9_DAO_DUX\n" +
            "FAM_ZZK9_DAQ\n" +
            "FAM_ZZK9_DAS\n" +
            "FAM_ZZK9_DAX\n" +
            "FAM_ZZK9_DBJ\n" +
            "FAM_ZZK9_DBO\n" +
            "FAM_ZZK9_DBQ\n" +
            "FAM_ZZK9_DBV\n" +
            "FAM_ZZK9_DCA\n" +
            "FAM_ZZK9_DCB\n" +
            "FAM_ZZK9_DCO\n" +
            "FAM_ZZK9_DCP\n" +
            "FAM_ZZK9_DCR\n" +
            "FAM_ZZK9_DCX\n" +
            "FAM_ZZK9_DCZ\n" +
            "FAM_ZZK9_DD3\n" +
            "FAM_ZZK9_DD4\n" +
            "FAM_ZZK9_DD4_DLI\n" +
            "FAM_ZZK9_DDA\n" +
            "FAM_ZZK9_DDK\n" +
            "FAM_ZZK9_DDY\n" +
            "FAM_ZZK9_DDZ\n" +
            "FAM_ZZK9_DDZ_B0F\n" +
            "FAM_ZZK9_DDZ_DGA\n" +
            "FAM_ZZK9_DE3\n" +
            "FAM_ZZK9_DE8\n" +
            "FAM_ZZK9_DE8_DGM\n" +
            "FAM_ZZK9_DED_DDZ\n" +
            "FAM_ZZK9_DEJ\n" +
            "FAM_ZZK9_DEN_DER\n" +
            "FAM_ZZK9_DER_DCU\n" +
            "FAM_ZZK9_DES\n" +
            "FAM_ZZK9_DFC\n" +
            "FAM_ZZK9_DFE\n" +
            "FAM_ZZK9_DFGHJ_DKA\n" +
            "FAM_ZZK9_DFH\n" +
            "FAM_ZZK9_DFX\n" +
            "FAM_ZZK9_DGB\n" +
            "FAM_ZZK9_DGM\n" +
            "FAM_ZZK9_DGM_DYB\n" +
            "FAM_ZZK9_DGQ\n" +
            "FAM_ZZK9_DHU\n" +
            "FAM_ZZK9_DHU_DGM\n" +
            "FAM_ZZK9_DHY\n" +
            "FAM_ZZK9_DI3\n" +
            "FAM_ZZK9_DI3_DRG\n" +
            "FAM_ZZK9_DI8_9_DZJ\n" +
            "FAM_ZZK9_DI8_DI9\n" +
            "FAM_ZZK9_DIQ\n" +
            "FAM_ZZK9_DIT\n" +
            "FAM_ZZK9_DJO\n" +
            "FAM_ZZK9_DJU\n" +
            "FAM_ZZK9_DJY\n" +
            "FAM_ZZK9_DK6\n" +
            "FAM_ZZK9_DK6_B0H\n" +
            "FAM_ZZK9_DK6_DUE\n" +
            "FAM_ZZK9_DKG\n" +
            "FAM_ZZK9_DLE\n" +
            "FAM_ZZK9_DLI\n" +
            "FAM_ZZK9_DLI_UB_GM\n" +
            "FAM_ZZK9_DLK\n" +
            "FAM_ZZK9_DLV_DAN\n" +
            "FAM_ZZK9_DLW\n" +
            "FAM_ZZK9_DLW_DI3\n" +
            "FAM_ZZK9_DLW_DNB\n" +
            "FAM_ZZK9_DMG\n" +
            "FAM_ZZK9_DMP\n" +
            "FAM_ZZK9_DN2\n" +
            "FAM_ZZK9_DNA\n" +
            "FAM_ZZK9_DNB_DHG\n" +
            "FAM_ZZK9_DNB_DPR\n" +
            "FAM_ZZK9_DNF\n" +
            "FAM_ZZK9_DNL\n" +
            "FAM_ZZK9_DNM\n" +
            "FAM_ZZK9_DO2\n" +
            "FAM_ZZK9_DOR\n" +
            "FAM_ZZK9_DOU\n" +
            "FAM_ZZK9_DOV\n" +
            "FAM_ZZK9_DPB_PC_VG\n" +
            "FAM_ZZK9_DPD_DED\n" +
            "FAM_ZZK9_DPQ\n" +
            "FAM_ZZK9_DPR\n" +
            "FAM_ZZK9_DPX\n" +
            "FAM_ZZK9_DQF\n" +
            "FAM_ZZK9_DQK\n" +
            "FAM_ZZK9_DRB\n" +
            "FAM_ZZK9_DRB_DZJ\n" +
            "FAM_ZZK9_DRB_RE_CU\n" +
            "FAM_ZZK9_DRE\n" +
            "FAM_ZZK9_DRG\n" +
            "FAM_ZZK9_DRG_DGM\n" +
            "FAM_ZZK9_DRL\n" +
            "FAM_ZZK9_DRS\n" +
            "FAM_ZZK9_DRU\n" +
            "FAM_ZZK9_DSD\n" +
            "FAM_ZZK9_DUB\n" +
            "FAM_ZZK9_DUD\n" +
            "FAM_ZZK9_DUE\n" +
            "FAM_ZZK9_DUF\n" +
            "FAM_ZZK9_DUH\n" +
            "FAM_ZZK9_DVC\n" +
            "FAM_ZZK9_DVD_DLE\n" +
            "FAM_ZZK9_DVH\n" +
            "FAM_ZZK9_DVI\n" +
            "FAM_ZZK9_DVT\n" +
            "FAM_ZZK9_DWL_B0E\n" +
            "FAM_ZZK9_DWL_B0H\n" +
            "FAM_ZZK9_DXN\n" +
            "FAM_ZZK9_DXY\n" +
            "FAM_ZZK9_DYD\n" +
            "FAM_ZZK9_DYD_DHG\n" +
            "FAM_ZZK9_DYQ\n" +
            "FAM_ZZK9_DYR\n" +
            "FAM_ZZK9_DYR_DJO\n" +
            "FAM_ZZK9_DZH_DUF\n" +
            "FAM_ZZK9_DZJ_B0E\n" +
            "FAM_ZZK9_FH_AQ_BQ\n" +
            "FAM_ZZK9_P_AN_AL\n" +
            "FAM_ZZK9_QK_PB\n" +
            "FAM_ZZK9__DLZ\n" +
            "FAM_ZZK9__DOF\n" +
            "FAM_K9V7_PACK_DZJ\n" +
            "FAM_K9V7_PACK_DZV\n" +
            "FAM_ZZK9V7_B0H_D2A";

}
