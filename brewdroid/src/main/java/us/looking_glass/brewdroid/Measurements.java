package us.looking_glass.brewdroid;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.analysis.solvers.PolynomialSolver;
import org.apache.commons.math3.exception.NoBracketingException;

/**
 * Created by chshrcat on 12/13/13.
 */
public class Measurements {
    private static PolynomialFunction brixToSG = new PolynomialFunction(new double[]{
             1,
             3.875135555e-03,
             9.702881653e-06,
             3.883357480e-07,
            -1.782845295e-08,
             5.591472292e-10,
            -1.100667976e-11,
             1.362230734e-13,
            -1.033082001e-15,
             4.387787019e-18,
            -7.995558730e-21
    });
    private static double minSgToBrix = brixToSG.value(-50);
    private static double maxSgToBrix = brixToSG.value(100);
    private static PolynomialFunction RiToBrix = new PolynomialFunction(new double[]{
            -35551.5928619409,
            91433.0611544285,
            -89106.07864284,
            38966.57232,
            -6427.26
    });
    private static double minRiFromBrix = 1.27255355577497;
    private static double maxRiFromBrix = 1.54763146704752;
    private static PolynomialFunction ZeissToRi = new PolynomialFunction(new double[] {
            1.327338,
            3.9347E-4,
            -2.0446E-7
    });
    private static double minRiFromZeiss = ZeissToRi.value(-5);
    private static double maxRiFromZeiss = ZeissToRi.value(105);

    private static PolynomialSolver solver = new LaguerreSolver();
    private static double convertLinear(double value, double m1, double m2) {
        return value / m1 * m2;
    }
    private static double convertLinearSingle(double value, double m, double b) {
        return value * m + b;
    }
    private static double invertLinearSingle(double value, double m, double b) {
        return (value - b) / m;
    }
    private static double convertLinear(double value, double m1, double b1, double m2, double b2) {
        value = (value - b1) / m1;
        return (value * m2) + b2;
    }
    private static double inversePoly(double in, double in_min, double out_min, double in_max, double out_max, PolynomialFunction poly) {
        if (in <= in_min)
            return out_min;
        if (in >= in_max)
            return out_max;
        double[] coeffs = poly.getCoefficients();
        coeffs[0] -= in;
        try {
            return solver.solve(100, new PolynomialFunction(coeffs), out_min, out_max);
        } catch (NoBracketingException e) {
            return Math.abs(in - in_min) < Math.abs(in - in_max) ? out_min : out_max;
        }
    }

    public static interface Unit {
        double from (double value, Unit from);
        int getAbbreviation();
        int getDescription();
    }

    enum Density implements Unit {
        GRAVITY(R.string.d_sg_a, R.string.d_sg_d, 1, 0),
        BRIX(R.string.d_brix_a, R.string.d_brix_d, 0, 0),
        OESCHLE(R.string.d_oeschle_a, R.string.d_oechle_d, 1000, -1000),
        BAUME(R.string.d_baume_a, R.string.d_baume_d, 0, 0),
        TWADDELL(R.string.d_twaddell_a, R.string.d_twaddell_d, 200, -200),
        KMW(R.string.d_kmw_a, R.string.d_kmw_d, 0, 0),
        G_ML(R.string.d_gml_a, R.string.d_gml_d, 9.982e-1, 0),
        G_L(R.string.d_gl_a, R.string.d_gl_d, 9.982e2, 0),
        KG_L(R.string.d_kgl_a, R.string.d_kgl_d, 9.982e-1, 0),
        KG_M3(R.string.d_kgm3_a, R.string.d_kgm3_d, 9.982e2, 0),
        LB_GAL_US(R.string.d_lbgalus_a, R.string.d_lbgalus_d, 8.33038272199, 0),
        LB_GAL_IM(R.string.d_lbgali_a, R.string.d_lbgali_d, 10.004376908931999, 0),
        LB_FT3(R.string.d_lbft3_a, R.string.d_lbft3_d, 62.31559025095599, 0),
        PA(R.string.d_pa_a, R.string.d_pa_d, 0, 0),
        ;


        private final int abbreviation;
        private final int description;
        private final double m;
        private final double b;

