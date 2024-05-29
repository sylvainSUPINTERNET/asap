package com.go.asap;

import java.util.HashSet;
import java.util.Set;

public class P22Table {


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

    public final static String EXPECTED_POSSIBLES_VALUES = "B0C\tP2 \n" +
            "B0D\tA5 AD \n" +
            "B0F\tES JG JH JU L6 L7 L8 LY M5 M6 MK MN MT MU MZ NP P6 PR Q5 ZI \n" +
            "B0G\t01 05 06 0F 0J 0K 0M 0W \n" +
            "D6F\t02 ZZ \n" +
            "D7S\t00 02 \n" +
            "DAO\t00 01 \n" +
            "DAQ\t00 05 \n" +
            "DCX\t01 02 \n" +
            "DD4\t00 03 05 24 27 \n" +
            "DDZ\tE3 E5 JM JN PW TZ VK \n" +
            "DE3\t00 01 \n" +
            "DER\t00 P3 \n" +
            "DFH\t01 05 \n" +
            "DFX\t00 01 \n" +
            "DHG\t00 06 \n" +
            "DLV\t00 02 03 \n" +
            "DLZ\t00 02 04 \n" +
            "DN9\t00 03 07 12 \n" +
            "DNA\t00 01 \n" +
            "DRE\t01 07 \n" +
            "DRG\t03 15 20 31 35 37 \n" +
            "DRS\t00 09 12 14 19 \n" +
            "DSP\t00 11 \n" +
            "DUB\t00 01 03 21 23 \n" +
            "DUH\t00 75 76 \n" +
            "DUN\t00 ZZ \n" +
            "DVQ\t04 30 54 58 62 65 67 71 \n" +
            "DYD\t00 02 07 \n" +
            "DYE\t01 02 03 \n" +
            "B0E\t0A 0B 0F 0H 0J 0K 0L 0M 0P 0Q 0R 0T 0U \n" +
            "B0H\tB0 C0 DB E0 EE F0 FY G0 GC GE GL J0 JG JR JX M0 T0 U0 UB UR V0 \n" +
            "B0J\t01 15 2P A0 AD D1 U1 \n" +
            "B0M\tM0 M5 M6 P0 \n" +
            "B0N\t2T 9V EQ F4 LD SM VH VL WP \n" +
            "B0P\tA4 A5 A6 A7 AP AQ AT D7 V1 WN \n" +
            "D25\t00 01 \n" +
            "D2A\t00 AR BA BG BH CL CY DO DZ EG FI GF GP GR HR IE IS JO LB LT MD MQ NC PF RE RO RS SI SK TN TR UA YT ZA \n" +
            "D5N\t00 04 05 06 11 \n" +
            "D6E\t00 01 \n" +
            "DAA\t04 25 \n" +
            "DAB\t00 13 \n" +
            "DAL\t00 43 \n" +
            "DBB\t03 11 12 \n" +
            "DCB\t04 09 \n" +
            "DCF\t00 08 14 \n" +
            "DCG\t18 40 \n" +
            "DDA\t15 16 \n" +
            "DE2\t00 01 \n" +
            "DE7\t00 01 02 04 \n" +
            "DE8\t00 02 \n" +
            "DEE\t00 55 \n" +
            "DEK\t08 10 \n" +
            "DEN\tTV TX TY UB VD \n" +
            "DFE\t00 10 \n" +
            "DGG\t00 06 \n" +
            "DGM\tAQ AW AX AZ BJ BP \n" +
            "DGQ\t13 14 15 \n" +
            "DGV\t00 09 \n" +
            "DHU\t02 03 13 \n" +
            "DI2\t00 02 03 \n" +
            "DI4\t00 01 \n" +
            "DI8\t00 01 \n" +
            "DI9\t00 01 \n" +
            "DIM\t10 13 14 \n" +
            "DIN\t00 03 \n" +
            "DIP\t01 02 \n" +
            "DIT\t00 12 \n" +
            "DJA\t11 18 \n" +
            "DJB\t00 04 \n" +
            "DJD\t02 20 \n" +
            "DJY\t00 02 11 \n" +
            "DK9\t00 01 \n" +
            "DKA\t26 31 34 37 38 \n" +
            "DLA\t02 05 10 \n" +
            "DLE\t01 05 \n" +
            "DLI\t00 03 04 15 \n" +
            "DLX\t23 25 26 30 44 51 \n" +
            "DMG\t07 14 \n" +
            "DMI\t00 \n" +
            "DMT\t00 A1 MB NQ NR NS NY PL \n" +
            "DMZ\t00 01 02 \n" +
            "DN8\t00 03 \n" +
            "DNB\t03 08 \n" +
            "DNK\t00 05 \n" +
            "DNM\t00 04 \n" +
            "DO1\t00 03 \n" +
            "DOF\t00 35 \n" +
            "DOK\t00 01 \n" +
            "DOR\t01 03 \n" +
            "DPD\tEG EH EI EJ EK EL FR ZA ZB ZC \n" +
            "DQK\t00 02 13 14 \n" +
            "DRC\t71 AL EN ES NN RC \n" +
            "DRH\t12 20 \n" +
            "DRL\t02 03 05 06 \n" +
            "DRU\t00 20 \n" +
            "DRZ\t48 89 \n" +
            "DSB\t00 01 \n" +
            "DSD\t00 55 62 76 \n" +
            "DVB\t09 10 \n" +
            "DVD\t02 09 \n" +
            "DVF\t37 39 40 \n" +
            "DVH\t23 37 49 \n" +
            "DWV\t60 61 62 63 70 71 \n" +
            "DWY\t21 22 23 24 38 75 76 AJ AK AL AN \n" +
            "DYM\t00 22 23 25 \n" +
            "DYQ\t01 02 \n" +
            "DYR\t00 04 07 22 \n" +
            "DZH\tG6 HR HT HU JA JB JC K0 K1 K2 K3 K4 K5 KD KE KH KT OB Z3 Z5 \n" +
            "DZJ\t01 02 06 07 10 16 17 26 28 A0 A1 A2 A3 A4 A5 A6 A7 A8 A9 B0 B1 EJ \n" +
            "DZV\tBX BZ CA CB CC H3 K4 K5 K6 K7 KU \n" +
            "REG\tAM AP AQ AR";

