package us.looking_glass.brewdroid;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;

/**
 * Created by chshrcat on 12/19/13.
 */
public class Alcoholometry {
    private static final double[] A = new double[] {
             9.982012300e2,
            -1.929769495e2,
             3.891238958e2,
            -1.668103923e3,
             1.352215441e4,
            -8.829278388e4,
             3.062874042e5,
            -6.138381234e5,
             7.470172998e5,
            -5.478461354e5,
             2.234460334e5,
            -3.903285426e4
    };
    private static final double[] B = new double[] {
            -2.0618513e-01,
            -5.2682542e-03,
             3.6130013e-05,
            -3.8957702e-07,
             7.1693540e-09,
            -9.9739231e-11
    };

    private static final double[][] C = new double[][] {
            {
                     1.693443461530087e-1,
                    -1.046914743455169e01,
                     7.196353469546523e01,
                    -7.047478054272792e02,
                     3.924090430035045e03,
                    -1.210164659068747e04,
                     2.248646550400788e04,
                    -2.605562982188164e04,
                     1.852373922069467e04,
                    -7.420201433430137e03,
                     1.285617841998974e03
            },
            {
                    -1.193013005057010e-2,
                     2.517399633803461e-1,
                    -2.170575700536993,
                     1.353034988843029e01,
                    -5.029988758547014e01,
                     1.096355666577570e02,
                    -1.422753946421155e02,
                     1.080435942856230e02,
                    -4.414153236817392e01,
                     7.442971530188783
            },
            {
                    -6.802995733503803e-4,
                     1.876837790289664e-2,
                    -2.002561813734156e-1,
                     1.022992966719220,
                    -2.895696483903638,
                     4.810060584300675,
                    -4.672147440794683,
                     2.458043105903461,
                    -5.411227621436812e-1
            },
            {
                     4.075376675622027e-6,
                    -8.763058573471110e-6,
                     6.515031360099368e-6,
                    -1.515784836987210e-6
            },
            {
                    -2.788074354782409e-8,
                     1.345612883493354e-8
            }
    };
    private static final double ethanol20C = density(1, 20);
    private static final PolynomialFunction density20C = densityConstantTemperature(20);
    private static final double volumeWeightCoefficients[] = density20C.multiply(new PolynomialFunction(new double[]{0, 1 / ethanol20C})).getCoefficients();


    public static double density(double strength, double temp) {
        double p[] = new double[11];
        double t[] = new double[6];
        temp -= 20;
        double tmp = strength;
        for (int i = 0; i < p.length; i++) {
            p[i] = tmp;
            tmp *= strength;
        }
        tmp = temp;
        for (int i = 0; i < t.length; i++) {
            t[i] = tmp;
            tmp *= temp;
        }
        double result = A[0];
        for (int i = 1; i < A.length; i++) result += p[i - 1] * A[i];
        for (int i = 0; i < B.length; i++) result += t[i] * B[i];
        for (int i = 0; i < C.length; i++) {
            double[] C_sub = C[i];
            for (int k = 0; k < C_sub.length; k++) result += C_sub[k] * p[k] * t[i];
        }
        return result;
    }

    public static PolynomialFunction densityConstantTemperature(double temp) {
        temp -= 20;
        double t[] = new double[6];
        double coeffs[] = new double[12];
        System.arraycopy(A, 0, coeffs, 0, A.length);
        double tmp = temp;
        for (int i = 0; i < t.length; i++) {
            t[i] = tmp;
            tmp *= temp;
        }
        for (int i = 0; i < B.length; i++) coeffs[0] += t[i] * B[i];
        for (int i = 0; i < C.length; i++) {
            double[] C_sub = C[i];
            for (int k = 0; k < C_sub.length; k++) coeffs[k + 1] += C_sub[k] * t[i];
        }
        return new PolynomialFunction(coeffs);
    }

    public static double ABV(double ABW) {
        return ABV(ABW, density20C.value(ABW));
    }

    public static double ABV(double ABW, double density) {
        return ABW * (density / ethanol20C);
    }

    public static double ABW(double ABV) {
        if (ABV <= 0)
            return 0;
        else if (ABV >= 1)
            return 1;
        double coeffs[] = new double[volumeWeightCoefficients.length];
        coeffs[0] = -ABV;
        System.arraycopy(volumeWeightCoefficients, 1, coeffs, 1, coeffs.length - 1);
        LaguerreSolver solver = new LaguerreSolver(1e-20);
        PolynomialFunction volumeToWeight = new PolynomialFunction(coeffs);
        return solver.solve(100, volumeToWeight, 0, 1);
    }

    public static double ABW(double ABV, double density) {
        return ABV * (ethanol20C / density);
    }
}