        Density(int abbreviation, int description, double m, double b) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.m = m;
            this.b = b;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Density) {
                Density d = (Density) from;
                switch(d) {
                    case GRAVITY:
                        break;
                    case OESCHLE:
                    case TWADDELL:
                    case G_ML:
                    case G_L:
                    case KG_L:
                    case KG_M3:
                    case LB_GAL_US:
                    case LB_GAL_IM:
                    case LB_FT3:
                        value = invertLinearSingle(value, d.m, d.b);
                        break;
                    case BAUME:
                        value = value > 0 ?
                                145 / (145 - value) :
                                0;
                        break;
                    case KMW:
                        value = value > 0 ?
                                2.2e-5 * value * value + 4.54e-3 * value + 1 :
                                1.0;
                        break;
                    case BRIX:
                        value = brixToSG.value(value);
                        break;
                    case PA:
                        value = (9221 * value + 8e5) / (3e3 * value + 8e5);
                        break;
                }
                switch(this) {
                    case GRAVITY:
                        break;
                    case OESCHLE:
                    case TWADDELL:
                    case G_ML:
                    case G_L:
                    case KG_L:
                    case KG_M3:
                    case LB_GAL_US:
                    case LB_GAL_IM:
                    case LB_FT3:
                        value = convertLinearSingle(value, m, b);
                        break;
                    case BAUME:
                        value = value > 1  ?
                                145 - 145 / value :
                                0;
                        break;
                    case KMW:
                        value = value > 1 ?
                                (Math.sqrt(2.2e5 * value - 168471) - 227) * (5.0 / 11) :
                                0;
                        break;
                    case BRIX:
                        value = inversePoly(value, minSgToBrix, maxSgToBrix, -50, 100, brixToSG);
                        break;
                    case PA:
                        value = 1000 * (value - 1) / (7.75 - 3.75 * (value - 1.007));
                        break;
                }
                return value;
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    enum Temperature implements Unit {
        CELSIUS(R.string.t_c_a, R.string.t_c_d, 1, -273.15),
        FARENHEIT(R.string.t_f_a, R.string.t_f_d, 1.8, -459.67),
        KELVIN(R.string.t_k_a, R.string.t_k_d, 1, 0),
        RANKINE(R.string.t_r_a, R.string.t_r_d, 1.8, 0),
        ;

        private final int abbreviation;
        private final int description;
        private final double m;
        private final double b;

        Temperature(int abbreviation, int description, double m, double b) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.m = m;
            this.b = b;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Temperature) {
                Temperature t = (Temperature) from;
                return convertLinear(value, t.m, t.b, m, b);
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    private static final double tartaric = 75.045;
    private static final double malic = 67.045;
    private static final double citric = 64.04;
    private static final double sulfuric = 49.04;

    enum Acidity implements Unit {
        PCT_TARTARIC(R.string.a_pt_a, R.string.a_pt_d, tartaric),
        PCT_MALIC(R.string.a_pm_a, R.string.a_pm_d, malic),
        PCT_CITRIC(R.string.a_pc_a, R.string.a_pc_d, citric),
        PCT_SULFURIC(R.string.a_ps_a, R.string.a_ps_d, sulfuric),
        GL_TARTARIC(R.string.a_glt_a, R.string.a_glt_d, tartaric * 10),
        GL_MALIC(R.string.a_glm_a, R.string.a_glm_d, malic * 10),
        GL_CITRIC(R.string.a_glc_a, R.string.a_glc_d, citric * 10),
        GL_SULFURIC(R.string.a_gls_a, R.string.a_gls_d, sulfuric * 10),
        MEQ_L(R.string.a_meql_a, R.string.a_meql_d, 10000);

        private final int abbreviation;
        private final int description;
        private final double m;

        Acidity(int abbreviation, int description, double m) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.m = m;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Acidity) {
                Acidity a = (Acidity) from;
                return convertLinear(value, a.m, m);
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    enum AlcoholicStrength implements Unit {
        ABW(R.string.as_abw_a, R.string.as_abw_d),
        ABV(R.string.as_abv_a, R.string.as_abv_d),
        PROOF_US(R.string.as_pus_a, R.string.a_pus_d),
        PROOF_UK(R.string.a_puk_a, R.string.a_puk_d)
        ;

        private final int abbreviation;
        private final int description;

        AlcoholicStrength(int abbreviation, int description) {
            this.abbreviation = abbreviation;
            this.description = description;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (!(from instanceof AlcoholicStrength))
                throw new IllegalArgumentException("can't convert between different dimensions");
            AlcoholicStrength s = (AlcoholicStrength) from;
            switch (s) {
                case ABW:
                    value = 100 * Alcoholometry.ABV(value / 100);
                    break;
                case PROOF_US:
                    value /= 2;
                    break;
                case PROOF_UK:
                    value *= 0.5715;
                    break;
            }
            switch (this) {
                case ABW:
                    value = 100 * Alcoholometry.ABW(value / 100);
                    break;
                case PROOF_US:
                    value *= 2;
                    break;
                case PROOF_UK:
                    value /= 0.5715;
                    break;
            }
            return value;
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    enum Volume implements Unit {
        CC(R.string.v_cm3_a, R.string.v_cm3_d, 1),
        ML(R.string.v_ml_a, R.string.v_ml_d, 1),
        L(R.string.v_l_a, R.string.v_l_d, 0.001),
        HL(R.string.v_hl_a, R.string.v_hl_d, 0.00001),
        CM(R.string.v_m3_a, R.string.v_m3_d, 0.000001),
        TSP(R.string.v_tsp_a, R.string.v_tsp_d, 0.202884136),
        TBSP(R.string.v_tbsp_a, R.string.v_tbsp_d, 0.0676280454),
        OZ_US(R.string.v_ozus_a, R.string.v_ozus_d, 0.0338140227),
        C_US(R.string.v_cus_a, R.string.v_cus_d, 0.00422675284),
        PT_US(R.string.v_ptus_a, R.string.v_ptus_d, 0.00211337642),
        QT_US(R.string.v_qtus_a, R.string.v_qtus_d, 0.00105668821),
        GAL_US(R.string.v_galus_a, R.string.v_galus_d, 0.000264172052),
        OZ_I(R.string.v_ozi_a, R.string.v_ozi_d, 0.0351950652),
        PT_I(R.string.v_pti_a, R.string.v_pti_d, 0.00175975326),
        QT_I(R.string.v_qti_a, R.string.v_qt_d, 0.00087987663),
        GAL_I(R.string.v_gali_a, R.string.v_gali_d, 0.000219969157),
        CI(R.string.v_in3_a, R.string.v_in3_d, 0.0610237441),
        CF(R.string.v_ft3_a, R.string.v_ft3_d, 3.53146667e-5);

        private final int abbreviation;
        private final int description;
        private final double m;

        Volume(int abbreviation, int description, double m) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.m = m;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Volume) {
                Volume v = (Volume) from;
                return convertLinear(value, v.m, this.m);
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    enum Weight implements Unit {
        MG(R.string.m_mg_a, R.string.m_mg_d, 1),
        G(R.string.m_g_a, R.string.m_g_d, .001),
        KG(R.string.m_kg_a, R.string.m_kg_d, .000001),
        TONNES(R.string.m_tonnes_a, R.string.m_tonnes_d, .000000001),
        GR(R.string.m_gr_a, R.string.m_gr_d, 0.0154323584),
        OZ(R.string.m_oz_a, R.string.m_oz_d, 3.5274e-5),
        LB(R.string.m_lb_a, R.string.m_lb_d, 2.20462e-6),
        TONS(R.string.m_tons_a, R.string.m_tons_d, 1.10231e-9);

        private final int abbreviation;
        private final int description;
        private final double m;

        Weight(int abbreviation, int description, double m) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.m = m;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Weight) {
                Weight w = (Weight) from;
                return convertLinear(value, w.m, this.m);
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    enum Concentration implements Unit {
        MGL(R.string.c_mgl_a, R.string.c_mgl_d, 1),
        GHL(R.string.c_ghl_a, R.string.c_ghl_d, .1),
        GL(R.string.c_gl_a, R.string.c_gl_d, .001),
        GDL(R.string.c_gdl_a, R.string.c_gdl_d, 1e-4),
        GML(R.string.c_gml_a, R.string.c_gml_d, 1e-6),
        KGL(R.string.c_kgl_a, R.string.c_kgl_d, 1e-6),
        GGAL(R.string.c_ggal_a, R.string.c_ggal_d, 0.00378541178),
        OZGAL(R.string.c_ozgal_a, R.string.c_ozgal_d, 0.000133526471),
        LBGAL(R.string.c_lbgal_a, R.string.c_lbgal_d, 8.34540445E-6),
        LBKGAL(R.string.c_lbkgal_a, R.string.c_lbkgal_d, 8.34540445E-3);

        private final int abbreviation;
        private final int description;
        private final double m;

        Concentration(int abbreviation, int description, double m) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.m = m;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Concentration) {
                Concentration c = (Concentration) from;
                return convertLinear(value, c.m, this.m);
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }

    enum Refractivity implements Unit {
        RI(R.string.r_ri_a, R.string.r_ri_d),
        BRIX(R.string.r_brix_a, R.string.r_brix_d),
        ZEISS(R.string.r_zeiss_a, R.string.r_zeiss_d)
        ;

        private final int abbreviation;
        private final int description;

        Refractivity(int abbreviation, int description) {
            this.abbreviation = abbreviation;
            this.description = description;
        }

        @Override
        public double from(double value, Unit from) {
            if (from == this)
                return value;
            if (from instanceof Refractivity) {
                Refractivity r = (Refractivity) from;
                switch(r) {
                    case BRIX:
                        value = inversePoly(value, -50, minRiFromBrix, 100, maxRiFromBrix, RiToBrix);
                        break;
                    case ZEISS:
                        value = ZeissToRi.value(value);
                        break;
                    case RI:
                        break;
                }
                switch(this) {
                    case BRIX:
                        value = RiToBrix.value(value);
                        break;
                    case ZEISS:
                        value = inversePoly(value, minRiFromZeiss, -5, maxRiFromZeiss, 105, ZeissToRi);
                        break;
                    case RI:
                        break;
                }
                return value;
            }
            throw new IllegalArgumentException("can't convert between different dimensions");
        }

        @Override
        public int getAbbreviation() {
            return abbreviation;
        }

        @Override
        public int getDescription() {
            return description;
        }
    }
}
