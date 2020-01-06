package maop_ch10;

import cartago.*;
import org.apache.commons.math3.transform.*;
import org.apache.commons.math3.complex.*;

public class FFTCalculator extends Artifact {
    private FastFourierTransformer transf;

    void init(){
        transf = new FastFourierTransformer(DftNormalization.STANDARD);
    }
    
    @OPERATION 
    void fftTransform(double[] f, OpFeedbackParam<Complex[]> result) {
        Complex[] res = transf.transform(f,TransformType.FORWARD);
        result.set(res);
    }  

    // aux function to manage data structures
    
    @OPERATION 
    void getReal(Complex[] data, OpFeedbackParam<Double[]> dataRe){ }

    @OPERATION 
    void getComplex(Complex[] data, OpFeedbackParam<Double[]> dataCo){ }
}
