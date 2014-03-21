package us.looking_glass.brewdroid;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.analysis.solvers.PolynomialSolver;

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
    private static double sgToBrix(double in) {
        if (in < minSgToBrix)
            return -50;
        else if (in >= maxSgToBrix)
            return 100;
        double[] coeffs = brixToSG.getCoefficients();
        coeffs[0] -= in;
        return solver.solve(100, new PolynomialFunction(coeffs), -50, 100);
    }

    public static interface Unit {
        double from (double value, Unit from);
        String getAbbreviation();
        String getDescription();
    }

    enum Density implements Unit {
        GRAVITY("SG", "specific gravity", 1, 0),
        BRIX("\u00b0Bx", "degrees Brix", 0, 0),
        OESCHLE("\u00b0Oe", "degrees Oeschle", 1000, -1000),
        BAUME("\u00b0B\u00e9", "degrees Baum\u00e9", 0, 0),
        TWADDELL("\u00b0Tw", "degrees Twaddell", 200, -200),
        KMW("KMW", "Klosterneuburger Zuckergrade", 0, 0),
        G_ML("g/mL", "grams per milliliter", 9.982e-1, 0),
        G_L("g/L", "grams per liter", 9.982e2, 0),
        KG_L("kg/L", "kilograms per liter", 9.982e-1, 0),
        KG_M3("kg/m\u00b3", "kilograms per cubic meter", 9.982e2, 0),
        LB_GAL_US("lb/gal (US)", "pounds per gallon (US)", 8.33038272199, 0),
        LB_GAL_IM("lb/gal (Imp)", "pounds per gallon (Imperial)", 10.004376908931999, 0),
        LB_FT3("lb/ft\u00b3", "pounds per cubic foot", 62.31559025095599, 0),
        PA("PA", "potential alcohol", 0, 0),
        ;


        private final String abbreviation;
        private final String description;
        private final double m;
        private final double b;

        Density(String abbreviation, String description, double m, double b) {
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
                        value = sgToBrix(value);
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }


        @Override
        public String toString() {
            return getAbbreviation();
        }
    }

    enum Temperature implements Unit {
        CELSIUS("\u00b0C", "degrees Celsius", 1, -273.15),
        FARENHEIT("\u00b0F", "degrees Farenheit", 1.8, -459.67),
        KELVIN("K", "Kelvin", 1, 0),
        RANKINE("\u00b0R", "Rankine", 1.8, 0),
        ;

        private final String abbreviation;
        private final String description;
        private final double m;
        private final double b;

        Temperature(String abbreviation, String description, double m, double b) {
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getAbbreviation();
        }
    }

    private static final double tartaric = 75.045;
    private static final double malic = 67.045;
    private static final double citric = 64.04;
    private static final double sulfuric = 49.04;

    enum Acidity implements Unit {
        PCT_TARTARIC("% tartaric", "percent tartaric", tartaric),
        PCT_MALIC("% malic", "percent malic", malic),
        PCT_CITRIC("% citric", "percent citric", citric),
        PCT_SULFURIC("% sulfuric", "percent sulfuric", sulfuric),
        GL_TARTARIC("g/L tartaric", "grams per liter (ppt) tartaric", tartaric * 10),
        GL_MALIC("g/L malic", "grams per liter (ppt) malic", malic * 10),
        GL_CITRIC("g/L citric", "grams per liter (ppt) citric", citric * 10),
        GL_SULFURIC("g/L sulfuric", "grams per (ppt) sulfuric", sulfuric * 10),
        MEQ_L("mEq/L", "milli-equivalent per liter", 10000);

        private final String abbreviation;
        private final String description;
        private final double m;

        Acidity(String abbreviation, String description, double m) {
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getAbbreviation();
        }
    }

    enum AlcoholicStrength implements Unit {
        ABW("% ABW", "percent by weight"),
        ABV("% ABV", "percent by volume"),
        PROOF_US("proof (US)", "proof (US)"),
        PROOF_UK("proof (UK)", "proof (UK)")
        ;

        private final String abbreviation;
        private final String description;

        AlcoholicStrength(String abbreviation, String description) {
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getAbbreviation();
        }
    }

    enum Volume implements Unit {
        CC("cm\u00b3", "cubic centimeters", 1),
        ML("mL", "milliliters", 1),
        L("L", "liters", 0.001),
        HL("hL", "hectoliters", 0.00001),
        CM("m\u00b3", "cubic meters", 0.000001),
        TSP("tsp", "teaspoons", 0.202884136),
        TBSP("tbsp", "tablespoons", 0.0676280454),
        OZ_US("oz (US)", "ounces (US)", 0.0338140227),
        C_US("c (US)", "cups (US)", 0.00422675284),
        PT_US("pt (US)", "pints (US)", 0.00211337642),
        QT_US("qt (US)", "quarts (US)", 0.00105668821),
        GAL_US("gal (US)", "gallons (US)", 0.000264172052),
        OZ_I("oz (Imperial)", "ounces (Imperial)", 0.0351950652),
        PT_I("pt (Imperial)", "pints (Imperial)", 0.00175975326),
        QT_I("qt (Imperial)", "quarts (Imperial)", 0.00087987663),
        GAL_I("gal (Imperial)", "gallons (Imperial)", 0.000219969157),
        CI("in\u00b3", "cubic inches", 0.0610237441),
        CF("ft\u00b3", "cubic feet", 3.53146667e-5);

        private final String abbreviation;
        private final String description;
        private final double m;

        Volume(String abbreviation, String description, double m) {
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getAbbreviation();
        }
    }

    enum Weight implements Unit {
        MG("mg", "milligrams", 1),
        G("g", "grams", .001),
        KG("kg", "kilograms", .000001),
        TONNES("t", "tonnes", .000000001),
        GR("gr", "grains", 0.0154323584),
        OZ("oz", "ounces", 3.5274e-5),
        LB("lb", "pounds", 2.20462e-6),
        TONS("T", "tones", 1.10231e-9);

        private final String abbreviation;
        private final String description;
        private final double m;

        Weight(String abbreviation, String description, double m) {
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getAbbreviation();
        }
    }

    enum Concentration implements Unit {
        MGL("mg/L", "milligrams per liter (ppm)", 1),
        GHL("g/hL", "grams per hectoliter", .1),
        GL("g/L", "grams per liter (ppt)", .001),
        GDL("g/dL", "grams per deciliter (percent)", 1e-4),
        GML("g/mL", "grams per milliliter", 1e-6),
        KGL("kg/L", "kilograms per liter", 1e-6),
        GGAL("g/gal (US)", "grams per gallon (US)", 0.00378541178),
        OZGAL("oz/gal (US)", "ounces per gallon (US)", 0.000133526471),
        LBGAL("lb/gal (US)", "pounds per gallon (US)", 8.34540445E-6),
        LBKGAL("lb/kgal (US)", "pounds per 1000 gallons (US)", 8.34540445E-3);

        private final String abbreviation;
        private final String description;
        private final double m;

        Concentration(String abbreviation, String description, double m) {
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
        public String getAbbreviation() {
            return abbreviation;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getAbbreviation();
        }
    }
}