    public final static String TABLES =
            //"PBV1_B0C_B0F_B0G\n" +
           // "PBV_test\n" +
            "PBV_TEST_B0C_B0F_B0G\n"+
            "FAM_1PP2_AFFEC_B0P\n" +
            "FAM_1PP2_B0H_D2A\n" +
            "FAM_1PP2_B0M_B0N\n" +
            "FAM_1PP2_B0P_B0R\n" +
            "FAM_1PP2_D25\n" +
            "FAM_1PP2_D6E_REG\n" +
            "FAM_1PP2_DAA\n" +
            "FAM_1PP2_DAB\n" +
            "FAM_1PP2_DAL\n" +
            "FAM_1PP2_DAO\n" +
            "FAM_1PP2_DAQ\n" +
            "FAM_1PP2_DBB\n" +
            "FAM_1PP2_DBJ\n" +
            "FAM_1PP2_DCB\n" +
            "FAM_1PP2_DCF\n" +
            "FAM_1PP2_DCF_DCG\n" +
            "FAM_1PP2_DCG\n" +
            "FAM_1PP2_DCX\n" +
            "FAM_1PP2_DDA\n" +
            "FAM_1PP2_DE3\n" +
            "FAM_1PP2_DE3_DZJ\n" +
            "FAM_1PP2_DEE\n" +
            "FAM_1PP2_DFH\n" +
            "FAM_1PP2_DFH_B0F\n" +
            "FAM_1PP2_DFH_DNK\n" +
            "FAM_1PP2_DFX\n" +
            "FAM_1PP2_DGG\n" +
            "FAM_1PP2_DGM_DZJ\n" +
            "FAM_1PP2_DGQ\n" +
            "FAM_1PP2_DGQ_B0P\n" +
            "FAM_1PP2_DGV\n" +
            "FAM_1PP2_DHG\n" +
            "FAM_1PP2_DI4_REG\n" +
            "FAM_1PP2_DI8_REG\n" +
            "FAM_1PP2_DI9_REG\n" +
            "FAM_1PP2_DIM\n" +
            "FAM_1PP2_DIN\n" +
            "FAM_1PP2_DIP_DRH\n" +
            "FAM_1PP2_DJA\n" +
            "FAM_1PP2_DJB\n" +
            "FAM_1PP2_DJB_B0G\n" +
            "FAM_1PP2_DJB_DZJ\n" +
            "FAM_1PP2_DJD\n" +
            "FAM_1PP2_DJD_B0N\n" +
            "FAM_1PP2_DK9\n" +
            "FAM_1PP2_DLA\n" +
            "FAM_1PP2_DLE\n" +
            "FAM_1PP2_DLT\n" +
            "FAM_1PP2_DLU_DLT\n" +
            "FAM_1PP2_DLZ_\n" +
            "FAM_1PP2_DMG\n" +
            "FAM_1PP2_DMT\n" +
            "FAM_1PP2_DMZ\n" +
            "FAM_1PP2_DN8_DJB\n" +
            "FAM_1PP2_DNA\n" +
            "FAM_1PP2_DNA_B0P\n" +
            "FAM_1PP2_DNA_DRH\n" +
            "FAM_1PP2_DNF\n" +
            "FAM_1PP2_DNG\n" +
            "FAM_1PP2_DNK\n" +
            "FAM_1PP2_DNM\n" +
            "FAM_1PP2_DO1\n" +
            "FAM_1PP2_DO2\n" +
            "FAM_1PP2_DOK\n" +
            "FAM_1PP2_DOR\n" +
            "FAM_1PP2_DPD\n" +
            "FAM_1PP2_DPX_B0D\n" +
            "FAM_1PP2_DRE\n" +
            "FAM_1PP2_DRE_DWV\n" +
            "FAM_1PP2_DRE_DZJ\n" +
            "FAM_1PP2_DRJ\n" +
            "FAM_1PP2_DRJ_DRK\n" +
            "FAM_1PP2_DRJ_LT_YI\n" +
            "FAM_1PP2_DRL\n" +
            "FAM_1PP2_DRL_DLE\n" +
            "FAM_1PP2_DRU\n" +
            "FAM_1PP2_DRZ\n" +
            "FAM_1PP2_DSB\n" +
            "FAM_1PP2_DSD\n" +
            "FAM_1PP2_DSP\n" +
            "FAM_1PP2_DUH\n" +
            "FAM_1PP2_DUN_D6F\n" +
            "FAM_1PP2_DVB\n" +
            "FAM_1PP2_DVD\n" +
            "FAM_1PP2_DVD_B0F\n" +
            "FAM_1PP2_DVF_DLW\n" +
            "FAM_1PP2_DVH\n" +
            "FAM_1PP2_DVQ\n" +
            "FAM_1PP2_DWV_AFFEC\n" +
            "FAM_1PP2_DWV_B0F\n" +
            "FAM_1PP2_DWV_DZV\n" +
            "FAM_1PP2_DWY_AFFEC\n" +
            "FAM_1PP2_DWY_DZJ\n" +
            "FAM_1PP2_DYD\n" +
            "FAM_1PP2_DYD_DFH\n" +
            "FAM_1PP2_DYD_DGM\n" +
            "FAM_1PP2_DYE_\n" +
            "FAM_1PP2_DYI\n" +
            "FAM_1PP2_DYR\n" +
            "FAM_1PP2_DYR_DIM\n" +
            "FAM_1PP2_DZH\n" +
            "FAM_1PP2_DZH_AFFEC\n" +
            "FAM_1PP2_DZJ_AFFEC\n" +
            "FAM_1PP2_DZJ_DI2\n" +
            "FAM_1PP2_DZV_AFFEC\n" +
            "FAM_1PP2_DZV_DFH\n" +
            "FAM_1PP2_D_E_F_G_H\n" +
            "FAM_1PP2_PACK_DWV\n" +
            "PK_1PP2_DWY_02\n" +
            "PK_1PP2_DZJ_01\n" +
            "PK_1PP2_DZV_01";


}
